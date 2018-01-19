<?php
// +----------------------------------------------------------------------
// | 海豚PHP框架 [ DolphinPHP ]
// +----------------------------------------------------------------------
// | 版权所有 2016~2017 河源市卓锐科技有限公司 [ http://www.zrthink.com ]
// +----------------------------------------------------------------------
// | 官方网站: http://dolphinphp.com
// +----------------------------------------------------------------------
// | 开源协议 ( http://www.apache.org/licenses/LICENSE-2.0 )
// +----------------------------------------------------------------------

namespace app\user\model;

use think\helper\Str;
use think\Model;
use think\helper\Hash;
use app\user\model\Role as RoleModel;
use think\Db;

/**
 * 后台用户模型
 * @package app\admin\model
 */
class User extends Model
{
    // 设置当前模型对应的完整数据表名称
    protected $table = '__ADMIN_USER__';

    // 自动写入时间戳
    protected $autoWriteTimestamp = true;

    // 对密码进行加密
    public function setPasswordAttr($value)
    {
        return Hash::make((string)$value);
    }

    // 获取注册ip
    public function setSignupIpAttr()
    {
        return get_client_ip(1);
    }

    /**
     * 用户登录
     * @param string $username 用户名
     * @param string $password 密码
     * @param bool $rememberme 记住登录
     * @author 蔡伟明 <314013107@qq.com>
     * @return bool|mixed
     */
    public function login($username = '', $password = '', $rememberme = false)
    {
        $username = trim($username);
        $password = trim($password);

        // 匹配登录方式
        /*if (preg_match("/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/", $username)) {
            // 邮箱登录
            $map['email'] = $username;
        } elseif (preg_match("/^1\d{10}$/", $username)) {
            // 手机号登录
            $map['mobile'] = $username;
        } else {
            // 用户名登录
            $map['username'] = $username;
        }*/

        //所有方法爆炸
        //使用oAuth2 OpenId登录
        $map['openid'] = $username;

        $map['status'] = 1;

        // 查找用户
        $user = $this::get($map);
        if (!$user) {
            $this->error = '用户不存在或被禁用！';
        } else {
            // 检查是否分配用户组
            if ($user['role'] == 0) {
                $this->error = '禁止访问，原因：未分配角色！';
                return false;
            }
            // 检查是可登录后台
            /*if (!RoleModel::where('id', $user['role'])->value('access')) {
                $this->error = '禁止访问，原因：用户所在角色禁止访问后台！';
                return false;
            }
            if (!Hash::check((string)$password, $user->password)) {
                $this->error = '密码错误！';
            } else {*/
                $uid = $user->id;

                // 更新登录信息
                $user->last_login_time = request()->time();
                $user->last_login_ip   = get_client_ip(1);
                if ($user->save()) {
                    // 自动登录
                    return $this->autoLogin($this::get($uid), $rememberme);
                } else {
                    // 更新登录信息失败
                    $this->error = '登录信息更新失败，请重新登录！';
                    return false;
                }
            /*}*/
        }
        return false;
    }

    /**
     * 自动登录
     * @param object $user 用户对象
     * @param bool $rememberme 是否记住登录，默认7天
     * @author 蔡伟明 <314013107@qq.com>
     * @return bool|int
     */
    public function autoLogin($user, $rememberme = false)
    {
        // 记录登录SESSION和COOKIES
        $auth = array(
            'uid'             => $user->id,
            'group'           => $user->group,
            'role'            => $user->role,
            'role_name'       => Db::name('admin_role')->where('id', $user->role)->value('name'),
            'avatar'          => $user->avatar,
            'username'        => $user->username,
            'nickname'        => $user->nickname,
            'last_login_time' => $user->last_login_time,
            'last_login_ip'   => get_client_ip(1),
        );
        session('user_auth', $auth);
        session('user_auth_sign', $this->dataAuthSign($auth));

        $aes = new \com\iydhp\AES();
        cookie('user_session', $aes->aes128cbcBase64Encrypt(json_encode($auth)));

        // 保存用户节点权限
        if ($user->role != 1) {
            $menu_auth = Db::name('admin_role')->where('id', session('user_auth.role'))->value('menu_auth');
            $menu_auth = json_decode($menu_auth, true);
            /*if (!$menu_auth) {
                $this->error = '未分配任何节点权限！';
                return false;
            }*/
        }

        // 记住登录
        /*if ($rememberme) {
            $signin_token = $user->username.$user->id.$user->last_login_time;
            cookie('uid', $user->id, 24 * 3600 * 7);
            cookie('signin_token', $this->dataAuthSign($signin_token), 24 * 3600 * 7);
        }*/

        return $user->id;
    }

    /**
     * 数据签名认证
     * @param array $data 被认证的数据
     * @author 蔡伟明 <314013107@qq.com>
     * @return string 签名
     */
    public function dataAuthSign($data = [])
    {
        // 数据类型检测
        if(!is_array($data)){
            $data = (array)$data;
        }

        // 排序
        ksort($data);
        // url编码并生成query字符串
        $code = http_build_query($data);
        // 生成签名
        $sign = sha1($code);
        return $sign;
    }

    /**
     * 判断是否登录
     * @author 蔡伟明 <314013107@qq.com>
     * @return int 0或用户id
     */
    public function isLogin()
    {
        $user_session = cookie('user_session');
        if (!empty($user_session)){
            $aes = new \com\iydhp\AES();
            $user = json_decode($aes->aes128cbcDecrypt($user_session), true);
            if ($user['uid'] != ''){
                return $user['uid'];
            }
            return 0;
        }
        return 0;
    }

    public static function getUserByOpenId($openid){
        return User::get(['openid'=>$openid]);
    }

    public static function addUserByOpenId($openid){
        $username = Str::random(16);
        $data = [
            'openid' => $openid,
            'username' => $username,
            'nickname' => $username,
            'role' => 4,
            'avatar' => 8,
            'status' => 1,
            'signup_ip' => get_client_ip(1)
        ];
        $model = new User($data);
        if ($model->save() > 0){
            return $model['id'];
        }else{
            return false;
        }
    }

    public static function getHosterList(){
        return User::all(['role' => 3]);
    }

    public static function getUserByUid($uid, $fields){
        return self::where(['id' => $uid])->field($fields)->find();
    }

    public static function updateUser($map, $data){
        $data = array_merge($map, $data);
        return User::update($data);
    }

    public static function getUser($where, $fields){
        return self::where($where)->field($fields)->find();
    }

}

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

namespace app\user\home;

use app\index\controller\Home;
use app\user\model\Token;
use app\user\model\User as UserModel;
use think\Log;
use util\File;

class Index extends Home
{

    //MGC的官方oAuth将关闭这个appid,包括app使用的appid
    //你也许可以联系qq群547714682获得新的appid，或者自行改写用户模块

    public function login(){}

    /**
     * 登录
     * @param string $return_url
     * @param string $admin
     */
    public function oauth($return_url = '/', $admin = '0'){
        cookie('return_url', $return_url);

        header('Location: https://account.qi78.com/authorize?client_id=04eda8dccdf0ebf0bf49e10f8b59287b&response_type=code&scope=user_id&redirect_uri='.urlencode('https://sv.qi78.com/user/loginCallback'));
    }

    /**
     * 登录回调
     * @param $code
     */
    public function oauthCallback($code = ''){
        if($code == ''){
            exit();
        }
        $openid = $this->getOpenIdByCode($code);

        //exit($openid);

        $user = UserModel::getUserByOpenId($openid);
        if (!$user){ //用户未注册
            $uid['id'] = UserModel::addUserByOpenId($openid);
            if ($uid <= 0){
                $this->error('系统错误，请重新登录');
            }
        }

        /*if ($user['role'] == 1){
            $this->error('禁止登陆管理员账号');
        }*/

        $uid = (new UserModel())->login($openid);

        //File::write_file(RUNTIME_PATH."log".time().'.txt','code='.$code.'&opneid='.$openid.'&uid='.$uid.'&user='.json_encode($user));

        if ($uid && $uid > 0){
            action_log('user_signin', 'admin_user', $uid, $uid);
            if (cookie('return_url')){
                header('Location: '.urldecode(cookie('return_url')));
                exit();
            }
            header('Location: /');
        }else{
            exit($user->getError());
        }
    }

    /**
     * 登出
     * @param string $return_url
     */
    public function logout($return_url = ''){
        session(null);
        cookie('uid', null);
        cookie('signin_token', null);

        cookie('logout_return_url', $return_url);
        header('Location: https://account.qi78.com/user/logout?return_url='.urldecode('http://'.$_SERVER['HTTP_HOST'].'/user/logoutCallback'));
    }

    /**
     * 登出回调
     */
    public function logoutCallback()
    {
        if (cookie('logout_return_url')) {
            header('Location: ' . urldecode(cookie('logout_return_url')));
            exit();
        }
        header('Location: /');
    }

    /**
     * APP的code登录
     * @param $code
     * @return array
     */
    public function appLogin($code){
        $openid = $this->getOpenIdByCodeForApp($code);

        config('default_return_type','json');

        if ($user = UserModel::getUserByOpenId($openid)){ //用户已注册
            $token = $this->genApiToken($user['id']);
            return ajax_build_result(200, '登录成功', ['token' => $token['token'],'expire' => $token['expire']]);
        }else{ //未注册
            $uid = UserModel::addUserByOpenId($openid);
            $token = $this->genApiToken($uid);
            if ($uid > 0){
                return ajax_build_result(200, '登录成功', ['token' => $token['token'],'expire' => $token['expire']]);
            }else{
                return ajax_build_result(601, '登录失败');
            }
        }
    }

    /**
     * 获取服务器平台的ApiToken
     * @param string $code
     * @return array
     */
    public function getToken($code = ''){
        $token = Token::getTokenByCode($code);
        config('default_return_type','json');
        if ($token){
            return ajax_build_result(200, 'success', ['token'=> $token['token'], 'expire'=> $token['expire']]);
        }else{
            return ajax_build_result(601, 'not exist');
        }

    }

    /**
     * 按type获取服务器平台的ApiToken
     * @return array|bool
     */
    private function genApiToken($uid){
        $type = session('token_type') ? session('token_type') : 'app';
        return Token::addToken($uid, $type);
    }

    /**
     * 使用code换取openid：web
     * @param $code
     */
    private function getOpenIdByCode($code){
        $ch = curl_init();
        $post_data = [
            "grant_type" => "authorization_code",
            "code" => $code,
            'client_id' => '04eda8dccdf0ebf0bf49e10f8b59287b',
            'client_secret' => '',
            'redirect_uri' => 'https://sv.qi78.com/user/loginCallback'
        ];
        curl_setopt_array($ch,[
            CURLOPT_URL => "https://api.qi78.com/access_token.php",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_MAXREDIRS => 10,
            CURLOPT_TIMEOUT => 30,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "POST",
            CURLOPT_POSTFIELDS => $post_data,
            CURLOPT_SSL_VERIFYPEER => false,
        ]);
        $output = curl_exec($ch);
        if (empty($output)) {
            exit();
        }

        curl_close($ch);

        $json = json_decode($output, true);
        $openid = !empty($json['unionid']) ? $json['unionid'] : exit('系统错误1，请重新登录');
        return $openid;
    }

    /**
     * 使用code换取openid：app
     * @param $code
     */
    private function getOpenIdByCodeForApp($code){
        $ch = curl_init();
        $post_data = [
            "grant_type" => "authorization_code",
            "code" => $code,
            'client_id' => 'aaa384cbfe8a7641b59b3cd7114ec3c5',
            'client_secret' => '',
            'redirect_uri' => 'mgc://app/login'
        ];
        curl_setopt_array($ch,[
            CURLOPT_URL => "https://api.qi78.com/access_token.php",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_MAXREDIRS => 10,
            CURLOPT_TIMEOUT => 30,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "POST",
            CURLOPT_POSTFIELDS => $post_data,
            CURLOPT_SSL_VERIFYPEER => false,
        ]);
        $output = curl_exec($ch);
        if (empty($output)) {
            exit();
        }

        curl_close($ch);
        $json = json_decode($output, true);
        $openid = !empty($json['unionid']) ? $json['unionid'] : exit('');
        return $openid;
    }
}

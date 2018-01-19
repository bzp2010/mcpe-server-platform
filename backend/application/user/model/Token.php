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
 * 令牌模型
 * @package app\admin\model
 */
class Token extends Model
{

    public static function addToken($uid, $type, $expire = 2592000){
        $where = [
            'uid' => $uid,
            'type' => $type
        ];
        $data = [
            'uid' => $uid,
            'type' => $type,
            'code' => sha1(Str::random(20)),
            'token' => sha1(Str::random(20)),
            'expire' => time() + $expire
        ];
        if ($token = self::where($where)->find()){
            $token->delete();
        }
        $model = new Token($data);
        $result = $model->save();
        if ($result > 0){
            return $data;
        }else{
            return false;
        }
    }

    public static function getUidByToken($token){
        $data = Token::get(['token' => $token]);
        if (!$data){
            return -1; //Token不存在
        }
        if ($data['expire'] > time()){
            return $data['uid'];
        }else{
            return -2; //Token过期
        }
    }

    public static function getTokenByCode($code){
        return Token::get(['code' => $code]);
    }

}

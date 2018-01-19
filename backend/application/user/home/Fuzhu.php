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
use app\user\model\User;

class Fuzhu extends Home
{

    public function index(){

        if (!is_signin()){
            header('Location: /user/login?return_url='.urlencode('/user/fuzhu.html'));
            exit();
        }

        $user = User::getUserByUid(is_signin(),'id,openid,role');
        $model = new User();
        $model->login($user['openid']);
        if ($user['role'] <= 3){
            header('Location: /admin.php');
        }else{
            $requestCode = substr($user['openid'], 0, strlen($user['openid']) / 2);
            return view('',['requestCode' => $requestCode]);
        }
    }

}

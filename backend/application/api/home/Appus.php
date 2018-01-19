<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 13:22
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\home;

use app\api\model\Comment;
use app\home\model\Server;
use app\home\model\ServerStatus;
use app\index\controller\Home;
use app\user\model\Token;
use app\user\model\User;
use think\Config;
use think\Response;

class Appus extends ApiBase {

    //你需要修改这里的CDN地址

    protected function _initialize()
    {
        parent::_initialize();
    }

    /**
     * 获取启动页闪图
     * @return array
     */
    public function getSplashImage(){
        $data = [
            'url' => module_config('api.params_splash_image_url')
        ];
        return ajax_build_result(200,'success', $data);
    }

    /**
     * 获取评分项目
     * @param int $page
     * @param string $type
     * @param $serverid
     * @return array
     */
    public function listComment($type = 'server', $serverid = 1){
        $data = Comment::getCommentByTypeAndServerId(10, $type, $serverid);
        //$data = [];
        foreach ($data as $item){
            $user = User::getUserByUid($item['uid'], 'id,nickname,avatar,score');
            $path = get_file_path($user['avatar']);
            if (count(explode('://', $path)) > 1){
                $user['avatar'] = $path;
            }else{
                $user['avatar'] = 'https://svcdn.qi78.com'.$path;
            }
            $item['user'] = $user;
            unset($item['uid']);
            unset($item['type']);
            unset($item['serverid']);
        }
        return ajax_build_result(200, '查询成功', $data);
    }

    public function checkToken($token){
        $uid = Token::getUidByToken($token);
        if ($uid > 0){
            $data = ['expired' => false];
        }else{
            $data = ['expired' => true];
        }
        return ajax_build_result(200, '查询成功', $data);
    }

    public function test(){
        //var_dump(Comment::getCommentByTypeAndServerIdAndUid(10,'normal','1','1')); //获取某用户在某服务器的评价
        //var_dump(Comment::getScoreByTypeAndServerId('server','1')); //获取服务器的平均得分
        //var_dump(module_config('api.use_server_status_cache'));
        //var_dump(Server::getRandServer('id')['id']);
    }

    public function searchServer($category = 0, $key){
        $servers = Server::searchServer(10, $category, $key)->toArray();
        $newData = [];
        for ($i = 0;$i < count($servers['data']);$i++) {
            $item = $servers['data'][$i];
            if ($item['ip'] && $item['port']) {

                $status = ServerStatus::getStatus($item['id']);
                $status['numplayers'] = $status['nowplayer'];
                $status['maxplayers'] = $status['maxplayer'];
                $status['online'] = !empty($status) && intval($status['numplayers']) >= 0 && intval($status['maxplayers']) >0 ? true : false;
                if (!$status['online']) {
                    continue;
                }
                $item['nowPlayer'] = $status['numplayers'];
                $item['maxPlayer'] = $status['maxplayers'];
                $item['online'] = $status['online'];
                $item['score'] = Comment::getScoreByTypeAndServerId("server", $item['id']);
                unset($item['ip']);
                unset($item['port']);
                $item['type'] = '';
                $newData[] = $item;
            }
        }
        $servers['data'] = $newData;
        return ajax_build_result(200, '查询成功', $servers);
    }

    public function info(){
        header("Location: http://sv.kkmo.net/list/".input('get.id').".htm");
    }

}
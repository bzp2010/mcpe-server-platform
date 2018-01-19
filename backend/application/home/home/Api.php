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

namespace app\home\home;

use app\api\model\Comment;
use app\home\model\Category;
use app\home\model\Server;
use app\home\model\ServerStatus;
use app\index\controller\Home;
use think\Config;
use think\Exception;

class Api extends Home
{

    public function _initialize()
    {
        header('Access-Control-Allow-Origin: *');
        Config::set('default_return_type', 'json');
        Config::set('default_ajax_return', 'json');
    }

    public function getIndexServerList()
    {
        $featureServerId = module_config('home.server_feature');
        $featureServerId = $featureServerId != '' ? $featureServerId : 1;
        //$recommandServerId = module_config('home.server_recommand');
        //$recommandServerId = $recommandServerId != '' ? $recommandServerId : 1;
        getRandServer:{
            $recommandServerId = Server::getRandServer('id');
            $recommandServerId = $recommandServerId['id'];
        }
        if ($featureServerId == $recommandServerId) goto getRandServer;
        $list['feature'] = Server::getServerById($featureServerId);
        $list['recommend'] = Server::getServerById($recommandServerId);

        $listName[0] = 'feature';
        $listName[1] = 'recommend';
        if ($list){
            $countList = count($list);
            for ($i = 0; $i < $countList; $i++) {
                $item = $list[$listName[$i]];
                if ($item['ip'] && $item['port']){
                    if (module_config('api.use_server_status_cache') == 'on'){
                        $status = ServerStatus::getStatus($item['id']);
                        $status['numplayers'] = $status['nowplayer'];
                        $status['maxplayers'] = $status['maxplayer'];
                    }else{
                        try {
                            $mcQuery = new \xPaw\MinecraftQuery();
                            $mcQuery->Connect($item['ip'], intval($item['port']));
                            $status = $mcQuery->GetInfo();
                        }catch (\xPaw\MinecraftQueryException $e){}
                    }
                }else{
                    $status['numplayers'] = 0;
                    $status['maxplayers'] = 0;
                    $status['online'] = false;
                }
                if ($status == null || !$status['online']) {
                    unset($list[$i]);
                    continue;
                }
                $item['nowPlayer'] = $status['numplayers'];
                $item['maxPlayer'] = $status['maxplayers'];
                unset($item['ip']);
                unset($item['port']);
                unset($item['title']);
                unset($item['uid']);
                unset($item['create_time']);
                unset($item['update_time']);
                unset($item['desc']);
                unset($item['oneworddesc']);
                unset($item['image']);
                unset($item['icon']);
                unset($item['status']);
                unset($status);
            }
            $list['feature']['type'] = '精选服务器';
            $list['feature']['score'] = Comment::getScoreByTypeAndServerId("server", $featureServerId);
            $list['recommend']['type'] = '为您推荐';
            $list['recommend']['score'] = Comment::getScoreByTypeAndServerId("server", $recommandServerId);
            $data = ajax_build_result(200, 'success', $list);
        }else{
            $data = ajax_build_result(500, 'error', []);
        }

        return $data;
    }


    public function getServerList($category = '')
    {
        $count = Server::getAllServerCount();
        if ($count > 10){
            $list = Server::getServerListForApi(10, $category);
        }else{
            $list = Server::getServerListForApi($count, $category);
        }
        if ($list){
            $countList = count($list);
            $data = [];
            for ($i = 0; $i < $countList; $i++) {
                $item = $list[$i];
                $status = null;
                //获取服务器状态
                if ($item['ip'] && $item['port']){
                    if (module_config('api.use_server_status_cache') == 'on'){
                        $status = ServerStatus::getStatus($item['id']);
                        $status['numplayers'] = $status['nowplayer'];
                        $status['maxplayers'] = $status['maxplayer'];
                    }else{
                        try {
                            $mcQuery = new \xPaw\MinecraftQuery();
                            $mcQuery->Connect($item['ip'], intval($item['port']));
                            $status = $mcQuery->GetInfo();
                        }catch (\xPaw\MinecraftQueryException $e){}
                    }
                }
                if ($status == null || !$status['online']) {
                    unset($list[$i]);
                    continue;
                }

                $item['nowPlayer'] = $status['numplayers'];
                $item['maxPlayer'] = $status['maxplayers'];
                unset($item['ip']);
                unset($item['port']);
                unset($status);
                $item['score'] = Comment::getScoreByTypeAndServerId("server", $item['id']);
                $item['type'] = '';
                $data[] = $item;
            }
            return ajax_build_result(200, 'success', $data);
        }else{
            return ajax_build_result(500, 'error', []);
        }
    }

    public function getServerInfo($id = 1)
    {
        $info = Server::getServerById($id);

        if ($info){
            unset($info['create_time']);
            unset($info['update_time']);
            unset($info['status']);
            //unset($info['id']);
            unset($info['uid']);
            unset($info['title']);
            $status = null;
            if ($info['ip'] && $info['port']){
                if (module_config('api.use_server_status_cache') == 'on'){
                    $status = ServerStatus::getStatus($id);
                    $status['numplayers'] = $status['nowplayer'];
                    $status['maxplayers'] = $status['maxplayer'];
                }else{
                    try {
                        $mcQuery = new \xPaw\MinecraftQuery();
                        $mcQuery->Connect($info['ip'], intval($info['port']));
                        $status = $mcQuery->GetInfo();
                    }catch (\xPaw\MinecraftQueryException $e){}
                }
            }

            if ($status && $status['online']) {
                $info['nowPlayer'] = $status['numplayers'];
                $info['maxPlayer'] = $status['maxplayers'];
                $info['online'] = $status['online'];
            }elseif($status && !$status['online']){
                $info['nowPlayer'] = 0;
                $info['maxPlayer'] = 0;
                $info['online'] = false;
            }else{
                $info['nowPlayer'] = 0;
                $info['maxPlayer'] = 0;
                $info['online'] = false;
            }
            $info['score'] = Comment::getScoreByTypeAndServerId('server', $id);
            $info['scorernum'] = Comment::getScorerNumByTypeAndServerId('server', $id);
            $data = ajax_build_result(200, 'success', $info);
        }else{
            $data = ajax_build_result(500, 'error', []);
        }

        return $data;
    }

    public function getCategory(){
        $info = Category::getAllCategoryList(['status' => 1]);
        foreach ($info as $key => $obj){
            $categoryListTemp[$key] = $obj->getData();
        }
        $categoryListTemp = $this->multi_array_sort($categoryListTemp,'sort');
        if ($categoryListTemp){
            foreach ($categoryListTemp as $item){
                unset($item['sort']);
                unset($item['status']);
                $item['icon'] = '';
                $categoryList[] = $item;
            }
            $data = ajax_build_result(200, 'success', $categoryList);
        }else{
            $data = ajax_build_result(500, 'error', []);
        }
        return $data;
    }

    private function multi_array_sort($multi_array,$sort_key,$sort=SORT_ASC){
        if(is_array($multi_array)){
            foreach ($multi_array as $row_array){
                if(is_array($row_array)){
                    $key_array[] = $row_array[$sort_key];
                }else{
                    return false;
                }
            }
        }else{
            return false;
        }
        array_multisort($key_array,$sort,$multi_array);
        return $multi_array;
    }
}

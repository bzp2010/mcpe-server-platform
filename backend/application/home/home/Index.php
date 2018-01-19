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

class Index extends Home
{

    public function _initialize()
    {
        header('Access-Control-Allow-Origin: *');
        Config::set('default_return_type', 'json');
        Config::set('default_ajax_return', 'json');
    }

    public function getIndexServerList()
    {
        $serverInfoFields = 'id,name,version,category,background,manager,ip,port';
        $featureServerId = module_config('home.server_feature') != '' ? module_config('home.server_feature') : 1;
        $featureServerInfo = Server::getServerById($featureServerId, $serverInfoFields);
        if ($featureServerInfo){
            $featureServerInfo['type'] = '精选服务器';
            $featureServerInfo['score'] = Comment::getScoreByTypeAndServerId('server', $featureServerId);
            if ($featureServerInfo['ip'] && $featureServerInfo['port']){
                $status = ServerStatus::getStatus($featureServerId);
                $featureServerInfo['nowPlayer'] = $status['nowplayer'];
                $featureServerInfo['maxPlayer'] = $status['maxplayer'];
                $featureServerInfo['online'] = $status['online'];
                unset($featureServerInfo['ip']);
                unset($featureServerInfo['port']);
            }
        }

        getRandonServer : {
            $recommandServerId = Server::getRandomServerId();
            $recommandServerInfo = Server::getServerById($recommandServerId, $serverInfoFields);
            if ($recommandServerInfo){
                $recommandServerInfo['type'] = '为您推荐';
                $recommandServerInfo['score'] = Comment::getScoreByTypeAndServerId('server', $recommandServerId);
                if ($recommandServerInfo['ip'] && $recommandServerInfo['port']){
                    $status = ServerStatus::getStatus($recommandServerId);
                    if(!$status['online']){
                        goto getRandonServer;
                    }
                    $recommandServerInfo['nowPlayer'] = $status['nowplayer'];
                    $recommandServerInfo['maxPlayer'] = $status['maxplayer'];
                    $recommandServerInfo['online'] = $status['online'];
                    unset($recommandServerInfo['ip']);
                    unset($recommandServerInfo['port']);
                }
            }
        }

        $list['feature'] = $featureServerInfo;
        $list['recommend'] = $recommandServerInfo;
        $data = ajax_build_result(200, 'success', $list);
        return $data;
    }

    public function getIndexServerListClone()
    {
        $serverInfoFields = 'id,name,version,category,background,manager,ip,port';
        $featureServerId = module_config('home.server_feature') != '' ? module_config('home.server_feature') : 1;
        $featureServerInfo = Server::getServerById($featureServerId, $serverInfoFields);
        if ($featureServerInfo){
            $featureServerInfo['type'] = '精选服务器';
            $featureServerInfo['score'] = Comment::getScoreByTypeAndServerId('server', $featureServerId);
            if ($featureServerInfo['ip'] && $featureServerInfo['port']){
                $status = ServerStatus::getStatus($featureServerId);
                $featureServerInfo['nowPlayer'] = $status['nowplayer'];
                $featureServerInfo['maxPlayer'] = $status['maxplayer'];
                $featureServerInfo['online'] = $status['online'];
                unset($featureServerInfo['ip']);
                unset($featureServerInfo['port']);
            }
        }

        getRandonServer : {
        $recommandServerId = Server::getRandomServerId();
        $recommandServerInfo = Server::getServerById($recommandServerId, $serverInfoFields);
        if ($recommandServerInfo){
            $recommandServerInfo['type'] = '为您推荐';
            $recommandServerInfo['score'] = Comment::getScoreByTypeAndServerId('server', $recommandServerId);
            if ($recommandServerInfo['ip'] && $recommandServerInfo['port']){
                $status = ServerStatus::getStatus($recommandServerId);
                if(!$status['online']){
                    goto getRandonServer;
                }
                $recommandServerInfo['nowPlayer'] = $status['nowplayer'];
                $recommandServerInfo['maxPlayer'] = $status['maxplayer'];
                $recommandServerInfo['online'] = $status['online'];
                unset($recommandServerInfo['ip']);
                unset($recommandServerInfo['port']);
            }
        }
    }

        $list[] = $featureServerInfo;
        $list[] = $recommandServerInfo;
        $data = ajax_build_result(200, 'success', $list);
        return $data;
    }

    public function getServerList($category = '')
    {
        if ($category == '0'){
            return $this->getIndexServerListClone();
        }

        $count = Server::getAllServerCount();
        if ($count > 10){
            $list = Server::getServerListForApi(20, $category);
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
                    $status = ServerStatus::getStatus($item['id']);
                    $item['nowPlayer'] = $status['nowplayer'];
                    $item['maxPlayer'] = $status['maxplayer'];
                    $item['online'] = $status['online'];
                    unset($item['ip']);
                    unset($item['port']);
                    if (!$status['online']) {
                        continue;
                    }
                }else{
                    continue;
                }

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
        if ($info = Server::getServerById($id,'id,name,version,category,background,manager,ip,port,oneworddesc,desc,image,icon')){
            if ($info['ip'] && $info['port']){
                $status = ServerStatus::getStatus($id);
                $info['nowPlayer'] = $status['nowplayer'];
                $info['maxPlayer'] = $status['maxplayer'];
                $info['online'] = $status['online'];
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
        $categorys = Category::getAllCategoryList(['status' => 1]);
        foreach ($categorys as $key => $obj){
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

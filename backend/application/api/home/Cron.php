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
use think\Cache;
use think\Config;
use think\Response;

class Cron extends ApiBase {

    protected function _initialize()
    {
        parent::_initialize();
    }

    public function getServerListToCacheStatus($position = 'BJ'){
        action:
            $cur_page = Cache::get('CRON_SERVER_CUR_PAGE_'.$position, 1);
            if ($servers = Server::getServerListForCron(5, $cur_page)){ //正常执行
                return $servers;
            }else{ //一轮结束
                Cache::set('CRON_SERVER_CUR_PAGE_'.$position, 1);
                goto action;
            }

    }

    public function uploadServerStatus($position = 'BJ', $data = ''){
        $cur_page = Cache::get('CRON_SERVER_CUR_PAGE_'.$position, 1);
        $data = json_decode($data, true);
        if ($data != ''){
            for ($i = 0;$i < count($data);$i++){
                ServerStatus::updateStatus($data[$i]);
            }
            Cache::set('CRON_SERVER_CUR_PAGE_'.$position, $cur_page + 1);
        }
        return ['status' => 'ok'];
    }

}
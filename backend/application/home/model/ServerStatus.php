<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 0:20
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\home\model;

use think\Model;

class ServerStatus extends Model {

    public static function updateStatus($data){
        $data['update_time'] = time();
        if (ServerStatus::get($data['id'])){
            return ServerStatus::update($data);
        }else{
            $model = new ServerStatus($data);
            $model->save();
        }
    }

    public static function getStatus($id){
        return ServerStatus::get($id);
    }

    public function getOnlineAttr($data){
        if ($data == 1) {
            return true;
        }else{
            return false;
        }
    }

}
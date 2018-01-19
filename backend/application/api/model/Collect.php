<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/14
 * Time: 11:55
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\model;

use think\Model;

class Collect extends Model {

    public static function addCollect($uid, $type, $id){
        $data = [
            'uid' => $uid,
            'type' => $type,
            'serverid' => $id,
            'time' => time()
        ];
        $model = new Collect($data);
        if ($result = $model->save() == 1){
            return $data;
        }else{
            return false;
        }
    }

    public static function getCollectsByUid($listRows = 10,$uid){
        return self::where([
            'uid' => $uid
        ])->field('id,type,serverid,time')->paginate($listRows);
    }

    public static function getCollectIdByTypeAndServerIdAndUid($uid, $type = 'server', $serverid = 0){
        return self::where([
            'type' => $type,
            'serverid' => $serverid,
            'uid' => $uid
        ])->field('id,time')->find();
    }

    public static function deleteCollectById($id){
        $collect = Collect::get($id);
        if($result = $collect->delete() == 1){
            return true;
        }else{
            return false;
        }
    }

    public static function isCollected($uid, $type, $serverid){
        return Collect::get([
            'uid' => $uid,
            'type' => $type,
            'serverid' => $serverid
        ]) ? true : false;
    }

}
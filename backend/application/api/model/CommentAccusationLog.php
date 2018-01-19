<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/14
 * Time: 11:55
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\model;

use think\Model;

class CommentAccusationLog extends Model {

    public static function addAccusationLog($uid, $cid){
        $log = CommentAccusationLog::getAccusationLog($uid, $cid);
        if ($log){
            return false;
        }
        $data = [
            'uid' => $uid,
            'cid' => $cid,
            'time' => time()
        ];
        $model = new CommentAccusationLog($data);
        if ($model->save() > 0){
            return $data;
        }else{
            return false;
        }
    }

    public static function getAccusationLog($uid, $cid){
        return CommentAccusationLog::get(['uid' => $uid, 'cid' => $cid]);
    }

}
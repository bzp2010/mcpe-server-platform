<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/14
 * Time: 11:55
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\model;

use think\Model;

class CommentAccusation extends Model {

    public static function addAccusation($uid, $cid){
        if (CommentAccusationLog::addAccusationLog($uid, $cid)){
            $accusation = CommentAccusation::get(['cid' => $cid]);
            if ($accusation){
                //大于标准锁死评分
                $lock_accusation_time = module_config('api.params_lock_accusation_time');
                $lock_accusation_time = $lock_accusation_time ? $lock_accusation_time : 5;
                if ($accusation['time'] > $lock_accusation_time){
                    Comment::updateStatus($cid, '2');
                }

                //更新评分举报次数
                $result = $accusation
                    ->where(['cid' => $cid])
                    ->update(['time' => $accusation['time'] + 1]);
                if ($result){
                    return 1;
                }else{
                    return 0;
                }
            }else{
                $data = [
                    'cid' => $cid,
                    'time' => 1
                ];
                $model = new CommentAccusation($data);
                return $model->save();
            }
        }else{ // 重复举报
            return -1;
        }
    }



}
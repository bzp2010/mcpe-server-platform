<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/14
 * Time: 11:55
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\model;

use app\home\model\Server;
use think\Model;

class Comment extends Model {

    public static function addComment($uid, $type, $serverid, $score, $text){
        $data = [
            'uid' => $uid,
            'type' => $type,
            'serverid' => $serverid,
            'score' => $score,
            'text' => $text,
            'time' => time()
        ];
        $model = new Comment($data);
        if ($result = $model->save() == 1){
            return $data;
        }else{
            return false;
        }
    }

    public static function editComment($uid, $type, $serverid, $score, $text){
        $result = self::where([
            'uid' => $uid,
            'type' => $type,
            'serverid' => $serverid
        ])->update([
            'score' => $score,
            'text' => $text
        ]);
        if ($result > 0){
            return true;
        }else{
            return false;
        }
    }

    public static function getCommentByTypeAndServerId($listRows = 10,$type, $serverid){
        return self::where([
            'type' => $type,
            'serverid' => $serverid,
            'status' => 1
        ])->field('id,uid,score,text,time')->paginate($listRows);
    }

    public static function deleteCommentById($id){
        $comment = Comment::get($id);
        if($result = $comment->delete() == 1){
            return true;
        }else{
            return false;
        }
    }

    public static function getCommentByTypeAndServerIdAndUid($type, $serverid, $uid){
        return self::where([
            'type' => $type,
            'serverid' => $serverid,
            'uid' => $uid
        ])->find();
    }

    public static function updateStatus($id, $status){
        return Comment::update(['id' => $id, 'status' => $status]);
    }

    public static function getCommentForAdmin($listRows = 10){
        return self::where(1)->paginate($listRows);
    }

    public static function getCommentForFuzhu($listRows = 10, $uid = 1){
        $server = Server::getAllServerByUid($uid);
        $serverIds = [];
        for ($i = 0; $i < count($server); $i++){
            $serverIds[] = $server[$i]['id'];
        }
        $where['type'] = 'server';
        $where['serverid'] = ['in', $serverIds];
        return self::where($where)->paginate($listRows);
    }

    /*public function getTimeAttr($time){
        return date('Y-m-d H:i:s', $time);
    }*/

    ///////////////////////////////////////////////////////翻新//////////////////////////

    /**
     * 获取服务器评分
     * @param $type
     * @param $serverid
     * @return float|int
     */
    public static function getScoreByTypeAndServerId($type, $serverid){
        $data = self::where([
            'type' => $type,
            'serverid' => $serverid
        ])->field('score')->select();
        $score = 0;
        foreach ($data as $item) {
            $score = $score + $item['score'];
        }
        if (count($data) != 0) {
            return floatval(sprintf("%.1f", $score / count($data)));
        }
        return 0;
    }

    /**
     * 获取服务器评分人数
     * @param $type
     * @param $serverid
     * @return int|string
     */
    public static function getScorerNumByTypeAndServerId($type, $serverid){
        return self::where([
            'type' => $type,
            'serverid' => $serverid
        ])->count('id');
    }

}
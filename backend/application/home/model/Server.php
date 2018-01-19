<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 0:20
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\home\model;

use think\Model;

define('TIME', time());

class Server extends Model {

    protected $insert = ['create_time' => TIME];
    protected $update = ['update_time' => TIME];

    public static function getServerListForApi($limit = 10, $category){
        $field = 'id,category,name,manager,version,background,ip,port';
        $map['status'] = 1;
        if ($category){
            $map['category'] = $category;
            return self::where($map)
                ->field($field)
                ->order('rand()')
                ->limit($limit)
                ->select();
        }else{
            return self::where($map)
                ->field($field)
                ->order('rand()')
                ->limit($limit)
                ->select();
        }
    }

    public static function getServerListForAdmin($listRows = 10, $isAdmin = false, $uid = 0){
        if ($isAdmin){
            return self::where(1)->paginate($listRows);
        }else{
            return self::where('uid', $uid)->paginate($listRows);
        }

    }

    /**
     * 服务器状态分布式缓存
     * @param int $listRows
     * @param bool $isAdmin
     * @param int $uid
     * @return \think\Paginator
     */
    public static function getServerListForCron($listRows = 10, $page){
        return self::where(1)->field('id,ip,port')->page($page, $listRows)->select();
    }

    public static function getUnVerifyServerList($listRows = 10){
        return self::where('status', 2)->paginate($listRows);
    }

    public static function getAllServerCount(){
        return self::count('id');
    }

    public static function getAllServerCountByUid($uid){
        return self::where('uid', $uid)->count();
    }

    public static function getAllServerByUid($uid){
        return Server::all(['uid' => $uid]);
    }

    public static function getUnVerifyServerCount(){
        return self::where('status', 2)->count();
    }

    public static function searchServer($listRows = 10, $category = 0, $key){
        $field = 'id,category,name,manager,version,background,ip,port';
        if ($category != 0){
            return self::where('name','like',"%{$key}%")
                ->where('category',$category)
                ->field($field)
                ->paginate($listRows);
        }else{
            return self::where('name','like',"%{$key}%")
                ->field($field)
                ->paginate($listRows);
        }

    }

    public function getCategoryAttr($id){
        $categoryInfo = Category::getCategoryById($id);
        return $categoryInfo['name'];
    }

    public function getIconAttr($id){
        $path = get_file_path($id);
        if (count(explode('://', $path)) > 1){
            return $path;
        }else{
            return 'https://svcdn.qi78.com'.$path;
        }
    }

    public function getBackgroundAttr($id){
        $path = get_file_path($id);
        if (count(explode('://', $path)) > 1){
            return $path;
        }else{
            return 'https://svcdn.qi78.com'.$path;
        }
    }

    public function getImageAttr($id){
        $ids = is_array(explode(',', $id)) ? explode(',', $id) : [$id];
        $result = [];
        foreach ($ids as $item) {
            $path = get_file_path($item);
            if (count(explode('://', $path)) > 1){
                $result[] = $path;
            }else{
                $result[] = 'https://svcdn.qi78.com'.$path;
            }
        }
        return $result;
    }

    public function getDescAttr($desc){
        $desc = nl2br($desc);
        $desc = str_replace(array("\n","\r"), "", $desc);
        return $desc;
    }

    ///////////////////////////////////////////////////////翻新//////////////////////////

    /**
     * 获取服务器二合一
     * @param $where
     * @param $fields
     * @param int $listRows
     * @return \think\Paginator
     */
    public static function getServer($where, $fields = '', $listRows = 10){
        return self::where($where)->field($fields)->paginate($listRows);
    }

    /**
     * 获取服务器信息
     * @param $id
     * @param string $fields
     * @return array|false|\PDOStatement|string|Model
     */
    public static function getServerById($id, $fields = ''){
        return self::where('id',$id)->field($fields)->find();
    }

    /**
     * 获取服务器数量
     * @param $where
     * @param string $fields
     * @return int|string
     */
    public static function getServerCount($where = 1, $fields = 'id'){
        return self::where($where)->count($fields);
    }


    public static function getRandomServerId(){
        $data = self::where(['status' => 1])->field('id')->order("rand()")->find();
        return $data['id'];
    }

}
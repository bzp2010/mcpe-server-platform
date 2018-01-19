<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 10:08
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\home\model;

use think\Model;

class Category extends Model {

    public static function getCategoryById($id){
        return Category::get($id,true);
    }

    public static function getAllCategoryListForAdmin(){
        return Category::all();
    }

    public static function getAllCategoryListForApi(){
        return Category::all(['status'=>'1']);
    }

    public static function getAllCategoryList($where){
        return Category::all($where);
    }

    public static function getAllCategoryCount(){
        return self::count();
    }
}
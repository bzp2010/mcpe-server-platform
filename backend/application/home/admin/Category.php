<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 13:22
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\home\admin;

use app\admin\controller\Admin;
use app\common\builder\ZBuilder;
use app\home\model\Category as CategoryModel;
use app\home\model\Server as ServerModel;
use app\user\model\Role;
use think\Request;

class Category extends Admin {

    public function index(){
        $categoryList = CategoryModel::getAllCategoryListForAdmin();

        $addFields = [
            ['text', 'name', '类别名称', ''],
            ['text', 'sort', '排序等级', ''],
        ];

        $editFields = [
            ['hidden', 'id', 'ID'],
            ['text', 'name', '类别名称', ''],
            ['text', 'sort', '排序等级', ''],
        ];

        return ZBuilder::make('table')
            ->setPageTitle('服务器类别')
            ->addColumns([
                ['id', 'ID'],
                ['name', '类别名称'],
                ['sort','排序等级'],
                ['status','排序等级','status'],
                ['right_button', '操作', 'btn']
            ])
            ->addRightButton('enable', ['table' => 'category']) // 启用
            ->addRightButton('disable', ['table' => 'category']) // 禁用
            ->addRightButton('delete', ['table' => 'category']) // 删除
            ->autoAdd($addFields,'category','','','',true) // 删除
            ->autoEdit($editFields,'category','','','',true) // 删除
            ->setRowList($categoryList)
            ->fetch();

    }

}
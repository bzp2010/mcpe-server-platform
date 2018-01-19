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
use app\user\model\Role;
use app\user\model\User;
use think\Db;
use app\home\model\Server as ServerModel;
use app\home\model\Category as CategoryModel;

class Index extends Admin {

    public function index(){
        $isAdmin = Role::checkAuth('home/server/allctr', true);
        $unVerifyCount = ServerModel::getUnVerifyServerCount();
        if ($isAdmin) {
            $allCount = ServerModel::getAllServerCount();
        }else{
            $allCount = ServerModel::getAllServerCountByUid(is_signin());
        }
        $categoryCount = CategoryModel::getAllCategoryCount();
        return $this->fetch('',[
            'isAdmin' => $isAdmin,
            'unVerifyCount' => $unVerifyCount,
            'allCount' => $allCount,
            'categoryCount' => $categoryCount,
        ]);
    }

    public function config(){
        return $this->moduleConfig();
    }

    public function fuzhu($requestCode = ''){
        if ($requestCode){
            $user = User::getUser(['openid' => ['like', $requestCode.'%']], 'id, role');
            if ($user && $user['role'] != 3){
                $result = User::updateUser(['id' => $user['id']], ['role' => 3]);
                if ($result){
                    $this->success('提升成功');
                }
            }
            $this->error('提升失败');
        }else{
            return ZBuilder::make('form')
                ->addText('requestCode','申请码','服主申请码')
                ->fetch();
        }
    }

}
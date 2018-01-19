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
use app\home\model\Category;
use app\home\model\Server as ServerModel;
use app\user\model\Role;
use app\user\model\User;
use think\Request;

class Server extends Admin {

    public function index(){
        $isAdmin = Role::checkAuth('home/server/allctr', true); //是否具有完全控制权限节点
        $serverList = ServerModel::getServerListForAdmin(10, $isAdmin, is_signin());
        $categoryList = Category::getAllCategoryListForAdmin();
        foreach ($categoryList as $item){
            $categoryNameList[$item['id']] = $item['name'];
        }
        $addFields = [
            ['hidden', 'uid', is_signin()],
            ['select', 'category', '服务器类型', [], $categoryNameList],
            ['text', 'name', '服务器名称', ''],
            ['text', 'title', '服务器标题', ''],
            ['text', 'manager', '服务器管理者', ''],
            ['text', 'version', '服务器版本'],
            ['text', 'oneworddesc', '一句话服务器介绍', ''],
            ['textarea', 'desc', '服务器介绍', ''],
            ['text', 'ip', '服务器IP',''],
            ['text', 'port' , '服务器PORT'],
            ['image', 'icon', '服务器logo'],
            ['image', 'background', '服务器列表背景','请确保您的图片色调较深'],
            ['images', 'image', '服务器预览图', '可上传多张'],
        ];
        $editFields = [
            ['hidden', 'id'],
            ['select', 'category', '服务器类型', [], $categoryNameList],
            ['text', 'name', '服务器名称', ''],
            ['text', 'title', '服务器标题', ''],
            ['text', 'manager', '服务器管理者', ''],
            ['text', 'version', '服务器版本'],
            ['text', 'oneworddesc', '一句话服务器介绍', ''],
            ['textarea', 'desc', '服务器介绍', ''],
            ['text', 'ip', '服务器IP',''],
            ['text', 'port' , '服务器PORT'],
            ['image', 'icon', '服务器logo'],
            ['image', 'background', '服务器列表背景','请确保您的图片色调较深'],
            ['images', 'image', '服务器预览图', '可上传多张'],
        ];
        if (Role::checkAuth('home/server/allctr', true)){
            $addFields[] = ['select', 'status', '服务器状态', [], ['1'=>'已审核','2'=>'未审核','0'=>'已禁用']];
            $editFields[] = ['text', 'uid', '服务器创建者', [], 0];
            $editFields[] = ['select', 'status', '服务器状态', [], ['1'=>'已审核','2'=>'未审核','0'=>'已禁用']];
        }
        return ZBuilder::make('table')
            ->setPageTitle('服务器列表')
            ->setSearch(['id' => 'ID', 'name' => '服务器名称', 'manager' => '服务器管理者'])
            ->addColumns([
                ['id', 'ID'],
                ['name', '服务器名称'],
                ['manager','管理者'],
                ['version','服务器版本'],
                ['ip','服务器IP'],
                ['port','服务器端口'],
                ['status', '状态', 'status'],
                ['right_button', '操作', 'btn']
            ])
            //->addRightButton('enable', ['table' => 'server']) // 启用
            //->addRightButton('disable', ['table' => 'server']) // 禁用
            ->addRightButton('delete', ['table' => 'server']) // 删除
            //->addTopButton('enable', ['table' => 'server']) // 启用
            //->addTopButton('disable', ['table' => 'server']) // 禁用
            ->addTopButton('delete', ['table' => 'server']) // 删除
            ->autoAdd($addFields, 'server', '', true ,'' , false)
            ->autoEdit($editFields,'server','',true,'',false) // 删除
            ->setRowList($serverList)
            ->fetch();
    }

    public function add()
    {
        if (!Role::checkAuth('home/server/allctr', true)){
            $serverCount = ServerModel::getAllServerCountByUid(is_signin());
            if ($serverCount >= module_config('home.fz_max_server_num')){
                $this->error('您已添加的服务器数量超过限制，无法添加更多了');
            }
            $this->request->post('status', '2');
        }
        $this->request->post('uid', is_signin());
        return parent::add();
    }

    public function enable($record = [])
    {
        $this->checkCanEnDisDel($record,'enable');
        return parent::enable($record);
    }

    public function disable($record = [])
    {
        $this->checkCanEnDisDel($record, 'disable');
        return parent::disable($record);
    }

    public function delete($record = [])
    {
        $this->checkCanEnDisDel($record, 'delete');
        return parent::delete($record);
    }

    private function checkCanEnDisDel($record, $action = 'enable'){
        $isAdmin = Role::checkAuth('home/server/allctr', true);
        if (!$isAdmin) {
            if (in_array($action, ['enable', 'disable'])){
                $this->error('您不是管理员，无权启用或禁用');
            }
            if (is_array($record)) {
                foreach ($record as $item) {
                    $serverInfo = ServerModel::getServerById($item);
                    if ($serverInfo['uid'] != is_signin()){
                        $this->error('您不是服务器：'.$serverInfo['name'].'的创建者，无权编辑');
                    }
                }
            }else{
                $serverInfo = ServerModel::getServerById($record);
                if ($serverInfo['uid'] != is_signin()){
                    $this->error('您不是服务器：'.$serverInfo['name'].'的创建者，无权编辑');
                }
            }
        }
    }

    public function edit($id = '')
    {
        $serverInfo = ServerModel::getServerById($id);
        $isAdmin = Role::checkAuth('home/server/allctr', true);
        if (!$isAdmin && $serverInfo['uid'] != is_signin()){
            $this->error('您不是服务器：'.$serverInfo['name'].'的创建者，无权编辑');
        }
        if ($this->request->isPost() && !$isAdmin){
            $this->request->post(['status'=>'2']);
        }
        return parent::edit($id);
    }

}
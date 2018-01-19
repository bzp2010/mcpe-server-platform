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
use think\Request;

class Verify extends Admin {

    public function index(){
        $this->checkAuth(); //检验权限
        $serverList = ServerModel::getUnVerifyServerList();
        $categoryList = Category::getAllCategoryListForAdmin();
        foreach ($categoryList as $item){
            $categoryNameList[$item['id']] = $item['name'];
        }
        $editFields = [
            //['hidden', 'id'],
            ['select', 'category', '服务器类型', [], $categoryNameList],
            ['text', 'name', '服务器名称', ''],
            ['text', 'title', '服务器标题', ''],
            ['text', 'manager', '服务器管理者', ''],
            ['text', 'version', '服务器版本'],
            ['text', 'desc', '服务器介绍', ''],
            ['text', 'ip', '服务器IP',''],
            ['text', 'port' , '服务器PORT'],
            ['image', 'icon', '服务器logo'],
            ['image', 'background', '服务器列表背景','请确保您的图片色调较深'],
            ['images', 'image', '服务器预览图', '可上传多张'],
        ];
        $btn_access = [
            'title' => '查看详情',
            'icon'  => 'fa fa-fw fa-info',
            'href'  => url('info', ['id' => '__id__'])
        ];
        return ZBuilder::make('table')
            ->setPageTitle('服务器审核')
            ->addColumns([
                ['id', 'ID'],
                ['name', '服务器名称'],
                ['manager','管理者'],
                ['version','服务器版本'],
                ['status', '状态', 'status'],
                ['right_button', '操作', 'btn']
            ])
            ->addRightButton('custom', $btn_access, false) // 添加授权按钮
            ->setRowList($serverList)
            ->fetch();
    }

    public function info($id = 1) {
        $this->checkAuth(); //检验权限
        $serverInfo = ServerModel::getServerById($id);
        $data = $serverInfo->getData();
        $data_desc_html = $data['desc'];
        $data_background = '<img width="800px" src="'.get_file_path($data['background']).'" />';
        $data_icon = '<img width="128px" src="'.get_file_path($data['icon']).'" />';
        $data_image_id = explode(',',$data['image']);
        $data_image_div_width = (800 * count($data_image_id) + 5 * (count($data_image_id) - 1));
        $data_image = '<div style="overflow:auto;max-width: 100%;height: auto;"><div style="width:'.$data_image_div_width.'px;">';
        $data['category'] = $serverInfo['category'];
        for ($i = 0; $i < count($data_image_id); $i++){
            if ($i != count($data_image_id) - 1){
                $data_image .= '<img style="width:800px;margin-right:5px;" src="'.get_file_path($data_image_id[$i]).'" />';
            }else{
                $data_image .= '<img style="width:800px;" src="'.get_file_path($data_image_id[$i]).'" />';
            }
        }
        $data_image .= '</div></div>';
        unset($data['icon']);
        unset($data['background']);
        unset($data['image']);

        return ZBuilder::make('form')
            ->setPageTitle('查看详情')
            ->addFormItems([
                ['static', 'id','ID'],
                ['static', 'category', '服务器类型'],
                ['static', 'name', '服务器名称'],
                ['static', 'title', '服务器标题'],
                ['static', 'manager', '服务器管理者'],
                ['static', 'version', '服务器版本'],
                ['static', 'desc', '服务器介绍', $data_desc_html],
                ['static', 'ip', '服务器IP'],
                ['static', 'port' , '服务器PORT'],
                ['static', 'icon', '服务器logo', $data_icon],
                ['static', 'background', '服务器列表背景', $data_background],
                ['static', 'image', '服务器预览图', $data_image],
            ])
            ->setFormData($data)
            ->setUrl(url('verify',['id'=>$id]))
            ->hideBtn(['submit'])
            ->addBtn('<button type="submit" class="btn btn-success" name="verify_action" value="1">同意申请</button>')
            ->addBtn('<button type="submit" class="btn btn-danger" name="verify_action" value="0">拒绝申请</button>')
            ->fetch();
    }

    public function verify($id = 1){
        $this->checkAuth(); //检验权限
        if ($this->request->isPost()){
            $verify_action = $this->request->post('verify_action');
            if ($id && $verify_action && $verify_action == 1){
                $serverInfo = ServerModel::getServerById($id);
                $serverInfo->status = 1;
                $serverInfo->save();
                $this->success('审核已通过');
            }
            $this->success('审核未通过');
        }
    }

    private function checkAuth(){
        $isAdmin = Role::checkAuth('home/verify/allctr', true);
        if (!$isAdmin){
            $this->error('您不是服务器平台管理员，无权审核');
        }
    }

    public function edit($id = ''){}
    public function add(){}
    public function delete($record = []){}
    public function enable($record = []){}
    public function disable($record = []){}


}
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
use app\api\model\Comment as CommentModel;

class Comment extends Admin {

    public function index($list_rows = 10){
        $isAdmin = Role::checkAuth('home/comment/allctr', true);
        if ($isAdmin){
            $commentList = CommentModel::getCommentForAdmin($list_rows);
        }else{
            $commentList = CommentModel::getCommentForFuzhu($list_rows, UID);
        }

        foreach ($commentList as $item){
            $item['time'] = date('Y-m-d H:i:s', $item['time']);
        }

        $editFields = [
            ['static', 'id', 'ID', '', '', true],
            ['text', 'score', '评分'],
            ['text', 'text', '评分内容'],
            ['static', 'time', '时间'],
        ];
        if (Role::checkAuth('home/comment/allctr', true)){
            $editFields[] = ['select', 'status', '评分状态', [], ['1'=>'已审核','2'=>'未审核','0'=>'已禁用']];
        }
        $builder = ZBuilder::make('table')
            ->setPageTitle('服务器评价')
            ->addColumns([
                ['id', 'ID'],
                ['serverid', '服务器ID'],
                ['score', '评分'],
                ['text', '评分内容'],
                ['time','时间'],
                ['status','状态', 'status'],
            ])
            ->addRightButton('enable', ['table' => 'comment']) // 启用
            ->addRightButton('disable', ['table' => 'comment']) // 禁用
            ->addRightButton('delete', ['table' => 'comment']) // 删除
            ->addTopButton('enable', ['table' => 'comment']) // 启用
            ->addTopButton('disable', ['table' => 'comment']) // 禁用
            ->addTopButton('delete', ['table' => 'comment']); // 删除
        if ($isAdmin){
            $builder->addColumn('right_button', '操作', 'btn');
            $builder->autoEdit($editFields,'comment','',true,'Y-m-d H:i:s',false);
        }
        return $builder->setRowList($commentList)
            ->fetch();
    }

}
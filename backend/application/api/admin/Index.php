<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 13:22
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\admin;

use app\admin\controller\Admin;

class Index extends Admin {

    public function index(){

    }

    public function config(){
        return $this->moduleConfig();
    }

}
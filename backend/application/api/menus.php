<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 13:23
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */

return [
    [
        'title'       => 'APP',
        'icon'        => 'fa fa-fw fa-newspaper-o',
        'url_type'    => 'module_admin',
        'url_value'   => 'api/index/config',
        'url_target'  => '_self',
        'online_hide' => 0,
        'sort'        => 100,
        'child'       => [
            /*[
                'title'       => '仪表盘',
                'icon'        => 'fa fa-fw fa-tachometer',
                'url_type'    => 'module_admin',
                'url_value'   => 'home/index/index',
                'url_target'  => '_self',
                'online_hide' => 0,
                'sort'        => 100,
            ],*/
            [
                'title'       => 'API设置',
                'icon'        => 'fa fa-fw fa-cog',
                'url_type'    => 'module_admin',
                'url_value'   => 'api/index/config',
                'url_target'  => '_self',
                'online_hide' => 0,
                'sort'        => 100,
            ],
        ]
    ]
];
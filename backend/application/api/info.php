<?php
/**
 * 模块信息
 */
return [
    // 模块名[必填]
    'name'        => 'api',
    // 模块标题[必填]
    'title'       => 'API接口',
    // 模块唯一标识[必填]，格式：模块名.开发者标识.module
    'identifier'  => 'api.iydhp.module',
    // 模块图标[选填]
    'icon'        => 'fa fa-fw fa-newspaper-o',
    // 模块描述[选填]
    'description' => 'API接口',
    // 开发者[必填]
    'author'      => '云端华平',
    // 开发者网址[选填]
    'author_url'  => 'http://www.iydhp.com',
    // 版本[必填],格式采用三段式：主版本号.次版本号.修订版本号
    'version'     => '1.0.0',
    // 模块依赖[可选]，格式[[模块名, 模块唯一标识, 依赖版本, 对比方式]]
    'need_module' => [
        ['admin', 'admin.dolphinphp.module', '1.0.0']
    ],
    // 插件依赖[可选]，格式[[插件名, 插件唯一标识, 依赖版本, 对比方式]]
    'need_plugin' => [],
    // 数据表[有数据库表时必填]
    'tables' => [

    ],
    // 原始数据库表前缀
    'database_prefix' => 'dp_',

    // 模块参数配置
    'config' => [
        ['text', 'params_splash_image_url', '启动页闪图url'],
        ['number', 'params_lock_accusation_time', '锁定评分举报次数'],
        ['switch', 'use_server_status_cache', '服务器状态是否使用缓存'],
    ],

    // 行为配置
    'action' => [
        
    ],

    // 授权配置
    'access' => [

    ],
];

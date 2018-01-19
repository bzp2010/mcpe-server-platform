<?php
use think\Route;

Route::rule('user/login','user/index/oauth');
Route::rule('user/loginCallback','user/index/oauthCallback');
Route::rule('user/logout','user/index/logout');
Route::rule('user/logoutCallback','user/index/logoutCallback');

Route::rule('user/fuzhu','user/fuzhu/index');

Route::rule('v1/api/getServerList','home/api/getServerList'); //ok
Route::rule('v1/api/getServerInfo','home/api/getServerInfo'); //ok
Route::rule('v1/api/getIndexServerList','home/api/getIndexServerList'); //ok
Route::rule('v1/api/getCategory','home/api/getCategory'); //ok

//需验证
Route::post('api/server/addFavourite','api/apps/addCollect'); //ok
Route::post('api/server/deleteFavourite','api/apps/deleteCollect'); //ok
Route::rule('api/server/listFavourite','api/apps/listCollect'); //ok
Route::rule('api/server/isFavourited', 'api/apps/isCollected');
Route::post('api/server/addComment','api/apps/addComment');
Route::post('api/server/editComment','api/apps/editComment');
Route::rule('api/server/getComment','api/apps/getComment'); //获取服务器全部评分
Route::post('api/server/accusationComment','api/apps/accusationComment');
Route::rule('api/user/getUserInfo','api/apps/getUserInfo');
Route::post('api/user/updateNickname','api/apps/updateNickname');
Route::post('api/user/updateAvatar','api/apps/updateAvatar');

//公共
Route::rule('info','api/appus/info');
Route::post('user/app/login','user/index/appLogin');
Route::rule('user/app/checkToken','api/appus/checkToken');
Route::rule('api/app/getSplashImage','api/appus/getSplashImage');
Route::rule('api/server/listComment','api/appus/listComment');
Route::rule('api/server/search','api/appus/searchServer');

Route::rule('api/cron/getServerListToCacheStatus','api/cron/getServerListToCacheStatus');
Route::rule('api/cron/uploadServerStatus','api/cron/uploadServerStatus');

//V2

Route::rule('api/getIndexServerList','home/index/getIndexServerList'); //ok
Route::rule('api/getServerInfo','home/index/getServerInfo');
Route::rule('api/getCategory','home/index/getCategory');
Route::rule('api/getServerList','home/index/getServerList'); //ok
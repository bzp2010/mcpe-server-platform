<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/16
 * Time: 1:50
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
//ini_set('display_errors', 1);
//error_reporting(E_ERROR);

//exit();

set_time_limit(120);

$position = $_SERVER['HTTP_P'] ? $_SERVER['HTTP_P'] : 'BJ';
$serverListJson = file_get_contents("https://sv.qi78.com/api/cron/getServerListToCacheStatus?position=".$position);
$serverList = json_decode($serverListJson, true);

if($serverList){
    include_once("src/MinecraftQuery.php");
    include_once("src/MinecraftQueryException.php");
    $data = array();
    foreach ($serverList as $item){
        $status = null;
        try {
            $mcQuery = new \xPaw\MinecraftQuery();
            $mcQuery->Connect($item['ip'], intval($item['port']));
            $status = $mcQuery->GetInfo();
        }catch (\xPaw\MinecraftQueryException $e){
            $data[] = array(
                'id' => $item['id'],
                'nowplayer' => 0,
                'maxplayer' => 0,
                'online' => false
            );
            continue;
        }
        $data[] = array(
            'id' => $item['id'],
            'nowplayer' => $status['numplayers'],
            'maxplayer' => $status['maxplayers'],
            'online' => true
        );
    }
    $callbackData = json_encode($data);
    $callbackStatusJson = file_get_contents("https://sv.qi78.com/api/cron/uploadServerStatus?position=".$position."&data=".$callbackData);
    $callbackStatus = json_decode($callbackStatusJson, true);
    if ($callbackStatus['status'] == 'ok'){
        //file_put_contents("log/ok".time().".txt", '0');
        echo "ok";
    }else{
        //file_put_contents("log/error".time().".txt", '0');
        echo "error";
    }
}
<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 13:22
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\home;

use app\index\controller\Home;
use app\user\model\Token;
use think\Config;
use think\Response;

class ApiBase extends Home {

    protected $checkApiToken = false;
    protected $checkSignature = false;

    protected function _initialize()
    {
        header('Access-Control-Allow-Origin: *');
        Config::set('default_return_type', 'json');
        //验签
        if ($this->checkSignature) {
            parse_str($_SERVER['QUERY_STRING'], $get);
            $post = $_POST;
            $sign = input('get.sign');
            $getTime = input('get.time');
            $time = time();
            if (($time - $getTime) > 10) {
                exit(json_encode(ajax_build_result(703, '签名时间错误')));
            }
            //参数
            if (!($sign && $getTime && $time)) {
                exit(json_encode(ajax_build_result(702, '签名参数错误')));
            }
            //签名
            $array = array_merge($get, $post);
            if (!self::checkSign($sign, $array, $getTime)) {
                exit(json_encode(ajax_build_result(701, '签名错误')));
            }
        }
        //验令
        if ($this->checkApiToken){
            $token = input('token');
            $uid = Token::getUidByToken($token);
            if (!empty($uid) && $uid > 0){
                define('TOKEN_UID', $uid);
            }elseif($uid == -2){
                exit(json_encode(ajax_build_result(705, '登录状态已失效')));
            }elseif($uid == -1){
                exit(json_encode(ajax_build_result(704, '登录状态错误')));
            }else{
                exit(json_encode(ajax_build_result(700, '系统错误')));
            }
        }
    }

    /**
     * 获取数据签名
     *
     * @param  array $param 签名数组
     * @param  string $code 安全校验码（大概是时间戳）
     * @param  string $sign_type 签名类型
     * @return string        签名字符串
     */
    private static function getSign($param, $code, $sign_type = 'MD5')
    {
        //去除数组中的空值和签名参数(sign，time)
        unset($param['sign']);
        unset($param['time']);
        //按键名升序排列数组
        ksort($param);
        //把数组所有元素，按照“参数值|参数值”的模式用“|”字符拼接成字符串
        $param_str = implode('|', $param);
        //把拼接后的字符串再与安全校验码直接连接起来
        $param_str = $param_str . '|' . $code;
        //创建签名字符串
        return self::createSign($param_str, $sign_type);
    }

    /**
     * 校验数据签名
     *
     * @param  string $sign 接口收到的签名
     * @param  array $param 签名数组
     * @param  string $code 安全校验码
     * @param  string $sign_type 签名类型
     * @return boolean true正确，false失败
     */
    private static function checkSign($sign, $param, $code, $sign_type = 'MD5')
    {
        return strtolower($sign) == self::getSign($param, $code, $sign_type);
    }

    /**
     * 创建签名字符串
     *
     * @param  string $param 需要加密的字符串
     * @param  string $type 签名类型 默认值：MD5
     * @return string 签名结果
     */
    private static function createSign($param, $type = 'MD5')
    {
        $type = strtolower($type);
        if ($type == 'md5') {
            return md5($param);
        }
    }

}
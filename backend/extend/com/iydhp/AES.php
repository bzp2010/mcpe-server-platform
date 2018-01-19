<?php
namespace com\iydhp;

class AES{
    const KEY = "625202f9149e061d";
    const IV ="5efd3f6060e20330";

    public function getIv()
    {
        return $this->iv;
    }


    public function getKey()
    {
        return $this->key;
    }

    /**
     * pkcs7补码
     * @param string $string  明文
     * @param int $blocksize Blocksize , 以 byte 为单位
     * @return String
     */
    private function addPkcs7Padding($string, $blocksize = 32) {
        $len = strlen($string); //取得字符串长度
        $pad = $blocksize - ($len % $blocksize); //取得补码的长度
        $string .= str_repeat(chr($pad), $pad); //用ASCII码为补码长度的字符， 补足最后一段
        return $string;
    }

    /**
     * 加密然后base64转码
     *
     * @param String 明文
     * @param 加密的初始向量（IV的长度必须和Blocksize一样， 且加密和解密一定要用相同的IV）
     * @param $key 密钥
     */
    function aes256cbcEncrypt($str, $iv, $key ) {
        return base64_encode(mcrypt_encrypt(MCRYPT_RIJNDAEL_256, $key, $this->addPkcs7Padding($str) , MCRYPT_MODE_CBC, $iv));
    }

    /**
     * 除去pkcs7 padding
     *
     * @param String 解密后的结果
     *
     * @return String
     */
    private function stripPkcs7Padding($string){
        $slast = ord(substr($string, -1));
        $slastc = chr($slast);
        $pcheck = substr($string, -$slast);

        if(preg_match("/$slastc{".$slast."}/", $string)){
            $string = substr($string, 0, strlen($string)-$slast);
            return $string;
        } else {
            return false;
        }
    }
    /**
     * 解密
     *
     * @param String $encryptedText 二进制的密文
     * @param String $iv 加密时候的IV
     * @param String $key 密钥
     * @return String
     */
    function aes256cbcDecrypt($encryptedText, $iv, $key) {
        $encryptedText =base64_decode($encryptedText);
        return $this->stripPkcs7Padding(mcrypt_decrypt(MCRYPT_RIJNDAEL_256, $key, $encryptedText, MCRYPT_MODE_CBC, $iv));
    }

    function aes128cbcDecrypt($encryptedText, $iv=self::IV, $key=self::KEY) {
        $encryptedText =base64_decode($encryptedText);
        return $this->stripPkcs7Padding(mcrypt_decrypt(MCRYPT_RIJNDAEL_128, $key, $encryptedText, MCRYPT_MODE_CBC, $iv));
    }

    function hexToStr($hex)//十六进制转字符串
    {
        $string="";
        for($i=0;$i<strlen($hex)-1;$i+=2)
            $string.=chr(hexdec($hex[$i].$hex[$i+1]));
        return  $string;
    }
    function strToHex($string)//字符串转十六进制
    {
        $hex="";
        $tmp="";
        for($i=0;$i<strlen($string);$i++)
        {
            $tmp = dechex(ord($string[$i]));
            $hex.= strlen($tmp) == 1 ? "0".$tmp : $tmp;
        }
        $hex=strtoupper($hex);
        return $hex;
    }
    function aes128cbcHexDecrypt($encryptedText, $iv=self::IV, $key=self::KEY) {
        $str = $this->hexToStr($encryptedText);
        return $this->stripPkcs7Padding(mcrypt_decrypt(MCRYPT_RIJNDAEL_128, $key, $str, MCRYPT_MODE_CBC, $iv));
    }

    function aes128cbcEncrypt($str, $iv=self::IV, $key=self::KEY ) {
        // $this->addPkcs7Padding($str,16)
        $base = (mcrypt_encrypt(MCRYPT_RIJNDAEL_128, $key,$this->addPkcs7Padding($str,16) , MCRYPT_MODE_CBC, $iv));
        return $this->strToHex($base);
    }

    function aes128cbcBase64Encrypt($str, $iv=self::IV, $key=self::KEY ) {
        $base = base64_encode(mcrypt_encrypt(MCRYPT_RIJNDAEL_128, $key,$this->addPkcs7Padding($str,16) , MCRYPT_MODE_CBC, $iv));
        return $base;
    }

}
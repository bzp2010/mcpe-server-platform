<?php
/**
 * Project: kkmo_sv.
 * Date: 2017/8/6
 * Time: 13:22
 * Copyright (c) 2014-2017 http://iydhp.com All rights reserved.
 */
namespace app\api\home;

use app\api\model\Collect;
use app\api\model\Comment;
use app\api\model\CommentAccusation;
use app\home\model\Server;
use app\home\model\ServerStatus;
use app\index\controller\Home;
use app\user\model\User;
use think\Config;
use think\Hook;
use think\Response;
use app\admin\model\Attachment as AttachmentModel;

class Apps extends ApiBase {

    //你需要修改这里的CDN地址

    protected $checkApiToken = true;

    protected function _initialize()
    {
        parent::_initialize();
    }

    /**
     * 获取用户信息
     * @return array
     */
    public function getUserInfo(){
        $user = User::get(TOKEN_UID);
        unset($user['openid']);
        unset($user['username']);
        unset($user['password']);
        unset($user['email']);
        unset($user['email_bind']);
        unset($user['mobile']);
        unset($user['mobile_bind']);
        unset($user['money']);
        unset($user['group']);
        unset($user['signup_ip']);
        unset($user['create_time']);
        unset($user['update_time']);
        unset($user['last_login_time']);
        unset($user['last_login_ip']);
        unset($user['sort']);
        unset($user['status']);
        unset($user['role']);
        $path = get_file_path($user['avatar']);
        if (count(explode('://', $path)) > 1){
            $user['avatar'] = $path;
        }else{
            $user['avatar'] = 'https://svcdn.qi78.com'.$path;
        }
        return ajax_build_result(200, '查询成功', $user);
    }

    public function updateNickname($nickname = ''){
        if($nickname == '') return ajax_build_result(601, '新昵称为空');
        $user = User::getUserByUid(TOKEN_UID, 'id,nickname');
        if ($user){
            $user->nickname = $nickname;
            $result = $user->save();
            if ($result > 0){
                return ajax_build_result(200, '修改昵称成功');
            }else{
                return ajax_build_result(602, '修改昵称失败');
            }
        }else{
            return ajax_build_result(603, '用户不存在');
        }
    }

    public function updateAvatar(){
        // 临时取消执行时间限制
        set_time_limit(0);
        $dir = 'images';
        $module = 'home';

        // 附件大小限制
        $size_limit = $dir == 'images' ? config('upload_image_size') : config('upload_file_size');
        $size_limit = $size_limit * 1024;
        // 附件类型限制
        $ext_limit = $dir == 'images' ? config('upload_image_ext') : config('upload_file_ext');
        $ext_limit = $ext_limit != '' ? parse_attr($ext_limit) : '';

        // 获取附件数据
        $file = $this->request->file('file');

        // 判断附件是否已存在
        if ($file_exists = AttachmentModel::get(['md5' => $file->hash('md5')])) { //成功
            if ($file_exists['driver'] == 'local') {
                $file_path = PUBLIC_PATH. $file_exists['path'];
            } else {
                $file_path = $file_exists['path'];
            }
            if (User::updateUser(['id' => TOKEN_UID],['avatar' => $file_exists['id']])){
                return ajax_build_result(200, '修改头像成功', ['url' => $file_path]);
            }else{
                return ajax_build_result(602, '修改头像失败');
            }
        }

        // 判断附件大小是否超过限制
        if ($size_limit > 0 && ($file->getInfo('size') > $size_limit)) { //附件过大
            return ajax_build_result(603, '文件过大');
        }

        // 判断附件格式是否符合
        $file_name = $file->getInfo('name');
        $file_ext  = strtolower(substr($file_name, strrpos($file_name, '.')+1));
        $error_msg = '';
        if ($ext_limit == '') {
            $error_msg = '获取文件信息失败！';
        }
        if ($file->getMime() == 'text/x-php' || $file->getMime() == 'text/html') {
            $error_msg = '禁止上传非法文件！';
        }
        if (!in_array($file_ext, $ext_limit)) {
            $error_msg = '附件类型不正确！';
        }
        if ($error_msg != '') { //各种错误
            return ajax_build_result(601, $error_msg);
        }

        // 附件上传钩子，用于第三方文件上传扩展
        if (config('upload_driver') != 'local') {
            $hook_result = Hook::listen('upload_attachment', $file, ['from' => 'api', 'module' => $module, 'uid' => TOKEN_UID], true);
            if (false !== $hook_result) {
                //var_dump($hook_result);
                if (User::updateUser(['id' => TOKEN_UID],['avatar' => $hook_result['id']])){
                    return ajax_build_result(200, '修改头像成功', ['url' => $hook_result['path']]);
                }else{
                    return ajax_build_result(602, '修改头像失败');
                }
            }
        }


    }

    /**
     * 收藏服务器或领域
     * @param string $type 类型（服务器、领域）（server、realms）
     * @param int $id 服务或领域id
     * @return array
     */
    public function addCollect($type = 'server',$id = 0){
        if ($id != 0){
            $exist = Collect::isCollected(TOKEN_UID, $type, $id);
            if ($exist){
                return ajax_build_result(603,'您已经收藏过了');
            }
            if ($result = Collect::addCollect(TOKEN_UID, $type, $id)){
                $collect = Collect::getCollectIdByTypeAndServerIdAndUid(TOKEN_UID, $type, $id);
                return ajax_build_result(200,'收藏成功', ['id' => $collect['id']]);
            }else{
                return ajax_build_result(602,'收藏失败');
            }
        }else{
            return ajax_build_result(601,'操作错误，请重试');
        }
    }

    /**
     * 取消收藏
     * @param int $id
     * @return array
     */
    public function deleteCollect($id = 0){
        if ($id != 0){
            if (Collect::deleteCollectById($id)){
                return ajax_build_result(200,'取消收藏成功');
            }else{
                return ajax_build_result(602,'取消收藏失败');
            }
        }else{
            return ajax_build_result(601,'操作错误，请重试');
        }
    }

    /**
     * 获取收藏
     * @return array
     */
    public function listCollect(){
        $collects = Collect::getCollectsByUid(10,TOKEN_UID);
        $collects = $collects ? $collects : [];
        if ($collects && $collects != []){
            for ($i = 0;$i < count($collects);$i++){
                $item = $collects[$i];
                if ($item['type'] == 'server'){
                    $server = Server::getServerById($item['serverid'],'id,category,name,manager,version,background,ip,port');
                    if (empty($server['name'])){
                        $this->deleteCollect($item['id']);
                        unset($collects[$i]);
                        continue;
                    }
                    if ($server['ip'] && $server['port']){
                        if (module_config('api.use_server_status_cache') == 'on'){
                            $status = ServerStatus::getStatus($server['id']);
                            $status['numplayers'] = $status['nowplayer'];
                            $status['maxplayers'] = $status['maxplayer'];
                        }else{
                            try {
                                $mcQuery = new \xPaw\MinecraftQuery();
                                $mcQuery->Connect($server['ip'], intval($server['port']));
                                $status = $mcQuery->GetInfo();
                            }catch (\xPaw\MinecraftQueryException $e){}
                        }
                    }
                    if (empty($status)) $status = array('numplayers'=>0,'maxplayers'=>0);
                    $server['nowPlayer'] = $status['numplayers'];
                    $server['maxPlayer'] = $status['maxplayers'];
                    $item['server'] = $server;
                }
            }
        }
        return ajax_build_result(200,'查询成功', $collects);
    }

    /**
     * 是否已收藏
     * @param string $type
     * @param int $id
     * @return bool
     */
    public function isCollected($type = 'server',$id = 0){
        if (Collect::isCollected(TOKEN_UID, 'server', $id)){
            $collect = Collect::getCollectIdByTypeAndServerIdAndUid(TOKEN_UID, $type, $id);
            return ajax_build_result(200,'查询成功', ['favourited' => true,'id' => $collect['id'],'time' => $collect['time']]);
        }else{
            return ajax_build_result(200,'查询成功', ['favourited' => false]);
        }
    }

    /**
     * 发布评价
     * @param int $score
     * @param string $text
     * @return array
     */
    public function addComment($type = 'server', $serverid = -1,$score = -1, $text = ''){
        if ($serverid <= 0){
            return ajax_build_result(601,'操作错误，请重试');
        }

        $user = User::getUserByUid(TOKEN_UID, 'nickname');
        $nickname = $user['nickname'];

        if (strlen($nickname) == 16 && preg_match("/^[a-z0-9]+$/i", $nickname)){
            return ajax_build_result(605,'请先修改昵称');
        }

        $comment = Comment::getCommentByTypeAndServerIdAndUid($type, $serverid, TOKEN_UID);
        if (!$comment && $score >= 0 && $text != ''){
            if ($result = Comment::addComment(TOKEN_UID, $type, $serverid, $score, $text)) {
                return ajax_build_result(200,'评分成功');
            }else{
                return ajax_build_result(604,'评分失败');
            }
        }elseif ($comment){
            return ajax_build_result(603,'禁止重复评价');
        }else{
            return ajax_build_result(602,'请输入正确的评分内容');
        }
    }

    /**
     * 发布评价
     * @param int $score
     * @param string $text
     * @return array
     */
    public function editComment($type = 'server', $serverid = -1,$score = -1, $text = ''){
        if ($serverid <= 0){
            return ajax_build_result(601,'操作错误，请重试');
        }

        $user = User::getUserByUid(TOKEN_UID, 'nickname');
        $nickname = $user['nickname'];

        if (strlen($nickname) == 16 && preg_match("/^[a-z0-9]+$/i", $nickname)){
            return ajax_build_result(605,'请先修改昵称');
        }

        $comment = Comment::getCommentByTypeAndServerIdAndUid($type, $serverid, TOKEN_UID);
        if ($comment && $score >= 0 && $text != ''){
            if ($result = Comment::editComment(TOKEN_UID, $type, $serverid, $score, $text)) {
                return ajax_build_result(200,'修改成功');
            }else{
                return ajax_build_result(604,'修改失败');
            }
        }else{
            return ajax_build_result(602,'请输入正确的评分内容');
        }
    }

    /**
     * 获取用户名在某服务器的评价
     * @param string $type
     * @param int $serverid
     * @return array
     */
    public function getComment($type = 'server', $serverid = -1)
    {
        if ($comment = Comment::getCommentByTypeAndServerIdAndUid($type, $serverid, TOKEN_UID)) {
            unset($comment['uid']);
            unset($comment['type']);
            unset($comment['serverid']);
            return ajax_build_result(200,'查询成功',$comment);
        }else{
            return ajax_build_result(200,'查询成功');
        }
    }

    /**
     * @param $id 举报
     */
    public function accusationComment($id){
        $result = CommentAccusation::addAccusation(TOKEN_UID, $id);
        switch ($result){
            case 1:
                return ajax_build_result(200,'举报成功');
                break;
            case 0:
                return ajax_build_result(601,'举报失败');
                break;
            case -1:
                return ajax_build_result(602,'禁止重复举报');
                break;
        }
    }

}
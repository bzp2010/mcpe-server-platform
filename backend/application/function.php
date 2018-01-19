<?php

function ajax_build_result($id = 200, $msg = 'success', $data = null)
{
    $result = array(
        'status' => $id,
        'msg' => $msg,
        'data' => $data
    );
    if (!$data) unset($result['data']);
    return $result;
}
<?php
namespace com\iydhp;

use Exception;

class MinecraftQuery {
  public static $MAGIC_PREFIX = "\xFE\xFD";
  const PACKET_TYPE_CHALLENGE = 9;
  const PACKET_TYPE_QUERY = 0;
  public static $HUMAN_READABLE_NAMES = array(
      'game_id' => "Game Name",
      'gametype' => "Game Type",
      'motd' => "Message of the Day",
      'hostname' => "Server Address",
      'hostport' => "Server Port",
      'map' => "Main World Name",
      'maxplayers' => "Maximum Players",
      'numplayers' => "Players Online",
      'players' => "List of Players",
      'plugins' => "List of Plugins",
      'raw_plugins' => "Raw Plugin Info",
      'software' => "Server Software",
      'version' => "Game Version",
    );
  private $host;
  private $hostname;
  private $port;
  private $timeout;
  private $id;
  private $retries = 0;
  private $max_retries;
  private $id_packed;
  private $challenge;
  private $challenge_packed;
  private $socket;
  public function __construct($host, $port, $timeout=10, $retries=2, $id=0) {
    $this->host = gethostbyname($host);
    $this->hostname = $host;
    $this->port = $port;
    $this->timeout = array('sec' => $timeout, 'usec' => 0);
    $this->id_packed = pack('N', $id);
    $this->challenge_packed = pack('N', 0);
    $this->max_retries = $retries;
    $this->socket = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
    socket_set_option($this->socket, SOL_SOCKET, SO_RCVTIMEO, $this->timeout);
    socket_set_option($this->socket, SOL_SOCKET, SO_SNDTIMEO, $this->timeout);
  }
  private function sendRaw($data) {
    $data = self::$MAGIC_PREFIX . $data;
    $send = socket_sendto($this->socket, $data, strlen($data), 0, $this->host, $this->port);
  }
  private function sendPacket($type, $data='') {
    $this->sendRaw(pack('C', $type) . $this->id_packed . $this->challenge_packed . $data);
  }
  private function readPacket() {
    $ip2long = ip2long($this->host);
    $len = @socket_recvfrom($this->socket, $buff, 1460, 0, $ip2long, $this->port);
    if (!isset($len) || $len === FALSE) {
      throw new Exception();
    }
    $type = unpack('C', substr($buff, 0, 1));
    $id = unpack('N', substr($buff, 1, 4));
    return array($type, $id, substr($buff, 5));
  }
  private function handshake($bypass_retries=FALSE) {
    $this->sendPacket(self::PACKET_TYPE_CHALLENGE);
    unset($this->challenge);
    try {
      list($type, $id, $buff) = $this->readPacket();
    }
    catch(Exception $e) {
      if (!$bypass_retries) $this->retries++;
      if ($this->retries < $this->max_retries) {
        $this->handshake($bypass_retries);
      }
      return;
    }
    $this->challenge = (int) $buff;
    $this->challenge_packed = pack('N', $buff);
  }
  public function getStatus() {
    if (empty($this->challenge)) {
      $this->handshake();
    }
    $this->sendPacket(self::PACKET_TYPE_QUERY);
    try {
      list($type, $id, $buff) = $this->readPacket();
    }
    catch(Exception $e) {
      if ($this->retries < $this->max_retries) {
        $this->handshake();
        return $this->getStatus();
      }
      else {
        return NULL;
      }
    }
    $data = array();
    $buff = explode("\x00", $buff, 6);
    $data['motd'] = array_shift($buff);
    $data['gametype'] = array_shift($buff);
    $data['map'] = array_shift($buff);
    $data['numplayers'] = (int) array_shift($buff);
    $data['maxplayers'] = (int) array_shift($buff);
    $buff = $buff[0];
	$t = unpack('v', substr($buff, 0, 2));
    $data['hostport'] = array_shift($t);
    $data['hostname'] = substr($buff, 2);
    if ($data['maxplayers'] && $data['maxplayers'] > 0) {
        $data['online'] = true;
    }else{
        $data['online'] = false;
    }
    return $data;
  }
  public function getRules() {
    if (empty($this->challenge)) {
      $this->handshake();
    }
    $this->sendPacket(self::PACKET_TYPE_QUERY, $this->id_packed);
    try {
      list($type, $id, $buff) = $this->readPacket();
    }
    catch(Exception $e) {
      if ($this->retries < $this->max_retries) {
        $this->handshake();
        return $this->getStatus();
      }
      else {
        return NULL;
      }
    }
    $data = array();
    $buff = substr($buff, 11);
    list($items, $players) = explode("\x00\x00\x01player_\x00\x00", $buff);
    if (strpos($items, 'hostname') === 0) {
      $items = 'motd'.substr($items, 8);
    }
    $items = explode("\x00", $items);
    foreach ($items as $value) {
      if (!isset($key)) {
        $key = $value;
      }
      else {
        if (in_array($key, array('numplayers', 'maxplayers', 'hostport'))) {
          $data[$key] = (int) $value;
        }
        else {
          $data[$key] = $value;
        }
        unset($key);
      }
    }
    $players = substr($players, 0, -2);
    if (!empty($players)) {
      $data['players'] = explode("\x00", $players);
    }
    else {
      $data['players'] = array();
    }
    if (!empty($data['plugins'])) {
      $data['plugins_raw'] = $data['plugins'];
      list($data['software'], $data['plugins']) = $this->parsePlugins($data['plugins']);
    }
    if (empty($data['hostname'])) {
      $data['hostname'] = $this->hostname;
    }
    return $data;
  }
  private function parsePlugins($raw) {
    $parts = explode(':', $raw);
    $server = trim($parts[0]);
    $plugins = array();
    if (count($parts) == 2) {
      $tmp_plugins = explode(';', $parts[1]);
      $tmp_plugins = array_map('trim', $tmp_plugins);
      foreach ($tmp_plugins as $value) {
        $p = explode(' ', $value, 2);
        $plugins[] = array('name' => $p[0], 'version' => $p[1]);
      }
    }
    return array($server, $plugins);
  }
}
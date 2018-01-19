<?php

namespace xPaw;

class MinecraftQuery
{
	/*
	 * Class written by xPaw
	 *
	 * Website: http://xpaw.me
	 * GitHub: https://github.com/xPaw/PHP-Minecraft-Query
	 */

	const STATISTIC = 0x00;
	const HANDSHAKE = 0x09;

	private $Socket;
	private $Players;
	private $Info;

	public function Connect( $Ip, $Port = 25565, $Timeout = 3, $ResolveSRV = true )
	{
		if( !is_int( $Timeout ) || $Timeout < 0 )
		{
			throw new \InvalidArgumentException( 'Timeout must be an integer.' );
		}

		if( $ResolveSRV )
		{
			$this->ResolveSRV( $Ip, $Port );
		}

		$this->Socket = @FSockOpen( 'udp://' . $Ip, (int)$Port, $ErrNo, $ErrStr, $Timeout );

		if( $ErrNo || $this->Socket === false )
		{
			throw new MinecraftQueryException( 'Could not create socket: ' . $ErrStr );
		}

		Stream_Set_Timeout( $this->Socket, $Timeout );
		Stream_Set_Blocking( $this->Socket, true );

		try
		{
			$Challenge = $this->GetChallenge( );

			$this->GetStatus( $Challenge );
		}
		// We catch this because we want to close the socket, not very elegant
		catch( MinecraftQueryException $e )
		{
			FClose( $this->Socket );

			throw new MinecraftQueryException( $e->getMessage( ) );
		}

		FClose( $this->Socket );
	}

	public function GetInfo( )
	{
		return isset( $this->Info ) ? $this->Info : false;
	}

	public function GetPlayers( )
	{
		return isset( $this->Players ) ? $this->Players : false;
	}

	private function GetChallenge( )
	{
		$Data = $this->WriteData( self :: HANDSHAKE );

		if( $Data === false )
		{
			throw new MinecraftQueryException( 'Failed to receive challenge.' );
		}

		return Pack( 'N', $Data );
	}

	private function GetStatus( $Challenge )
	{
		$Data = $this->WriteData( self :: STATISTIC, $Challenge . Pack( 'c*', 0x00, 0x00, 0x00, 0x00 ) );

		if( !$Data )
		{
			throw new MinecraftQueryException( 'Failed to receive status.' );
		}

		$Last = '';
		$Info = Array( );

		$Data    = SubStr( $Data, 11 ); // splitnum + 2 int
		$Data    = Explode( "\x00\x00\x01player_\x00\x00", $Data );

		if( Count( $Data ) !== 2 )
		{
			throw new MinecraftQueryException( 'Failed to parse server\'s response.' );
		}

		$Players = SubStr( $Data[ 1 ], 0, -2 );
		$Data    = Explode( "\x00", $Data[ 0 ] );

		// Array with known keys in order to validate the result
		// It can happen that server sends custom strings containing bad things (who can know!)
		$Keys = Array(
			'hostname'   => 'hostname',
			'gametype'   => 'gametype',
			'version'    => 'version',
			'plugins'    => 'plugins',
			'map'        => 'map',
			'numplayers' => 'numplayers',
			'maxplayers' => 'maxplayers',
			'hostport'   => 'hostport',
			'hostip'     => 'hostip',
			'game_id'    => 'gamename'
		);

		foreach( $Data as $Key => $Value )
		{
			if( ~$Key & 1 )
			{
				if( !Array_Key_Exists( $Value, $Keys ) )
				{
					$Last = false;
					continue;
				}

				$Last = $Keys[ $Value ];
				$Info[ $Last ] = '';
			}
			else if( $Last != false )
			{
				$Info[ $Last ] = $Value;
			}
		}

		// Ints
		$Info[ 'numplayers' ]    = IntVal( $Info[ 'numplayers' ] );
		$Info[ 'maxplayers' ] = IntVal( $Info[ 'maxplayers' ] );
		$Info[ 'hostport' ]   = IntVal( $Info[ 'hostport' ] );

		if ($Info[ 'maxplayers' ] && $Info[ 'maxplayers' ] > 0){
            $Info[ 'online' ] = true;
        }

		// Parse "plugins", if any
		if( $Info[ 'plugins' ] )
		{
			$Data = Explode( ": ", $Info[ 'plugins' ], 2 );

			$Info[ 'rawplugins' ] = $Info[ 'plugins' ];
			$Info[ 'software' ]   = $Data[ 0 ];

			if( Count( $Data ) == 2 )
			{
				$Info[ 'plugins' ] = Explode( "; ", $Data[ 1 ] );
			}
		}
		else
		{
			$Info[ 'software' ] = 'Vanilla';
		}

		$this->Info = $Info;

		if( empty( $Players ) )
		{
			$this->Players = null;
		}
		else
		{
			$this->Players = Explode( "\x00", $Players );
		}
	}

	private function WriteData( $Command, $Append = "" )
	{
		$Command = Pack( 'c*', 0xFE, 0xFD, $Command, 0x01, 0x02, 0x03, 0x04 ) . $Append;
		$Length  = StrLen( $Command );

		if( $Length !== FWrite( $this->Socket, $Command, $Length ) )
		{
			throw new MinecraftQueryException( "Failed to write on socket." );
		}

		$Data = FRead( $this->Socket, 4096 );

		if( $Data === false )
		{
			throw new MinecraftQueryException( "Failed to read from socket." );
		}

		if( StrLen( $Data ) < 5 || $Data[ 0 ] != $Command[ 2 ] )
		{
			return false;
		}

		return SubStr( $Data, 5 );
	}

	private function ResolveSRV( &$Address, &$Port )
	{
		if( ip2long( $Address ) !== false )
		{
			return;
		}

		$Record = dns_get_record( '_minecraft._tcp.' . $Address, DNS_SRV );

		if( empty( $Record ) )
		{
			return;
		}

		if( isset( $Record[ 0 ][ 'target' ] ) )
		{
			$Address = $Record[ 0 ][ 'target' ];
		}
	}
}

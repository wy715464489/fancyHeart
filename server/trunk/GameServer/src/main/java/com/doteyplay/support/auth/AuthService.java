package com.doteyplay.support.auth;

import com.doteyplay.game.config.ServerConfig;
import com.doteyplay.luna.client.AsynchronismClientManager;
import com.doteyplay.luna.client.ConnectionInfo;
import com.doteyplay.luna.common.action.IBaseAction;
import com.doteyplay.luna.common.message.EncoderMessage;
import com.doteyplay.support.ISupportService;

public class AuthService implements ISupportService
{

	private static final long MAX_TIME_OUT = 5 * 1000;
	/**
	 * 认证服务器的信息
	 */
	private static final ConnectionInfo CONNECTION_INFO = new ConnectionInfo(
			ServerConfig.AUTH_SERVER_IP, ServerConfig.AUTH_SERVER_PORT, MAX_TIME_OUT,
			MAX_TIME_OUT);

	/**
	 * 单例对象
	 */
	public static AuthService instance = new AuthService();

	private AsynchronismClientManager manager = new AsynchronismClientManager();

	/**
	 * 初始化同步操作的客户端管理的Manager
	 */
	private AuthService()
	{
		manager.initial(CONNECTION_INFO, AuthController.getInstance());
		
		AuthHandler.getInstance().init(this);
	}

	/**
	 * 获得单例的方式
	 * 
	 * @return
	 */
	public static AuthService getInstance()
	{
		if (instance == null)
			instance = new AuthService();
		return instance;
	}

	/**
	 * 同步调用消息的方法
	 * 
	 * @param encoderMessage
	 * @return
	 */
	public void invoke(EncoderMessage encoderMessage)
	{
		if (encoderMessage == null)
			return;
		
		manager.invoke(encoderMessage);
	}
}

package com.doteyplay.core.bhns.net;

import org.apache.mina.core.session.IoSession;

import com.doteyplay.luna.common.action.ActionController;
import com.doteyplay.luna.common.action.IBaseAction;
import com.doteyplay.luna.common.message.DecoderMessage;
/**
 * 代理服务服务器
 * @author 
 * 
 */
public class ServerActionManager implements ActionController
{

	private static ServerActionManager instance = new ServerActionManager();

	public static ServerActionManager getInstance()
	{
		return instance;
	}

	@Override
	public IBaseAction getAction(DecoderMessage arg0)
	{
		return DefaultServerAction.getInstance();
	}

	@Override
	public void sessionClose(IoSession arg0)
	{
	}

	@Override
	public void sessionOpen(IoSession session)
	{
		
	}

}

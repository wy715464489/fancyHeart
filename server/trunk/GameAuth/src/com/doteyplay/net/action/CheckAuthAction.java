package com.doteyplay.net.action;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.doteyplay.bean.UserBean;
import com.doteyplay.luna.common.action.BaseAction;
import com.doteyplay.luna.common.message.DecoderMessage;
import com.doteyplay.luna.common.message.EncoderMessage;
import com.doteyplay.manager.AuthManager;
import com.doteyplay.manager.NosqlCached;
import com.doteyplay.net.MessageCommands;
import com.google.gson.Gson;

public class CheckAuthAction extends BaseAction
{
	private static final Logger logger = Logger.getLogger(CheckAuthAction.class
			.getName());

	private Gson gson = new Gson();

	private static CheckAuthAction instance = new CheckAuthAction();
	public static CheckAuthAction getInstance()
	{
		return instance;
	}

	@Override
	public void doAction(long arg0)
	{
	}

	@Override
	public void doAction(IoSession session, DecoderMessage dMessage)
	{
		long sessionId = dMessage.getLong();
		String account = dMessage.getString();
		String sessionKey = dMessage.getString();
		int areaId = dMessage.getInt();
		
		UserBean bean = AuthManager.getInstance().checkAuth(account, sessionKey,areaId);
		
		EncoderMessage eMessage = new EncoderMessage((short) MessageCommands.CHECK_AUTH, false);
		eMessage.putLong(sessionId);
		if(bean == null)
			eMessage.putBoolean(false);
		else
		{
			eMessage.putBoolean(true);
			eMessage.putString(gson.toJson(bean));
		}
			
		session.write(eMessage);
	}

}
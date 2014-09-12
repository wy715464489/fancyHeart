package com.doteyplay.support.auth.action;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.doteyplay.core.bhns.BOServiceManager;
import com.doteyplay.game.domain.gamebean.UserBean;
import com.doteyplay.game.service.bo.virtualworld.IVirtualWorldService;
import com.doteyplay.luna.common.action.IBaseAction;
import com.doteyplay.luna.common.message.DecoderMessage;
import com.google.gson.Gson;

public class CheckAuthAction implements IBaseAction
{

	private static Logger logger = Logger.getLogger(CheckAuthAction.class
			.getName());

	private Gson gson = new Gson();

	private static CheckAuthAction action = new CheckAuthAction();

	private CheckAuthAction()
	{

	}

	public static synchronized CheckAuthAction getInstance()
	{
		if (action == null)
			action = new CheckAuthAction();
		return action;
	}

	@Override
	public void doAction(IoSession session, DecoderMessage dMessage)
	{
		long sessionId = dMessage.getLong();
		boolean success = dMessage.getBoolean();

		UserBean userBean = null;
		IVirtualWorldService virtualWorldService = BOServiceManager
				.findDefaultService(IVirtualWorldService.PORTAL_ID);
		if (success)
			userBean = gson.fromJson(dMessage.getString(), UserBean.class);

		virtualWorldService.loginResult(success, userBean, sessionId);
	}
}

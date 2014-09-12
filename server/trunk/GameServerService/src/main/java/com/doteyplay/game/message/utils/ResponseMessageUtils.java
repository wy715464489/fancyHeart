package com.doteyplay.game.message.utils;

import com.doteyplay.core.bhns.BOServiceManager;
import com.doteyplay.core.bhns.gateway.IGateWayService;
import com.doteyplay.game.domain.role.Role;
import com.doteyplay.game.message.common.CommonResponseMessage;

/**
 * @className:ResponseMessageUtils.java
 * @classDescription:
 * @author:Tom.Zheng
 * @createTime:2014��7��18�� ����6:14:14
 */
public class ResponseMessageUtils {

	/**
	 * @param messageId
	 * @param state
	 * @param roleId
	 */
	public static  void sendResponseMessage(int messageId,int state,long roleId){
		CommonResponseMessage message = new CommonResponseMessage();
		message.setMessageId(messageId);
		message.setState(state);
		
		IGateWayService gameGateWayService = BOServiceManager.findService(IGateWayService.PORTAL_ID, roleId);
		gameGateWayService.sendMessage(message);
		
	}
	
	
}

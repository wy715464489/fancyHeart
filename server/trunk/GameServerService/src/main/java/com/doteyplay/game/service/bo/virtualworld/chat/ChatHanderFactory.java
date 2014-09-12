package com.doteyplay.game.service.bo.virtualworld.chat;

import com.doteyplay.game.constants.chat.ChatConstant;
import com.doteyplay.game.service.bo.virtualworld.chat.handle.EnergyHandlerImpl;
import com.doteyplay.game.service.bo.virtualworld.chat.handle.ExpHandlerImpl;
import com.doteyplay.game.service.bo.virtualworld.chat.handle.GoodsHandlerImpl;
import com.doteyplay.game.service.bo.virtualworld.chat.handle.IChatHandler;
import com.doteyplay.game.service.bo.virtualworld.chat.handle.MoneyHandlerImpl;
import com.doteyplay.game.service.bo.virtualworld.chat.handle.PetHandlerImpl;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.EnergyPatameterObject;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.ExpPatameterObject;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.GoodsPatameterObject;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.IPatameterObject;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.MoneyPatameterObject;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.PetPatameterObject;

/**
 * @className:GMProcessFactory.java
 * @classDescription:
 * @author:Tom.Zheng
 * @createTime:2014��7��10�� ����11:34:22
 */
public class ChatHanderFactory {

	private static ChatHanderFactory instance = new ChatHanderFactory();

	private ChatHanderFactory() {
	}

	public static ChatHanderFactory getInstance() {
		return instance;
	}

	public IChatHandler<?> getGMProcess(ChatConstant chatConstant) {

		switch (chatConstant) {
		case GOODS_SUFFIX:
			return new GoodsHandlerImpl();
		case MONEY_SUFFIX:
			return new MoneyHandlerImpl();
		case EXP_SUFFIX:
			return new ExpHandlerImpl();
		case ENERGY_SUFFIX:
			return new EnergyHandlerImpl();
		case PET_SUFFIX:
			return new PetHandlerImpl();
		default:
			break;
		}
		return null;
	}
	
	public IPatameterObject getPatameterObject(ChatConstant chatConstant){
		switch (chatConstant) {
		case GOODS_SUFFIX:
			return new GoodsPatameterObject();
		case MONEY_SUFFIX:
			return new MoneyPatameterObject();
		case EXP_SUFFIX:
			return new ExpPatameterObject();
		case ENERGY_SUFFIX:
			return new EnergyPatameterObject();
		case PET_SUFFIX:
			return new PetPatameterObject();
		default:
			break;
		}
		return null;
	}

}

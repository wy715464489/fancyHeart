package com.doteyplay.game;

public enum MessageCommands
{

	//服务器内网间交互消息，用来转发数据块
	SERVER_TRANSFER_MESSAGE(0, BOConst.BO_WORLD,false)
	//心跳
	,ACK_MESSAGE(1, BOConst.BO_WORLD,true)
	//加入游戏
	,LOGIN_GAME_MESSAGE(2, BOConst.BO_WORLD,false)
	//通用提示信息
	,COMMON_TIPS_MESSAGE(3, BOConst.BO_WORLD, true)
	//显示关卡及节点详情
	,SHOW_TOLLGATE_DETAIL_MESSAGE(4, BOConst.BO_TOLLGATE,true)
	//副本关卡
	,SEND_TOLLGATE_CHANGE_MESSAGE(5, BOConst.BO_TOLLGATE,true)
	//通用属性更新消息.
	,COMMON_PROP_UPDATE_MESSAGE(6, BOConst.BO_ROLE,true)
	//通用物品更新消息.
	,COMMON_ITEM_UPDATE_MESSAGE(7, BOConst.BO_ITEM,true)
	//聊天信息.
	,CHAT_MESSAGE(8, BOConst.BO_WORLD,true)
	//进入结点的消息.
	,ENTER_BATTLE_MESSAGE(9,BOConst.BO_TOLLGATE,true)
	//战斗结果接收消息
	,BATTLE_RESULT_MESSAGE(10,BOConst.BO_TOLLGATE,true)
	//公共响应信息.
	,COMMONS_RESPONSE_MESSAGE(11,BOConst.BO_WORLD,true)
	 //副本节点开放变化
	,SEND_NODE_CHANGE_MESSAGE(12, BOConst.BO_TOLLGATE,true)
	
	 //卖物品
	,ITEM_SELL_MESSAGE(13, BOConst.BO_ITEM,true)
	//更新组编辑内容
	,GROUP_UPDATE_MESSAGE(14,BOConst.BO_TOLLGATE,true)
	//宠物的添加和删除
	,PET_ADD_REMOVE_MESSAGE(15,BOConst.BO_ROLE,true)
	//物品合成
	,ITEM_CREATE_MESSAGE(16,BOConst.BO_ITEM,true)
	//宠物召唤
	,PET_CALL_MESSAGE(17,BOConst.BO_ROLE,true)
		//穿装备
	,OUTFIT_INSTALL_MESSAGE(18,BOConst.BO_ITEM,true)
	//升星
	,PET_STAR_UPGRADE_MESSAGE(19,BOConst.BO_ROLE,true)
	//升階
	,PET_QUALITY_UPGRADE_MESSAGE(20,BOConst.BO_ROLE,true)
	//变异
	,PET_GOLD_QUALITY_UPGRADE_MESSAGE(21,BOConst.BO_ROLE,true)
	,
    ;

	public static final int MESSAGE_NUM = MessageCommands.values().length;
	
	public final int COMMAND_ID;
	//消息归属的服务id
	public final int BO_ID;
	
	public final boolean IS_NEED_AUTH;
	
	private MessageCommands(int commandId, int boId, boolean isNeedAuth)
	{
		COMMAND_ID = commandId;
		BO_ID = boId;
		IS_NEED_AUTH = isNeedAuth;
	}
	
	public static String getMessageCommandName(int commandId)
	{
		return values()[commandId].name();
	}
	
	public static int getBoIdByCommandId(int commandId)
	{
		return values()[commandId].BO_ID;
	}
}
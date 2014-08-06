package com.doteyplay.game;

public enum MessageCommands
{

	//�����������佻����Ϣ������ת�����ݿ�
	SERVER_TRANSFER_MESSAGE(0, BOConst.BO_WORLD,false)
	//����
	,ACK_MESSAGE(1, BOConst.BO_WORLD,true)
	//������Ϸ
	,LOGIN_GAME_MESSAGE(2, BOConst.BO_WORLD,false)
	//ͨ����ʾ��Ϣ
	,COMMON_TIPS_MESSAGE(3, BOConst.BO_WORLD, true)
	//��ʾ�ؿ����ڵ�����
	,SHOW_TOLLGATE_DETAIL_MESSAGE(4, BOConst.BO_TOLLGATE,true)
	//�����ؿ�
	,SEND_TOLLGATE_CHANGE_MESSAGE(5, BOConst.BO_TOLLGATE,true)
	//ͨ�����Ը�����Ϣ.
	,COMMON_PROP_UPDATE_MESSAGE(6, BOConst.BO_ROLE,true)
	//ͨ����Ʒ������Ϣ.
	,COMMON_ITEM_UPDATE_MESSAGE(7, BOConst.BO_ITEM,true)
	//������Ϣ.
	,CHAT_MESSAGE(8, BOConst.BO_WORLD,true)
	//���������Ϣ.
	,ENTER_BATTLE_MESSAGE(9,BOConst.BO_TOLLGATE,true)
	//ս�����������Ϣ
	,BATTLE_RESULT_MESSAGE(10,BOConst.BO_TOLLGATE,true)
	//������Ӧ��Ϣ.
	,COMMONS_RESPONSE_MESSAGE(11,BOConst.BO_WORLD,true)
	 //�����ڵ㿪�ű仯
	,SEND_NODE_CHANGE_MESSAGE(12, BOConst.BO_TOLLGATE,true)
    ;

	public static final int MESSAGE_NUM = MessageCommands.values().length;
	
	public final int COMMAND_ID;
	//��Ϣ�����ķ���id
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
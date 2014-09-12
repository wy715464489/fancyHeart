package com.doteyplay.game.service.bo.tollgate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.doteyplay.core.bhns.AbstractSimpleService;
import com.doteyplay.game.MessageCommands;
import com.doteyplay.game.config.template.BattleDataTemplate;
import com.doteyplay.game.config.template.TollgateDataManager;
import com.doteyplay.game.config.template.TollgateNodeDataTemplate;
import com.doteyplay.game.constants.common.CommonResponseType;
import com.doteyplay.game.constants.common.RewardType;
import com.doteyplay.game.constants.pet.PetConstants;
import com.doteyplay.game.constants.tollgate.GroupUpdateResultType;
import com.doteyplay.game.constants.tollgate.NodeUpdateState;
import com.doteyplay.game.constants.tollgate.TollgateErrorType;
import com.doteyplay.game.constants.tollgate.TollgateRewardExp;
import com.doteyplay.game.domain.pet.Pet;
import com.doteyplay.game.domain.role.Role;
import com.doteyplay.game.domain.role.RolePetManager;
import com.doteyplay.game.domain.tollgate.BattleResult;
import com.doteyplay.game.domain.tollgate.NodeInfo;
import com.doteyplay.game.domain.tollgate.RoleTollgate;
import com.doteyplay.game.message.proto.AccountProBuf.PGroup;
import com.doteyplay.game.message.proto.CommonRespProBuf.PCommonResp;
import com.doteyplay.game.message.tollgate.BattleResultMessage;
import com.doteyplay.game.message.tollgate.NodeChangeMessage;
import com.doteyplay.game.message.tollgate.ShowTollgateDetailMessage;
import com.doteyplay.game.message.tollgate.TollgateChangeMessage;
import com.doteyplay.game.message.utils.ResponseMessageUtils;
import com.doteyplay.game.service.runtime.GlobalRoleCache;
import com.doteyplay.game.util.excel.TemplateService;


/**
 * 
 * @className:TollgateInfoService.java
 * @classDescription: 关卡服务类.
 * 
 * @author:Tom.Zheng
 * @createTime:2014年7月16日 下午3:29:31
 */
public class TollgateInfoService extends
		AbstractSimpleService<ITollgateInfoService> implements
		ITollgateInfoService {
	private static Logger logger = Logger.getLogger(TollgateInfoService.class);

	private RoleTollgate roleTollgate = null;

	@Override
	public int getPortalId() {
		// TODO Auto-generated method stub
		return PORTAL_ID;
	}

	@Override
	public void initlize() {
		roleTollgate = new RoleTollgate(this.getServiceId());

		roleTollgate.initlize();

		logger.error("角色的副本数据加载成功！roleId ="+this.getServiceId());

	}

	/**
	 * 向用户发送所有的关卡信息.
	 */
	@Override
	public ShowTollgateDetailMessage showTollgateDetailInfo() {
		// TODO Auto-generated method stub
		ShowTollgateDetailMessage message = new ShowTollgateDetailMessage();

		roleTollgate.showTollgateDetailInfo(message);

		return message;
	}

	/**
	 * 发送开启或关闭某个节点的信息. 附带着,另一节点的星级改变信息.
	 */
	@Override
	public void sendNodeChangeInfo(int tollgateId, int nodeId) {
		NodeChangeMessage message = new NodeChangeMessage();
		message.setTollgateId(tollgateId);
		message.addUpdateItem(nodeId, 1, 3, 0);

		this.sendMessage(message);

	}

	private void sendTollgateChangeMessage(int tollgateId,NodeInfo... nodeInfo) {
		TollgateChangeMessage message = new TollgateChangeMessage();

		message.addOperateTollgate(tollgateId,true,nodeInfo);

		this.sendMessage(message);
	}
	/**
	 * 进入战斗. 
	   1.检查节点激活情况.
	   2.成功后,进入节点.
	 */
	@Override
	public void enterBattle(int tollgateId, int nodeId, int groupId) {
		// TODO Auto-generated method stub
		// 进入关卡或者战斗事件.
		boolean isOpenTollgateAndNode = checkNodeIsAcitived(tollgateId, nodeId);
		if(!isOpenTollgateAndNode){
			return;
		}
	    Role role = getRole();
		role.setPetGroupId(groupId);
		ResponseMessageUtils.sendResponseMessage(
					MessageCommands.ENTER_BATTLE_MESSAGE.ordinal(),
					TollgateErrorType.Success.ordinal(), this.getServiceId());

			// 发送成功的消息
		
	}
	/**
	 * 接收战斗结果
	 * 1.检查该节点,激活情况.
	 * @param star
	 */
	@Override
	public void acceptBattleResult(int tollgateId, int nodeId, int star) {
		// 检查节点情况.
		boolean isOpenTollgateAndNode = checkNodeIsAcitived(tollgateId, nodeId);
		if (!isOpenTollgateAndNode) {
			return;
		}
		if (star < 0 || star > 3) {
			logger.error("客户端传送星极的参数不对!star="+star);
			return;
		}
		// 根据战斗结果,给玩家添加新的经验.
		int isUpdate = roleTollgate.acceptBattleResult(tollgateId, nodeId, star);

		addBattleResultReward(tollgateId, nodeId, star);
		// 开启下一关.按顺序来开启.
		
		openNextTollgate(tollgateId, nodeId, star,isUpdate);

	}
	
	/**
	 * 检查节点是否已经激活.
	 * 1.如果结点已经激活,进入战场,回复成功.
	 * 2.如果结点未激活,提示错误.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	private boolean checkNodeIsAcitived(int tollgateId, int nodeId){
		boolean isOpenTollgateAndNode = roleTollgate.isOpenTollgateAndNode(
				tollgateId, nodeId);

		if (!isOpenTollgateAndNode) {
			// 发送错误信息.未激活该节点。
			ResponseMessageUtils.sendResponseMessage(
					MessageCommands.ENTER_BATTLE_MESSAGE.ordinal(),
					TollgateErrorType.NoNode.ordinal(), this.getServiceId());
			return false;
		} 
		
		return true;
	}

	
	/**
	 * 解发开启下一关的操作.
	 * @param tollgateId
	 * @param nodeId
	 * @param star
	 */
	private void openNextTollgate(int tollgateId, int nodeId, int star,int isUpdate) {
		Set<Integer> allOpenTollgateIds = roleTollgate.getAllOpenTollgate();
		//返回下一个应该开启的节点.
		TollgateNodeDataTemplate nextNodeData = TollgateDataManager
				.getInstance().getNextNodeData(tollgateId, nodeId,
						allOpenTollgateIds);
		NodeChangeMessage message = new NodeChangeMessage();
		message.setTollgateId(tollgateId);
		if(isUpdate==NodeUpdateState.UPDATE.ordinal()){
			message.addUpdateItem(nodeId, 3, star, 2);//历史节点是更新.
		}
		if(nextNodeData!=null){
			//是否已经开启.
			boolean hasOpen = roleTollgate.isOpenTollgateAndNode(
					nextNodeData.getTollgateGateId(), nextNodeData.getId());
			if (!hasOpen) {
				// 开启下一关.
				roleTollgate.openTollgateOrNodeAndUpdateDB(
						nextNodeData.getTollgateGateId(),
						nextNodeData.getId());
				message.addUpdateItem(nextNodeData.getId(), 3, 0, 1);//当前节点.是添加.
			} 
		
		}else{
			//如果下一个节点是空的.应该开启下一关卡.
			
			int nextTollgate = TollgateDataManager.getInstance().getNextTollgate(
					tollgateId, allOpenTollgateIds);
			
			// 解锁新的关卡.并发送给客户端.
			boolean hasOpenTollgate = roleTollgate.isOpenTollgate(nextTollgate);
			if (!hasOpenTollgate) {
				int firstNodeId = TollgateDataManager.getInstance().getFirstNodeId(
						nextTollgate);
				roleTollgate.openTollgateOrNodeAndUpdateDB(nextTollgate,
						firstNodeId);
				NodeInfo info = roleTollgate.getManager().getTollgateAndNodeById(
						nextTollgate, firstNodeId);
				sendTollgateChangeMessage(nextTollgate, info);
				
			}
		}
		//如果更新消息不为null,即可以发送消息.
		if(!message.isItemsEmpty()){
			this.sendMessage(message);
		}
		

	}

	/**
	 * 由指定的关卡信息,找到指定的奖励信息,进行添加战队奖励.添加每一个宠物的奖励.
	 * 
	 * @param tollgateId
	 * @param nodeId
	 * @param star
	 * @param petIds
	 */
	private void addBattleResultReward(int tollgateId, int nodeId, int star) {

		TollgateNodeDataTemplate nodeDataTemplate = TollgateDataManager
				.getInstance().getTollgateData(tollgateId, nodeId);
		if (nodeDataTemplate == null) {
			logger.error("模板数据不存在!tollgateId="+tollgateId+"nodeId="+nodeId);
			return;
		}
		int opreateType = nodeDataTemplate.getOpreateType();

		if (opreateType == 0) {
			logger.error("结点的操作类型不能为opreateType=0,0是进入下一个战场!tollgateId="+tollgateId+"nodeId="+nodeId);
			return;
		}
		int battleId = nodeDataTemplate.getOpreateId();

		Map<Integer, BattleDataTemplate> all = TemplateService.getInstance()
				.getAll(BattleDataTemplate.class);

		if (!all.containsKey(battleId)) {
			logger.error("结点的原型类型中关联的战场Id在战场表中没有相关数据,battleId="+battleId+"tollgateId="+tollgateId+"nodeId="+nodeId);
			return;
		}
		BattleDataTemplate temp = all.get(battleId);

		int petExp = temp.getPetExp();// 战队经验

		int gameCoin = temp.getGameCoin();// 角色金币

		int dropGroupId = temp.getDropGroupId();
		BattleResult result = new BattleResult();
		result.gameCoin = gameCoin;
		result.star = star;
		recordRoleHistory(result, false);
		recordPetCurrent(result, false);
		addRoleExp(tollgateId);
		addPetExp(petExp,getRole());
		addGameCoin(gameCoin,getRole());
		addDropGroupId(dropGroupId);
		recordRoleHistory(result, true);
		recordPetCurrent(result, true);
		showBattleResultMsg(result);

	}
	/**
	 * 记录经验和级别的变化
	 * @param result
	 * @param isNew
	 */
	private void recordRoleHistory(BattleResult result, boolean isNew) {
		Role role = GlobalRoleCache.getInstance().getRoleById(getServiceId());
		if(role==null){
			logger.error("角色为null,无法添加经验!roleId="+getServiceId());
			return;
		}
		if (isNew) {
			result.battleRoleResult.recordNewRole(
					role.getRoleBean().getLevel(), role.getRoleBean().getExp());
		} else {
			result.battleRoleResult.recordOldRole(
					role.getRoleBean().getLevel(), role.getRoleBean().getExp());

		}

	}
	/**
	 * 记录宠物的等级和经验
	 * @param result
	 * @param isNew
	 */
	private void recordPetCurrent(BattleResult result, boolean isNew) {
		Role role = GlobalRoleCache.getInstance().getRoleById(getServiceId());
		List<Pet> curPetList = role.getPetManager().getCurPetList();
		for (Pet pet : curPetList) {
			if (isNew) {
				result.recordNewPet(pet.getId(), pet.getBean().getLevel(), pet
						.getBean().getExp());
			} else {
				result.recordOldPet(pet.getId(), pet.getBean().getLevel(), pet
						.getBean().getExp());
			}
		}

	}
	/*
	 *发送战斗结果. 
	 */
	private void showBattleResultMsg(BattleResult result) {
		BattleResultMessage message = new BattleResultMessage();
		message.setBattleResult(result);
		this.sendMessage(message);
	}

	private void addRoleExp(int tollgateId) {

		int tollgateShowType = TollgateDataManager.getInstance()
				.getTollgateShowType(tollgateId);

		int costEnergyPoint = TollgateRewardExp
				.getCostEnergyPoint(tollgateShowType);

		int roleExp = TollgateRewardExp
				.rewardRoleExpByEnergyPoint(costEnergyPoint);

		if (roleExp < 0) {
			throw new RuntimeException("副本奖励经验不能为负数");
		}

		Role role = GlobalRoleCache.getInstance().getRoleById(getServiceId());
		// 原则是先扣体力,再加经验.由于早期没有体力.
		role.addExp(roleExp);
		// 扣体力.
		if (role.addEnergy(-costEnergyPoint, RewardType.BATTLE, true)) {
			// 加经验
			logger.error("测试期间,体力值,还没有正式使用.");
		}

	}
	/**
	 * 添加宠物的经验.
	 * @param petExp
	 */
	private void addPetExp(int petExp,Role role) {
		role.getPetManager().addCurPetListExp(petExp);

	}
	/**
	 * 添加游戏币.
	 * @param gameCoin
	 */
	private void addGameCoin(int gameCoin,Role role) {

		if (gameCoin < 0) {
			throw new RuntimeException("副本奖励游戏币不能为负数");
		}
		// 加钱
		role.addMoney(gameCoin, RewardType.BATTLE, true);

	}
	/**
	 * 添加掉落组物品
	 * @param dropGroupId
	 */
	private void addDropGroupId(int dropGroupId) {
		logger.error("测试时,掉落组物品,还没有添加!");
	}

	private Role getRole() {
		Role role = GlobalRoleCache.getInstance().getRoleById(getServiceId());
		return role;
	}
	public void release() {
		if (roleTollgate != null) {
			roleTollgate.release();
			roleTollgate = null;
		}
	}
	/**
	 * 编辑组的结果,进行保存入库的操作.  
	 * 1.首先检测,组Id是否合法.<0 或>=5都是不合法的
	 * 2.检查每一个npcId 是不是当前存在的.
	 */
	@Override
	public void updateGroupOperateResult(List<PGroup> groupsList) {
		// TODO Auto-generated method stub
		
		Role role = getRole();
		boolean isOk = true;
		for (PGroup pGroup : groupsList) {
			int groupId = pGroup.getGroupId();
			if(groupId<0||groupId>=PetConstants.MAX_GROUP_NUM){
				isOk =false;
				break;
			}
			List<Long> npcIdList = pGroup.getNpcIdList();
			if(!checkPetIdExsit(role, npcIdList)){
				isOk =false;
				break;
			}
			
		}
		
		if(!isOk){
			//发送检查失败的信息.
			ResponseMessageUtils.sendResponseMessage(
					MessageCommands.GROUP_UPDATE_MESSAGE.ordinal(),
					GroupUpdateResultType.ERROR.ordinal(), this.getServiceId());
			return;
		}
		
		
		updateGroupResult(role,groupsList);
		
	}
	/**
	 * 1.将客户端传的分组信息,重新组装.以key为groupId,value为组Id的顺序搞定.
	 * 2.调用宠物的重置操作,将map传进去.
	 * 3.成功返回信息
	 * @param role
	 * @param groupsList
	 */	
	private void updateGroupResult(Role role,List<PGroup> groupsList){
		//1.
		Map<Long,Set<Integer>> map = new HashMap<Long,Set<Integer>>();
		
		for (PGroup pGroup : groupsList) {
			int groupId = pGroup.getGroupId();
			List<Long> npcIdList = pGroup.getNpcIdList();
			for (Long petId : npcIdList) {
				
				Set<Integer> groupIdSet = map.get(petId);
				if(groupIdSet==null){
					groupIdSet= new HashSet<Integer>();
					map.put(petId, groupIdSet);
				}
				groupIdSet.add(groupId);
			}
			
		}
		//2.
		role.getPetManager().resetPetGroup(map);
		
		//3.发送成功的消息.
		ResponseMessageUtils.sendResponseMessage(
				MessageCommands.GROUP_UPDATE_MESSAGE.ordinal(),
				GroupUpdateResultType.Success.ordinal(), this.getServiceId());
	}
	
	
	
	private boolean checkPetIdExsit(Role role,List<Long> npcIdList){
		Map<Long, Pet> petMap = role.getPetManager().getPetMap();
		
		if(npcIdList.isEmpty()){
			return false;
		}
		for (Long petId : npcIdList) {
			if(!petMap.containsKey(petId)){
				return false;
			}
		}
		
		return true;
	}
	
	

}

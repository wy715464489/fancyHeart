package com.doteyplay.game.config.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.doteyplay.game.constants.tollgate.TollgateNodeType;
import com.doteyplay.game.util.excel.TemplateService;

/**
 * @className:TollgateDataManager.java
 * @classDescription:
 * @author:Tom.Zheng
 * @createTime:2014年6月26日 下午1:35:23
 */
public class TollgateDataManager {
	
	
	
	private static TollgateDataManager instance = new TollgateDataManager();
	
	private TollgateDataManager(){}
	
	public static TollgateDataManager getInstance(){
		return instance;
	}
	
	
	public boolean isValidateNodeExsit(int tollgateId,int nodeId){
		return getTollgateData(tollgateId,nodeId)!=null;
	}
	
	/**
	 * 获取节点的初使化星级.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	public final byte getTollgateNodeInitStar( int nodeId){
		TollgateNodeDataTemplate node =getTollgateNodeDataTemplate(nodeId);
		if(node!=null){
			if(node.getOpreateType()==TollgateNodeType.ENTER_TOLLGATENODE.ordinal()){
				return TollgateNodeType.ENTER_TOLLGATENODE.getStarInitNum();
			}else{
				return TollgateNodeType.ENTER_BATTLE.getStarInitNum();
			}
		}
		return 0;
	}
	

	
	/**
	 * 返回具体的node节点原型数据.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	public final TollgateNodeDataTemplate getTollgateNodeDataTemplate(int nodeId) {
		Map<Integer, TollgateNodeDataTemplate> all = TemplateService.getInstance()
				.getAll(TollgateNodeDataTemplate.class);
		TollgateNodeDataTemplate node = all.get(nodeId);
		if (node == null) {
			return null;
		}
		return node;
	}
	/**
	 * 返回具体的node节点原型数据.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	public final TollgateNodeDataTemplate getTollgateData(int tollgateId, int nodeId) {
		Map<Integer, TollgateDataObject> all = TemplateService.getInstance()
				.getAll(TollgateDataObject.class);

		TollgateDataObject data = all.get(tollgateId);

		if (data == null) {
			return null;
		}

		TollgateNodeDataTemplate node = data.getNode(nodeId);

		if (node == null) {
			return null;
		}
		return node;
	}
	/**
	 * 返回某一关内,第一个节点.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	public final int getFirstNodeId(int tollgateId) {
		
		TollgateNodeDataTemplate node = getFirstNodeData(tollgateId);
		
		if (node == null) {
			throw new RuntimeException("关卡编辑错误没有节点.tollgateId="+tollgateId);
		}
		return node.getId();
	}
	
	/**
	 * 返回某一关内,第一个节点.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	public final TollgateNodeDataTemplate getFirstNodeData(int tollgateId) {
		Map<Integer, TollgateDataObject> all = TemplateService.getInstance()
				.getAll(TollgateDataObject.class);
		
		TollgateDataObject data = all.get(tollgateId);
		
		if (data == null) {
			return null;
		}
		
		TollgateNodeDataTemplate node = data.getFirstNode();
		
		if (node == null) {
			return null;
		}
		return node;
	}
	
	/**
	 * 返回下一节点.按顺序来.
	 * @param tollgateId
	 * @param nodeId
	 * @return
	 */
	public final TollgateNodeDataTemplate getNextNodeData(int tollgateId,int nodeId,Set<Integer> allOpenTollgateIds){
		Map<Integer, TollgateDataObject> all = TemplateService.getInstance()
				.getAll(TollgateDataObject.class);
		
		TollgateDataObject data = all.get(tollgateId);

		if (data == null) {
			return null;
		}
		TollgateNodeDataTemplate node = data.getNextNode(nodeId);
		
		if(node!=null){
			//检查结点是否已经开启.如果开启.结果只发更新消息.如查没有开启,开启后,发送开启消息.
			return node;
		}
		return null;

	}
	
	public final int getNextTollgate(int tollgateId,Set<Integer> allOpenTollgateIds){
		Map<Integer, TollgateDataObject> all = TemplateService.getInstance()
				.getAll(TollgateDataObject.class);
		//已经开放的关卡数量 == 总的关卡数量.
		if (allOpenTollgateIds.size() == all.keySet().size()) {
			// 到最后一关了.
			throw new RuntimeException("达到最后一个关卡节点了.");
		}
		int nextTollgateId = 0;
		Set<Integer> keySet = all.keySet();
		List<Integer> keyList = new ArrayList<Integer>();
		for (Integer integer : keySet) {
			keyList.add(integer);
		}
		
		Collections.sort(keyList, new Comparator<Integer>(){

			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1-o2;
			}
			
		});
		
		for (Integer element : keyList) {
			// 新的关卡Id.不是被击活的关卡Id.
			if(!allOpenTollgateIds.contains(element)){
				nextTollgateId = element;
				break;
			}
		}
		
		return nextTollgateId;

	}
	
	/**
	 * 查找显示的类型.
	 * @param tollgateId
	 * @return
	 */
	public int getTollgateShowType(int tollgateId){
		Map<Integer, TollgateDataObject> all = TemplateService.getInstance()
				.getAll(TollgateDataObject.class);
		
		TollgateDataObject tollgateDataObject = all.get(tollgateId);
		
		return tollgateDataObject.getTollgateShowType();
	}

	
	
}

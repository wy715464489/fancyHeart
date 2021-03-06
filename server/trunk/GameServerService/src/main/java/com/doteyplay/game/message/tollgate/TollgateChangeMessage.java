package com.doteyplay.game.message.tollgate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import com.doteyplay.game.MessageCommands;
import com.doteyplay.game.domain.tollgate.NodeInfo;
import com.doteyplay.game.message.proto.GateProBuf;
import com.doteyplay.game.message.proto.GateProBuf.PNodeItem;
import com.doteyplay.game.message.proto.GateProBuf.PUpdateGates;
import com.doteyplay.net.message.AbstractMessage;

/**
 *
* @className:TollgateAndNodeChangeInfoMessage.java
* @classDescription:向客户段展示副本及节点的增量改变消息
* @author:Tom.Zheng
* @createTime:2014年6月24日 下午6:52:10
 */
public class TollgateChangeMessage extends AbstractMessage {
	/**
	 * @author:Tom.Zheng
	 * @createTime:2014年6月24日 下午6:52:28
	 */
	private static final long serialVersionUID = -2782476069461493749L;
	
	private Map<Integer,Prop> operateTollgateMap;

	private class Prop{
		List<NodeInfo> nodeInfos;
		boolean isLock;
		
		public Prop(){
			nodeInfos = new ArrayList<NodeInfo>();
		}
		
		public void addNodeInfo(NodeInfo info){
			nodeInfos.add(info);
		}
	}

	public TollgateChangeMessage()
	{
		super(MessageCommands.SEND_TOLLGATE_CHANGE_MESSAGE);
	}
	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public void decodeBody(IoBuffer in) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void encodeBody(IoBuffer out) {
		// TODO Auto-generated method stub
		//生成byte数组.发出去.
		GateProBuf.PUpdateGates.Builder builder = GateProBuf.PUpdateGates.newBuilder();
		if(this.operateTollgateMap==null){
			throw new RuntimeException("消息为空,不用发送.");
		}
		for (Integer key : this.operateTollgateMap.keySet()) {
			GateProBuf.PGateItem.Builder itemBuilder = GateProBuf.PGateItem.newBuilder();
			itemBuilder.setGateID(key);
			Prop p = operateTollgateMap.get(key);
			itemBuilder.setIsLock(p.isLock);
			for (NodeInfo element : p.nodeInfos) {
				
				PNodeItem.Builder nodeItem = PNodeItem.newBuilder();
				nodeItem.setXID(element.getNodeId());
				nodeItem.setStar(element.getStarResult());
				nodeItem.setTimes(element.getTimes());
				itemBuilder.addItems(nodeItem);
			}
			builder.addGates(itemBuilder);
		}
		PUpdateGates build = builder.build();
		out.put(build.toByteArray());
	}

	public void addOperateTollgate(int tollgateId,boolean isLock,NodeInfo... nodeInfo){
		if (this.operateTollgateMap==null) {
			this.operateTollgateMap = new HashMap<Integer,Prop>();
		}
		Prop p = new Prop();
		for (NodeInfo nodeInfo2 : nodeInfo) {
			p.addNodeInfo(nodeInfo2);
		}
		p.isLock = isLock;
		this.operateTollgateMap.put(tollgateId, p);
	}
	
	

}

package com.doteyplay.game.message.login;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.doteyplay.core.bhns.BOServiceManager;
import com.doteyplay.game.MessageCommands;
import com.doteyplay.game.constants.account.LoginResult;
import com.doteyplay.game.domain.item.RoleItem;
import com.doteyplay.game.domain.outfit.Outfit;
import com.doteyplay.game.domain.pet.Pet;
import com.doteyplay.game.domain.role.Role;
import com.doteyplay.game.domain.skill.SpriteSkill;
import com.doteyplay.game.message.proto.AccountProBuf;
import com.doteyplay.game.message.proto.ItemProBuf;
import com.doteyplay.game.message.proto.NpcProBuf;
import com.doteyplay.game.message.tollgate.ShowTollgateDetailMessage;
import com.doteyplay.game.service.bo.tollgate.ITollgateInfoService;
import com.doteyplay.net.message.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class LoginGameMessage extends AbstractMessage
{
	private static final Logger logger = Logger
			.getLogger(LoginGameMessage.class);

	private String account;
	private String sessionKey;
	private int areaId;

	private LoginResult result;
	private Role role;
	private Collection<RoleItem> bagItemList;
	private Map<Long, Outfit> outfitMap; 
	
	public LoginGameMessage()
	{
		super(MessageCommands.LOGIN_GAME_MESSAGE);
	}

	@Override
	public void decodeBody(IoBuffer in)
	{
		byte[] bytes = getProtoBufBytes(in);
		try
		{
			AccountProBuf.LoginReq req = AccountProBuf.LoginReq
					.parseFrom(bytes);
			account = req.getAccount();
			sessionKey = req.getKey();
			areaId = req.getAreaId();
		} catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void encodeBody(IoBuffer out)
	{
		AccountProBuf.LoginResp.Builder builder = AccountProBuf.LoginResp
				.newBuilder();
		builder.setResult(this.result.ordinal());

		if (result == LoginResult.SUCCESS)
		{
			// role基本信息
			AccountProBuf.PRole.Builder roleBuilder = AccountProBuf.PRole
					.newBuilder();
			roleBuilder.setCoin(role.getRoleBean().getMoney());
			roleBuilder.setLevel(role.getRoleBean().getLevel());
			roleBuilder.setRmb(role.getRoleBean().getRmb());
			roleBuilder.setRoleId(role.getRoleId());
			roleBuilder.setRoleName(role.getRoleBean().getName());
			roleBuilder.setStamina(role.getRoleBean().getEnergy());
			roleBuilder.setVipLvl(role.getRoleBean().getVipLevel());
			roleBuilder.setExp(role.getRoleBean().getExp());
			roleBuilder.setGroupId(role.getRoleBean().getPetGroupId());
			builder.setRole(roleBuilder);

			// 物品列表
			if (bagItemList != null && bagItemList.size() > 0)
			{
				for (RoleItem item : bagItemList)
				{
					ItemProBuf.PItem.Builder itemBuilder = ItemProBuf.PItem
							.newBuilder();
					itemBuilder.setItemId(item.getBean().getItemId());
					itemBuilder.setItemNum(item.getBean().getItemNum());
					itemBuilder.setNpcId(item.getPetId());
					builder.addItemList(itemBuilder);
				}
			}

			//副本
			ITollgateInfoService tollgateInfoService = BOServiceManager
					.findService(ITollgateInfoService.PORTAL_ID, role.getRoleId());

			ShowTollgateDetailMessage tempMessage = tollgateInfoService
					.showTollgateDetailInfo();

			builder.setGate(tempMessage.getPGateResp());
			
			//pet
			for(Pet pet:role.getPetManager().getPetMap().values())
			{
				NpcProBuf.PNpc.Builder petBuilder = NpcProBuf.PNpc.newBuilder();
				
				//base info
				petBuilder.setExp(pet.getBean().getExp());
				petBuilder.setLevel(pet.getBean().getLevel());
				petBuilder.setNpcId(pet.getId());
				petBuilder.setQuality(pet.getBean().getQuality());
				petBuilder.setRoleId(role.getRoleId());
				petBuilder.setNpcName(pet.getName());
				petBuilder.setSpriteId(pet.getBean().getSpriteId());
				petBuilder.setStar(pet.getBean().getStar());
				
				//outfit
				for(RoleItem item:outfitMap.get(pet.getId()).getEquipList())
				{
					ItemProBuf.PItem.Builder itemBuilder = ItemProBuf.PItem
							.newBuilder();
					itemBuilder.setItemId(item.getBean().getItemId());
					itemBuilder.setItemNum(item.getBean().getItemNum());
					itemBuilder.setNpcId(pet.getId());
					itemBuilder.setPosId(item.getEquipIdx());
					petBuilder.addEquipList(itemBuilder);
				}
				
				//skill
				for(SpriteSkill skill:pet.getSkillManager().getSkillMap().values())
				{
					petBuilder.addSkillIdList(skill.getBean().getSkillId());
				}
				builder.addNpcList(petBuilder);
			}
			
			//pet Group 关系
			
			Map<Integer, List<Pet>> curPetMap = role.getPetManager().getCurPetMap();
			
			for (Integer groupId : curPetMap.keySet()) {
				AccountProBuf.PGroup.Builder groupBuilder = AccountProBuf.PGroup.newBuilder();
				groupBuilder.setGroupId(groupId);
				List<Pet> list = curPetMap.get(groupId);
				for (Pet pet : list) {
					groupBuilder.addNpcId(pet.getId());
				}
				
				builder.addGroups(groupBuilder);
			}
		}

		AccountProBuf.LoginResp resp = builder.build();
		out.put(resp.toByteArray());
	}

	public void release()
	{
	}

	public LoginResult getResult()
	{
		return result;
	}

	public void setResult(LoginResult result)
	{
		this.result = result;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public String getSessionKey()
	{
		return sessionKey;
	}

	public void setSessionKey(String sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public Collection<RoleItem> getBagItemList()
	{
		return bagItemList;
	}

	public void setBagItemList(Collection<RoleItem> bagItemList)
	{
		this.bagItemList = bagItemList;
	}

	public Map<Long, Outfit> getOutfitMap()
	{
		return outfitMap;
	}

	public void setOutfitMap(Map<Long, Outfit> outfitMap)
	{
		this.outfitMap = outfitMap;
	}

	public int getAreaId()
	{
		return areaId;
	}

	public void setAreaId(int areaId)
	{
		this.areaId = areaId;
	}
	
}

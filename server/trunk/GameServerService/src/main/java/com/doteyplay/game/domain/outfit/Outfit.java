package com.doteyplay.game.domain.outfit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doteyplay.core.dbcs.DBCS;
import com.doteyplay.game.constants.item.OutfitInstallResult;
import com.doteyplay.game.domain.gamebean.OutfitBean;
import com.doteyplay.game.domain.item.RoleItem;
import com.doteyplay.game.domain.sprite.AbstractSprite;
import com.doteyplay.game.message.common.CommonItemUpdateMessage;
import com.doteyplay.game.message.proto.ItemProBuf.PItemChangeLog;
import com.doteyplay.game.persistence.serverdata.outfit.IOutfitBeanDao;

public class Outfit
{
	private List<RoleItem> equipList = new ArrayList<RoleItem>();
	
	private OutfitBean outfitBean;
	private AbstractSprite sprite;
	private long roleId;

	public Outfit(long roleId, AbstractSprite sprite,OutfitBean outfitBean)
	{
		this.outfitBean = outfitBean;
		if(this.outfitBean == null)
		{
			this.outfitBean = new OutfitBean();
			this.outfitBean.setRoleId(roleId);
			this.outfitBean.setPetId(sprite.getId());
			
			IOutfitBeanDao dao = DBCS.getExector(IOutfitBeanDao.class);
			dao.insertOutfitBean(this.outfitBean);
		}
		else
		{
			if(this.outfitBean.getOutfitData() != null)
			{
				try
				{
					DataInputStream in = new DataInputStream(new ByteArrayInputStream(
							this.outfitBean.getOutfitData()));
					int size = in.readByte();
					for(int i = 0 ; i < size; i ++)
					{
						int itemId = in.readInt();
						int equipIdx = in.readByte();
						RoleItem item = RoleItem.createRoleItem(roleId, itemId, 1);
						item.setPetId(this.outfitBean.getPetId());
						item.setEquipIdx(equipIdx);
						equipList.add(item);
					}
					in.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}	
			}
		}
		
		this.sprite = sprite;
		this.roleId = roleId;
	}
	
	public void saveDB()
	{
		try
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			
			if(equipList != null)
			{
				out.writeByte(equipList.size());
				for(RoleItem item :equipList)
				{
					if(item != null)
					{
						out.writeInt(item.getBean().getItemId());
						out.writeByte(item.getEquipIdx());
					}
				}
			}
			out.flush();
			out.close();	
			
			outfitBean.setOutfitData(bout.toByteArray());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		IOutfitBeanDao outfitBeanDao = DBCS.getExector(IOutfitBeanDao.class);
		outfitBeanDao.updateOutfitBean(outfitBean);
		
	}

	public OutfitInstallResult installOutfit(RoleItem item, int equipIdx)
	{
		item = RoleItem.closeRoleItem(item.getBean());
		item.getBean().setItemNum(1);
		item.setPetId(this.sprite.getId());
		item.setEquipIdx(equipIdx);
		
		equipList.add(item);

		PItemChangeLog.Builder builder = PItemChangeLog.newBuilder();
		builder.setItemAddNum(1);
		builder.setItemFinalNum(1);
		builder.setItemId(item.getBean().getItemId());
		builder.setNpcId(this.sprite.getId());
		builder.setPosId(equipIdx);
		
		List<PItemChangeLog.Builder> itemList = new ArrayList<PItemChangeLog.Builder>();
		itemList.add(builder);
		CommonItemUpdateMessage message = new CommonItemUpdateMessage(itemList);
		this.sprite.getTopOwner().sendMsg(message);
		
		this.saveDB();
		return OutfitInstallResult.SUCCESS;
	}

	public OutfitInstallResult installOutfitCheck(RoleItem item, int equipIdx)
	{
		if (getQualityItemIdList().indexOf(item.getBean().getItemId()) <= -1)
			return OutfitInstallResult.ITEM_NOT_FIT;
		
			for(RoleItem tmpItem:equipList)
			{
				if (tmpItem != null && tmpItem.getEquipIdx() == equipIdx)
					return OutfitInstallResult.ITEM_HAS_EXIST;
			}
		return OutfitInstallResult.SUCCESS; 
	}
	
	public void removeAll()
	{
		List<PItemChangeLog.Builder> itemList = new ArrayList<PItemChangeLog.Builder>();
		for(RoleItem item:equipList)
		{
			PItemChangeLog.Builder builder = PItemChangeLog.newBuilder();
			builder.setItemAddNum(-1);
			builder.setItemFinalNum(0);
			builder.setNpcId(this.sprite.getId());
			builder.setItemId(item.getBean().getItemId());
			builder.setPosId(item.getEquipIdx());
			itemList.add(builder);	
		}
		
		CommonItemUpdateMessage message = new CommonItemUpdateMessage(itemList);
		this.sprite.getTopOwner().sendMsg(message);
		
		equipList.clear();
		this.saveDB();
	}

	public AbstractSprite getSprite()
	{
		return sprite;
	}

	public void setSprite(AbstractSprite sprite)
	{
		this.sprite = sprite;
	}



	public long getRoleId()
	{
		return roleId;
	}

	public void setRoleId(long roleId)
	{
		this.roleId = roleId;
	}

	public List<Integer> getQualityItemIdList()
	{
		if(sprite == null)
			return null;
		
		return sprite.getSpriteDataObject().getPropDataList()
				.get(sprite.getSpriteBean().getQuality()).getCurQualityItemList();
	}
	
	public boolean isOutfitFull()
	{
		for(Integer itemId:getQualityItemIdList())
		{
			boolean hasItem = false;
			equiplist:
			for(RoleItem item :equipList)
				if(item.getBean().getItemId() == itemId)
				{
					hasItem = true;
					break equiplist;
				}
			
			if(!hasItem)
				return false;
		}
		return true;
	}

	public List<RoleItem> getEquipList()
	{
		return equipList;
	}

	public void setEquipList(List<RoleItem> equipList)
	{
		this.equipList = equipList;
	}

	public OutfitBean getOutfitBean()
	{
		return outfitBean;
	}

	public void setOutfitBean(OutfitBean outfitBean)
	{
		this.outfitBean = outfitBean;
	}
}

package com.doteyplay.game.message.pet;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.doteyplay.game.MessageCommands;
import com.doteyplay.game.domain.pet.Pet;
import com.doteyplay.game.domain.skill.SpriteSkill;
import com.doteyplay.game.message.proto.NpcProBuf;
import com.doteyplay.net.message.AbstractMessage;

public class PetAddRemoveMessage extends AbstractMessage
{
	private static final Logger logger = Logger
			.getLogger(PetAddRemoveMessage.class);

	private List<Pet> petList;
	private boolean addOrRemove; 
	
	public PetAddRemoveMessage()
	{
		super(MessageCommands.PET_ADD_REMOVE_MESSAGE);
	}

	@Override
	public void decodeBody(IoBuffer in)
	{
		
	}

	@Override
	public void encodeBody(IoBuffer out)
	{
		NpcProBuf.PAddOrRemoveNpc.Builder builder = NpcProBuf.PAddOrRemoveNpc.newBuilder();
		builder.setAddOrRemove(addOrRemove);
		
		if(petList != null || petList.size() > 0)
		{
			for(Pet pet:petList)
			{
				NpcProBuf.PNpc.Builder petBuilder = NpcProBuf.PNpc.newBuilder();
				
				//base info
				petBuilder.setExp(pet.getBean().getExp());
				petBuilder.setLevel(pet.getBean().getLevel());
				petBuilder.setNpcId(pet.getId());
				petBuilder.setQuality(pet.getBean().getQuality());
				petBuilder.setRoleId(pet.getTopOwner().getRoleId());
				petBuilder.setNpcName(pet.getName());
				petBuilder.setSpriteId(pet.getBean().getSpriteId());
				petBuilder.setStar(pet.getBean().getStar());
				
				//skill
				for(SpriteSkill skill:pet.getSkillManager().getSkillMap().values())
					petBuilder.addSkillIdList(skill.getBean().getSkillId());
				
				builder.addNpcs(petBuilder);
			}
		}
		
		NpcProBuf.PAddOrRemoveNpc resp = builder.build();
		out.put(resp.toByteArray());
	}

	public void release()
	{
	}

	public List<Pet> getPetList()
	{
		return petList;
	}

	public void setPetList(List<Pet> petList)
	{
		this.petList = petList;
	}

	public boolean isAddOrRemove()
	{
		return addOrRemove;
	}

	public void setAddOrRemove(boolean addOrRemove)
	{
		this.addOrRemove = addOrRemove;
	}
}

package com.doteyplay.game.service.bo.role;

import com.doteyplay.core.bhns.ISimpleService;
import com.doteyplay.game.BOConst;
import com.doteyplay.game.constants.item.OutfitUpgradeResult;
import com.doteyplay.game.constants.pet.PetStarUpgradeResult;

public interface IRoleService extends ISimpleService
{
	public final static int PORTAL_ID = BOConst.BO_ROLE;

	public void initialize();

	public void summonPet(int petId);

	public OutfitUpgradeResult qualityUpgrade(long petId);

	public PetStarUpgradeResult starUpgrade(long petId);
	
	public void highQualityUpgrade(long petId);
}
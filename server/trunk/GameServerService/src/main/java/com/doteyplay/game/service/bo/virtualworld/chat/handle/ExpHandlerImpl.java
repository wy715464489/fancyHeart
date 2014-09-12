package com.doteyplay.game.service.bo.virtualworld.chat.handle;

import java.util.List;
import java.util.Map;

import com.doteyplay.core.dbcs.DBCS;
import com.doteyplay.game.CommonConstants;
import com.doteyplay.game.domain.gamebean.PetBean;
import com.doteyplay.game.domain.gamebean.RoleBean;
import com.doteyplay.game.domain.pet.Pet;
import com.doteyplay.game.domain.role.Role;
import com.doteyplay.game.persistence.serverdata.pet.IPetBeanDao;
import com.doteyplay.game.persistence.serverdata.role.IRoleBeanDao;
import com.doteyplay.game.service.bo.virtualworld.chat.patameter.ExpPatameterObject;

/**
 * 给人物加经验的同时,宠物也加一样的经验,方便测试使用.
 * @className:ExpHandlerImpl.java
 * @classDescription:
 * @author:Tom.Zheng
 * @createTime:2014年7月10日 下午4:37:05
 */
public class ExpHandlerImpl extends AbstractHandlerImpl<ExpPatameterObject>{

	@Override
	public void onlineHandle(Role role, ExpPatameterObject t) {
		// TODO Auto-generated method stub
		role.addExp(t.getNum());
		Map<Long, Pet> curPetMap = role.getPetManager().getPetMap();
		for (Pet pet : curPetMap.values()) {
			pet.addExp(t.getNum());
		}
	}

	@Override
	public void offlineHandle(long roleId, ExpPatameterObject t) {
		// TODO Auto-generated method stub
		//采用邮件服务.发送其它值.
		IRoleBeanDao dao = DBCS.getExector(IRoleBeanDao.class);
		
		RoleBean roleBean = dao.selectRoleBean(roleId);
		
		if (roleBean == null) {
			throw new RuntimeException("GM do 角色属性添加,Id对应的角色不存在");
		}
		int oldLevel =roleBean.getLevel();
		if (oldLevel >= CommonConstants.ROLE_MAX_LEVEL - 1){
			return;
		}else{
			roleBean.setExp((t.getNum()+roleBean.getExp())>0?t.getNum()+roleBean.getExp():0);
	
			long curUpgradeExp = CommonConstants.ROLE_LEVEL_EXP[roleBean
					.getLevel()];
	
			while (curUpgradeExp < roleBean.getExp())
			{
				roleBean.setLevel(roleBean.getLevel() + 1);
	
				if (roleBean.getLevel() >= CommonConstants.ROLE_MAX_LEVEL)
					break;
				curUpgradeExp = CommonConstants.ROLE_LEVEL_EXP[roleBean
						.getLevel()];
			}
			
			dao.updateRoleBean(roleBean);
		}
		IPetBeanDao petBeanDao = DBCS.getExector(IPetBeanDao.class);
		
		List<PetBean> petBeans = petBeanDao.selectPetBeanListByRoleId(roleId);
		
		for (PetBean petBean : petBeans) {
			int oldPetLevel = petBean.getLevel();
			if (oldPetLevel >= CommonConstants.ROLE_MAX_LEVEL)
				return;

			if (oldPetLevel >= roleBean.getLevel())
				return;

			petBean.setExp((petBean.getExp()+t.getNum())>0?petBean.getExp()+t.getNum():0);

			long curUpgradeExp = CommonConstants.NPC_LEVEL_EXP[petBean.getLevel()];

			while (curUpgradeExp < petBean.getExp())
			{
				int newLevel = petBean.getLevel()+1;
				if(newLevel>CommonConstants.ROLE_MAX_LEVEL){
					break;
				}else{
					petBean.setLevel(newLevel);
					curUpgradeExp = CommonConstants.NPC_LEVEL_EXP[petBean.getLevel()];
				}
			}
			petBeanDao.updatePetBean(petBean);
		}
		
	}

}

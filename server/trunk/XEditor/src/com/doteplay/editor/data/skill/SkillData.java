/**
 * 
 */
package com.doteplay.editor.data.skill;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Element;

import com.doteplay.editor.common.BaseData;
import com.doteplay.editor.common.DataManager;
import com.doteplay.editor.util.Util;
import com.doteyplay.game.CommonConstants;
import com.doteyplay.game.config.GameConfig;
import com.doteyplay.game.constants.ActivateEvent;
import com.doteyplay.game.constants.DamageType;
import com.doteyplay.game.constants.skill.SkillAmmoType;
import com.doteyplay.game.constants.skill.SkillEffectRange;
import com.doteyplay.game.constants.skill.SkillTargetSelectType;
import com.doteyplay.game.gamedata.data.skill.SkillGameData;

public class SkillData extends BaseData<SkillGameData>
{
	public SkillData()
	{
		super(new SkillGameData());
		type = "skill";
		dataGroup = DataManager.getDataGroup(type);
	}
	
	public boolean checkData()
	{
		if(gameData.actionType == null)
			super.checkDataResult="ȱ����������";
		if(gameData.effectRange == null)
			super.checkDataResult="ȱ��Ӱ������";
		if(gameData.damageType == null)
			super.checkDataResult="ȱ���˺�����";
		if(gameData.ammoType == null)
			super.checkDataResult="ȱ�ٵ�������";
		if(gameData.selectType == null)
			super.checkDataResult="ȱ��ѡ�����";
		if(gameData.event == null)
			super.checkDataResult="ȱ���¼�����";
		return true;
	}
	
	@Override
	public void releaseData()
	{
	}

}
package com.doteyplay.game.gamedata.data.sprite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.doteyplay.game.constants.sprite.SpritePropType;
import com.doteyplay.game.constants.sprite.SpriteQualityType;
import com.doteyplay.game.gamedata.data.IGameData;

public class SpriteQualityPropData implements IGameData
{
	public List<Integer> baseValueList = new ArrayList<Integer>();
	public List<Integer> rateList = new ArrayList<Integer>();
	public SpriteQualityType spriteQualityType = SpriteQualityType.QUALITY_0;
	
	public List<Integer> curQualityItemList = new ArrayList<Integer>();

	@Override
	public void load(DataInputStream in) throws IOException
	{

	}

	@Override
	public void save(DataOutputStream out) throws IOException
	{
	}


	public SpriteQualityType getSpriteQualityType()
	{
		return spriteQualityType;
	}

	public List<Integer> getCurQualityItemList()
	{
		return curQualityItemList;
	}

	public List<Integer> getBaseValueList()
	{
		return baseValueList;
	}

	public void setBaseValueList(List<Integer> baseValueList)
	{
		this.baseValueList = baseValueList;
	}

	public List<Integer> getRateList()
	{
		return rateList;
	}

	public void setRateList(List<Integer> rateList)
	{
		this.rateList = rateList;
	}

	public void setSpriteQualityType(SpriteQualityType spriteQualityType)
	{
		this.spriteQualityType = spriteQualityType;
	}

	public void setCurQualityItemList(List<Integer> curQualityItemList)
	{
		this.curQualityItemList = curQualityItemList;
	}
}

package com.doteyplay.game.config.template;

import com.doteyplay.game.constants.sprite.SpriteStarType;
import com.doteyplay.game.gamedata.data.sprite.SpriteStarData;
import com.doteyplay.game.util.excel.ExcelCellBinding;
import com.doteyplay.game.util.excel.ExcelRowBinding;
import com.doteyplay.game.util.excel.TemplateConfigException;
import com.doteyplay.game.util.excel.TemplateObject;
import com.doteyplay.game.util.excel.TemplateService;

@ExcelRowBinding
public class SpriteStarDataTemplate extends TemplateObject
{
	@ExcelCellBinding
	protected int storeNum;
	
	@ExcelCellBinding
	protected int upgradeMoney;
	
	@ExcelCellBinding
	protected int hpRate;
	
	@ExcelCellBinding
	protected int apRate;
	
	@ExcelCellBinding
	protected int pdRate;
	
	@ExcelCellBinding
	protected int mdRate;

	@Override
	public void onLoadFinished()
	{
		int spriteId = this.id / 10;
		int star = this.id % 10 - 1;
		SpriteDataObject spriteDataObject = TemplateService.getInstance().get(spriteId, SpriteDataObject.class);
		
		SpriteStarData data = new SpriteStarData();
		data.spriteStarType = SpriteStarType.values()[star];
		data.stoneNum = this.storeNum;
		data.upgradeMoney = this.upgradeMoney;
				
		data.propRate.add(this.hpRate);
		data.propRate.add(this.apRate);
		data.propRate.add(this.pdRate);
		data.propRate.add(this.mdRate);
		data.propRate.add(0);
		data.propRate.add(0);
		data.propRate.add(0);
		
		spriteDataObject.starDataList.add(data);
	}

	public int getStoreNum()
	{
		return storeNum;
	}

	public void setStoreNum(int storeNum)
	{
		this.storeNum = storeNum;
	}

	public int getUpgradeMoney()
	{
		return upgradeMoney;
	}

	public void setUpgradeMoney(int upgradeMoney)
	{
		this.upgradeMoney = upgradeMoney;
	}

	public int getHpRate()
	{
		return hpRate;
	}

	public void setHpRate(int hpRate)
	{
		this.hpRate = hpRate;
	}

	public int getApRate()
	{
		return apRate;
	}

	public void setApRate(int apRate)
	{
		this.apRate = apRate;
	}

	public int getPdRate()
	{
		return pdRate;
	}

	public void setPdRate(int pdRate)
	{
		this.pdRate = pdRate;
	}

	public int getMdRate()
	{
		return mdRate;
	}

	public void setMdRate(int mdRate)
	{
		this.mdRate = mdRate;
	}

	@Override
	public String toString()
	{
		return "SpriteStarDataTemplate [storeNum="
				+ storeNum + ", upgradeMoney=" + upgradeMoney + ", hpRate="
				+ hpRate + ", apRate=" + apRate + ", pdRate=" + pdRate
				+ ", mdRate=" + mdRate + "]";
	}

}
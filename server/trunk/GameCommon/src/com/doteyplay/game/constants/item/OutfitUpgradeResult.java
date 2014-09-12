package com.doteyplay.game.constants.item;

public enum OutfitUpgradeResult
{
	SUCCESS,
	//物品不足
	ITEM_NOT_ENOUGH,
	//找不到穿装备的精灵
	NOT_FOUND_SPRITE,
	//品质最大值
	MAX_QUALITY_ALREADY,
	//金币不足
	MONEY_NOT_ENOUGH,
	//不符合变异品质条件
	NOT_FIT_UP_QUALITY_LEVEL,
	//被动技能等级已满
	SKILL_LEVEL_FULL,
	;
}

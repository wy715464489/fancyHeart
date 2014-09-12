//
//  Bag.cpp
//  fancyHeart
//
//  Created by doteyplay on 14-8-5.
//
//

#include "Bag.h"

Bag* Bag::create()
{
    Bag* bag=new Bag();
    if (bag && bag->init("publish/bag/bag.ExportJson")) {
        bag->autorelease();
        return bag;
    }
    CC_SAFE_DELETE(bag);
    return nullptr;
}

bool Bag::init(std::string fileName)
{
	if(!BaseUI::init(fileName))
    {
        return false;
    }
	//如果需要对cocostudio 设计的ui进行调整
    
    Widget*imgBg = static_cast<Widget*>(layout->getChildByName("imgBg"));
    this->rightBg=static_cast<Widget*>(imgBg->getChildByName("rightBg"));
    this->leftBg=static_cast<Widget*>(imgBg->getChildByName("leftBg"));
    this->top=static_cast<Widget*>(layout->getChildByName("top"));
    Button* sellBtn=static_cast<Button*>(leftBg->getChildByName("sellBtn"));
    Button* lookBtn=static_cast<Button*>(leftBg->getChildByName("lookBtn"));
    Button* btnAddMoney=static_cast<Button*>(top->getChildByName("btnAddMoney"));
    Button* btnAddGem=static_cast<Button*>(top->getChildByName("btnAddGem"));
    Button* btnAddPower=static_cast<Button*>(top->getChildByName("btnAddPower"));
    this->isShow=false;
    this->isNotAllSell = false;
    
    ScrollView*scrollView=static_cast<ui::ScrollView*>(this->rightBg->getChildByName("scrollView"));
    this->propItem=scrollView->getChildByName("propItem");
    //当前道具得数量
    this->currentPropNum = static_cast<Text*>(leftBg->getChildByName("currentPropNum"));
    this->propertyTxt1 = static_cast<Text*>(leftBg->getChildByName("propertyTxt1"));
    this->propertyTxt2 = static_cast<Text*>(leftBg->getChildByName("propertyTxt2"));
    this->propertyTxt3 = static_cast<Text*>(leftBg->getChildByName("propertyTxt3"));
    this->propertyTxt4 = static_cast<Text*>(leftBg->getChildByName("propertyTxt4"));
    this->propertyTxt5 = static_cast<Text*>(leftBg->getChildByName("propertyTxt5"));
    this->itemPriceTxt = static_cast<Text*>(leftBg->getChildByName("itemPriceTxt"));
    this->desTxt =static_cast<Text*>(leftBg->getChildByName("desTxt"));
    this->nameTxt = static_cast<Text*>(leftBg->getChildByName("nameTxt"));
    this->propItem->retain();

    this->itemInfo.Clear();
    //道具的显示（-1表示显示全部）
    this->currentType = -1;
    this->getTypeInfo(this->currentType);
    //玩家信息的显示
    this->setPlayerInfo();
    
    sellBtn->addTouchEventListener(CC_CALLBACK_2(Bag::touchButtonEvent, this));
    lookBtn->addTouchEventListener(CC_CALLBACK_2(Bag::touchButtonEvent, this));
    btnAddMoney->addTouchEventListener(CC_CALLBACK_2(Bag::touchButtonEvent, this));
    btnAddGem->addTouchEventListener(CC_CALLBACK_2(Bag::touchButtonEvent, this));
    btnAddPower->addTouchEventListener(CC_CALLBACK_2(Bag::touchButtonEvent, this));
    
    std::vector<Button*> buttons;
    std::vector<std::string> btnName={"tab1","tab2","tab3","tab4","tab5","returnBtn"};
    for (std::string name : btnName)
    {
        auto btn=this->rightBg->getChildByName(name);
        btn->setTouchEnabled(true);
        btn->addTouchEventListener(CC_CALLBACK_2(Bag::touchEvent,this));
        buttons.push_back(static_cast<Button*>(btn));
    }
    //移除舞台上的单个item
    this->propItem->removeFromParent();
    
    tabBar=TabBar::create(buttons);
    tabBar->retain();

	return true;
}

void Bag::onEnter()
{
    BaseUI::onEnter();
}

void Bag::setPlayerInfo()
{
    PRole role=Manager::getInstance()->getRoleData()->role();
    static_cast<Text*>(this->top->getChildByName("moneyTxt"))->setString(Value(role.rmb()).asString());
    static_cast<Text*>(this->top->getChildByName("gemTxt"))->setString(Value(role.coin()).asString());
    static_cast<Text*>(this->top->getChildByName("powerTxt"))->setString(Value(role.stamina()).asString());
}

void Bag::getTypeInfo(int type)
{
//    Widget* leftBg=static_cast<Widget*>(layout->getChildByName("leftBg"));
    this->leftBg->setVisible(this->isShow);
    auto itemlist = Manager::getInstance()->getRoleData()->itemlist();
//    Widget* rightBg=static_cast<Widget*>(layout->getChildByName("rightBg"));
    ScrollView*scrollView=static_cast<ui::ScrollView*>(this->rightBg->getChildByName("scrollView"));
    
    int num=itemlist.size();
    Size size=Size(scrollView->getInnerContainerSize().width,(propItem->getContentSize().height+15)*(num/4) - 10);
    scrollView->setInnerContainerSize(size);
    //移除所有对象
    scrollView->removeAllChildren();
    //清除道具容器
    this->propItems.clear();
    //根据服务器给的数据添加道具列表
    
    
    
    for (int i = 0;i<itemlist.size();i++)
    {
        auto item = Manager::getInstance()->getRoleData()->itemlist(i);
        XItem*xItem = XItem::record(Value(item.itemid()));
        if (type != -1) {
            //类型一样，则显示出来此道具
            if (xItem->getType() == type) {
                this->setItem(item,i);
            }
        }else if(type == -1){
            this->setItem(item,i);
        }
    }
    if (this->isShow == false) {
        auto move = MoveTo::create(0.2, Vec2(-(this->leftBg->getContentSize().width/2)-Director::getInstance()->getWinSize().width/2,this->leftBg->getPositionY()));//MoveTo::create(0, Vec2(-(this->leftBg->getContentSize().width/2),this->leftBg->getPositionY()));
        this->leftBg->runAction(move);
    }
}
//设置单个item显示信息
void Bag::setItem(PItem item,int index)
{
    //当前模版得数量
    int len = int(this->propItems.size());
//    Widget* rightBg=static_cast<Widget*>(layout->getChildByName("rightBg"));
    ScrollView*scrollView=static_cast<ui::ScrollView*>(this->rightBg->getChildByName("scrollView"));
    
    Widget* newItem = dynamic_cast<Widget*>(this->propItem->clone());
    scrollView->addChild(newItem);
    newItem->setTouchEnabled(true);
    newItem->addTouchEventListener(CC_CALLBACK_2(Bag::itemTouchEvent,this));
    
    Text* propNum = static_cast<Text*>(newItem->getChildByName("propNum"));//数量
    
    //将道具单项存储起来
    this->propItems.pushBack(newItem);
    
    //此处设置tag，用来对应总数据里的序列数
    newItem->setTag(index);
    
    Size sSize=scrollView->getInnerContainerSize();
    Size itemSize=newItem->getContentSize();
    
    float x=(sSize.width/4-itemSize.width)/4.0+sSize.width/4*(len%4)+50;
    float y=sSize.height-(itemSize.height + 15)*(len/4) - 55;
    newItem->setPosition(Vec2(x,y));
    
    //数值转化为字符串
    propNum->setString(Value(item.itemnum()).asString());
    
}
//被选中的道具的具体显示信息
void Bag::setProperty(PItem itemData,bool isHaveData)
{
    this->currentPropNum->setString("");
    this->itemPriceTxt->setString("");
    this->propertyTxt1->setString("");
    this->propertyTxt2->setString("");
    this->propertyTxt3->setString("");
    this->propertyTxt4->setString("");
    this->propertyTxt5->setString("");
    this->desTxt->setString("");
    this->nameTxt->setString("");
    
    //表示当前左侧栏不显示任何道具信息
    if (isHaveData == false) {
        //此处应该还有道具图片以及道具品质颜色框的去掉
        return;
    }else{
        this->itemInfo = itemData;
    }
    this->currentPropNum->setString("拥有"+Value(itemData.itemnum()).asString()+"件");
    XItem*xItem = XItem::record(Value(itemData.itemid()));
    this->itemPriceTxt->setString(Value(xItem->getPrice()).asString());
    this->desTxt->setString(Value(xItem->getDesID()).asString());
//    int aa = xItem->getNameID();
    this->nameTxt->setString(Value(xItem->getNameID()).asString());
    //属性的显示
    vector<string> propertyData;
    if (xItem->getCrh()!= 0) {
        propertyData.push_back("暴击:"+Value(xItem->getCrh()).asString());
    }
    if (xItem->getMiss()!= 0) {
        propertyData.push_back("闪避:"+Value(xItem->getMiss()).asString());
    }
    if (xItem->getDef() != 0) {
        propertyData.push_back("物防:"+Value(xItem->getDef()).asString());
    }
    if (xItem->getMDef() != 0) {
        propertyData.push_back("法防:"+Value(xItem->getMDef()).asString());
    }
    if (xItem->getAtk() != 0) {
        propertyData.push_back("攻击:"+Value(xItem->getAtk()).asString());
    }
    if (xItem->getHp() != 0) {
        propertyData.push_back("生命:"+Value(xItem->getHp()).asString());
    }
    if (xItem->getHeal() != 0) {
        propertyData.push_back("生命恢复速度:"+Value(xItem->getHeal()).asString());
    }
    //为每个属性赋值显示
//    Widget* leftBg=static_cast<Widget*>(layout->getChildByName("leftBg"));

    int propertyLen = fmin(5, propertyData.size());
    for (int i = 0; i<propertyLen; i++) {
        Text* propertyTxt = static_cast<Text*>(this->leftBg->getChildByName("propertyTxt"+Value(i+1).asString()));
        propertyTxt->setString(propertyData.at(i));
    }
    //此处应该设置道具图片以及道具品质颜色框的显示
}

void Bag::setRightPosition()
{
//    Widget* rightBg=static_cast<Widget*>(layout->getChildByName("rightBg"));
//    Widget* leftBg=static_cast<Widget*>(layout->getChildByName("leftBg"));
    if (this->isShow) {
        float x = this->rightBg->getPositionX()-(this->leftBg->getContentSize().width/2+ rightBg->getContentSize().width/2) -3;
        float y =this->leftBg->getPositionY();
        this->leftBg->runAction(
                          MoveTo::create(0.2, Vec2(x,y)));
        
    }else{
        auto move = MoveTo::create(0.2, Vec2(-(this->leftBg->getContentSize().width/2)-Director::getInstance()->getWinSize().width/2,this->leftBg->getPositionY()));
        this->leftBg->runAction(move);
    }
}

void Bag::touchEvent(cocos2d::Ref *pSender, TouchEventType type)
{
    Button* btn=static_cast<Button*>(pSender);
    if(type!=TouchEventType::ENDED){
        return;
    }
    switch (btn->getTag()) {
        case 11221://tab1全部
        {
            //设置按钮选中状态
            tabBar->setIndex(0);
            this->currentType=-1;
            break;
        }
        case 11225://tab2装备
        {
            tabBar->setIndex(1);
            this->currentType = 1;
            break;
        }
        case 11224://tab3卷轴
        {
            tabBar->setIndex(2);
            this->currentType = 2;
            break;
        }
        case 11223://tab4召唤石
        {
            tabBar->setIndex(3);
            this->currentType = 3;
            break;
        }
        case 11222://tab5消耗品
        {
            tabBar->setIndex(4);
            this->currentType = 0;
            break;
        }
        case 11226://返回按钮
        {
            this->clear(true);
            return;
            break;
        }
        default:
            break;
    }
    getTypeInfo(this->currentType);
}

void Bag::itemTouchEvent(Ref *pSender, TouchEventType type)
{
    Widget* item=static_cast<Widget*>(pSender);
    auto itemData = Manager::getInstance()->getRoleData()->itemlist(item->getTag());
    
    this->setProperty(itemData,true);
    if (this->isShow == false) {
        this->isShow = true;
//        Widget* leftBg=static_cast<Widget*>(layout->getChildByName("leftBg"));//
        this->leftBg->setVisible(this->isShow);//
        this->setRightPosition();
    }
}

void Bag::touchButtonEvent(Ref *pSender, TouchEventType type)
{
    auto btn=static_cast<Button*>(pSender);
    if (!btn) {
        return;
    }
    if (type==TouchEventType::ENDED) {
        switch (btn->getTag()) {
            case 11214://出售按钮
            {
                BagSellProp* bagSellProp = BagSellProp::create(this,this->itemInfo);
                bagSellProp->show(this);
            }
                break;
            case 11215://查看按钮
                
                break;
            case 11579://点击后弹出摇钱树窗口
                
                break;
            case 11580://点击后弹出钻石充值窗口
                
                break;
            case 11574://点击后弹出体力购买窗口
                
                break;
            default:
                break;
        }
    }
}

void Bag::sendInfo(int selectNumber)
{
    if (selectNumber == this->itemInfo.itemnum()) {
        this->isNotAllSell = false;
    }else{
        this->isNotAllSell = true;
    }
    PSellGroup pSellGroup;
    pSellGroup.add_itemid(this->itemInfo.itemid());
    pSellGroup.add_itemsellnum(selectNumber);
    Manager::getInstance()->socket->send(C_SELLPROP, &pSellGroup);
}

void Bag::initNetEvent(){
    auto listener = EventListenerCustom::create(NET_MESSAGE, [=](EventCustom* event){
        NetMsg* msg = static_cast<NetMsg*>(event->getUserData());
        switch (msg->msgId)
        {
            case C_COMMONMSG://通用返回消息
            {
                //0：成功 1:物品不足
                PCommonResp commonResp;
                commonResp.ParseFromArray(msg->bytes,msg->len);
                //卖出商品成功(如果出售的数量是总数量，那么isShow＝false)
                if (commonResp.status() == 0) {
                    if (this->isShow && this->isNotAllSell ==false) {
                        this->isShow = this->isNotAllSell;
                        this->setRightPosition();
                    }
                    for (int i = 0; i<Manager::getInstance()->getRoleData()->itemlist_size(); i++) {
                        auto item = Manager::getInstance()->getRoleData()->itemlist(i);
                        if (item.itemid() == this->itemInfo.itemid()) {
                            this->itemInfo = item;
                            this->setProperty(this->itemInfo,true);
                            break;
                        }
                    }
                }//物品不足
                else if(commonResp.status() == 1){
                    
                }
            }
                break;
            case C_UPITEM://更新道具
                this->getTypeInfo(this->currentType);
                break;
            case C_UPROLE:
                this->setPlayerInfo();
                break;
            default:
                break;
        }
    });
    Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener, this);
}

void Bag::onExit()
{
    this->propItem->release();
    this->tabBar->release();
    //停止执行自己定义的函数定时器
    BaseUI::onExit();
}

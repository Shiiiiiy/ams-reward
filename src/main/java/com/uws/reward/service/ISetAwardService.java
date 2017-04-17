package com.uws.reward.service;

import java.util.List;
import java.util.Map;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.sys.model.Dic;

/**
 * @author zhangyb	
 * @version:2015年8月11日 下午1:44:29
 * @Description:评优设置功能
 */
public interface ISetAwardService {

	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 * 评优设置page查询方法
	 */
	public Page queryAwardPage(AwardType award,int pageNo,int pageSize);
	
	/**
	 * @param award
	 * 保存评优设置
	 */
	public void saveAward(AwardType award);
	
	/**
	 * @param award
	 * 更新评优设置
	 */
	public void updateAward(AwardType award);
	
	/**
	 * @param award
	 * 删除评优设置
	 */
	public void delAward(AwardType award);
	
	/**
	 * @param awardId
	 * @return
	 * 通过ID获取Award对象
	 */
	public AwardType getAwardById(String awardId);
	
	/**
	 * @param award
	 * @return
	 * 判断评优类型是否存在
	 */
	public boolean checkAward(AwardType award);
	
	/** 
	* @Title: getPublishAward 
	* @Description:  获取当前学年设置发布的评优评奖信息
	* @param  @param award
	* @param  @return    
	* @return List<AwardType>    
	* @throws 
	*/
	public List<AwardType> getPublishedAward(Dic yearDic,Dic availableDic);
	
	/**
	 * @param awardInfo
	 * @param pageNo
	 * @param pageSize
	 * @return
	 *  查询评奖评优信息
	 */
	public Page queryAwardInfoPage(AwardInfo awardInfo,int pageNo,int pageSize);
	
	/**
	 * @param awardInfo
	 *  保存评奖评优信息
	 */
	public void saveAwardInfo(AwardInfo awardInfo);
	
	/**
	 * @param awardInfo
	 *  更新评奖评优信息
	 */
	public void updateAwardInfo(AwardInfo awardInfo);
	
	/**
	 * @param awardInfo
	 *  删除评奖评优信息
	 */
	public void deleteAwardInfo(AwardInfo awardInfo);
	
	/**
	 * @param id
	 * @return
	 *  通过ID获取评奖评优信息
	 */
	public AwardInfo getAwardInfoById(String id);
	
	/**
	 * @param name
	 * @return
	 *  通过名称获取评奖评优信息list
	 */
	public List<AwardInfo> getAwardInfoListByName(String name);
	
	/**
	 * @param typeCode
	 * @return
	 *  通过奖优类型获取奖优list
	 */
	public List<AwardInfo> getAwardInfoListByType(String typeCode);
	
	/**
	 * @param condition
	 *  保存条件信息
	 */
	public void saveCondition(AwardCondition condition,String[] fileId);
	
	/**
	 * @param condition
	 *  修改条件信息
	 */
	public void updateCondition(AwardCondition condition,String[] fileId);
	
	/**
	 * @param condition
	 *  删除条件信息
	 */
	public void delCondition(AwardCondition condition);
	
	/**
	 * @param awardId
	 * @return
	 *  通过awardId获取评优条件信息
	 */
	public AwardCondition getConByAwardId(String awardId);
	
	/**
	 * @param conditionInfo
	 *  保存条件明细信息
	 */
	public void saveConditionInfo(ConditionInfo conditionInfo);
	
	/**
	 * @param conditionInfo
	 *  修改条件明细信息
	 */
	public void updateConditionInfo(ConditionInfo conditionInfo);
	
	/**
	 * @param conditionInfo
	 *  删除条件明细信息
	 */
	public void delConditionInfo(ConditionInfo conditionInfo);
	
	/**
	 * @param conditionId
	 * @return
	 *  通过条件ID获取条件明细LIST
	 */
	public List<ConditionInfo> getConInfoListByConId(String conditionId);
	
	/**
	 * @param quotaInfo
	 *  保存人员限额信息
	 */
	public void saveQuotaInfo(QuotaInfo quotaInfo);
	
	/**
	 * @param quotaInfo
	 *  修改人员限额信息
	 */
	public void updateQuotaInfo(QuotaInfo quotaInfo);
	
	/**
	 * @param quotaInfo
	 *  删除人员限额信息
	 */
	public void delQuotaInfo(QuotaInfo quotaInfo);
	
	/**
	 * @param conditionId
	 * @return
	 *  通过条件ID获取人员限额明细LIST
	 */
	public List<QuotaInfo> getQuotaInfoListByConId(String conditionId);
	
	/**
	 * @param award
	 * @param infoList
	 * @param quotaList
	 *  删除评奖评优设置信息
	 */
	public void deleteAward(AwardType award,AwardCondition condition,List<ConditionInfo> infoList,List<QuotaInfo> quotaList);
	
	/**
	 * @param conInfoList
	 *  删除评奖评优条件
	 */
	public void deleConInfoList(List<ConditionInfo> conInfoList);
	
	/** 
	* @Title: getAwardTypeByName 
	* @Description:  通过学年和名称获取评优评奖类型
	* @param  @param year
	* @param  @param awardName
	* @param  @return    
	* @return AwardType    
	* @throws 
	*/
	public AwardType getAwardTypeByName(Dic year,String awardCode,String secondAwardName);
	
	/** 
	* @Title: getAwardTypeList 
	* @Description:  获取指定条件的awardType
	* @param  @param year
	* @param  @param awardName
	* @param  @param secondAwardName
	* @param  @return    
	* @return List<AwardType>    
	* @throws 
	*/
	public List<AwardType> getAwardTypeList(Dic year,String awardName,String secondAwardName);
	
	/** 
	* @Title: getMaxAwardCode 
	* @Description:  获取最大的奖项编码
	* @param  @return    
	* @return int    
	* @throws 
	*/
	public int getMaxAwardCode();
	
	/** 
	* @Title: getAwardByCode 
	* @Description:  通过code获取awardInfo
	* @param  @param awardCode
	* @param  @return    
	* @return AwardType    
	* @throws 
	*/
	public AwardInfo getAwardInfoByCode(String awardCode);
	
	/** 
	* @Title: getAwardTypeListByInfoId 
	* @Description:  通过InfoId获取与之关联的awardTypeList
	* @param  @param infoId
	* @param  @return    
	* @return List<AwardType>    
	* @throws 
	*/
	public List<AwardType> getAwardTypeListByInfoId(String infoId);
	
	/** 
	* @Title: getAvailableAwardTypeCode 
	* @Description:  返回适合的评优评奖类型编码
	* @param  @param awardType
	* @param  @return    
	* @return String    
	* @throws 
	*/
	public String getMaxAwardTypeCode(Dic year);
	
	/** 
	* @Title: getAwardTypeByCode 
	* @Description:  通过评奖评优编码获取awardType
	* @param  @param awardTypeCode
	* @param  @return    
	* @return AwardType    
	* @throws 
	*/
	public AwardType getAwardTypeByCode(String awardTypeCode);
	/** 
	* @Title: getCheckConInfoList 
	* @Description: 获取可比较的infoList
	* @param  @param conditionId
	* @param  @return    
	* @return List<ConditionInfo>    
	* @throws 
	*/
	public List<ConditionInfo> getCheckConInfoList(String conditionId);
	
	/** 
	* @Title: checkStuApplyPermission 
	* @Description: 验证学生申请权限 
	* @param  @param StuId
	* @param  @param YearDic
	* @param  @param infoList
	* @param  @param map
	* @param  @return    
	* @return boolean    
	* @throws 
	*/
	public boolean checkStuApplyPermission(String StuId,Dic YearDic,List<ConditionInfo> infoList,Map<String,String> map);
	
	/** 
	* @Title: removeObjFromSession 
	* @Description: 从session一级缓存中删除对象
	* @param  @param obj    
	* @return void    
	* @throws 
	*/
	public void removeObjFromSession(Object obj);
}

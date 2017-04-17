package com.uws.reward.dao;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.sys.model.Dic;

/**
 * @author zhangyb
 * @version:2015年8月11日 下午1:54:10
 * @Description:评优设置Dao
 */
public interface ISetAwardDao {

	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  查询评优设置page
	 */
	public Page queryAwardPage(AwardType award,int pageNo,int pageSize);
	
	/**
	 * @param award
	 *  保存Award
	 */
	public void saveAward(AwardType award);
	
	/**
	 * @param award
	 *  更新Award
	 */
	public void updateAward(AwardType award);
	
	/**
	 * @param award
	 *  删除Award
	 */
	public void delAward(AwardType award);
	
	/**
	 * @param awardId
	 * @return
	 *  通过ID获取Award
	 */
	public AwardType getAwardById(String awardId);
	
	/** 
	* @Title: getAwardByCode 
	* @Description:  通过code获取awardType
	* @param  @param awardCode
	* @param  @return    
	* @return AwardType    
	* @throws 
	*/
	public AwardInfo getAwardInfoByCode(String awardCode);
	
	/**
	 * @param award
	 * @return
	 *  判断评优类型是否存在
	 */
	public boolean checkAward(AwardType award);
	
	/** 
	* @Title: getPublishedAwardType 
	* @Description:  获取已发布的评优评奖设置
	* @param  @param award
	* @param  @return    
	* @return List<AwardType>    
	* @throws 
	*/
	public List<AwardType> getPublishedAwardType(Dic yearDic,Dic availableDic);
	
	/**
	 * @param awardInfo
	 * @param pageNo
	 * @param pageSize
	 * @return
	 *  评奖评优信息
	 */
	public Page queryAwardInfoPage(AwardInfo awardInfo,int pageNo,int pageSize);
	
	/**
	 * @param awardInfo
	 *  保存评奖评优信息
	 */
	public void saveAwardInfo(AwardInfo awardInfo);
	
	/**
	 * @param awardInfo
	 *  修改评奖评优信息
	 */
	public void updateAwardInfo(AwardInfo awardInfo);
	
	/**
	 * @param awardInfo
	 *  删除评奖评优信息
	 */
	public void delteAwardInfo(AwardInfo awardInfo);
	
	/**
	 * @param id
	 * @return
	 *  通过Id获取评奖评优信息
	 */
	public AwardInfo getAwardInfoById(String id);
	
	/**
	 * @param name
	 * @return
	 *  通过name获取评奖评优list
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
	 * @return
	 *  保存新增的条件信息
	 */
	public void saveAwardCondition(AwardCondition condition);
	
	/**
	 * @param condition
	 *  更新条件信息
	 */
	public void updateAwardCondition(AwardCondition condition);
	
	/**
	 * @param condition
	 *  删除条件信息
	 */
	public void delAwardCondition(AwardCondition condition);
	
	/**
	 * @param AwardId
	 * @return
	 *  通过评优类型ID获取评优条件
	 */
	public AwardCondition getConByAwardId(String awardId);
	
	/**
	 * @param conditionInfo
	 *  保存条件明细信息
	 */
	public void saveConditionInfo(ConditionInfo conditionInfo);
	
	/**
	 * @param conditionInfo
	 *  更新条件明细信息
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
	 *  通过条件ID获取条件明细信息list
	 */
	public List<ConditionInfo> getConInfoListByConId(String conditionId);
	
	/**
	 * @param quotaInfo
	 *  保存人员限额信息
	 */
	public void saveQuotaInfo(QuotaInfo quotaInfo);
	
	/**
	 * @param quotaInfo
	 *  更新人员限额信息
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
	 *  通过条件ID获取人员限额明细信息LIST
	 */
	public List<QuotaInfo> getQuotaInfoByConId(String conditionId);
	
	/** 
	* @Title: getAwardTypeByName 
	* @Description:  通过学年和名称获取发布的评优评奖类型
	* @param  @param year
	* @param  @param awardName
	* @param  @return    
	* @return AwardType    
	* @throws 
	*/
	public AwardType getAwardTypeByName(Dic year,String awardCode,String secondAwardName);
	
	/** 
	* @Title: getAwardTypeList 
	* @Description:  获取AwardType  List
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
	* @Title: getAwardInfoListById 
	* @Description:  通过awardInfoId获取引用的awardTypeList
	* @param  @param infoId
	* @param  @return    
	* @return List<AwardType>    
	* @throws 
	*/
	public List<AwardType> getAwardTypeListById(String infoId);
	
	/** 
	* @Title: getMaxAwardTypeCode 
	* @Description:  获取当前年最大的评奖类型编码
	* @param  @param year
	* @param  @return    
	* @return String    
	* @throws 
	*/
	public String getMaxAwardTypeCode(Dic year);
	
	/** 
	* @Title: getAwardTypeByCode 
	* @Description:  
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
	* @Title: removeObjFromSession 
	* @Description: 从session一级缓存中删除对象
	* @param  @param obj    
	* @return void    
	* @throws 
	*/
	public void removeObjFromSession(Object obj);
	
	/***
	 * 查询学院的评奖名额
	 * @param conditionId
	 * @param collegeId
	 * @return
	 */
	public QuotaInfo getCollegeQuotaInfoByConId(String conditionId, String collegeId);
	
	/***
	 * 查询班级人数、专业人数
	 * @param studentId
	 * @return
	 */
	public List queryMajorClassCount(String studentId);
}

package com.uws.reward.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IScoreService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.reward.dao.ISetAwardDao;
import com.uws.reward.service.ISetAwardService;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;

/**
 * @author zhangyb
 * @version:2015年8月11日 下午1:52:21
 * @Description:评优设置ServiceImpl
 */
@Service("setAwardService")
public class SetAwardServiceImpl extends BaseServiceImpl implements
		ISetAwardService {

	@Autowired
	private ISetAwardDao setAwardDao;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IScoreService scoreService;
	@Autowired
	private ICompService compService;
	
	@Override
	public Page queryAwardPage(AwardType award,int pageNo,int pageSize) {
		//  Auto-generated method stub
		return setAwardDao.queryAwardPage(award, pageNo, pageSize);
	}

	@Override
	public void saveAward(AwardType award) {
		//  Auto-generated method stub
		setAwardDao.saveAward(award);
	}

	@Override
	public void updateAward(AwardType award) {
		//  Auto-generated method stub
		setAwardDao.updateAward(award);
	}

	@Override
	public void delAward(AwardType award) {
		//  Auto-generated method stub
		setAwardDao.delAward(award);
	}

	@Override
	public AwardType getAwardById(String awardId) {
		//  Auto-generated method stub
		return setAwardDao.getAwardById(awardId);
	}

	@Override
	public boolean checkAward(AwardType award) {
		//  Auto-generated method stub
		return setAwardDao.checkAward(award);
	}

	@Override
	public Page queryAwardInfoPage(AwardInfo awardInfo, int pageNo, int pageSize) {
		//  Auto-generated method stub
		return this.setAwardDao.queryAwardInfoPage(awardInfo, pageNo, pageSize);
	}

	@Override
	public void saveAwardInfo(AwardInfo awardInfo) {
		//  Auto-generated method stub
		this.setAwardDao.saveAwardInfo(awardInfo);
	}

	@Override
	public void updateAwardInfo(AwardInfo awardInfo) {
		//  Auto-generated method stub
		this.setAwardDao.updateAwardInfo(awardInfo);
	}

	@Override
	public void deleteAwardInfo(AwardInfo awardInfo) {
		//  Auto-generated method stub
		this.setAwardDao.delteAwardInfo(awardInfo);
	}

	@Override
	public AwardInfo getAwardInfoById(String id) {
		//  Auto-generated method stub
		return this.setAwardDao.getAwardInfoById(id);
	}

	@Override
	public List<AwardInfo> getAwardInfoListByName(String name) {
		//  Auto-generated method stub
		return this.setAwardDao.getAwardInfoListByName(name);
	}

	@Override
	public List<AwardInfo> getAwardInfoListByType(String typeCode) {
		//  Auto-generated method stub
		return this.setAwardDao.getAwardInfoListByType(typeCode);
	}

	@Override
	public void saveCondition(AwardCondition condition,String[] fileId) {
		//  Auto-generated method stub
		this.setAwardDao.saveAwardCondition(condition);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(condition.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, condition.getId());
		  }
	}

	@Override
	public void updateCondition(AwardCondition condition,String[] fileId) {
		//  Auto-generated method stub
		this.setAwardDao.updateAwardCondition(condition);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(condition.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, condition.getId());
		  }
	}

	@Override
	public void delCondition(AwardCondition condition) {
		//  Auto-generated method stub
		this.setAwardDao.delAwardCondition(condition);
	}

	@Override
	public void saveConditionInfo(ConditionInfo conditionInfo) {
		//  Auto-generated method stub
		this.setAwardDao.saveConditionInfo(conditionInfo);
	}

	@Override
	public void updateConditionInfo(ConditionInfo conditionInfo) {
		//  Auto-generated method stub
		this.setAwardDao.updateConditionInfo(conditionInfo);
	}

	@Override
	public void delConditionInfo(ConditionInfo conditionInfo) {
		//  Auto-generated method stub
		this.setAwardDao.delConditionInfo(conditionInfo);
	}

	@Override
	public List<ConditionInfo> getConInfoListByConId(String conditionId) {
		//  Auto-generated method stub
		return this.setAwardDao.getConInfoListByConId(conditionId);
	}

	@Override
	public void saveQuotaInfo(QuotaInfo quotaInfo) {
		//  Auto-generated method stub
		this.setAwardDao.saveQuotaInfo(quotaInfo);
	}

	@Override
	public void updateQuotaInfo(QuotaInfo quotaInfo) {
		//  Auto-generated method stub
		this.setAwardDao.updateQuotaInfo(quotaInfo);
	}

	@Override
	public void delQuotaInfo(QuotaInfo quotaInfo) {
		//  Auto-generated method stub
		this.setAwardDao.delQuotaInfo(quotaInfo);
	}

	@Override
	public List<QuotaInfo> getQuotaInfoListByConId(String conditionId) {
		//  Auto-generated method stub
		return this.setAwardDao.getQuotaInfoByConId(conditionId);
	}

	@Override
	public AwardCondition getConByAwardId(String awardId) {
		//  Auto-generated method stub
		return this.setAwardDao.getConByAwardId(awardId);
	}

	@Override
	public void deleteAward(AwardType award,AwardCondition condition, List<ConditionInfo> infoList,
			List<QuotaInfo> quotaList) {
		//  Auto-generated method stub
		if(infoList.size() > 0) {
			for(ConditionInfo info : infoList) {
				this.setAwardDao.delConditionInfo(info);
			}
		}
		if(quotaList.size() > 0) {
			for(QuotaInfo quota : quotaList) {
				this.setAwardDao.delQuotaInfo(quota);
			}
		}
		this.setAwardDao.delAwardCondition(condition);
		this.setAwardDao.delAward(award);
	}

	@Override
	public void deleConInfoList(List<ConditionInfo> conInfoList) {
		//  Auto-generated method stub
		if(conInfoList.size() > 0) {
			for(ConditionInfo info : conInfoList) {
				this.setAwardDao.delConditionInfo(info);
			}
		}
	}

	@Override
	public List<AwardType> getPublishedAward(Dic yearDic,Dic availableDic) {
		//  Auto-generated method stub
		return this.setAwardDao.getPublishedAwardType(yearDic,availableDic);
	}

	@Override
	public AwardType getAwardTypeByName(Dic year, String awardCode,String secondAwardName) {
		//  Auto-generated method stub
		return this.setAwardDao.getAwardTypeByName(year, awardCode,secondAwardName);
	}

	@Override
	public List<AwardType> getAwardTypeList(Dic year, String awardName,
			String secondAwardName) {
		//  Auto-generated method stub
		return this.setAwardDao.getAwardTypeList(year,awardName,secondAwardName);
	}

	/* (非 Javadoc) 
	* <p>Title: getMaxAwardCode</p> 
	* <p>Description: </p> 
	* @return 
	* @see com.uws.reward.service.ISetAwardService#getMaxAwardCode() 
	*/
	@Override
	public int getMaxAwardCode() {
		return this.setAwardDao.getMaxAwardCode();
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardByCode</p> 
	* <p>Description: </p> 
	* @param awardCode
	* @return 
	* @see com.uws.reward.service.ISetAwardService#getAwardByCode(java.lang.String) 
	*/
	@Override
	public AwardInfo getAwardInfoByCode(String awardCode) {
		return this.setAwardDao.getAwardInfoByCode(awardCode);
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardTypeListByInfoId</p> 
	* <p>Description: </p> 
	* @param infoId
	* @return 
	* @see com.uws.reward.service.ISetAwardService#getAwardTypeListByInfoId(java.lang.String) 
	*/
	@Override
	public List<AwardType> getAwardTypeListByInfoId(String infoId) {
	
		return this.setAwardDao.getAwardTypeListById(infoId);
	}

	/* (非 Javadoc) 
	* <p>Title: getAvailableAwardTypeCode</p> 
	* <p>Description: </p> 
	* @param awardType
	* @return 
	* @see com.uws.reward.service.ISetAwardService#getAvailableAwardTypeCode(com.uws.domain.reward.AwardType) 
	*/
	@Override
	public String getMaxAwardTypeCode(Dic year) {
		
		return this.setAwardDao.getMaxAwardTypeCode(year);
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardTypeByCode</p> 
	* <p>Description: </p> 
	* @param year
	* @param awardTypeCode
	* @return 
	* @see com.uws.reward.service.ISetAwardService#getAwardTypeByCode(com.uws.sys.model.Dic, java.lang.String) 
	*/
	@Override
	public AwardType getAwardTypeByCode(String awardTypeCode) {
		return this.setAwardDao.getAwardTypeByCode(awardTypeCode);
	}

	/* (非 Javadoc) 
	* <p>Title: getCheckConInfoList</p> 
	* <p>Description: </p> 
	* @param conditionId
	* @return 
	* @see com.uws.reward.service.ISetAwardService#getCheckConInfoList(java.lang.String) 
	*/
	@Override
	public List<ConditionInfo> getCheckConInfoList(String conditionId) {
		return this.setAwardDao.getCheckConInfoList(conditionId);
	}

	/* (非 Javadoc) 
	* <p>Title: checkStuApplyPermission</p> 
	* <p>Description: </p> 
	* @param StuId
	* @param YearDic
	* @param infoList
	* @param map
	* @return 
	* @see com.uws.reward.service.ISetAwardService#checkStuApplyPermission(java.lang.String, com.uws.sys.model.Dic, java.util.List, java.util.Map) 
	*/
	@Override
	public boolean checkStuApplyPermission(String stuId, Dic yearDic,
			List<ConditionInfo> infoList, Map<String, String> evaluationMap) {
		boolean returnFlag = true;
		int majorCount = 0;//专业人数
		int classCount = 0;//班级人数
		int rank = 0;//条件设置默认值
		List maxList = this.setAwardDao.queryMajorClassCount(stuId);
		if(maxList != null && maxList.size()>1){
			majorCount = Integer.parseInt( maxList.get(0).toString());
			classCount = Integer.parseInt( maxList.get(1).toString());
		}
		for(ConditionInfo info : infoList) {
			if(info.getCheckOrNot().equals("Y")){
				if(info.getCompareMethod().equals(">=")) {
					if(evaluationMap.containsKey(info.getConditionName())) {
						rank = Integer.parseInt(info.getConditionValue());
						if(StringUtils.hasText(info.getConditionName())){
							if(info.getConditionName().contains("MajorRank")){
								rank= (int) (Math.ceil(Integer.parseInt(info.getConditionValue()) * majorCount * 0.01));
							}else if(info.getConditionName().contains("ClassRank")){
								rank = (int) (Math.ceil(Integer.parseInt(info.getConditionValue()) * classCount * 0.01));
							}
						}
						if(DataUtil.isNotNull(evaluationMap.get(info.getConditionName())) && 
								Integer.parseInt(evaluationMap.get(info.getConditionName())) >= rank) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.averageScoreOfYear)){
						String value = this.scoreService.queryYearAvgScore(stuId, yearDic);
						//value.contains("没有成绩") 判断是不是存在没有成绩的学期，若存在不符合条件
						if(DataUtil.isNotNull(value) && !value.contains("没有成绩")
								&& Double.parseDouble(value) >= Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.singleScore)) {
						String value = this.scoreService.queryStudentLowScore(stuId, yearDic, null);
						if(DataUtil.isNotNull(value) && 
								Double.parseDouble(value) >= Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.sportScore)) {
						String value = this.scoreService.queryStudentSportScore(stuId, yearDic.getCode());
						if(DataUtil.isNotNull(value) && 
								Double.parseDouble(value) >= Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}
				}else if(info.getCompareMethod().equals("<=")) {
					if(evaluationMap.containsKey(info.getConditionName())) {
						rank = Integer.parseInt(info.getConditionValue());
						if(StringUtils.hasText(info.getConditionName())){
							if(info.getConditionName().contains("MajorRank")){
								rank= (int) (Math.ceil(Integer.parseInt(info.getConditionValue()) * majorCount * 0.01));
							}else if(info.getConditionName().contains("ClassRank")){
								rank = (int) (Math.ceil(Integer.parseInt(info.getConditionValue()) * classCount * 0.01));
							}
						}
						if(DataUtil.isNotNull(evaluationMap.get(info.getConditionName())) && 
								Integer.parseInt(evaluationMap.get(info.getConditionName())) <= rank) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.averageScoreOfYear)){
						String value = this.scoreService.queryYearAvgScore(stuId, yearDic);
						//value.contains("没有成绩") 判断是不是存在没有成绩的学期，若存在不符合条件
						if(DataUtil.isNotNull(value) && !value.contains("没有成绩")
								&& Double.parseDouble(value) <= Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.singleScore)) {
						String value = this.scoreService.queryStudentLowScore(stuId, yearDic, null);
						if(DataUtil.isNotNull(value) && 
								Double.parseDouble(value) <= Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.sportScore)) {
						String value = this.scoreService.queryStudentSportScore(stuId, yearDic.getCode());
						if(DataUtil.isNotNull(value) && 
								Double.parseDouble(value) <= Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}
				}else if(info.getCompareMethod().equals("=")) {
					if(evaluationMap.containsKey(info.getConditionName())) {
						rank = Integer.parseInt(info.getConditionValue());
						if(StringUtils.hasText(info.getConditionName())){
							if(info.getConditionName().contains("MajorRank")){
								rank= (int) (Math.ceil(Integer.parseInt(info.getConditionValue()) * majorCount * 0.01));
							}else if(info.getConditionName().contains("ClassRank")){
								rank = (int) (Math.ceil(Integer.parseInt(info.getConditionValue()) * classCount * 0.01));
							}
						}
						if(DataUtil.isNotNull(evaluationMap.get(info.getConditionName())) && 
								Integer.parseInt(evaluationMap.get(info.getConditionName())) == rank) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.averageScoreOfYear)){
						String value = this.scoreService.queryYearAvgScore(stuId, yearDic);
						//value.contains("没有成绩") 判断是不是存在没有成绩的学期，若存在不符合条件
						if(DataUtil.isNotNull(value) && !value.contains("没有成绩")
								&& Double.parseDouble(value) == Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.singleScore)) {
						String value = this.scoreService.queryStudentLowScore(stuId, yearDic, null);
						if(DataUtil.isNotNull(value) && 
								Double.parseDouble(value) == Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.sportScore)) {
						String value = this.scoreService.queryStudentSportScore(stuId, yearDic.getCode());
						if(DataUtil.isNotNull(value) && 
								Double.parseDouble(value) == Double.parseDouble(info.getConditionValue())) {
						}else{
							returnFlag = false;
							return returnFlag;
						}
					}else if(info.getConditionName().equals(RewardConstant.isPassDorm)){
						boolean checkDormIsGood = compService.checkDormIsGood(stuId);
						if(info.getConditionValue().equals("N")){//点击否的话是不需要合格寝室
							return returnFlag = true;
						}else{
							if(checkDormIsGood){
								return returnFlag = true;
							}else{
								returnFlag = false;
								return returnFlag;
							}
						}
					}else if(info.getConditionName().equals(RewardConstant.coursesPass)){
						boolean flag = this.scoreService.checkCoursesPass(stuId, yearDic.getCode());
						if(info.getConditionValue().equals("N")){//点击否的话是不需要合格寝室
							return returnFlag = true;
						}else{
							if(flag){
								return returnFlag = true;
							}else{
								returnFlag = false;
								return returnFlag;
							}
						}
					}
				}
			}
		}
		return returnFlag;
	}

	/* (非 Javadoc) 
	* <p>Title: removeObjFromSession</p> 
	* <p>Description: </p> 
	* @param obj 
	* @see com.uws.reward.service.ISetAwardService#removeObjFromSession(java.lang.Object) 
	*/
	@Override
	public void removeObjFromSession(Object obj) {
		this.setAwardDao.removeObjFromSession(obj);
	}

}

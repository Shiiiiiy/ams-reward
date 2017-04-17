package com.uws.reward.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.reward.dao.ISetAwardDao;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.dao.IDicDao;
import com.uws.sys.model.Dic;

/**
 * @author zhangyb
 * @version:2015年8月11日 下午2:00:40
 * @Description:评优设置daoImpl
 *
 */
@Repository("setAwardDao")
public class SetAwardDaoImpl extends BaseDaoImpl implements ISetAwardDao {
	
	@Autowired
	private IDicDao dicDao;
	
	@Override
	public Page queryAwardPage(AwardType award,int pageNo,int pageSize) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from AwardType a where 1=1");
//		学年
		if(DataUtil.isNotNull(award.getSchoolYear()) && DataUtil.isNotNull(award.getSchoolYear().getId())) {
			hql.append(" and a.schoolYear.id = ?");
			values.add(award.getSchoolYear().getId());
		}
//		评奖评优类型
		if(DataUtil.isNotNull(award.getAwardType()) && DataUtil.isNotNull(award.getAwardType().getCode())) {
			hql.append(" and a.awardType.code = ?");
			values.add(award.getAwardType().getCode());
		}
//		状态
		if(DataUtil.isNotNull(award.getAwardStatus()) && DataUtil.isNotNull(award.getAwardStatus().getCode())) {
			hql.append(" and a.awardStatus.code = ?");
			values.add(award.getAwardStatus().getCode());
		}
//		名称
		if(DataUtil.isNotNull(award.getAwardInfoId()) && DataUtil.isNotNull(award.getAwardInfoId().getAwardName())) {
			hql.append(" and a.awardInfoId.awardName like ?");
			values.add("%" + HqlEscapeUtil.escape(award.getAwardInfoId().getAwardName()) + "%");
		}
//		适用对象
		if(DataUtil.isNotNull(award.getAwardInfoId()) && DataUtil.isNotNull(award.getAwardInfoId().getAvailableObject())
				&& DataUtil.isNotNull(award.getAwardInfoId().getAvailableObject().getCode())) {
			hql.append(" and a.awardInfoId.availableObject.code = ?");
			values.add(award.getAwardInfoId().getAvailableObject().getCode());
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void saveAward(AwardType award) {
		//  Auto-generated method stub
		save(award);
	}

	@Override
	public void updateAward(AwardType award) {
		//  Auto-generated method stub
		update(award);
	}

	@Override
	public void delAward(AwardType award) {
		//  Auto-generated method stub
		delete(award);
	}

	@Override
	public AwardType getAwardById(String awardId) {
		//  Auto-generated method stub
		String hql = " from AwardType a where a.id = ?";
		return (AwardType) queryUnique(hql,new Object[]{awardId});
	}

	@Override
	public boolean checkAward(AwardType award) {
		//  Auto-generated method stub
		boolean result = false;
		String hql = " from AwardType a where a.schoolYear.code = ? and a.awardInfoId.id = ?";
		AwardType awardType = (AwardType) this.queryUnique(hql, new Object[]{award.getSchoolYear().getCode(),award.getAwardInfoId().getId()});
		if(DataUtil.isNotNull(awardType)) {
			result = true;
		}
		return result;
	}

	@Override
	public Page queryAwardInfoPage(AwardInfo awardInfo, int pageNo, int pageSize) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from AwardInfo a where 1=1");
		if(DataUtil.isNotNull(awardInfo.getAwardName())) {
			hql.append(" and a.awardName like ?");
			values.add("%" + HqlEscapeUtil.escape(awardInfo.getAwardName()) + "%");
		}
		if(awardInfo.getAwardType() != null && DataUtil.isNotNull(awardInfo.getAwardType().getCode())) {
			hql.append(" and a.awardType.code = ?");
			values.add(awardInfo.getAwardType().getCode());
		}
		if(awardInfo.getAwardStatus() != null && DataUtil.isNotNull(awardInfo.getAwardStatus().getCode())) {
			hql.append(" and a.awardStatus.code = ?");
			values.add(awardInfo.getAwardStatus().getCode());
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void saveAwardInfo(AwardInfo awardInfo) {
		//  Auto-generated method stub
		save(awardInfo);
	}

	@Override
	public void updateAwardInfo(AwardInfo awardInfo) {
		//  Auto-generated method stub
		update(awardInfo);
	}

	@Override
	public void delteAwardInfo(AwardInfo awardInfo) {
		//  Auto-generated method stub
		delete(awardInfo);
	}

	@Override
	public AwardInfo getAwardInfoById(String id) {
		//  Auto-generated method stub
		String hql = " from AwardInfo a where a.id = ?";
		return (AwardInfo) queryUnique(hql,new Object[]{id});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AwardInfo> getAwardInfoListByName(String name) {
		//  Auto-generated method stub
		String hql = " from AwardInfo a where a.awardName = ?";
		return this.query(hql, new Object[]{name});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AwardInfo> getAwardInfoListByType(String typeCode) {
		//  Auto-generated method stub
		String hql = " from AwardInfo a where a.awardType.code = ? and a.awardStatus.code = 'ENABLE'";
		return this.query(hql, new Object[]{typeCode});
	}

	@Override
	public void saveAwardCondition(AwardCondition condition) {
		//  Auto-generated method stub
		save(condition);
	}

	@Override
	public void updateAwardCondition(AwardCondition condition) {
		//  Auto-generated method stub
		update(condition);
	}

	@Override
	public void delAwardCondition(AwardCondition condition) {
		//  Auto-generated method stub
		delete(condition);
	}

	@Override
	public void saveConditionInfo(ConditionInfo conditionInfo) {
		//  Auto-generated method stub
		save(conditionInfo);
	}

	@Override
	public void updateConditionInfo(ConditionInfo conditionInfo) {
		//  Auto-generated method stub
		update(conditionInfo);
	}

	@Override
	public void delConditionInfo(ConditionInfo conditionInfo) {
		//  Auto-generated method stub
		delete(conditionInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ConditionInfo> getConInfoListByConId(String conditionId) {
		//  Auto-generated method stub
		String hql = " from ConditionInfo c where c.conditionId.id = ?";
		return query(hql,new Object[]{conditionId});
	}

	@Override
	public void saveQuotaInfo(QuotaInfo quotaInfo) {
		//  Auto-generated method stub
		save(quotaInfo);
	}

	@Override
	public void updateQuotaInfo(QuotaInfo quotaInfo) {
		//  Auto-generated method stub
		update(quotaInfo);
	}

	@Override
	public void delQuotaInfo(QuotaInfo quotaInfo) {
		//  Auto-generated method stub
		delete(quotaInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QuotaInfo> getQuotaInfoByConId(String conditionId) {
		//  Auto-generated method stub
		String hql = " from QuotaInfo q where q.conditionId.id = ? order by q.academyId.code desc";
		return query(hql,new Object[]{conditionId});
	}

	@Override
	public AwardCondition getConByAwardId(String awardId) {
		//  Auto-generated method stub
		String hql = " from AwardCondition c where c.awardId.id = ?";
		return (AwardCondition) this.queryUnique(hql, new Object[]{awardId});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AwardType> getPublishedAwardType(Dic yearDic,Dic availableDic) {
		//  Auto-generated method stub
		StringBuffer hql = new StringBuffer(" from AwardType a where a.awardStatus.code = 'PUBLISHED'");
		List<String> values = new ArrayList<String>();
		if(yearDic != null && DataUtil.isNotNull(yearDic.getCode())) {
			hql.append(" and a.schoolYear.code = ?");
			values.add(yearDic.getCode());
		}
//		适用对象 班级/学生
		if(availableDic != null && DataUtil.isNotNull(availableDic.getCode())) {
			hql.append(" and a.awardInfoId.availableObject.code = ?");
			values.add(availableDic.getCode());
		}
		return this.query(hql.toString(), values.toArray());
	}

	@Override
	public AwardType getAwardTypeByName(Dic year, String awardCode,String secondAwardName) {
		//  Auto-generated method stub
		StringBuffer hql = new StringBuffer(" from AwardType a where a.schoolYear.code = ? and a.awardStatus.code = 'PUBLISHED'"
				+ "and a.awardInfoId.awardCode = ?");
		List<Object> values = new ArrayList<Object>();
		values.add(year.getCode());
		values.add(Long.parseLong(awardCode));
		if(DataUtil.isNotNull(secondAwardName) && awardCode.equals(RewardConstant.XINGZHI)) {
			hql.append(" and a.secondAwardName.code = ?");
			values.add(secondAwardName);
		}
		return (AwardType) this.queryUnique(hql.toString(), values.toArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AwardType> getAwardTypeList(Dic year, String awardName,
			String secondAwardName) {
		//  Auto-generated method stub
		StringBuffer hql = new StringBuffer(" from AwardType a where 1 = 1");
		List<String> values = new ArrayList<String>();
		if(DataUtil.isNotNull(year.getCode())) {
			hql.append(" and a.schoolYear.code = ?");
			values.add(year.getCode());
		}
		if(DataUtil.isNotNull(awardName)) {
			hql.append(" and a.awardInfoId.id = ?");
			values.add(awardName);
		}
		if(DataUtil.isNotNull(secondAwardName)) {
			hql.append(" and a.secondAwardName.code = ?");
			values.add(secondAwardName);
		}
		return this.query(hql.toString(), values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: getMaxAwardCode</p> 
	* <p>Description: </p> 
	* @return 
	* @see com.uws.reward.dao.ISetAwardDao#getMaxAwardCode() 
	*/
	@Override
	public int getMaxAwardCode() {
		//  Auto-generated method stub
		String sql = "select max(awardCode) from AwardInfo";
		Long a = (Long) this.queryUnique(sql, new Object[]{});
		int b = 0;
		if(a != null) {
			b = a.intValue();
		}
		return b > 0 ? b : 0;
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardByCode</p> 
	* <p>Description: </p> 
	* @param awardCode
	* @return 
	* @see com.uws.reward.dao.ISetAwardDao#getAwardByCode(java.lang.String) 
	*/
	@Override
	public AwardInfo getAwardInfoByCode(String awardCode) {
		//  Auto-generated method stub
		String hql = " from AwardInfo a where a.awardCode = ?";
		long a = Long.parseLong(awardCode);
		return (AwardInfo) queryUnique(hql,new Object[]{a});
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardTypeListById</p> 
	* <p>Description: </p> 
	* @param infoId
	* @return 
	* @see com.uws.reward.dao.ISetAwardDao#getAwardTypeListById(java.lang.String) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<AwardType> getAwardTypeListById(String infoId) {
		
		String hql = " from AwardType a where a.awardInfoId.id = ?";
		return this.query(hql, new Object[]{infoId});
	}

	/* (非 Javadoc) 
	* <p>Title: getMaxAwardTypeCode</p> 
	* <p>Description: </p> 
	* @param year
	* @return 
	* @see com.uws.reward.dao.ISetAwardDao#getMaxAwardTypeCode(com.uws.sys.model.Dic) 
	*/
	@Override
	public String getMaxAwardTypeCode(Dic year) {
		String hql = " select max(a.awardTypeCode) from AwardType a where a.schoolYear.code = ?";
		String code = (String) this.queryUnique(hql, new Object[]{year.getCode()});
		if(DataUtil.isNull(code)) {
			code = year.getCode()+"01";
		}else{
			code = (Integer.parseInt(code)+1)+"";
		}
		return code;
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardTypeByCode</p> 
	* <p>Description: </p> 
	* @param awardTypeCode
	* @return 
	* @see com.uws.reward.dao.ISetAwardDao#getAwardTypeByCode(java.lang.String) 
	*/
	@Override
	public AwardType getAwardTypeByCode(String awardTypeCode) {
		String hql = "from  AwardType a where a.awardTypeCode = ?";
		return (AwardType) this.queryUnique(hql, new Object[]{awardTypeCode});
	}

	/* (非 Javadoc) 
	* <p>Title: getCheckConInfoList</p> 
	* <p>Description: </p> 
	* @param conditionId
	* @return 
	* @see com.uws.reward.dao.ISetAwardDao#getCheckConInfoList(java.lang.String) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<ConditionInfo> getCheckConInfoList(String conditionId) {
		String hql = " from ConditionInfo c where c.conditionId.id = ? and c.checkOrNot = 'Y'";
		return query(hql,new Object[]{conditionId});
	}

	/* (非 Javadoc) 
	* <p>Title: removeObjFromSession</p> 
	* <p>Description: </p> 
	* @param obj 
	* @see com.uws.reward.dao.ISetAwardDao#removeObjFromSession(java.lang.Object) 
	*/
	@Override
	public void removeObjFromSession(Object obj) {
		this.sessionFactory.getCurrentSession().evict(obj);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public QuotaInfo getCollegeQuotaInfoByConId(String conditionId, String collegeId){
		//  Auto-generated method stub
		String hql = " from QuotaInfo q where q.conditionId.id = ? and q.academyId.id = ? order by q.academyId.code desc";
		List<QuotaInfo>  list = query(hql,new Object[]{conditionId, collegeId});
		return list != null?list.get(0):null;
	}
	
	/***
	 * 查询班级人数、专业人数
	 * @param studentId
	 * @return
	 */
	@Override
	public List queryMajorClassCount(String studentId){
		StudentInfoModel student = (StudentInfoModel) this.get(StudentInfoModel.class, studentId);
		if(DataUtil.isNotNull(student)){
			StringBuffer hql = new StringBuffer("select count(t.major) from hky_student_info t where t.major = ? "
									+ " union all "
									+ " select count(q.class_id) from hky_student_info q where q.class_id = ? ");
			
			return this.querySQL(hql.toString(), new Object[]{student.getMajor().getId(), student.getClassId().getId()});
		}else{
			return null;
		}
	}
}

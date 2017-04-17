/**   
* @Title: CollegeAwardDaoImpl.java 
* @Package com.uws.reward.dao.impl 
* @author zhangyb   
* @date 2015年12月31日 上午10:38:11 
* @version V1.0   
*/
package com.uws.reward.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.reward.CollegeAwardInfo;
import com.uws.reward.dao.ICollegeAwardDao;

/** 
 * @ClassName: CollegeAwardDaoImpl 
 * @Description: 校内奖励daoimpl 
 * @author zhangyb 
 * @date 2015年12月31日 上午10:38:11  
 */
@Repository("collegeAwardDao")
public class CollegeAwardDaoImpl extends BaseDaoImpl implements
		ICollegeAwardDao {

	/* (非 Javadoc) 
	* <p>Title: queryCollegeAwardPage</p> 
	* <p>Description: </p> 
	* @param awardInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.reward.dao.ICollegeAwardDao#queryCollegeAwardPage(com.uws.domain.reward.CollegeAwardInfo, int, int) 
	*/
	@Override
	public Page queryCollegeAwardPage(CollegeAwardInfo awardInfo, int pageNo,
			int pageSize) {
		
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from CollegeAwardInfo c where 1=1");
//		学年
		if(DataUtil.isNotNull(awardInfo.getSchoolYear()) && DataUtil.isNotNull(awardInfo.getSchoolYear().getCode())) {
			hql.append(" and c.schoolYear.code = ?");
			values.add(awardInfo.getSchoolYear().getCode());
		}
//		学期
		if(DataUtil.isNotNull(awardInfo.getSchoolTerm()) && DataUtil.isNotNull(awardInfo.getSchoolTerm().getCode())) {
			hql.append(" and c.schoolTerm.code = ?");
			values.add(awardInfo.getSchoolTerm().getCode());
		}
//		学院
		if(DataUtil.isNotNull(awardInfo.getStudentId()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getCollege()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getCollege().getId())) {
			
			hql.append(" and c.studentId.college.id = ?");
			values.add(awardInfo.getStudentId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(awardInfo.getStudentId()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getMajor()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getMajor().getId())) {
			hql.append(" and c.studentId.major.id = ?");
			values.add(awardInfo.getStudentId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(awardInfo.getStudentId()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getClassId()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getClassId().getId())) {
			hql.append(" and c.studentId.classId.id = ?");
			values.add(awardInfo.getStudentId().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(awardInfo.getStudentId()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getName()) ){
			hql.append(" and c.studentId.name like ?");
			values.add("%" + HqlEscapeUtil.escape(awardInfo.getStudentId().getName()) + "%"); 
		}
//		学号
		if(DataUtil.isNotNull(awardInfo.getStudentId()) &&
				DataUtil.isNotNull(awardInfo.getStudentId().getStuNumber())) {
			hql.append(" and c.studentId.stuNumber like ?");
			values.add("%" + HqlEscapeUtil.escape(awardInfo.getStudentId().getStuNumber()) + "%");
		}
//		奖励名称
		if(DataUtil.isNotNull(awardInfo.getAwardName())) {
			hql.append(" and c.awardName like ?");
			values.add("%" + HqlEscapeUtil.escape(awardInfo.getAwardName()) + "%");
		}
		hql.append(" order by c.updateTime desc");
		return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: saveAwardInfo</p> 
	* <p>Description: </p> 
	* @param awardInfo 
	* @see com.uws.reward.dao.ICollegeAwardDao#saveAwardInfo(com.uws.domain.reward.CollegeAwardInfo) 
	*/
	@Override
	public void saveAwardInfo(CollegeAwardInfo awardInfo) {
		this.save(awardInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: delAwardInfo</p> 
	* <p>Description: </p> 
	* @param awardInfo 
	* @see com.uws.reward.dao.ICollegeAwardDao#delAwardInfo(com.uws.domain.reward.CollegeAwardInfo) 
	*/
	@Override
	public void delAwardInfo(CollegeAwardInfo awardInfo) {
		this.delete(awardInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateAwardInfo</p> 
	* <p>Description: </p> 
	* @param awardInfo 
	* @see com.uws.reward.dao.ICollegeAwardDao#updateAwardInfo(com.uws.domain.reward.CollegeAwardInfo) 
	*/
	@Override
	public void updateAwardInfo(CollegeAwardInfo awardInfo) {
		this.update(awardInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardInfoById</p> 
	* <p>Description: </p> 
	* @param awardInfoId
	* @return 
	* @see com.uws.reward.dao.ICollegeAwardDao#getAwardInfoById(java.lang.String) 
	*/
	@Override
	public CollegeAwardInfo getAwardInfoById(String awardInfoId) {
		String hql = " from CollegeAwardInfo c where c.id = ?";
		return (CollegeAwardInfo) this.queryUnique(hql, new Object[]{awardInfoId});
	}

	/* (非 Javadoc) 
	* <p>Title: checkAwardInfo</p> 
	* <p>Description: </p> 
	* @param schoolYear
	* @param schoolTerm
	* @param studentId
	* @param awardName
	* @return 
	* @see com.uws.reward.dao.ICollegeAwardDao#checkAwardInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String) 
	*/
	@Override
	public long checkAwardInfo(String schoolYear, String schoolTerm,
			String studentId, String awardName) {
		String sql = " from CollegeAwardInfo c where c.schoolYear.code = ? and c.schoolTerm.code = ? "
				+ "and c.studentId.id = ? and c.awardName = ?";
		@SuppressWarnings("unchecked")
		List<CollegeAwardInfo> awardList = this.query(sql, new Object[]{schoolYear,schoolTerm,studentId,awardName});
		return awardList.size();
	}

	/* (非 Javadoc) 
	* <p>Title: countCollegeAwardNum</p> 
	* <p>Description: </p> 
	* @return 
	* @see com.uws.reward.dao.ICollegeAwardDao#countCollegeAwardNum() 
	*/
	@Override
	public long countCollegeAwardNum() {
		
		String hql = "select count(c.id) from CollegeAwardInfo c where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

}

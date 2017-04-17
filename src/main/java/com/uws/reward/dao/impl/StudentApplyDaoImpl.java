/**   
* @Title: StudentApplyDaoImpl.java 
* @Package com.uws.reward.dao 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年8月21日 下午1:55:45 
* @version V1.0   
*/
package com.uws.reward.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.reward.dao.IStudentApplyDao;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.util.ProjectConstants;

/** 
 * @ClassName: StudentApplyDaoImpl 
 * @Description:  学生评奖评优申请daoImpl
 * @author zhangyb 
 * @date 2015年8月21日 下午1:55:45  
 */
@Repository("studentApplyDao")
public class StudentApplyDaoImpl extends BaseDaoImpl implements
		IStudentApplyDao {

	@Override
	public Page queryStuApplyPage(StudentApplyInfo stuApplyInfo, int pageNo,
			int pageSize) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from StudentApplyInfo s where 1=1");
//		学年
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear())
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode())) {
			hql.append(" and s.awardTypeId.schoolYear.code = ?");
			values.add(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode());
		}
//		奖项类型
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getAwardType())
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getAwardType().getCode())) {
			hql.append(" and s.awardTypeId.awardType.code = ?");
			values.add(stuApplyInfo.getAwardTypeId().getAwardType().getCode());
		}
//		学生Id
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId())  && DataUtil.isNotNull(stuApplyInfo.getStudentId().getId())) {
			hql.append(" and s.studentId.id like  ?");
			values.add("%" + HqlEscapeUtil.escape(stuApplyInfo.getStudentId().getId()) + "%");
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void saveStuApply(StudentApplyInfo stuApplyInfo) {
		save(stuApplyInfo);
	}

	@Override
	public void updateStuApply(StudentApplyInfo stuApplyInfo) {
		update(stuApplyInfo);
	}

	@Override
	public void deleteStuApply(StudentApplyInfo stuApplyInfo) {
		delete(stuApplyInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StudentApplyInfo> getStuApplyInfoList(StudentApplyInfo stuApplyInfo) {
		List<Object> values = new ArrayList<Object>();
		String hql = " from StudentApplyInfo s where 1=1  ";
		hql += " and s.awardTypeId.awardStatus.code = ? ";
		values.add(stuApplyInfo.getAwardTypeId().getAwardStatus().getCode());//发布状态
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) && DataUtil.isNotNull(stuApplyInfo.getStudentId().getId())){//学号
			hql += " and s.studentId.id = ? ";
			values.add(stuApplyInfo.getStudentId().getId());
		}
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId()) && DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear()) 
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode())){//学年
			hql = hql+" and s.awardTypeId.schoolYear.code = ?  ";
			values.add(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode());
		}
		return this.query(hql, values.toArray());
	}

	@Override
	public StudentApplyInfo getStuApplyInfoById(String id) {
		String hql = " from StudentApplyInfo s where s.id = ?";
		return (StudentApplyInfo) this.queryUnique(hql, new Object[]{id});
	}

	@Override
	public Page queryStuApplyPage(StudentApplyInfo stuApply, int pageNo,
			int pageSize, String userId,String[] objectIds) {
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("from StudentApplyInfo s where 1=1 and (s.nextApprover.id = :userId or s.id in (:objectIds))");
		values.put("userId", userId);
		values.put("objectIds", objectIds);
//		学年
		if(DataUtil.isNotNull(stuApply.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApply.getAwardTypeId().getSchoolYear())
				&& DataUtil.isNotNull(stuApply.getAwardTypeId().getSchoolYear().getCode())) {
			hql.append(" and s.awardTypeId.schoolYear.code = :schoolYearCode");
			values.put("schoolYearCode",stuApply.getAwardTypeId().getSchoolYear().getCode());
		}
//		学院
		if(DataUtil.isNotNull(stuApply.getStudentId())
				&& DataUtil.isNotNull(stuApply.getStudentId().getCollege())
				&& DataUtil.isNotNull(stuApply.getStudentId().getCollege().getId())) {
			
			hql.append(" and s.studentId.college.id = :collegeId");
			values.put("collegeId",stuApply.getStudentId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(stuApply.getStudentId())
				&& DataUtil.isNotNull(stuApply.getStudentId().getMajor())
				&& DataUtil.isNotNull(stuApply.getStudentId().getMajor().getId())) {
			hql.append(" and s.studentId.major.id = :majorId");
			values.put("majorId",stuApply.getStudentId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(stuApply.getStudentId()) 
				&& DataUtil.isNotNull(stuApply.getStudentId().getClassId()) 
				&& DataUtil.isNotNull(stuApply.getStudentId().getClassId().getId())) {
			hql.append(" and s.studentId.classId.id = :classId");
			values.put("classId",stuApply.getStudentId().getClassId().getId());
		}
//		奖项类型
		if(DataUtil.isNotNull(stuApply.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApply.getAwardTypeId().getAwardType())
				&& DataUtil.isNotNull(stuApply.getAwardTypeId().getAwardType().getCode())) {
			hql.append(" and s.awardTypeId.awardType.code = :awardTypeCode");
			values.put("awardTypeCode",stuApply.getAwardTypeId().getAwardType().getCode());
		}
		
		// 审核状态
		if (!StringUtils.isEmpty(stuApply.getProcessStatus())) {
			if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(stuApply.getProcessStatus())) {
				hql.append(" and s.nextApprover.id = :approveUserId ");
				values.put("approveUserId",userId);
			}else{
				hql.append(" and s.processStatus = :processStatus and (s.nextApprover.id != :approveUserId or s.nextApprover is null)  ");
				values.put("processStatus",stuApply.getProcessStatus());
				values.put("approveUserId",userId);
			}
		}
		
//		姓名
		if(DataUtil.isNotNull(stuApply.getStudentId()) &&
				DataUtil.isNotNull(stuApply.getStudentId().getName())) {
			hql.append(" and s.studentId.name like :studentName");
			values.put("studentName", "%" + HqlEscapeUtil.escape(stuApply.getStudentId().getName()) + "%");
		}
//		学号
		if(DataUtil.isNotNull(stuApply.getStudentId()) && DataUtil.isNotNull(stuApply.getStudentId().getId())) {
			hql.append(" and s.studentId.id like  :studentId");
			values.put("studentId", "%" + HqlEscapeUtil.escape(stuApply.getStudentId().getId()) + "%");
		}
		
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
	}

	@Override
	public Page queryStudentPage(StudentApplyInfo stuApplyInfo, int pageNo,
			int pageSize) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from StudentApplyInfo s where 1=1");
		hql.append("and s.processStatus != '" +RewardConstant.approveStatus + "'");
//		学年
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear())
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode())) {
			hql.append(" and s.awardTypeId.schoolYear.code = ?");
			values.add(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode());
		}
//		学院
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getCollege())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getCollege().getId())) {
			hql.append(" and s.studentId.college.id = ?");
			values.add(stuApplyInfo.getStudentId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getMajor())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getMajor().getId())) {
			hql.append(" and s.studentId.major.id = ?");
			values.add(stuApplyInfo.getStudentId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getClassId())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getClassId().getId())) {
			hql.append(" and s.studentId.classId.id = ?");
			values.add(stuApplyInfo.getStudentId().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getName())) {
			hql.append(" and s.studentId.name like ?");
			values.add("%" + HqlEscapeUtil.escape(stuApplyInfo.getStudentId().getName()) + "%");
		}
//		奖项类型
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getAwardType())
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getAwardType().getCode())) {
			hql.append(" and s.awardTypeId.awardType.code = ?");
			values.add(stuApplyInfo.getAwardTypeId().getAwardType().getCode());
		}
//		学生Id
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getId())) {
			hql.append(" and s.studentId.id like  ?");
			values.add("%" + HqlEscapeUtil.escape(stuApplyInfo.getStudentId().getId()) + "%");
		}
//		审批状态
		if(DataUtil.isNotNull(stuApplyInfo.getProcessStatus())) {
			/*if(stuApplyInfo.getProcessStatus().equals("SAVED")) {
				hql.append(" and s.processStatus is null");
				hql.append(" and s.applyStatus.code= 'SAVED'");
			}else*/ 
			if(stuApplyInfo.getProcessStatus().equals("APPROVEING")) {
				hql.append(" and (s.processStatus is null or s.processStatus = 'APPROVEING')");
				hql.append(" and s.applyStatus.code= 'SUBMITTED'");
			}else if(stuApplyInfo.getProcessStatus().equals("REJECT")) {
				hql.append(" and s.processStatus = ?");
				hql.append(" and s.applyStatus.code= 'SAVED'");
				values.add("REJECT");
			}else if(stuApplyInfo.getProcessStatus().equals("PASS")) {
				hql.append(" and s.processStatus = ?");
				hql.append(" and s.applyStatus.code= 'SUBMITTED'");
				values.add("PASS");
			}
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StudentApplyInfo> getStuInfoList(StudentApplyInfo stuApplyInfo) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from StudentApplyInfo s where 1=1");
//		学年
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear())
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode())) {
			hql.append(" and s.awardTypeId.schoolYear.code = ?");
			values.add(stuApplyInfo.getAwardTypeId().getSchoolYear().getCode());
		}
//		学院
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getCollege())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getCollege().getId())) {
			hql.append(" and s.studentId.college.id = ?");
			values.add(stuApplyInfo.getStudentId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getMajor())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getMajor().getId())) {
			hql.append(" and s.studentId.major.id = ?");
			values.add(stuApplyInfo.getStudentId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId()) 
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getClassId())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getClassId().getId())) {
			hql.append(" and s.studentId.classId.id = ?");
			values.add(stuApplyInfo.getStudentId().getClassId().getId());
		}
//		奖项类型
		if(DataUtil.isNotNull(stuApplyInfo.getAwardTypeId())
				&& DataUtil.isNotNull(stuApplyInfo.getAwardTypeId().getId())) {
			hql.append(" and s.awardTypeId.id = ?");
			values.add(stuApplyInfo.getAwardTypeId().getId());
		}
//		学生权限申请途径
		if(DataUtil.isNotNull(stuApplyInfo.getApplySource()) 
				&& DataUtil.isNotNull(stuApplyInfo.getApplySource().getCode())) {
			hql.append(" and s.applySource.code = ?");
			values.add(stuApplyInfo.getApplySource().getCode());
		}
//		学生ID
		if(DataUtil.isNotNull(stuApplyInfo.getStudentId())
				&& DataUtil.isNotNull(stuApplyInfo.getStudentId().getId())) {
			hql.append(" and s.studentId.id = ?");
			values.add(stuApplyInfo.getStudentId().getId());
		}
//		流程审批状态
		if(DataUtil.isNotNull(stuApplyInfo.getProcessStatus())) {
			hql.append(" and s.processStatus = 'PASS'");
		}
		return this.query(hql.toString(), values.toArray());
	}

	@Override
	public long countStuApply() {
		String hql = "select count(s.id) from StudentApplyInfo s where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

	/* (非 Javadoc) 
	* <p>Title: checkStuApplyXingZhi</p> 
	* <p>Description: </p> 
	* @param stuId
	* @param yearDic
	* @return 
	* @see com.uws.reward.dao.IStudentApplyDao#checkStuApplyXingZhi(java.lang.String, com.uws.sys.model.Dic) 
	*/
	@Override
	public boolean checkStuApplyXingZhi(String stuId, Dic yearDic,Dic applyStatus) {
		String hql = " from StudentApplyInfo s where s.studentId.id = ? and "
				+ "s.awardTypeId.schoolYear.id = ? and s.applyStatus.id = ? and s.awardTypeId.secondAwardName.name like ('%"+
				RewardConstant.xingZhiAwardName + "%')";
		@SuppressWarnings("unchecked")
		List<StudentApplyInfo> applyList = this.query(hql, 
				new Object[]{stuId,yearDic.getId(),applyStatus.getId()});
		return applyList.size() > 0 ? false : true;
	}

	/* (非 Javadoc) 
	* <p>Title: getApplyInfo</p> 
	* <p>Description: </p> 
	* @param secondAwardName
	* @param Year
	* @return 
	* @see com.uws.reward.dao.IStudentApplyDao#getApplyInfo(java.lang.String, com.uws.sys.model.Dic) 
	*/
	@Override
	public StudentApplyInfo getApplyInfo(String secondAwardName, Dic year,String stuId) {
		
		String sql = " from StudentApplyInfo s where s.awardTypeId.schoolYear.code = ? "
				+ "and s.awardTypeId.secondAwardName.code = ? and s.studentId.id = ?";
		
		return (StudentApplyInfo) this.queryUnique(sql, new Object[]{year.getCode(),secondAwardName,stuId}) != null ? 
				(StudentApplyInfo) this.queryUnique(sql, new Object[]{year.getCode(),secondAwardName,stuId}) : null;
	}
	
	/***
	 * 判断学生该学年是否存在互斥情况，三好学生和优秀班干部互斥
	 * @param stuId
	 * @param yearDic
	 * @param rewardCode
	 * @return
	 */
	@Override
	public boolean checkStuSanHaoStuleader(String stuId,Dic yearDic,String rewardCode){
		String hql = " from StudentApplyInfo s where s.studentId.id = ? and "
				+ "s.awardTypeId.schoolYear.id = ? and s.awardTypeId.awardInfoId.awardCode = ? ";
		@SuppressWarnings("unchecked")
		List<StudentApplyInfo> applyList = this.query(hql, 
				new Object[]{stuId,yearDic.getId(),Long.parseLong(rewardCode)});
		return applyList.size() > 0 ? false : true;
	}
	
	/***
	 * 审批通过的名额
	 * @param awardTypeId
	 * @param collegeId
	 * @return
	 */
	@Override
	public int queryApproveSum(String awardTypeId, String collegeId){
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select count(*) from StudentApplyInfo s where s.awardTypeId.id = ? ");
		values.add(awardTypeId);
		hql.append(" and s.studentId.college.id = ? ");
		values.add(collegeId);
		hql.append(" and s.processStatus = ?");
		values.add("PASS");
		hql.append(" and s.applyStatus.code= 'SUBMITTED'");
		
		return (int) this.queryCount(hql.toString(), values.toArray());
	}
}

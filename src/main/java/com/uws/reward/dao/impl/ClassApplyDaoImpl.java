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
import com.uws.domain.reward.ClassApplyInfo;
import com.uws.reward.dao.IClassApplyDao;
import com.uws.util.ProjectConstants;

/** 
* @ClassName: ClassApplyDaoImpl 
* @Description:  班级申请daoimpl 
* @author zhangyb 
* @date 2015年9月7日 下午6:14:36  
*/
@Repository("classApplyDao")
public class ClassApplyDaoImpl extends BaseDaoImpl implements IClassApplyDao {

	@Override
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo, int pageNo,
			int pageSize) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" select c from ClassApplyInfo c left join c.classId s left join s.headermaster t where 1=1 ");
//		学年
		if(DataUtil.isNotNull(classApplyInfo.getAwardTypeId()) && DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getSchoolYear()) &&
				DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getSchoolYear().getId())) {
			hql.append(" and c.awardTypeId.schoolYear.id = ?");
			values.add(classApplyInfo.getAwardTypeId().getSchoolYear().getId());
		}
//		学院
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getCollage())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getCollage().getId())) {
			hql.append(" and c.classId.major.collage.id = ?");
			values.add(classApplyInfo.getClassId().getMajor().getCollage().getId());
		}
//		专业
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getId())) {
			hql.append(" and c.classId.major.id = ?");
			values.add(classApplyInfo.getClassId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getId())) {
			hql.append(" and c.classId.id = ?");
			values.add(classApplyInfo.getClassId().getId());
		}
//		奖项类型
		if(DataUtil.isNotNull(classApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getAwardType())
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getAwardType().getCode())) {
			hql.append(" and c.awardTypeId.awardType.code = ?");
			values.add(classApplyInfo.getAwardTypeId().getAwardType().getCode());
		}
//		班主任
		if(classApplyInfo != null && DataUtil.isNotNull(classApplyInfo.getClassId())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getHeadermaster())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getHeadermaster().getId())) {
			hql.append(" and t.id = ?");
//			hql.append(" and c.classId.headermaster.id = ?");
			values.add(classApplyInfo.getClassId().getHeadermaster().getId());
		}
//		审批状态
		if(DataUtil.isNotNull(classApplyInfo.getProcessStatus())) {
			/*if(classApplyInfo.getProcessStatus().equals("SAVED")) {
				hql.append(" and c.processStatus is null");
				hql.append(" and c.applyStatus.code= 'SAVED'");
			}else */
			if(classApplyInfo.getProcessStatus().equals("APPROVEING")) {
				hql.append(" and (c.processStatus is null or c.processStatus = 'APPROVEING')");
				hql.append(" and c.applyStatus.code= 'SUBMITTED'");
			}else if(classApplyInfo.getProcessStatus().equals("REJECT")) {
				hql.append(" and c.processStatus = ?");
				hql.append(" and c.applyStatus.code= 'SAVED'");
				values.add("REJECT");
			}else if(classApplyInfo.getProcessStatus().equals("PASS")) {
				hql.append(" and c.processStatus = ?");
				hql.append(" and c.applyStatus.code= 'SUBMITTED'");
				values.add("PASS");
			}
		}
		hql.append(" order by c.updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void saveClassApply(ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		save(classApplyInfo);
	}

	@Override
	public void updateClassApply(ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		update(classApplyInfo);
	}

	@Override
	public void deleteClassApply(ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		delete(classApplyInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClassApplyInfo> getClassApplyInfoList(
			ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		String hql = " from ClassApplyInfo c where  c.awardTypeId.schoolYear.code = ? and c.awardTypeId.awardStatus.code = ?";
		return this.query(hql, new Object[]{"",classApplyInfo.getAwardTypeId().getSchoolYear().getCode(),
				classApplyInfo.getAwardTypeId().getAwardStatus().getCode()});
	}

	@Override
	public ClassApplyInfo getClassApplyInfoById(String id) {
		//  Auto-generated method stub
		String hql = " from ClassApplyInfo c where c.id = ?";
		return (ClassApplyInfo) this.queryUnique(hql, new Object[]{id});
	}

	@Override
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo, int pageNo,
			int pageSize, String userId, String[] objectIds) {
		//  Auto-generated method stub
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("from ClassApplyInfo c where 1=1 and (c.nextApprover.id = :userId or c.id in (:objectIds))");
		values.put("userId", userId);
		values.put("objectIds", objectIds);
//		学年
		if(DataUtil.isNotNull(classApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getSchoolYear())
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getSchoolYear().getCode())) {
			hql.append(" and c.awardTypeId.schoolYear.code = :schoolYearCode");
			values.put("schoolYearCode",classApplyInfo.getAwardTypeId().getSchoolYear().getCode());
		}
//		学院
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getCollage())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getCollage().getId())) {
			
			hql.append(" and c.classId.major.collage.id = :collegeId");
			values.put("collegeId",classApplyInfo.getClassId().getMajor().getCollage().getId());
		}
//		班级
		if(DataUtil.isNotNull(classApplyInfo.getClassId()) &&
				DataUtil.isNotNull(classApplyInfo.getClassId().getId())) {
			hql.append(" and c.classId.id = :classId");
			values.put("classId",classApplyInfo.getClassId().getId());
		}
//		奖项类型
		if(DataUtil.isNotNull(classApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getAwardType())
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getAwardType().getCode())) {
			hql.append(" and c.awardTypeId.awardType.code = :awardTypeCode");
			values.put("awardTypeCode",classApplyInfo.getAwardTypeId().getAwardType().getCode());
		}
//		审批状态
		if (!StringUtils.isEmpty(classApplyInfo.getProcessStatus())) {
			if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(classApplyInfo.getProcessStatus())) {
				hql.append(" and c.nextApprover.id = :approveUserId ");
				values.put("approveUserId",userId);
			}else{
				hql.append(" and c.processStatus = :processStatus and (c.nextApprover.id != :approveUserId or c.nextApprover is null)  ");
				values.put("processStatus",classApplyInfo.getProcessStatus());
				values.put("approveUserId",userId);
			}
		}
		
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClassApplyInfo> getClassInfoList(ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from ClassApplyInfo c where 1=1");
//		学年
		if(DataUtil.isNotNull(classApplyInfo.getAwardTypeId()) 
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getSchoolYear())
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getSchoolYear().getCode())) {
			hql.append(" and c.awardTypeId.schoolYear.code = ?");
			values.add(classApplyInfo.getAwardTypeId().getSchoolYear().getCode());
		}
//		学院
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getCollage())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getCollage().getId())) {
			hql.append(" and c.classId.major.collage.id = ?");
			values.add(classApplyInfo.getClassId().getMajor().getCollage().getId());
		}
//		专业
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId()) 
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getMajor().getId())) {
			hql.append(" and c.classId.major.id = ?");
			values.add(classApplyInfo.getClassId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(classApplyInfo) && DataUtil.isNotNull(classApplyInfo.getClassId())  
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getId())) {
			hql.append(" and c.classId.id = ?");
			values.add(classApplyInfo.getClassId().getId());
		}
//		教师
		if(classApplyInfo != null && DataUtil.isNotNull(classApplyInfo.getClassId())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getHeadermaster())
				&& DataUtil.isNotNull(classApplyInfo.getClassId().getHeadermaster().getId())) {
			hql.append(" and c.classId.headermaster.id = ?");
			values.add(classApplyInfo.getClassId().getHeadermaster().getId());
		}
		
//		奖项类型
		if(DataUtil.isNotNull(classApplyInfo.getAwardTypeId())
				&& DataUtil.isNotNull(classApplyInfo.getAwardTypeId().getId())) {
			hql.append(" and c.awardTypeId.id = ?");
			values.add(classApplyInfo.getAwardTypeId().getId());
		}
//		班级权限申请途径
		if(DataUtil.isNotNull(classApplyInfo.getApplySource()) && DataUtil.isNotNull(classApplyInfo.getApplySource().getCode())) {
			hql.append(" and c.applySource.code = ?");
			values.add(classApplyInfo.getApplySource().getCode());
		}
		
//		流程审批状态
		if(DataUtil.isNotNull(classApplyInfo.getProcessStatus())) {
			hql.append(" and c.processStatus = 'PASS'");
		}
		
		hql.append(" order by updateTime desc");
		
		return this.query(hql.toString(), values.toArray());
	}

	@Override
	public long countClassApply() {
		//  Auto-generated method stub
		String hql = "select count(c.id) from ClassApplyInfo c where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}
	
	

}

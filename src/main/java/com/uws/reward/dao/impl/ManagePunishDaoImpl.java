package com.uws.reward.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.reward.PunishInfo;
import com.uws.reward.dao.IManagePunishDao;

/** 
* @ClassName: ManagePunishDaoImpl 
* @Description:  惩罚管理daoImplementation
* @author zhangyb 
* @date 2015年8月24日 上午10:29:59  
*/
@Repository("managePunishDao")
public class ManagePunishDaoImpl extends BaseDaoImpl implements
		IManagePunishDao {

	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Override
	public Page queryPunishPage(PunishInfo punishInfo, int pageNo, int pageSize) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from PunishInfo p where 1=1");
//		处分学年
		if(DataUtil.isNotNull(punishInfo.getPunishYear()) && DataUtil.isNotNull(punishInfo.getPunishYear().getCode())) {
			hql.append(" and p.punishYear.code = ?");
			values.add(punishInfo.getPunishYear().getCode());
		}
//		处分学期
		if(DataUtil.isNotNull(punishInfo.getPunishTerm()) && DataUtil.isNotNull(punishInfo.getPunishTerm().getCode())) {
			hql.append(" and p.punishTerm.code = ?");
			values.add(punishInfo.getPunishTerm().getCode());
		}
//		学院
		if(DataUtil.isNotNull(punishInfo.getStuId()) && 
				DataUtil.isNotNull(punishInfo.getStuId().getCollege()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getCollege().getId())) {
			hql.append(" and p.stuId.college.id = ?");
			values.add(punishInfo.getStuId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getMajor()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getMajor().getId())) {
			hql.append(" and p.stuId.major.id = ?");
			values.add(punishInfo.getStuId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getClassId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getClassId().getId())) {
			hql.append(" and p.stuId.classId.id = ?");
			values.add(punishInfo.getStuId().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getName())) {
			hql.append(" and p.stuId.name like ?");
			values.add("%" + HqlEscapeUtil.escape(punishInfo.getStuId().getName()) + "%");
		}
//		学号
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getStuNumber())) {
			hql.append(" and p.stuId.stuNumber like ?");
			values.add("%" + HqlEscapeUtil.escape(punishInfo.getStuId().getStuNumber()) + "%");
		}
//		处分名称码
		if(DataUtil.isNotNull(punishInfo.getPunish()) && DataUtil.isNotNull(punishInfo.getPunish().getCode())) {
			hql.append(" and p.punish.code = ?");
			values.add(punishInfo.getPunish().getCode());
		}
//		处分文号
		if(DataUtil.isNotNull(punishInfo.getPunishNum())) {
			hql.append(" and p.punishNum like ?");
			values.add("%" + HqlEscapeUtil.escape(punishInfo.getPunishNum()) + "%");
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void savePunish(PunishInfo punishInfo) {
		//  Auto-generated method stub
		save(punishInfo);
	}

	@Override
	public void updatePunish(PunishInfo punishInfo) {
		//  Auto-generated method stub
		update(punishInfo);
	}

	@Override
	public void deletePunish(PunishInfo punishInfo) {
		//  Auto-generated method stub
		delete(punishInfo);
	}

	@Override
	public PunishInfo getPunishInfoById(String id) {
		//  Auto-generated method stub
		String hql = " from PunishInfo p where p.id = ?";
		return (PunishInfo) queryUnique(hql, new Object[]{id});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PunishInfo> getPunishList(String stuId,String punishNum) {
		//  Auto-generated method stub
		String hql = " from PunishInfo p where p.stuId.id = ? and p.punishNum = ?";
		return query(hql, new Object[]{stuId,punishNum});
	}

	@Override
	public Page queryPunishPage(PunishInfo punishInfo, int pageNo,
			int pageSize, String flag) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from PunishInfo p where 1=1");
		if(flag.equals("student")) {
			String userId = this.sessionUtil.getCurrentUserId();
			punishInfo.setStuIdStr(userId);
		}
		
//		处分学年
		if(DataUtil.isNotNull(punishInfo.getPunishYear()) && DataUtil.isNotNull(punishInfo.getPunishYear().getCode())) {
			hql.append(" and p.punishYear.code = ?");
			values.add(punishInfo.getPunishYear().getCode());
		}
//		处分学期
		if(DataUtil.isNotNull(punishInfo.getPunishTerm()) && DataUtil.isNotNull(punishInfo.getPunishTerm().getCode())) {
			hql.append(" and p.punishTerm.code = ?");
			values.add(punishInfo.getPunishTerm().getCode());
		}
//		学院
		if(DataUtil.isNotNull(punishInfo.getStuId()) && 
				DataUtil.isNotNull(punishInfo.getStuId().getCollege()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getCollege().getId())) {
			hql.append(" and p.stuId.college.id = ?");
			values.add(punishInfo.getStuId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getMajor()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getMajor().getId())) {
			hql.append(" and p.stuId.major.id = ?");
			values.add(punishInfo.getStuId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getClassId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getClassId().getId())) {
			hql.append(" and p.stuId.classId.id = ?");
			values.add(punishInfo.getStuId().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getName())) {
			hql.append(" and p.stuId.name like ?");
			values.add("%" + HqlEscapeUtil.escape(punishInfo.getStuId().getName()) + "%");
		}
//		学号
		if(DataUtil.isNotNull(punishInfo.getStuId()) &&
				DataUtil.isNotNull(punishInfo.getStuId().getStuNumber())) {
			hql.append(" and p.stuId.stuNumber like ?");
			values.add("%" + HqlEscapeUtil.escape(punishInfo.getStuId().getStuNumber()) + "%");
		}
//		处分名称码
		if(DataUtil.isNotNull(punishInfo.getPunish()) && DataUtil.isNotNull(punishInfo.getPunish().getCode())) {
			hql.append(" and p.punish.code = ?");
			values.add(punishInfo.getPunish().getCode());
		}
//		处分文号
		if(DataUtil.isNotNull(punishInfo.getPunishNum())) {
			hql.append(" and p.punishNum like ?" );
			values.add("%" + HqlEscapeUtil.escape(punishInfo.getPunishNum()) + "%");
		}
//		如果是学生登录
		if(DataUtil.isNotNull(punishInfo.getStuIdStr())) {
			hql.append(" and p.stuId.stuNumber = ?");
			values.add(punishInfo.getStuIdStr());
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public long countPunishNum() {
		//  Auto-generated method stub
		String hql = "select count(p.id) from PunishInfo p where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

	/* (非 Javadoc) 
	* <p>Title: getStuPunishList</p> 
	* <p>Description: </p> 
	* @param student
	* @return 
	* @see com.uws.reward.dao.IManagePunishDao#getStuPunishList(com.uws.domain.orientation.StudentInfoModel) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<PunishInfo> getStuPunishList(PunishInfo punishInfo) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from PunishInfo p where 1=1");
		if(punishInfo != null && punishInfo.getStuId() != null) {
			hql.append(" and p.stuId.id = ?");
			values.add(punishInfo.getStuId().getId());
		}
		if(punishInfo != null && punishInfo.getPunishYear() != null && DataUtil.isNotNull(punishInfo.getPunishYear().getCode())) {
			hql.append(" and p.punishYear.code = ? ");
			values.add(punishInfo.getPunishYear().getCode());
		}
		return this.query(hql.toString(), values.toArray());
	}

}

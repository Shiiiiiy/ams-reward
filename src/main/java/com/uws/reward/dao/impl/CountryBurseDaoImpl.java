package com.uws.reward.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.reward.dao.ICountryBurseDao;

/** 
* @ClassName: CountryBurseDaoImpl 
* @Description:  国家奖助daoImpl
* @author zhangyb 
* @date 2015年8月27日 上午11:14:17  
*/
@Repository("countryBurseDao")
public class CountryBurseDaoImpl extends BaseDaoImpl implements
		ICountryBurseDao {

	@Override
	public Page queryBursePage(CountryBurseInfo burse, int pageNo,
			int pageSize) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from CountryBurseInfo b where 1=1");
//		学年
		if(DataUtil.isNotNull(burse.getSchoolYear()) && DataUtil.isNotNull(burse.getSchoolYear().getCode())) {
			hql.append(" and b.schoolYear.code = ?");
			values.add(burse.getSchoolYear().getCode());
		}
//		学院
		if(DataUtil.isNotNull(burse.getStuId()) && 
				DataUtil.isNotNull(burse.getStuId().getCollege().getId())) {
			
			hql.append(" and b.stuId.college.id = ?");
			values.add(burse.getStuId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(burse.getStuId()) &&
				DataUtil.isNotNull(burse.getStuId().getMajor().getId())) {
			hql.append(" and b.stuId.major.id = ?");
			values.add(burse.getStuId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(burse.getStuId()) &&
				DataUtil.isNotNull(burse.getStuId().getClassId().getId())) {
			hql.append(" and b.stuId.classId.id = ?");
			values.add(burse.getStuId().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(burse.getStuId()) &&
				DataUtil.isNotNull(burse.getStuId().getName())) {
			hql.append(" and b.stuId.name like ?");
			values.add("%" + HqlEscapeUtil.escape(burse.getStuId().getName()) + "%");
		}
//		学号
		if(DataUtil.isNotNull(burse.getStuId()) &&
				DataUtil.isNotNull(burse.getStuId().getStuNumber())) {
			hql.append(" and b.stuId.stuNumber like ?");
			values.add("%" + HqlEscapeUtil.escape(burse.getStuId().getStuNumber()) + "%");
		}
//		奖金类型
		if(DataUtil.isNotNull(burse.getBurseName()) && DataUtil.isNotNull(burse.getBurseName().getCode())) {
			hql.append(" and b.burseName.code = ?");
			values.add(burse.getBurseName().getCode());
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void saveBurseInfo(CountryBurseInfo burse) {
		//  Auto-generated method stub
		save(burse);
	}

	@Override
	public void updateBurseInfo(CountryBurseInfo burse) {
		//  Auto-generated method stub
		update(burse);
	}

	@Override
	public void deleteBurseInfo(CountryBurseInfo burse) {
		//  Auto-generated method stub
		delete(burse);
	}

	@Override
	public CountryBurseInfo getBurseInfoById(String id) {
		//  Auto-generated method stub
		String hql = " from CountryBurseInfo b where 1 = 1 and b.id = ?";
		return (CountryBurseInfo) this.queryUnique(hql, new Object[]{id});
	}

	@Override
	public long countCountryBurse() {
		//  Auto-generated method stub
		String hql = "select count(b.id) from CountryBurseInfo b where 1 = 1";
		return (Long)this.queryUnique(hql, new Object[]{});
	}

	/* (非 Javadoc) 
	* <p>Title: getStuBurseList</p> 
	* <p>Description: </p> 
	* @param student
	* @return 
	* @see com.uws.reward.dao.ICountryBurseDao#getStuBurseList(com.uws.domain.orientation.StudentInfoModel) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<CountryBurseInfo> getStuBurseList(StudentInfoModel student) {
		//  Auto-generated method stub
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from CountryBurseInfo b where 1=1");
		if(DataUtil.isNotNull(student)) {
			hql.append(" and b.stuId.id = ?");
			values.add(student.getId());
		}
		return this.query(hql.toString(), values.toArray());
	}

}

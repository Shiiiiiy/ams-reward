package com.uws.reward.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.reward.dao.ICountryBurseDao;
import com.uws.reward.service.ICountryBurseService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;

/** 
* @ClassName: CountryBurseServiceImpl 
* @Description:  国家奖助serviceImpl
* @author zhangyb 
* @date 2015年8月27日 上午11:16:16  
*/
@Repository("countryBurseService")
public class CountryBurseServiceImpl extends BaseServiceImpl implements
		ICountryBurseService {
	
	@Autowired
	private ICountryBurseDao countryBurseDao;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IStudentCommonService studentCommonService;
	
	
	@Override
	public Page queryBursePage(CountryBurseInfo burse, int pageNo,
			int pageSize) {
		//  Auto-generated method stub
		return this.countryBurseDao.queryBursePage(burse, pageNo, pageSize);
	}

	@Override
	public void saveBurseInfo(CountryBurseInfo burse) {
		//  Auto-generated method stub
		this.countryBurseDao.saveBurseInfo(burse);
	}

	@Override
	public void updateBurseInfo(CountryBurseInfo burse) {
		//  Auto-generated method stub
		this.countryBurseDao.updateBurseInfo(burse);
	}

	@Override
	public void deleteBurseInfo(CountryBurseInfo burse) {
		//  Auto-generated method stub
		this.countryBurseDao.deleteBurseInfo(burse);
	}

	@Override
	public CountryBurseInfo getBurseInfoById(String id) {
		//  Auto-generated method stub
		return this.countryBurseDao.getBurseInfoById(id);
	}
	
	@Override
	public void importData(List<CountryBurseInfo> list) {
		//  Auto-generated method stub
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		for(CountryBurseInfo burse : list) {
			StudentInfoModel stu = this.studentCommonService.queryStudentById(burse.getStuNum());
			if(DataUtil.isNotNull(burse.getHelpGradeStr())){
				burse.setBurseName(this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "HELP_AWARD"));
			}
			burse.setCreator(user);
			burse.setStuId(stu);
			this.countryBurseDao.saveBurseInfo(burse);
		}
	}

	@Override
	public void importCountryData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		//  Auto-generated method stub
		Map map = new HashMap();
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		for(Object[] array : list) {
			CountryBurseInfo info = (CountryBurseInfo)array[0];
			map.put(info.getStuId().getId()+info.getSchoolYear().getName()+info.getBurseName().getName(), info);
		}
		ImportUtil iu = new ImportUtil();
		List<CountryBurseInfo> infoList = iu.getDataList(filePath, "importCountryBurse", null, CountryBurseInfo.class);     //Excel数据
		for(CountryBurseInfo info : infoList) {
			String flag = info.getStuNum() + info.getSchoolYearStr() + info.getBurseNameStr();
			if(!map.containsKey(flag)) {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(info.getStuNum());
				info.setStuId(stu);
				info.setCreator(user);
				this.countryBurseDao.saveBurseInfo(info);
			}else{
				CountryBurseInfo infoPo = (CountryBurseInfo) map.get(flag);  //已有数据
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
					infoPo.setComments(info.getComments());
					infoPo.setBurseName(info.getBurseName());
					infoPo.setSchoolYear(info.getSchoolYear());
					this.countryBurseDao.updateBurseInfo(infoPo);
				}
			}
		}
	}
	
	@Override
	public void importInspireData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		//  Auto-generated method stub
		Map map = new HashMap();
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		for(Object[] array : list) {
			CountryBurseInfo info = (CountryBurseInfo)array[0];
			map.put(info.getStuId().getId()+info.getSchoolYear().getName()+info.getBurseName().getName(), info);
		}
		ImportUtil iu = new ImportUtil();
		List<CountryBurseInfo> infoList = iu.getDataList(filePath, "importInspireBurse", null, CountryBurseInfo.class);     //Excel数据
		for(CountryBurseInfo info : infoList) {
			String flag = info.getStuNum() + info.getSchoolYearStr() + info.getBurseNameStr();
			if(!map.containsKey(flag)) {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(info.getStuNum());
				info.setStuId(stu);
				info.setCreator(user);
				this.countryBurseDao.saveBurseInfo(info);
			}else{
				CountryBurseInfo infoPo = (CountryBurseInfo) map.get(flag);  //已有数据
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
					infoPo.setComments(info.getComments());
					infoPo.setBurseName(info.getBurseName());
					infoPo.setSchoolYear(info.getSchoolYear());
					this.countryBurseDao.updateBurseInfo(infoPo);
				}
			}
		}
	}
	
	@Override
	public void importHelpData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		//  Auto-generated method stub
		Map map = new HashMap();
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		for(Object[] array : list) {
			CountryBurseInfo info = (CountryBurseInfo)array[0];
			map.put(info.getStuId().getId()+info.getSchoolYear().getName()+info.getHelpGrade().getName(), info);
		}
		ImportUtil iu = new ImportUtil();
		List<CountryBurseInfo> infoList = iu.getDataList(filePath, "importHelpBurse", null, CountryBurseInfo.class);     //Excel数据
		for(CountryBurseInfo info : infoList) {
			String flag = info.getStuNum() + info.getSchoolYearStr() + info.getHelpGradeStr();
			if(!map.containsKey(flag)) {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(info.getStuNum());
				info.setBurseName(this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "HELP_AWARD"));
				info.setStuId(stu);
				info.setCreator(user);
				this.countryBurseDao.saveBurseInfo(info);
			}else{
				CountryBurseInfo infoPo = (CountryBurseInfo) map.get(flag);  //已有数据
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
					infoPo.setComments(info.getComments());
					infoPo.setBurseName(this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "HELP_AWARD"));
					infoPo.setSchoolYear(info.getSchoolYear());
					infoPo.setHelpGrade(info.getHelpGrade());
					this.countryBurseDao.updateBurseInfo(infoPo);
				}
			}
		}
	}

	@Override
	public List<Object[]> compareData(List<CountryBurseInfo> list)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException {
		//  Auto-generated method stub
		List compareList = new ArrayList();
		Object[] array = (Object[])null;
		long count = this.countryBurseDao.countCountryBurse();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.countryBurseDao.queryBursePage(new CountryBurseInfo(), i+1, 10);
				List<CountryBurseInfo> infoList = (List<CountryBurseInfo>) page.getResult();
				for(CountryBurseInfo info : infoList) {
					for(CountryBurseInfo xls : list) {
						if((info.getStuId().getId()+info.getSchoolYear().getName()+info.getBurseName().getName()).equals
								(xls.getStuNum() + xls.getSchoolYearStr()+xls.getBurseNameStr())) {
							array = new Object[]{info,xls};
							compareList.add(array);
						}
					}
				}
			}
		}
		return compareList;
	}

	@Override
	public List<Object[]> compareHelpData(List<CountryBurseInfo> list)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException {
		//  Auto-generated method stub
		List compareList = new ArrayList();
		Object[] array = (Object[])null;
		long count = this.countryBurseDao.countCountryBurse();
		CountryBurseInfo countryBurse = new CountryBurseInfo();
		Dic helpBurse = this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "HELP_AWARD"); 
		countryBurse.setBurseName(helpBurse);
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.countryBurseDao.queryBursePage(countryBurse, i+1, 10);
				List<CountryBurseInfo> infoList = (List<CountryBurseInfo>) page.getResult();
				for(CountryBurseInfo info : infoList) {
					for(CountryBurseInfo xls : list) {
						if((info.getStuId().getId()+info.getSchoolYear().getName()+info.getHelpGrade().getName()).equals
								(xls.getStuNum() + xls.getSchoolYearStr()+xls.getHelpGradeStr())) {
							array = new Object[]{info,xls};
							compareList.add(array);
						}
					}
				}
			}
		}
		return compareList;
	}
	
	/* (非 Javadoc) 
	* <p>Title: getStuBurseList</p> 
	* <p>Description: </p> 
	* @param student
	* @return 
	* @see com.uws.reward.service.ICountryBurseService#getStuBurseList(com.uws.domain.orientation.StudentInfoModel) 
	*/
	@Override
	public List<CountryBurseInfo> getStuBurseList(StudentInfoModel student) {
		//  Auto-generated method stub
		return this.countryBurseDao.getStuBurseList(student);
	}

}

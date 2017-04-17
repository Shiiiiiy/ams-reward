/**   
* @Title: CollegeAwardServiceImpl.java 
* @Package com.uws.reward.service.impl 
* @author zhangyb   
* @date 2015年12月31日 上午10:48:59 
* @version V1.0   
*/
package com.uws.reward.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CollegeAwardInfo;
import com.uws.reward.dao.ICollegeAwardDao;
import com.uws.reward.service.ICollegeAwardService;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

/** 
 * @ClassName: CollegeAwardServiceImpl 
 * @Description: 校内奖励service 
 * @author zhangyb 
 * @date 2015年12月31日 上午10:48:59  
 */
@Service("collegeAwardService")
public class CollegeAwardServiceImpl extends BaseServiceImpl implements
		ICollegeAwardService {
	@Autowired
	private ICollegeAwardDao collegeAwardDao;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private IStudentCommonService studentCommonService;

	/* (非 Javadoc) 
	* <p>Title: queryCollegeAwardPage</p> 
	* <p>Description: </p> 
	* @param awardInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.reward.service.ICollegeAwardService#queryCollegeAwardPage(com.uws.domain.reward.CollegeAwardInfo, int, int) 
	*/
	@Override
	public Page queryCollegeAwardPage(CollegeAwardInfo awardInfo, int pageNo,
			int pageSize) {
		return this.collegeAwardDao.queryCollegeAwardPage(awardInfo, pageNo, pageSize);
	}

	/* (非 Javadoc) 
	* <p>Title: saveAwardInfo</p> 
	* <p>Description: </p> 
	* @param awardInfo 
	* @see com.uws.reward.service.ICollegeAwardService#saveAwardInfo(com.uws.domain.reward.CollegeAwardInfo) 
	*/
	@Override
	public void saveAwardInfo(CollegeAwardInfo awardInfo,String[] fileId) {
		this.collegeAwardDao.saveAwardInfo(awardInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(awardInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, awardInfo.getId());
		    }	
	}

	/* (非 Javadoc) 
	* <p>Title: delAwardInfo</p> 
	* <p>Description: </p> 
	* @param awardInfo 
	* @see com.uws.reward.service.ICollegeAwardService#delAwardInfo(com.uws.domain.reward.CollegeAwardInfo) 
	*/
	@Override
	public void delAwardInfo(CollegeAwardInfo awardInfo) {
		this.collegeAwardDao.delAwardInfo(awardInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateAwardInfo</p> 
	* <p>Description: </p> 
	* @param awardInfo 
	* @see com.uws.reward.service.ICollegeAwardService#updateAwardInfo(com.uws.domain.reward.CollegeAwardInfo) 
	*/
	@Override
	public void updateAwardInfo(CollegeAwardInfo awardInfo,String[] fileId) {
		this.collegeAwardDao.updateAwardInfo(awardInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(awardInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, awardInfo.getId());
		    }
	}

	/* (非 Javadoc) 
	* <p>Title: getAwardInfoById</p> 
	* <p>Description: </p> 
	* @param awardInfoId
	* @return 
	* @see com.uws.reward.service.ICollegeAwardService#getAwardInfoById(java.lang.String) 
	*/
	@Override
	public CollegeAwardInfo getAwardInfoById(String awardInfoId) {
		return this.collegeAwardDao.getAwardInfoById(awardInfoId);
	}

	/* (非 Javadoc) 
	* <p>Title: checkAwardInfo</p> 
	* <p>Description: </p> 
	* @param schoolYear
	* @param schoolTerm
	* @param studentId
	* @param awardName
	* @return 
	* @see com.uws.reward.service.ICollegeAwardService#checkAwardInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String) 
	*/
	@Override
	public long checkAwardInfo(String schoolYear, String schoolTerm,
			String studentId, String awardName) {
		return this.collegeAwardDao.checkAwardInfo(schoolYear, schoolTerm, studentId, awardName);
	}

	/* (非 Javadoc) 
	* <p>Title: importData</p> 
	* <p>Description: </p> 
	* @param list 
	* @see com.uws.reward.service.ICollegeAwardService#importData(java.util.List) 
	*/
	@Override
	public void importData(List<CollegeAwardInfo> list) {

		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		for(CollegeAwardInfo info : list) {
			StudentInfoModel stu = this.studentCommonService.queryStudentById(info.getStudentIdStr());
			info.setStudentId(stu);
			info.setCreator(user);
			this.collegeAwardDao.saveAwardInfo(info);
		}
	}

	/* (非 Javadoc) 
	* <p>Title: compareCollegeAward</p> 
	* <p>Description: </p> 
	* @param list 
	* @see com.uws.reward.service.ICollegeAwardService#compareCollegeAward(java.util.List) 
	*/
	@Override
	public void compareCollegeAward(List<CollegeAwardInfo> list) {
		
		long count = this.collegeAwardDao.countCollegeAwardNum();
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		if(count != 0L) {
				Page page = this.queryCollegeAwardPage(new CollegeAwardInfo(), 1, (int)count);
				List<CollegeAwardInfo> infoList = (List<CollegeAwardInfo>) page.getResult();
				if(infoList.size() > 0) {
					for(CollegeAwardInfo xls : list) {
						boolean flag = true;
						for(CollegeAwardInfo info : infoList) {
							if((info.getStudentId().getId() + info.getAwardName().trim() + 
									info.getSchoolYear().getName() + info.getSchoolTerm().getName()).equals(xls.getStudentIdStr() + xls.getAwardName().trim() + 
											xls.getSchoolYearStr() + xls.getSchoolTermStr())) {
								BeanUtils.copyProperties(xls, info, new String[]{"id","studentId","schoolYear","schoolTerm",
										"creator","createTime"});
								this.collegeAwardDao.updateAwardInfo(info);;
								flag = false;
								break;
							}
						}
						if(flag) {
							StudentInfoModel stu = this.studentCommonService.queryStudentById(xls.getStudentIdStr());
							xls.setStudentId(stu);
							xls.setCreator(user);
							this.collegeAwardDao.saveAwardInfo(xls);;
						}
						
					}
				}
		}else{
			for(CollegeAwardInfo xls : list) {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(xls.getStudentIdStr());
				xls.setStudentId(stu);
				xls.setCreator(user);
				this.collegeAwardDao.saveAwardInfo(xls);;
			}
		}
		
	}

}

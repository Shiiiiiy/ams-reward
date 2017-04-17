package com.uws.reward.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.common.dao.ICommonRoleDao;
import com.uws.common.service.IStudentCommonService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.PunishInfo;
import com.uws.reward.dao.IManagePunishDao;
import com.uws.reward.service.IManagePunishService;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

/** 
* @ClassName: ManagePunishServiceImpl 
* @Description:  惩罚管理serviceImpl
* @author zhangyb 
* @date 2015年8月24日 上午10:33:05  
*/
@Repository("managePunishService")
public class ManagePunishServiceImpl extends BaseServiceImpl implements
		IManagePunishService {

	@Autowired
	private IManagePunishDao managePunishDao;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private ICommonRoleDao commonRoleDao;
	
	@Override
	public Page queryPunishPage(PunishInfo punishInfo, int pageNo, int pageSize) {
		//  Auto-generated method stub
		return this.managePunishDao.queryPunishPage(punishInfo, pageNo, pageSize);
	}

	@Override
	public void savePunish(PunishInfo punishInfo,String[] fileId) {
		//  Auto-generated method stub
		this.managePunishDao.savePunish(punishInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(punishInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, punishInfo.getId());
		  }
	}

	@Override
	public void updatePunish(PunishInfo punishInfo,String[] fileId) {
		//  Auto-generated method stub
		this.managePunishDao.updatePunish(punishInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(punishInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, punishInfo.getId());
		  }
	}

	@Override
	public void deletePunish(PunishInfo punishInfo) {
		//  Auto-generated method stub
		this.managePunishDao.deletePunish(punishInfo);
	}

	@Override
	public PunishInfo getPunishInfoById(String id) {
		//  Auto-generated method stub
		return this.managePunishDao.getPunishInfoById(id);
	}

	@Override
	public boolean checkPunish(String stuId, String punishNum) {
		//  Auto-generated method stub
		List<PunishInfo> infoList = this.managePunishDao.getPunishList(stuId, punishNum);
		if(infoList.size() > 0) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void importData(List<PunishInfo> list) {
		//  Auto-generated method stub
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		Dic punishStatus = this.dicUtil.getDicInfo("PUNISH_STATUS", "EXECUTED");
		for(PunishInfo info : list) {
			StudentInfoModel stu = this.studentCommonService.queryStudentById(info.getStuIdStr());
			info.setCreator(user);
			info.setPunishStatus(punishStatus);
			info.setStuId(stu);
			this.managePunishDao.savePunish(info);
		}
	}

	@Override
	public void importData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		//  Auto-generated method stub
		Map map = new HashMap();
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		Dic punishStatus = this.dicUtil.getDicInfo("PUNISH_STATUS", "EXECUTED");
		for(Object[] array : list) {
			PunishInfo info = (PunishInfo)array[0];
			map.put(info.getStuId().getId()+info.getPunishNum(), info);
		}
		ImportUtil iu = new ImportUtil();
		List<PunishInfo> infoList = iu.getDataList(filePath, "importPunish", null, PunishInfo.class);     //Excel数据
		for(PunishInfo info : infoList) {
			String flag = info.getStuIdStr() + info.getPunishNum();
			if(!map.containsKey(flag)) {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(info.getStuIdStr());
				info.setStuId(stu);
				info.setCreator(user);
				info.setPunishStatus(punishStatus);
				this.managePunishDao.savePunish(info);
			}else{
				PunishInfo infoPo = (PunishInfo) map.get(flag);  //已有数据
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
//					StudentInfoModel stu = info.getStuId();
//					infoPo.setStuId(stu);
//					infoPo.setPunishNum(info.getPunishNum());
					infoPo.setComments(info.getComments());
					infoPo.setPunishStartDate(info.getPunishStartDate());
					infoPo.setPunishEndDate(info.getPunishEndDate());
					infoPo.setPunishYear(info.getPunishYear());
					infoPo.setPunishTerm(info.getPunishTerm());
					infoPo.setPunish(info.getPunish());
					infoPo.setPunishReason(info.getPunishReason());
					this.managePunishDao.updatePunish(infoPo);
				}
			}
		}
	}

	@Override
	public List<Object[]> compareData(List<PunishInfo> list)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException {
		//  Auto-generated method stub
		List compareList = new ArrayList();
		Object[] array = (Object[])null;
		long count = this.managePunishDao.countPunishNum();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.queryPunishPage(new PunishInfo(), i+1, 10);
				List<PunishInfo> infoList = (List<PunishInfo>) page.getResult();
				for(PunishInfo info : infoList) {
					for(PunishInfo xls : list) {
						if((info.getStuId().getId()+info.getPunishNum()).equals(xls.getStuIdStr() + xls.getPunishNum())) {
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
	public Page queryPunishPage(PunishInfo punishInfo, int pageNo,
			int pageSize, String flag) {
		//  Auto-generated method stub
		return this.managePunishDao.queryPunishPage(punishInfo, pageNo, pageSize,flag);
	}

	/* (非 Javadoc) 
	* <p>Title: getStuPunishList</p> 
	* <p>Description: </p> 
	* @param student
	* @return 
	* @see com.uws.reward.service.IManagePunishService#getStuPunishList(com.uws.domain.orientation.StudentInfoModel) 
	*/
	@Override
	public List<PunishInfo> getStuPunishList(PunishInfo punishInfo) {
		//  Auto-generated method stub
		return this.managePunishDao.getStuPunishList(punishInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updatePunishInfo</p> 
	* <p>Description: </p> 
	* @param list 
	* @see com.uws.reward.service.IManagePunishService#updatePunishInfo(java.util.List) 
	*/
	@Override
	public void comparePunishInfo(List<PunishInfo> list) {
		
		long count = this.managePunishDao.countPunishNum();
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
		Dic punishStatus = this.dicUtil.getDicInfo("PUNISH_STATUS", "EXECUTED");
		if(count != 0L) {
				Page page = this.queryPunishPage(new PunishInfo(), 1, (int)count);
				List<PunishInfo> infoList = (List<PunishInfo>) page.getResult();
				if(infoList.size() > 0) {
					for(PunishInfo xls : list) {
						boolean flag = true;
						for(PunishInfo info : infoList) {
							if((info.getStuId().getId()+info.getPunish().getName()+info.getPunishYear().getName()+
									info.getPunishTerm().getName()+info.getPunishReason().trim()).equals(xls.getStuIdStr() 
											+xls.getPunishStr()+ xls.getPunishYearStr()
											+ xls.getPunishTermStr() + xls.getPunishReason().trim())) {
								BeanUtils.copyProperties(xls, info, new String[]{"id","stuId","punish","punishYear","punishTerm",
										"punishStatus","creator","punishCode","punishDate","punishInfo","punishTypeCode","punishRepealDate",
										"punishRepealNum","appealDate","examineDate","examineResult"});
								this.managePunishDao.updatePunish(info);
								flag = false;
								break;
							}
						}
						if(flag) {
							StudentInfoModel stu = this.studentCommonService.queryStudentById(xls.getStuIdStr());
							xls.setStuId(stu);
							xls.setCreator(user);
							xls.setPunishStatus(punishStatus);
							this.managePunishDao.savePunish(xls);
						}
						
					}
				}
		}else{
			for(PunishInfo xls : list) {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(xls.getStuIdStr());
				xls.setStuId(stu);
				xls.setCreator(user);
				xls.setPunishStatus(punishStatus);
				this.managePunishDao.savePunish(xls);
			}
		}
	}

	/* (非 Javadoc) 
	* <p>Title: checkUserIsExist</p> 
	* <p>Description: </p> 
	* @param userId
	* @param roleCode
	* @return 
	* @see com.uws.reward.service.IManagePunishService#checkUserIsExist(java.lang.String, java.lang.String) 
	*/
	@Override
	public boolean checkUserIsExist(String userId, String roleCode) {
		
		return this.commonRoleDao.checkUserIsExist(userId, roleCode);
	}

}

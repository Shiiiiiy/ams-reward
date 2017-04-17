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
import org.springframework.stereotype.Service;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ClassApplyInfo;
import com.uws.reward.dao.IClassApplyDao;
import com.uws.reward.service.IClassApplyService;
import com.uws.reward.service.ISetAwardService;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

/** 
* @ClassName: ClassApplyServiceImpl 
* @Description:  班级申请service
* @author zhangyb 
* @date 2015年9月7日 下午6:16:29  
*/
@Service("classApplyService")
public class ClassApplyServiceImpl extends BaseServiceImpl implements
		IClassApplyService {

	@Autowired
	private IClassApplyDao classApplyDao;
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private ISetAwardService setAwardService;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private IStuJobTeamSetCommonService jobTeamService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	
	@Override
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo, int pageNo,
			int pageSize) {
		//  Auto-generated method stub
		return this.classApplyDao.queryClassApplyPage(classApplyInfo, pageNo, pageSize);
	}

	@Override
	public void saveClassApply(ClassApplyInfo classApplyInfo, String[] fileId) {
		//  Auto-generated method stub
		this.classApplyDao.saveClassApply(classApplyInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(classApplyInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, classApplyInfo.getId());
		    }
	}

	@Override
	public void updateClassApply(ClassApplyInfo classApplyInfo, String[] fileId) {
		//  Auto-generated method stub
		this.classApplyDao.updateClassApply(classApplyInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(classApplyInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, classApplyInfo.getId());
		    }
	}

	@Override
	public void deleteClassApply(ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		this.classApplyDao.deleteClassApply(classApplyInfo);
	}

	@Override
	public List<ClassApplyInfo> getClassApplyInfoList(
			ClassApplyInfo classApplyInfo) {
		//  Auto-generated method stub
		return this.classApplyDao.getClassApplyInfoList(classApplyInfo);
	}

	@Override
	public ApproveResult saveClassApplyApproveResult(String objectId,
			ApproveResult result, String nextApproverId, String[] fileId) {
		//  Auto-generated method stub
		if(DataUtil.isNotNull(result)) {
			ClassApplyInfo classInfo = new ClassApplyInfo();
			Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
			Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "ONESELF_ADD");
			Dic meetDic = this.dicUtil.getDicInfo("Y&N", "Y");
			if(DataUtil.isNotNull(objectId)){
				classInfo = this.getClassApplyInfoById(objectId);
			}
			if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
				classInfo.setApplyStatus(this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "REJECTED"));
			}else{
				classInfo.setApplyStatus(applyStatus);
			}
			classInfo.setProcessStatus(result.getProcessStatusCode());
			classInfo.setApproveStatus(result.getApproveStatus());
			if(DataUtil.isNotNull(nextApproverId)) {       //下一节点审批人不为空
				User nextApprover = new User();
				nextApprover.setId(nextApproverId);
				classInfo.setNextApprover(nextApprover);
			}else{
				classInfo.setNextApprover(null);
			}
			/*if(DataUtil.isNotNull(nextApproverId) && DataUtil.isNull(classInfo.getApproveStatus())) {
				classInfo.setApproveLevel("college");   //学院审批
			}else if(classInfo.getProcessStatus().equals("APPROVEING") && result.getApproveStatus().indexOf("通过") > -1){
				classInfo.setApproveLevel("office");    //为学生处审批
			}*/
			classInfo.setApplySource(applySource);
			classInfo.setMeetCondition(meetDic);
			if(DataUtil.isNotNull(objectId)) {
				this.classApplyDao.updateClassApply(classInfo);
			}else{
				this.classApplyDao.saveClassApply(classInfo);
			}
		}
		return result;
	}

	@Override
	public ClassApplyInfo getClassApplyInfoById(String id) {
		//  Auto-generated method stub
		return this.classApplyDao.getClassApplyInfoById(id);
	}

	@Override
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo, int pageNo,
			int pageSize, String userId, String[] objectIds) {
		//  Auto-generated method stub
		return this.classApplyDao.queryClassApplyPage(classApplyInfo, pageNo, pageSize, userId, objectIds);
	}

	@Override
	public List<ClassApplyInfo> getClassInfoList(ClassApplyInfo classInfo) {
		//  Auto-generated method stub
		return this.classApplyDao.getClassInfoList(classInfo);
	}

	@Override
	public void importData(List<ClassApplyInfo> classInfoList) {
		//  Auto-generated method stub
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "STUDENT_MANAGER_IMPORT");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "N");
		for(ClassApplyInfo cla : classInfoList) {
			BaseClassModel clas = this.baseDataService.findClassById(cla.getClassIdStr());
			AwardType award = this.setAwardService.getAwardTypeByCode(cla.getAwardTypeCode());
			cla.setClassId(clas);
			cla.setAwardTypeId(award);
			cla.setApplyStatus(applyStatus);
			cla.setApplySource(applySource);
			cla.setMeetCondition(meetDic);
			cla.setProcessStatus("PASS");
			this.classApplyDao.saveClassApply(cla);
		}
	}

	@Override
	public void importData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, 
			ExcelException, InstantiationException, ClassNotFoundException, Exception {
		//  Auto-generated method stub
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "STUDENT_MANAGER_IMPORT");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "N");
		String userId = sessionUtil.getCurrentUserId();
		User user = new User();
		user.setId(userId);
		Map map = new HashMap();
		for(Object[] array : list) {
			ClassApplyInfo cla = (ClassApplyInfo) array[0];
			map.put(cla.getClassId().getCode()+cla.getAwardTypeId().getAwardTypeCode(), cla);
		}
		ImportUtil iu = new ImportUtil();
		List<ClassApplyInfo> infoList = iu.getDataList(filePath, "importClassApprove", null, ClassApplyInfo.class);     //Excel数据
		for(ClassApplyInfo cla : infoList) {
			String flag = cla.getClassIdStr() + cla.getAwardTypeCode();
			if(!map.containsKey(flag)) {
				BaseClassModel clas = this.baseDataService.findClassById(cla.getClassIdStr());
				AwardType award = this.setAwardService.getAwardTypeByCode(cla.getAwardTypeCode());
				cla.setClassId(clas);
				cla.setAwardTypeId(award);
				cla.setApplyStatus(applyStatus);
				cla.setApplySource(applySource);
				cla.setMeetCondition(meetDic);
				cla.setProcessStatus("PASS");
				this.classApplyDao.saveClassApply(cla);
			}else{
				ClassApplyInfo classApplyInfo = (ClassApplyInfo) map.get(flag);
				if(StringUtils.isBlank(compareId) || !compareId.contains(classApplyInfo.getId())) {
					BeanUtils.copyProperties(cla, classApplyInfo, new String[]{"classId","awardTypeId","applySource","applyStatus",
							"createTime","id"});
					classApplyInfo.setMeetCondition(meetDic);
					classApplyInfo.setProcessStatus("PASS");
					this.updateClassApply(classApplyInfo,null);
				}
			}
		}
		
	}

	@Override
	public List<Object[]> compareData(List<ClassApplyInfo> list) {
		//  Auto-generated method stub
		List compareList = new ArrayList();
		Object[] array = (Object[])null;
		long count = this.classApplyDao.countClassApply();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.queryClassApplyPage(new ClassApplyInfo(), i+1, 10);
				List<ClassApplyInfo> infoList = (List<ClassApplyInfo>) page.getResult();
				for(ClassApplyInfo info : infoList) {
					for(ClassApplyInfo xls : list) {
						if((info.getClassId().getCode()+info.getAwardTypeId().getAwardTypeCode()).equals(
										xls.getClassIdStr() + xls.getAwardTypeCode())) {
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
	* <p>Title: saveMulResult</p> 
	* <p>Description: </p> 
	* @param resultList 
	* @see com.uws.reward.service.IClassApplyService#saveMulResult(java.util.List) 
	*/
	@Override
	public void saveMulResult(List<ApproveResult> resultList) {
		//  Auto-generated method stub
		ClassApplyInfo classInfo = new ClassApplyInfo();
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		if(resultList.size() > 0) {
			for(ApproveResult result : resultList) {
				classInfo = this.getClassApplyInfoById(result.getObjectId());
				if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
					classInfo.setApplyStatus(this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SAVED"));
				}else{
					classInfo.setApplyStatus(applyStatus);
				}
				classInfo.setProcessStatus(result.getProcessStatusCode());
				classInfo.setApproveStatus(result.getApproveStatus());
				User user = null;
				if(DataUtil.isNotNull(result.getNextApproverList()) && result.getNextApproverList().size() > 0) {
					Approver approve = result.getNextApproverList().get(0);
					user = new User(approve.getUserId());
				}
				if(DataUtil.isNotNull(user)) {       //下一节点审批人不为空
					classInfo.setNextApprover(user);
				}else{
					classInfo.setNextApprover(null);
				}
				this.classApplyDao.updateClassApply(classInfo);
			}
		}
	}

}

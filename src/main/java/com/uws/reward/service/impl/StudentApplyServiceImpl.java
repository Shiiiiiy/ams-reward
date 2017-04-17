package com.uws.reward.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IScoreService;
import com.uws.common.service.IStudentCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardStatisticInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ClassApplyInfo;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.reward.dao.ISetAwardDao;
import com.uws.reward.dao.IStudentApplyDao;
import com.uws.reward.service.IClassApplyService;
import com.uws.reward.service.ISetAwardService;
import com.uws.reward.service.IStudentApplyService;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

/** 
* @ClassName: StudentApplyServiceImpl 
* @Description:  学生申请serviceImpl
* @author zhangyb 
* @date 2015年8月21日 下午1:51:49  
*/
@Service("studentApplyService")
public class StudentApplyServiceImpl extends BaseServiceImpl implements
		IStudentApplyService {

	@Autowired
	private IStudentApplyDao studentApplyDao;
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private ISetAwardService setAwardService;
	@Autowired
	private ISetAwardDao setAwardDao;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private IClassApplyService classApplyService;
	@Autowired
	private IStudentCommonService studentCommonService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private IScoreService scoreService;
	
	
	@Override
	public Page queryStuApplyPage(StudentApplyInfo stuApply, int pageNo,
			int pageSize) {
		//  Auto-generated method stub
		return this.studentApplyDao.queryStuApplyPage(stuApply, pageNo, pageSize);
	}
	
	@Override
	public Page queryStuApplyPage(StudentApplyInfo stuApply, int pageNo,
			int pageSize,String userId,String[] objectIds) {
		return this.studentApplyDao.queryStuApplyPage(stuApply, pageNo, pageSize, userId,objectIds);
	}

	@Override
	public void saveStuApply(StudentApplyInfo stuApply, String[] fileId) {
		//  Auto-generated method stub
		this.studentApplyDao.saveStuApply(stuApply);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(stuApply.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, stuApply.getId());
		    }	
	}

	@Override
	public void updateStuApply(StudentApplyInfo stuApply, String[] fileId) {
		//  Auto-generated method stub
		this.studentApplyDao.updateStuApply(stuApply);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(stuApply.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, stuApply.getId());
		    }	
	}

	@Override
	public void deleteStuApply(StudentApplyInfo stuApply) {
		//  Auto-generated method stub
		this.studentApplyDao.deleteStuApply(stuApply);
	}

	@Override
	public List<StudentApplyInfo> getStuApplyInfoList(
			StudentApplyInfo stuApplyInfo) {
		//  Auto-generated method stub
		return this.studentApplyDao.getStuApplyInfoList(stuApplyInfo);
	}

	@Override
	public ApproveResult saveStuApplyApproveResult(String objectId,
			ApproveResult result, String nextApproverId, String[] fileId) {
		//  Auto-generated method stub
		
		if(DataUtil.isNotNull(result)) {
			StudentApplyInfo stuInfo = new StudentApplyInfo();
			Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
			Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "ONESELF_ADD");
			Dic meetDic = this.dicUtil.getDicInfo("Y&N", "Y");
			if(DataUtil.isNotNull(objectId)){
				stuInfo = this.getStuApplyInfoById(objectId);
			}
			if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
				stuInfo.setApplyStatus(this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "REJECTED"));
			}else{
				stuInfo.setApplyStatus(applyStatus);
			}
			stuInfo.setProcessStatus(result.getProcessStatusCode());
			stuInfo.setApproveStatus(result.getApproveStatus());
			if(DataUtil.isNotNull(nextApproverId)) {       //下一节点审批人不为空
				User nextApprover = new User();
				nextApprover.setId(nextApproverId);
				stuInfo.setNextApprover(nextApprover);
			}else{
				stuInfo.setNextApprover(null);
			}
			stuInfo.setApplySource(applySource);
			stuInfo.setMeetCondition(meetDic);
			if(DataUtil.isNotNull(objectId)) {
				this.studentApplyDao.updateStuApply(stuInfo);
			}else{
				this.studentApplyDao.saveStuApply(stuInfo);
			}
		}
		return result;
	}
	

	@Override
	public StudentApplyInfo getStuApplyInfoById(String id) {
		//  Auto-generated method stub
		return this.studentApplyDao.getStuApplyInfoById(id);
	}

	@Override
	public Page queryStudentPage(StudentApplyInfo stuApply, int pageNo,
			int pageSize) {
		//  Auto-generated method stub
		return this.studentApplyDao.queryStudentPage(stuApply, pageNo, pageSize);
	}

	@Override
	public List<StudentApplyInfo> getStuApplyList(StudentApplyInfo stuApply) {
		//  Auto-generated method stub
		return this.studentApplyDao.getStuInfoList(stuApply);
	}

	@Override
	public Map<String, Object> getAcademyResultMap(Dic year, String academyId) {
		//  Auto-generated method stub
		int first=0,second=0,third=0,threeGood = 0,goodLeader=0,goodClass = 0;   //合计所需
		BaseAcademyModel academy = this.baseDataService.findAcademyById(academyId);
		Map<String,Object> resultMap = new HashMap<String, Object>();    //各个班级所获各个奖项数量map
		List<BaseMajorModel> majorList = compService.queryMajorByCollage(academy.getId());
//		遍历该学院下的专业
		for(BaseMajorModel major : majorList) {
//			遍历该专业下的班级
			List<BaseClassModel> classList = compService.queryClassByMajor(major.getId());
			for(BaseClassModel classId : classList) {
				StudentApplyInfo stu = new StudentApplyInfo();
				ClassApplyInfo cla = new ClassApplyInfo();
				cla.setClassId(classId);
				stu.setStudentId(new StudentInfoModel());
				stu.getStudentId().setClassId(classId);
				stu.setProcessStatus("PASS");
				cla.setProcessStatus("PASS");
//				此学年发布的评奖评优项目名称
				List<AwardType> pubAwardList = this.setAwardService.getPublishedAward(year,null);
				AwardStatisticInfo statisticInfo = new AwardStatisticInfo(); 
				for(AwardType award : pubAwardList) {
					stu.setAwardTypeId(award);
					List<StudentApplyInfo> stuInfoList = this.getStuApplyList(stu);   //学生获奖list
					cla.setAwardTypeId(award);
					List<ClassApplyInfo> classInfoList = this.classApplyService.getClassInfoList(cla);  //班级获奖list
					if(stuInfoList.size() > 0) {
//						判断奖项名称set到AwardStatistic实体中
						if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.SANHAO)) {
							statisticInfo.setThreeGoodNum(stuInfoList.size()+"");
							threeGood+=stuInfoList.size();
						}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.STULEADER)) {
							statisticInfo.setStuLeaderNum(stuInfoList.size()+"");
							goodLeader+=stuInfoList.size();
						}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
								award.getSecondAwardName().getCode().equals("FIRST_AWARD")) {
							statisticInfo.setFirstAwardNum(stuInfoList.size()+"");
							first+=stuInfoList.size();
						}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
								award.getSecondAwardName().getCode().equals("SECOND_AWARD")) {
							statisticInfo.setSecondAwardNum(stuInfoList.size()+"");
							second+=stuInfoList.size();
						}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
								award.getSecondAwardName().getCode().equals("THIRD_AWARD")) {
							statisticInfo.setThirdAwardNum(stuInfoList.size()+"");
							third+=stuInfoList.size();
						}
					}
//					判断班级级别奖项
					if(classInfoList.size() > 0) {
						if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.GOODCLASS)) {
							statisticInfo.setGoodClassNum(classInfoList.size()+"");
							goodClass+=classInfoList.size();
						}
					}
				}
//				该学年该班级人数对象set jin map
				resultMap.put(classId.getId(), statisticInfo);
			}
		}
		AwardStatisticInfo statistic = new AwardStatisticInfo();
		statistic.setFirstAwardNum(first+"");
		statistic.setSecondAwardNum(second+"");
		statistic.setThirdAwardNum(third+"");
		statistic.setThreeGoodNum(threeGood+"");
		statistic.setStuLeaderNum(goodLeader+"");
		statistic.setGoodClassNum(goodClass+"");
		resultMap.put("sum", statistic);
		return resultMap;
	}

	@Override
	public Map<String, ArrayList<BaseClassModel>> getAcademyClassMap(String academyId) {
		//  Auto-generated method stub
		List<BaseMajorModel> majorList = compService.queryMajorByCollage(academyId);
		List<BaseClassModel> classList = new ArrayList<BaseClassModel>();
		Map<String,ArrayList<BaseClassModel>> classMap = new HashMap<String, ArrayList<BaseClassModel>>();
		for(BaseMajorModel major : majorList) {
//			遍历该专业下的班级
			classList = compService.queryClassByMajor(major.getId());
			classMap.put(major.getId(), (ArrayList<BaseClassModel>) classList);
		}
		return classMap;
	}

	@Override
	public Map<String, Object> getClassResultMap(Dic year, List<BaseClassModel> classList) {
		//  Auto-generated method stub
		int first=0,second=0,third=0,threeGood = 0,goodLeader=0,goodClass = 0;   //合计所需
		Map<String,Object> resultMap = new HashMap<String, Object>();    
		for(BaseClassModel classId : classList) {
			StudentApplyInfo stu = new StudentApplyInfo();
			ClassApplyInfo cla = new ClassApplyInfo();
			cla.setClassId(classId);
			stu.setStudentId(new StudentInfoModel());
			stu.getStudentId().setClassId(classId);
			stu.setProcessStatus("PASS");
			cla.setProcessStatus("PASS");
//			此学年发布的评奖评优项目名称
			List<AwardType> pubAwardList = this.setAwardService.getPublishedAward(year,null);
			AwardStatisticInfo statisticInfo = new AwardStatisticInfo(); 
			for(AwardType award : pubAwardList) {
				stu.setAwardTypeId(award);
				List<StudentApplyInfo> stuInfoList = this.getStuApplyList(stu);   //学生获奖list
				cla.setAwardTypeId(award);
				List<ClassApplyInfo> classInfoList = this.classApplyService.getClassInfoList(cla);  //班级获奖list
				if(stuInfoList.size() > 0) {
//					判断奖项名称set到AwardStatistic实体中
					if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.SANHAO)) {
						statisticInfo.setThreeGoodNum(stuInfoList.size()+"");
						threeGood+=stuInfoList.size();
					}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.STULEADER)) {
						statisticInfo.setStuLeaderNum(stuInfoList.size()+"");
						goodLeader+=stuInfoList.size();
					}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
							award.getSecondAwardName().getCode().equals("FIRST_AWARD")) {
						statisticInfo.setFirstAwardNum(stuInfoList.size()+"");
						first+=stuInfoList.size();
					}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
							award.getSecondAwardName().getCode().equals("SECOND_AWARD")) {
						statisticInfo.setSecondAwardNum(stuInfoList.size()+"");
						second+=stuInfoList.size();
					}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
							award.getSecondAwardName().getCode().equals("THIRD_AWARD")) {
						statisticInfo.setThirdAwardNum(stuInfoList.size()+"");
						third+=stuInfoList.size();
					}
				}
//				判断班级级别奖项
				if(classInfoList.size() > 0) {
					if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.GOODCLASS)) {
						statisticInfo.setGoodClassNum(classInfoList.size()+"");
						goodClass+=classInfoList.size();
					}
				}
			}
//			该学年该班级人数对象set jin map
			resultMap.put(classId.getId(), statisticInfo);
		}
		AwardStatisticInfo statistic = new AwardStatisticInfo();
		statistic.setFirstAwardNum(first+"");
		statistic.setSecondAwardNum(second+"");
		statistic.setThirdAwardNum(third+"");
		statistic.setThreeGoodNum(threeGood+"");
		statistic.setStuLeaderNum(goodLeader+"");
		statistic.setGoodClassNum(goodClass+"");
		resultMap.put("sum", statistic);
		return resultMap;
	}

	@Override
	public Map<String, Object> getCollegeResultMap(Dic year,
			List<BaseAcademyModel> academyList) {
		//  Auto-generated method stub
		int first=0,second=0,third=0,threeGood = 0,goodLeader=0,goodClass = 0;   //合计所需
		Map<String,Object> resultMap = new HashMap<String, Object>();    //各个班级所获各个奖项数量map
		for(BaseAcademyModel academy : academyList) {
			List<BaseMajorModel> majorList = compService.queryMajorByCollage(academy.getId());
			if(majorList.size() > 0) {
				for(BaseMajorModel major : majorList) {
					StudentApplyInfo stu = new StudentApplyInfo();
					ClassApplyInfo cla = new ClassApplyInfo();
					stu.setStudentId(new StudentInfoModel());
					stu.getStudentId().setMajor(major);
					cla.setClassId(new BaseClassModel());
					cla.getClassId().setMajor(major);
					stu.setProcessStatus("PASS");
					cla.setProcessStatus("PASS");
//					此学年发布的评奖评优项目名称
					List<AwardType> pubAwardList = this.setAwardService.getPublishedAward(year,null);
					AwardStatisticInfo statisticInfo = new AwardStatisticInfo(); 
					for(AwardType award : pubAwardList) {
						stu.setAwardTypeId(award);
						List<StudentApplyInfo> stuInfoList = this.getStuApplyList(stu);   //学生获奖list
						cla.setAwardTypeId(award);
						List<ClassApplyInfo> classInfoList = this.classApplyService.getClassInfoList(cla);  //班级获奖list
						if(stuInfoList.size() > 0) {
//							判断奖项名称set到AwardStatistic实体中
							if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.SANHAO)) {
								statisticInfo.setThreeGoodNum(stuInfoList.size()+"");
								threeGood+=stuInfoList.size();
							}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.STULEADER)) {
								statisticInfo.setStuLeaderNum(stuInfoList.size()+"");
								goodLeader+=stuInfoList.size();
							}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
									award.getSecondAwardName().getCode().equals("FIRST_AWARD")) {
								statisticInfo.setFirstAwardNum(stuInfoList.size()+"");
								first+=stuInfoList.size();
							}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
									award.getSecondAwardName().getCode().equals("SECOND_AWARD")) {
								statisticInfo.setSecondAwardNum(stuInfoList.size()+"");
								second+=stuInfoList.size();
							}else if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
									award.getSecondAwardName().getCode().equals("THIRD_AWARD")) {
								statisticInfo.setThirdAwardNum(stuInfoList.size()+"");
								third+=stuInfoList.size();
							}
						}
//						判断班级级别奖项
						if(classInfoList.size() > 0) {
							if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.GOODCLASS)) {
								statisticInfo.setGoodClassNum(classInfoList.size()+"");
								goodClass+=classInfoList.size();
							}
						}
					}
//					该学年该专业人数对象set jin map
					resultMap.put(major.getId(), statisticInfo);
				}
			}
		}
		AwardStatisticInfo statistic = new AwardStatisticInfo();
		statistic.setFirstAwardNum(first+"");
		statistic.setSecondAwardNum(second+"");
		statistic.setThirdAwardNum(third+"");
		statistic.setThreeGoodNum(threeGood+"");
		statistic.setStuLeaderNum(goodLeader+"");
		statistic.setGoodClassNum(goodClass+"");
		resultMap.put("sum", statistic);
		return resultMap;
	}

	@Override
	public Map<String, ArrayList<BaseMajorModel>> getMajorMap(
			List<BaseAcademyModel> academyList) {
		//  Auto-generated method stub
		Map<String,ArrayList<BaseMajorModel>> majorMap = new HashMap<String,ArrayList<BaseMajorModel>>();
		for(BaseAcademyModel academy : academyList) {
			List<BaseMajorModel> majorList = compService.queryMajorByCollage(academy.getId());
			majorMap.put(academy.getId(), (ArrayList<BaseMajorModel>) majorList);
		}
		return majorMap;
	}

	@Override
	public HSSFWorkbook packAcademyHssf(Dic year,String academyId,
			Map<String, Object> resultMap) {
		//  Auto-generated method stub
		BaseAcademyModel academy = this.baseDataService.findAcademyById(academyId);
		List<BaseMajorModel> majorList = compService.queryMajorByCollage(academy.getId());
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(academy.getName());
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(year.getName() + "学年" + academy.getName() + "评奖评优汇总统计表");
		cell.setCellStyle(style);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("专业");
		cell = row.createCell(1);
		cell.setCellValue("班级");
		cell = row.createCell(2);
		cell.setCellValue("一等奖学金");
		cell = row.createCell(3);
		cell.setCellValue("二等奖学金");
		cell = row.createCell(4);
		cell.setCellValue("三等奖学金");
		cell = row.createCell(5);
		cell.setCellValue("三好学生");
		cell = row.createCell(6);
		cell.setCellValue("优秀学生干部");
		cell = row.createCell(7);
		cell.setCellValue("先进班集体");
//		输出数据
		int i = 2;
		for(BaseMajorModel major : majorList) {
			List<BaseClassModel> classList = compService.queryClassByMajor(major.getId());
			int j = 0;
			for(BaseClassModel cla : classList) {
				row = sheet.createRow(j+i);
				AwardStatisticInfo statistic = (AwardStatisticInfo) resultMap.get(cla.getId());
				cell = row.createCell(1);
				cell.setCellValue(cla.getClassName());
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue(statistic.getFirstAwardNum());
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue(statistic.getSecondAwardNum());
				cell.setCellStyle(style);
				cell = row.createCell(4);
				cell.setCellValue(statistic.getThirdAwardNum());
				cell.setCellStyle(style);
				cell = row.createCell(5);
				cell.setCellValue(statistic.getThreeGoodNum());
				cell.setCellStyle(style);
				cell = row.createCell(6);
				cell.setCellValue(statistic.getStuLeaderNum());
				cell.setCellStyle(style);
				cell = row.createCell(7);
				cell.setCellValue(statistic.getGoodClassNum());
				cell.setCellStyle(style);
				j++;
			}
			row = sheet.getRow(i);
			if(DataUtil.isNotNull(row)) {
				cell = row.createCell(0);
				cell.setCellValue(major.getMajorName());
				cell.setCellStyle(style);
				if(classList.size() == 0) {
					sheet.addMergedRegion(new CellRangeAddress(i, i+classList.size(), 0, 0));
				}else{
					sheet.addMergedRegion(new CellRangeAddress(i, i+classList.size()-1, 0, 0));
				}
				i = i + classList.size();
			}
		}
		int lastRowNum = sheet.getLastRowNum();
		row = sheet.createRow(lastRowNum+1);
		HSSFCell lastCell = row.createCell(0);
		lastCell.setCellValue("合计");
		lastCell.setCellStyle(style);
		sheet.addMergedRegion(new CellRangeAddress(lastRowNum+1, lastRowNum+1, 0, 1));
		AwardStatisticInfo statistic = (AwardStatisticInfo) resultMap.get("sum");
		if(DataUtil.isNotNull(statistic)) {
			lastCell = row.createCell(2);
			lastCell.setCellValue(statistic.getFirstAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(3);
			lastCell.setCellValue(statistic.getSecondAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(4);
			lastCell.setCellValue(statistic.getThirdAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(5);
			lastCell.setCellValue(statistic.getThreeGoodNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(6);
			lastCell.setCellValue(statistic.getStuLeaderNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(7);
			lastCell.setCellValue(statistic.getGoodClassNum());
			lastCell.setCellStyle(style);
		}
		return wb;
	}

	@Override
	public HSSFWorkbook packCollegeHssf(Dic year,
			List<BaseAcademyModel> academyList, Map<String, Object> resultMap) {
		//  Auto-generated method stub
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(year.getName()+"评奖评优统计");
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 8500);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(year.getName() + "学年评奖评优汇总统计表");
		cell.setCellStyle(style);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("学院");
		cell = row.createCell(1);
		cell.setCellValue("专业");
		cell = row.createCell(2);
		cell.setCellValue("一等奖学金");
		cell = row.createCell(3);
		cell.setCellValue("二等奖学金");
		cell = row.createCell(4);
		cell.setCellValue("三等奖学金");
		cell = row.createCell(5);
		cell.setCellValue("三好学生");
		cell = row.createCell(6);
		cell.setCellValue("优秀学生干部");
		cell = row.createCell(7);
		cell.setCellValue("先进班集体");
//		输出数据
		int i = 2;
		for(BaseAcademyModel academy : academyList) {
			List<BaseMajorModel> majorList = compService.queryMajorByCollage(academy.getId());
			int j = 0;
			for(BaseMajorModel major : majorList) {
				row = sheet.createRow(j+i);
				AwardStatisticInfo statistic = (AwardStatisticInfo) resultMap.get(major.getId());
				cell = row.createCell(1);
				cell.setCellValue(major.getMajorName());
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue(statistic.getFirstAwardNum());
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue(statistic.getSecondAwardNum());
				cell.setCellStyle(style);
				cell = row.createCell(4);
				cell.setCellValue(statistic.getThirdAwardNum());
				cell.setCellStyle(style);
				cell = row.createCell(5);
				cell.setCellValue(statistic.getThreeGoodNum());
				cell.setCellStyle(style);
				cell = row.createCell(6);
				cell.setCellValue(statistic.getStuLeaderNum());
				cell.setCellStyle(style);
				cell = row.createCell(7);
				cell.setCellValue(statistic.getGoodClassNum());
				cell.setCellStyle(style);
				j++;
			}
			row = sheet.getRow(i);
			if(DataUtil.isNotNull(row)) {
				cell = row.createCell(0);
				cell.setCellValue(academy.getName());
				cell.setCellStyle(style);
				if(majorList.size() == 0) {
					sheet.addMergedRegion(new CellRangeAddress(i, i+majorList.size(), 0, 0));
				}else{
					sheet.addMergedRegion(new CellRangeAddress(i, i+majorList.size()-1, 0, 0));
				}
				i = i + majorList.size();
			}
		}
		int lastRowNum = sheet.getLastRowNum();
		row = sheet.createRow(lastRowNum+1);
		HSSFCell lastCell = row.createCell(0);
		lastCell.setCellValue("合计");
		lastCell.setCellStyle(style);
		sheet.addMergedRegion(new CellRangeAddress(lastRowNum+1, lastRowNum+1, 0, 1));
		AwardStatisticInfo statistic = (AwardStatisticInfo) resultMap.get("sum");
		if(DataUtil.isNotNull(statistic)) {
			lastCell = row.createCell(2);
			lastCell.setCellValue(statistic.getFirstAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(3);
			lastCell.setCellValue(statistic.getSecondAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(4);
			lastCell.setCellValue(statistic.getThirdAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(5);
			lastCell.setCellValue(statistic.getThreeGoodNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(6);
			lastCell.setCellValue(statistic.getStuLeaderNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(7);
			lastCell.setCellValue(statistic.getGoodClassNum());
			lastCell.setCellStyle(style);
		}
		return wb;
	}

	@Override
	public void importData(List<StudentApplyInfo> stuInfoList) {
		//  Auto-generated method stub
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "STUDENT_MANAGER_IMPORT");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "N");
		String userId = sessionUtil.getCurrentUserId();
		User user = new User();
		user.setId(userId);
		for(StudentApplyInfo stu : stuInfoList) {
			StudentInfoModel student = this.studentCommonService.queryStudentById(stu.getStudentIdStr());
			AwardType award = this.setAwardService.getAwardTypeByCode(stu.getAwardTypeCode());
			stu.setStudentId(student);
			stu.setAwardTypeId(award);
			stu.setApplyStatus(applyStatus);
			stu.setApplySource(applySource);
			stu.setMeetCondition(meetDic);
			stu.setProcessStatus("PASS");
			this.studentApplyDao.saveStuApply(stu);
		}
	}

	@Override
	public void importData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, 
			ExcelException, InstantiationException, ClassNotFoundException, Exception {
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "STUDENT_MANAGER_IMPORT");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "N");
		String userId = sessionUtil.getCurrentUserId();
		User user = new User();
		user.setId(userId);
		Map map = new HashMap();
		for(Object[] array : list) {
			StudentApplyInfo stu = (StudentApplyInfo) array[0];
			map.put(stu.getStudentId().getId()+stu.getAwardTypeId().getAwardTypeCode(), stu);
		}
		ImportUtil iu = new ImportUtil();
		List<StudentApplyInfo> infoList = iu.getDataList(filePath, "importStudentApprove", null, StudentApplyInfo.class);     //Excel数据
		for(StudentApplyInfo stu : infoList) {
			String flag = stu.getStudentIdStr() + stu.getAwardTypeCode();
			if(!map.containsKey(flag)) {
				StudentInfoModel student = this.studentCommonService.queryStudentById(stu.getStudentIdStr());
				AwardType award = this.setAwardService.getAwardTypeByCode(stu.getAwardTypeCode());
				stu.setStudentId(student);
				stu.setAwardTypeId(award);
				stu.setApplyStatus(applyStatus);
				stu.setApplySource(applySource);
				stu.setMeetCondition(meetDic);
				stu.setProcessStatus("PASS");
				this.studentApplyDao.saveStuApply(stu);
			}else{
				StudentApplyInfo stuApplyInfo = (StudentApplyInfo) map.get(flag);
				if(StringUtils.isBlank(compareId) || !compareId.contains(stuApplyInfo.getId())) {
					BeanUtils.copyProperties(stu, stuApplyInfo, new String[]{"studentId","awardTypeId","schoolYear","applySource","applyStatus",
							"createTime","id"});
					stuApplyInfo.setMeetCondition(meetDic);
					stuApplyInfo.setProcessStatus("PASS");
					this.updateStuApply(stuApplyInfo,null);
				}
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> compareData(List<StudentApplyInfo> list) {
		List compareList = new ArrayList();
		Object[] array = (Object[])null;
		long count = this.studentApplyDao.countStuApply();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.queryStuApplyPage(new StudentApplyInfo(), i+1, 10);
				List<StudentApplyInfo> infoList = (List<StudentApplyInfo>) page.getResult();
				for(StudentApplyInfo info : infoList) {
					for(StudentApplyInfo xls : list) {
						if((info.getStudentId().getId()+info.getAwardTypeId().getAwardTypeCode())
								.equals(xls.getStudentIdStr() + xls.getAwardTypeCode())) {
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
	* @see com.uws.reward.service.IStudentApplyService#saveMulResult(java.util.List) 
	*/
	@Override
	public void saveMulResult(List<ApproveResult> resultList) {
		//  Auto-generated method stub
		StudentApplyInfo stuInfo = new StudentApplyInfo();
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		if(resultList.size() > 0) {
			for(ApproveResult result : resultList) {
				stuInfo = this.getStuApplyInfoById(result.getObjectId());
				if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
					stuInfo.setApplyStatus(this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "REJECTED"));
				}else{
					stuInfo.setApplyStatus(applyStatus);
				}
				stuInfo.setProcessStatus(result.getProcessStatusCode());
				stuInfo.setApproveStatus(result.getApproveStatus());
				User user = null;
				if(DataUtil.isNotNull(result.getNextApproverList()) && result.getNextApproverList().size() > 0) {
					Approver approve = result.getNextApproverList().get(0);
					user = new User(approve.getUserId());
				}
				if(DataUtil.isNotNull(user)) {       //下一节点审批人不为空
					stuInfo.setNextApprover(user);
				}else{
					stuInfo.setNextApprover(null);
				}
				this.studentApplyDao.updateStuApply(stuInfo);
			}
		}
	}

	/* (非 Javadoc) 
	* <p>Title: packTeacherHssf</p> 
	* <p>Description: </p> 
	* @param year
	* @param classList
	* @param resultMap
	* @return 
	* @see com.uws.reward.service.IStudentApplyService#packTeacherHssf(com.uws.sys.model.Dic, java.util.List, java.util.Map) 
	*/
	@Override
	public HSSFWorkbook packTeacherHssf(Dic year,
			List<BaseClassModel> classList, Map<String, Object> resultMap) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(year.getName()+"评奖评优统计");
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(year.getName() + "学年评奖评优汇总统计表");
		cell.setCellStyle(style);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("班级");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("一等奖学金");
		cell = row.createCell(2);
		cell.setCellValue("二等奖学金");
		cell = row.createCell(3);
		cell.setCellValue("三等奖学金");
		cell = row.createCell(4);
		cell.setCellValue("三好学生");
		cell = row.createCell(5);
		cell.setCellValue("优秀学生干部");
		cell = row.createCell(6);
		cell.setCellValue("先进班集体");
//		输出数据
		int i = 2;
		for(BaseClassModel cla : classList) {
			row = sheet.createRow(i);
			AwardStatisticInfo statistic = (AwardStatisticInfo) resultMap.get(cla.getId());
			cell = row.createCell(0);
			cell.setCellValue(cla.getClassName());
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue(statistic.getFirstAwardNum());
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue(statistic.getSecondAwardNum());
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue(statistic.getThirdAwardNum());
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue(statistic.getThreeGoodNum());
			cell.setCellStyle(style);
			cell = row.createCell(5);
			cell.setCellValue(statistic.getStuLeaderNum());
			cell.setCellStyle(style);
			cell = row.createCell(6);
			cell.setCellValue(statistic.getGoodClassNum());
			cell.setCellStyle(style);
			i++;
		}
		int lastRowNum = sheet.getLastRowNum();
		row = sheet.createRow(lastRowNum+1);
		HSSFCell lastCell = row.createCell(0);
		lastCell.setCellValue("合计");
		lastCell.setCellStyle(style);
		AwardStatisticInfo statistic = (AwardStatisticInfo) resultMap.get("sum");
		if(DataUtil.isNotNull(statistic)) {
			lastCell = row.createCell(1);
			lastCell.setCellValue(statistic.getFirstAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(2);
			lastCell.setCellValue(statistic.getSecondAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(3);
			lastCell.setCellValue(statistic.getThirdAwardNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(4);
			lastCell.setCellValue(statistic.getThreeGoodNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(5);
			lastCell.setCellValue(statistic.getStuLeaderNum());
			lastCell.setCellStyle(style);
			lastCell = row.createCell(6);
			lastCell.setCellValue(statistic.getGoodClassNum());
			lastCell.setCellStyle(style);
		}
		return wb;
	}

	/* (非 Javadoc) 
	* <p>Title: getStuConInfoValue</p> 
	* <p>Description: </p> 
	* @param stuId
	* @param yearDic
	* @param infoList
	* @param map
	* @return 
	* @see com.uws.reward.service.IStudentApplyService#getStuConInfoValue(java.lang.String, com.uws.sys.model.Dic, java.util.List, java.util.Map) 
	*/
	@Override
	public List<ConditionInfo> getStuConInfoValue(String stuId, Dic yearDic,
			List<ConditionInfo> infoList, Map<String, String> evaluationMap) {
		List<ConditionInfo> valueList = new ArrayList<ConditionInfo>();
		
		for(ConditionInfo info : infoList) {
			ConditionInfo con = new ConditionInfo();
			if(info.getCheckOrNot().equals("Y")) {
				if(evaluationMap.containsKey(info.getConditionName())) {
					con.setConditionName(info.getConditionName());
					con.setTextName(info.getTextName());
					if(DataUtil.isNotNull(evaluationMap.get(info.getConditionName()))) {
						con.setConditionValue(evaluationMap.get(info.getConditionName()));
					}else{
						con.setConditionValue("");
					}
					valueList.add(con);
				}else if(info.getConditionName().equals(RewardConstant.averageScoreOfYear)){
					String value = this.scoreService.queryYearAvgScore(stuId, yearDic);
					con.setConditionName(info.getConditionName());
					con.setTextName(info.getTextName());
					if(DataUtil.isNotNull(value)) {
						con.setConditionValue(value);
					}else{
						con.setConditionValue("");
					}
					valueList.add(con);
				}else if(info.getConditionName().equals(RewardConstant.isPassDorm)){
					boolean checkDormIsGood = compService.checkDormIsGood(stuId);
					con.setConditionName(info.getConditionName());
					con.setTextName(info.getTextName());
					if(checkDormIsGood) {
						con.setConditionValue("是");
					}else{
						con.setConditionValue("否");
					}
					valueList.add(con);
				}else if(info.getConditionName().equals(RewardConstant.singleScore)) {
					String value = this.scoreService.queryStudentLowScore(stuId, yearDic, null);
					con.setConditionName(info.getConditionName());
					con.setTextName(info.getTextName());
					if(DataUtil.isNotNull(value)) {
						con.setConditionValue(value);
					}else{
						con.setConditionValue("");
					}
					valueList.add(con);
				}else if(info.getConditionName().equals(RewardConstant.sportScore)) {
					String value = this.scoreService.queryStudentSportScore(stuId, yearDic.getCode());
					con.setConditionName(info.getConditionName());
					con.setTextName(info.getTextName());
					if(DataUtil.isNotNull(value)) {
						con.setConditionValue(value);
					}else{
						con.setConditionValue("");
					}
					valueList.add(con);
				}else if(info.getConditionName().equals(RewardConstant.coursesPass)){
					boolean flag = this.scoreService.checkCoursesPass(stuId, yearDic.getCode());
					con.setConditionName(info.getConditionName());
					con.setTextName(info.getTextName());
					if(flag) {
						con.setConditionValue("是");
					}else{
						con.setConditionValue("否");
					}
					valueList.add(con);
				}else{
					con.setTextName(info.getTextName());
					con.setConditionName(info.getConditionName());
					con.setConditionValue("");
					valueList.add(con);
				}
			}else{
				con.setTextName(info.getTextName());
				con.setConditionName(info.getConditionName());
				con.setConditionValue("");
				valueList.add(con);
			}
		}
		return valueList;
	}

	/* (非 Javadoc) 
	* <p>Title: checkStuApplyXingZhi</p> 
	* <p>Description: </p> 
	* @param stuId
	* @param yearDic
	* @param applyStatus
	* @return 
	* @see com.uws.reward.service.IStudentApplyService#checkStuApplyXingZhi(java.lang.String, com.uws.sys.model.Dic, com.uws.sys.model.Dic) 
	*/
	@Override
	public boolean checkStuApplyXingZhi(String stuId, Dic yearDic,
			Dic applyStatus) {
		return this.studentApplyDao.checkStuApplyXingZhi(stuId, yearDic, applyStatus);
	}

	/* (非 Javadoc) 
	* <p>Title: updateStuApply</p> 
	* <p>Description: </p> 
	* @param stuApply 
	* @see com.uws.reward.service.IStudentApplyService#updateStuApply(com.uws.domain.reward.StudentApplyInfo) 
	*/
	@Override
	public void updateStuApply(StudentApplyInfo stuApply) {
		this.studentApplyDao.updateStuApply(stuApply);
	}

	/* (非 Javadoc) 
	* <p>Title: getApplyInfo</p> 
	* <p>Description: </p> 
	* @param secondAwardName
	* @param Year
	* @return 
	* @see com.uws.reward.service.IStudentApplyService#getApplyInfo(java.lang.String, com.uws.sys.model.Dic) 
	*/
	@Override
	public StudentApplyInfo getApplyInfo(String secondAwardName, Dic year,String stuId) {
		return this.studentApplyDao.getApplyInfo(secondAwardName, year, stuId);
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
		return this.studentApplyDao.checkStuSanHaoStuleader(stuId, yearDic, rewardCode);
	}
	
	/***
	 * 判断申请通过的名额
	 * @param stuApplyId
	 * @return
	 */
	@Override
	public boolean checkApprovedPass(String stuApplyId){
		int amount = 0;
		StudentApplyInfo stuInfo = this.studentApplyDao.getStuApplyInfoById(stuApplyId);
		AwardCondition condition = this.setAwardDao.getConByAwardId(stuInfo.getAwardTypeId().getId());
		QuotaInfo quotaInfo = this.setAwardDao.getCollegeQuotaInfoByConId(condition.getId(), stuInfo.getStudentId().getCollege().getId());
		if(DataUtil.isNotNull(quotaInfo)){
			amount = Integer.parseInt(quotaInfo.getNum()!=null?quotaInfo.getNum():"0");
		}
		int approveSum = this.studentApplyDao.queryApproveSum(stuInfo.getAwardTypeId().getId(), stuInfo.getStudentId().getCollege().getId());
		if(amount >0 && approveSum < amount){
			return true;
		}else{
			return false;
		}
	}
}

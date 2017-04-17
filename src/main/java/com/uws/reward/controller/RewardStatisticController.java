package com.uws.reward.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.ClassApplyInfo;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.reward.service.IClassApplyService;
import com.uws.reward.service.ISetAwardService;
import com.uws.reward.service.IStudentApplyService;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/** 
 * @ClassName: RewardStatisticController 
 * @Description:  评奖评优统计查询
 * @author zhangyb 
 * @date 2015年9月23日 上午11:57:33  
 */
@Controller
public class RewardStatisticController {

	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private ISetAwardService setAwardService;
	@Autowired
	private IStudentApplyService studentApplyService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IFlowInstanceService flowInstanceService;
	@Autowired
	private IClassApplyService classApplyService;
	@Autowired
	private IStuJobTeamSetCommonService jobTeamService;
	@Autowired
	private IDicService dicService;
	
	/** 
	* @Title: queryStudentPage 
	* @Description:  学生评奖评优查询
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentquery/opt-query/queryStudentPage.do"})
	public String queryStudentPage(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		boolean flag = ProjectSessionUtils.checkIsStudent(request);   //判断当前登录人是不是学生
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> awardTypeList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != stuInfo && null != stuInfo.getStudentId() 
				&& null != stuInfo.getStudentId().getCollege() 
				&& null != stuInfo.getStudentId().getCollege().getId()
				&& stuInfo.getStudentId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(stuInfo.getStudentId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != stuInfo && null != stuInfo.getStudentId() 
				&& null != stuInfo.getStudentId().getClassId() 
				&& null != stuInfo.getStudentId().getMajor() 
				&& null != stuInfo.getStudentId().getMajor().getId() 
				&& stuInfo.getStudentId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(stuInfo.getStudentId().getMajor().getId());
		}
		if(flag) {   //如果是学生
			String userId = this.sessionUtil.getCurrentUserId();
			StudentInfoModel stu = this.studentCommonService.queryStudentById(userId);
			stuInfo.setStudentId(stu);
		}
		Page page = this.studentApplyService.queryStudentPage(stuInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("awardTypeList", awardTypeList);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		model.addAttribute("flag", flag);
		return "reward/studentApply/queryApplyList";
	}
	
	/** 
	* @Title: viewStudent 
	* @Description:  查看学生评奖评优申请
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/studentquery/opt-query/viewStudent.do","/reward/studentquery/viewStudent.do"})
	public String viewStudent(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo) {
		
		String stuAppId = request.getParameter("id");
		stuInfo = this.studentApplyService.getStuApplyInfoById(stuAppId);
		AwardCondition condition = this.setAwardService.getConByAwardId(stuInfo.getAwardTypeId().getId());
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(stuAppId,ProjectConstants.IS_APPROVE_ENABLE);
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("instanceList", instanceList);
		model.addAttribute("conditionUploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(stuInfo.getId()));
		String returnPage = "";
		if(stuInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.SANHAO)) {
			returnPage = "reward/studentApply/threeGoodView";
		}else if(stuInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI)) {
			returnPage = "reward/studentApply/xingZhiView";
		}else{
			returnPage = "reward/studentApply/threeGoodView";
		}
		return returnPage;
	}
	
	/** 
	* @Title: queryClassPage 
	* @Description:  班级评奖评优查询
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classquery/opt-query/queryClassPage.do"})
	public String queryClassPage(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> awardTypeList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != classInfo && null != classInfo.getClassId() 
				&& null != classInfo.getClassId().getMajor() 
				&& null != classInfo.getClassId().getMajor().getCollage()
				&& null != classInfo.getClassId().getMajor().getCollage().getId()
				&& classInfo.getClassId().getMajor().getCollage().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(classInfo.getClassId().getMajor().getCollage().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != classInfo && null != classInfo.getClassId() 
				&& null != classInfo.getClassId().getMajor() 
				&& null != classInfo.getClassId().getMajor().getId() 
				&& classInfo.getClassId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(classInfo.getClassId().getMajor().getId());
		}
//		String userId = this.sessionUtil.getCurrentUserId();
//		boolean leaderTeacher = this.jobTeamService.isHeadMaster(userId);    //是否是班主任
		boolean isStudent = ProjectSessionUtils.checkIsStudent(request);     //是否是学生
		String returnPage = "";
		if(!isStudent) {
			returnPage = "reward/classApply/queryApplyList";
		}else{
			returnPage = "reward/studentApply/errorMessage";
		}
		Page page = this.classApplyService.queryClassApplyPage(classInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("awardTypeList", awardTypeList);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
//		model.addAttribute("flag", flag);
		return returnPage;
	}
	
	/** 
	* @Title: viewClass 
	* @Description:  查看班级评奖评优申请
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classquery/opt-query/viewClass.do"})
	public String viewClass(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo) {
		
		String classAppId = request.getParameter("id");
		classInfo = this.classApplyService.getClassApplyInfoById(classAppId);
		AwardCondition condition = this.setAwardService.getConByAwardId(classInfo.getAwardTypeId().getId());
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(classAppId,ProjectConstants.IS_APPROVE_ENABLE);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("instanceList", instanceList);
		model.addAttribute("conditionUploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(classInfo.getId()));
		String returnPage = "";
		if(classInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.GOODCLASS)) {
			returnPage = "reward/classApply/goodClassView";
		}else{
			returnPage = "reward/classApply/goodClassView";
		}
		return returnPage;
	}
	
	/** 
	* @Title: queryStatisticsPage 
	* @Description:  评奖评优汇总统计查询
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/statisticsquery/opt-query/queryStatisticsPage.do"})
	public String queryStatisticsPage(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo, ClassApplyInfo classInfo) {
		
		String returnSource = "";
		String userId = this.sessionUtil.getCurrentUserId();
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		if(!DataUtil.isNotNull(stuInfo.getSchoolYear())) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			stuInfo.setSchoolYear(currentYear);
		}
		//点击清空默认当前学年操作
		if(stuInfo.getSchoolYear()!=null && DataUtil.isNull(stuInfo.getSchoolYear().getCode())){
			Dic currentYear = SchoolYearUtil.getYearDic();
			stuInfo.setSchoolYear(currentYear);
		}
		Dic dicYearInfo = dicService.getDicInfo("YEAR", stuInfo.getSchoolYear().getCode());
		boolean isOfficeLeader = false;
		boolean isTeacherHeader = this.jobTeamService.isHeadMaster(userId);
		boolean isAcademy = CheckUtils.isCurrentOrgEqCollege(userId);
		boolean isStudent = ProjectSessionUtils.checkIsStudent(request);
		String curTeacherOrgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(DataUtil.isNotNull(curTeacherOrgId) && 
				curTeacherOrgId.equals(ProjectConstants.STUDNET_OFFICE_ORG_ID)) {
			isOfficeLeader = true;
		}
		if(isTeacherHeader) {             //当为班主任登录时       
			List<BaseClassModel> classList = this.jobTeamService.getHeadteacherClass(userId);
			Map<String,Object> resultMap = this.studentApplyService.getClassResultMap(stuInfo.getSchoolYear(), classList);
			model.addAttribute("tableTitle", stuInfo.getSchoolYear().getName()+
					classList.get(0).getMajor().getCollage().getName()+classList.get(0).getMajor().getMajorName() + "汇总统计");
			model.addAttribute("classList", classList);
			model.addAttribute("stuInfo", stuInfo);
			model.addAttribute("resultMap", resultMap);
			model.addAttribute("schoolYearList", schoolYearList);
			returnSource = "reward/awardStatistic/classLeaderStatistic";
		}else if(isOfficeLeader) {  //为学生处领导登录时
			List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
			Map<String,Object> resultMap = this.studentApplyService.getCollegeResultMap(stuInfo.getSchoolYear(), academyList);
			Map<String,ArrayList<BaseMajorModel>> majorMap = this.studentApplyService.getMajorMap(academyList);
			model.addAttribute("tableTitle", dicYearInfo.getName()+"评奖评优汇总统计");
			model.addAttribute("stuInfo", stuInfo);
			model.addAttribute("academyList", academyList);
			model.addAttribute("majorMap", majorMap);
			model.addAttribute("resultMap", resultMap);
			model.addAttribute("schoolYearList", schoolYearList);
			returnSource = "reward/awardStatistic/collegeStatistic";
		}else if(isAcademy){        //为二级学院领导
			String academyId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
			if(DataUtil.isNull(academyId)) {
				returnSource = "reward/studentApply/errorMessage";
			}else{
				BaseAcademyModel academy = this.baseDataService.findAcademyById(academyId);     //获取当前登录用户的学院ID
				if(DataUtil.isNotNull(academy)) {
					Map<String,Object> resultMap = this.studentApplyService.getAcademyResultMap(stuInfo.getSchoolYear(), academyId);    //各个班级所获各个奖项数量map
					List<BaseMajorModel> majorList = compService.queryMajorByCollage(academyId);
					Map<String,ArrayList<BaseClassModel>> classMap = this.studentApplyService.getAcademyClassMap(academyId);
					model.addAttribute("majorList", majorList);
					model.addAttribute("classMap", classMap);
					model.addAttribute("resultMap", resultMap);
					model.addAttribute("tableTitle", dicYearInfo.getName()+academy.getName()+"汇总统计");
				}
				model.addAttribute("stuInfo", stuInfo);
				model.addAttribute("schoolYearList", schoolYearList);
				returnSource = "reward/awardStatistic/academyStatistic";
			}
		}else if(!isStudent) {
			List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
			Map<String,Object> resultMap = this.studentApplyService.getCollegeResultMap(stuInfo.getSchoolYear(), academyList);
			Map<String,ArrayList<BaseMajorModel>> majorMap = this.studentApplyService.getMajorMap(academyList);
			model.addAttribute("tableTitle", dicYearInfo.getName()+"评奖评优汇总统计");
			model.addAttribute("stuInfo", stuInfo);
			model.addAttribute("academyList", academyList);
			model.addAttribute("majorMap", majorMap);
			model.addAttribute("resultMap", resultMap);
			model.addAttribute("schoolYearList", schoolYearList);
			returnSource = "reward/awardStatistic/collegeStatistic";
		}
		return returnSource;
	}
	
	/** 
	* @Title: exportScore 
	* @Description:  导出评奖评优汇总统计
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @param response    
	* @return void    
	* @throws 
	*/
	@SuppressWarnings("unused")
	@RequestMapping({"/reward/statisticsquery/opt-query/exportStatistic.do"})
	public void  exportScore(ModelMap model, HttpServletRequest request, StudentApplyInfo stuInfo, HttpServletResponse response) {
		String yearId=request.getParameter("schoolYear.code");
		String fileName = "";
		HSSFWorkbook wb = null;
		String userId = this.sessionUtil.getCurrentUserId();
		if(!DataUtil.isNotNull(yearId)) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			stuInfo.setSchoolYear(currentYear);
		}
		//点击清空默认当前学年操作
		if(yearId!=null && DataUtil.isNull(yearId)){
			Dic currentYear = SchoolYearUtil.getYearDic();
			stuInfo.setSchoolYear(currentYear);
		}
		Dic dicYearInfo = dicService.getDicInfo("YEAR",yearId);
		/*BaseAcademyModel academy = this.baseDataService.findAcademyById("19");     //获取当前登录用户的学院ID
		boolean isTeaHeader = this.jobTeamService.isHeadMaster(userId);
		boolean isCollegeLeader = false;*/
		boolean isOfficeLeader = false;
		boolean isTeacherHeader = this.jobTeamService.isHeadMaster(userId);
		boolean isAcademy = CheckUtils.isCurrentOrgEqCollege(userId);
		boolean isStudent = ProjectSessionUtils.checkIsStudent(request);
		String curTeacherOrgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(DataUtil.isNotNull(curTeacherOrgId) && 
				curTeacherOrgId.equals(ProjectConstants.STUDNET_OFFICE_ORG_ID)) {
			isOfficeLeader = true;
		}
		if(isAcademy){    //当为学院领导时
			//		学院下的所有班级统计
			BaseAcademyModel academy = this.baseDataService.findAcademyById(curTeacherOrgId);
			Map<String,Object> resultMap = this.studentApplyService.getAcademyResultMap(stuInfo.getSchoolYear(), curTeacherOrgId);    //各个班级所获各个奖项数量map
			wb = this.studentApplyService.packAcademyHssf(dicYearInfo,academy.getId(), resultMap);
			fileName = dicYearInfo.getName() + "学年" + academy.getName() + "评奖评优汇总统计.xls";
		}else if(isTeacherHeader) {   //当为班主任登录时
//			获取班主任所在班级
			List<BaseClassModel> classList = this.jobTeamService.getHeadteacherClass(userId);
			Map<String,Object> resultMap = this.studentApplyService.getClassResultMap(stuInfo.getSchoolYear(), classList);
			wb = this.studentApplyService.packTeacherHssf(dicYearInfo, classList, resultMap);
			fileName = dicYearInfo.getName() + "学年所带班级评奖评优汇总统计.xls";
		}else if(isOfficeLeader) {             //当为学生处领导登录时
			List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
			Map<String,Object> resultMap = this.studentApplyService.getCollegeResultMap(dicYearInfo, academyList);
			Map<String,ArrayList<BaseMajorModel>> majorMap = this.studentApplyService.getMajorMap(academyList);
			wb = this.studentApplyService.packCollegeHssf(dicYearInfo, academyList, resultMap);
			fileName = dicYearInfo.getName() + "学年评奖评优汇总统计.xls";
		}else{
			List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
			Map<String,Object> resultMap = this.studentApplyService.getCollegeResultMap(stuInfo.getSchoolYear(), academyList);
			Map<String,ArrayList<BaseMajorModel>> majorMap = this.studentApplyService.getMajorMap(academyList);
			wb = this.studentApplyService.packCollegeHssf(dicYearInfo, academyList, resultMap);
			fileName = dicYearInfo.getName() + "学年评奖评优汇总统计.xls";
		}
	    try {
	    	response.setContentType("application/x-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "iso-8859-1"));
			response.setCharacterEncoding("UTF-8");
			ServletOutputStream fos = response.getOutputStream();
			wb.write(fos);
			fos.close();
		} catch (UnsupportedEncodingException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
}

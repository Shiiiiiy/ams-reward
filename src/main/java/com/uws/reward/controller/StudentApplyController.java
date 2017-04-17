package com.uws.reward.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.JsonUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IEvaluationCommonService;
import com.uws.common.service.IScoreService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.reward.service.ISetAwardService;
import com.uws.reward.service.IStudentApplyService;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;


/**
 *@author:zhangyb
 *@version:2015年8月21日 上午11:40:12
 *@Description: 学生评优评奖申请controller
 *
 */
@Controller
public class StudentApplyController {
	
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IBaseDataService baseDataService;
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
	private IEvaluationCommonService evaluationService;
	@Autowired
	private IScoreService scoreService;
	
	/** 
	* @Title: queryStuApplyPage 
	* @Description:  学生申请列表查询
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @param award
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapply/opt-query/queryStuApplyPage.do"})
	public String queryStuApplyPage(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo,AwardType award) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		String curUserId = this.sessionUtil.getCurrentUserId();
		String schoolYear = request.getParameter("schoolYear.id");
		Dic yearDic = null;
		if(schoolYear == null){
			yearDic = SchoolYearUtil.getYearDic();
			award.setSchoolYear(yearDic);
	  	}
		User curUser = new User();
		curUser.setId(curUserId);
		Dic pubStatusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "PUBLISHED");
		Dic availableObject = this.dicUtil.getDicInfo("AWARDINFO_OBJECT", "STUDENT");   //适用对象为学生
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> awardTypeList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		AwardInfo awardInfo = new AwardInfo();
		awardInfo.setAvailableObject(availableObject);
		award.setAwardStatus(pubStatusDic);
		award.setAwardInfoId(awardInfo);
//		获取当前登录学生已保存或提交的申请
		StudentInfoModel stu = this.studentCommonService.queryStudentById(curUserId);
		stuInfo.setAwardTypeId(award);
		stuInfo.setStudentId(stu);
		List<StudentApplyInfo> stuApplyInfoList = this.studentApplyService.getStuApplyInfoList(stuInfo);
		Map<String, StudentApplyInfo> map = new HashMap<String, StudentApplyInfo>();
		if(stuApplyInfoList.size() > 0) {
			for(StudentApplyInfo info : stuApplyInfoList) {
				map.put(info.getAwardTypeId().getId(), info);
			}
			model.addAttribute("map", map);
		}
		Page page = this.setAwardService.queryAwardPage(award, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
//		判断奖项申请日期是否已截止
		@SuppressWarnings("unchecked")
		List<AwardType> awardList = (List<AwardType>) page.getResult();
		if(awardList.size() > 0) {
			Map<String,String> dateMap = new HashMap<String, String>();
			for(AwardType awardType : awardList) {
				Date startDate = awardType.getBeginDate(); 
				Date endDate = awardType.getEndDate();
				boolean flag = startDate.compareTo(DateUtil.getDate()) <=0 
						&& DateUtils.addDays(endDate, 1).compareTo(DateUtil.getDate())>=0;
				dateMap.put(awardType.getId(), flag+"");
			}
			model.addAttribute("dateMap", dateMap);
		}
		model.addAttribute("page", page);
		model.addAttribute("award", award);
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("awardTypeList", awardTypeList);
		return "reward/studentApply/queryStuApplyList";
	}
	
	/**
	 * @throws ParseException  
	* @Title: checkStuPermission 
	* @Description:  判断该学生是否符合该奖项的申请条件
	* @param  @param awardTypeId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapply/opt-query/checkStuPermission.do"})
	@ResponseBody
	public String checkStuPermission(@RequestParam String awardTypeId) throws ParseException {
		
		String returnFlag = "false";
		boolean flag = true;
		AwardCondition condition = this.setAwardService.getConByAwardId(awardTypeId); 
		List<ConditionInfo> infoList = this.setAwardService.getCheckConInfoList(condition.getId());
		String userId = this.sessionUtil.getCurrentUserId();
		StudentInfoModel stu = this.studentCommonService.queryStudentById(userId);
		//Dic yearDic = SchoolYearUtil.getYearDic();
		Dic yearDic = condition.getAwardId().getSchoolYear();
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SUBMITTED");
		Map<String,String> evaluationMap = new HashMap<String, String>();
		for(ConditionInfo info : infoList) {
			if(info != null && StringUtils.hasText(info.getConditionName()) && (info.getConditionName().contains("Rank"))){
				evaluationMap  = this.evaluationService.queryStudentEvaluationScore(yearDic.getId(), stu);
				if(evaluationMap == null || evaluationMap.size()<1){
					//有测评排名条件但是没有成绩则不符合条件
					flag = false;
				}
				break;
			}
		}
		if(flag){
			//设置条件校验
			flag = this.setAwardService.checkStuApplyPermission(userId, yearDic, infoList, evaluationMap);
		}
//		添加学生行知一二三等奖只能申请一个逻辑
		AwardType awardType = this.setAwardService.getAwardById(awardTypeId);
		if(awardType.getSecondAwardName() != null) {  //如果所选是一二三等奖的话
			if(awardType.getSecondAwardName().getName().indexOf(RewardConstant.xingZhiAwardName) > -1) {
				boolean applyFlag = this.studentApplyService.checkStuApplyXingZhi(userId, yearDic, applyStatus);
				if(applyFlag) {
					if(flag) {
						returnFlag = "true";
					}
				}else{
					returnFlag = "APPLYED";
				}
			}else{
				if(flag) {
					returnFlag = "true";
				}
			}
		}else{
			if(flag) {
				//新增需求：三好学生和优秀班干部互斥（只能申请一个）
				String rewardCode = 
						RewardConstant.SANHAO.equals(awardType.getAwardInfoId().getAwardCode()+"")?RewardConstant.STULEADER:RewardConstant.SANHAO;
				boolean applyFlag = this.studentApplyService.checkStuSanHaoStuleader(userId, yearDic, rewardCode);
				if(applyFlag){
					returnFlag = "true";
				}else{
					returnFlag = "SANHAOSTULEADER";
				}
			}
		}
		return returnFlag;
	}
	
	/** 
	* @Title: editStuApply 
	* @Description:  学生申请
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapply/opt-query/editStuApply.do"})
	public String editStuApply(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo) {
		
		String stuApplyId = request.getParameter("stuApplyId");
		String awardTypeId = request.getParameter("awardType");
		AwardCondition condition = null;
		if(DataUtil.isNotNull(stuApplyId)){
			stuInfo = this.studentApplyService.getStuApplyInfoById(stuApplyId);
			condition = this.setAwardService.getConByAwardId(stuInfo.getAwardTypeId().getId());
		} else {
			AwardType award = this.setAwardService.getAwardById(awardTypeId);
			stuInfo.setAwardTypeId(award);
			condition = this.setAwardService.getConByAwardId(awardTypeId);
			model.addAttribute("award", award);
		}
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		String userId = this.sessionUtil.getCurrentUserId();
		StudentInfoModel stu = this.studentCommonService.queryStudentById(userId);
		Dic yearDic = condition.getAwardId().getSchoolYear();
		Map<String,String> evaluationMap  = this.evaluationService.queryStudentEvaluationScore(yearDic.getId(), stu);
		stuInfo.setStudentId(stu);
		List<ConditionInfo> conValueList = this.studentApplyService.getStuConInfoValue(userId, yearDic, conInfoList, evaluationMap);
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("conValueList", conValueList);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("conditionUploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(stuInfo.getId()));
		String returnPage = "";
		if(stu == null || !DataUtil.isNotNull(stu)) {
			returnPage = "reward/studentApply/errorMessage";
		}else{
			if(stuInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.SANHAO)) {
				returnPage = "reward/studentApply/threeGoodApply";
			}else if(stuInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI)) {
				returnPage = "reward/studentApply/xingZhiApply";
			}else{
				returnPage = "reward/studentApply/threeGoodApply";
			}
		}
		return returnPage;
	}
	
	/** 
	* @Title: viewStudentApplyInfo 
	* @Description:  统计学生申请查看
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapply/opt-query/viewStudentApplyInfo.do"})
	public String viewStudentApplyInfo(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo) {
		
		String applyId = request.getParameter("id");
		stuInfo = this.studentApplyService.getStuApplyInfoById(applyId);
		AwardCondition condition = this.setAwardService.getConByAwardId(stuInfo.getAwardTypeId().getId());
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(applyId,ProjectConstants.IS_APPROVE_ENABLE);
		Map<String,String> evaluationMap  = this.evaluationService.queryStudentEvaluationScore(SchoolYearUtil.getYearDic().getId(), stuInfo.getStudentId());
		List<ConditionInfo> conValueList = this.studentApplyService.getStuConInfoValue(stuInfo.getStudentId().getId(), SchoolYearUtil.getYearDic(), conInfoList, evaluationMap);
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("conValueList", conValueList);
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
	 * 初始化当前流程
	 * @Title: saveCurProcess
	 * @Description: 初始化当前流程
	 * @param model
	 * @param request
	 * @param objectId			业务主键
	 * @param flags
	 * @param approveStatus		当前节点审批状态
	 * @param processStatusCode	流程当前状态
	 * @param nextApproverId	下一节点办理人
	 */
	@RequestMapping(value = {"/reward/studentapply/opt-add/saveCurProcess.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId,String[] fileId){
		
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				//发起审核流程
				result = flowInstanceService.initProcessInstance(objectId,"REWARD_STUDENT_APPLY_APPROVE", 
						 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
				result = this.studentApplyService.saveStuApplyApproveResult(objectId, result, nextApproverId, fileId);
				result.setResultFlag("success");
			} catch (Exception e) {
				result.setResultFlag("error");
			}
		}else{
			result.setResultFlag("deprecated");
	    }
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
	/** 
	* @Title: saveStuApply 
	* @Description:  保存学生申请
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapply/opt-query/saveStuApply.do"})
	public String saveStuApply(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo,String[] fileId) {
		
		String stuId = this.sessionUtil.getCurrentUserId();
		StudentInfoModel stu = this.studentCommonService.queryStudentById(stuId);
		String awardTypeId = request.getParameter("awardId");
		AwardType awardType = this.setAwardService.getAwardById(awardTypeId);
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SAVED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "ONESELF_ADD");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "Y");
		stuInfo.setApplyStatus(applyStatus);
		stuInfo.setApplySource(applySource);
		stuInfo.setMeetCondition(meetDic);
		stuInfo.setStudentId(stu);
		stuInfo.setAwardTypeId(awardType);
		if(DataUtil.isNotNull(stuInfo.getId())) {
			this.studentApplyService.updateStuApply(stuInfo, fileId);
		} else {
			this.studentApplyService.saveStuApply(stuInfo,fileId);
		}
		return "redirect:/reward/studentapply/opt-query/queryStuApplyPage.do";
	}
	
	
	/** 
	* @Title: saveStuApplyJson 
	* @Description:  直接点提交保存
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/studentapply/opt-query/saveStuApplyJson.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveStuApplyJson(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo,String[] fileId) {
		
		String stuId = this.sessionUtil.getCurrentUserId();
		StudentInfoModel stu = this.studentCommonService.queryStudentById(stuId);
		String awardTypeId = request.getParameter("awardId");
		AwardType awardType = this.setAwardService.getAwardById(awardTypeId);
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SAVED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "ONESELF_ADD");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "Y");
		stuInfo.setApplyStatus(applyStatus);
		stuInfo.setApplySource(applySource);
		stuInfo.setMeetCondition(meetDic);
		stuInfo.setStudentId(stu);
		stuInfo.setAwardTypeId(awardType);
		if(DataUtil.isNotNull(stuInfo.getId())) {
			this.studentApplyService.updateStuApply(stuInfo, fileId);
		}else{
			this.studentApplyService.saveStuApply(stuInfo, fileId);
		}
		return stuInfo.getId();
	}
	
	
	
}

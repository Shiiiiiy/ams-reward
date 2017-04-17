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
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ClassApplyInfo;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.reward.service.IClassApplyService;
import com.uws.reward.service.ISetAwardService;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.DicServiceImpl;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;

/** 
* @ClassName: ClassApplyController 
* @Description:  班级申请controller
* @author zhangyb 
* @date 2015年9月7日 下午6:18:36  
*/
@Controller
public class ClassApplyController {

	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IBaseDataService baseDataService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private ISetAwardService setAwardService;
	@Autowired
	private IClassApplyService classApplyService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IFlowInstanceService flowInstanceService;
	@Autowired
	private IStuJobTeamSetCommonService jobTeamService;
	
	/** 
	* @Title: queryClassApplyPage 
	* @Description:  班级申请列表页
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @param award
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapply/opt-query/queryClassApplyPage.do"})
	public String queryClassApplyPage(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo,AwardType award) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		String curUserId = this.sessionUtil.getCurrentUserId();
		String schoolYear = request.getParameter("awardTypeId.schoolYear.id");
		Dic yearDic = null;
		if(schoolYear == null){
			yearDic = SchoolYearUtil.getYearDic();
			award.setSchoolYear(yearDic);
	  	}else if(StringUtils.hasText(schoolYear)){
	  		Dic dic = new Dic();dic.setId(schoolYear);
			award.setSchoolYear(dic);
	  	}
		User curUser = new User();
		curUser.setId(curUserId);
		Dic pubStatusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "PUBLISHED");
		Dic currentYear = SchoolYearUtil.getYearDic();
		Dic availableObject = this.dicUtil.getDicInfo("AWARDINFO_OBJECT", "CLASS");   //适用对象为班级
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "UNAPPLY");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> awardTypeList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		AwardInfo awardInfo = new AwardInfo();
		awardInfo.setAvailableObject(availableObject);
		award.setAwardStatus(pubStatusDic);
		//award.setSchoolYear(currentYear);
		award.setAwardInfoId(awardInfo);
		classInfo.setAwardTypeId(award);
		boolean headTeacher = this.jobTeamService.isHeadMaster(curUserId);
		BaseTeacherModel teacher = this.baseDataService.findTeacherById(curUserId);
		if(headTeacher) {
			List<AwardType> awardList = this.setAwardService.getPublishedAward(currentYear, availableObject);
			List<BaseClassModel> classList = this.jobTeamService.getHeadteacherClass(curUserId);
//			获取当前登录班主任已保存或提交的申请
			if(classList.size() > 0) {
				BaseClassModel clazz = null;
				for(BaseClassModel cla : classList) {
					clazz = new BaseClassModel();
					clazz.setId(cla.getId());
					for(AwardType awardType : awardList) {
						ClassApplyInfo classA = new ClassApplyInfo();
						classA.setAwardTypeId(awardType);
						classA.setClassId(clazz);
						List<ClassApplyInfo> claApplyList = this.classApplyService.getClassInfoList(classA);
						if(claApplyList.size() <= 0) {
							classA.setClassId(cla);
							classA.setApplyStatus(applyStatus);
							this.classApplyService.saveClassApply(classA, null);
						}
					}
				}
			}
//			判断奖项申请日期是否已截止
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
		}
		BaseClassModel cla = new BaseClassModel();
		cla.setHeadermaster(teacher);
		classInfo.setClassId(cla);
		Page page = this.classApplyService.queryClassApplyPage(classInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("award", award);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("awardTypeList", awardTypeList);
		return "reward/classApply/queryClassApplyList";
	}
	
	/**
	 * @throws ParseException  
	* @Title: checkClassPermission 
	* @Description:  验证班级申请权限
	* @param  @param awardTypeId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapply/opt-query/checkClassPermission.do"})
	@ResponseBody
	public String checkClassPermission(@RequestParam String awardTypeId) throws ParseException {
		
		String returnFlag = "false";
//		AwardCondition condition = this.setAwardService.getConByAwardId(awardTypeId); 
//		List<ConditionInfo> infoList = this.setAwardService.getConInfoListByConId(condition.getId());
//		暂定实现逻辑：获取该学生的该奖项所需
//		AwardType award = this.setAwardService.getAwardById(awardTypeId);
		if(true) {
			returnFlag = "true";
		}
		return returnFlag;
	}
	
	/** 
	* @Title: editClassApply 
	* @Description: 班级评优申请 
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapply/opt-query/editClassApply.do"})
	public String editClassApply(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo) {
		
		String classApplyId = request.getParameter("classApplyId");
		String awardTypeId = request.getParameter("awardType");
		AwardCondition condition = null;
		if(DataUtil.isNotNull(classApplyId)) {
			classInfo = this.classApplyService.getClassApplyInfoById(classApplyId);
			condition = this.setAwardService.getConByAwardId(classInfo.getAwardTypeId().getId());
		} else {
			AwardType award = this.setAwardService.getAwardById(awardTypeId);
			classInfo.setAwardTypeId(award);
			condition = this.setAwardService.getConByAwardId(awardTypeId);
			model.addAttribute("award", award);
		}
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		String userId = this.sessionUtil.getCurrentUserId();
		boolean headerTeacher = this.jobTeamService.isHeadMaster(userId);
		List<BaseClassModel> classList = this.jobTeamService.getHeadteacherClass(userId);
		model.addAttribute("classList", classList);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("conditionUploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(classInfo.getId()));
		String returnPage = "";
		if(headerTeacher) {
			if(classInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.GOODCLASS)) {
				returnPage = "reward/classApply/goodClassApply";
			}else{
				returnPage = "reward/classApply/goodClassApply";
			}
		}else{
			returnPage = "reward/studentApply/errorMessage";
		}
		return returnPage;
	}
	
	/** 
	* @Title: viewClassApplyInfo 
	* @Description:  班级申请明细查看
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapply/opt-query/viewClassApplyInfo.do"})
	public String viewClassApplyInfo(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo) {
		
		String applyId = request.getParameter("id");
		classInfo = this.classApplyService.getClassApplyInfoById(applyId);
		AwardCondition condition = this.setAwardService.getConByAwardId(classInfo.getAwardTypeId().getId());
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(applyId,ProjectConstants.IS_APPROVE_ENABLE);
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
	@RequestMapping(value = {"/reward/classapply/opt-add/saveCurProcess.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId,String[] fileId){
		
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				//发起审核流程
				result = flowInstanceService.initProcessInstance(objectId,"REWARD_CLASS_APPLY_APPROVE", 
						 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
				result = this.classApplyService.saveClassApplyApproveResult(objectId, result, nextApproverId, fileId);
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
	* @Title: saveClassApply 
	* @Description:  保存班级申请
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapply/opt-query/saveClassApply.do"})
	public String saveClassApply(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo,String[] fileId) {
		
//		BaseClassModel classModel = this.jobTeamService.getHeadteacherClass(userId);
		String awardTypeId = request.getParameter("awardId");
		AwardType awardType = this.setAwardService.getAwardById(awardTypeId);
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SAVED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "ONESELF_ADD");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "Y");
		classInfo.setApplyStatus(applyStatus);
		classInfo.setApplySource(applySource);
		classInfo.setMeetCondition(meetDic);
//		classInfo.setTeacherId(teacher);
//		classInfo.setClassId(classModel);
		classInfo.setAwardTypeId(awardType);
		if(DataUtil.isNotNull(classInfo.getId())) {
			this.classApplyService.updateClassApply(classInfo, fileId);
		} else {
			this.classApplyService.saveClassApply(classInfo,fileId);
		}
		return "redirect:/reward/classapply/opt-query/queryClassApplyPage.do";
	}
	
	
	/** 
	* @Title: saveClassApplyJson 
	* @Description:  直接点提交 
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value = {"/reward/classapply/opt-query/saveClassApplyJson.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveClassApplyJson(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo,String[] fileId) {
		
//		BaseClassModel classModel = this.jobTeamService.getHeadteacherClass(userId);
		String awardTypeId = request.getParameter("awardId");
		AwardType awardType = this.setAwardService.getAwardById(awardTypeId);
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "SAVED");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "ONESELF_ADD");
		Dic meetDic = this.dicUtil.getDicInfo("Y&N", "Y");
		classInfo.setApplyStatus(applyStatus);
		classInfo.setApplySource(applySource);
		classInfo.setMeetCondition(meetDic);
//		classInfo.setClassId(classModel);
		classInfo.setAwardTypeId(awardType);
		this.classApplyService.updateClassApply(classInfo,fileId);
		return classInfo.getId();
	}
	
}

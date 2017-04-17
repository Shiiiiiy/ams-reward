package com.uws.reward.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.JsonUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IEvaluationCommonService;
import com.uws.common.service.IScoreService;
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardType;
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
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName: StudentApproveController 
* @Description:  学生申请审批controller
* @author zhangyb 
* @date 2015年9月2日 下午1:57:33  
*/
@Controller
public class StudentApproveController {

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
	private IExcelService excelService;
	@Autowired
	private IEvaluationCommonService evaluationService;
	@Autowired
	private IScoreService scoreService;
	
	/** 
	* @Title: queryStuCollegeApprovePage 
	* @Description:  学生申请学院审核列表页
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @param award
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapprove/opt-query/queryStudentApprovePage.do"})
	public String queryStudentApprovePage(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo,AwardType award) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		String curUserId = this.sessionUtil.getCurrentUserId();
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
		if(DataUtil.isNotNull(stuInfo.getAwardTypeId()) && !DataUtil.isNotNull(stuInfo.getAwardTypeId().getSchoolYear())) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			stuInfo.getAwardTypeId().setSchoolYear(currentYear);
		}
//		查询审批过的记录
		String[] objectIds = this.flowInstanceService.getObjectIdByProcessKey(RewardConstant.STUDENT_APPROVE_FLOW_KEY, curUserId);
		Page page = this.studentApplyService.queryStuApplyPage(stuInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, curUserId,objectIds);
		model.addAttribute("userId", curUserId);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("page", page);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("awardTypeList", awardTypeList);
		return "reward/studentApply/studentApprove/queryStudentApproveList";
	}
	
	/** 
	* @Title: approveStuApply 
	* @Description:  审批学生申请
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapprove/opt-query/approveStuApply.do"})
	public String approveStuApply(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo) {
		
		String stuApplyId = request.getParameter("id");
		stuInfo = this.studentApplyService.getStuApplyInfoById(stuApplyId);
		String awardTypeId = stuInfo.getAwardTypeId().getId();
		AwardCondition condition = this.setAwardService.getConByAwardId(awardTypeId);
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
//		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(stuApplyId,ProjectConstants.IS_APPROVE_ENABLE);
//		model.addAttribute("instanceList",instanceList);
		Map<String,String> evaluationMap  = this.evaluationService.queryStudentEvaluationScore(SchoolYearUtil.getYearDic().getId(), stuInfo.getStudentId());
		List<ConditionInfo> conValueList = this.studentApplyService.getStuConInfoValue(stuInfo.getStudentId().getId(), SchoolYearUtil.getYearDic(), conInfoList, evaluationMap);
//		如果审核的是行知一二三等奖的时候 审批人可以调整奖项等级
		if(stuInfo.getAwardTypeId().getSecondAwardName() != null) {
			String secondAwardName = stuInfo.getAwardTypeId().getSecondAwardName().getName();
			if(DataUtil.isNotNull(secondAwardName) && secondAwardName.indexOf(RewardConstant.xingZhiAwardName) > -1) {
				Dic firstAward = this.dicUtil.getDicInfo("SECOND_AWARD_NAME", "FIRST_AWARD");
				Dic secondAward = this.dicUtil.getDicInfo("SECOND_AWARD_NAME", "SECOND_AWARD");
				Dic thirdAward = this.dicUtil.getDicInfo("SECOND_AWARD_NAME", "THIRD_AWARD");
				List<Dic> secondAwardNameList = new ArrayList<Dic>();
				secondAwardNameList.add(firstAward);
				secondAwardNameList.add(secondAward);
				secondAwardNameList.add(thirdAward);
				model.addAttribute("adjustFlag", "true");
				model.addAttribute("secondAwardNameList", secondAwardNameList);
				model.addAttribute("secondAwardName", stuInfo.getAwardTypeId().getSecondAwardName().getCode());
			}
		}
		model.addAttribute("stuInfo", stuInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("conValueList", conValueList);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("conditionUploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(stuInfo.getId()));
		String returnPage = "";
		if(stuInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.SANHAO)) {
			returnPage = "reward/studentApply/studentApprove/threeGoodApprove";
		}else if(stuInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI)) {
			returnPage = "reward/studentApply/studentApprove/xingZhiApprove";
		}else{
			returnPage = "reward/studentApply/studentApprove/threeGoodApprove";
		}
		return returnPage;
	}
	
	/***
	 * 判断审批通过人数
	 * @param id
	 * @param studentId
	 * @param scholarshipId
	 * @param year
	 * @return
	 */
	@ResponseBody
	@RequestMapping({"/reward/studentApprove/opt-query/checkApprovedPass"})
	public String checkApprovedPass(@RequestParam String stuApplyId){
		if(this.studentApplyService.checkApprovedPass(stuApplyId)){
	       return "success";
	    }
	     return "fail";
	}
	
	/** 
	* @Title: saveApproveInfo 
	* @Description:  保存审批信息
	* @param  @param model
	* @param  @param request
	* @param  @param objectId
	* @param  @param nextApproverId
	* @param  @param approveStatus
	* @param  @param processStatusCode
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value = {"/reward/studentapprove/opt-query/saveApproveInfo.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveApproveInfo(ModelMap model,HttpServletRequest request,
			String objectId,String nextApproverId,String approveStatus,String processStatusCode){
		
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				  result.setApproveStatus(approveStatus);
				  result.setProcessStatusCode(processStatusCode);
				  this.studentApplyService.saveStuApplyApproveResult(objectId, result, nextApproverId, null);
				  result.setResultFlag("success");
			} catch (Exception e) {
				result.setResultFlag("error");
				e.printStackTrace();
			}
		}else{
			result.setResultFlag("deprecated");
	    }
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
	/** 
	* @Title: viewApproveStudentInfo 
	* @Description:  学生审批查看
	* @param  @param model
	* @param  @param request
	* @param  @param stuInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapprove/opt-query/viewApproveStudentInfo.do"})
	public String viewApproveStudentInfo(ModelMap model,HttpServletRequest request,StudentApplyInfo stuInfo) {
		
		String applyId = request.getParameter("id");
		stuInfo = this.studentApplyService.getStuApplyInfoById(applyId);
		AwardCondition condition = this.setAwardService.getConByAwardId(stuInfo.getAwardTypeId().getId());
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(applyId,ProjectConstants.IS_APPROVE_ENABLE);
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
	* @Title: importStudentApprove 
	* @Description:  学生处导入学生审批
	* @param  @param model
	* @param  @param file
	* @param  @param maxSize
	* @param  @param allowedExt
	* @param  @param request
	* @param  @param session
	* @param  @return
	* @param  @throws Exception    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapprove/opt-query/importStudentApprove.do"})
	public String importStudentApprove(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session) {
		List errorText = new ArrayList();
		String errorTemp = "";
		Dic year = SchoolYearUtil.getYearDic();
		MultipartFileValidator validator = new MultipartFileValidator();
		if(DataUtil.isNotNull(allowedExt)) {
			validator.setAllowedExtStr(allowedExt.toLowerCase());
		}
		if(DataUtil.isNotNull(maxSize)) {
			validator.setMaxSize(Long.valueOf(maxSize).longValue());
		}else{
			validator.setMaxSize(20971520);
		}
		String returnValue = validator.validate(file);
		if(!returnValue.equals("")) {
			errorTemp = returnValue;
			errorText.add(errorTemp);
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "reward/studentApply/studentApprove/importApprove";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<StudentApplyInfo> list = iu.getDataList(filePath, "importStudentApprove", null, StudentApplyInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, StudentApplyInfo> map = new HashMap<String, StudentApplyInfo>();
				for(StudentApplyInfo c : list) {
					if(map.containsKey(c.getStudentIdStr() + c.getAwardTypeCode())) {
						String message = "模板中数据有重复：学号为"+c.getStudentIdStr()+"，奖项编码为"+c.getAwardTypeCode()+
								"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(c.getStudentIdStr()+c.getAwardTypeCode(), c);
					}
				}
				List arrayList = this.studentApplyService.compareData(list);                                  //Excel与已有的重复的数据
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.studentApplyService.importData(list);
				}else{
					session.setAttribute("arrayList", arrayList);
					List subList = null;
					if(arrayList.size() >= Page.DEFAULT_PAGE_SIZE) {
						subList = arrayList.subList(0, Page.DEFAULT_PAGE_SIZE);
					}else{
						subList = arrayList;
					}
					Page page = new Page();
					page.setPageSize(Page.DEFAULT_PAGE_SIZE);
					page.setResult(subList);
					page.setStart(0L);
					page.setTotalCount(arrayList.size());
					model.addAttribute("page", page);
				}
			} catch (OfficeXmlFileException e) {
				//  Auto-generated catch block
				e.printStackTrace();
				errorTemp = "OfficeXmlFileException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IOException e) {
				//  Auto-generated catch block
				e.printStackTrace();
				errorTemp = "IOException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IllegalAccessException e) {
				//  Auto-generated catch block
				e.printStackTrace();
				errorTemp = "IllegalAccessException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ExcelException e) {
				//  Auto-generated catch block
				e.printStackTrace();
				errorTemp = e.getMessage();
				errorText.add(errorTemp);
			} catch (InstantiationException e) {
				//  Auto-generated catch block
				e.printStackTrace();
				errorTemp = "InstantiationException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ClassNotFoundException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				//  Auto-generated catch block
				e.printStackTrace();
				String message = e.getMessage();
				if(DataUtil.isNotNull(message)) {
					errorText.add(message);
				}else{
					errorText.add("模板不正确或者模板内数据异常，请检查后再导入。");
				}
			}
			model.addAttribute("importFlag", Boolean.valueOf(true));
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
			return "reward/studentApply/studentApprove/importApprove";
		}
	}
	
	@RequestMapping(value={"/reward/studentapprove/opt-query/compareStudentApprove.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String compareStudentApprove(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
		required=true) String pageNo) {
		
		List arrayList = (List)session.getAttribute("arrayList");
		List<Object[]> subList = null;
		int pageno = Integer.parseInt(pageNo);
		int length = arrayList.size();
		if(arrayList.size() >= Page.DEFAULT_PAGE_SIZE * pageno) {
			subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), Page.DEFAULT_PAGE_SIZE * pageno);
		}else{
			subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), length);
		}
		JSONArray array = new JSONArray();
	    JSONObject obj = null;
	    JSONObject json = new JSONObject();
	    for(Object[] infoArray : subList) {
	    	StudentApplyInfo info = (StudentApplyInfo) infoArray[0];
	    	StudentApplyInfo xls = (StudentApplyInfo) infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("stuName", info.getStudentId().getName());
	    	obj.put("stuId", info.getStudentId().getStuNumber());
	    	obj.put("awardType", info.getAwardTypeId().getAwardInfoId().getAwardName());
	    	obj.put("secondAward", info.getAwardTypeId().getSecondAwardName().getName());
	    	obj.put("xlsStuName", info.getStudentId().getName());
	    	obj.put("xlsStuId", xls.getStudentIdStr());
	    	obj.put("xlsAwardType", xls.getAwardTypeIdStr());
	    	obj.put("xlsSecondAward", info.getAwardTypeId().getSecondAwardName().getName());
	    	array.add(obj);
	    }
	    json.put("result", array);
	    obj = new JSONObject();
	    obj.put("totalPageCount", Integer.valueOf(length % Page.DEFAULT_PAGE_SIZE == 0 ? 
	    		length / Page.DEFAULT_PAGE_SIZE : length / Page.DEFAULT_PAGE_SIZE + 1));
	    obj.put("previousPageNo", Integer.valueOf(pageno - 1));
	    obj.put("nextPageNo", Integer.valueOf(pageno + 1));
	    obj.put("currentPageNo", Integer.valueOf(pageno));
	    obj.put("pageSize", Integer.valueOf(Page.DEFAULT_PAGE_SIZE));
	    obj.put("totalCount", Integer.valueOf(length));
	    json.put("page", obj);
	    return json.toString();
	}
	
	@SuppressWarnings("finally")
	@RequestMapping({"/reward/studentapprove/opt-query/importData.do"})
	public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		try {
			this.studentApplyService.importData(arrayList, filePath, compareId);
		} catch (ExcelException e) {
			//  Auto-generated catch block
			errorText.add(0, e.getMessage());
			 
		    errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
		    model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (OfficeXmlFileException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}finally{
			model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "reward/studentApply/studentApprove/importApprove";
		}
	}
	
	/** 
	* @Title: checkedApproveList 
	* @Description:  跳转到批量审批页面
	* @param  @param applyInfo
	* @param  @param selectedBox
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/studentapprove/opt-query/checkedApproveList.do"})
	public String checkedApproveList(ModelMap model,StudentApplyInfo applyInfo,String selectedBox,HttpServletRequest request) {
		
		List<StudentApplyInfo> stuList = new ArrayList<StudentApplyInfo>();
		if(selectedBox.indexOf(",") > -1) {
			String[] checkedIds = selectedBox.split(",");
			for(String s : checkedIds) {
				applyInfo = this.studentApplyService.getStuApplyInfoById(s);
				if(DataUtil.isNotNull(applyInfo)) {
					stuList.add(applyInfo);
				}
			}
		}else if(DataUtil.isNotNull(selectedBox)){
			applyInfo = this.studentApplyService.getStuApplyInfoById(selectedBox);
			if(DataUtil.isNotNull(applyInfo)) {
				stuList.add(applyInfo);
			}
		}
		model.addAttribute("stuList", stuList);
	    model.addAttribute("objectIds", selectedBox);
		return "reward/studentApply/studentApprove/stuMulApprove";
	}
	
	/** 
	* @Title: saveMutiResult 
	* @Description:  保存批量审批结果信息
	* @param  @param model
	* @param  @param request
	* @param  @param mulResults
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"reward/studentapprove/opt-save/saveMutiResult.do"})
	public String saveMutiResult(ModelMap model,HttpServletRequest request,String mulResults) {
		
		List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ProjectConstants.IS_APPROVE_ENABLE);
		if(DataUtil.isNotNull(list) && list.size()>0){
			this.studentApplyService.saveMulResult(list);
		}
		return "redirect:/reward/studentapprove/opt-query/queryStudentApprovePage.do";
	}
	
	/** 
	* @Title: changeApproveInfo 
	* @Description: 更新审核时调整的奖学金等级
	* @param  @param stuApplyId
	* @param  @param secondAwardName    
	* @return void    
	* @throws 
	*/
	@RequestMapping(value={"reward/studentapprove/opt-save/changeApproveInfo.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String changeApproveInfo(@RequestParam String stuApplyId,@RequestParam String secondAwardName) {
		
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "ADJUSTED");
		StudentApplyInfo applyInfo = this.studentApplyService.getStuApplyInfoById(stuApplyId);
		StudentApplyInfo updateInfo = this.studentApplyService.getApplyInfo(secondAwardName, SchoolYearUtil.getYearDic(), applyInfo.getStudentId().getId());
		if(updateInfo != null) {
			updateInfo.setAwardTypeId(applyInfo.getAwardTypeId());
			updateInfo.setApplyStatus(applyStatus);
			updateInfo.setProcessStatus(RewardConstant.approveStatus);
			updateInfo.setApproveStatus(null);
			updateInfo.setNextApprover(null);
			this.studentApplyService.updateStuApply(updateInfo);
		}else{
			StudentApplyInfo newInfo = new StudentApplyInfo();
			BeanUtils.copyProperties(applyInfo, newInfo, new String[]{"id","createTime","updateTime"});
			newInfo.setApplyStatus(applyStatus);
			newInfo.setProcessStatus(RewardConstant.approveStatus);
			newInfo.setApproveStatus(null);
			newInfo.setNextApprover(null);
			this.studentApplyService.saveStuApply(newInfo, null);
		}
		AwardType awardType = this.setAwardService.getAwardTypeByName(SchoolYearUtil.getYearDic(), RewardConstant.XINGZHI, secondAwardName);
		applyInfo.setAwardTypeId(awardType);
		this.studentApplyService.updateStuApply(applyInfo);
		return "";
	}

}

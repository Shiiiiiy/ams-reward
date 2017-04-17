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
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName: ClassApproveController 
* @Description:  班级审批controller 
* @author zhangyb 
* @date 2015年9月7日 下午6:19:10  
*/
@Controller
public class ClassApproveController {

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
	@Autowired
	private IExcelService excelService;
	@Autowired
	private ICompService compService;
	
	/** 
	* @Title: queryClassCollegeApprovePage 
	* @Description:  待审批列表查询页
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @param award
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapprove/opt-query/queryClassApprovePage.do"})
	public String queryClassApprovePage(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo,AwardType award) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		String curUserId = this.sessionUtil.getCurrentUserId();
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
		
		if(DataUtil.isNotNull(classInfo.getAwardTypeId()) && !DataUtil.isNotNull(classInfo.getAwardTypeId().getSchoolYear())) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			classInfo.getAwardTypeId().setSchoolYear(currentYear);
		}
//		查询审批过的记录
		String[] objectIds = this.flowInstanceService.getObjectIdByProcessKey(RewardConstant.CLASS_APPROVE_FLOW_KEY, curUserId);
		Page page = this.classApplyService.queryClassApplyPage(classInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, curUserId, objectIds);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("userId", curUserId);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		model.addAttribute("page", page);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("awardTypeList", awardTypeList);
		return "reward/classApply/classApprove/queryClassApproveList";
	}
	
	/** 
	* @Title: approveClassApply 
	* @Description:  审批班级申请
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapprove/opt-query/approveClassApply.do"})
	public String approveClassApply(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo) {
		
		String classApplyId = request.getParameter("id");
		classInfo = this.classApplyService.getClassApplyInfoById(classApplyId);
		String awardTypeId = classInfo.getAwardTypeId().getId();
		AwardCondition condition = this.setAwardService.getConByAwardId(awardTypeId);
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
//		List<FlowHistoryPo> instanceList = this.flowInstanceService.getCurProcessHistory(classApplyId,ProjectConstants.IS_APPROVE_ENABLE);
//		model.addAttribute("instanceList",instanceList);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("condition", condition);
		model.addAttribute("conInfoList", conInfoList);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("conditionUploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(classInfo.getId()));
		String returnPage = "";
		if(classInfo.getAwardTypeId().getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.GOODCLASS)) {
			returnPage = "reward/classApply/classApprove/goodClassApprove";
		}else{
			returnPage = "reward/classApply/classApprove/goodClassApprove";
		}
		return returnPage;
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
	@RequestMapping(value = {"/reward/classapprove/opt-query/saveApproveInfo.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveApproveInfo(ModelMap model,HttpServletRequest request,
			String objectId,String nextApproverId,String approveStatus,String processStatusCode){
		
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				  result.setApproveStatus(approveStatus);
				  result.setProcessStatusCode(processStatusCode);
				  this.classApplyService.saveClassApplyApproveResult(objectId, result, nextApproverId, null);
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
	* @Title: viewApproveClassInfo 
	* @Description:  班级审批查看
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/classapprove/opt-query/viewApproveClassInfo.do"})
	public String viewApproveClassInfo(ModelMap model,HttpServletRequest request,ClassApplyInfo classInfo) {
		
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
	* @Title: importClassApprove 
	* @Description:  学生处导入班级审批
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
	@RequestMapping({"/reward/classapprove/opt-query/importClassApprove.do"})
	public String importClassApprove(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session)  {
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
		    return "reward/classApply/classApprove/importApprove";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<ClassApplyInfo> list = iu.getDataList(filePath, "importClassApprove", null, ClassApplyInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, ClassApplyInfo> map = new HashMap<String, ClassApplyInfo>();
				for(ClassApplyInfo c : list) {
					if(map.containsKey(c.getClassIdStr() + c.getAwardTypeCode())) {
						String message = "模板中数据有重复：班号为"+c.getClassIdStr()+"，奖项编码为"+c.getAwardTypeCode()+
								"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(c.getClassIdStr()+c.getAwardTypeCode(), c);
					}
				}
				List arrayList = this.classApplyService.compareData(list);                                  //Excel与已有的重复的数据
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.classApplyService.importData(list);
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
			return "reward/classApply/classApprove/importApprove";
		}
	}
	
	@RequestMapping(value={"/reward/classapprove/opt-query/compareClassApprove.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String compareClassApprove(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
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
	    	ClassApplyInfo info = (ClassApplyInfo) infoArray[0];
	    	ClassApplyInfo xls = (ClassApplyInfo) infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("className", info.getClassId().getId());
	    	obj.put("awardName", info.getAwardTypeId().getId());
	    	obj.put("xlsClassName", xls.getClassIdStr());
	    	obj.put("xlsAwardName", xls.getAwardTypeIdStr());
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
	@RequestMapping({"/reward/classapprove/opt-query/importData.do"})
	public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		try {
			this.classApplyService.importData(arrayList, filePath, compareId);
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
		    return "reward/classApply/classApprove/importApprove";
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
	@RequestMapping({"/reward/classapprove/opt-query/checkedApproveList.do"})
	public String checkedApproveList(ModelMap model,ClassApplyInfo applyInfo,String selectedBox,HttpServletRequest request) {
		
		List<ClassApplyInfo> stuList = new ArrayList<ClassApplyInfo>();
		if(selectedBox.indexOf(",") > -1) {
			String[] checkedIds = selectedBox.split(",");
			for(String s : checkedIds) {
				applyInfo = this.classApplyService.getClassApplyInfoById(s);
				if(DataUtil.isNotNull(applyInfo)) {
					stuList.add(applyInfo);
				}
			}
		}else if(DataUtil.isNotNull(selectedBox)){
			applyInfo = this.classApplyService.getClassApplyInfoById(selectedBox);
			if(DataUtil.isNotNull(applyInfo)) {
				stuList.add(applyInfo);
			}
		}
		model.addAttribute("classList", stuList);
	    model.addAttribute("objectIds", selectedBox);
		return "reward/classApply/classApprove/classMulApprove";
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
	@RequestMapping({"reward/classapprove/opt-save/saveMutiResult.do"})
	public String saveMutiResult(ModelMap model,HttpServletRequest request,String mulResults) {
		
		List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ProjectConstants.IS_APPROVE_ENABLE);
		if(DataUtil.isNotNull(list) && list.size()>0){
			this.classApplyService.saveMulResult(list);
		}
		return "redirect:/reward/classapprove/opt-query/queryClassApprovePage.do";
	}
}

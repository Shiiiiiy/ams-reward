package com.uws.reward.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.common.service.IBaseDataService;
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
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.PunishInfo;
import com.uws.reward.service.IManagePunishService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.user.model.User;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName: ManagePunishController 
* @Description:  惩罚管理controller
* @author zhangyb 
* @date 2015年8月24日 上午10:27:54  
*/
@Controller
public class ManagePunishController {

	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IBaseDataService baseDataService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private IManagePunishService managePunishService;
	@Autowired
	private IStudentCommonService studentCommonService;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private IExcelService excelService;
	@Autowired
	private ICompService compService;
	
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	
	/** 
	* @Title: queryPunishPage 
	* @Description:  
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/queryPunishPage.do"})
	public String queryPunishPage(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> punishCodeList = this.dicUtil.getDicInfoList("PUNISH_CODE");     //处分名称码
		List<Dic> punishStatusList = this.dicUtil.getDicInfoList("PUNISH_STATUS"); //处分状态		
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		
		List<BaseAcademyModel> academyList = new ArrayList<BaseAcademyModel>();
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);  //组织机构ID
		boolean collegeTeacher = CheckUtils.isCurrentOrgEqCollege(orgId);    //是否属于二级学院
		if(collegeTeacher) {
			BaseAcademyModel academy = this.baseDataService.findAcademyById(orgId);
			if(punishInfo.getStuId() != null) {
				punishInfo.getStuId().setCollege(academy);
			}else{
				StudentInfoModel stu = new StudentInfoModel();
				stu.setCollege(academy);
				punishInfo.setStuId(stu);
			}
			academyList.clear();
			academyList.add(academy);
		}else{
			academyList = this.baseDataService.listBaseAcademy();
		}
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != punishInfo && null != punishInfo.getStuId() 
				&& null != punishInfo.getStuId().getCollege() 
				&& null != punishInfo.getStuId().getCollege().getId()
				&& punishInfo.getStuId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(punishInfo.getStuId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != punishInfo && null != punishInfo.getStuId() 
				&& null != punishInfo.getStuId().getClassId() 
				&& null != punishInfo.getStuId().getMajor() 
				&& null != punishInfo.getStuId().getMajor().getId() 
				&& punishInfo.getStuId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(punishInfo.getStuId().getMajor().getId());
		}
		if(!DataUtil.isNotNull(punishInfo.getPunishYear())) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			punishInfo.setPunishYear(currentYear);
		}
		
		Page page = this.managePunishService.queryPunishPage(punishInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("punishInfo", punishInfo);
		model.addAttribute("punishCodeList", punishCodeList);
		model.addAttribute("punishStatusList", punishStatusList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("page", page);
		return "reward/managePunish/queryPunishList";
	}
	
	/** 
	* @Title: addPunish 
	* @Description:  新增惩罚信息
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/addPunish.do"})
	public String addPunish(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		List<Dic> punishCodeList = this.dicUtil.getDicInfoList("PUNISH_CODE");     //处分名称码
		Dic currentYear = SchoolYearUtil.getYearDic();
		punishInfo.setPunishYear(currentYear);
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);  //组织机构ID
		boolean collegeTeacher = CheckUtils.isCurrentOrgEqCollege(orgId);    //是否属于二级学院
		if(collegeTeacher) {
			model.addAttribute("collegeId", orgId);
		}
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("punishCodeList", punishCodeList);
		model.addAttribute("punishInfo", punishInfo);
		return "reward/managePunish/addPunish";
	}
	
	/**
	 * @throws ParseException  
	* @Title: savePunish 
	* @Description:  保存punish
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/savePunish.do"})
	public String savePunish(ModelMap model,HttpServletRequest request,PunishInfo punishInfo,String[] fileId) throws ParseException {
		
		String stuIds = request.getParameter("stuIds");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		user.setId(userId);
//		Date punishStartDate = sdf.parse(punishInfo.getPunishStartDateStr());
//		Date punishEndDate = sdf.parse(punishInfo.getPunishEndDateStr());
		Dic yearDic = this.dicUtil.getDicInfo("YEAR", punishInfo.getPunishYear().getCode());
		Dic termDic = this.dicUtil.getDicInfo("TERM", punishInfo.getPunishTerm().getCode());
		Dic punishStatus = this.dicUtil.getDicInfo("PUNISH_STATUS", "EXECUTED");
		Dic punishDic = this.dicUtil.getDicInfo("PUNISH_CODE", punishInfo.getPunish().getCode());
		punishInfo.setPunishYear(yearDic);
		punishInfo.setPunishTerm(termDic);
		punishInfo.setPunish(punishDic);
//		punishInfo.setPunishStartDate(punishStartDate);
//		punishInfo.setPunishEndDate(punishEndDate);
		punishInfo.setPunishStatus(punishStatus);
		StudentInfoModel stu = this.studentCommonService.queryStudentById(stuIds);
		punishInfo.setStuId(stu);
		if (DataUtil.isNotNull(punishInfo.getId())) {   //更新
			this.managePunishService.updatePunish(punishInfo, fileId);
		} else {
			punishInfo.setCreator(user);
			PunishInfo info = new PunishInfo();
			BeanUtils.copyProperties(punishInfo, info);
			this.managePunishService.savePunish(info,fileId);
		}
		return "redirect:/reward/managepunish/opt-query/queryPunishPage.do";
	}
	
	/** 
	* @Title: editPunish 
	* @Description:  修改punish
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/editPunish.do"})
	public String editPunish(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		
		String id = request.getParameter("id");
		punishInfo = this.managePunishService.getPunishInfoById(id);
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		List<Dic> punishCodeList = this.dicUtil.getDicInfoList("PUNISH_CODE");     //处分名称码
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		punishInfo.setPunishStartDateStr(sdf.format(punishInfo.getPunishStartDate()));
//		punishInfo.setPunishEndDateStr(sdf.format(punishInfo.getPunishEndDate()));
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("punishCodeList", punishCodeList);
		model.addAttribute("punishInfo", punishInfo);
		return "reward/managePunish/editPunish";
	}
	
	/** 
	* @Title: deletePunish 
	* @Description:  delete punish
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/deletePunish.do"})
	public String deletePunish(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		String id = request.getParameter("id");
		punishInfo = this.managePunishService.getPunishInfoById(id);
		this.managePunishService.deletePunish(punishInfo);
		return "redirect:/reward/managepunish/opt-query/queryPunishPage.do";
	}
	
	/** 
	* @Title: delMorePunish 
	* @Description:  批量删除
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/delMorePunish.do"})
	public String delMorePunish(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		String ids = request.getParameter("id");
		if(ids.indexOf("on,") > -1) {
			ids = ids.replace("on,", "");
		}
		String[] idArr = ids.split(",");
		for(String s : idArr) {
			punishInfo = this.managePunishService.getPunishInfoById(s);
			if(DataUtil.isNotNull(punishInfo)) {
				this.managePunishService.deletePunish(punishInfo);
			}
		}
		return "redirect:/reward/managepunish/opt-query/queryPunishPage.do";
	}
	/** 
	* @Title: viewPunish 
	* @Description:  view punish
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/viewPunish.do"})
	public String viewPunish(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		String id = request.getParameter("id");
		punishInfo = this.managePunishService.getPunishInfoById(id);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		model.addAttribute("punishInfo", punishInfo);
		return "reward/managePunish/viewPunish";
	}
	
	/** 
	* @Title: viewPunishInfo 
	* @Description:  违纪信息查询查看
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/querypunish/opt-query/viewPunishInfo.do"})
	public String viewPunishInfo(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		String id = request.getParameter("id");
		punishInfo = this.managePunishService.getPunishInfoById(id);
		boolean flag = ProjectSessionUtils.checkIsStudent(request);   //判断当前登录人是不是学生
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		model.addAttribute("punishInfo", punishInfo);
		model.addAttribute("queryInfo", flag);    //违纪信息查询标志
		return "reward/managePunish/viewPunish";
	}
	
	/** 
	* @Title: checkPunishRepeat 
	* @Description:  验证punish是否重复
	* @param  @param punishInfo
	* @param  @param punishNum
	* @param  @param stuId
	* @param  @param punishId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/checkPunishRepeat.do"})
	@ResponseBody
	public String checkPunishRepeat(PunishInfo punishInfo,@RequestParam String punishNum,@RequestParam String studentId,
			@RequestParam String punishId){
		
		String result = "true";
		boolean flag = this.managePunishService.checkPunish(studentId, punishNum);
		if(DataUtil.isNotNull(punishId)) {   //修改
			punishInfo = this.managePunishService.getPunishInfoById(punishId);
			if(flag && !punishInfo.getPunishNum().equals(punishNum)) {
				result = "false";
			}
		}else{
			if(flag) {
				result = "false";
			}
		}
		return result;
	}
	
	/** 
	* @Title: importPunish 
	* @Description:  导出punish
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
	@SuppressWarnings("unchecked")
	@RequestMapping({"/reward/managepunish/opt-query/importPunish.do"})
	public String importPunish(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session){
		
		List errorText = new ArrayList();
		String errorTemp = "";
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
		    return "reward/managePunish/importPunish";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<PunishInfo> list = iu.getDataList(filePath, "importPunish", null, PunishInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, PunishInfo> map = new HashMap<String, PunishInfo>();
				for(PunishInfo p : list) {
					if(map.containsKey(p.getStuIdStr() + p.getPunishStr()  + p.getPunishYearStr() 
							+ p.getPunishTermStr() + p.getPunishReason())) {
						String message = "模板中数据有重复：学号为"+p.getStuIdStr()+"，处分名称为"+p.getPunishStr()+ "，学年为" 
					+ p.getPunishYearStr() + "，学期为" + p.getPunishTermStr() + "，处分原因为"+ p.getPunishReason() +"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(p.getStuIdStr() + p.getPunishStr()  + p.getPunishYearStr() 
								+ p.getPunishTermStr() + p.getPunishReason(), p);
					}
				}
				this.managePunishService.comparePunishInfo(list);                                 
//				List arrayList = new ArrayList();                                  
				/*if((arrayList == null) || (arrayList.size() == 0)) {
					this.managePunishService.importData(list);
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
				}*/
			} catch (OfficeXmlFileException e) {
				e.printStackTrace();
				errorTemp = "OfficeXmlFileException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IOException e) {
				e.printStackTrace();
				errorTemp = "IOException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				errorTemp = "IllegalAccessException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ExcelException e) {
				e.printStackTrace();
				errorTemp = e.getMessage();
				errorText.add(errorTemp);
			} catch (InstantiationException e) {
				e.printStackTrace();
				errorTemp = "InstantiationException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				String message = "日期列数据格式异常，请按照YYYY-MM-DD标准数据格式填写!";
				errorText.add(message);
			}catch (Exception e) {
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
			return "reward/managePunish/importPunish";
		}
	}
	
	/** 
	* @Title: comparePunish 
	* @Description:  比对导入的数据
	* @param  @param model
	* @param  @param request
	* @param  @param session
	* @param  @param pageNo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/managepunish/opt-query/comparePunish.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String comparePunish(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
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
	    	PunishInfo info = (PunishInfo) infoArray[0];
	    	PunishInfo xls = (PunishInfo) infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("stuName", info.getStuId().getName());
	    	obj.put("stuId", info.getStuId().getId());
	    	obj.put("punish", info.getPunish().getName());
	    	obj.put("punishNum", info.getPunishNum());
	    	obj.put("xlsStuName", xls.getStuName());
	    	obj.put("xlsStuId", xls.getStuIdStr());
	    	obj.put("xlsPunish", xls.getPunish().getName());
	    	obj.put("xlsPunishNum", xls.getPunishNum());
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
	
	/** 
	* @Title: importData 
	* @Description:  执行导入
	* @param  @param model
	* @param  @param session
	* @param  @param compareId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@SuppressWarnings("finally")
	@RequestMapping({"/reward/managepunish/opt-query/importData.do"})
	public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		try {
			this.managePunishService.importData(arrayList, filePath, compareId);
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
		    return "reward/managePunish/importPunish";
		}
	}
	
	/** 
	* @Title: exportPunishList 
	* @Description:  查询导出页数
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/nsm/exportPunishList.do"})
	public String exportPunishList(ModelMap model, HttpServletRequest request) {
		int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
		int pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
		int maxNumber = 0;
		if (pageTotalCount < exportSize){
			maxNumber = 1;
		}else if (pageTotalCount % exportSize == 0){
			maxNumber = pageTotalCount / exportSize;
		}else {
			maxNumber = pageTotalCount / exportSize + 1;
		}
		model.addAttribute("exportSize", Integer.valueOf(exportSize));
	    model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
	    if(maxNumber < 500){
	        model.addAttribute("isMore", "false");
	    }else{
	        model.addAttribute("isMore", "true");
	    }
	    return "reward/managePunish/exportPunish";
	}
	
	/** 
	* @Title: exportPunish 
	* @Description:  执行导出
	* @param  @param model
	* @param  @param request
	* @param  @param info
	* @param  @param response    
	* @return void    
	* @throws 
	*/
	@RequestMapping({"/reward/managepunish/opt-query/exportPunish.do"})
	public void exportPunish(ModelMap model, HttpServletRequest request, PunishInfo info, HttpServletResponse response) {
		String exportSize = request.getParameter("punishQuery_exportSize");
	    String exportPage = request.getParameter("punishQuery_exportPage");
	    Page page = this.managePunishService.queryPunishPage(info,Integer.parseInt(exportPage), Integer.parseInt(exportSize));
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    List listMap = new ArrayList();
	    List<PunishInfo> infoList = (List)page.getResult();
	    for(PunishInfo p : infoList) {
	    	Map map = new HashMap();
	    	map.put("stuName", p.getStuId().getName());
	    	map.put("stuId", p.getStuId().getId());
	    	map.put("punishYear", p.getPunishYear().getName());
	    	map.put("punishTerm", p.getPunishTerm().getName());
	    	map.put("punishNum", p.getPunishNum());
	    	map.put("punish", p.getPunish().getName());
	    	map.put("punishStartDate", p.getPunishStartDate()!=null ? sdf.format(p.getPunishStartDate()) : null);
	    	map.put("punishEndDate", p.getPunishEndDate() != null ? sdf.format(p.getPunishEndDate()) : null);
	    	map.put("punishReason", p.getPunishReason());
	    	map.put("comments", p.getComments());
	    	listMap.add(map);
	    }
	    
	    HSSFWorkbook wb;
		try {
			wb = this.excelService.exportData("export_reward_punish.xls", "exportPunish", listMap);
			String filename = "处分信息" + exportPage + ".xls";
		    response.setContentType("application/x-excel");
		    response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
		    response.setCharacterEncoding("UTF-8");
		    OutputStream ouputStream = response.getOutputStream();
		    wb.write(ouputStream);
		    ouputStream.flush();
		    ouputStream.close();
		} catch (ExcelException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	* @Title: queryPunishInfoPage 
	* @Description:  违纪信息查询
	* @param  @param model
	* @param  @param request
	* @param  @param punishInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/querypunish/opt-query/queryPunishInfoPage.do"})
	public String queryPunishInfoPage(ModelMap model,HttpServletRequest request,PunishInfo punishInfo) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> punishCodeList = this.dicUtil.getDicInfoList("PUNISH_CODE");     //处分名称码
		List<Dic> punishStatusList = this.dicUtil.getDicInfoList("PUNISH_STATUS"); //处分状态		
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		List<BaseAcademyModel> academyList = new ArrayList<BaseAcademyModel>();
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);  //组织机构ID
		boolean collegeTeacher = CheckUtils.isCurrentOrgEqCollege(orgId);    //是否属于二级学院
		if(collegeTeacher) {
			BaseAcademyModel academy = this.baseDataService.findAcademyById(orgId);
			if(punishInfo.getStuId() != null) {
				punishInfo.getStuId().setCollege(academy);
			}else{
				StudentInfoModel stu = new StudentInfoModel();
				stu.setCollege(academy);
				punishInfo.setStuId(stu);
			}
			academyList.clear();
			academyList.add(academy);
		}else{
			academyList = this.baseDataService.listBaseAcademy();
		}
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != punishInfo && null != punishInfo.getStuId() 
				&& null != punishInfo.getStuId().getCollege() 
				&& null != punishInfo.getStuId().getCollege().getId()
				&& punishInfo.getStuId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(punishInfo.getStuId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != punishInfo && null != punishInfo.getStuId() 
				&& null != punishInfo.getStuId().getClassId() 
				&& null != punishInfo.getStuId().getMajor() 
				&& null != punishInfo.getStuId().getMajor().getId() 
				&& punishInfo.getStuId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(punishInfo.getStuId().getMajor().getId());
		}
		if(!DataUtil.isNotNull(punishInfo.getPunishYear())) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			punishInfo.setPunishYear(currentYear);
		}
		boolean flag = ProjectSessionUtils.checkIsStudent(request);   //判断当前登录人是不是学生
		Page page;
		if(flag) {
			page = this.managePunishService.queryPunishPage(punishInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE,"student");
		}else{
			page = this.managePunishService.queryPunishPage(punishInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE,"manager");
		}
		model.addAttribute("queryInfo", flag);    //违纪信息查询标志
		model.addAttribute("punishInfo", punishInfo);
		model.addAttribute("punishCodeList", punishCodeList);
		model.addAttribute("punishStatusList", punishStatusList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("page", page);
		return "reward/managePunish/queryPunishInfoList";
	}
	
}

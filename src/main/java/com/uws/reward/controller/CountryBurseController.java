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

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.reward.service.ICountryBurseService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;

/** 
* @ClassName: CountryBurseController 
* @Description:  国家奖助controller
* @author zhangyb 
* @date 2015年8月27日 上午11:12:31  
*/
@Controller
public class CountryBurseController {

	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICountryBurseService countryBurseService;
	@Autowired
	private IStudentCommonService studentCommonService;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private ICompService compService;
	
	/** 
	* @Title: queryBursePage 
	* @Description:  国家奖学金查询
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/countryaward/opt-query/queryCountryBursePage.do"})
	public String queryCountryBursePage(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != burse && null != burse.getStuId() 
				&& null != burse.getStuId().getCollege() 
				&& null != burse.getStuId().getCollege().getId()
				&& burse.getStuId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(burse.getStuId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != burse && null != burse.getStuId() 
				&& null != burse.getStuId().getClassId() 
				&& null != burse.getStuId().getMajor() 
				&& null != burse.getStuId().getMajor().getId() 
				&& burse.getStuId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(burse.getStuId().getMajor().getId());
		}
		burse.setBurseName(this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "COUNTRY_AWARD"));
		Page page = this.countryBurseService.queryBursePage(burse, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("burse", burse);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("page", page);
		return "reward/countryBurse/queryCountryBurseList";
	}
	
	/** 
	* @Title: queryInspireBursePage 
	* @Description:  国家励志奖学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/inspireaward/opt-query/queryInspireBursePage.do"})
	public String queryInspireBursePage(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != burse && null != burse.getStuId() 
				&& null != burse.getStuId().getCollege() 
				&& null != burse.getStuId().getCollege().getId()
				&& burse.getStuId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(burse.getStuId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != burse && null != burse.getStuId() 
				&& null != burse.getStuId().getClassId() 
				&& null != burse.getStuId().getMajor() 
				&& null != burse.getStuId().getMajor().getId() 
				&& burse.getStuId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(burse.getStuId().getMajor().getId());
		}
		burse.setBurseName(this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "COUNTRY_INSPIRE_AWARD"));
		Page page = this.countryBurseService.queryBursePage(burse, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("burse", burse);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("classList", classList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("page", page);
		return "reward/countryBurse/queryInspireBurseList";
	}
	
	/** 
	* @Title: queryHelpBursePage 
	* @Description:  助学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/helpaward/opt-query/queryHelpBursePage.do"})
	public String queryHelpBursePage(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != burse && null != burse.getStuId() 
				&& null != burse.getStuId().getCollege() 
				&& null != burse.getStuId().getCollege().getId()
				&& burse.getStuId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(burse.getStuId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != burse && null != burse.getStuId() 
				&& null != burse.getStuId().getClassId() 
				&& null != burse.getStuId().getMajor() 
				&& null != burse.getStuId().getMajor().getId() 
				&& burse.getStuId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(burse.getStuId().getMajor().getId());
		}
		burse.setBurseName(this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "HELP_AWARD"));
		Page page = this.countryBurseService.queryBursePage(burse, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("burse", burse);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("page", page);
		return "reward/countryBurse/queryHelpBurseList";
	}
	
	/** 
	* @Title: importBurse 
	* @Description:  国家奖学金导入burse
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
	@RequestMapping({"/reward/countryaward/opt-query/importCountryBurse.do"})
	public String importCountryBurse(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session)  {
		
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
		    return "reward/countryBurse/importCountryBurse";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<CountryBurseInfo> list = iu.getDataList(filePath, "importCountryBurse", null, CountryBurseInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, CountryBurseInfo> map = new HashMap<String, CountryBurseInfo>();
				for(CountryBurseInfo c : list) {
					if(map.containsKey(c.getStuNum() + c.getSchoolYearStr()+c.getBurseNameStr())) {
						String message = "模板中数据有重复：学号为"+c.getStuNum()+"，学年为"+c.getSchoolYearStr()+
								"，奖助类型为"+c.getBurseNameStr()+"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(c.getStuNum()+c.getSchoolYearStr()+c.getBurseNameStr(), c);
					}
				}
				List arrayList = this.countryBurseService.compareData(list);                                  //Excel与已有的重复的数据
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.countryBurseService.importData(list);
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
			return "reward/countryBurse/importCountryBurse";
		}
	}
	
	/** 
	* @Title: comparePunish 
	* @Description:  国家奖学金比对导入的数据
	* @param  @param model
	* @param  @param request
	* @param  @param session
	* @param  @param pageNo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/countryaward/opt-query/compareCountryBurse.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String compareCountryBurse(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
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
	    	CountryBurseInfo info = (CountryBurseInfo) infoArray[0];
	    	CountryBurseInfo xls = (CountryBurseInfo) infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("stuName", info.getStuId().getName());
	    	obj.put("stuId", info.getStuId().getId());
	    	obj.put("year", info.getSchoolYear().getName());
	    	obj.put("burseName", info.getBurseName().getName());
	    	obj.put("xlsStuName", xls.getStuName());
	    	obj.put("xlsStuId", xls.getStuNum());
	    	obj.put("xlsYear", xls.getSchoolYearStr());
	    	obj.put("xlsBurseName", xls.getBurseNameStr());
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
	* @Description:  国家奖学金执行导入
	* @param  @param model
	* @param  @param session
	* @param  @param compareId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@SuppressWarnings("finally")
	@RequestMapping({"/reward/countryaward/opt-query/importCountryData.do"})
	public String importCountryData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		try {
			this.countryBurseService.importCountryData(arrayList, filePath, compareId);
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
		    return "reward/countryBurse/importCountryBurse";
		}
	}
	
	/** 
	* @Title: importBurse 
	* @Description:  助学金导入burse
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
	@RequestMapping({"/reward/helpaward/opt-query/importHelpBurse.do"})
	public String importHelpBurse(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session)  {
		
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
		    return "reward/countryBurse/importHelpBurse";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<CountryBurseInfo> list = iu.getDataList(filePath, "importHelpBurse", null, CountryBurseInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, CountryBurseInfo> map = new HashMap<String, CountryBurseInfo>();
				for(CountryBurseInfo c : list) {
					if(map.containsKey(c.getStuNum() + c.getSchoolYearStr()+c.getHelpGradeStr())) {
						String message = "模板中数据有重复：学号为"+c.getStuNum()+"，学年为"+c.getSchoolYearStr()+
								"，资助档次为"+c.getHelpGradeStr()+"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(c.getStuNum()+c.getSchoolYearStr()+c.getHelpGradeStr(), c);
					}
				}
				List arrayList = this.countryBurseService.compareHelpData(list);                                  //Excel与已有的重复的数据
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.countryBurseService.importData(list);
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
			return "reward/countryBurse/importHelpBurse";
		}
	}
	
	/** 
	* @Title: comparePunish 
	* @Description:  助学金比对导入的数据
	* @param  @param model
	* @param  @param request
	* @param  @param session
	* @param  @param pageNo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/helpaward/opt-query/compareHelpBurse.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String compareHelpBurse(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
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
	    	CountryBurseInfo info = (CountryBurseInfo) infoArray[0];
	    	CountryBurseInfo xls = (CountryBurseInfo) infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("stuName", info.getStuId().getName());
	    	obj.put("stuId", info.getStuId().getId());
	    	obj.put("year", info.getSchoolYear().getName());
	    	obj.put("burseName", info.getHelpGrade().getName());
	    	obj.put("xlsStuName", xls.getStuName());
	    	obj.put("xlsStuId", xls.getStuNum());
	    	obj.put("xlsYear", xls.getSchoolYearStr());
	    	obj.put("xlsBurseName", info.getHelpGrade().getName());
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
	* @Description:  助学金执行导入
	* @param  @param model
	* @param  @param session
	* @param  @param compareId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@SuppressWarnings("finally")
	@RequestMapping({"/reward/helpaward/opt-query/importHelpData.do"})
	public String importHelpData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		try {
			this.countryBurseService.importHelpData(arrayList, filePath, compareId);
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
		    return "reward/countryBurse/importHelpBurse";
		}
	}
	
	/** 
	* @Title: importBurse 
	* @Description:  国家励志奖学金导入burse
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
	@RequestMapping({"/reward/inspireaward/opt-query/importInspireBurse.do"})
	public String importInspireBurse(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
			HttpServletRequest request, HttpSession session)  {
		
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
		    return "reward/countryBurse/importInspireBurse";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<CountryBurseInfo> list = iu.getDataList(filePath, "importInspireBurse", null, CountryBurseInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, CountryBurseInfo> map = new HashMap<String, CountryBurseInfo>();
				for(CountryBurseInfo c : list) {
					if(map.containsKey(c.getStuNum() + c.getSchoolYearStr()+c.getBurseNameStr())) {
						String message = "模板中数据有重复：学号为"+c.getStuNum()+"，学年为"+c.getSchoolYearStr()+
								"，奖助类型为"+c.getBurseNameStr()+"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(c.getStuNum()+c.getSchoolYearStr()+c.getBurseNameStr(), c);
					}
				}
				List arrayList = this.countryBurseService.compareData(list);                                  //Excel与已有的重复的数据
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.countryBurseService.importData(list);
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
			return "reward/countryBurse/importInspireBurse";
		}
	}
	
	/** 
	* @Title: comparePunish 
	* @Description:  国家励志奖学金比对导入的数据
	* @param  @param model
	* @param  @param request
	* @param  @param session
	* @param  @param pageNo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/inspireaward/opt-query/compareInspireBurse.do"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String compareInspireBurse(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
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
	    	CountryBurseInfo info = (CountryBurseInfo) infoArray[0];
	    	CountryBurseInfo xls = (CountryBurseInfo) infoArray[1];
	    	obj = new JSONObject();
	    	obj.put("stuName", info.getStuId().getName());
	    	obj.put("stuId", info.getStuId().getId());
	    	obj.put("year", info.getSchoolYear().getName());
	    	obj.put("burseName", info.getBurseName().getName());
	    	obj.put("xlsStuName", xls.getStuName());
	    	obj.put("xlsStuId", xls.getStuNum());
	    	obj.put("xlsYear", xls.getSchoolYearStr());
	    	obj.put("xlsBurseName", xls.getBurseNameStr());
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
	* @Description:  国家励志奖学金执行导入
	* @param  @param model
	* @param  @param session
	* @param  @param compareId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@SuppressWarnings("finally")
	@RequestMapping({"/reward/inspireaward/opt-query/importInspireData.do"})
	public String importInspireData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		try {
			this.countryBurseService.importInspireData(arrayList, filePath, compareId);
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
		    return "reward/countryBurse/importInspireBurse";
		}
	}
	
	/** 
	* @Title: deleteCountryBurse 
	* @Description:  删除国家奖学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/countryaward/opt-query/deleteCountryBurse.do"})
	public String deleteCountryBurse(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String id = request.getParameter("id");
		if(id.indexOf("on,") > -1) {
			id = id.replace("on,", ""); 
		}
		if(id.indexOf(",") > -1) {
			String ids[] = id.split(",");
			for(String s : ids) {
				burse = this.countryBurseService.getBurseInfoById(s);
				if(DataUtil.isNotNull(burse)) {
					this.countryBurseService.deleteBurseInfo(burse);
				}
			}
		}else{
			burse = this.countryBurseService.getBurseInfoById(id);
			this.countryBurseService.deleteBurseInfo(burse);
		}
		return "redirect:/reward/countryaward/opt-query/queryCountryBursePage.do";
	}
	
	/** 
	* @Title: deleteInspireBurse 
	* @Description:  删除国家励志奖学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/inspireaward/opt-query/deleteInspireBurse.do"})
	public String deleteInspireBurse(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String id = request.getParameter("id");
		if(id.indexOf("on,") > -1) {
			id = id.replace("on,", ""); 
		}
		if(id.indexOf(",") > -1) {
			String ids[] = id.split(",");
			for(String s : ids) {
				burse = this.countryBurseService.getBurseInfoById(s);
				if(DataUtil.isNotNull(burse)) {
					this.countryBurseService.deleteBurseInfo(burse);
				}
			}
		}else{
			burse = this.countryBurseService.getBurseInfoById(id);
			this.countryBurseService.deleteBurseInfo(burse);
		}
		return "redirect:/reward/inspireaward/opt-query/queryInspireBursePage.do";
	}
	
	/** 
	* @Title: deleteHelpBurse 
	* @Description:  删除国家助学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/helpaward/opt-query/deleteHelpBurse.do"})
	public String deleteHelpBurse(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String id = request.getParameter("id");
		if(id.indexOf("on,") > -1) {
			id = id.replace("on,", ""); 
		}
		if(id.indexOf(",") > -1) {
			String ids[] = id.split(",");
			for(String s : ids) {
				burse = this.countryBurseService.getBurseInfoById(s);
				if(DataUtil.isNotNull(burse)) {
					this.countryBurseService.deleteBurseInfo(burse);
				}
			}
		}else{
			burse = this.countryBurseService.getBurseInfoById(id);
			this.countryBurseService.deleteBurseInfo(burse);
		}
		return "redirect:/reward/helpaward/opt-query/queryHelpBursePage.do";
	}
	
	/** 
	* @Title: viewCountryBurse 
	* @Description:  查看国家奖学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/countryaward/opt-query/viewCountryBurse.do","/reward/countryaward/viewCountryBurse.do"})
	public String viewCountryBurse(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String id = request.getParameter("id");
		burse = this.countryBurseService.getBurseInfoById(id);
		model.addAttribute("burse", burse);
		return "reward/countryBurse/viewCountryBurse";
	}
	
	/** 
	* @Title: viewInspireBurse 
	* @Description:  查看国家励志奖学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/inspireaward/opt-query/viewInspireBurse.do","/reward/inspireaward/viewInspireBurse.do"})
	public String viewInspireBurse(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String id = request.getParameter("id");
		burse = this.countryBurseService.getBurseInfoById(id);
		model.addAttribute("burse", burse);
		return "reward/countryBurse/viewInspireBurse";
	}
	
	/** 
	* @Title: viewHelpBurse 
	* @Description:  查看国家助学金
	* @param  @param model
	* @param  @param request
	* @param  @param burse
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value={"/reward/helpaward/opt-query/viewHelpBurse.do","/reward/helpaward/viewHelpBurse.do"})
	public String viewHelpBurse(ModelMap model,HttpServletRequest request,CountryBurseInfo burse) {
		
		String id = request.getParameter("id");
		burse = this.countryBurseService.getBurseInfoById(id);
		model.addAttribute("burse", burse);
		return "reward/countryBurse/viewHelpBurse";
	}
}

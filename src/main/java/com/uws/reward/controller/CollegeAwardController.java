/**   
* @Title: CollegeAwardController.java 
* @Package com.uws.reward.controller 
* @Description: 
* @author zhangyb   
* @date 2015年12月31日 上午10:36:23 
* @version V1.0   
*/
package com.uws.reward.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CollegeAwardInfo;
import com.uws.reward.service.ICollegeAwardService;
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
 * @ClassName: CollegeAwardController 
 * @Description: 校内奖励 
 * @author zhangyb 
 * @date 2015年12月31日 上午10:36:23  
 */
@Controller
public class CollegeAwardController {
	
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private ICollegeAwardService collegeAwardService;
	@Autowired
	private IStudentCommonService studentCommonService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	private FileUtil fileUtil=FileFactory.getFileUtil();

	/** 
	* @Title: queryCollegeAwardPage 
	* @Description: 校内奖励列表页
	* @param  @param model
	* @param  @param request
	* @param  @param awardInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-query/queryCollegeAwardPage.do"})
	public String queryCollegeAwardPage(ModelMap model,HttpServletRequest request,CollegeAwardInfo collegeAward) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		List<BaseAcademyModel> academyList = new ArrayList<BaseAcademyModel>();
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);  //组织机构ID
		boolean collegeTeacher = CheckUtils.isCurrentOrgEqCollege(orgId);    //是否属于二级学院
		if(collegeTeacher) {
			BaseAcademyModel academy = this.baseDataService.findAcademyById(orgId);
			if(collegeAward.getStudentId() != null) {
				collegeAward.getStudentId().setCollege(academy);
			}else{
				StudentInfoModel stu = new StudentInfoModel();
				stu.setCollege(academy);
				collegeAward.setStudentId(stu);
			}
			academyList.clear();
			academyList.add(academy);
		}else{
			academyList = this.baseDataService.listBaseAcademy();
		}
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != collegeAward && null != collegeAward.getStudentId() 
				&& null != collegeAward.getStudentId().getCollege() 
				&& null != collegeAward.getStudentId().getCollege().getId()
				&& collegeAward.getStudentId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(collegeAward.getStudentId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != collegeAward && null != collegeAward.getStudentId() 
				&& null != collegeAward.getStudentId().getClassId() 
				&& null != collegeAward.getStudentId().getMajor() 
				&& null != collegeAward.getStudentId().getMajor().getId() 
				&& collegeAward.getStudentId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(collegeAward.getStudentId().getMajor().getId());
		}
		if(!DataUtil.isNotNull(collegeAward.getSchoolYear())) {
			Dic currentYear = SchoolYearUtil.getYearDic();
			collegeAward.setSchoolYear(currentYear);
		}
		Page page = this.collegeAwardService.queryCollegeAwardPage(collegeAward, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("collegeAward", collegeAward);
		model.addAttribute("page", page);
		return "reward/collegeAward/queryCollegeAwardList";
	}
	
	/** 
	* @Title: addCollegeAward 
	* @Description: 新增/修改校内奖励
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-add/addCollegeAward.do"})
	public String addCollegeAward(ModelMap model,HttpServletRequest request) {
		
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		String userId = this.sessionUtil.getCurrentUserId();
		User user = new User();
		user.setId(userId);
		user.setName(this.sessionUtil.getCurrentLoginName());
		CollegeAwardInfo collegeAward = new CollegeAwardInfo();
		collegeAward.setCreator(user);
		collegeAward.setSchoolYear(SchoolYearUtil.getYearDic());
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);  //组织机构ID
		boolean collegeTeacher = CheckUtils.isCurrentOrgEqCollege(orgId);    //是否属于二级学院
		if(collegeTeacher) {
			model.addAttribute("collegeId", orgId);
		}
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("collegeAward", collegeAward);
		return "reward/collegeAward/editCollegeAward";
	}
	
	/** 
	* @Title: saveCollegeAward 
	* @Description: 保存更新或者新增
	* @param  @param model
	* @param  @param request
	* @param  @param collegeAward
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-save/saveCollegeAward.do"})
	public String saveCollegeAward(ModelMap model,HttpServletRequest request,CollegeAwardInfo collegeAward,String[] fileId) {
		
		Dic yearDic = this.dicUtil.getDicInfo("YEAR", collegeAward.getSchoolYear().getCode());
		Dic termDic = this.dicUtil.getDicInfo("TERM", collegeAward.getSchoolTerm().getCode());
		collegeAward.setSchoolYear(yearDic);
		collegeAward.setSchoolTerm(termDic);
		if(collegeAward.getStudentId() != null && DataUtil.isNull(collegeAward.getStudentId().getId())) {
			collegeAward.getStudentId().setId(collegeAward.getStudentId().getStuNumber());
		}
		if(DataUtil.isNotNull(collegeAward.getId())) {
			CollegeAwardInfo awardInfo = this.collegeAwardService.getAwardInfoById(collegeAward.getId());
			BeanUtils.copyProperties(collegeAward, awardInfo, new String[]{"createTime","updateTime"});
			this.collegeAwardService.updateAwardInfo(awardInfo, fileId);
		}else{
			this.collegeAwardService.saveAwardInfo(collegeAward, fileId);
		}
		return "redirect:/reward/collegeaward/opt-query/queryCollegeAwardPage.do";
	}
	
	/** 
	* @Title: checkCollegeAward 
	* @Description: 验证奖励是否重复 
	* @param  @param schoolYear
	* @param  @param schoolTerm
	* @param  @param studentId
	* @param  @param awardName
	* @param  @param awardId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-query/checkCollegeAward.do"})
	@ResponseBody
	public String checkCollegeAward(@RequestParam String schoolYear,@RequestParam String schoolTerm,
			@RequestParam String studentId,@RequestParam String awardName,@RequestParam String awardId) {
		
		String result = "true";
		if(DataUtil.isNotNull(awardId)) {   //更新
			CollegeAwardInfo awardInfo = this.collegeAwardService.getAwardInfoById(awardId);
			if(studentId.equals(awardInfo.getStudentId().getId())) {  //没改学生
				long num = this.collegeAwardService.checkAwardInfo(schoolYear, schoolTerm, studentId, awardName);
				if(!awardName.equals(awardInfo.getAwardName()) && (int)num >= 1) {
					result = "false";
				}
			}else{
				long num = this.collegeAwardService.checkAwardInfo(schoolYear, schoolTerm, studentId, awardName);
				result = (int)num <= 0 ? "true" : "false";
			}
		}else{  //新增
			long num = this.collegeAwardService.checkAwardInfo(schoolYear, schoolTerm, studentId, awardName);
			result = (int)num <= 0 ? "true" : "false";
		}
		return result;
	}
	
	/** 
	* @Title: delCollegeAward 
	* @Description: 删除学生校内奖励
	* @param  @param id
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-del/delCollegeAward.do"})
	public String delCollegeAward(String id) {
		
		CollegeAwardInfo awardInfo = this.collegeAwardService.getAwardInfoById(id);
		this.collegeAwardService.delAwardInfo(awardInfo);
		return "redirect:/reward/collegeaward/opt-query/queryCollegeAwardPage.do";
	}
	
	/** 
	* @Title: viewCollegeAward 
	* @Description: 查看学生校内奖励 
	* @param  @param id
	* @param  @param model
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-view/viewCollegeAward.do"})
	public String viewCollegeAward(String id,ModelMap model) {
		
		CollegeAwardInfo awardInfo = this.collegeAwardService.getAwardInfoById(id);
		model.addAttribute("collegeAward", awardInfo);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(awardInfo.getId()));
		return "reward/collegeAward/viewCollegeAward";
	}
	
	/** 
	* @Title: updateCollegeAward 
	* @Description: 修改校内奖励 
	* @param  @param id
	* @param  @param model
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-update/updateCollegeAward.do"})
	public String updateCollegeAward(String id,ModelMap model) {
		
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
		CollegeAwardInfo awardInfo = this.collegeAwardService.getAwardInfoById(id);
		model.addAttribute("collegeAward", awardInfo);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("schoolTermList", schoolTermList);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(awardInfo.getId()));
		return "reward/collegeAward/editCollegeAward";
	}
	
	/** 
	* @Title: importCollegeAward 
	* @Description: 导入校内奖励 
	* @param  @param model
	* @param  @param file
	* @param  @param maxSize
	* @param  @param allowedExt
	* @param  @param request
	* @param  @param session
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/collegeaward/opt-query/importCollegeAward.do"})
	public String importCollegeAward(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, 
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(!returnValue.equals("")) {
			errorTemp = returnValue;
			errorText.add(errorTemp);
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "reward/collegeAward/importCollegeAward";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				List<CollegeAwardInfo> list = iu.getDataList(filePath, "importCollegeAward", null, CollegeAwardInfo.class);        //Excel数据
//				判断模板中有重复数据的情况
				Map<String, CollegeAwardInfo> map = new HashMap<String, CollegeAwardInfo>();
				for(CollegeAwardInfo c : list) {
					if(map.containsKey(c.getStudentIdStr() + c.getAwardName() + c.getSchoolYearStr() 
							+ c.getSchoolTermStr())) {
						String message = "模板中数据有重复：学号为"+c.getStudentIdStr()+"，学年为"+c.getSchoolYearStr()+ "，学期为" 
					+ c.getSchoolTermStr() + "，获奖名称为" + c.getAwardName() +"，请检查后重新上传。";
						Exception e = new Exception(message);
						throw e;
					}else{
						map.put(c.getStudentIdStr() + c.getAwardName() + c.getSchoolYearStr() 
								+ c.getSchoolTermStr(), c);
					}
				}
				this.collegeAwardService.compareCollegeAward(list);
//				List arrayList = this.managePunishService.compareData(list);                                  //Excel与已有的重复的数据
				/*List arrayList = new ArrayList();                                  //导入去重验证 暂时注释掉
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.collegeAwardService.importData(list);
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
			} catch (Exception e) {
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
			return "reward/collegeAward/importCollegeAward";
		}
	}
}

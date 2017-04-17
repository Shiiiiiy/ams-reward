package com.uws.reward.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.apw.model.ApproveResult;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.sys.model.Dic;

/**
 * @author zhangyb
 * @version:2015年8月21日 下午1:44:32
 * @Description: 学生申请service
 */
public interface IStudentApplyService {

	/** 
	* @Title: queryAwardPage 
	* @Description:  列表query方法
	* @param  @param stuApply
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApplyPage(StudentApplyInfo stuApply,int pageNo,int pageSize);
	
	/** 
	* @Title: saveStuApply 
	* @Description:  保存学生申请信息
	* @param  @param stuApply    
	* @return void    
	* @throws 
	*/
	public void saveStuApply(StudentApplyInfo stuApply, String[] fileId);
	
	/** 
	* @Title: updateStuApply 
	* @Description:  更新学生申请信息
	* @param  @param stuApply    
	* @return void    
	* @throws 
	*/
	public void updateStuApply(StudentApplyInfo stuApply,String[] fileId);
	
	/** 
	* @Title: deleteStuApply 
	* @Description:  删除学生申请信息
	* @param  @param stuApply    
	* @return void    
	* @throws 
	*/
	public void deleteStuApply(StudentApplyInfo stuApply);
	
	/** 
	* @Title: updateStuApply 
	* @Description: 更新学生申请信息 
	* @param  @param stuApply    
	* @return void    
	* @throws 
	*/
	public void updateStuApply(StudentApplyInfo stuApply);
	
	/** 
	* @Title: getStuApplyInfoList 
	* @Description:  获取该学生的评奖评优申请记录
	* @param  @param stuAplyInfo
	* @param  @return    
	* @return List<StudentApplyInfo>    
	* @throws 
	*/
	public List<StudentApplyInfo> getStuApplyInfoList(StudentApplyInfo stuApplyInfo);
	
	/**
	 * @param fileId  
	* @Title: saveStuApplyApproveResult 
	* @Description:  保存学生申请
	* @param  @param objectId
	* @param  @param result
	* @param  @param nextApproverId
	* @param  @return    
	* @return ApproveResult    
	* @throws 
	*/
	public ApproveResult saveStuApplyApproveResult(String objectId,ApproveResult result,String nextApproverId, String[] fileId);
	
	/** 
	* @Title: getStuApplyInfoById 
	* @Description:  通过id获取学生申请对象
	* @param  @param id
	* @param  @return    
	* @return StudentApplyInfo    
	* @throws 
	*/
	public StudentApplyInfo getStuApplyInfoById(String id);

	/** 
	* @Title: queryStuApplyPage 
	* @Description:  通过当前登录人查询需此人审批的数据
	* @param  @param stuApply
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @param userId当前登录人
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApplyPage(StudentApplyInfo stuApply, int pageNo, int pageSize,
			String userId,String[] objectIds);
	
	/** 
	* @Title: queryStudentPage 
	* @Description:  学生评奖评优查询
	* @param  @param stuApply
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStudentPage(StudentApplyInfo stuApply, int pageNo, int pageSize);
	
	/** 
	* @Title: getStuApplyList 
	* @Description:  统计获取学生评奖评优信息list
	* @param  @param stuApply
	* @param  @return    
	* @return List<StudentApplyInfo>    
	* @throws 
	*/
	public List<StudentApplyInfo> getStuApplyList(StudentApplyInfo stuApply);
	
	/** 
	* @Title: getResultMap 
	* @Description:  获取指定学年指定学院的统计封装map信息 key是班级Id value是统计结果对象
	* @param  @param year
	* @param  @param academyId
	* @param  @return    
	* @return Map<String,Object>    
	* @throws 
	*/
	public Map<String,Object> getAcademyResultMap(Dic year,String academyId);
	
	/** 
	* @Title: getClassMap 
	* @Description:  获取该学院下所有专业的班级map key为专业Id  value为班级list
	* @param  @param academyId
	* @param  @return    
	* @return Map<String,ArrayList<BaseClassModel>>    
	* @throws 
	*/
	public Map<String,ArrayList<BaseClassModel>> getAcademyClassMap(String academyId);
	
	/** 
	* @Title: getClassResultMap 
	* @Description:  获取该班级指定学年的统计map
	* @param  @param year
	* @param  @param classId
	* @param  @return    
	* @return Map<String,Object>    
	* @throws 
	*/
	public Map<String,Object> getClassResultMap(Dic year,List<BaseClassModel> classList);
	
	/** 
	* @Title: getCollegeResultMap 
	* @Description:  封装所有学院下的各个专业统计信息
	* @param  @param year
	* @param  @param academyList
	* @param  @return    
	* @return Map<String,Object>    
	* @throws 
	*/
	public Map<String,Object> getCollegeResultMap(Dic year,List<BaseAcademyModel> academyList);
	
	/** 
	* @Title: getMajorMap 
	* @Description:  获取各个学院下的专业集合 key为学院Id value为专业list
	* @param  @param academyList
	* @param  @return    
	* @return Map<String,ArrayList<BaseMajorModel>>    
	* @throws 
	*/
	public Map<String,ArrayList<BaseMajorModel>> getMajorMap(List<BaseAcademyModel> academyList);
	
	/** 
	* @Title: packAcademyHssf 
	* @Description:  导出学院领导视角下的统计数据
	* @param  @param academyId
	* @param  @param resultMap
	* @param  @return    
	* @return HSSFWorkbook    
	* @throws 
	*/
	public HSSFWorkbook packAcademyHssf(Dic year,String academyId,Map<String, Object> resultMap);
	
	/** 
	* @Title: packCollegeHssf 
	* @Description:  导出校领导视角下的统计数据
	* @param  @param year
	* @param  @param academyList
	* @param  @param resultMap
	* @param  @return    
	* @return HSSFWorkbook    
	* @throws 
	*/
	public HSSFWorkbook packCollegeHssf(Dic year,List<BaseAcademyModel> academyList,Map<String, Object> resultMap);
	
	/** 
	* @Title: packTeacherHssf 
	* @Description:  导出班主任视角下的统计数据
	* @param  @param year
	* @param  @param classList
	* @param  @param resultMap
	* @param  @return    
	* @return HSSFWorkbook    
	* @throws 
	*/
	public HSSFWorkbook packTeacherHssf(Dic year,List<BaseClassModel> classList,Map<String, Object> resultMap);
	
	/** 
	* @Title: importData 
	* @Description:  学生处导入学生申请
	* @param  @param stuInfoList    
	* @return void    
	* @throws 
	*/
	public void importData(List<StudentApplyInfo> stuInfoList);
	
	/**
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws ExcelException 
	 * @throws IllegalAccessException 
	 * @throws IOException 
	 * @throws OfficeXmlFileException  
	* @Title: importData 
	* @Description:  学生处导入学生申请
	* @param  @param list
	* @param  @param filePath
	* @param  @param compareId    
	* @return void    
	* @throws 
	*/
	public void importData(List<Object[]> list, String filePath,
			String compareId) throws OfficeXmlFileException, IOException, 
			IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception;
	
	/** 
	* @Title: compareData 
	* @Description:  去重对比
	* @param  @param list
	* @param  @return    
	* @return List<Object[]>    
	* @throws 
	*/
	public List<Object[]> compareData(List<StudentApplyInfo> list);
	
	/** 
	* @Title: saveMulResult 
	* @Description:  保存批量审批信息
	* @param  @param resultList    
	* @return void    
	* @throws 
	*/
	public void saveMulResult(List<ApproveResult> resultList);
	
	/** 
	* @Title: getConInfoValue 
	* @Description: 获取该学生该条件成绩
	* @param  @param stuId
	* @param  @param yearDic
	* @param  @param infoList
	* @param  @param map
	* @param  @return    
	* @return List<ConditionInfo>    
	* @throws 
	*/
	public List<ConditionInfo> getStuConInfoValue(String stuId,Dic yearDic,List<ConditionInfo> infoList,Map<String,String> map);
	
	/** 
	* @Title: checkStuApplyXingZhi 
	* @Description: 判断学生是否已申请过行知奖学金
	* @param  @param stuId
	* @param  @param yearDic
	* @param  @return    
	* @return boolean    
	* @throws 
	*/
	public boolean checkStuApplyXingZhi(String stuId,Dic yearDic,Dic applyStatus);
	
	/** 
	* @Title: getApplyInfo 
	* @Description: 获取当前年指定二级奖项的学生申请 
	* @param  @param secondAwardName
	* @param  @param Year
	* @param  @return    
	* @return StudentApplyInfo    
	* @throws 
	*/
	public StudentApplyInfo getApplyInfo(String secondAwardName,Dic year,String stuId);
	
	/***
	 * 判断学生该学年是否存在互斥情况，三好学生和优秀班干部互斥
	 * @param stuId
	 * @param yearDic
	 * @param rewardCode
	 * @return
	 */
	public boolean checkStuSanHaoStuleader(String stuId,Dic yearDic,String rewardCode);
	
	/***
	 * 判断申请通过的名额
	 * @param stuApplyId
	 * @return
	 */
	public boolean checkApprovedPass(String stuApplyId);
}

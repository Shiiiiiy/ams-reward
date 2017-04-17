package com.uws.reward.dao;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.sys.model.Dic;

/** 
 * @ClassName: IStudentApplyDao 
 * @Description:  学生评优评奖申请DAO
 * @author zhangyb 
 * @date 2015年8月21日 下午1:52:52  
 */
public interface IStudentApplyDao {
	
	/** 
	* @Title: queryStuApplyPage 
	* @Description:  学生申请列表查询
	* @param  @param stuApplyInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApplyPage(StudentApplyInfo stuApplyInfo,int pageNo,int pageSize);
	
	/** 
	* @Title: saveStuApply 
	* @Description:  保存学生申请
	* @param  @param stuApplyInfo    
	* @return void    
	* @throws 
	*/
	public void saveStuApply(StudentApplyInfo stuApplyInfo);
	
	/** 
	* @Title: updateStuApply 
	* @Description:  更新学生申请
	* @param  @param stuApplyInfo    
	* @return void    
	* @throws 
	*/
	public void updateStuApply(StudentApplyInfo stuApplyInfo);
	
	/** 
	* @Title: deleteStuApply 
	* @Description:  删除学生申请
	* @param  @param stuApplyInfo    
	* @return void    
	* @throws 
	*/
	public void deleteStuApply(StudentApplyInfo stuApplyInfo);
	
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
	* @Title: getStuApplyInfoById 
	* @Description:  通过ID获取学生申请对象
	* @param  @param id
	* @param  @return    
	* @return StudentApplyInfo    
	* @throws 
	*/
	public StudentApplyInfo getStuApplyInfoById(String id);

	/** 
	* @Title: queryStuApplyPage 
	* @Description:  查询当前登录人的待审批学生申请
	* @param  @param stuApply
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @param userId
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApplyPage(StudentApplyInfo stuApply, int pageNo,
			int pageSize, String userId,String[] objectIds);
	
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
	public Page queryStudentPage(StudentApplyInfo stuApply, int pageNo,
			int pageSize);
	

	/** 
	* @Title: getStuInfoList 
	* @Description:  统计汇总评奖评优
	* @param  @param stuApplyInfo
	* @param  @return    
	* @return List<StudentApplyInfo>    
	* @throws 
	*/
	public List<StudentApplyInfo> getStuInfoList(StudentApplyInfo stuApplyInfo);
	
	/** 
	* @Title: countStuApply 
	* @Description:  get student apply num
	* @param  @return    
	* @return long    
	* @throws 
	*/
	public long countStuApply();
	
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
	public StudentApplyInfo getApplyInfo(String secondAwardName,Dic Year,String stuId);
	
	/***
	 * 判断学生该学年是否存在互斥情况，三好学生和优秀班干部互斥
	 * @param stuId
	 * @param yearDic
	 * @param rewardCode
	 * @return
	 */
	public boolean checkStuSanHaoStuleader(String stuId,Dic yearDic,String rewardCode);
	
	/***
	 * 审批通过的名额
	 * @param awardTypeId
	 * @param collegeId
	 * @return
	 */
	public int queryApproveSum(String awardTypeId, String collegeId);
}

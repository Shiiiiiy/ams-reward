package com.uws.reward.dao;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.ClassApplyInfo;

/** 
* @ClassName: IClassApplyDao 
* @Description:  班级申请dao
* @author zhangyb 
* @date 2015年9月7日 下午6:13:46  
*/
public interface IClassApplyDao {

	/** 
	* @Title: queryClassApplyPage 
	* @Description:  班级申请列表页
	* @param  @param classApplyInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo,int pageNo,int pageSize);
	
	/** 
	* @Title: saveClassApply 
	* @Description:  保存
	* @param  @param classApplyInfo    
	* @return void    
	* @throws 
	*/
	public void saveClassApply(ClassApplyInfo classApplyInfo);
	
	/** 
	* @Title: updateClassApply 
	* @Description:  修改
	* @param  @param classApplyInfo    
	* @return void    
	* @throws 
	*/
	public void updateClassApply(ClassApplyInfo classApplyInfo);
	
	/** 
	* @Title: deleteClassApply 
	* @Description:  删除
	* @param  @param classApplyInfo    
	* @return void    
	* @throws 
	*/
	public void deleteClassApply(ClassApplyInfo classApplyInfo);
	
	/** 
	* @Title: getClassApplyInfoList 
	* @Description:  获取该班级的评奖评优申请记录
	* @param  @param classApplyInfo
	* @param  @return    
	* @return List<ClassApplyInfo>    
	* @throws 
	*/
	public List<ClassApplyInfo> getClassApplyInfoList(ClassApplyInfo classApplyInfo);
	
	/** 
	* @Title: getClassApplyInfoById 
	* @Description:  get classApplyInfo by id
	* @param  @param id
	* @param  @return    
	* @return ClassApplyInfo    
	* @throws 
	*/
	public ClassApplyInfo getClassApplyInfoById(String id);
	
	/** 
	* @Title: queryClassApplyPage 
	* @Description:  根据当前用户查询班级申请列表页
	* @param  @param classApplyInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @param userId
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo, int pageNo,
			int pageSize, String userId, String[] objectIds);
	
	/** 
	* @Title: getClassInfoList 
	* @Description:  统计
	* @param  @param classInfo
	* @param  @return    
	* @return List<ClassApplyInfo>    
	* @throws 
	*/
	public List<ClassApplyInfo> getClassInfoList(ClassApplyInfo classInfo);
	
	/** 
	* @Title: countClassApply 
	* @Description:  get class apply number
	* @param  @return    
	* @return long    
	* @throws 
	*/
	public long countClassApply();
}

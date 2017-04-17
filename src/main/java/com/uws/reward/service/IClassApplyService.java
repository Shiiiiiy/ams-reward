package com.uws.reward.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.apw.model.ApproveResult;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.ClassApplyInfo;

/** 
* @ClassName: IClassApplyService 
* @Description:  班级申请service 
* @author zhangyb 
* @date 2015年9月7日 下午6:15:39  
*/
public interface IClassApplyService {

	/** 
	* @Title: queryClassApplyPage 
	* @Description:  classApply page query function
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
	* @Description:  save classApply model
	* @param  @param classApplyInfo
	* @param  @param fileId    
	* @return void    
	* @throws 
	*/
	public void saveClassApply(ClassApplyInfo classApplyInfo, String[] fileId);
	
	/** 
	* @Title: updateClassApply 
	* @Description:  update classApply model
	* @param  @param classApplyInfo
	* @param  @param fileId    
	* @return void    
	* @throws 
	*/
	public void updateClassApply(ClassApplyInfo classApplyInfo,String[] fileId);
	
	/** 
	* @Title: deleteClassApply 
	* @Description:  delete classApply model
	* @param  @param classApplyInfo    
	* @return void    
	* @throws 
	*/
	public void deleteClassApply(ClassApplyInfo classApplyInfo);
	
	/** 
	* @Title: getClassApplyInfoList 
	* @Description:  获取该班级申请记录
	* @param  @param classApplyInfo
	* @param  @return    
	* @return List<ClassApplyInfo>    
	* @throws 
	*/
	public List<ClassApplyInfo> getClassApplyInfoList(ClassApplyInfo classApplyInfo);
	
	/** 
	* @Title: saveClassApplyApproveResult 
	* @Description:  保存班级申请
	* @param  @param objectId
	* @param  @param result
	* @param  @param nextApproverId
	* @param  @param fileId
	* @param  @return    
	* @return ApproveResult    
	* @throws 
	*/
	public ApproveResult saveClassApplyApproveResult(String objectId,ApproveResult result,String nextApproverId, String[] fileId);
	
	/** 
	* @Title: getClassApplyInfoById 
	* @Description:  通过ID获取班级申请记录
	* @param  @param id
	* @param  @return    
	* @return ClassApplyInfo    
	* @throws 
	*/
	public ClassApplyInfo getClassApplyInfoById(String id);
	
	/** 
	* @Title: queryStuApplyPage 
	* @Description:  获取当前登录人的待审批信息
	* @param  @param classApplyInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @param userId
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryClassApplyPage(ClassApplyInfo classApplyInfo, int pageNo, int pageSize,
			String userId, String[] objectIds);
	
	/** 
	* @Title: getClassInfoList 
	* @Description:  获取统计list
	* @param  @param classInfo
	* @param  @return    
	* @return List<ClassApplyInfo>    
	* @throws 
	*/
	public List<ClassApplyInfo> getClassInfoList(ClassApplyInfo classInfo);
	
	public void importData(List<ClassApplyInfo> classInfoList);
	
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
	public List<Object[]> compareData(List<ClassApplyInfo> list);
	
	/** 
	* @Title: saveMulResult 
	* @Description:  保存批量审批信息
	* @param  @param resultList    
	* @return void    
	* @throws 
	*/
	public void saveMulResult(List<ApproveResult> resultList);
}

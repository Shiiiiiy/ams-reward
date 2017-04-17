/**   
* @Title: ICollegeAwardService.java 
* @Package com.uws.reward.service 
* @author zhangyb   
* @date 2015年12月31日 上午10:46:19 
* @version V1.0   
*/
package com.uws.reward.service;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.CollegeAwardInfo;

/** 
 * @ClassName: ICollegeAwardService 
 * @Description: 校内奖励service 
 * @author zhangyb 
 * @date 2015年12月31日 上午10:46:19  
 */
public interface ICollegeAwardService {

	/** 
	* @Title: queryCollegeAwardPage 
	* @Description: 校内奖励列表页查询 
	* @param  @param awardInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryCollegeAwardPage(CollegeAwardInfo awardInfo,int pageNo,int pageSize);
	
	/** 
	* @Title: save 
	* @Description: 保存 
	* @param  @param awardInfo    
	* @return void    
	* @throws 
	*/
	public void saveAwardInfo(CollegeAwardInfo awardInfo,String[] fileId);
	
	/** 
	* @Title: delAwardInfo 
	* @Description: 删除 
	* @param  @param awardInfo    
	* @return void    
	* @throws 
	*/
	public void delAwardInfo(CollegeAwardInfo awardInfo);
	
	/** 
	* @Title: updateAwardInfo 
	* @Description: 更新 
	* @param  @param awardInfo    
	* @return void    
	* @throws 
	*/
	public void updateAwardInfo(CollegeAwardInfo awardInfo,String[] fileId);
	
	/** 
	* @Title: getAwardInfoById 
	* @Description: 根据ID获取校内奖励 
	* @param  @param awardInfoId
	* @param  @return    
	* @return CollegeAwardInfo    
	* @throws 
	*/
	public CollegeAwardInfo getAwardInfoById(String awardInfoId);
	
	/** 
	* @Title: checkAwardInfo 
	* @Description: 验证学生奖励是否重复
	* @param  @param schoolYear
	* @param  @param schoolTerm
	* @param  @param studentId
	* @param  @param awardName
	* @param  @return    
	* @return long    
	* @throws 
	*/
	public long checkAwardInfo(String schoolYear,String schoolTerm,String studentId,String awardName);
	
	/** 
	* @Title: importData 
	* @Description: 导入获奖信息 
	* @param  @param list    
	* @return void    
	* @throws 
	*/
	public void importData(List<CollegeAwardInfo> list);
	
	/** 
	* @Title: compareCollegeAward 
	* @Description: 更新重复数据 
	* @param  @param list    
	* @return void    
	* @throws 
	*/
	public void compareCollegeAward(List<CollegeAwardInfo> list);
}

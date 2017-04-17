/**   
* @Title: ICollegeAwardDao.java 
* @Package com.uws.reward.dao 
* @author zhangyb   
* @date 2015年12月31日 上午10:37:16 
* @version V1.0   
*/
package com.uws.reward.dao;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.CollegeAwardInfo;

/** 
 * @ClassName: ICollegeAwardDao 
 * @Description: 校内奖励dao 
 * @author zhangyb 
 * @date 2015年12月31日 上午10:37:16  
 */
public interface ICollegeAwardDao {

	
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
	public void saveAwardInfo(CollegeAwardInfo awardInfo);
	
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
	public void updateAwardInfo(CollegeAwardInfo awardInfo);
	
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
	* @Title: countCollegeAwardNum 
	* @Description: 获取校内奖励总数 
	* @param  @return    
	* @return long    
	* @throws 
	*/
	public long countCollegeAwardNum();
}

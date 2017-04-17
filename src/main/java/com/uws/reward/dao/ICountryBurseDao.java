package com.uws.reward.dao;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CountryBurseInfo;

/** 
* @ClassName: ICountryBurseDao 
* @Description:  国家奖助dao
* @author zhangyb 
* @date 2015年8月27日 上午11:13:25  
*/
public interface ICountryBurseDao {

	/** 
	* @Title: queryBursePage 
	* @Description:  国家奖助page query
	* @param  @param burse
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @param type
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryBursePage(CountryBurseInfo burse,int pageNo,int pageSize);
	
	/** 
	* @Title: saveBurseInfo 
	* @Description:  保存国家奖助
	* @param  @param burse    
	* @return void    
	* @throws 
	*/
	public void saveBurseInfo(CountryBurseInfo burse);
	
	/** 
	* @Title: updateBurseInfo 
	* @Description:  更新国家奖助
	* @param  @param burse    
	* @return void    
	* @throws 
	*/
	public void updateBurseInfo(CountryBurseInfo burse);
	
	/** 
	* @Title: deleteBurseInfo 
	* @Description:  删除国家奖助
	* @param  @param burse    
	* @return void    
	* @throws 
	*/
	public void deleteBurseInfo(CountryBurseInfo burse);
	
	/** 
	* @Title: getBurseInfoById 
	* @Description:  通过Id获取国家奖助object
	* @param  @param id
	* @param  @return    
	* @return CountryBurseInfo    
	* @throws 
	*/
	public CountryBurseInfo getBurseInfoById(String id);
	
	/** 
	* @Title: countCountryBurse 
	* @Description:  获取数据条数
	* @param  @return    
	* @return long    
	* @throws 
	*/
	public long countCountryBurse();
	
	/** 
	* @Title: getStuBurseList 
	* @Description:  获取该生所有国家奖助信息
	* @param  @param student
	* @param  @return    
	* @return List<CountryBurseInfo>    
	* @throws 
	*/
	public List<CountryBurseInfo> getStuBurseList(StudentInfoModel student);
}

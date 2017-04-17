package com.uws.reward.dao;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.PunishInfo;

/** 
* @ClassName: IManagePunishDao 
* @Description:  惩罚管理DAO
* @author zhangyb 
* @date 2015年8月24日 上午10:29:00  
*/
public interface IManagePunishDao {

	/** 
	* @Title: queryPunishPage 
	* @Description:  惩罚管理维护页面查询
	* @param  @param punishInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryPunishPage(PunishInfo punishInfo,int pageNo,int pageSize);
	
	/** 
	* @Title: savePunish 
	* @Description:  保存punishinfo 
	* @param  @param punishInfo    
	* @return void    
	* @throws 
	*/
	public void savePunish(PunishInfo punishInfo);
	
	/** 
	* @Title: updatePunish 
	* @Description:  更新punishInfo
	* @param  @param punishInfo    
	* @return void    
	* @throws 
	*/
	public void updatePunish(PunishInfo punishInfo);
	
	/** 
	* @Title: deletePunish 
	* @Description:  删除punishInfo
	* @param  @param punishInfo    
	* @return void    
	* @throws 
	*/
	public void deletePunish(PunishInfo punishInfo);
	
	/** 
	* @Title: getPunishInfoById 
	* @Description:  通过Id获取punishInfo
	* @param  @param id
	* @param  @return    
	* @return PunishInfo    
	* @throws 
	*/
	public PunishInfo getPunishInfoById(String id);
	
	/** 
	* @Title: punishList 
	* @Description:  获取punishList
	* @param  @param punishInfo
	* @param  @return    
	* @return List<PunishInfo>    
	* @throws 
	*/
	public List<PunishInfo> getPunishList(String stuId,String punishNum);
	
	/** 
	* @Title: queryPunishPage 
	* @Description:  根据flag判断查询单个学生的或者全部的
	* @param  @param punishInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @param flag
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryPunishPage(PunishInfo punishInfo,int pageNo,int pageSize,String flag);
	
	/** 
	* @Title: countPunishNum 
	* @Description:  获取所有的数据数
	* @param  @return    
	* @return long    
	* @throws 
	*/
	public long countPunishNum();
	
	/** 
	* @Title: getStuPunishList 
	* @Description:  获取学生所有惩罚信息
	* @param  @param punishInfo
	* @param  @return    
	* @return List<PunishInfo>    
	* @throws 
	*/
	public List<PunishInfo> getStuPunishList(PunishInfo punishInfo);
}

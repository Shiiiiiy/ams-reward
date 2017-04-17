package com.uws.reward.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.reward.PunishInfo;

/** 
* @ClassName: IManagePunishService 
* @Description:  惩罚管理service
* @author zhangyb 
* @date 2015年8月24日 上午10:31:07  
*/
public interface IManagePunishService {

	/** 
	* @Title: queryPunishPage 
	* @Description:  查询惩罚管理page
	* @param  @param punishInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryPunishPage(PunishInfo punishInfo,int pageNo,int pageSize);
	
	/**
	 * @param fileId  
	* @Title: savePunish 
	* @Description:  保存惩罚信息
	* @param  @param punishInfo    
	* @return void    
	* @throws 
	*/
	public void savePunish(PunishInfo punishInfo, String[] fileId);
	
	/** 
	* @Title: updatePunish 
	* @Description:  更新
	* @param  @param punishInfo    
	* @return void    
	* @throws 
	*/
	public void updatePunish(PunishInfo punishInfo,String[] fileId);
	
	/** 
	* @Title: deletePunish 
	* @Description:  删除
	* @param  @param punishInfo    
	* @return void    
	* @throws 
	*/
	public void deletePunish(PunishInfo punishInfo);
	
	/** 
	* @Title: getPunishInfoById 
	* @Description:  get punishInfo by id
	* @param  @param id
	* @param  @return    
	* @return PunishInfo    
	* @throws 
	*/
	public PunishInfo getPunishInfoById(String id);
	
	/** 
	* @Title: checkPunish 
	* @Description:  通过学生ID和处分文号确定是否重复
	* @param  @param stuId
	* @param  @param punishNum
	* @param  @return    
	* @return boolean    
	* @throws 
	*/
	public boolean checkPunish(String stuId,String punishNum);
	
	public void importData(List<PunishInfo> list);
	
	/**
	 * @throws Exception  
	* @Title: importData 
	* @Description:  导入Excel方法
	* @param  @param list
	* @param  @param filePath
	* @param  @param compareId
	* @param  @throws OfficeXmlFileException
	* @param  @throws IOException
	* @param  @throws IllegalAccessException
	* @param  @throws ExcelException
	* @param  @throws InstantiationException
	* @param  @throws ClassNotFoundException    
	* @return void    
	* @throws 
	*/
	public void importData(List<Object[]> list, String filePath, String compareId) throws OfficeXmlFileException, 
		IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception;
	
	/** 
	* @Title: compareData 
	* @Description:  导入去重方法
	* @param  @param list
	* @param  @return
	* @param  @throws OfficeXmlFileException
	* @param  @throws IOException
	* @param  @throws IllegalAccessException
	* @param  @throws ExcelException
	* @param  @throws InstantiationException
	* @param  @throws ClassNotFoundException    
	* @return List<Object[]>    
	* @throws 
	*/
	public List<Object[]> compareData(List<PunishInfo> list) throws OfficeXmlFileException, IOException, 
		IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException;
	
	/** 
	* @Title: queryPunishPage 
	* @Description:  违纪信息查询
	* @param  @param punishInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryPunishPage(PunishInfo punishInfo,int pageNo,int pageSize,String flag);
	
	/** 
	* @Title: getStuPunishList 
	* @Description:  获取该学生所有惩罚信息
	* @param  @param punishInfo
	* @param  @return    
	* @return List<PunishInfo>    
	* @throws 
	*/
	public List<PunishInfo> getStuPunishList(PunishInfo punishInfo);
	
	/** 
	* @Title: updatePunishInfo 
	* @Description: 更新处分信息 
	* @param  @param list    
	* @return void    
	* @throws 
	*/
	public void comparePunishInfo(List<PunishInfo> list);
	
	/**
	 * 
	 * @Title: checkUserIsExist
	 * @Description: 判断角色定义是否存在
	 * @param userId
	 * @param roleCode
	 * @return
	 * @throws
	 */
	public boolean checkUserIsExist(String userId,String roleCode);
}

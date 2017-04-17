package com.uws.reward.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.CountryBurseInfo;

/** 
* @ClassName: ICountryBurseService 
* @Description: 国家奖助service 
* @author zhangyb 
* @date 2015年8月27日 上午11:15:21  
*/
public interface ICountryBurseService {
	
	/** 
	* @Title: queryBursePage 
	* @Description:  
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
	* @Title: importData 
	* @Description:  导入国家奖助
	* @param  @param list    
	* @return void    
	* @throws 
	*/
	void importData(List<CountryBurseInfo> list);

	/** 
	* @Title: importData 
	* @Description:  导入指定数据
	* @param  @param list
	* @param  @param filePath
	* @param  @param compareId
	* @param  @throws OfficeXmlFileException
	* @param  @throws IOException
	* @param  @throws IllegalAccessException
	* @param  @throws ExcelException
	* @param  @throws InstantiationException
	* @param  @throws ClassNotFoundException
	* @param  @throws Exception    
	* @return void    
	* @throws 
	*/
	void importCountryData(List<Object[]> list, String filePath, String compareId)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException,
			Exception;
	
	/** 
	* @Title: importData 
	* @Description:  导入指定数据
	* @param  @param list
	* @param  @param filePath
	* @param  @param compareId
	* @param  @throws OfficeXmlFileException
	* @param  @throws IOException
	* @param  @throws IllegalAccessException
	* @param  @throws ExcelException
	* @param  @throws InstantiationException
	* @param  @throws ClassNotFoundException
	* @param  @throws Exception    
	* @return void    
	* @throws 
	*/
	void importInspireData(List<Object[]> list, String filePath, String compareId)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException,
			Exception;

	/** 
	* @Title: compareData 
	* @Description:  比对数据 
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
	List<Object[]> compareData(List<CountryBurseInfo> list)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException;

	/** 
	* @Title: importHelpData 
	* @Description:  助学金导入
	* @param  @param list
	* @param  @param filePath
	* @param  @param compareId
	* @param  @throws OfficeXmlFileException
	* @param  @throws IOException
	* @param  @throws IllegalAccessException
	* @param  @throws ExcelException
	* @param  @throws InstantiationException
	* @param  @throws ClassNotFoundException
	* @param  @throws Exception    
	* @return void    
	* @throws 
	*/
	void importHelpData(List<Object[]> list, String filePath, String compareId)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException,
			Exception;
	
	/** 
	* @Title: getStuBurseList 
	* @Description:  获取该学生所有国家奖助信息
	* @param  @param student
	* @param  @return    
	* @return List<CountryBurseInfo>    
	* @throws 
	*/
	public List<CountryBurseInfo> getStuBurseList(StudentInfoModel student);

	/** 
	* @Title: compareHelpData 
	* @Description:  国家助学金比较方法
	* @param  @param list
	* @param  @return
	* @param  @throws OfficeXmlFileException
	* @param  @throws IOException
	* @param  @throws IllegalAccessException
	* @param  @throws ExcelException
	* @param  @throws InstantiationException
	* @param  @throws ClassNotFoundException    
	* @return List<Object []>    
	* @throws 
	*/
	List<Object[]> compareHelpData(List<CountryBurseInfo> list)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException;

}

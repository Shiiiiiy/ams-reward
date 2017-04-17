/**   
* @Title: CollegeAwardRule.java 
* @Package com.uws.reward.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2016年1月4日 下午4:59:43 
* @version V1.0   
*/
package com.uws.reward.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/** 
 * @ClassName: CollegeAwardRule 
 * @Description: 校内奖励规则验证 
 * @author zhangyb 
 * @date 2016年1月4日 下午4:59:43  
 */
public class CollegeAwardRule implements IRule {
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
	List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");

	/* (非 Javadoc) 
	 * <p>Title: validate</p> 
	 * <p>Description: </p> 
	 * @param paramExcelData
	 * @param paramExcelColumn
	 * @param paramMap
	 * @throws ExcelException 
	 * @see com.uws.core.excel.rule.IRule#validate(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map) 
	 */
	@Override
	public void validate(ExcelData data,
			ExcelColumn cloumn, Map map) throws ExcelException {

		IStudentCommonService studentCommonService = (IStudentCommonService) 
				SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		boolean flag = false;
		boolean insert = false;
		String value = this.subStr(data.getValue().toString());
		if("studentIdStr".equalsIgnoreCase(cloumn.getTable_column())) {
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			StudentInfoModel stu = studentCommonService.queryStudentById(bd.toPlainString());
			if(DataUtil.isNotNull(stu)) {
				flag = true;
			}else{
				flag = false;
			}
		}
		if("schoolYearStr".equalsIgnoreCase(cloumn.getTable_column())) {
			insert = true;
			for(Dic dic : this.schoolYearList) {
				if(dic.getName().equals(value)) {
					flag = true;
					break;
				}
			}
		}
		if("schoolTermStr".equalsIgnoreCase(cloumn.getTable_column())) {
			insert = true;
			for(Dic dic : this.schoolTermList) {
				if(dic.getName().equals(value)) {
					flag = true;
					break;
				}
			}
		}
		if((insert) && (!flag)){
			String isText = data.getId().replaceAll("\\$", "");
			throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")在数据库中不存在，请严格按照选项进行选择；<br/>");
		}
	}

	/* (非 Javadoc) 
	 * <p>Title: format</p> 
	 * <p>Description: </p> 
	 * @param paramExcelData
	 * @param paramExcelColumn
	 * @param paramMap 
	 * @see com.uws.core.excel.rule.IRule#format(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map) 
	 */
	@Override
	public void format(ExcelData paramExcelData, ExcelColumn paramExcelColumn,
			Map paramMap) {

	}

	/* (非 Javadoc) 
	 * <p>Title: operation</p> 
	 * <p>Description: </p> 
	 * @param paramExcelData
	 * @param paramExcelColumn
	 * @param paramMap
	 * @param paramMap1
	 * @param paramInt 
	 * @see com.uws.core.excel.rule.IRule#operation(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map, java.util.Map, int) 
	 */
	@Override
	public void operation(ExcelData data,
			ExcelColumn cloumn, Map initData,
			Map<String, ExcelData> eds, int site) {

		if("studentIdStr".equals(cloumn.getName())) {
			String stuId = this.subStr(this.getString(site, eds, "C"));
			BigDecimal bd = new BigDecimal(stuId);
			data.setValue(bd.toPlainString());
		}
		if("schoolYear".equals(cloumn.getName())) {
			String year = this.getString(site, eds, "A");
			for(Dic dic : this.schoolYearList) {
				if(year.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
		if("schoolTerm".equals(cloumn.getName())) {
			String term = this.getString(site, eds, "B");
			for(Dic dic : this.schoolTermList) {
				if(term.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
	}
	
	private String getString(int site, Map eds, String key){
        String s = "";
        String keyName = (new StringBuilder("$")).append(key).append("$").append(site).toString();
        if(eds.get(keyName) != null && ((ExcelData)eds.get(keyName)).getValue() != null)
            s = (new StringBuilder(String.valueOf(s))).append((String)((ExcelData)eds.get(keyName)).getValue()).toString();
        return s.trim();
    }
	
	private String subStr(String str) {
		
		if(str.endsWith(".0")) {
			str = str.replace(".0", "");
		}
		return str.trim();
	}

}

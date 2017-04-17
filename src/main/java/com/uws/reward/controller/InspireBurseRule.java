/**   
* @Title: InspireBurseRule.java 
* @Package com.uws.reward.controller 
* @Description: (用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 上午10:29:57 
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
 * @ClassName: InspireBurseRule 
 * @Description:  
 * @author zhangyb 
 * @date 2015年10月21日 上午10:29:57  
 */
public class InspireBurseRule implements IRule {

	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
	List<Dic> burseNameList = this.dicUtil.getDicInfoList("COUNTRY_BURSE_TYPE");
	List<Dic> helpList = this.dicUtil.getDicInfoList("AID_LEVELS");
	private Dic inspireBurseDic = this.dicUtil.getDicInfo("COUNTRY_BURSE_TYPE", "COUNTRY_INSPIRE_AWARD");
	@Override
	public void format(ExcelData arg0, ExcelColumn arg1, Map arg2) {
		//  Auto-generated method stub

	}

	/* (非 Javadoc) 
	 * <p>Title: operation</p> 
	 * <p>Description: </p> 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4 
	 * @see com.uws.core.excel.rule.IRule#operation(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map, java.util.Map, int) 
	 */
	@Override
	public void operation(ExcelData data, ExcelColumn column, Map initData,
			Map<String, ExcelData> eds, int site) {
		//  Auto-generated method stub
		if("schoolYear".equals(column.getName())) {
			String year = this.getString(site, eds, "C");
			for(Dic dic : this.schoolYearList) {
				if(year.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
		if("burseName".equals(column.getName())) {
			String name = this.getString(site, eds, "D");
			for(Dic dic : this.burseNameList) {
				if(name.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
	
		if("helpGrade".equals(column.getName())) {
			String grade = this.getString(site, eds, "D");
			for(Dic dic : this.helpList) {
				if(grade.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
		
		if("stuNum".equals(column.getName())) {
			BigDecimal bd = new BigDecimal(this.getString(site, eds, "B"));
			data.setValue(bd.toPlainString());
		}
	}

	/* (非 Javadoc) 
	 * <p>Title: validate</p> 
	 * <p>Description: </p> 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws ExcelException 
	 * @see com.uws.core.excel.rule.IRule#validate(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map) 
	 */
	@Override
	public void validate(ExcelData data, ExcelColumn column, Map map)
			throws ExcelException {
		IStudentCommonService studentCommonService = (IStudentCommonService) 
				SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		boolean flag = false;
		boolean insert = false;
		String errorText = "";
		String value = this.subStr(data.getValue().toString());
		if("stuNum".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			StudentInfoModel stu = studentCommonService.queryStudentById(bd.toPlainString());
			if(DataUtil.isNotNull(stu)) {
				flag = true;
			}else{
				flag = false;
			}
		}
		if("burseNameStr".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
/*			for(Dic dic : this.burseNameList) {
				if(value.equals(dic.getName())) {
//					data.setValue(dic);
					flag = true;
					break;
				}
			}*/
			if(value.equals(inspireBurseDic.getName())) {
//				data.setValue(dic);
				flag = true;
			}else{
				errorText = "模板错误，请选择正确的导入模板。";
			}
		}
		if("schoolYearStr".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
			for(Dic dic : this.schoolYearList) {
				if(value.equals(dic.getName())) {
//					data.setValue(dic);
					flag = true;
					break;
				}
			}
		}
		if((insert) && (!flag)){
			if(DataUtil.isNotNull(errorText)) {
				throw new ExcelException(errorText);
			}else{
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")在数据库中不存在，请严格按照选项进行选择；<br/>");
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

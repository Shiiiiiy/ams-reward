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

public class PunishRule implements IRule {

	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	List<Dic> punishCodeList = this.dicUtil.getDicInfoList("PUNISH_CODE");     //处分名称码
	List<Dic> punishStatusList = this.dicUtil.getDicInfoList("PUNISH_STATUS"); //处分状态		
	List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
	List<Dic> schoolTermList = this.dicUtil.getDicInfoList("TERM");
	
	@Override
	public void validate(ExcelData data,
			ExcelColumn cloumn, Map map) throws ExcelException {
		// TODO Auto-generated method stub
		IStudentCommonService studentCommonService = (IStudentCommonService) 
				SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		boolean flag = false;
		boolean insert = false;
		String value = this.subStr(data.getValue().toString());
		if("stuIdStr".equalsIgnoreCase(cloumn.getTable_column())) {
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			StudentInfoModel stu = studentCommonService.queryStudentById(bd.toPlainString());
			if(DataUtil.isNotNull(stu)) {
				flag = true;
			}else{
				flag = false;
			}
		}
		if("punishStr".equalsIgnoreCase(cloumn.getTable_column())) {
			insert = true;
			for(Dic dic : this.punishCodeList) {
				if(dic.getName().equals(value)) {
					flag = true;
					break;
				}
			}
		}
		if("punishYearStr".equalsIgnoreCase(cloumn.getTable_column())) {
			insert = true;
			for(Dic dic : this.schoolYearList) {
				if(dic.getName().equals(value)) {
					flag = true;
					break;
				}
			}
		}
		if("punishTermStr".equalsIgnoreCase(cloumn.getTable_column())) {
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

	@Override
	public void operation(ExcelData data,
			ExcelColumn cloumn, Map initData,
			Map<String, ExcelData> eds, int site) {
		if("stuIdStr".equals(cloumn.getName())) {
			String stuId = this.subStr(this.getString(site, eds, "B"));
			BigDecimal bd = new BigDecimal(stuId);
//			StudentInfoModel stu = studentCommonService.queryStudentById(stuId);
			data.setValue(bd.toPlainString());
		}
		if("punish".equals(cloumn.getName())) {
			String punish = this.getString(site, eds, "D");
			for(Dic dic : this.punishCodeList) {
				if(punish.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
		if("punishYear".equals(cloumn.getName())) {
			String year = this.getString(site, eds, "G");
			for(Dic dic : this.schoolYearList) {
				if(year.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
		if("punishTerm".equals(cloumn.getName())) {
			String term = this.getString(site, eds, "H");
			for(Dic dic : this.schoolTermList) {
				if(term.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
			}
		}
	
	}
	
	@Override
	public void format(ExcelData data, ExcelColumn column,
			Map map){
		// TODO Auto-generated method stub
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

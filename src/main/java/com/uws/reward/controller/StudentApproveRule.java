package com.uws.reward.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardType;
import com.uws.reward.service.ISetAwardService;
import com.uws.sys.model.Dic;

/** 
* @ClassName: StudentApproveRule 
* @Description:  学生审批导入验证
* @author zhangyb 
* @date 2015年10月28日 上午9:50:59  
*/
public class StudentApproveRule implements IRule {
	
	
	@Override
	public void format(ExcelData data, ExcelColumn column, Map map) {
	}

	@Override
	public void operation(ExcelData data, ExcelColumn column, Map initData,
			Map<String, ExcelData> eds, int site) {
		
		if("misMatchThing".equals(column.getName())) {
			String misMatchThing = this.getString(site, eds, "H");
			misMatchThing = subStr(misMatchThing);
			data.setValue(misMatchThing);
		}
		if("specialReason".equals(column.getName())) {
			String specialReason = this.getString(site, eds, "I");
			specialReason = subStr(specialReason);
			data.setValue(specialReason);
		}
		if("personalPerformance".equals(column.getName())) {
			String personalPerformance = this.getString(site, eds, "J");
			personalPerformance = subStr(personalPerformance);
			data.setValue(personalPerformance);
		}
		if("applyReason".equals(column.getName())) {
			String applyReason = this.getString(site, eds, "K");
			applyReason = subStr(applyReason);
			data.setValue(applyReason);
		}
		if("mainEvent".equals(column.getName())) {
			String mainEvent = this.getString(site, eds, "L");
			mainEvent = subStr(mainEvent);
			data.setValue(mainEvent);
		}
		if("studentIdStr".equalsIgnoreCase(column.getTable_column())) {
			BigDecimal bd = new BigDecimal( this.getString(site, eds, "C"));
			data.setValue(bd.toPlainString());
		}
		if("awardTypeCode".equalsIgnoreCase(column.getTable_column())) {
			BigDecimal bd = new BigDecimal( this.getString(site, eds, "G"));
			data.setValue(bd.toPlainString());
		}
		

		
	}

	@Override
	public void validate(ExcelData data, ExcelColumn column, Map map)
			throws ExcelException {
		
		IStudentCommonService studentCommonService = (IStudentCommonService) 
				SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		ISetAwardService setAwardService = (ISetAwardService) SpringBeanLocator.getBean("setAwardService");
		Dic year = SchoolYearUtil.getYearDic();
//		List<Dic> secondNameDic = this.dicUtil.getDicInfoList("SECOND_AWARD_NAME");
		boolean flag = false;
		boolean insert = false;
		String text = "";
		String value = this.subStr(data.getValue().toString());
		if("studentIdStr".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			String studentIdStr = bd.toPlainString();
			StudentInfoModel stu = studentCommonService.queryStudentById(studentIdStr);
			if(DataUtil.isNotNull(stu)) {
				flag = true;
			}else{
				flag = false;
			}
		}
		if("awardTypeCode".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			String awardTypeCode = bd.toPlainString();
			List<AwardType> awardList = setAwardService.getPublishedAward(year,null);
			for(AwardType award : awardList) {
				if(award.getAwardTypeCode().equals(awardTypeCode)) {
					if(award.getAwardInfoId().getAvailableObject().getCode().equals("STUDENT")) {
						flag = true;
					}else{
						text = ")该奖项为班级奖项，请确认后重新填写；<br/>";
					}
				}
			}
		}
		if((insert) && (!flag)){
			String isText = data.getId().replaceAll("\\$", "");
			if(DataUtil.isNotNull(text)) {
				throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + text);
			}else{
				throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")在数据库中不存在，请严格按照系统数据填写；<br/>");
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

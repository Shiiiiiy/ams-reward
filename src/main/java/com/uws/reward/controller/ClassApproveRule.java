/**   
* @Title: CLassApproveRule.java 
* @Package com.uws.reward.controller 
* @Description: (用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月28日 上午9:47:55 
* @version V1.0   
*/
package com.uws.reward.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.uws.common.service.IBaseDataService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.reward.AwardType;
import com.uws.reward.service.ISetAwardService;
import com.uws.sys.model.Dic;

/** 
 * @ClassName: CLassApproveRule 
 * @Description:  班级审批导入验证
 * @author zhangyb 
 * @date 2015年10月28日 上午9:47:55  
 */
public class ClassApproveRule implements IRule {

	@Override
	public void format(ExcelData arg0, ExcelColumn arg1, Map arg2) {
		//  Auto-generated method stub

	}

	@Override
	public void operation(ExcelData data, ExcelColumn column, Map initData,
			Map<String, ExcelData> eds, int site) {
		//  Auto-generated method stub
		if("classIdStr".equals(column.getName())) {
			String classIdStr = this.getString(site, eds, "D");
			classIdStr = subStr(classIdStr);
			BigDecimal bd = new BigDecimal(classIdStr);
			data.setValue(bd.toPlainString());
		}
		if("classAwardInfo".equals(column.getName())) {
			String classAwardInfo = this.getString(site, eds, "F");
			classAwardInfo = subStr(classAwardInfo);
			data.setValue(classAwardInfo);
		}
		if("mainResult".equals(column.getName())) {
			String mainResult = this.getString(site, eds, "G");
			mainResult = subStr(mainResult);
			data.setValue(mainResult);
		}
		if("awardTypeCode".equalsIgnoreCase(column.getTable_column())) {
			BigDecimal bd = new BigDecimal(this.getString(site, eds, "E"));
			data.setValue(bd.toPlainString());
		}
	}

	@Override
	public void validate(ExcelData data, ExcelColumn column, Map map)
			throws ExcelException {
		
		ISetAwardService setAwardService = (ISetAwardService) SpringBeanLocator.getBean("setAwardService");
		IBaseDataService baseDataService = (IBaseDataService) SpringBeanLocator.getBean("com.uws.common.service.impl.BaseDataServiceImpl");
		Dic year = SchoolYearUtil.getYearDic();
//		List<Dic> secondNameDic = this.dicUtil.getDicInfoList("SECOND_AWARD_NAME");
		boolean flag = false;
		boolean insert = false;
		String text = "";
		String value = this.subStr(data.getValue().toString());
		if("awardTypeCode".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
			List<AwardType> awardList = setAwardService.getPublishedAward(year,null);
			BigDecimal bd = new BigDecimal(value);
			String awardTypeCode = bd.toPlainString();
			for(AwardType award : awardList) {
				if(award.getAwardTypeCode().equals(awardTypeCode)) {
					if(award.getAwardInfoId().getAvailableObject().getCode().equals("CLASS")) {
						flag = true;
					}else{
						text = ")该奖项适用对象不是班级，请确认后重新填写；<br/>";
					}
				}
			}
		}
		if("classIdStr".equalsIgnoreCase(column.getTable_column())) {
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			String classIdStr = bd.toPlainString();
			BaseClassModel cla = baseDataService.findClassById(classIdStr);
			if(DataUtil.isNotNull(cla)) {
				flag = true;
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

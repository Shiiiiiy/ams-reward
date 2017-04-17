package com.uws.reward.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardCondition;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.ClassApplyInfo;
import com.uws.domain.reward.ConditionInfo;
import com.uws.domain.reward.QuotaInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.reward.service.IClassApplyService;
import com.uws.reward.service.ISetAwardService;
import com.uws.reward.service.IStudentApplyService;
import com.uws.reward.util.RewardConstant;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

/**
 * @author zhangyb
 * @version:2015年8月11日 上午11:33:50
 * @Description:评优设置Controller
 */
@Controller
public class SetAwardController {

	
	@Autowired
	private ISetAwardService setAwardService;
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private Logger log = new LoggerFactory(SetAwardController.class);
	@Autowired
	private IBaseDataService baseDataService;
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private IStudentApplyService studentApplyService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IStuJobTeamSetCommonService stuJobTeamSetCommonService;
	@Autowired
	private IClassApplyService classApplyService;

	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  评优/评奖设置page查询方法
	 */
	@RequestMapping({"/reward/settingaward/opt-query/queryAwardPage.do","/reward/setexcellent/opt-query/queryAwardPage.do"})
	public String queryAwardPage(ModelMap model,HttpServletRequest request,AwardType award) {
		
//		判断是评奖还是评优
		String source = request.getParameter("type");
		Dic awardDic = this.dicUtil.getDicInfo("AWARD_TYPE", "AWARD");
		Dic excellentDic = this.dicUtil.getDicInfo("AWARD_TYPE", "EXCELLENT");
		Dic objectDic = this.dicUtil.getDicInfo("AWARDINFO_OBJECT", "STUDENT");
		List<Dic> awardStatusList = this.dicUtil.getDicInfoList("AWARD_DATA_STATUS");
		String returnSource = "";
		if(source.equals("AWARD")) {
			award.setAwardType(awardDic);            //评奖
			if(award.getAwardInfoId() == null) {
				award.setAwardInfoId(new AwardInfo());
			}
			award.getAwardInfoId().setAvailableObject(objectDic);
			returnSource = "reward/settingAward/setAwardList";
		} else if(source.equals("EXCELLENT")) {
			award.setAwardType(excellentDic);        //评优
			returnSource = "reward/settingAward/setExcellentList";
		}
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
//		List<Dic> awardTypeList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		Page page = this.setAwardService.queryAwardPage(award, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("award", award);
		model.addAttribute("awardStatusList", awardStatusList);
		model.addAttribute("schoolYearList", schoolYearList);
		return returnSource;
	}
	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  新增评奖/评优类型
	 */
	@RequestMapping({"/reward/settingaward/opt-query/insertAward.do"})
	public String insertAward(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String type = request.getParameter("type");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> secondAwardList = this.dicUtil.getDicInfoList("SECOND_AWARD_NAME");
		List<AwardInfo> excellentList = this.setAwardService.getAwardInfoListByType("EXCELLENT");
		List<AwardInfo> awardList = this.setAwardService.getAwardInfoListByType("AWARD");
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		Dic currentYear = SchoolYearUtil.getYearDic();
		award.setSchoolYear(currentYear);
		String returnSource = "";
		if(type.equals("AWARD")) {
			AwardInfo awardInfo = this.setAwardService.getAwardInfoByCode(RewardConstant.XINGZHI);
			if(null !=awardInfo && StringUtils.hasText(awardInfo.getId()))
			{
				model.addAttribute("xingzhiName", awardInfo.getAwardName());
			}
			model.addAttribute("awardInfoList", awardList);
			model.addAttribute("secondAwardList", secondAwardList);
			returnSource = "reward/settingAward/addAward";
		}else{
			model.addAttribute("awardInfoList", excellentList);
			returnSource = "reward/settingAward/addExcellent";
		}
		model.addAttribute("award", award);
		model.addAttribute("quotaInfoList", academyList);
		model.addAttribute("schoolYearList", schoolYearList);
		return returnSource;
	}
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 * @throws ParseException
	 *  保存评优设置
	 */
	@RequestMapping({"/reward/settingaward/opt-query/saveAward.do"})
	public String saveAward(ModelMap model,HttpServletRequest request,AwardType award,String[] fileId) throws ParseException {
		
		User user = new User();
		String userId = this.sessionUtil.getCurrentUserId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		AwardInfo awardInfo = this.setAwardService.getAwardInfoById(award.getAwardInfoId().getId());
		user.setId(userId);
		log.debug("字符串转换日期");
		Date beginDate = sdf.parse(award.getBeginDateStr());
		Date endDate = sdf.parse(award.getEndDateStr());
//		判断点击保存还是发布
		String buttonType = request.getParameter("buttonType");
//		设置数据状态为已保存
		Dic statusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "SAVED");
		Dic pubStatusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "PUBLISHED");
		Dic yearDic = this.dicUtil.getDicInfo("YEAR", award.getSchoolYear().getCode());
		if(DataUtil.isNotNull(award.getSecondAwardName())) {
			Dic secondAwardDic = this.dicUtil.getDicInfo("SECOND_AWARD_NAME", award.getSecondAwardName().getCode());
			award.setSecondAwardName(secondAwardDic);
		}
		award.setCreator(user);
		award.setSchoolYear(yearDic);
		award.setAwardType(awardInfo.getAwardType());
		award.setAwardInfoId(awardInfo);
		award.setBeginDate(beginDate);
		award.setEndDate(endDate);
		if(DataUtil.isNotNull(buttonType)) {
			award.setAwardStatus(pubStatusDic);
		} else {
			award.setAwardStatus(statusDic);
		}
//		保存评奖评优设置编码
		String awardTypeCode = this.setAwardService.getMaxAwardTypeCode(yearDic);
		award.setAwardTypeCode(awardTypeCode);
		log.debug("保存评优类型数据");
		this.setAwardService.saveAward(award);
//		保存评优条件信息
		AwardCondition condition = new AwardCondition();
//		附件ID
		String comments = request.getParameter("conComments");
		condition.setAwardId(award);
		condition.setComments(comments);
		log.debug("保存条件信息数据");
		this.setAwardService.saveCondition(condition,fileId);
//		保存评优条件明细信息
//		选中的name KEY
		String submitIds = request.getParameter("submitIds");
		String[] conditionNames = null;
		if(DataUtil.isNotNull(submitIds)) {
			conditionNames = submitIds.split(",");
		}
		String conditionValue = "";
		String nameText = "";                 //文本名称
		String compareStr = "";               //大小比较
		String checkStr = "";                 //是否可比较
		log.debug("循环保存条件明细信息数据");
		if(DataUtil.isNotNull(conditionNames)) {
			for(String s : conditionNames) {
				ConditionInfo conditionInfo = new ConditionInfo();
				conditionInfo.setConditionId(condition);
				conditionValue = request.getParameter(s);
				nameText = request.getParameter(s+"TEXT");
				if(DataUtil.isNotNull(nameText)) {
					nameText = nameText.trim();
				}
				compareStr = request.getParameter(s+"COMPARE");
				if(DataUtil.isNotNull(compareStr)) {
					compareStr = compareStr.trim();
				}
				checkStr = request.getParameter(s+"CHECK");
				if(DataUtil.isNotNull(checkStr)) {
					checkStr = checkStr.trim();
				}
				log.debug(compareStr);
				conditionInfo.setTextName(nameText);
				conditionInfo.setCompareMethod(compareStr);
				conditionInfo.setCheckOrNot(checkStr);
				conditionInfo.setConditionName(s);
				conditionInfo.setConditionValue(conditionValue);
				this.setAwardService.saveConditionInfo(conditionInfo);
			}
		}
//		保存人员限额明细数据
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		String academyId = "";
		String academyNum = "";
		log.debug("循环保存人员限额明细信息数据");
		for(BaseAcademyModel academy : academyList) {
			QuotaInfo quotaInfo = new QuotaInfo();
			academyId = academy.getId();
			academyNum = request.getParameter(academyId+"academy");
			quotaInfo.setConditionId(condition);
			quotaInfo.setAcademyId(academy);
			quotaInfo.setNum(academyNum);
			this.setAwardService.saveQuotaInfo(quotaInfo);
		}
		return "redirect:/reward/settingaward/opt-query/queryAwardPage.do?type="+awardInfo.getAwardType().getCode();
	}
	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  修改评奖评优信息
	 */
	@RequestMapping({"/reward/settingaward/opt-query/updateAward.do"})
	public String updateAward(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String awardId = request.getParameter("id");
		award = this.setAwardService.getAwardById(awardId);
		AwardCondition condition = this.setAwardService.getConByAwardId(awardId);
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
//		转成map
		Map<String,ConditionInfo> conInfoMap = new HashMap<String,ConditionInfo>();
		for(ConditionInfo conInfo : conInfoList) {
			conInfoMap.put(conInfo.getConditionName(), conInfo);
		}
		condition.setConInfoList(conInfoList);
		condition.setQuotaInfoList(quotaInfoList);
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
//		判断评奖还是评优
		String returnSource = "";
		if(award.getAwardType().getCode().equals("AWARD")) {
			List<AwardInfo> awardInfoList = this.setAwardService.getAwardInfoListByType("AWARD");
			List<Dic> secondAwardList = this.dicUtil.getDicInfoList("SECOND_AWARD_NAME");
			AwardInfo awardInfo = this.setAwardService.getAwardInfoByCode(RewardConstant.XINGZHI);
			model.addAttribute("xingzhiName", awardInfo.getAwardName());
			model.addAttribute("awardInfoList", awardInfoList);
			model.addAttribute("secondAwardList", secondAwardList);
			returnSource = "reward/settingAward/updateAward";
		}else{
			List<AwardInfo> excellentInfoList = this.setAwardService.getAwardInfoListByType("EXCELLENT");
			model.addAttribute("awardInfoList", excellentInfoList);
			returnSource = "reward/settingAward/updateExcellent";
		}
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
		model.addAttribute("award", award);
		model.addAttribute("condition", condition);
		model.addAttribute("conComments", condition.getComments());
		model.addAttribute("conInfoMap", conInfoMap);
		model.addAttribute("quotaInfoList", quotaInfoList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("schoolYearList", schoolYearList);
		return returnSource;
	}
	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 * @throws ParseException
	 *  保存修改的评优信息/编辑页发布评优设置
	 */
	@RequestMapping({"/reward/settingaward/opt-query/saveUpdatedAward.do"})
	public String saveUpdatedAward(ModelMap model,HttpServletRequest request,AwardType award,String[] fileId) throws ParseException {
		
		String awardId = request.getParameter("id");
		AwardType newAward = this.setAwardService.getAwardById(awardId);
		award.setId(awardId);
		BeanUtils.copyProperties(award, newAward, new String[]{"createTime","creator","awardTypeCode"});
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		AwardInfo awardInfo = this.setAwardService.getAwardInfoById(newAward.getAwardInfoId().getId());
		Date beginDate = sdf.parse(newAward.getBeginDateStr());
		Date endDate = sdf.parse(newAward.getEndDateStr());
//		判断点击保存还是发布
		String buttonType = request.getParameter("buttonType");
//		设置数据状态为已保存
		Dic statusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "SAVED");
		Dic pubStatusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "PUBLISHED");
		Dic yearDic = this.dicUtil.getDicInfo("YEAR", newAward.getSchoolYear().getCode());
		newAward.setSchoolYear(yearDic);
		newAward.setAwardInfoId(awardInfo);
		newAward.setAwardType(awardInfo.getAwardType());
		newAward.setBeginDate(beginDate);
		newAward.setEndDate(endDate);
		if(DataUtil.isNotNull(award.getSecondAwardName())) {
			Dic secondAwardDic = this.dicUtil.getDicInfo("SECOND_AWARD_NAME", award.getSecondAwardName().getCode());
			newAward.setSecondAwardName(secondAwardDic);
		}
		if(DataUtil.isNotNull(buttonType)) {
			newAward.setAwardStatus(pubStatusDic);
		} else {
			newAward.setAwardStatus(statusDic);
		}
		log.debug("更新评优类型数据");
		this.setAwardService.updateAward(newAward);
//		保存评优条件信息
		AwardCondition condition = this.setAwardService.getConByAwardId(newAward.getId());
		String comments = request.getParameter("conComments");
		condition.setComments(comments);
		log.debug("保存条件信息数据");
		this.setAwardService.updateCondition(condition,fileId);
//		保存评优条件明细信息
//		选中的name KEY
		String submitIds = request.getParameter("submitIds");
		String[] conditionNames = null;
		if(DataUtil.isNotNull(submitIds)) {
			conditionNames = submitIds.split(",");
		}
		String conditionValue = "";
		String nameText = "";                 //文本名称
		String compareStr = "";               //大小比较
		String checkStr = "";                 //是否可比较
//		获取已有的评优条件list
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		log.debug("循环删除原有的条件明细数据");
		this.setAwardService.deleConInfoList(conInfoList);
		log.debug("循环保存条件明细信息数据");
		if(DataUtil.isNotNull(conditionNames)) {
			for(String s : conditionNames) {
				conditionValue = request.getParameter(s);
				nameText = request.getParameter(s+"TEXT");
				if(DataUtil.isNotNull(nameText)) {
					nameText = nameText.trim();
				}
				compareStr = request.getParameter(s+"COMPARE");
				if(DataUtil.isNotNull(compareStr)) {
					compareStr = compareStr.trim();
				}
				checkStr = request.getParameter(s+"CHECK");
				if(DataUtil.isNotNull(checkStr)) {
					checkStr = checkStr.trim();
				}
				ConditionInfo conditionInfo = new ConditionInfo();
				conditionInfo.setTextName(nameText);
				conditionInfo.setCompareMethod(compareStr);
				conditionInfo.setCheckOrNot(checkStr);
				conditionInfo.setConditionId(condition);
				conditionInfo.setConditionName(s);
				conditionInfo.setConditionValue(conditionValue);
				this.setAwardService.saveConditionInfo(conditionInfo);
			}
		}
		
//		更新人员限额明细数据
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		String academyId = "";
		String academyNum = "";
		log.debug("循环保存人员限额明细信息数据");
		for(QuotaInfo quota : quotaInfoList) {
			academyId = quota.getAcademyId().getId();
			academyNum = request.getParameter(academyId+"academy");
			quota.setNum(academyNum);
			this.setAwardService.updateQuotaInfo(quota);
		}
		return "redirect:/reward/settingaward/opt-query/queryAwardPage.do?type=" + awardInfo.getAwardType().getCode();
	}
	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  删除评奖评优信息
	 */
	@RequestMapping({"/reward/settingaward/opt-query/delAward.do"})
	public String delAward(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String awardId = request.getParameter("id");
		award = this.setAwardService.getAwardById(awardId);
		AwardCondition condition = this.setAwardService.getConByAwardId(awardId);
		List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
		List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
		this.setAwardService.deleteAward(award, condition, conInfoList, quotaInfoList);
		return "redirect:/reward/settingaward/opt-query/queryAwardPage.do?type=" + award.getAwardType().getCode();
	}
	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  列表页发布评优评奖信息
	 */
	@RequestMapping({"/reward/settingaward/opt-query/publishAward.do"})
	public String publishAward(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String awardId = request.getParameter("id");
		award = this.setAwardService.getAwardById(awardId);
		Dic pubStatusDic = this.dicUtil.getDicInfo("AWARD_DATA_STATUS", "PUBLISHED");
		award.setAwardStatus(pubStatusDic);
		this.setAwardService.updateAward(award);
		return "redirect:/reward/settingaward/opt-query/queryAwardPage.do?type=" + award.getAwardType().getCode();
	}
	
	/**
	 * @param model
	 * @param request
	 * @param award
	 * @return
	 *  查看评奖评优信息
	 */
	@RequestMapping({"/reward/settingaward/opt-query/viewAward.do"})
	public String viewAward(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String awardId = request.getParameter("id");
		award = this.setAwardService.getAwardById(awardId);
		AwardCondition condition = this.setAwardService.getConByAwardId(awardId);
		if(DataUtil.isNotNull(condition)) {
			List<ConditionInfo> conInfoList = this.setAwardService.getConInfoListByConId(condition.getId());
			List<QuotaInfo> quotaInfoList = this.setAwardService.getQuotaInfoListByConId(condition.getId());
//		转成map
			Map<String,ConditionInfo> conInfoMap = new HashMap<String,ConditionInfo>();
			for(ConditionInfo conInfo : conInfoList) {
				conInfoMap.put(conInfo.getConditionName(), conInfo);
			}
			condition.setConInfoList(conInfoList);
			condition.setQuotaInfoList(quotaInfoList);
			model.addAttribute("conInfoMap", conInfoMap);
			model.addAttribute("quotaInfoList", quotaInfoList);
		}
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
//		判断评奖还是评优
		String returnSource = "";
		if(award.getAwardType().getCode().equals("AWARD")) {
//			List<AwardInfo> awardInfoList = this.setAwardService.getAwardInfoListByType("AWARD");
//			model.addAttribute("awardInfoList", awardInfoList);
			model.addAttribute("award", award);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
			returnSource = "reward/settingAward/viewAward";
		}else{
//			List<AwardInfo> excellentInfoList = this.setAwardService.getAwardInfoListByType("EXCELLENT");
//			model.addAttribute("awardInfoList", excellentInfoList);
			model.addAttribute("award", award);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(condition.getId()));
			returnSource = "reward/settingAward/viewExcellent";
		}
		model.addAttribute("award", award);
		model.addAttribute("condition", condition);
		model.addAttribute("conComments", condition.getComments());
		model.addAttribute("schoolYearList", schoolYearList);
		return returnSource;
	}
	/**
	 * @param model
	 * @param request
	 * @param awardInfo
	 * @return
	 *  评奖评优信息列表页
	 */
	@RequestMapping({"/reward/awardinfo/opt-query/queryAwardInfoPage.do"})
	public String queryAwardInfoPage(ModelMap model,HttpServletRequest request,AwardInfo awardInfo) {
		
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> statusDicList = this.dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
		List<Dic> awardDicList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		Page page = this.setAwardService.queryAwardInfoPage(awardInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
//		判断基础奖项信息是否可删除
		Map<String,String> map = new HashMap<String,String>();
		@SuppressWarnings("unchecked")
		List<AwardInfo> awardInfoList = (List<AwardInfo>) page.getResult();
		if(awardInfoList.size() > 0) {
			for(AwardInfo info : awardInfoList) {
				if(this.setAwardService.getAwardTypeListByInfoId(info.getId()).size() > 0 || 
						(RewardConstant.XINGZHI + RewardConstant.SANHAO + RewardConstant.STULEADER + 
								RewardConstant.GOODCLASS).indexOf(info.getAwardCode()+"") > -1) {
					map.put(info.getId(), "false");    //不可删除
				}else{
					map.put(info.getId(), "true");     //可删除
				}
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("map", map);
		model.addAttribute("awardInfo", awardInfo);
		model.addAttribute("statusDicList", statusDicList);
		model.addAttribute("awardDicList", awardDicList);
		return "reward/awardInfo/awardInfoList";
	}
	
	/**
	 * @param model
	 * @param request
	 * @param awardInfo
	 * @return
	 *  新增/修改评奖评优信息
	 */
	@RequestMapping({"/reward/awardinfo/opt-query/insertAwardInfo.do","/reward/awardinfo/opt-query/updateAwardInfo.do"})
	public String editAwardInfo(ModelMap model,HttpServletRequest request,AwardInfo awardInfo) {
		
		List<Dic> statusDicList = this.dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
		List<Dic> awardDicList = this.dicUtil.getDicInfoList("AWARD_TYPE");
		List<Dic> infoObject = this.dicUtil.getDicInfoList("AWARDINFO_OBJECT");    //适用对象  班级or学生
		String id = request.getParameter("id");
		if(DataUtil.isNotNull(id)) {       //修改
			AwardInfo awardInfoOld = this.setAwardService.getAwardInfoById(id);
			model.addAttribute("awardInfo", awardInfoOld);
		}else{                             //新增
			model.addAttribute("awardInfo", awardInfo);
		}
		model.addAttribute("infoObject", infoObject);
		model.addAttribute("statusDicList", statusDicList);
		model.addAttribute("awardDicList", awardDicList);
		return "reward/awardInfo/editAwardInfo";
	}
	
	/**
	 * @param model
	 * @param request
	 * @param awardInfo
	 * @return
	 *  保存新增的评奖评优/保存修改的评奖评优
	 */
	@RequestMapping({"/reward/awardinfo/opt-query/saveAwardInfo.do"})
	public String saveAwardInfo(ModelMap model,HttpServletRequest request,AwardInfo awardInfo) {
		
		String id = request.getParameter("id");
		Dic statusDic = this.dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", awardInfo.getAwardStatus().getCode());
		Dic typeDic = this.dicUtil.getDicInfo("AWARD_TYPE", awardInfo.getAwardType().getCode());
		Dic infoObject = this.dicUtil.getDicInfo("AWARDINFO_OBJECT", awardInfo.getAvailableObject().getCode());
		if(DataUtil.isNotNull(id)) {            //保存修改
			AwardInfo awardInfoOld = this.setAwardService.getAwardInfoById(id);
			awardInfoOld.setAwardName(awardInfo.getAwardName());
			awardInfoOld.setAwardStatus(statusDic);
			awardInfoOld.setAwardType(typeDic);
			awardInfoOld.setAvailableObject(infoObject);
			this.setAwardService.updateAwardInfo(awardInfoOld);
		} else {                                //保存新增
			User user = new User();
			String userId = this.sessionUtil.getCurrentUserId();
			user.setId(userId);
			int maxCode = this.setAwardService.getMaxAwardCode();
			awardInfo.setCreator(user);
			awardInfo.setAwardStatus(statusDic);
			awardInfo.setAwardType(typeDic);
			awardInfo.setAvailableObject(infoObject);
			awardInfo.setAwardCode(maxCode+1);
			this.setAwardService.saveAwardInfo(awardInfo);
		}
		return "redirect:/reward/awardinfo/opt-query/queryAwardInfoPage.do";
	}
	/**
	 * @param model
	 * @param request
	 * @param awardInfo
	 * @return
	 *  删除评奖评优信息
	 */
	@RequestMapping({"/reward/awardinfo/opt-query/delAwardInfo.do"})
	public String delAwardInfo(ModelMap model,HttpServletRequest request,AwardInfo awardInfo) {
		
		String id = request.getParameter("id");
		this.setAwardService.deleteAwardInfo(this.setAwardService.getAwardInfoById(id));
		return "redirect:/reward/awardinfo/opt-query/queryAwardInfoPage.do";
	}
	
	/** 
	* @Title: setAward 
	* @Description:  不满足条件评奖评优人员设置
	* @param model
	* @param request
	* @param award
	* @param      
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/settingaward/opt-query/setAward.do"})
	public String setAward(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String returnSource = "";
		String awardId = request.getParameter("id");
		award = this.setAwardService.getAwardById(awardId);
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "SYSTEM_ADD");
		if(award.getAwardInfoId().getAvailableObject().getCode().equals("STUDENT")) {
			StudentApplyInfo stu = new StudentApplyInfo();
			String stuIds = "";
			stu.setAwardTypeId(award);
			stu.setApplySource(applySource);
			List<StudentApplyInfo> stuList = this.studentApplyService.getStuApplyList(stu);
			if(stuList.size() > 0) {
				for(StudentApplyInfo s : stuList) {
					stuIds = stuIds + s.getStudentId().getId() + ",";
				}
			}
			model.addAttribute("award", award);
			model.addAttribute("stuIds", stuIds);
			model.addAttribute("stuList", stuList);
			returnSource = "reward/settingAward/setStudentSpecial";
		}else{
			ClassApplyInfo cla = new ClassApplyInfo();
			String classIds = "";
			cla.setAwardTypeId(award);
			cla.setApplySource(applySource);
			List<ClassApplyInfo> classList = this.classApplyService.getClassInfoList(cla);
			if(classList.size() > 0) {
				for(ClassApplyInfo c : classList) {
					classIds = classIds + c.getClassId().getId() + ",";
				}
			}
			model.addAttribute("award", award);
			model.addAttribute("classIds", classIds);
			model.addAttribute("classList", classList);
			returnSource = "reward/settingAward/setClassSpecial";
		}
//		获取通过添加获得权限的学生list
		return returnSource;
	}
	
	/** 
	* @Title: checkAwardInfoName 
	* @Description:  验证评奖评优类型
	* @param  @param info
	* @param  @param id
	* @param  @param name
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/awardinfo/opt-query/checkAwardInfoName.do"})
	@ResponseBody
	public String checkAwardInfoName(AwardInfo info,@RequestParam String id,@RequestParam String name) {
		String result = "true";
		List<AwardInfo> infoList = this.setAwardService.getAwardInfoListByName(name);
		if(DataUtil.isNotNull(id)) {
			info = this.setAwardService.getAwardInfoById(id);
			if(DataUtil.isNotNull(info)) {
				if(infoList.size() >= 1 && !name.equals(info.getAwardName())) {
					result = "false";
				}
			}
		}else{
			if(infoList.size() >= 1) {
				result = "false";
			}
		}
		return result;
	}
	
	/** 
	* @Title: saveStuSpecial 
	* @Description:  保存选中学生的申请可申请权限信息
	* @param  @param model
	* @param  @param request
	* @param  @param award
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/settingaward/opt-query/saveStuSpecial.do"})
	public String saveStuSpecial(ModelMap model,HttpServletRequest request,AwardType award) {
		
		String type = request.getParameter("type");
		String awardId = request.getParameter("awardTypeId");
		String returnSource = "";
		Dic meetCondition = this.dicUtil.getDicInfo("Y&N", "Y");
		Dic applySource = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_SOURCE", "SYSTEM_ADD");
		Dic applyStatus = this.dicUtil.getDicInfo("AWARD_EXCELLENT_APPLY_STATUS", "UNAPPLY");
		returnSource = "redirect:/reward/settingaward/opt-query/queryAwardPage.do?type=" + type;
		award = this.setAwardService.getAwardById(awardId);
		if(award.getAwardInfoId().getAwardCode() != Long.valueOf(RewardConstant.GOODCLASS)) {
//			选中的学生ID
			String stuIds = request.getParameter("selStuIds");
			String[] stuIdArr = null;
			if(DataUtil.isNotNull(stuIds) && stuIds.indexOf(",") > -1) {
				stuIdArr = stuIds.split(",");
			}
			StudentApplyInfo applyInfo = null;
			if(stuIdArr != null) {
				for(String s : stuIdArr) {
					StudentInfoModel stu = this.studentCommonService.queryStudentById(s);
					applyInfo = new StudentApplyInfo();
					applyInfo.setStudentId(stu);
					applyInfo.setAwardTypeId(award);
//					判断是否已有此条申请
					List<StudentApplyInfo> stuInfoList = this.studentApplyService.getStuApplyList(applyInfo);
					if(stuInfoList.size() <= 0) {
						applyInfo.setMeetCondition(meetCondition);
						applyInfo.setApplyStatus(applyStatus);
						applyInfo.setApplySource(applySource);
						this.studentApplyService.saveStuApply(applyInfo, null);
					}
				}
			}
		}else{
			String classIds = request.getParameter("selClassIds");
			String[] classIdArr = null;
			if(DataUtil.isNotNull(classIds) && classIds.indexOf(",") > -1) {
				classIdArr = classIds.split(",");
			}
			ClassApplyInfo classInfo = null;
			if(classIdArr != null) {
				for(String c : classIdArr) {
					classInfo = new ClassApplyInfo();
					BaseClassModel cla = this.baseDataService.findClassById(c);
					classInfo.setAwardTypeId(award);
					classInfo.setClassId(cla);
					List<ClassApplyInfo> classInfoList = this.classApplyService.getClassInfoList(classInfo);
					if(classInfoList.size() <= 0) {
						classInfo.setMeetCondition(meetCondition);
						classInfo.setApplyStatus(applyStatus);
						classInfo.setApplySource(applySource);
						this.classApplyService.saveClassApply(classInfo, null);
					}
				}
			}
		}
		return returnSource;
	}
	
	/** 
	* @Title: checkAwardType 
	* @Description:  验证评优类型
	* @param  @param id
	* @param  @param name
	* @param  @param secondName
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/reward/settingaward/opt-query/checkAwardType.do"})
	@ResponseBody
	public String checkAwardType(@RequestParam String id,@RequestParam String name,@RequestParam String secondName,
			@RequestParam String schoolYear) {
		
		String result = "true";
		Dic year = null;
		if(DataUtil.isNotNull(schoolYear)) {
			year = this.dicUtil.getDicInfo("YEAR", schoolYear);
		}
		List<AwardType> awardTypeList = this.setAwardService.getAwardTypeList(year, name, secondName);
		AwardInfo info = this.setAwardService.getAwardInfoById(name);
		if(DataUtil.isNotNull(id)) {   //修改
			AwardType award = this.setAwardService.getAwardById(id);
			if(award.getAwardInfoId().getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
					DataUtil.isNotNull(secondName)) {    //是行知且与二级奖项的时候修改
				if(awardTypeList.size() >= 1 && !award.getSecondAwardName().getCode().equals(secondName)) {
					result = "false";
				}
			}else if(awardTypeList.size() >= 1 && info.getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
					DataUtil.isNull(secondName)){
				result = "true";
			}else{
				if(awardTypeList.size() >= 1 && !award.getAwardInfoId().getId().equals(name)) {
					result = "false";
				}
			}
		}else{
			if(awardTypeList.size() >= 1 && info.getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) 
					&& DataUtil.isNull(secondName)) {
				result = "true";
			}else if(awardTypeList.size() >= 1 && info.getAwardCode() == Long.valueOf(RewardConstant.XINGZHI) && 
					DataUtil.isNotNull(secondName)){
				result = "false";
			}else if(awardTypeList.size() >= 1) {
				result = "false";
			}
		}
		return result;
	}
	
	
	
}

package com.comvee.tnb.config;

import android.text.TextUtils;

import com.comvee.tnb.TNBApplication;
import com.comvee.util.Util;

/**
 * 请求地址
 */
public class ConfigUrlMrg {
	public static String username = "11223344556";
	public static String userpaw = "1234567890";
	private final static String TEST_HOST = "http://192.168.2.3:7666/health/mobile";
	private final static String TEST_HOST_1 = "http://192.168.2.3:6879/health/mobile";
	// 测试线
	private final static String TEST_HOST_5 = "http://192.168.20.6:8080/health/mobile";
	// 预上线
	private final static String TEST_HOST_3 = "http://comvee.3322.org:8888/health/mobile";
	// 预上线2
	private final static String TEST_HOST_4 = "http://192.168.2.3:8888/health/mobile";
	// 线上
	private final static String LINE_HOST = "https://diabetesintf.izhangkong.com/mobile";
	// "http://www.zhangkong.me/mobile";"https://diabetesintf.izhangkong.com/mobile";
	private final static String LIAN_HOST = "http://192.168.20.23:8080/healthNew/mobile";
	private final static String CHEN_HOST = "http://192.168.20.77:8080/health/mobile";
	public static String HOST = "https://diabetesintf.izhangkong.com/mobile";
	static {
		HOST = Util.getMetaData(TNBApplication.getInstance(), "HOST", null);
		if (TextUtils.isEmpty(HOST) || "${HOST}".equals(HOST)) {
			// HOST = LINE_HOST;
			HOST = ConfigParams.getHostAdrr(TNBApplication.getInstance(), LINE_HOST, true);
		}
	}
	public static String URL_HEAD = HOST.substring(0, ConfigUrlMrg.HOST.length() - 6);
	public static String RECORD_DETAIL_LIST = HOST + "/member/getparamCententText";
	public static String MODIFY_MEMBER_SUFFER_PARAM = HOST + "/member/modifyMemberSuggerParam";
	// 糖友课堂
	public static String BOOK_CLASS = HOST + "/info/loadAllKnow";
	public static String RECORD_SUGAR_SET = HOST + "/member/loadRangeSet";
	public static String RECORD_SUGAR_SET_MODIFY = HOST + "/member/rangeSet";
	// 检测血糖值是否偏高
	public static String RECORD_CHECK_SURGAR_VALUE = HOST + "/member/loadRangeInfoByValue";
	// 获取单条快速录入日志详情
	public static String RECORD_GET_SURGAR_VALUE = HOST + "/member/loadMemberParamLogInfo";
	// 修改快速录入日志
	public static String RECORD_MODIFY_SURGAR_VALUE = HOST + "/member/modifyMemberParamLog";
	// 删除血糖记录
	public static String RECORD_REMOVE_SURGAR_VALUE = HOST + "/member/delMemberParamLog";
	// 删除bmi,糖化，血压记录
	public static String RECORD_REMOVE_VALUE = HOST + "/member/deteleMemberParamLog";
	// 我的历程
	public static String MY_HISTORY = HOST + "/memberCenter/loadMemberHistory";
	public static String RECORD_DATA_TABLE = HOST + "/memberCenter/loadDailyRecodeScore";
	public static String RECORD_DATA = HOST + "/memberCenter/loadDailyRecode";
	public static String RECORD_ADD = HOST + "/memberCenter/addDailyRecode";
	public static String BOOK_LIST = HOST + "/info/loadKnowledgeGroup";
	public static String RECORD_MODIFY_SURGAR_VALUE_NEW = HOST + "/member/modifyMemberSuggerCenter";
	public static String HEATH_KNOWLEDGE = HOST + "/info/loadHotHomePage.do";

	//public static String LOAD_HOT_SPOT = HOST + "/info/searchHotSpotList.do";
	public static String LOAD_HOT_SPOT = HOST + "/hotSpot/searchKnowledgeList.do";
	/**
	 * 获取血糖监测的任务
	 */
	public static String NEW_GUIDE_TASK_BLOOD_SUGAR_MONITOR = HOST + "/guide/bloodGlucoseValue";

	/**
	 * 发现模块的轮播图
	 */
	public static String INDEX_DISCOVERY_BANNAR = HOST + "/job/loadNoticeList";

	/**
	 * 运动的任务信息
	 */
	public static String NEW_GUIDE_TASK_SOPRTS = HOST + "/guide/sportValue";
	/**
	 * 完成全面检查
	 */
	public static String NEW_GUIDE_TASK_FULL_CHECK_RESULT = HOST + "/guide/finishCompleteInspection";
	/**
	 * 获取全面检查的内容
	 */
	public static String NEW_GUIDE_TASK_FULL_CHECK = HOST + "/guide/getCompleteInspection";

	/**
	 * 首页的问题结果页
	 */
	public static String NEW_INDEX_TASK_OVER = HOST + "/guide/taskOver";
	public static String FOOD_REMIND_TASK_RESULT = HOST + "/guide/foodRemindTask";
	/**
	 * 新首页
	 */
	public static String NEW_INDEX_DETAIL = HOST + "/job/indexJobNew";
	/**
	 * 新首页
	 */
	public static String NEW_GUIDE_LIST = HOST + "/guide/allTask";
	/**
	 * 获取指导任务详细
	 */
	public static String GET_GRAPHS_FOR_EXCETPTION = HOST + "/member/getGraphsForException";
	public static String GUIDE_JOB_DETAIL = HOST + "/guide/guideJobDetail";

	/**
	 * 获取阅读任务详细
	 */
	public static String READ_JOB_DETAIL = HOST + "/guide/readJobDetail";
	/**
	 * 食谱推荐
	 */
	public static String NEW_GUIDE_TASK_FOOD = HOST + "/guide/foodTask";
	/**
	 * 食谱
	 */
	public static String GUIDE_RECIPE = HOST + "/guide/foodValue";
	/**
	 * 食谱
	 */
	public static String NEW_GUIDE_RECIPE = HOST + "/guide/newFood";
	public static String NEW_GUIDE_RECIPE_INDEX = HOST + "/guide/foodResult";
	/**
	 * 完成指导任务
	 */
	public static String GUIDE_JOB_FINISH = HOST + "/guide/fulfilJobDetail";

	// zwc
	public static String SHOW = HOST + "/mobile/member/loadRangeInfoByValue";

	/**
	 * 2.添加游客
	 * http://192.168.9.100:8089/health/mobile/user/guestReg?birthday=xxx&
	 * weight=xxx&height=xxx&relation=xxx&callreason=xxx birthday:生日 weight:体重
	 * height:身高 relation:关系 callreason:是否糖尿病
	 */
	public static String GUEST_REG = HOST + "/user/guestReg";

	// 注册后首次添加成员
	public static String REG_FIRST_CREATE_MEMBER = HOST + "/member/addMemberNew";

	public static String TEST_DATA_INIT = HOST + "/guide/demoInit";

	/**
	 * 登入
	 */
	public static String LOGIN = HOST + "/user/login";

	/**
	 * 绑定QQ
	 */
	public static String BIND_QQ = HOST + "/user/memberBinding";

	/**
	 * 验证码
	 */
	public static String CHECK_SMS = HOST + "/user/checkMsg";

	/**
	 * 发送验证码
	 */
	public static String SEND_SMS = HOST + "/user/sendMsg";

	/**
	 * 注册
	 */
	public static String REGIST = HOST + "/user/reg";

	/**
	 * 重置密码
	 */
	public static String RESET_PWD = HOST + "/user/findPwd";

	/**
	 * 添加成员
	 */
	public static String ADD_MEMBER = HOST + "/member/addMemberNew";
	/**
	 * 修改成员
	 */
	// public static String MODIFY_MEMBER = HOST + "/member/modifyMember";

	public static String MODIFY_MEMBER1 = HOST + "/member/modifyMemberNew";
	/**
	 * 获取成员列表
	 */
	public static String GET_MEMBER_LIST = HOST + "/member/listMember";
	/**
	 * 获取成员列表
	 */
	public static String GET_MEMBER_INFO = HOST + "/member/viewMember";

	/**
	 * 服务条款
	 */
	public static String REG_INFO = HOST + "/user/regInfo";
	/**
	 * 删除成员
	 */
	public static String DEL_MEMBER = HOST + "/member/deleteMember";
	/**
	 * 套餐列表
	 */
	public static String SERVER_APPLY_LIST = HOST + "/server/loadFeePackageList";
	/**
	 * 申请套餐
	 */
	public static String SERVER_APPLY = HOST + "/server/buyServer";

	/**
	 * 拥有的套餐列表
	 */
	public static String SERVER_LIST = HOST + "/server/loadMemberPackageList";

	/**
	 * 拥有的套餐详情
	 */
	public static String SERVER_DETAIL = HOST + "/server/memberPackageDetail";

	/**
	 * 查询服务列表
	 * http://192.168.9.100:8089/health/mobile/server/loadHasServer?serverCode
	 * =XXXXXX
	 */
	public static String SERVER_LIST_BY_SERVER = HOST + "/server/loadHasServer";

	public static String ASK_LOAD_LAST_QUSTION = HOST + "/server/loadLastQuestionInfo";
	public static String ASK_MSG_LIST = HOST + "/server/docMemberDialogue.do";
	public static String ASK_DELEAT_MSG = HOST + "/server/delDocMemDialogue";
	/**
	 * 提问
	 * http://192.168.9.100:8089/health/mobile/server/addQuestion?questionCon=
	 * adfasdfasdf
	 * &selectPackage=14428&attachUrl=http://192.168.9.100:8089/health
	 * /attachment/headimage/1304111422474163_tmp.png
	 */
	public static String ASK_SUBMIT_CONTENT = HOST + "/server/addQuestion";
	public static String ASK_SUBMIT_NEW_CONTENT = HOST + "/server/addDocDialogue.do";
	/**
	 * 上传头像 阿里云 fileName=****&fileStr=****
	 */
	public static String SUBMIT_IMG_YUN = "http://img.mamibon.com:8080/fileuploader/uploader.do";
	// public static String SUBMIT_IMG_NEW
	// ="http://192.168.9.71:8080/fileuploader/uploader.do";
	/**
	 * 问答列表
	 * http://192.168.9.100:8089/health/mobile/server/memberQuestionList?page=1
	 */
	public static String ASK_LIST = HOST + "/server/memberQuesList";
	public static String ASK_TELL_ORDER_LIST = HOST + "/server/loadMobileOrder";
	public static String ASK_TELL_ORDER_REMOVE = HOST + "/server/deleteMobileOrder";
	public static String ASK_TELL_VERIFY = HOST + "/server/addMobileOrder";
	public static String ASK_DETAIL = HOST + "/server/loadQuestionInfo";
	public static String ASK_NEW_SERVER_LIST = HOST + "/server/getDoctorHome";
	public static String ASK_DOC_SERVER_LIST = HOST + "/server/doctorHomePage";
	public static String ASK_DOC_LIST = HOST + "/server/getDoctorList";

	public static String VOUCHER_DOC_LIST = HOST + "/coupon/getDoctorsBycouponId";
	/**
	 * 成员套餐列表
	 */
	public static String MEMBER_SERVER = HOST + "/server/loadMydoctorList";
	/**
	 * 电话咨询列表
	 */
	public static String ASK_TELL_LIST = HOST + "/server/loadAdvisoryConsult";
	public static String NO_TELL_ORDER = HOST + "/server/addYyMsg";

	/**
	 * 添加追问
	 */
	public static String ASK_ADD_CONTENT = HOST + "/server/addContinueQuestion";

	/**
	 * 获取医生拥有套餐类型的详细列表
	 */
	public static String GET_DOC_SERVER_MESSAGE = HOST + "/server/getDoctorServerList";
	/**
	 * 下服务订单
	 */
	public static String BUY_DOCTOR_SERVER = HOST + "/server/buyDoctorServer";
	/**
	 * 获取签名字符串
	 */
	public static String PAY_SIGN_TRADE = HOST + "/server/rSATrade";
	/**
	 * http://192.168.9.100:8089/health/mobile/server/changeMember?memberId=
	 * xxxxxxx 切换成员
	 */
	public static String MEMBER_CHANGE = HOST + "/member/changeMember";

	/**
	 * 设置目标和头像 /mobile/memberCenter/setGoal?photo=xxx&goal=xxx
	 */
	public static String MEMBER_SET_GOAL = HOST + "/memberCenter/setGoal";

	/**
	 * 健康记录录入
	 */
	public static String TENDENCY_INPUT = HOST + "/member/memberParamLogSet";
	public static String TENDENCY_INPUT1 = HOST + "/mobile/member/rangeSet";
	public static String RECORD_SUGGER = HOST + "/member/memberParamLogSetNew";
	/**
	 * 趋势图点列表
	 * http://192.168.9.100:8089/health/mobile/member/getGraphsForParameters
	 * ?startDt
	 * =2013-04/08&endDt=2013/04/15&paramKey=height&join_id=123&sessionID
	 * =a53cc1a37b893fea9be8400b6bc69adc
	 * &sessionMemberID=d2f8d6d8c4ae99c74cb575ba6aa7db68
	 */
	public static String TENDENCY_POINT_LIST = HOST + "/member/getGraphsForParameters";
	public static String TENDENCY_POINT_LIST_ONE_DAY = HOST + "/member/loadMemberParamLogByDay";

	/**
	 * 提醒列表
	 * http://192.168.9.100:8089/health/mobile/estimate/memberTask?join_id=123
	 * &sessionID=a53cc1a37b893fea9be8400b6bc69adc&sessionMemberID=
	 * d2f8d6d8c4ae99c74cb575ba6aa7db68
	 */
	public static String REMIND_LIST = HOST + "/estimate/memberTask";

	public static String TASK_CHECK_LOAD = HOST + "/job/loadDefualtRemindCfg";
	public static String TASK_CHECK_MODIFY = HOST + "/job/updateSelfMonitoring";
	/**
	 * 修改密码 /user/modifyPassword
	 */
	public static String MODIFY_PWD = HOST + "/user/modifyPassword";

	/**
	 * 建议反馈 /user/addSuggest
	 */
	public static String SUGGEST = HOST + "/info/addSuggest";

	/**
	 * 修改提醒
	 */
	// public static String TASK_MODIFY = HOST +
	// "/estimate/modifyMemberTaskRemind";

	/**
	 * http://192.168.9.100:8089/health/mobile/estimate/finishTask?memberTaskId=
	 * xxx&vhActionId=xxx 完成任务
	 */
	// public static String TASK_COMPLETE = HOST + "/estimate/finishTask";

	/**
	 * 问题列表
	 *
	 * quesType 1、低风险评估2、高风险评估
	 *
	 */
	public static String ASSESS_QUESTION_LIST = HOST + "/estimate/loadAssessmentQues";

	/**
	 * ASSESS_QUESTION_SUBMIT
	 * http://192.168.9.100:8089/mobile/estimate/calculateSAcasesScore
	 * ?paramStr=[{"code":"t0Q0","value":"1"},{}...]
	 */
	public static String ASSESS_QUESTION_CHECK_SUBMIT = HOST + "/estimate/calculateSAcasesScore";

	/**
	 * 评估检查
	 */
	public static String ASSESS_CHECK = HOST + "/estimate/checkAssessment";

	/**
	 * 评估提交
	 */
	public static String ASSESS_QUESTION_SUBMIT = HOST + "/estimate/commitAssessmentQues";

	/**
	 * 医生评估
	 */
	public static String ASSESS_APPLY_DOC = HOST + "/estimate/doctorAssessment";

	// 验证成员可添加剩于次数
	public static String MEMBER_CHECK_ADD_MEMBER_MAX = HOST + "/member/checkAddMemberMax";

	/**
	 * 评估列表
	 */
	public static String ASSESS_LIST = HOST + "/estimate/assessmentList";

	/**
	 * 评估报告 http://192.168.9.100:8089/health/mobile/estimate/assessmentReport?
	 * reportId=48
	 */
	public static String ASSESS_REPORT = HOST + "/estimate/assessmentReport";

	/**
	 * 推送绑定
	 */
	public static String BIND_PUSH = HOST + "/info/setPushKey";

	/**
	 * 提醒开关设置
	 */
	public static String MORE_MODIFY_REMIND_SET = HOST + "/info/setRemind";

	/**
	 * 读取提醒开关设置
	 */
	public static String MORE_LOAD_REMIND_SET = HOST + "/info/loadRemindSet";
	public static String GET_TELL_DETAIL = HOST + "/server/loadMobileOrder";
	/**
	 * 版本更新
	 */
	public static String MORE_UPDATE_APP = HOST + "/info/clientVersion";

	/**
	 * 文字说明
	 */
	public static String MORE_TEXT_INFO = HOST + "/info/textInfo";

	/**
	 * 服务条款
	 */
	public static String MORE_WEB_SERVER = HOST + "/statement.html";

	/**
	 * 任务中心
	 */
	public static String TASK_CENTER = HOST + "/job/jobList";
	public static String EXCEPTION = HOST + "/member/memberExceptionList";
	/**
	 * 推荐任务中心
	 */
	public static String RECOMMENT_TASK_CENTER = HOST + "/job/recommendJobList";
	/**
	 * 任务领取
	 */
	public static String TASK_APPLY = HOST + "/job/getJob";
	/**
	 * 任务说明
	 */
	public static String TASK_INTRODUCE = HOST + "/job/jobDetail";
	/**
	 * 我的任务列表
	 */
	public static String TASK_MINE = HOST + "/job/memberJobList";

	/**
	 * 任务详情
	 */
	public static String TASK_DETAIL = HOST + "/job/memberJobInfo";

	/**
	 * 建议的任务列表
	 */
	public static String RECOMMOND_TASK_LIST = HOST + "/job/taskSuggest";
	/**
	 * 完成任务
	 */
	public static String TASK_COMPLETE = HOST + "/job/finishJob";

	/**
	 * 任务开关
	 */
	public static String TASK_MODIFY = HOST + "/job/setMemberJobRemind";

	/**
	 * 删除任务
	 */
	public static String TASK_REMOVE = HOST + "/job/canncelJob";

	/**
	 * 首页
	 */
	public static String INDEX = HOST + "/job/indexJob";
	/**
	 * 新首页血糖数据
	 */
	public static String INDEX_SUGAR_MSG = HOST + "/job/index";

	public static String INDEX_TASK_GUIDE_NEW = HOST + "/guide/loadMemberTaskCfg";
	public static String INDEX_TASK_LOOK = HOST + "/job/readJob";

	/**
	 * 配置
	 */
	public static String APP_CONFIG = HOST + "/info/loadAppCfg";

	/**
	 * 食物血糖指数1
	 */
	public static String BLOOD_SUGAR = HOST + "/tool/loadGiFoods";
	/**
	 * 医生拥有的套餐列表
	 */
	public static String DOC_SERVER_LIST = HOST + "/server/getDoctorPackages";
	/**
	 * 食物热量指数1
	 */
	public static String HEAT = HOST + "/tool/getIngredientDics.do";
	/**
	 * 食物热量
	 */
	public static String HEAT_SECOND = HOST + "/tool/getIngredients";

	/**
	 * 食物热量指数 详细
	 */
	public static String HEAT_DETAIL = HOST + "/tool/loadFoodEnergy";

	/**
	 * 添加设备 //绑定机器码
	 * http://218.106.144.2:8890/mobile/member/addMemberidMachine?machineId
	 * =xxxxx&machineType=xxx
	 */
	public static String MACHINE_ADD = HOST + "/member/addMemberidMachine";

	/**
	 * 删除设备 //删除
	 * http://218.106.144.2:8890/mobile/member/deleteMemberidMachine?machineId
	 * =xxxxx&machineType=xxx
	 */
	public static String MACHINE_REMOVE = HOST + "/member/deleteMemberidMachine";

	public static String MACHINE_SUGARBLOOD = HOST + "/member/loadMemberParamById";

	public static String MACHINE_HIGHBLOOD = HOST + "/member/loadMemberParamById";

	public static String INDEX_BLOOD_SCORE = HOST + "/member/loadLastMemberParam";

	/**
	 * 已绑定列表
	 */
	public static String MACHINE_LIST = HOST + "/member/selectMemberidMachine";

	public static String ORDER_LIST = HOST + "/mall/orderListNew";

	public static String ORDER_REMOVE = HOST + "/mall/delOrder";

	public static String ORDER_DETAIL = HOST + "/mall/orderInfo";

	public static String MESSAGE_LIST = HOST + "/job/indexMsg";
	public static String MESSAGE_LIST_NEW = HOST + "/job/getCurrent";
	public static String FINISH_WARM = HOST + "/job/finishWarm";

	public static String UPLOAD_FILE = "http://img.mamibon.com:8080/fileuploader/uploader.do";
	/**
	 * 验证乐活节目收藏
	 */
	public static String CHECK_RADIO_COLLECT = HOST + "/lohas/checkCollect";
	/**
	 * 验证收藏
	 */
	public static String CHECKCOLLECT = HOST + "/memberCenter/checkCollect";
	/**
	 * 收藏列表
	 */
	public static String COLLECT_LIST = HOST + "/memberCenter/collectList";
	/**
	 * 取消收藏
	 */
	public static String COLLECT_CANCLE = HOST + "/memberCenter/cancleKnowledge";
	/**
	 * 添加收藏
	 */
	public static String COLLECT_ADD = HOST + "/memberCenter/collectKnowledge";
	/**
	 * 添加资讯收藏
	 */
	public static String COLLECT_ADD_MESSAGE = HOST + "/memberCenter/collectMessage";

	public static String COLLECT_ADD_NEW = HOST + "/memberCenter/collectArticle";
	/**
	 * 随访列表
	 */
	public static String FOLLOW_LIST = HOST + "/follow/selectFollowUpList";

	/**
	 * 获取随访问卷
	 */
	public static String FOLLOW_GETQUESTION = HOST + "/follow/selectFollowUpById";

	/**
	 * 提交随访
	 */
	public static String FOLLOW_SUBMIT = HOST + "/follow/addFollowUpMemberLog";
	/**
	 * 随访行动方案
	 */
	public static String FOLLOW_ACTION = HOST + "/follow/getActionPlanDetail";
	/**
	 * 随访历史记录
	 */
	public static String FOLLOW_LOG = HOST + "/follow/getFollowUpMemberLog";

	/**
	 * 请求业务的个数
	 */
	public static String MEMBER_CENTER_NUM = HOST + "/memberCenter/memberCenterSet";
	//

	/**
	 * 月度总结列表
	 */
	public static String SUM_LIST = HOST + "/member/getMemberSummeryList";
	/**
	 * 请求成员信息
	 */
	public static String MEMBER_ARCHIVENEW = HOST + "/member/memberArchiveNew";

	public static String LOAD_HEALTH_INDEX = HOST + "/info/loadHealthIndex";
	public static String FINASH_FOOD_TASK = HOST + "/guide/finishFood";
	public static String FINASH_FOOD_REMIDE = HOST + "/guide/finishFoodRemind";
	public static String NEW_SPORT_JOB = HOST + "/guide/newSport";
	public static String SPORE_REMIND = HOST + "/guide/sportRemind";
	public static String REFRES_MSG = HOST + "/server/refreshDialogue.do";
	/**
	 * 健康知识
	 */
	public static String LOAD_ALL_KNOWLEDGE = HOST + "/info/loadAllKnow";

	// 上传化验单
	public static String UPLOAD_LABORATOR = HOST + "/member/uploadFile";

	// 验化单解读ID
	public static String ANSWER_SERVICE_ID = HOST + "/member/getAnswerService";
	// 化验单列表

	public static String LABORATOR_LIST = HOST + "/member/getUploadPicsAndAnswer";

	// 更新化验单
	public static String UPDATE_LABORATOR = HOST + "/member/resumeUploadPic";
	// 删除某张化验单
	public static String DELETE_LABORATOR_PIC = HOST + "/member/delUploadPic";
	// 删除化验单
	public static String DELETE_LABORATOR = HOST + "/member/delUploadFolder";

	// 上传食谱
	public static String UPLOAD_DIET = HOST + "/member/uploadFoodFileNew";
	// 食谱列表
	public static String DIET_LIST_NEW = HOST + "/member/getFoodUploadPicsNew";
	// 添加到我的三餐
	public static String DIET_COLLECT = HOST + "/memberCenter/collectArticle";
	// 移出我的三餐
	public static String DIET_REMOVE_COLLECT = HOST + "/memberCenter/cancleKnowledge";
	// 我的三餐列表
	public static String THREE_MEAL_COLLECT = HOST + "/memberCenter/collectList";
	// 医生推荐食材列表
	public static String DIET_DOCTOR_REMOMMEND = HOST + "/tool/getDoctorRecommendIngredients";
	// 化验食谱
	public static String DIET_LIST = HOST + "/member/getFoodUploadPics";
	// 更新食谱
	public static String UPDATE_DIET = HOST + "/member/resumeFoodUploadPicNew";
	// 删除某张食谱图
	public static String DELETE_DIET_PIC = HOST + "/member/delFoodUploadPic";
	// 删除食谱
	public static String DELETE_DIET = HOST + "/member/delFoodUploadFolder";
	// 运动记录
	public static String SPORT_LIST = HOST + "/tool/getSportRecords";
	// 运动类型
	public static String SPORT_TYPE = HOST + "/tool/getSportCalories";
	// 添加运动记录
	public static String ADD_SPORT_RECORD = HOST + "/tool/addSportRecord";
	// 删除运动记录
	public static String DELETESPORT_RECORD = HOST + "/tool/delSportRecord";
	public static String UPDATE_SPORT_RECORD = HOST + "/tool/updateSportRecord";
	// 药名列表
	public static String MEDICINAL_LIST = HOST + "/tool/getDrugsdepots";
	// 用药提醒列表
	public static String DRUG_REMIND_LIST = HOST + "/tool/getDrugsRecords";
	// 添加用药提醒
	public static String ADD_PHARMACY_REMIND = HOST + "/tool/saveDrugsRecord";
	// 删除用药提醒
	public static String DELETE_PHARMACY_REMIND = HOST + "/tool/delDrugsRecord";
	// 消息数量
	public static String GET_MSG_NUM = HOST + "/job/indexMsgCount";
	// 卡券列表
	public static String GET_VOUCHER = HOST + "/coupon/loadCouponListNew";
	// 首页卡券弹窗
	public static String INDEX_DISCOUNT = HOST + "/coupon/getCoupon";
	// 获取分享信息
	public static String GET_VOUCHER_SHARE_MSG = HOST + "/coupon/getCouponDetail";

	// 获取交换历史列表
	public static String FOOD_EXCHANGE_HISTORY = HOST + "/member/foodChangeList";
	// 添加食物交换
	public static String ADD_FOOD_EXCHANGE = HOST + "/member/uploadFoodChange";
	// 删除食物交换历史
	public static String DELETE_FOOD_EXCHANGE = HOST + "/member/delfoodChange";

	// 分享成功后回调
	public static String SHARE_COMPLETE = HOST + "/memberCenter/forwardingArticle.do";
	// 修改餐后血糖值
	public static String UPDATE_AFTERMEAL_VALUE = HOST + "/member/uploadFoodSugger";
	// 获取餐后血糖数据
	public static String GET_SUGGER_NUM = HOST + "/member/getFoodSugger";
	// 获取补丁包
	public static String GET_ANDFIX_FILE = HOST + "/info/getPackInfo";

	public static String RAIOD_MAIN_LIST = HOST + "/lohas/loadHomePage";
	public static String RAIOD_ALBUM = HOST + "/lohas/loadAlbumList";
	public static String RAIOD_ALBUM_LIST = HOST + "/lohas/loadProgramByType";
	// 加载评论列表
	public static String RADIO_COMMENT = HOST + "/lohas/loadComment";
	// 添加评论
	public static String RADIO_COMMENT_ADD = HOST + "/lohas/addComment";
	// 点赞
	public static String RADIO_COMMENT_ADD_PRAISE = HOST + "/lohas/addPraise";
	// 下一个专辑
	public static String RADIO_PLAY_NEXT = HOST + "/lohas/loadNextPlay";
	// 上一个一个专辑
	public static String RADIO_PLAY_PRO = HOST + "/lohas/loadPrePlay";
	public static String RADIO_COLLECT_ADD = HOST + "/lohas/collect";
	public static String RADIO_COLLECT_LOAD = HOST + "/lohas/collectList";
	public static String RADIO_COLLECT_CANCLE = HOST + "/lohas/cancleCollect";
	// 环信注册 设置昵称
	public static String RADIO_REG = HOST + "/easemob/regUser";
	// 加入直播
	public static String RADIO_JOIN_ROOM = HOST + "/lohas/joinChatRoom";

	// 直播预告 （设置提醒）
	public static String RADIO_ADVANCE_ADD = HOST + "/send/addSendMsg";
	// 直播预告 （取消提醒）
	public static String RADIO_ADVANCE_CANCLE = HOST + "/send/deleteSendMsg";

	public static String LOAD_INDEX_TURN = HOST + "/activity/loadIndexTurn";

	// 我的钱包
	public static String MONEY_MY_WALLET = HOST + "/activity/loadMemberPurse";
	// 闪屏页面
	public static String SPLASH_MONEY = HOST + "/info/loadHealthIndex";
	// 零钱首页
	public static String ACCOUNT_BALANCE = HOST + "/activity/loadPurseIndex";
	// 零钱明细
	public static String BALANCE_MONEY = HOST + "/activity/loadPurseDetail";
	// 记录血糖后领取红包(如果是正常注册账号)
	public static String GET_RED_FOR_APP = HOST + "/activity/getRedForApp.do";
	// 记录血糖后领取红包(如果是游客或者QQ登录用户)发验证码
	public static String SEND_SMS_FOR_APP_RED = HOST + "/activity/sendSmsForAppRed.do";
	//支付宝绑定
	public static String ZFB_BINDALIPAY = HOST + "/activity/takeMoney";
	//知识Tab
	public static String NEWS_KNOWLEDGE=HOST+"/hotSpot/loadKnowledgeType";
	//知识列表
	public static String NEWS_KNOWLEDGE_LIST=HOST+"/hotSpot/loadKnowledgeListByType";

	//栏目列表
	public static String NEWS_TITLE_LIST=HOST+"/hotSpot/modifyMemberHotSpot";

}

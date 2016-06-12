package com.snowstore.diana.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.snowstore.diana.vo.Areas;
import com.snowstore.poseidon.client.vo.response.BankInfo;

/**
 * 常量类
 * 
 * @author wujinsong
 * 
 */
public class Constants {
	/**
	 * 英文逗号
	 */
	public static final String Comma = ",";

	/**
	 * 是管理员
	 */
	public static final String ISMNG = "isMng";

	/**
	 * 不是管理员
	 */
	public static final String ISNOTMNG = "isNotMng";

	/**
	 * 英文分号
	 */
	public static final String ASemicolon = ";";
	/**
	 * A类卡券
	 */
	public static final String CARD_STOCK_A = "A";
	/**
	 * B类卡券
	 */
	public static final String CARD_STOCK_B = "B";
	/**
	 * 卡券支付成功
	 */
	public static final String CARDSTOCK_PAY_SUCCESS = "CARDSTOCK_PAY_SUCCESS";
	/**
	 * 快捷支付成功
	 */
	public static final String QUICK_PAY_SUCCESS = "QUICK_PAY_SUCCESS";
	/**
	 * 快捷支付中
	 */
	public static final String QUICK_PAY_IN = "QUICK_PAY_IN";
	/**
	 * 快捷支付失败
	 */
	public static final String QUICK_PAY_FAILURE = "QUICK_PAY_FAILURE";
	/**
	 * 后台订单类型-电影票
	 */
	public static final String ORDER_TYPE_MOVIE = "movie";
	/**
	 * 处理接口--成功
	 */
	public static final String STATUS_SUCCESS = "success";
	/**
	 * 处理接口--失败
	 */
	public static final String STATUS_ERROR = "error";
	/**
	 * 查询全部渠道选项
	 */
	public static final String ALL_PLATFORM = "ALL_PLATFORM";
	/**
	 * 序列ID属性名
	 */
	public static final String SERIAL_VERSION_UID = "serialVersionUID";
	/**
	 * 快捷支付流水号序列名
	 */
	public static final String SEQUENCE_AUTH_SERIAL_NUMBER = "SEQUENCE_AUTH_SERIAL_NUMBER";
	/**
	 * 快捷支付-账户类型-个人
	 */
	public static final String QUICKPAY_ACCOUNTTYPE_PERSONAL = "10040001";
	/**
	 * 快捷支付-证件类型-身份证
	 */
	public static final String QUICKPAY_IDTYPE_IDCARD = "身份证";
	/**
	 * 快捷支付-卡类型-借记卡
	 */
	public static final String QUICKPAY_CARDTYPE_DEBITCARD = "10060001";

	/**
	 * 快捷支付响应码
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午1:24:56
	 */
	public static final class QuickpayRespcode {
		/**
		 * 响应码-认证短信已发送
		 */
		public static final String AUTHSMSSEND = "100044";
		/**
		 * 响应码-代收短信已发送
		 */
		public static final String PAYSMSSEND = "100035";
		/**
		 * 响应码-成功
		 */
		public static final String SUCCESS = "000000";
	}

	/**
	 * 快捷支付-支付通道-中金
	 */
	public static final String QUICKPAY_PAYCHANNEL_ZHONGJIN = "10010001";

	/**
	 * 快捷支付-支付状态
	 * 
	 * @author XieZG
	 * @Date:2016年2月19日下午3:17:08
	 */
	public static final class QuickpayStatus {
		/**
		 * 支付状态-支付成功
		 */
		public static final String PAYSUCCESS = "支付成功";
		/**
		 * 支付状态-支付失败
		 */
		public static final String PAYFAILURE = "支付失败";
		/**
		 * 支付状态-支付确认中
		 */
		public static final String PAYIN = "支付确认中";
	}

	/**
	 * 快捷支付-代扣状态
	 * 
	 * @author XieZG
	 * @Date:2016年2月19日下午3:17:08
	 */
	public static final class WithholdStatus {
		public static final String SUCCESS = "代扣成功";
		public static final String FAILURE = "代扣失败";
		public static final String IN = "代扣处理中";
		public static final String PAYSUCCESS = "快捷支付成功";
		public static final String PAYFAILURE = "快捷支付失败";
		public static final String SMSSEND = "快捷支付验证短信已发送";
	}

	/**
	 * 快捷支付-订单状态
	 * 
	 * @author XieZG
	 * @Date:2016年2月19日下午3:17:08
	 */
	public static final class QuickPayOrderStatus {
		/**
		 * 订单状态-付款中
		 */
		public static final String PAYIN = "付款中";
		/**
		 * 订单状态-待付款
		 */
		public static final String PENDING_PAYMENT = "待付款";
	}

	/**
	 * 查询银行-业务类型-快捷支付
	 */
	public static final String QUICKPAY_BUSITYPE_QUICKPAY = "01000011";

	/**
	 * 快捷支付地区集合
	 */
	public static Areas areas = new Areas();
	/**
	 * 易联错误信息
	 */
	public static Map<String, String> JLFEX_ERROR = new HashMap<String, String>();
	/**
	 * 票务系统支持快捷支付银行CODE
	 */
	public static List<String> BANK_CODE_LIST = new ArrayList<String>();
	/**
	 * 票务系统支持银行集合
	 */
	public static Map<String, BankInfo> BANK_LIST = new Hashtable<String, BankInfo>();

	static {
		areas.put("北京", "北京");
		areas.put("天津", "天津");
		areas.put("河北", "张家口");
		areas.put("河北", "邢台");
		areas.put("河北", "唐山");
		areas.put("河北", "秦皇岛");
		areas.put("河北", "廊坊");
		areas.put("河北", "衡水");
		areas.put("河北", "邯郸");
		areas.put("河北", "承德");
		areas.put("河北", "沧州");
		areas.put("河北", "保定");
		areas.put("河北", "石家庄");
		areas.put("山西", "吕梁");
		areas.put("山西", "晋中");
		areas.put("山西", "运城");
		areas.put("山西", "阳泉");
		areas.put("山西", "忻州");
		areas.put("山西", "朔州");
		areas.put("山西", "临汾");
		areas.put("山西", "晋城");
		areas.put("山西", "大同");
		areas.put("山西", "长治");
		areas.put("山西", "太原");
		areas.put("内蒙古", "通辽");
		areas.put("内蒙古", "鄂尔多斯");
		areas.put("内蒙古", "兴安盟");
		areas.put("内蒙古", "锡林郭勒盟");
		areas.put("内蒙古", "乌兰察布");
		areas.put("内蒙古", "乌海");
		areas.put("内蒙古", "呼伦贝尔");
		areas.put("内蒙古", "赤峰");
		areas.put("内蒙古", "巴彦淖尔");
		areas.put("内蒙古", "阿拉善盟");
		areas.put("内蒙古", "包头");
		areas.put("内蒙古", "呼和浩特");
		areas.put("辽宁", "营口");
		areas.put("辽宁", "铁岭");
		areas.put("辽宁", "盘锦");
		areas.put("辽宁", "辽阳");
		areas.put("辽宁", "锦州");
		areas.put("辽宁", "葫芦岛");
		areas.put("辽宁", "阜新");
		areas.put("辽宁", "抚顺");
		areas.put("辽宁", "丹东");
		areas.put("辽宁", "朝阳");
		areas.put("辽宁", "本溪");
		areas.put("辽宁", "鞍山");
		areas.put("辽宁", "大连");
		areas.put("辽宁", "沈阳");
		areas.put("吉林", "延边");
		areas.put("吉林", "通化");
		areas.put("吉林", "松原");
		areas.put("吉林", "四平");
		areas.put("吉林", "辽源");
		areas.put("吉林", "吉林市");
		areas.put("吉林", "白山");
		areas.put("吉林", "白城");
		areas.put("吉林", "长春");
		areas.put("黑龙江", "伊春");
		areas.put("黑龙江", "绥化");
		areas.put("黑龙江", "双鸭山");
		areas.put("黑龙江", "齐齐哈尔");
		areas.put("黑龙江", "七台河");
		areas.put("黑龙江", "牡丹江");
		areas.put("黑龙江", "佳木斯");
		areas.put("黑龙江", "鸡西");
		areas.put("黑龙江", "黑河");
		areas.put("黑龙江", "鹤岗");
		areas.put("黑龙江", "大兴安岭地区");
		areas.put("黑龙江", "大庆");
		areas.put("黑龙江", "哈尔滨");
		areas.put("上海", "上海");
		areas.put("江苏", "镇江");
		areas.put("江苏", "扬州");
		areas.put("江苏", "盐城");
		areas.put("江苏", "徐州");
		areas.put("江苏", "无锡");
		areas.put("江苏", "泰州");
		areas.put("江苏", "宿迁");
		areas.put("江苏", "苏州");
		areas.put("江苏", "南通");
		areas.put("江苏", "连云港");
		areas.put("江苏", "淮安");
		areas.put("江苏", "常州");
		areas.put("江苏", "南京");
		areas.put("浙江", "丽水");
		areas.put("浙江", "衢州");
		areas.put("浙江", "舟山");
		areas.put("浙江", "温州");
		areas.put("浙江", "台州");
		areas.put("浙江", "绍兴");
		areas.put("浙江", "金华");
		areas.put("浙江", "嘉兴");
		areas.put("浙江", "湖州");
		areas.put("浙江", "宁波");
		areas.put("浙江", "杭州");
		areas.put("安徽", "宿州");
		areas.put("安徽", "亳州");
		areas.put("安徽", "宣城");
		areas.put("安徽", "芜湖");
		areas.put("安徽", "铜陵");
		areas.put("安徽", "马鞍山");
		areas.put("安徽", "六安");
		areas.put("安徽", "黄山");
		areas.put("安徽", "淮南");
		areas.put("安徽", "淮北");
		areas.put("安徽", "阜阳");
		areas.put("安徽", "滁州");
		areas.put("安徽", "池州");
		areas.put("安徽", "巢湖");
		areas.put("安徽", "蚌埠");
		areas.put("安徽", "安庆");
		areas.put("安徽", "合肥");
		areas.put("福建", "漳州");
		areas.put("福建", "三明");
		areas.put("福建", "泉州");
		areas.put("福建", "莆田");
		areas.put("福建", "宁德");
		areas.put("福建", "南平");
		areas.put("福建", "龙岩");
		areas.put("福建", "厦门");
		areas.put("福建", "福州");
		areas.put("江西", "鹰潭");
		areas.put("江西", "宜春");
		areas.put("江西", "新余");
		areas.put("江西", "上饶");
		areas.put("江西", "萍乡");
		areas.put("江西", "九江");
		areas.put("江西", "景德镇");
		areas.put("江西", "吉安");
		areas.put("江西", "赣州");
		areas.put("江西", "抚州");
		areas.put("江西", "南昌");
		areas.put("山东", "淄博");
		areas.put("山东", "枣庄");
		areas.put("山东", "烟台");
		areas.put("山东", "潍坊");
		areas.put("山东", "威海");
		areas.put("山东", "泰安");
		areas.put("山东", "日照");
		areas.put("山东", "临沂");
		areas.put("山东", "聊城");
		areas.put("山东", "莱芜");
		areas.put("山东", "济宁");
		areas.put("山东", "菏泽");
		areas.put("山东", "东营");
		areas.put("山东", "德州");
		areas.put("山东", "滨州");
		areas.put("山东", "青岛");
		areas.put("山东", "济南");
		areas.put("河南", "濮阳");
		areas.put("河南", "漯河");
		areas.put("河南", "驻马店");
		areas.put("河南", "周口");
		areas.put("河南", "许昌");
		areas.put("河南", "信阳");
		areas.put("河南", "新乡");
		areas.put("河南", "商丘");
		areas.put("河南", "三门峡");
		areas.put("河南", "平顶山");
		areas.put("河南", "南阳");
		areas.put("河南", "洛阳");
		areas.put("河南", "开封");
		areas.put("河南", "鹤壁");
		areas.put("河南", "焦作");
		areas.put("河南", "安阳");
		areas.put("河南", "郑州");
		areas.put("湖北", "仙桃");
		areas.put("湖北", "宜昌");
		areas.put("湖北", "潜江");
		areas.put("湖北", "天门");
		areas.put("湖北", "神农架林区");
		areas.put("湖北", "孝感");
		areas.put("湖北", "襄阳");
		areas.put("湖北", "咸宁");
		areas.put("湖北", "随州");
		areas.put("湖北", "十堰");
		areas.put("湖北", "荆州");
		areas.put("湖北", "荆门");
		areas.put("湖北", "黄石");
		areas.put("湖北", "黄冈");
		areas.put("湖北", "恩施");
		areas.put("湖北", "鄂州");
		areas.put("湖北", "武汉");
		areas.put("湖南", "株洲");
		areas.put("湖南", "张家界");
		areas.put("湖南", "岳阳");
		areas.put("湖南", "永州");
		areas.put("湖南", "益阳");
		areas.put("湖南", "湘西州");
		areas.put("湖南", "湘潭");
		areas.put("湖南", "邵阳");
		areas.put("湖南", "娄底");
		areas.put("湖南", "怀化");
		areas.put("湖南", "衡阳");
		areas.put("湖南", "郴州");
		areas.put("湖南", "常德");
		areas.put("湖南", "长沙");
		areas.put("广东", "珠海");
		areas.put("广东", "河源");
		areas.put("广东", "中山");
		areas.put("广东", "肇庆");
		areas.put("广东", "湛江");
		areas.put("广东", "云浮");
		areas.put("广东", "阳江");
		areas.put("广东", "韶关");
		areas.put("广东", "汕尾");
		areas.put("广东", "汕头");
		areas.put("广东", "清远");
		areas.put("广东", "梅州");
		areas.put("广东", "茂名");
		areas.put("广东", "揭阳");
		areas.put("广东", "江门");
		areas.put("广东", "惠州");
		areas.put("广东", "佛山");
		areas.put("广东", "东莞");
		areas.put("广东", "潮州");
		areas.put("广东", "深圳");
		areas.put("广东", "广州");
		areas.put("广西", "钦州");
		areas.put("广西", "来宾");
		areas.put("广西", "贺州");
		areas.put("广西", "贵港");
		areas.put("广西", "防城港");
		areas.put("广西", "崇左");
		areas.put("广西", "玉林");
		areas.put("广西", "梧州");
		areas.put("广西", "柳州");
		areas.put("广西", "河池");
		areas.put("广西", "桂林");
		areas.put("广西", "北海");
		areas.put("广西", "百色");
		areas.put("广西", "南宁");
		areas.put("海南", "五指山");
		areas.put("海南", "文昌");
		areas.put("海南", "万宁");
		areas.put("海南", "屯昌");
		areas.put("海南", "琼中");
		areas.put("海南", "琼海");
		areas.put("海南", "陵水");
		areas.put("海南", "临高");
		areas.put("海南", "乐东");
		areas.put("海南", "东方");
		areas.put("海南", "定安");
		areas.put("海南", "儋州");
		areas.put("海南", "澄迈");
		areas.put("海南", "昌江");
		areas.put("海南", "保亭");
		areas.put("海南", "白沙");
		areas.put("海南", "三亚");
		areas.put("海南", "海口");
		areas.put("重庆", "重庆");
		areas.put("四川", "资阳");
		areas.put("四川", "甘孜州");
		areas.put("四川", "阿坝州");
		areas.put("四川", "泸州");
		areas.put("四川", "自贡");
		areas.put("四川", "宜宾");
		areas.put("四川", "雅安");
		areas.put("四川", "遂宁");
		areas.put("四川", "攀枝花");
		areas.put("四川", "内江");
		areas.put("四川", "南充");
		areas.put("四川", "绵阳");
		areas.put("四川", "眉山");
		areas.put("四川", "凉山州");
		areas.put("四川", "乐山");
		areas.put("四川", "广元");
		areas.put("四川", "广安");
		areas.put("四川", "德阳");
		areas.put("四川", "达州");
		areas.put("四川", "巴中");
		areas.put("四川", "成都");
		areas.put("贵州", "黔西南州");
		areas.put("贵州", "黔南州");
		areas.put("贵州", "黔东南州");
		areas.put("贵州", "遵义");
		areas.put("贵州", "铜仁地区");
		areas.put("贵州", "六盘水");
		areas.put("贵州", "毕节地区");
		areas.put("贵州", "安顺");
		areas.put("贵州", "贵阳");
		areas.put("云南", "迪庆州");
		areas.put("云南", "昭通");
		areas.put("云南", "玉溪");
		areas.put("云南", "文山");
		areas.put("云南", "普洱");
		areas.put("云南", "曲靖");
		areas.put("云南", "怒江州");
		areas.put("云南", "临沧");
		areas.put("云南", "丽江");
		areas.put("云南", "红河州");
		areas.put("云南", "德宏州");
		areas.put("云南", "大理州");
		areas.put("云南", "楚雄州");
		areas.put("云南", "保山");
		areas.put("云南", "西双版纳");
		areas.put("云南", "昆明");
		areas.put("西藏", "山南地区");
		areas.put("西藏", "日喀则地区");
		areas.put("西藏", "那曲地区");
		areas.put("西藏", "林芝地区");
		areas.put("西藏", "昌都地区");
		areas.put("西藏", "阿里地区");
		areas.put("西藏", "拉萨");
		areas.put("陕西", "榆林");
		areas.put("陕西", "延安");
		areas.put("陕西", "咸阳");
		areas.put("陕西", "渭南");
		areas.put("陕西", "铜川");
		areas.put("陕西", "商洛");
		areas.put("陕西", "汉中");
		areas.put("陕西", "宝鸡");
		areas.put("陕西", "安康");
		areas.put("陕西", "西安");
		areas.put("甘肃", "甘南州");
		areas.put("甘肃", "张掖");
		areas.put("甘肃", "武威");
		areas.put("甘肃", "天水");
		areas.put("甘肃", "庆阳");
		areas.put("甘肃", "平凉");
		areas.put("甘肃", "陇南");
		areas.put("甘肃", "临夏州");
		areas.put("甘肃", "酒泉");
		areas.put("甘肃", "金昌");
		areas.put("甘肃", "嘉峪关");
		areas.put("甘肃", "定西");
		areas.put("甘肃", "白银");
		areas.put("甘肃", "兰州");
		areas.put("青海", "玉树州");
		areas.put("青海", "黄南州");
		areas.put("青海", "海西州");
		areas.put("青海", "海南州");
		areas.put("青海", "海北州");
		areas.put("青海", "果洛州");
		areas.put("青海", "海东地区");
		areas.put("青海", "西宁");
		areas.put("宁夏", "吴忠");
		areas.put("宁夏", "石嘴山");
		areas.put("宁夏", "固原");
		areas.put("宁夏", "中卫");
		areas.put("宁夏", "银川");
		areas.put("新疆", "五家渠");
		areas.put("新疆", "阿拉尔");
		areas.put("新疆", "克孜勒苏州");
		areas.put("新疆", "伊犁州");
		areas.put("新疆", "吐鲁番地区");
		areas.put("新疆", "塔城地区");
		areas.put("新疆", "石河子");
		areas.put("新疆", "图木舒克");
		areas.put("新疆", "克拉玛依");
		areas.put("新疆", "喀什地区");
		areas.put("新疆", "和田地区");
		areas.put("新疆", "哈密地区");
		areas.put("新疆", "昌吉州");
		areas.put("新疆", "博尔塔拉州");
		areas.put("新疆", "巴音郭楞");
		areas.put("新疆", "阿勒泰地区");
		areas.put("新疆", "阿克苏地区");
		areas.put("新疆", "乌鲁木齐");
		areas.put("香港", "香港");
		areas.put("澳门", "澳门");
		areas.put("台湾", "台湾");

		// 初始化票务系统支持快捷支付银行CODE
		BANK_CODE_LIST.add("00080003");//工商银行
		BANK_CODE_LIST.add("00080004");//农业银行
		BANK_CODE_LIST.add("00080001");//中国银行
		BANK_CODE_LIST.add("00080005");//建设银行
		BANK_CODE_LIST.add("00080006");//交通银行
		BANK_CODE_LIST.add("00080013");//光大银行
		BANK_CODE_LIST.add("00080009");//民生银行
		BANK_CODE_LIST.add("00080029");//广发银行
		BANK_CODE_LIST.add("00080012");//中信银行
		BANK_CODE_LIST.add("00080008");//浦发银行
		BANK_CODE_LIST.add("00080011");//兴业银行
		BANK_CODE_LIST.add("00080015");//上海银行
		BANK_CODE_LIST.add("00080020");//北京银行
		BANK_CODE_LIST.add("00080014");//平安银行
		BANK_CODE_LIST.add("00080051");//浙商银行

		// 初始化错误码
		JLFEX_ERROR.put("100043", "绑卡失败，请重新绑定");// 还有一种可能性，短信校验失败
		JLFEX_ERROR.put("100044", "快捷认证短信已发送");
		JLFEX_ERROR.put("100041", "银行卡解绑失败");
		JLFEX_ERROR.put("000000", "快捷代收--成功");
		JLFEX_ERROR.put("100045", "验证码已过期，请重新获取新的验证码");
		JLFEX_ERROR.put("100046", "验证码错误，请再次输入");
		JLFEX_ERROR.put("100047", "快捷认证处理中");
		JLFEX_ERROR.put("100034", "请重新支付");
		JLFEX_ERROR.put("100035", "验证码已发送，请注意查收");
		JLFEX_ERROR.put("100036", "验证码已过期，请重新获取新的验证码");
		JLFEX_ERROR.put("100037", "验证码错误，请再次输入");
		JLFEX_ERROR.put("100038", "快捷代收---处理中");
		JLFEX_ERROR.put("100049", "快捷结算–处理中");
		JLFEX_ERROR.put("100050", "支付失败，请重新支付");
		JLFEX_ERROR.put("000001", "服务器处理异常");
		JLFEX_ERROR.put("100101", "请求url不正确");
		JLFEX_ERROR.put("100102", "姓名必须中文、英文大小写字母及空格，限长度40个字符！");
		JLFEX_ERROR.put("100103", "时间误差过大");
		JLFEX_ERROR.put("100104", "方法名不存在");
		JLFEX_ERROR.put("100105", "不支持的响应格式");
		JLFEX_ERROR.put("100106", "证书无效");
		JLFEX_ERROR.put("100107", "数据超长");
		JLFEX_ERROR.put("100108", "支付失败，请重新支付");// 也可能是短信校验失败
		JLFEX_ERROR.put("100109", "请求方式不正确");
		JLFEX_ERROR.put("100110", "支付请求已提交，请勿重复提交");
		JLFEX_ERROR.put("100120", "0点~2点为系统维护时间");
		JLFEX_ERROR.put("100121", "1秒内禁止重复提交");
		JLFEX_ERROR.put("200001", "无效理财产品编号");
		JLFEX_ERROR.put("200002", "无效金额");
		JLFEX_ERROR.put("200003", "无效证件号");
		JLFEX_ERROR.put("200004", "无效手机号");
		JLFEX_ERROR.put("200005", "无效日期");
		JLFEX_ERROR.put("200006", "无效抓取条数");
		JLFEX_ERROR.put("200007", "无效页数");
		JLFEX_ERROR.put("200008", "无效状态");
		JLFEX_ERROR.put("200009", "无效文件编号");
		JLFEX_ERROR.put("200010", "无效文件类型");
		JLFEX_ERROR.put("200011", "客户不存在");
		JLFEX_ERROR.put("200013", "理财产品不存在");
		JLFEX_ERROR.put("200014", "理财产品启募时间未到");
		JLFEX_ERROR.put("200015", "理财产品不在募资中");
		JLFEX_ERROR.put("200016", "理财产品募资已经截止");
		JLFEX_ERROR.put("200020", "无效证件类型");
		JLFEX_ERROR.put("200021", "开户行不存在");
		JLFEX_ERROR.put("200022", "开户行所在省份不存在");
		JLFEX_ERROR.put("200023", "开户行所在城市不存在");
		JLFEX_ERROR.put("200024", "证书申请失败");
		JLFEX_ERROR.put("200025", "验证码不正确");
		JLFEX_ERROR.put("200026", "协议已经签署");
		JLFEX_ERROR.put("200027", "协议签署失败");
		JLFEX_ERROR.put("200028", "验证码已经失效");
		JLFEX_ERROR.put("200029", "证件号格式不正确");
		JLFEX_ERROR.put("200030", "支付失败，请重新支付");
		JLFEX_ERROR.put("200031", "支付失败，请重新支付");
		JLFEX_ERROR.put("200032", "支付失败，请重新支付");
		JLFEX_ERROR.put("200033", "支付失败，请重新支付");
		JLFEX_ERROR.put("200034", "支付超时，请重新下单");
		JLFEX_ERROR.put("200035", "同一个经济会员下，不同的客户不能拥有同一张银行卡");
		JLFEX_ERROR.put("200036", "还款计划不存在");
		JLFEX_ERROR.put("200037", "还款计划类型参数不确");
		JLFEX_ERROR.put("200038", "账户验证异常");
		JLFEX_ERROR.put("200039", "账户验证超时");
		JLFEX_ERROR.put("200040", "账户验证失败");
		JLFEX_ERROR.put("200041", "账户类型不存在");
		JLFEX_ERROR.put("200043", "只有订单为已付款、待付息、等待回款才能变更银行卡");
		JLFEX_ERROR.put("200044", "只能撤销募资中或待付款的理财产品");

		// 补全异常
		JLFEX_ERROR.put("200059", "该银行卡信息已绑定，请勿重复绑定！");
		JLFEX_ERROR.put("200069", "该验证码已认证失败，请重发验证码验证！");
		JLFEX_ERROR.put("300004", "客户证件号码已存在，但客户信息与系统不同！");
		JLFEX_ERROR.put("200045", "只能撤销募资中或待付款的理财产品");
		JLFEX_ERROR.put("200046", "开户行，省份，城市，开户支行,邮编 ,手机号,地址一致 不允许变更");
		JLFEX_ERROR.put("200047", "您的理财产品没有可撤销的额度，无法撤销");
		JLFEX_ERROR.put("200049", "无可变更内容,变更失败");
		JLFEX_ERROR.put("200050", "该订单已处于已撤销状态，不能进行撤单操作！");
		JLFEX_ERROR.put("200051", "该订单处于非可撤销状态，不能进行撤单操作！");
		JLFEX_ERROR.put("200052", "订单不存在");
		JLFEX_ERROR.put("200053", "所属订单状态为非等待回款待付息状态的理财产品无法发起申请");
		JLFEX_ERROR.put("200054", "已失效、募资中、等待募资的理财产品无法发起申请");
		JLFEX_ERROR.put("200055", "提前回购日期不能早于当前日期");
		JLFEX_ERROR.put("200056", "子资产信息不存在");
		JLFEX_ERROR.put("200057", "状态为申请中 不能再次发起");
		JLFEX_ERROR.put("200060", "一分钟内不允许重复提交认证请求");
		JLFEX_ERROR.put("200061", "流水号正在处理中");
		JLFEX_ERROR.put("200062", "目前只支持中金");
		JLFEX_ERROR.put("200063", "目前只支持借记卡");
		JLFEX_ERROR.put("200064", "目前只支持个人账户");
		JLFEX_ERROR.put("200070", "证件号不能为空");
		JLFEX_ERROR.put("200071", "银行账号不能为空");
		JLFEX_ERROR.put("200072", "不支持该证件类型");
		JLFEX_ERROR.put("200073", "支付通道不能为空");
		JLFEX_ERROR.put("200074", "状态为申请成功 不能再次发起");
		JLFEX_ERROR.put("300001", "订单金额超出可买金额");
		JLFEX_ERROR.put("300002", "订单不可撤销");
		JLFEX_ERROR.put("300003", "理财产新品的付款方式不能为银行汇款");
		JLFEX_ERROR.put("300005", "银行卡信息已存在，与系统中已保存信息不一致");
		JLFEX_ERROR.put("300006", "订单创建时间不正确");
		JLFEX_ERROR.put("300007", "银行卡信息不存在");
		JLFEX_ERROR.put("300008", "获取文件协议失败");
		JLFEX_ERROR.put("300009", "查询订单失败");
		JLFEX_ERROR.put("300040", "理财产品不存在");
		JLFEX_ERROR.put("999999", "支付失败，请重新支付");
		JLFEX_ERROR.put("800001", "理财产品不能下单");
		JLFEX_ERROR.put("400005", "提前回购日期不能早于起息日");
		JLFEX_ERROR.put("400006", "提前回购日期不能晚于回购日");
		JLFEX_ERROR.put("999998", "访问开放平台超时！");
		JLFEX_ERROR.put("200058", "账户信息有误，请重新核对");//会员{0}流水号{1}已存在
		
		JLFEX_ERROR.put("200017", "理财产品余额不足，我们会尽快处理");
		JLFEX_ERROR.put("200018", "订单金额低于起购金");
		JLFEX_ERROR.put("200019", "订单金额不是起购金额的整数倍");
		JLFEX_ERROR.put("800002", "根据经纪会员查询订单不存在");
		JLFEX_ERROR.put("200065", "流水号不存在");
		JLFEX_ERROR.put("200066", "流水号认证失败，不能解除");
		JLFEX_ERROR.put("200067", "流水号正在认证，不能解除");
		JLFEX_ERROR.put("200068", "流水号认证成功，不能认证");
		
		JLFEX_ERROR.put("200012", "机构名不匹配");
	}
}

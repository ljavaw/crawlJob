package com.dinfo.crawl.bean;

import java.util.ArrayList;
import java.util.List;

/** brief description
 * <p>Date : 2015年6月2日 下午2:00:56</p>
 * <p>Module : </p>
 * <p>Description: </p>
 * <p>Remark : </p>
 * @author Administrator
 * @version 
 * <p>------------------------------------------------------------</p>
 * <p> 修改历史</p>
 * <p> 序号 日期 修改人 修改原因</p>
 * <p> 1 </p>
 */

public class TableInfo {
	
	public static final String[] TABLENAMES = {"RESOURCE_COMMUNITY_INFO_T","COMMUNITY_BBS_INFO",
		"COMMUNITY_COMMENT_INFO","MERCHANT_INFO_T","MERCHANT_COMMENT_INFO","SHOP_INFO_T"};
	
	//社区索引信息表
	public static final String RESOURCE_COMMUNITY_KEYS_INDEX = "RESOURCE_COMMUNITY_KEYS_INDEX";
	//社区基本信息表
	public static final String RESOURCE_COMMUNITY_INFO_T = "RESOURCE_COMMUNITY_INFO_T";
	//社区论坛索引表
	public static final String COMMUNITY_BBS_INDEX = "COMMUNITY_BBS_INDEX";
	//社区论坛信息
	public static final String COMMUNITY_BBS_INFO = "COMMUNITY_BBS_INFO";
	//社区评论索引表
	public static final String COMMUNITY_COMMENT_INDEX = "COMMUNITY_COMMENT_INDEX";
	//社区评论信息表
	public static final String COMMUNITY_COMMENT_INFO = "COMMUNITY_COMMENT_INFO";
	//商户信息索引表
	public static final String MERCHANT_KEYS_INDEX = "MERCHANT_KEYS_INDEX";
	//商户信息表
	public static final String MERCHANT_INFO_T = "MERCHANT_INFO_T";
	//商户大众点评信息索引表
	public static final String MERCHANT_COMMENT_INDEX = "MERCHANT_COMMENT_INDEX";
	//商户大众点评信息表
	public static final String MERCHANT_COMMENT_INFO = "MERCHANT_COMMENT_INFO";
	//商户点评信息索引表
	public static final String SHOP_INFO_INDEX = "RESOURCE_SHOP_INFO_INDEX";
	//商户点评信息
	public static final String SHOP_INFO_T = "RESOURCE_SHOP_INFO_T";
	
	
	@SuppressWarnings("serial")
	public static final List<String> RESOURCE_COMMUNITY_INFO_T_FAMILYNAME_LIST = new ArrayList<String>(){{add("community");}};
	
	@SuppressWarnings("serial")
	public static final List<String> COMMUNITY_BBS_INFO_FAMILYNAME_LIST = new ArrayList<String>(){{add("bbs");}};
	
	@SuppressWarnings("serial")
	public static final List<String> COMMUNITY_COMMENT_INFO_FAMILYNAME_LIST = new ArrayList<String>(){{add("comment");}};
	
	@SuppressWarnings("serial")
	public static final List<String> MERCHANT_INFO_T_FAMILYNAME_LIST = new ArrayList<String>(){{add("merchantInfo");}};
	
	@SuppressWarnings("serial")
	public static final List<String> MERCHANT_COMMENT_INFO_FAMILYNAME_LIST = new ArrayList<String>(){{add("merchantcommentInfo");}};
	
	@SuppressWarnings("serial")
	public static final List<String> SHOP_INFO_T_FAMILYNAME_LIST = new ArrayList<String>(){{add("shopInfo");}};
	
	
	@SuppressWarnings("serial")
	public static final List<String> RESOURCE_COMMUNITY_INFO_T_CELL_LIST = 
			new ArrayList<String>(){{add("province");add("city");add("country");add("comment_url");add("comment_page_num");
			add("bbs_url");add("bbs_page_num");add("platform_type");add("longitude");add("latitude");
			add("community_name");add("community_address");add("volume_rate");add("green_rate");add("developer");
			add("commercial_district");add("houses");add("households");add("average_housingprice");add("housing_type");
			add("built_time");add("building_number");add("building_type");add("parking_number");add("parking_price");
			add("management_company");add("property_prices");add("decorate_condition");add("link_location");add("launch_time");
			add("presale_permit");add("sales_offices");add("property_address");add("traffic");add("project_supporting");
			add("decoration_materials");add("floor_condition");add("project_introduction");add("area_covered");add("built_area");
			add("start_time");add("end_time");add("agent");add("mortgage_bank");add("project_progress");
			add("property_right");add("parking_info");add("relevant_info");add("baseurl");add("url");
			add("get_time");}};
			
	@SuppressWarnings("serial")
	public static final List<String> COMMUNITY_BBS_INFO_CELL_LIST = 
			new ArrayList<String>(){{add("user_name");add("post_time");add("title");add("content");add("url");
			add("base_url");add("get_time");}};

	@SuppressWarnings("serial")
	public static final List<String> COMMUNITY_COMMENT_INFO_CELL_LIST = 
			new ArrayList<String>(){{add("comment_user");add("comment_time");add("comment_content");add("url");add("base_url");
			add("get_time");}};

	@SuppressWarnings("serial")
	public static final List<String> MERCHANT_INFO_T_CELL_LIST = 
			new ArrayList<String>(){{add("merchant_name");add("community_name");add("base_url");add("province");add("city");
			add("merchant_url");add("merchant_address");add("merchant_tag");add("merchant_tel");add("open_time");
			add("merchant_desc");add("store_introduction");add("feature");add("longitude");add("latitude");
			add("distance");add("comment_num");add("url");add("get_time");}};

	@SuppressWarnings("serial")
	public static final List<String> MERCHANT_COMMENT_INFO_CELL_LIST = 
			new ArrayList<String>(){{add("base_url");add("comment_user");add("comment_id");add("comment_time");add("comment_content");
			add("url");add("env_ass");add("get_time");}};

	@SuppressWarnings("serial")
	public static final List<String> SHOP_INFO_T_CELL_LIST = 
			new ArrayList<String>(){{add("shop_name");add("tel");add("opentime");add("consumption_person");add("flavor");
			add("service");add("environmental_score");add("address");add("cost");add("technology");
			add("price");add("base_url");add("url");add("get_time");}};

}

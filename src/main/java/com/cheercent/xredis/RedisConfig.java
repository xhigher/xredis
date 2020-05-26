package com.cheercent.xredis;

import com.cheercent.xredis.XRedis.RedisKeyBuilder;

public interface RedisConfig {
	
	int EXPIRE_DAY_30 = 2592000;
	int EXPIRE_DAY_7 = 604800;
	int EXPIRE_DAY_1 = 86400;
	int EXPIRE_DAY_3 = 259200;
	int EXPIRE_MIN_1 = 60;
	int EXPIRE_MIN_2 = 120;
	int EXPIRE_MIN_5 = 300;
	int EXPIRE_MIN_10 = 600;
	int EXPIRE_MIN_30 = 1800;
	int EXPIRE_HOUR_1 = 3600;
	int EXPIRE_HOUR_6 = 21800;
	int EXPIRE_SEC_10 = 10;
	
	
	String NODE_USER = "user";
	String NODE_PRODUCT = "product";
	

	RedisKeyBuilder USER_INFO = new RedisKeyBuilder(NODE_USER, "user_info", 0);
	RedisKeyBuilder USER_SESSION = new RedisKeyBuilder(NODE_PRODUCT, "user_session", EXPIRE_DAY_30);

	
	RedisKeyBuilder PRODUCT_INFO = new RedisKeyBuilder(NODE_PRODUCT, "product_info", EXPIRE_DAY_30);
	
	RedisKeyBuilder PRODUCT_PAGE_DATA = new RedisKeyBuilder(NODE_PRODUCT, "product_page_data", EXPIRE_DAY_1);
	RedisKeyBuilder PRODUCT_PAGE_TOTAL = new RedisKeyBuilder(NODE_PRODUCT, "product_page_total", EXPIRE_DAY_1);

	
}



# xtomcat-http-server
基于jedis封装超级易用工具，支持横向扩展

简介
---
  基于jedis封装超级易用工具，支持横向扩展, 简化繁琐的过期时间管理，屏蔽节点关系，不用关心资源获取与释放；


初始化服务

```properties
	redis.status=1

	redis.pool.maxActive=1024
	redis.pool.maxIdle=200
	redis.pool.maxWait=1000
	redis.pool.testOnBorrow=true
	redis.pool.testOnReturn=true

	redis.node.size=2

	redis.node1.name=user
	redis.node1.host=127.0.0.1
	redis.node1.port=6389
	redis.node1.pass=iQ6dSxzGfg
	redis.node1.db=1

	redis.node2.name=product
	redis.node2.host=127.0.0.1
	redis.node2.port=6389
	redis.node2.pass=iQ6dSxzGfg
	redis.node2.db=2
```

```java
	Properties properties = new Properties();
	InputStream is = Object.class.getResourceAsStream(configFile);
	properties.load(is);
	if (is != null) {
		is.close();
	}
	
	XRedis.init(properties);
```
 
业务层
```java
	//config
	String NODE_USER = "user"; //节点-用户
	String NODE_PRODUCT = "product";//节点-商品
	
	//用户信息，永久
	RedisKeyBuilder USER_INFO = new RedisKeyBuilder(NODE_USER, "user_info", 0);

	//用户登录会话信息，30天
	RedisKeyBuilder USER_SESSION = new RedisKeyBuilder(NODE_PRODUCT, "user_session", EXPIRE_DAY_30);

	
	//商品信息，3天
	RedisKeyBuilder PRODUCT_INFO = new RedisKeyBuilder(NODE_PRODUCT, "product_info", EXPIRE_DAY_3);
	
	//商品分页信息，1天
	RedisKeyBuilder PRODUCT_PAGE_DATA = new RedisKeyBuilder(NODE_PRODUCT, "product_page_data", EXPIRE_DAY_1);
	RedisKeyBuilder PRODUCT_PAGE_TOTAL = new RedisKeyBuilder(NODE_PRODUCT, "product_page_total", EXPIRE_DAY_1);


	//demo1：用户信息
    private static void demo1() {
    	String userInfo = null;
    	String userid = "1001";
    	RedisKey redisKey = RedisConfig.USER_INFO.build().append(userid);
    	if(redisKey.exists()) {
    		userInfo = redisKey.get();
    		logger.info("userid={}, user_info = {}", userid, userInfo);
    	}else {
    		userInfo = "{\"userid\":\"1001\",\"name\":\"xhigher\",\"sex\":1}";
    		redisKey.set(userInfo);
    		logger.info("userid={} saved", userid);
    	}
    	
    	userid = "1002";
    	redisKey.reset().append(userid);
    	if(redisKey.exists()) {
    		userInfo = redisKey.get();
    		logger.info("userid={}, user_info = {}", userid, userInfo);
    	}else {
    		userInfo = "{\"userid\":\"1002\",\"name\":\"xhigher2\",\"sex\":0}";
    		redisKey.set(userInfo);
    		logger.info("userid={} saved", userid);
    	}
    	
    	if(redisKey.exists()) {
    		redisKey.del();
    		logger.info("userid={} existed and del", userid);
    	}
    	
    	if(!redisKey.exists()) {
    		logger.info("userid={} not existed", userid);
    	}
    }


    //demo2：商品分页信息查询
    private static void demo2() {
    	int pagesize = 20;
    	int pagenum = 2;
    	int total = 0;
        RedisKey redisKey1 = RedisConfig.PRODUCT_PAGE_TOTAL.build().append(pagesize);
        String redisData = redisKey1.get();
        if (redisData != null) {
            int pageTotal = Integer.parseInt(redisData);
            if (pagenum > pageTotal) {
            	total = 0;
            	StringBuilder pageData = new StringBuilder();
                pageData.append("\"total\":").append(total);
                pageData.append("\"pagenum\"").append(pagenum);
                pageData.append("\"pagesize\"").append(pagesize);
                pageData.append("\"data\":").append("[]");
                logger.info(pageData.toString());
                return;
            }
        }
        String fieldPagenum = String.valueOf(pagenum);
        RedisKey redisKey2 = RedisConfig.PRODUCT_PAGE_DATA.build().append(pagesize).append(pagenum);
        redisData = redisKey2.hget(fieldPagenum);
        if (redisData == null) {
            //JSON data query from DB
        	total = 4;
        	StringBuilder pageData = new StringBuilder();
            pageData.append("\"total\":").append(total);
            pageData.append("\"pagenum\"").append(pagenum);
            pageData.append("\"pagesize\"").append(pagesize);
            pageData.append("\"data\":").append("[{},{},{},{}]");
            
            int pageTotal = (int) Math.ceil(total * 1.0 / pagesize);
            redisKey1.set(String.valueOf(pageTotal));
            redisKey2.hset(fieldPagenum, pageData.toString());
        } else {
        	logger.info(redisData);
        }
    }
```









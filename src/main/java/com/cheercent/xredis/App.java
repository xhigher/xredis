package com.cheercent.xredis;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheercent.xredis.XRedis.RedisKey;

/**
 * Hello world!
 *
 */
public class App {
	
	private static Logger logger = LoggerFactory.getLogger(App.class);
	
	private static String configFile = "/application.properties";
	
    public static void main( String[] args ){
        
    	init();
    	
    	//simple
    	demo1();
    	
    	//page
    	demo2();
    	
    }
    
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
            //JSON result from DB
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
    
    
    private static void init() {
    	try{
			Properties properties = new Properties();
			InputStream is = Object.class.getResourceAsStream(configFile);
			properties.load(is);
			if (is != null) {
				is.close();
			}
			
			XRedis.init(properties);
			
		}catch(Exception e){
			logger.error("XLauncher.Exception:", e);
		}
    }
}

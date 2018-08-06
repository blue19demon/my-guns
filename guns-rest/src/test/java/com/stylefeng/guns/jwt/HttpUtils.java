package com.stylefeng.guns.jwt;

import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.stylefeng.guns.rest.modular.example.ExampleController;

@SuppressWarnings({ "deprecation", "unused" })
public class HttpUtils {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	@SuppressWarnings("resource")
	public static String httpPostWithJson(JSONObject jsonObj,String url,String authorization){
	    boolean isSuccess = false;
	    String header = "Authorization";
	    HttpPost post = null;
	    try {
	        HttpClient httpClient = new DefaultHttpClient();

	        // 设置超时时间
	        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
	        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
	            
	        post = new HttpPost(url);
	        // 构造消息头
	        post.setHeader("Content-type", "application/json; charset=utf-8");
	        post.setHeader("Connection", "Close");
	        String sessionId = getSessionId();
	        post.setHeader("SessionId", sessionId);
	        if(null!=authorization&&!"".equals(authorization)) {
	        	post.setHeader(header, "Bearer "+authorization);
	        }
	                    
	        // 构建消息实体
	        StringEntity entity = new StringEntity(jsonObj.toString(), Charset.forName("UTF-8"));
	        entity.setContentEncoding("UTF-8");
	        // 发送Json格式的数据请求
	        entity.setContentType("application/json");
	        post.setEntity(entity);
	            
	        HttpResponse response = httpClient.execute(post);
	        // 检验返回码
	        int statusCode = response.getStatusLine().getStatusCode();
	        if(statusCode != HttpStatus.SC_OK){
	        	logger.info("请求出错: "+statusCode);
	            return null;
	        }else{
	        	HttpEntity responseEntity = response.getEntity();
            	String jsonString = EntityUtils.toString(responseEntity);
	        	return jsonString;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        isSuccess = false;
	    }finally{
	        if(post != null){
	            try {
	                post.releaseConnection();
	                Thread.sleep(500);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		return null;
	}

	// 构建唯一会话Id
	public static String getSessionId(){
	    UUID uuid = UUID.randomUUID();
	    String str = uuid.toString();
	    return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
	}
}

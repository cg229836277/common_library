package com.chuck.commonlib.util.http;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.chuck.commonlib.util.CollectionUtil;
import com.chuck.commonlib.util.StringUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpRequestUtil {	
	private static OkHttpClient client;
	private final static int WHAT_SUCCESS = 0;
	private final static int WHAT_FAIL = 5;
	//请求的地址为空
	private final static int WHAT_ERROR_URL_EMPTY = 1;
	private static final String ERROR_URL_EMPTY = "url is empty";
	//请求的上传的文件地址为空
	private final static int WHAT_ERROR_FILE_PATH_NULL = 2;
	private static final String ERROR_FILE_PATH_NULL = "file path is empty";
	//请求的上传的文件不存在
	private final static int WHAT_ERROR_FILE_NOT_EXIST = 3;
	private static final String ERROR_FILE_NOT_EXIST = "file do not exist";
	//请求的参数为空
	private final static int WHAT_ERROR_PARAMETERS_EMPTY = 4;
	private static final String ERROR_PARAMETERS_EMPTY = "parameters is empty";
	//网络请求的标签，用于取消请求
	private static String REQUEST_TAG = "get_request";
	
	private final static String REQUEST_FEEDBACK = "request_feedback";
	
	private static HttpRequestCallBackListenner mListenner;
	
	public static interface HttpRequestCallBackListenner{
		public String requestSuccess(String successInfo);
		public String requestFail(String failInfo);
	}
	
	private static Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String data = msg.getData().getString(REQUEST_FEEDBACK);
			switch (msg.what) {			
			case WHAT_SUCCESS:				
				mListenner.requestSuccess(data);
				break;
			case WHAT_ERROR_URL_EMPTY:
				mListenner.requestFail(ERROR_URL_EMPTY);
				break;
			case WHAT_ERROR_FILE_PATH_NULL:
				mListenner.requestFail(ERROR_FILE_PATH_NULL);
				break;
			case WHAT_ERROR_FILE_NOT_EXIST:
				mListenner.requestFail(ERROR_FILE_NOT_EXIST);
				break;
			case WHAT_ERROR_PARAMETERS_EMPTY:
				mListenner.requestFail(ERROR_PARAMETERS_EMPTY);
				break;
			case WHAT_FAIL:
				mListenner.requestFail(data);
				break;
			default:
				break;
			}
		}
	};
	
	private static Request initRequest(String url , RequestBody body){
		if(client == null){
			client = new OkHttpClient();
			client.setConnectTimeout(5, TimeUnit.SECONDS);
		    client.setWriteTimeout(5, TimeUnit.SECONDS);
		    client.setReadTimeout(10, TimeUnit.SECONDS);
		}
		if(body == null){
			if(StringUtil.isEmpty(REQUEST_TAG)){
				return new Request.Builder().url(url).build();
			}
			return new Request.Builder().tag(REQUEST_TAG).url(url).build();
		}else{
			if(StringUtil.isEmpty(REQUEST_TAG)){
				return new Request.Builder().url(url).post(body).build();
			}
			return new Request.Builder().tag(REQUEST_TAG).url(url).post(body).build();
		}		
	}
	
	private static boolean isUrlEmpty(String url){
		if (StringUtil.isEmpty(url)) {
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * get请求
	 * 请在异步线程操作
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String getRequest(String url , String requestTag) throws IOException{
		if (isUrlEmpty(url)) {
			return ERROR_URL_EMPTY;
		}
		
		if(!StringUtil.isEmpty(requestTag)){
			REQUEST_TAG = requestTag;
		}
		
		Request request = initRequest(url , null);
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			cancelRequest();
			throw new IOException("Unexpected code " + response);
		}
	}
	
	/**
	 * 异步请求，请求结果在主线程
	 * get异步请求
	 * @param url
	 * @throws Exception
	 */
	public static void getAsyncRequest(String url , String requestTag , final HttpRequestCallBackListenner listenner) throws Exception{
		mListenner = listenner;
		
		if (isUrlEmpty(url)) {
			dealWithRequestResult(WHAT_ERROR_URL_EMPTY, ERROR_URL_EMPTY);
			return;
		}	
		
		if(!StringUtil.isEmpty(requestTag)){
			REQUEST_TAG = requestTag;
		}
		
		Request request = initRequest(url , null);
	    client.newCall(request).enqueue(new Callback() {	
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				dealWithRequestResult(WHAT_FAIL, arg1.getMessage());	
				arg1.printStackTrace();
				cancelRequest();
			}
	
			@Override
			public void onResponse(Response arg0) throws IOException {
				if (!arg0.isSuccessful()){
					cancelRequest();
					throw new IOException("Unexpected code " + arg0);				
				}
				String result = arg0.body().string();
				dealWithRequestResult(WHAT_SUCCESS, result);				
			}	      
		});
	}	
	/**
	 * post请求，带json
	 * 请在异步线程操作
	 * @author admin
	 * @date 2015-10-19 下午2:27:39
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String postRequest(String url, String json , String requestTag) throws IOException {
		RequestBody body = RequestBody.create(HttpContentType.MEDIA_TYPE_JSON, json);
		if (isUrlEmpty(url)) {
			return ERROR_URL_EMPTY;
		}
		
		if(!StringUtil.isEmpty(requestTag)){
			REQUEST_TAG = requestTag;
		}

		Request request = initRequest(url,body);
		Response response = client.newCall(request).execute();
		if(!response.isSuccessful()){
			cancelRequest();
			throw new IOException("Unexpected code " + response);
		}
		return response.body().string();
	}
	
	/**
	 * 获取请求参数
	 * 
	 * @author admin
	 * @date 2015-10-19 下午2:27:58
	 * @param params
	 * @return
	 */
	private static RequestBody getRequestBody(Map<String , String> params){
		FormEncodingBuilder builder= new FormEncodingBuilder();
				
		Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();  		  
		while (entries.hasNext()) {  		  
		    Map.Entry<String, String> entry = entries.next();  
		    builder.add(entry.getKey(), entry.getValue());
		}
		RequestBody body = builder.build();
		return body;
	}
	
	/**
	 * 带参数的post请求
	 * 请在异步线程操作
	 * @author admin
	 * @date 2015-10-19 下午2:28:12
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String postParameters(String url , Map<String , String> params , String requestTag) throws Exception{
		if (isUrlEmpty(url)) {
			return ERROR_URL_EMPTY;
		}

		if (CollectionUtil.isMapNull(params)) {
			return ERROR_PARAMETERS_EMPTY;
		}
		
		if(!StringUtil.isEmpty(requestTag)){
			REQUEST_TAG = requestTag;
		}

		RequestBody body = getRequestBody(params);
		Request request = initRequest(url, body);

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()){
			cancelRequest();
			throw new IOException("Unexpected code " + response);
		}

		return response.body().string();
	}
	
	/**
	 * 上传文件,注意设置上传监听
	 * 请在异步线程操作，请求结果在主线程
	 * @author admin
	 * @date 2015-10-19 下午2:28:31
	 * @param url
	 * @param filePath
	 * @param fileType 常量参数位于HttpContentType函数内
	 * @throws Exception
	 */
	public static void postFile(String url , String filePath , MediaType fileType , String requestTag , final HttpRequestCallBackListenner listenner) throws Exception{
		mListenner = listenner;
		
		if(StringUtil.isEmpty(url)){
			dealWithRequestResult(WHAT_ERROR_URL_EMPTY, ERROR_URL_EMPTY);
		}		
		if(StringUtil.isEmpty(filePath)){
			dealWithRequestResult(WHAT_ERROR_FILE_PATH_NULL, ERROR_FILE_PATH_NULL);
		}		
		File file = new File(filePath);
		if(!file.exists()){
			dealWithRequestResult(WHAT_ERROR_FILE_NOT_EXIST, ERROR_FILE_NOT_EXIST);
		}
		
		if(!StringUtil.isEmpty(requestTag)){
			REQUEST_TAG = requestTag;
		}
		
		RequestBody body = RequestBody.create(fileType, file);
		Request request = initRequest(url, body);
		try {
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) {
				dealWithRequestResult(WHAT_FAIL , "Unexpected code " + response);
				cancelRequest();
			} else {
				dealWithRequestResult(WHAT_SUCCESS, response.body().string());	
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			cancelRequest();
		}
	}
	
	private static void dealWithRequestResult(int what , String result){
		if(!StringUtil.isEmpty(result) && mListenner != null){
			Message message = handler.obtainMessage();
			message.what = what;
			Bundle data = new Bundle();
			data.putString(REQUEST_FEEDBACK, result);
			message.setData(data);
			handler.sendMessage(message);
		}
	}
	
	public static void cancelRequest(){
		if(client != null){
			client.cancel(REQUEST_TAG);
			REQUEST_TAG = null;
		}
	}
}

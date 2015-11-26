package com.chuck.commonlib.util.http;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;

import com.chuck.commonlib.util.NetworkUtil;
import com.chuck.commonlib.util.StringUtil;
import com.j256.ormlite.stmt.query.Between;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

public class HttpDownloadUtil {
	private final static int CONNECT_TIMEOUT = 10* 1000;
	private static DecimalFormat decimalFormat = new DecimalFormat(".00");
	private static Future<File> downloading;
	
	private static final String ERROR_FILE_WEB_WRONG = "can not read file or web is wrong";
	private static final String ERROR_URL_FILE_PATH_NULL = "file or web path is null";
	private static final String ERROR_OCCUR_EXCEPTION = "meet exception during download";
	private static final String ERROR_NETWORK_NOT_AVAILABLE = "network can not be accessed";
	
	private Context mContext;
	
	public HttpDownloadUtil(Context context){
		mContext = context;
	}
	
	private static DownloadListenner mDownloadListenner;
	public interface DownloadListenner{;
		public void downloadProgress(String percent);
		public void downloadSuccess(String filePath);
		public void downloadFail(String errorInfo);
	}
	
	public void setDownloadListenner(DownloadListenner listenner){
		mDownloadListenner = listenner;
	}
	
	private static boolean isListennerNull(){
		if(mDownloadListenner == null){
			return true;
		}		
		return false;
	}
	
	/**
	 * 下载文件
	 * 
	 * @author admin
	 * @date 2015-10-21 下午2:42:13
	 * @param context
	 * @param url 下载地址
	 * @param filePath 文件存贮地址，包含文件名
	 */
	public void downloadFile(String url , String filePath){
		if(StringUtil.isEmpty(url, filePath)){
			if(!isListennerNull()){
				mDownloadListenner.downloadFail(ERROR_URL_FILE_PATH_NULL);
			}
			return;
		}
		
		if(!NetworkUtil.isNetworkAvailable(mContext)){
			if(!isListennerNull()){
				mDownloadListenner.downloadFail(ERROR_NETWORK_NOT_AVAILABLE);
			}
			return;
		}
			
		downloading = Ion.with(mContext.getApplicationContext()).load(url)
		.setTimeout(CONNECT_TIMEOUT).progressHandler(new ProgressCallback() {			
			@Override
			public void onProgress(long arg0, long arg1) {
				if(arg0 < 0l){
					if(!isListennerNull()){
						mDownloadListenner.downloadFail(ERROR_FILE_WEB_WRONG);						
					}
					downloading.cancel();
				}
				
				String progress = decimalFormat.format(100 * ((float)arg0/(float)arg1));
				if(!isListennerNull()){
					mDownloadListenner.downloadProgress(progress);
				}
			}
		}).write(new File(filePath)).setCallback(new FutureCallback<File>() {
	        @Override
	        public void onCompleted(Exception e, File result) {
	        	if(e != null){
	        		if(!isListennerNull()){
						mDownloadListenner.downloadFail(ERROR_OCCUR_EXCEPTION);						
	        		}	        		
	        	}
	        	if(!isListennerNull()){
        			mDownloadListenner.downloadSuccess(result.getAbsolutePath());
        		}
	        }
	    });
	}
	
	public void cancelDownload(){
		if(downloading != null){
			downloading.cancel();
		}
	}
}
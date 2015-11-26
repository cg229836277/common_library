package com.chuck.commonlib.base;

import java.io.File;

import com.chuck.commonlib.R;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;

public class CommonLibInit{
	
	private File cacheDir;
	
	private ImageLoader imageLoader;
	private Context mContext;
	
	private Class[] clazz;
	private int databaseVersion;
	private String databasePath;
	private String databaseName;
	
	private static CommonLibInit instance;
	
	public static CommonLibInit getInstance(){
		if(instance == null){
			instance = new CommonLibInit();
		}	
		return instance;
	}
	
	public void init(Context context){
		mContext = context;
		cacheDir = StorageUtils.getOwnCacheDirectory(mContext, "commonlib/pictures");
		initImageLoader();
	}
	
	private void initImageLoader(){
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnFail(R.drawable.ic_launcher)
        .showImageOnLoading(R.drawable.ic_launcher)
        .build();		
		
		ImageLoaderConfiguration options = new ImageLoaderConfiguration.Builder(mContext)
		.memoryCacheSize(2 * 1024 * 1024)
		.imageDownloader(new BaseImageDownloader(mContext,5 * 1000,30 * 1000))
		.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		.threadPoolSize(5)                         // default  
		.threadPriority(Thread.NORM_PRIORITY - 1)   // default  
		.tasksProcessingOrder(QueueProcessingType.FIFO) // default 
		.defaultDisplayImageOptions(displayOptions)
		.diskCache(new LimitedAgeDiskCache(cacheDir , 7 * 24 * 60 * 60))
		.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(options);
		imageLoader.clearMemoryCache();
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public Class[] getClazz() {
		return clazz;
	}

	public void setClazz(Class[] clazz) {
		this.clazz = clazz;
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public String getDatabasePath() {
		return databasePath;
	}

	public void setDatabasePath(String databasePath) {
		this.databasePath = databasePath;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}

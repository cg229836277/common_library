package com.chuck.commonlib.util;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.chuck.commonlib.base.CommonLibInit;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class BitmapUtil {
	
	private static CommonLibInit application = CommonLibInit.getInstance();
	
	/**
	 * 加载图片
	 * 
	 * @author admin
	 * @date 2015-10-19 下午5:31:32
	 * @param imagePath 在线地址或者本地sd卡地址
	 * @param imageView
	 */
	public static void displayImage(String imagePath, ImageView imageView){
		if(StringUtil.isEmpty(imagePath)){
			return;
		}
		
		if(!URLUtil.isNetworkUrl(imagePath)){
			imagePath = "file://" + imagePath;
			
		}
		application.getImageLoader().displayImage(imagePath, imageView);
	}
	
	/**
	 * 异步记载图片，本地sd卡或者网络地址
	 * 
	 * @author admin
	 * @date 2015-10-19 下午5:31:58
	 * @param imagePath
	 * @param imageView
	 */
	public static void displayImageAsync(String imagePath , ImageView imageView){
		if(StringUtil.isEmpty(imagePath)){
			return;
		}
		
		if(!URLUtil.isNetworkUrl(imagePath)){
			imagePath = "file://" + imagePath;
			
		}
		Bitmap bitmap = application.getImageLoader().loadImageSync(imagePath);
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * 加载本地或者在线图片，可以设置加载大小
	 * 
	 * @author admin
	 * @date 2015-10-19 下午5:33:02
	 * @param imagePath
	 * @param size
	 * @param imageView
	 */
	public static void displayImage(String imagePath , ImageSize size , final ImageView imageView){
		if(StringUtil.isEmpty(imagePath)){
			return;
		}
		
		if(!URLUtil.isNetworkUrl(imagePath)){
			imagePath = "file://" + imagePath;
			
		}
		
		application.getImageLoader().loadImage(imagePath, size, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
				application.getImageLoader().cancelDisplayTask(imageView);
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				imageView.setImageBitmap(loadedImage);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				
			}
		});
	}
	
	/**
	 * 异步加载在线或者本地sd卡图片，可以设置要设置的图片大小
	 * 
	 * @author admin
	 * @date 2015-10-19 下午5:33:35
	 * @param imagePath
	 * @param imageSize
	 * @param imageView
	 */
	public static void displayImageAsync(String imagePath , ImageSize imageSize , final ImageView imageView){
		if(StringUtil.isEmpty(imagePath)){
			return;
		}
		
		if(!URLUtil.isNetworkUrl(imagePath)){
			imagePath = "file://" + imagePath;
			
		}
		Bitmap bitmap = application.getImageLoader().loadImageSync(imagePath, imageSize);
		imageView.setImageBitmap(bitmap);		
	}
	
	public static Bitmap getBitmapFromView(View sourceView) {
		if (sourceView == null) {
			return null;
		}
		
		sourceView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		sourceView.layout(0, 0, sourceView.getMeasuredWidth(), sourceView.getMeasuredHeight());
		sourceView.buildDrawingCache();
        Bitmap bitmap = sourceView.getDrawingCache();

        return bitmap;
	}
}

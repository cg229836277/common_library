package com.chuck.commonlib.util.http;

import com.squareup.okhttp.MediaType;

public class HttpContentType {
	/**
	 * 不清楚请求类型
	 */
	public static final MediaType MEDIA_TYPE_ALL = MediaType.parse("application/octet-stream; charset=utf-8");
	/**
	 * 图片png格式
	 */
	public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("application/x-png; charset=utf-8");
	/**
	 * 图片jpg格式
	 */
	public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("application/x-jpg; charset=utf-8");
	/**
	 * 图片jpeg格式
	 */
	public static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg; charset=utf-8");
	/**
	 * mp4格式
	 */
	public static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mpeg4; charset=utf-8");
	/**
	 * txt格式
	 */
	public static final MediaType MEDIA_TYPE_TXT = MediaType.parse("text/plain; charset=utf-8");
	/**
	 * xml格式
	 */
	public static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=utf-8");
	/**
	 * apk格式
	 */
	public static final MediaType MEDIA_TYPE_APK = MediaType.parse("application/vnd.android.package-archive; charset=utf-8");
	/**
	 * doc格式
	 */
	public static final MediaType MEDIA_TYPE_DOC = MediaType.parse("application/msword; charset=utf-8");	
	/**
	 * ppt格式
	 */
	public static final MediaType MEDIA_TYPE_PPT = MediaType.parse("application/vnd.ms-powerpoint; charset=utf-8");	
	/**
	 * json格式
	 */
	public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
}

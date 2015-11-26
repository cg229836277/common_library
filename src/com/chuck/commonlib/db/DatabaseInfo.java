package com.chuck.commonlib.db;

/**
 * 包含版本，名字，路径，实体
 * @Title：云屋科技
 * @Description：
 * @date 2015-10-20 下午5:24:55
 * @author admin
 * @version 1.0
 */
public class DatabaseInfo{		
	public DatabaseInfo(){		
	}
	/**
	 * 表的名称class
	 */
	private Class[] clazz;
	/**
	 * 表的版本
	 */
	private int version;
	/**
	 * 表的绝对路径
	 */
	private String databasePath;
	/**
	 * 表的名称
	 */
	private String databaseName;
	/**
	 * 是否更新表
	 */
	private boolean willUpdate;
	public Class[] getClazz() {
		return clazz;
	}
	public void setClazz(Class[] clazz) {
		this.clazz = clazz;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
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
	public boolean isWillUpdate() {
		return willUpdate;
	}
	public void setWillUpdate(boolean willUpdate) {
		this.willUpdate = willUpdate;
	}
}
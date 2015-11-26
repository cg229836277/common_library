package com.chuck.commonlib.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.chuck.commonlib.base.CommonLibInit;
import com.chuck.commonlib.util.StringUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


/**
	*1.修改数据表名 
	*ALTER TABLE [方案名.]OLD_TABLE_NAME RENAME TO NEW_TABLE_NAME; 
	*2.修改列名 
	*ALTER TABLE [方案名.]TABLE_NAME RENAME COLUMN OLD_COLUMN_NAME TO NEW_COLUMN_NAME; 
	*3.修改列的数据类型 
	*ALTER TABLE [方案名.]TABLE_NAME MODIFY COLUMN_NAME NEW_DATATYPE; 
	*4.插入列 
	*ALTER TABLE [方案名.]TABLE_NAME ADD COLUMN_NAME DATATYPE; 
	*5.删除列 
	*ALTER TABLE [方案名.]TABLE_NAME DROP COLUMN COLUMN_NAME;
 * @Title：云屋科技
 * @Description：操作数据库之前请先确认调用了DaoUtil初始化
 * @date 2015-10-22 上午11:03:49
 * @author admin
 * @version 1.0
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {	
	
	private static DatabaseHelper helper = null;
	
	private static final AtomicInteger usageCounter = new AtomicInteger(0);
	
	private static Class[] mTables;
	
	private static String mDatabasePath;
	
	private static CommonLibInit application = CommonLibInit.getInstance();
	
	private static SQLiteDatabase writableDatabase;
	private static SQLiteDatabase readableDatabase;
	
	public DatabaseHelper(Context context) {
		super(context,application.getDatabaseName(), null, application.getDatabaseVersion());
		mDatabasePath = application.getDatabasePath();
		creatDatabaseFile();
	}
	
	public static synchronized DatabaseHelper getHelper(Context context){
		if(helper == null){
			helper = new DatabaseHelper(context);
		}
		return helper;
	}
	
	private void creatDatabaseFile(){
		File file = new File(mDatabasePath);
		if (!file.exists()||!file.isFile()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file, null);
			if (sqLiteDatabase!=null&&sqLiteDatabase.isOpen()) {
				sqLiteDatabase.close();
			}
			onCreate(getWritableDatabase(),getConnectionSource());
		}else {
			return ;
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			mTables = application.getClazz();
			if(mTables != null && mTables.length > 0){
				for(int i = 0 ; i < mTables.length ; i++){
					TableUtils.createTable(connectionSource, mTables[i]);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			mTables = application.getClazz();
			if(mTables != null && mTables.length > 0){
				for(int i = 0 ; i < mTables.length ; i++){
					TableUtils.dropTable(connectionSource, mTables[i], true);
				}			
			}
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (writableDatabase==null||!writableDatabase.isOpen()) {
			writableDatabase = SQLiteDatabase.openDatabase(mDatabasePath, null,SQLiteDatabase.OPEN_READWRITE);
		}
		return writableDatabase;
	}
		
	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
 		return SQLiteDatabase.openDatabase(mDatabasePath, null,SQLiteDatabase.OPEN_READONLY);
	}
	
	@Override
	public void close() {
		if (usageCounter.decrementAndGet() == 0) {
			super.close();
			helper = null;
		}
	}

	/**
	 * 更新升级数据库，包括删除，添加列，修改列的数据类型，修改表的名字
	 * 
	 * @author admin
	 * @date 2015-10-22 上午11:07:57
	 * @param updateType 要更新的类型 ， 直接查看这个类的public静态变量
	 * @param tableName 要添加表的名字
	 * @param columnName 要添加列的名字
	 * @param dataType 所属字段的数据类型"String" , "Integer" , "Boolean"
	 */
	public void addColumn(String tableName ,String columnName , String dataType){
		String tempDataType = null;
		String defaultValue = null;
		if(!StringUtil.isEmpty(tableName, columnName) && dataType != null){
			if(dataType.equals("String")){
				tempDataType = "String";
				defaultValue = null;
			}else if(dataType.equals("Integer")){
				tempDataType = "Integer";
				defaultValue = "0";
			}else if(dataType.equals("Boolean")){
				tempDataType = "Boolean";
				defaultValue = "0";
			}
		}
		if(!StringUtil.isEmpty(tempDataType)){
			getWritableDatabase().execSQL("alter table " + tableName + " add " + columnName + " " + tempDataType + " default " + defaultValue);			
		}
	}
	
	/**
	 * 删除表中的某一列
	 * 
	 * @author admin
	 * @date 2015-10-22 下午4:00:41
	 * @param tableName
	 * @param columnName
	 */
	public void deleteColumn(String tableName , String columnName){
		if(!StringUtil.isEmpty(tableName, columnName)){
			getWritableDatabase().execSQL("alter table " + tableName + " drop column " + columnName);
		}		
	}
	/**
	 * 修改表中某一列的数据类型
	 * 
	 * @author admin
	 * @date 2015-10-22 下午4:00:58
	 * @param tableName
	 * @param columnName
	 * @param tempDataType
	 */
	public void changeColumnType(String tableName , String columnName , String tempDataType){
		if(!StringUtil.isEmpty(tableName) && !StringUtil.isEmpty(columnName, tempDataType)){
			getWritableDatabase().execSQL("alter table " + tableName + " modify " + columnName + " " + tempDataType);
		}		
	}
	
	/**
	 * 修改某个表的名字
	 * 
	 * @author admin
	 * @date 2015-10-22 下午4:01:15
	 * @param oldTableName
	 * @param newTableName
	 */
	public void changeTableName(String oldTableName , String newTableName){
		if(!StringUtil.isEmpty(oldTableName, newTableName)){
			getWritableDatabase().execSQL("alter table " + oldTableName + " rename to " + newTableName);
		}
	}
}

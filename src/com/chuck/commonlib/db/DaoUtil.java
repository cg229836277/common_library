package com.chuck.commonlib.db;

import com.chuck.commonlib.base.CommonLibInit;

import android.content.Context;

public class DaoUtil {
    /**
     * 全局对象
     */
    private static DatabaseHelper mDatabaseHelper;
    private static Context mContext;
    private static CommonLibInit application;
    /**
     * @param 初始化数据库
     */
    public static void init(DatabaseInfo info) {     
       application = CommonLibInit.getInstance();
       application.setDatabaseVersion(info.getVersion());
       application.setClazz(info.getClazz());
       application.setDatabasePath(info.getDatabasePath());
       application.setDatabaseName(info.getDatabaseName());
    }
}
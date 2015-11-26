package com.chuck.commonlib.db;

import android.database.sqlite.SQLiteDatabase;

public class Transaction {
	private static Transaction transaction;
	private static SQLiteDatabase mDataBase;
	public synchronized static Transaction getTransaction(SQLiteDatabase dataBase){
		if(transaction == null){
			transaction = new Transaction();
		}
		mDataBase = dataBase;
		return transaction;
	}
	
	public void begainTransaction(){
		mDataBase.beginTransaction();
	}
	
	public void commit(){
		mDataBase.setTransactionSuccessful();
	}
	
	public void endTransaction(){
		mDataBase.endTransaction();
	}
	
}

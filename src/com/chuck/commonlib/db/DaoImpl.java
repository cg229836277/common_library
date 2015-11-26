package com.chuck.commonlib.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.chuck.commonlib.util.CollectionUtil;
import com.j256.ormlite.dao.Dao;

/**
 * 注意多个数据库，大数据量处理使用事务Transaction
 * @Title：云屋科技
 * @Description：
 * @date 2015-10-20 下午5:36:48
 * @author admin
 * @param <T> 实体类
 * @param <ID> 实体id
 * @version 1.0
 */
public class DaoImpl<T, ID>{
	private DatabaseHelper mHelper;
	private Dao<T, ID> mDao;
		
	public DaoImpl(Class<T> clazz , DatabaseHelper helper){
		mHelper = helper;
		try {
			mDao = mHelper.getDao(clazz);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过id查找
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:31:30
	 * @param id
	 * @return
	 */
	public T queryById(ID id){
		if(isParasNull(id)){
			return null;
		}
		
		try {
			return mDao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 多个变量查找，分为<key , value>键值对形式
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:33:17
	 * @param data
	 * @return
	 */
	public List<T> queryByFileds(Map<String , Object> data){
		if(CollectionUtil.isMapNull(data)){
			return null;
		}
		
		if(isDaoNull()){
			return null;
		}
		try {
			return mDao.queryForFieldValues(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查找所有的数据
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:31:43
	 * @return
	 */
	public List<T> queryForAll(){
		if(isDaoNull()){
			return null;
		}
		
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 直接保存
	 * 
	 * @author admin
	 * @date 2015-10-21 上午11:33:20
	 * @param data
	 * @return
	 */
	public boolean save(T data){
		if(isDaoNull()){
			return false;
		}
		
		try {
			mDao.create(data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 保存，如果不存在该数据就创建并保存，存在的话更新
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:31:55
	 * @param data
	 * @return
	 */
	public boolean saveOrUpdate(T data){
		if(isDaoNull()){
			return false;
		}
		
		try {
			T tempData = mDao.queryForSameId(data);
			if(tempData != null){
				mDao.update(data);
			}else{
				mDao.create(data);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 多个数据保存
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:59:37
	 * @param data
	 * @return
	 */
	public boolean save(List<T> data){
		if(isDaoNull()){
			return false;
		}
		
		if(CollectionUtil.isListNull(data)){
			return false;
		}
		
		boolean isSuccess = true;
		
		for(T tempData : data){
			if(tempData != null){
				isSuccess &= save(tempData);
			}
		}
		return isSuccess;
	}
	
	/**
	 * 更新
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:59:50
	 * @param data
	 * @return
	 */
	public boolean update(T data){
		if(isDaoNull()){
			return false;
		}
		try {
			T tempData = mDao.queryForSameId(data);
			if(tempData != null){
				mDao.update(data);
			}else{
				mDao.create(data);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 多个数据更新
	 * 
	 * @author admin
	 * @date 2015-10-21 上午10:59:59
	 * @param data
	 * @return
	 */
	public boolean update(List<T> data){
		if(isDaoNull()){
			return false;
		}
		
		if(CollectionUtil.isListNull(data)){
			return false;
		}
		
		boolean isSuccess = true;
		
		for(T tempData : data){
			if(tempData != null){
				isSuccess &= update(tempData);
			}
		}
		return isSuccess;
	}
	
	/**
	 * 删除
	 * 
	 * @author admin
	 * @date 2015-10-21 上午11:00:11
	 * @param data
	 * @return
	 */
	public boolean delete(T data){
		if(isDaoNull()){
			return false;
		}
		try {
			mDao.delete(data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 数据按照id删除
	 * 
	 * @author admin
	 * @date 2015-10-21 上午11:00:17
	 * @param id
	 * @return
	 */
	public boolean deleteById(ID id){
		if(isDaoNull()){
			return false;
		}
		try {
			mDao.deleteById(id);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 多个数据按照id删除
	 * 
	 * @author admin
	 * @date 2015-10-21 上午11:00:43
	 * @param data
	 * @return
	 */
	public boolean deleteByIds(List<ID> data){
		if(isDaoNull()){
			return false;
		}
		
		if(CollectionUtil.isListNull(data)){
			return false;
		}
		
		try {
			mDao.deleteIds(data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取事务，便于快速操作以及失败后回滚
	 * 
	 * @author admin
	 * @date 2015-10-21 上午11:00:50
	 * @param helper
	 * @return
	 */
	public Transaction getTransaction(){
		return Transaction.getTransaction(mHelper.getWritableDatabase());
	}
	
	private boolean isParasNull(ID id){
		if(id == null || mDao == null){
			return true;
		}		
		return false;
	}
	
	private boolean isDaoNull(){
		if(mDao == null){
			return true;
		}	
		return false;
	}
	
}

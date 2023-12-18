package com.ejie.x38.webdav.locking;

import java.util.List;

import org.springframework.stereotype.Service;



/**
 * @author UDA
 */
@Service(value = "dataBaseLockService")
public class DataBaseLockServiceImpl implements DataBaseLockService {

	private DataBaseLockDao dataBaseLockDao;
	
	@Override
	public DataBaseLockedObject getLockedObjectByPath(String path) {
		return this.getLockedObjectByPath(path, Boolean.TRUE);
	}
	
	@Override
	public DataBaseLockedObject getLockedObjectByPath(String path, Boolean tempLock) {
		 List<DataBaseModel> listDataBaseModel = this.dataBaseLockDao.getByPath(path, tempLock);
		 
		 if (listDataBaseModel.isEmpty()){
			 return null;
		 }else{
			 return new DataBaseLockedObject(listDataBaseModel);
		 }
		 
	}
	
	@Override
	public DataBaseLockedObject getLockedObjectById(String id, Boolean tempLock) {
		List<DataBaseModel> listDataBaseModel = this.dataBaseLockDao.getById(id, tempLock);
		 
		 if (listDataBaseModel.isEmpty()){
			 return null;
		 }else{
			 return new DataBaseLockedObject(listDataBaseModel);
		 }
	}

	@Override
	public Boolean isLockedByPath(String path, Boolean tempLock) {
		return dataBaseLockDao.isLockedByPath(path, tempLock);
	}
	
	@Override
	public Boolean isLockedById(String id, Boolean tempLock) {
		return dataBaseLockDao.isLockedById(id, tempLock);
	}

	@Override
	public void addLockedObject(DataBaseLockedObject webdavLockObj) {
		
		dataBaseLockDao.insert(webdavLockObj.getDataBaseModel());
		
	}

	@Override
	public void unlockObjectById(String id, Boolean tempLock) {
		dataBaseLockDao.deleteById(id, tempLock);
		
	}
	
	@Override
	public void unlockObjectByPath(String path, Boolean tempLock) {
		dataBaseLockDao.deleteByPath(path, tempLock);
		
	}
	

	@Override
	public void removeTimeoutLocks(Boolean tempLock) {
		dataBaseLockDao.removeTimeoutLocks(tempLock);
	}

	public void setDataBaseLockDao(DataBaseLockDao dataBaseLockDao) {
		this.dataBaseLockDao = dataBaseLockDao;
	}
	
	
}



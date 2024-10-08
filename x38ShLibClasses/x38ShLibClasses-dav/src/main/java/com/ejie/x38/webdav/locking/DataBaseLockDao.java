package com.ejie.x38.webdav.locking;

import java.util.List;




/**
 * @author UDA
 */

public interface DataBaseLockDao {

	void insert(DataBaseModel webdavLockObj);
	
	void delete(DataBaseModel webdavLockObj);
	
	public List<DataBaseModel> getByPath(String path, Boolean tempLock);
	
	public List<DataBaseModel> getById(String id, Boolean tempLock);

	Boolean isLockedByPath(String path, Boolean tempLock);
	
	Boolean isLockedById(String id, Boolean tempLock);

	void deleteById(String id, Boolean tempLock);
	
	void deleteByPath(String path, Boolean tempLock);
	
	void removeTimeoutLocks(Boolean tempLock);
}



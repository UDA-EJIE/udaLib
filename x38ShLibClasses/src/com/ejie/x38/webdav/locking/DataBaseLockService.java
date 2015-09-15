package com.ejie.x38.webdav.locking;




/**
 * @author UDA
 */

public interface DataBaseLockService {

	
	public DataBaseLockedObject getLockedObjectByPath(String path);
	
	public DataBaseLockedObject getLockedObjectByPath(String path, Boolean temp);
	
	public DataBaseLockedObject getLockedObjectById(String id, Boolean temp);
	
	Boolean isLockedByPath(String path, Boolean tempLock);
	
	Boolean isLockedById(String id, Boolean tempLock);
	
	void addLockedObject(DataBaseLockedObject webdavLockObj);
	
	void unlockObjectById(String id, Boolean tempLock);
	
	void unlockObjectByPath(String path, Boolean tempLock);
	
	void removeTimeoutLocks(Boolean tempLock);
    
}



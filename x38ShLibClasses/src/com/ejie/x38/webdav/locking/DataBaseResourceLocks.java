/*
 * Copyright 2005-2006 webdav-servlet group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ejie.x38.webdav.locking;

import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ejie.x38.webdav.ITransaction;
import com.ejie.x38.webdav.exceptions.LockFailedException;

/**
 * 
 * @author UDA
 */
@Component(value="dataBaseResourceLocks")
public class DataBaseResourceLocks extends SpringBeanAutowiringSupport implements IResourceLocks {

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(DataBaseResourceLocks.class);

    private boolean _temporary = true;

    protected DataBaseLockService dataBaseLockService;
    
    public synchronized boolean lock(ITransaction transaction, String path,
            String owner, boolean exclusive, int depth, int timeout,
            boolean temporary) throws LockFailedException {

    	LOG.debug("DataBaseResourceLocks.lock()");
    	
        DataBaseLockedObject lo = null;
        if (temporary) {
            lo = generateTempLockedObjects(transaction, path);
            lo._type = "read";
        } else {
            lo = generateLockedObjects(transaction, path);
            lo._type = "write";
        }
        
        if (lo.checkLocks(exclusive, depth)) {

            lo._exclusive = exclusive;
            lo._lockDepth = depth;
            lo._expiresAt = System.currentTimeMillis() + (timeout * 1000);
            if (lo._parent != null) {
                lo._parent._expiresAt = lo._expiresAt;
//                if (lo._parent.equals(_root)) {
//                if (lo._parent.getPath().equals("/")) {
//                	DataBaseLockedObject rootLo = getLockedObjectByPath(transaction,
//                            "/");
//                    rootLo._expiresAt = lo._expiresAt;
//                } else if (lo._parent.equals(_tempRoot)) {
//                } else if (lo._parent.getPath().equals("/")) {
//                	DataBaseLockedObject tempRootLo = getTempLockedObjectByPath(
//                            transaction, "/");
//                    tempRootLo._expiresAt = lo._expiresAt;
//                }
            }
            if (lo.addLockedObjectOwner(owner)) {
            	dataBaseLockService.addLockedObject(lo);
                return true;
            } else {
                LOG.trace("Couldn't set owner \"" + owner
                        + "\" to resource at '" + path + "'");
                return false;
            }
        } else {
            LOG.trace("Lock resource at " + path + " failed because"
                    + "\na parent or child resource is currently locked");
            return false;
        }
    	
    }

    public synchronized boolean unlock(ITransaction transaction, String id,
            String owner) {

    	LOG.debug("DataBaseResourceLocks.unlock()");
    	
    	if (dataBaseLockService.isLockedById(id, Boolean.FALSE)){
    		dataBaseLockService.unlockObjectById(id, Boolean.FALSE);
    	}
    	
    	this.checkTimeouts(transaction,  Boolean.FALSE);
    	
    	return true;
    }

    public synchronized void unlockTemporaryLockedObjects(
            ITransaction transaction, String path, String owner) {
    	
    	LOG.debug("DataBaseResourceLocks.unlockTemporaryLockedObjects()");
    	
    	if (dataBaseLockService.isLockedByPath(path, Boolean.TRUE)){
    		dataBaseLockService.unlockObjectByPath(path, Boolean.TRUE);
    	}
    	
    	this.checkTimeouts(transaction,  Boolean.TRUE);
    }

    public void checkTimeouts(ITransaction transaction, boolean temporary) {
    	LOG.debug("DataBaseResourceLocks.checkTimeouts()");
    	
    	dataBaseLockService.removeTimeoutLocks(temporary);
    }
    
    public boolean checkLocks(boolean exclusive, int depth) {
    	LOG.debug("DataBaseResourceLocks.checkLocks()");
    	
       return false;
    }

    public boolean exclusiveLock(ITransaction transaction, String path,
            String owner, int depth, int timeout) throws LockFailedException {
    	
    	LOG.debug("DataBaseResourceLocks.exclusiveLock()");
    	
        return lock(transaction, path, owner, true, depth, timeout, false);
    }

    public boolean sharedLock(ITransaction transaction, String path,
            String owner, int depth, int timeout) throws LockFailedException {
    	
    	LOG.debug("DataBaseResourceLocks.sharedLock()");
    	
        return lock(transaction, path, owner, false, depth, timeout, false);
    }

    public DataBaseLockedObject getLockedObjectByID(ITransaction transaction, String id) {
    	LOG.debug("DataBaseResourceLocks.getLockedObjectByID()");
    	
    	DataBaseLockedObject lockedObject = dataBaseLockService.getLockedObjectById(id, Boolean.FALSE);
    	if (lockedObject!=null) {
            return lockedObject;
        } else {
            return null;
        }
    }

    public DataBaseLockedObject getLockedObjectByPath(ITransaction transaction,
            String path) {
    	
    	LOG.debug("DataBaseResourceLocks.getLockedObjectByPath()");
    	
    	DataBaseLockedObject lockedObject = dataBaseLockService.getLockedObjectByPath(path, Boolean.FALSE);
    	
        if (lockedObject!=null) {
            return lockedObject;
        } else {
            return null;
        }
    }
    
    
    public DataBaseLockedObject getTempLockedObjectByID(ITransaction transaction,
            String id) {
    	LOG.debug("DataBaseResourceLocks.getTempLockedObjectById()");
    	
    	DataBaseLockedObject lockedObject = dataBaseLockService.getLockedObjectById(id, Boolean.TRUE);
    	
        if (lockedObject!=null) {
            return lockedObject;
        } else {
            return null;
        }
    }

    public DataBaseLockedObject getTempLockedObjectByPath(ITransaction transaction,
            String path) {
    	LOG.debug("DataBaseResourceLocks.getTempLockedObjectByPath()");
    	
    	DataBaseLockedObject lockedObject = dataBaseLockService.getLockedObjectByPath(path, Boolean.TRUE);
    	
        if (lockedObject!=null) {
            return lockedObject;
        } else {
            return null;
        }
    	
    }
    
    

    /**
     * generates real LockedObjects for the resource at path and its parent
     * folders. does not create new LockedObjects if they already exist
     * 
     * @param transaction
     * @param path
     *      path to the (new) LockedObject
     * @return the LockedObject for path.
     */
    private DataBaseLockedObject generateLockedObjects(ITransaction transaction,
            String path) {
    	LOG.debug("DataBaseResourceLocks.generateLockedObjects()");
    	
    	if (!dataBaseLockService.isLockedByPath(path, Boolean.FALSE) ){
    		DataBaseLockedObject returnObject = new DataBaseLockedObject(this, path,
                    !_temporary);
            String parentPath = getParentPath(path);
            if (parentPath != null && !"/".equals(path)) {
            	DataBaseLockedObject parentLockedObject = generateLockedObjects(
                        transaction, parentPath);
                parentLockedObject.addChild(returnObject);
                returnObject._parent = parentLockedObject;
            }
            return returnObject;
        } else {
            // there is already a LockedObject on the specified path
            return dataBaseLockService.getLockedObjectByPath(path, Boolean.FALSE);
        }
    }

    /**
     * generates temporary LockedObjects for the resource at path and its parent
     * folders. does not create new LockedObjects if they already exist
     * 
     * @param transaction
     * @param path
     *      path to the (new) LockedObject
     * @return the LockedObject for path.
     */
    private DataBaseLockedObject generateTempLockedObjects(ITransaction transaction,
            String path) {
    	LOG.debug("DataBaseResourceLocks.generateTempLockedObjects()");
    	
    	
    	if (!dataBaseLockService.isLockedByPath(path, Boolean.TRUE) ){
    		DataBaseLockedObject returnObject = new DataBaseLockedObject(this, path, _temporary);
            String parentPath = getParentPath(path);
            if (parentPath != null && !"/".equals(path)) {
            	DataBaseLockedObject parentLockedObject = generateTempLockedObjects(
                        transaction, parentPath);
                parentLockedObject.addChild(returnObject);
                returnObject._parent = parentLockedObject;
            }
            return returnObject;
        } else {
//             there is already a LockedObject on the specified path
            return dataBaseLockService.getLockedObjectByPath(path, _temporary);
        }
    	

    }
    

    /**
     * creates the parent path from the given path by removing the last '/' and
     * everything after that
     * 
     * @param path
     *      the path
     * @return parent path
     */
    private String getParentPath(String path) {
    	LOG.debug("DataBaseResourceLocks.getParentPath()");
        int slash = path.lastIndexOf('/');
        if (slash == -1) {
            return null;
        } else {
            if (slash == 0) {
                // return "root" if parent path is empty string
                return "/";
            } else {
                return path.substring(0, slash);
            }
        }
    }

	public void setDataBaseLockService(DataBaseLockService dataBaseLockService) {
		this.dataBaseLockService = dataBaseLockService;
	}
 }

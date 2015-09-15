package com.ejie.x38.webdav.locking;


/**
 * a helper class for ResourceLocks, represents the Locks
 * 
 * @author re
 * 
 */
public abstract class LockedObject <T extends IResourceLocks> {

    protected T _resourceLocks;

    protected String _path;

    protected String _id;

    /**
     * Describing the depth of a locked collection. If the locked resource is
     * not a collection, depth is 0 / doesn't matter.
     */
    protected int _lockDepth;

    /**
     * Describing the timeout of a locked object (ms)
     */
    protected long _expiresAt;

    /**
     * owner of the lock. shared locks can have multiple owners. is null if no
     * owner is present
     */
    // protected String[] _owner = null;
    protected String[] _owner = null;

    /**
     * children of that lock
     */
    protected LockedObject<T>[] _children = null;

    protected LockedObject<T> _parent = null;

    /**
     * weather the lock is exclusive or not. if owner=null the exclusive value
     * doesn't matter
     */
    protected boolean _exclusive = false;

    /**
     * weather the lock is a write or read lock
     */
    protected String _type = null;

    /**
     * @param resLocks
     *      the resourceLocks where locks are stored
     * @param path
     *      the path to the locked object
     * @param temporary
     *      indicates if the LockedObject should be temporary or not
     */
//    public LockedObject(ResourceLocks resLocks, String path, boolean temporary) {
//        _path = path;
//        _id = UUID.randomUUID().toString();
//        _resourceLocks = resLocks;
//
//        if (!temporary) {
//            _resourceLocks._locks.put(path, this);
//            _resourceLocks._locksByID.put(_id, this);
//        } else {
//            _resourceLocks._tempLocks.put(path, this);
//            _resourceLocks._tempLocksByID.put(_id, this);
//        }
//        _resourceLocks._cleanupCounter++;
//    }

    /**
     * adds a new owner to a lock
     * 
     * @param owner
     *      string that represents the owner
     * @return true if the owner was added, false otherwise
     */
    public boolean addLockedObjectOwner(String owner) {

        if (_owner == null) {
            _owner = new String[1];
        } else {

            int size = _owner.length;
            String[] newLockObjectOwner = new String[size + 1];

            // check if the owner is already here (that should actually not
            // happen)
            for (int i = 0; i < size; i++) {
                if (_owner[i].equals(owner)) {
                    return false;
                }
            }

            System.arraycopy(_owner, 0, newLockObjectOwner, 0, size);
            _owner = newLockObjectOwner;
        }

        _owner[_owner.length - 1] = owner;
        return true;
    }

    /**
     * tries to remove the owner from the lock
     * 
     * @param owner
     *      string that represents the owner
     */
    public void removeLockedObjectOwner(String owner) {

        try {
            if (_owner != null) {
                int size = _owner.length;
                for (int i = 0; i < size; i++) {
                    // check every owner if it is the requested one
                    if (_owner[i].equals(owner)) {
                        // remove the owner
                        String[] newLockedObjectOwner = new String[size - 1];
                        for (int j = 0; j < (size - 1); j++) {
                            if (j < i) {
                                newLockedObjectOwner[j] = _owner[j];
                            } else {
                                newLockedObjectOwner[j] = _owner[j + 1];
                            }
                        }
                        _owner = newLockedObjectOwner;

                    }
                }
                if (_owner.length == 0) {
                    _owner = null;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("LockedObject.removeLockedObjectOwner()");
            System.out.println(e.toString());
        }
    }

    /**
     * deletes this Lock object. assumes that it has no children and no owners
     * (does not check this itself)
     * 
     */
    public abstract void removeLockedObject();

    /**
     * deletes this Lock object. assumes that it has no children and no owners
     * (does not check this itself)
     * 
     */
    public abstract void removeTempLockedObject();



    /**
     * Sets a new timeout for the LockedObject
     * 
     * @param timeout
     */
    public void refreshTimeout(int timeout) {
        _expiresAt = System.currentTimeMillis() + (timeout * 1000);
    }

    /**
     * Gets the timeout for the LockedObject
     * 
     * @return timeout
     */
    public long getTimeoutMillis() {
        return (_expiresAt - System.currentTimeMillis());
    }

    /**
     * Return true if the lock has expired.
     * 
     * @return true if timeout has passed
     */
    public boolean hasExpired() {
        if (_expiresAt != 0) {
            return (System.currentTimeMillis() > _expiresAt);
        } else {
            return true;
        }
    }

    /**
     * Gets the LockID (locktoken) for the LockedObject
     * 
     * @return locktoken
     */
    public String getID() {
        return _id;
    }

    /**
     * Gets the owners for the LockedObject
     * 
     * @return owners
     */
    public String[] getOwner() {
        return _owner;
    }

    /**
     * Gets the path for the LockedObject
     * 
     * @return path
     */
    public String getPath() {
        return _path;
    }

    /**
     * Sets the exclusivity for the LockedObject
     * 
     * @param exclusive
     */
    public void setExclusive(boolean exclusive) {
        _exclusive = exclusive;
    }

    /**
     * Gets the exclusivity for the LockedObject
     * 
     * @return exclusivity
     */
    public boolean isExclusive() {
        return _exclusive;
    }

    /**
     * Gets the exclusivity for the LockedObject
     * 
     * @return exclusivity
     */
    public boolean isShared() {
        return !_exclusive;
    }

    /**
     * Gets the type of the lock
     * 
     * @return type
     */
    public String getType() {
        return _type;
    }

    /**
     * Gets the depth of the lock
     * 
     * @return depth
     */
    public int getLockDepth() {
        return _lockDepth;
    }

}

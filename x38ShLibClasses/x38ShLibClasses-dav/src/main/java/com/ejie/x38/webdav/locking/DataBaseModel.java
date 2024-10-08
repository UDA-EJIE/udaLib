package com.ejie.x38.webdav.locking;


/**
 * @author UDA
 */
public class DataBaseModel {

	private String id;
	
	private String owner;
	
	private String path;
	
	private Integer lockDepth;
	
	private Long expiresAt;
	
	private Boolean exclusiveLock;
	
	private String lockType;
	
	private String childrenId;
	
	private String parentId;
	
	private Boolean tempLock;
	
	public DataBaseModel() {
		super();
	}

	public DataBaseModel(String id, String owner, String path,
			Integer lockDepth, Long expiresAt, Boolean exclusive, String type,
			String children, String parent, Boolean tempLock) {
		super();
		this.id = id;
		this.owner = owner;
		this.path = path;
		this.lockDepth = lockDepth;
		this.expiresAt = expiresAt;
		this.exclusiveLock = exclusive;
		this.lockType = type;
		this.childrenId = children;
		this.parentId = parent;
		this.tempLock = tempLock;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getLockDepth() {
		return lockDepth;
	}

	public void setLockDepth(Integer lockDepth) {
		this.lockDepth = lockDepth;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Boolean getExclusiveLock() {
		return exclusiveLock;
	}

	public void setExclusiveLock(Boolean exclusiveLock) {
		this.exclusiveLock = exclusiveLock;
	}

	public String getLockType() {
		return lockType;
	}

	public void setLockType(String lockType) {
		this.lockType = lockType;
	}

	public String getChildrenId() {
		return childrenId;
	}

	public void setChildrenId(String childrenId) {
		this.childrenId = childrenId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Boolean getTempLock() {
		return tempLock;
	}

	public void setTempLock(Boolean tempLock) {
		this.tempLock = tempLock;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getName()).append(" Object {");
		result.append(" [ id: ").append(this.id).append(" ]");
		result.append(" [ owner: ").append(this.owner).append(" ]");
		result.append(" [ path: ").append(this.path).append(" ]");
		result.append(" [ lockDepth: ").append(this.lockDepth).append(" ]");
		result.append(" [ expiresAt: ").append(this.expiresAt).append(" ]");
		result.append(" [ exclusiveLock: ").append(this.exclusiveLock).append(" ]");
		result.append(" [ lockType: ").append(this.lockType).append(" ]");
		result.append(" [ childrenId: ").append(this.childrenId).append(" ]");
		result.append(" [ lockDepth: ").append(this.lockDepth).append(" ]");
		result.append(" [ parentId: ").append(this.parentId).append(" ]");
		result.append(" [ tempLock: ").append(this.tempLock).append(" ]");
		result.append("}");
		return result.toString();
	}
	
}



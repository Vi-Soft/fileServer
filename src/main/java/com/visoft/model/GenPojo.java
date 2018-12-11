package com.visoft.model;

import java.io.Serializable;
import java.time.Instant;

import org.bson.types.ObjectId;

/**
 * @author vlad
 *
 */
public abstract class GenPojo implements Serializable{

	private static final long serialVersionUID = -1105723711108931054L;
	
	private final ObjectId id;
	private final ObjectId postedBy;
	private final Instant createdAt;
	private final Instant notBefore;
	private final Instant expiration;
	private final boolean deleted;

	/**
	 * Copy constructor
	 */
	public GenPojo(GenPojo pojo) {
		this(pojo.getId(), pojo.getPostedBy(), pojo.getCreatedAt(),
				pojo.getNotBefore(), pojo.getExpiration(), pojo.isDeleted());
	}

	/**
	 * Create not-deleted, active from now and immortal instance.
	 * 
	 * @param postedBy
	 * @param createdAt
	 */
	public GenPojo(final ObjectId postedBy, final Instant expiration) {
		this(postedBy, Instant.now(), Instant.now(), expiration);
	}

	/**
	 * @param postedBy
	 * @param createdAt
	 * @param expiration
	 * @param notBefore
	 */
	public GenPojo(final ObjectId postedBy, final Instant createdAt,
			final Instant notBefore, final Instant expiration) {
		this(ObjectId.get(), postedBy, createdAt, notBefore, expiration, false);
	}

	/**
	 * @param id
	 * @param postedBy
	 * @param createdAt
	 * @param expiration
	 * @param notBefore
	 * @param deleted
	 */
	public GenPojo(final ObjectId id, final ObjectId postedBy,
			final Instant createdAt, final Instant notBefore,
			final Instant expiration, final boolean deleted) {
		super();
		this.id = id;
		this.postedBy = postedBy;
		this.createdAt = createdAt;
		this.notBefore = notBefore;
		this.expiration = expiration;
		this.deleted = deleted;
	}

	public ObjectId getId() {
		return id;
	}

	public ObjectId getPostedBy() {
		return postedBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getNotBefore() {
		return notBefore;
	}

//	public GenPojo setNotBefore(final Instant notBefore) throws Exception {
//		return ClassUtil.setter(this, "notBefore", notBefore);
//	}

	public Instant getExpiration() {
		return expiration;
	}

//	public GenPojo setExpiration(final Instant expiration) throws Exception {
//		return ClassUtil.setter(this, "expiration", expiration);
//	}

	public boolean isDeleted() {
		return deleted;
	}

//	public GenPojo setDeleted(final Instant deleted) throws Exception {
//		return ClassUtil.setter(this, "deleted", deleted);
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result
				+ ((expiration == null) ? 0 : expiration.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((notBefore == null) ? 0 : notBefore.hashCode());
		result = prime * result
				+ ((postedBy == null) ? 0 : postedBy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenPojo other = (GenPojo) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (deleted != other.deleted)
			return false;
		if (expiration == null) {
			if (other.expiration != null)
				return false;
		} else if (!expiration.equals(other.expiration))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (notBefore == null) {
			if (other.notBefore != null)
				return false;
		} else if (!notBefore.equals(other.notBefore))
			return false;
		if (postedBy == null) {
			if (other.postedBy != null)
				return false;
		} else if (!postedBy.equals(other.postedBy))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GenPojo [id=" + id + ", postedBy=" + postedBy + ", createdAt="
				+ createdAt + ", notBefore=" + notBefore + ", expiration="
				+ expiration + ", deleted=" + deleted + "]";
	}

}

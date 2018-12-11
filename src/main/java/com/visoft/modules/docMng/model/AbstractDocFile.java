package com.visoft.modules.docMng.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public abstract class AbstractDocFile {

	private final ObjectId id;
	private final String fileContentHash;
	private final String fileName;
	private final String postedBy;
	private final Instant createdAt;
	private final long fileLengthInBytes;
	private final boolean deleted;

	public AbstractDocFile(final String fileContentHash, final String fileName,
			final String postedBy, final long fileLengthInBytes, final Instant createdAt) {
		this(ObjectId.get(), fileContentHash, fileName, postedBy,
				fileLengthInBytes, createdAt);
	}

	public AbstractDocFile(final ObjectId id, final String fileContentHash,
			final String fileName, final String postedBy,
			final long fileLengthInBytes, final Instant createdAt) {
		super();
		this.id = id;
		this.fileContentHash = fileContentHash;
		this.fileName = fileName;
		this.postedBy = postedBy;
		this.createdAt = createdAt == null ? Instant.now() : createdAt;
		this.fileLengthInBytes = fileLengthInBytes;
		this.deleted = false;
	}

	@BsonId
	public ObjectId getId() {
		return id;
	}

	public String getFileContentHash() {
		return fileContentHash;
	}

	public String getFileName() {
		return fileName;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public long getFileLengthInBytes() {
		return fileLengthInBytes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result
				+ ((fileContentHash == null) ? 0 : fileContentHash.hashCode());
		result = prime * result
				+ (int) (fileLengthInBytes ^ (fileLengthInBytes >>> 32));
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AbstractDocFile other = (AbstractDocFile) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (deleted != other.deleted)
			return false;
		if (fileContentHash == null) {
			if (other.fileContentHash != null)
				return false;
		} else if (!fileContentHash.equals(other.fileContentHash))
			return false;
		if (fileLengthInBytes != other.fileLengthInBytes)
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "AbstractDocFile [id=" + id + ", fileContentHash="
				+ fileContentHash + ", fileName=" + fileName + ", postedBy="
				+ postedBy + ", createdAt=" + createdAt + ", fileLengthInBytes="
				+ fileLengthInBytes + ", deleted=" + deleted + "]";
	}

}

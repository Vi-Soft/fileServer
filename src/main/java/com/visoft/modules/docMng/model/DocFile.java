package com.visoft.modules.docMng.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

/**
 * @author vlad
 *
 */
@BsonDiscriminator
public final class DocFile extends AbstractDocFile {

	private final String projectId;
	private final String taskId;
	private final String businessType;

	@BsonCreator
	public DocFile(@BsonProperty("id") final ObjectId id,
			@BsonProperty("fileContentHash") final String fileContentHash,
			@BsonProperty("fileName") final String fileName,
			@BsonProperty("projectId") final String projectId,
			@BsonProperty("taskId") final String taskId,
			@BsonProperty("postedBy") final String postedBy,
			@BsonProperty("fileLengthInBytes") final long fileLengthInBytes,
			@BsonProperty("businessType") final String businessType,
			@BsonProperty("createdAt") final Instant createdAt) {
		super(id, fileContentHash, fileName, postedBy, fileLengthInBytes, createdAt);
		this.taskId = taskId;
		this.projectId = projectId;
		this.businessType = businessType;
	}

	public DocFile(
			@BsonProperty("fileContentHash") final String fileContentHash,
			@BsonProperty("fileName") final String fileName,
			@BsonProperty("projectId") final String projectId,
			@BsonProperty("taskId") final String taskId,
			@BsonProperty("postedBy") final String postedBy,
			@BsonProperty("fileLengthInBytes") final long fileLengthInBytes,
			@BsonProperty("businessType") final String businessType) {
		super(fileContentHash, fileName, postedBy, fileLengthInBytes, null);

		this.taskId = taskId;
		this.projectId = projectId;
		this.businessType = businessType;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getBusinessType() {
		return businessType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((businessType == null) ? 0 : businessType.hashCode());
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocFile other = (DocFile) obj;
		if (businessType == null) {
			if (other.businessType != null)
				return false;
		} else if (!businessType.equals(other.businessType))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + " DocFile [projectId=" + projectId + ", taskId=" + taskId
				+ ", businessType=" + businessType + "]";
	}

}

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
public final class Report extends AbstractDocFile {

	private final String projectId;
	private final String taskId;
	private final String reportType;

	@BsonCreator
	public Report(@BsonProperty("id") final ObjectId id,
			@BsonProperty("fileContentHash") final String fileContentHash,
			@BsonProperty("fileName") final String fileName,
			@BsonProperty("projectId") final String projectId,
			@BsonProperty("taskId") final String taskId,
			@BsonProperty("postedBy") final String postedBy,
			@BsonProperty("fileLengthInBytes") final long fileLengthInBytes,
			@BsonProperty("reportType") final String reportType,
			@BsonProperty("createdAt") final Instant createdAt) {
		super(id, fileContentHash, fileName, postedBy, fileLengthInBytes,
				createdAt);
		this.taskId = taskId;
		this.projectId = projectId;
		this.reportType = reportType;
	}

	public Report(@BsonProperty("fileContentHash") final String fileContentHash,
			@BsonProperty("fileName") final String fileName,
			@BsonProperty("projectId") final String projectId,
			@BsonProperty("taskId") final String taskId,
			@BsonProperty("postedBy") final String postedBy,
			@BsonProperty("fileLengthInBytes") final long fileLengthInBytes,
			@BsonProperty("reportType") final String reportType) {
		super(fileContentHash, fileName, postedBy, fileLengthInBytes, null);

		this.taskId = taskId;
		this.projectId = projectId;
		this.reportType = reportType;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getReportType() {
		return reportType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result
				+ ((reportType == null) ? 0 : reportType.hashCode());
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
		Report other = (Report) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (reportType == null) {
			if (other.reportType != null)
				return false;
		} else if (!reportType.equals(other.reportType))
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
		return "Report [projectId=" + projectId + ", taskId=" + taskId
				+ ", reportType=" + reportType + "]";
	}

}

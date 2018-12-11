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
public final class DocTemplate extends AbstractDocFile {

	private final String templateType;

	@BsonCreator
	public DocTemplate(@BsonProperty("id") final ObjectId id,
			@BsonProperty("fileContentHash") final String fileContentHash,
			@BsonProperty("fileName") final String fileName,
			@BsonProperty("postedBy") final String postedBy,
			@BsonProperty("fileLengthInBytes") final long fileLengthInBytes,
			@BsonProperty("templateType") final String templateType,
			@BsonProperty("createdAt") final Instant createdAt) {
		super(id, fileContentHash, fileName, postedBy, fileLengthInBytes, createdAt);
		this.templateType = templateType;
	}

	public DocTemplate(
			@BsonProperty("fileContentHash") final String fileContentHash,
			@BsonProperty("fileName") final String fileName,
			@BsonProperty("postedBy") final String postedBy,
			@BsonProperty("fileLengthInBytes") final long fileLengthInBytes,
			@BsonProperty("templateType") final String templateType) {
		super(fileContentHash, fileName, postedBy, fileLengthInBytes, null);
		this.templateType = templateType;
	}

	public String getTemplateType() {
		return templateType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((templateType == null) ? 0 : templateType.hashCode());
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
		DocTemplate other = (DocTemplate) obj;
		if (templateType == null) {
			if (other.templateType != null)
				return false;
		} else if (!templateType.equals(other.templateType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DocTemplate [templateType=" + templateType + "]";
	}

}

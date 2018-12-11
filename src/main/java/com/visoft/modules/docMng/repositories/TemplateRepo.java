package com.visoft.modules.docMng.repositories;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static java.util.Collections.unmodifiableList;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.visoft.exceptions.DocMngRuntimeException;
import com.visoft.modules.docMng.model.DocTemplate;
import com.visoft.utils.Const;
import com.visoft.utils.DBUtils;

public final class TemplateRepo {

	private static final MongoCollection<DocTemplate> templateFiles = DBUtils.DB
			.getCollection(Const.DOC_TEMPLATES_MONGO_COLLECTION,
					DocTemplate.class);

	private TemplateRepo() {

	}

	public static List<DocTemplate> getAllDocTemplates() {

		final Bson bsonFilter = eq(Const.DELETED, false);

		final List<DocTemplate> allDocs = StreamSupport
				.stream(templateFiles.find(bsonFilter).spliterator(), false)
				.collect(Collectors.toList());

		return unmodifiableList(allDocs);
	}

	/**
	 * @param fileContentHash
	 * @param fileName
	 * @param templateType
	 * @return
	 */
	public static DocTemplate saveDocTemplate(final String fileContentHash,
			final String fileName, final String token,
			final long fileLengthInBytes, final String templateType) {

		final DocTemplate docTemplate = new DocTemplate(fileContentHash,
				fileName, token, fileLengthInBytes, templateType);

		templateFiles.insertOne(docTemplate);

		return docTemplate;
	}

	/**
	 * @param id
	 * @param fileContentHash
	 * @param fileName
	 * @param postedBy
	 * @param fileLengthInBytes
	 * @param templateType
	 * @return
	 */
	public static Long updateDocTemplate(final String id,
			final String fileContentHash, final String fileName,
			final String postedBy, final long fileLengthInBytes,
			final String templateType) {

		final UpdateResult updateResult = templateFiles.updateOne(
				eq(Const._ID, new ObjectId(id)),
				combine(set(Const.FILE_CONTENT_HASH, fileContentHash),
						set(Const.POSTED_BY, postedBy),
						set(Const.FILE_LENGTH_IN_BYTES, fileLengthInBytes),
						set(Const.FILE_NAME, fileName),
						set(Const.CREATED_AT, Instant.now()),
						set(Const.TEMPLATE_TYPE, templateType)));

		return updateResult.getModifiedCount();
	}

	/**
	 * @param id
	 * @return
	 */
	public static Long deleteDocTemplate(final String id) {

		final UpdateResult updateResult = templateFiles.updateOne(
				eq(Const._ID, new ObjectId(id)), set(Const.DELETED, true));

		return updateResult.getModifiedCount();
	}

	/**
	 * @param id
	 * @return
	 */
	public static Long recoveryDocTemplate(final String id) {

		final UpdateResult updateResult = templateFiles.updateOne(
				eq(Const._ID, new ObjectId(id)), set(Const.DELETED, false));

		return updateResult.getModifiedCount();
	}

	/**
	 * @param id
	 * @return
	 */
	public static DocTemplate findById(final String id) {

		final DocTemplate docTemplate = Optional
				.ofNullable(
						templateFiles.find(and(eq(Const._ID, new ObjectId(id)),
								eq(Const.DELETED, false))).first())
				.orElseThrow(() -> new DocMngRuntimeException(
						"DocTemplate with id: " + id + " does not exists."));

		return docTemplate;
	}

}

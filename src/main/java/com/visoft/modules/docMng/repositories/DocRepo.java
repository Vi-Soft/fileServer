package com.visoft.modules.docMng.repositories;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.visoft.exceptions.DocMngRuntimeException;
import com.visoft.modules.docMng.model.DocFile;
import com.visoft.modules.user.UserRepo;
import com.visoft.utils.Const;
import com.visoft.utils.DBUtils;

/**
 * @author vlad
 *
 */
final public class DocRepo {

	private static final MongoCollection<DocFile> docFiles = DBUtils.DB
			.getCollection(Const.DOC_FILES_MONGO_COLLECTION, DocFile.class);

	private DocRepo() {

	}

	/**
	 * @param filterData
	 * @param limit
	 * @param skip
	 * @return
	 */
	public static List<DocFile> getAllDocFiles(
			final LinkedHashMap<String, String> filterData, final Number limit,
			final Number skip) {

		final Optional<Bson> mongoFilter = Optional.ofNullable(filterData)
				.map(DocRepo::buildDocFilter);
		final List<DocFile> allDocs = new ArrayList<>();
		final FindIterable<DocFile> selectedDocs = mongoFilter
				.map(docFiles::find).orElseGet(docFiles::find);
		for (DocFile docFile : selectedDocs.skip(skip.intValue())
				.limit(limit.intValue())) {
			allDocs.add(docFile);
		}
		return allDocs;
	}

	/**
	 * @param fileContentHash
	 * @param fileName
	 * @param projectId
	 * @param taskId
	 * @return
	 */
	public static DocFile saveDocFile(final String fileContentHash,
			final String fileName, final String projectId, final String taskId,
			final String postedBy, final long fileLengthInBytes,
			final String businessType) {

		// check user existing, else throw exception.
		UserRepo.findByToken(postedBy);

		final DocFile docFile = new DocFile(fileContentHash, fileName,
				projectId, taskId, postedBy, fileLengthInBytes, businessType);
		return saveDocFile(docFile);
	}

	/**
	 * @param id
	 * @return
	 */
	public static Long deleteDocFile(final String id) {

		final UpdateResult updateResult = docFiles.updateOne(
				eq(Const._ID, new ObjectId(id)), set(Const.DELETED, true));

		return updateResult.getModifiedCount();
	}

	/**
	 * @param id
	 * @return
	 */
	public static Long recoveryDocFile(final String id) {

		final UpdateResult updateResult = docFiles.updateOne(
				eq(Const._ID, new ObjectId(id)), set(Const.DELETED, false));

		return updateResult.getModifiedCount();
	}

	/**
	 * @param id
	 * @return
	 */
	public static DocFile findById(final String id) {
		final DocFile docFile = Optional
				.ofNullable(docFiles.find(and(eq(Const._ID, new ObjectId(id)),
						eq(Const.DELETED, false))).first())
				.orElseThrow(() -> new DocMngRuntimeException(
						"DocFile id: " + id + " does not exist."));
		return docFile;
	}

	public static DocFile saveDocFile(final DocFile docFile) {
		docFiles.insertOne(docFile);
		return docFile;
	}

	private static Bson buildDocFilter(
			final LinkedHashMap<String, String> filterData) {

		Bson result = null;

		final String projectIdValue = filterData.get(Const.PROJECT_ID);
		final String taskIdValue = filterData.get(Const.TASK_ID);

		Bson projectIdCondition = null;
		Bson taskIdCondition = null;

		if (projectIdValue != null && !projectIdValue.isEmpty()) {
			projectIdCondition = eq(Const.PROJECT_ID, projectIdValue);
		}

		if (taskIdValue != null && !taskIdValue.isEmpty()) {
			taskIdCondition = eq(Const.TASK_ID, taskIdValue);
		}

		if (projectIdCondition != null && taskIdCondition != null) {
			result = and(projectIdCondition, taskIdCondition);
		}

		result = projectIdCondition != null ? projectIdCondition
				: projectIdCondition;
		return result;
	}
}

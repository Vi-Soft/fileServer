package com.visoft.modules.docMng.repositories;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.visoft.modules.docMng.ipfs.IPFSUtil;
import com.visoft.modules.docMng.model.Report;
import com.visoft.modules.user.User;
import com.visoft.modules.user.UserRepo;
import com.visoft.utils.Const;
import com.visoft.utils.DBUtils;

import graphql.GraphQLException;
import io.ipfs.api.MerkleNode;

/**
 * @author vlad
 *
 */
public final class ReportRepo {
	private static final MongoCollection<Report> reports = DBUtils.DB
			.getCollection(Const.REPORTS_MONGO_COLLECTION, Report.class);

	private ReportRepo() {

	}

	/**
	 * @param id
	 * @return
	 */
	public static Report findById(final String id) {
		Report report = reports.find(
				and(eq(Const._ID, new ObjectId(id)), eq(Const.DELETED, false)))
				.first();
		return report;
	}

	/**
	 * @param filterData
	 * @param limit
	 * @param skip
	 * @return
	 */
	public static List<Report> getAllReports(
			final LinkedHashMap<String, String> filterData, final Number limit,
			final Number skip) {

		Optional<Bson> mongoFilter = Optional.ofNullable(filterData)
				.map(ReportRepo::buildReportFilter);
		List<Report> allDocs = new ArrayList<>();
		FindIterable<Report> selectedDocs = mongoFilter.map(reports::find)
				.orElseGet(reports::find);
		for (Report report : selectedDocs.skip(skip.intValue())
				.limit(limit.intValue())) {
			allDocs.add(report);
		}
		return allDocs;
	}

	/**
	 * @param report
	 * @return
	 */
	public static Report saveReport(Report report) {
		User user = UserRepo.findByToken(report.getPostedBy());
		if (user == null) {
			throw new GraphQLException("The user is not authorised");
		}
		reports.insertOne(report);
		return report;
	}

	/**
	 * @param fileContentHash
	 * @param fileName
	 * @param projectId
	 * @param taskId
	 * @param postedBy
	 * @param fileLengthInBytes
	 * @param reportType
	 * @return
	 */
	public static Report saveReport(final String fileContentHash,
			final String fileName, final String projectId, final String taskId,
			final String postedBy, final long fileLengthInBytes,
			final String reportType) {

		Report report = new Report(fileContentHash, fileName, projectId, taskId,
				postedBy, fileLengthInBytes, reportType);
		return saveReport(report);
	}

	/**
	 * @param id
	 * @return
	 */
	public static Report deleteReport(final String id) {
		// TODO: check UpdateResult
		Report report = findById(id);
		// UpdateResult updateResult =
		reports.updateOne(eq(Const._ID, new ObjectId(id)),
				set(Const.DELETED, true));
		return report;
	}

	/**
	 * @param id
	 * @return
	 */
	public static Report recoveryReport(final String id) {
		// TODO: check UpdateResult
		reports.updateOne(eq(Const._ID, new ObjectId(id)),
				set(Const.DELETED, false));
		return findById(id);
	}

	public static void main(String[] args) throws IOException {
		Report report = saveReport(
				"C:\\Users\\vlad\\Desktop\\vi-soft-NG\\Vi-Soft-NG\\PDFout",
				"5b3e0024cb726d48cc014f24.pdf", "projectId", "taskId", 12,
				"5b2a0a37cb726d23c04bf21b", "reportType");
		System.out.println(report);
	}

	/**
	 * Use for testing purposes only
	 * 
	 * @param path
	 * @param fileName
	 * @param projectId
	 * @param taskId
	 * @return
	 * @throws IOException
	 */
	public static Report saveReport(final String path, final String fileName,
			final String projectId, final String taskId,
			final long fileLengthInBytes, final String postedBy,
			final String reportType) throws IOException {
		byte[] fileContent = Files
				.readAllBytes(Paths.get(path + File.separator + fileName));
		MerkleNode merkleNode = IPFSUtil.add(fileName, fileContent);
		return saveReport(merkleNode.hash.toBase58(), fileName, projectId,
				taskId, postedBy, fileLengthInBytes, reportType);
	}

	private static Bson buildReportFilter(
			final LinkedHashMap<String, String> filterData) {

		Bson result = null;

		String projectIdValue = filterData.get(Const.PROJECT_ID);
		String taskIdValue = filterData.get(Const.TASK_ID);

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

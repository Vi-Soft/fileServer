package com.visoft.modules.docMng;

import java.io.IOException;

import com.networknt.exception.ApiException;
import com.visoft.modules.docMng.ipfs.IPFSUtil;
import com.visoft.modules.docMng.model.DocFile;
import com.visoft.modules.docMng.model.DocTemplate;
import com.visoft.modules.docMng.model.Report;
import com.visoft.modules.docMng.repositories.DocRepo;
import com.visoft.modules.docMng.repositories.ReportRepo;
import com.visoft.modules.docMng.repositories.TemplateRepo;

import io.ipfs.api.MerkleNode;

/**
 * @author vlad
 *
 */
public final class DocMngService {

	private DocMngService() {

	}

	/**
	 * @param docFile
	 * @param fileContent
	 * @return
	 * @throws IOException
	 */
	public static DocFile saveDocFile(final DocFile docFile,
			final byte[] fileContent) throws IOException {

		return saveDocFile(docFile.getFileName(), fileContent,
				docFile.getProjectId(), docFile.getTaskId(),
				docFile.getPostedBy(), docFile.getFileLengthInBytes(),
				docFile.getBusinessType());
	}

	/**
	 * @param fileName
	 * @param fileContent
	 * @param projectId
	 * @param taskId
	 * @return
	 * @throws IOException
	 */
	public static DocFile saveDocFile(final String fileName,
			final byte[] fileContent, final String projectId,
			final String taskId, final String postedBy,
			final long fileLengthInBytes, final String businessType)
			throws IOException {

		final MerkleNode merkleNode = IPFSUtil.add(fileName, fileContent);
		final DocFile docFile = DocRepo.saveDocFile(merkleNode.hash.toBase58(),
				fileName, projectId, taskId, postedBy, fileLengthInBytes,
				businessType);
		
		return docFile;
	}

	/**
	 * @param docTemplate
	 * @param fileContent
	 * @return
	 * @throws IOException
	 */
	public static DocTemplate saveDocTemplate(final DocTemplate docTemplate,
			final byte[] fileContent) throws IOException {

		return saveDocTemplate(docTemplate.getFileName(), fileContent,
				docTemplate.getPostedBy(), docTemplate.getFileLengthInBytes(),
				docTemplate.getTemplateType());
	}

	/**
	 * @param fileName
	 * @param fileContent
	 * @param postedBy
	 * @param fileLengthInBytes
	 * @param templateType
	 * @return
	 * @throws IOException
	 */
	public static DocTemplate saveDocTemplate(final String fileName,
			final byte[] fileContent, final String postedBy,
			final long fileLengthInBytes, final String templateType)
			throws IOException {

		final MerkleNode merkleNode = IPFSUtil.add(fileName, fileContent);
		final DocTemplate docTemplate = TemplateRepo.saveDocTemplate(
				merkleNode.hash.toBase58(), fileName, postedBy,
				fileLengthInBytes, templateType);
		
		return docTemplate;
	}

	/**
	 * @param report
	 * @param fileContent
	 * @return
	 * @throws IOException
	 */
	public static Report saveReport(final Report report,
			final byte[] fileContent) throws IOException {

		return saveReport(report.getFileName(), fileContent,
				report.getProjectId(), report.getTaskId(), report.getPostedBy(),
				report.getFileLengthInBytes(), report.getReportType());
	}

	public static Report saveReport(final String fileName,
			final byte[] fileContent, final String projectId,
			final String taskId, final String postedBy,
			final long fileLengthInBytes, final String reportType)
			throws IOException {

		final MerkleNode merkleNode = IPFSUtil.add(fileName, fileContent);
		final Report report = ReportRepo.saveReport(merkleNode.hash.toBase58(),
				fileName, projectId, taskId, postedBy, fileLengthInBytes,
				reportType);
		
		return report;
	}

	/**
	 * Update DocTemplate without changing the ObjectId in mongodb
	 * 
	 * @param id
	 * @param fileName
	 * @param fileContent
	 * @param token
	 * @param fileLengthInBytes
	 * @param templateType
	 * @return
	 * @throws IOException
	 */
	public static Long updateDocTemplate(final String id, final String fileName,
			final byte[] fileContent, final String token,
			final long fileLengthInBytes, final String templateType)
			throws IOException {

		final MerkleNode merkleNode = IPFSUtil.add(fileName, fileContent);

		return TemplateRepo.updateDocTemplate(id, merkleNode.hash.toBase58(),
				fileName, token, fileLengthInBytes, templateType);

	}

	/**
	 * Update DocTemplate without changing the ObjectId in mongodb
	 * 
	 * @param abstractDocFile
	 * @param fileContent
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static Long updateDocTemplate(final DocTemplate docTemplate,
			final byte[] fileContent) throws IOException {

		return updateDocTemplate(docTemplate.getId().toString(),
				docTemplate.getFileName(), fileContent,
				docTemplate.getPostedBy().toString(),
				docTemplate.getFileLengthInBytes(),
				docTemplate.getTemplateType());
	}

	/**
	 * Get meta information of DocFile, without the file content. To get the
	 * file content, call the method "getDocFileContent".
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	public static DocFile getDocFile(final String id) {
		
		return  DocRepo.findById(id);
		
	}

	/**
	 * Get meta information of DocTemplate, without the file content. To get the
	 * file content, call the method "getDocTemplateContent".
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	public static DocTemplate getDocTemplate(final String id) {
		
		return TemplateRepo.findById(id);
		
	}

	/**
	 * Get DocFileContent from IPFS
	 * 
	 * @param docFile
	 * @return
	 * @throws IOException
	 */
	public static byte[] getDocFileContent(final DocFile docFile)
			throws IOException {
		
		return getFileContentFromIPFS(docFile.getFileContentHash());
		
	}

	/**
	 * @param report
	 * @return
	 * @throws IOException
	 */
	public static byte[] getReportContent(final Report report)
			throws IOException {
		
		return getFileContentFromIPFS(report.getFileContentHash());
	}

	/**
	 * Get DocTemplateContent from IPFS
	 * 
	 * @param docTemplate
	 * @return
	 * @throws IOException
	 */
	public static byte[] getDocTemplateContent(final DocTemplate docTemplate)
			throws IOException {
		
		return getFileContentFromIPFS(docTemplate.getFileContentHash());
	}

	/**
	 * Get FileContent from IPFS
	 * 
	 * @param fileContentHash
	 * @return
	 * @throws IOException
	 */
	public static byte[] getFileContentFromIPFS(final String fileContentHash)
			throws IOException {

		return IPFSUtil.cat(fileContentHash);
	}

}

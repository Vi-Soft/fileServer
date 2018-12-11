package com.visoft.modules.docMng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Deque;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.exception.ApiException;
import com.networknt.status.Status;
import com.visoft.modules.docMng.model.DocFile;
import com.visoft.modules.docMng.model.DocTemplate;
import com.visoft.modules.docMng.model.Report;
import com.visoft.modules.docMng.model.TemplateDTO;
import com.visoft.utils.Const;
import com.visoft.utils.FileUtil;
import com.visoft.utils.HttpUtil;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.Headers;

/**
 * @author vlad
 *
 */
public class DocMngActions {

	private static final Logger log = LoggerFactory
			.getLogger(DocMngActions.class);

	private DocMngActions() {

	}

	/**
	 * @param exchange
	 * @param outPathStr
	 * @throws IOException
	 */
	public static void saveDocFileProcessing(final HttpServerExchange exchange,
			final String outPathStr) throws IOException {
		final String projectId = HttpUtil.parseFromForm(exchange,
				Const.PROJECT_ID);
		final String taskId = HttpUtil.parseFromForm(exchange, Const.TASK_ID);
		final String businessType = HttpUtil.parseFromForm(exchange,
				Const.BUSINESS_TYPE);

		final FormData attachment = exchange
				.getAttachment(FormDataParser.FORM_DATA);
		final Deque<FormData.FormValue> fileValueLs = attachment.get("file");
		for (FormData.FormValue fileValue : fileValueLs) {

			final Path file = fileValue.getPath();

			// TODO: remove deprecation
			final long fileLengthInBytes = fileValue.getFile().length();

			final String fileName = Jsoup.parse(fileValue.getFileName()).text();

			final byte[] fileContent = Files.readAllBytes(file);

			// TODO: open it
			// String postedBy = HttpUtil.getTokenFromExchange(exchange);
			// TODO: remove it
			String postedBy = "5b5450b7cb726d2bb8f60376";

			final DocFile docFile = DocMngService.saveDocFile(fileName,
					fileContent, projectId, taskId, postedBy, fileLengthInBytes,
					businessType);
			FileUtil.writeFile2disc(outPathStr, fileContent, docFile,
					Optional.of(projectId));
			log.info("added DocFile: {}, postedBy: {}, docFileId: {}. ",
					fileName, postedBy, docFile.getId());
		}
	}

	/**
	 * @param exchange
	 * @param outPathStr
	 * @throws IOException
	 * @throws Exception
	 */
	public static void saveTemplateProcessing(final HttpServerExchange exchange,
			final String outPathStr) throws IOException, Exception {

		final String templateType = HttpUtil.parseFromForm(exchange,
				Const.TEMPLATE_TYPE);

		final FormData attachment = exchange
				.getAttachment(FormDataParser.FORM_DATA);
		final Deque<FormData.FormValue> fileValueLs = attachment.get("file");
		for (FormData.FormValue fileValue : fileValueLs) {

			final Path file = fileValue.getPath();

			// TODO: remove deprecation
			final long fileLengthInBytes = fileValue.getFile().length();

			final String fileName = Jsoup.parse(fileValue.getFileName()).text();

			final byte[] fileContent = Files.readAllBytes(file);

			// TODO: open it
			// String postedBy = HttpUtil.getTokenFromExchange(exchange);
			// TODO: remove it
			final String postedBy = "5b2a0a37cb726d23c04bf21b";

			final DocTemplate docTemplate = DocMngService.saveDocTemplate(
					fileName, fileContent, postedBy, fileLengthInBytes,
					templateType);

			FileUtil.writeFile2disc(outPathStr, fileContent, docTemplate,
					Optional.empty());
			log.info("added DocTemplate: {}, postedBy: {}, docTemplateId: {}. ",
					fileName, postedBy, docTemplate.getId());
		}
	}

	/**
	 * @param exchange
	 * @param outPathStr
	 * @throws IOException
	 * @throws Exception
	 */
	public static void updateTemplateProcessing(
			final HttpServerExchange exchange, final String outPathStr)
			throws IOException, Exception {
		final String id = HttpUtil.parseFromForm(exchange, Const._ID);
		final String templateType = HttpUtil.parseFromForm(exchange,
				Const.TEMPLATE_TYPE);

		final FormData attachment = exchange
				.getAttachment(FormDataParser.FORM_DATA);
		final Deque<FormData.FormValue> fileValueLs = attachment.get("file");
		for (FormData.FormValue fileValue : fileValueLs) {

			final Path file = fileValue.getPath();

			// TODO: remove deprecation
			final long fileLengthInBytes = fileValue.getFile().length();

			final String fileName = Jsoup.parse(fileValue.getFileName()).text();

			final byte[] fileContent = Files.readAllBytes(file);

			// TODO: open it
			// String postedBy = HttpUtil.getTokenFromExchange(exchange);
			// TODO: remove it
			final String postedBy = "5b2a0a37cb726d23c04bf21b";

			final DocTemplate docTemplate = new DocTemplate(new ObjectId(id),
					null, fileName, postedBy, fileLengthInBytes, templateType,
					Instant.now());

			DocMngService.updateDocTemplate(docTemplate, fileContent);

			FileUtil.writeFile2disc(outPathStr, fileContent, docTemplate,
					Optional.empty());
			log.info(
					"updated DocTemplate: {}, postedBy: {}, docTemplateId: {}. ",
					fileName, postedBy, docTemplate.getId());
		}
	}

	/**
	 * @param exchange
	 * @param outPathStr
	 * @throws ApiException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void buildPdfProcessing(final HttpServerExchange exchange,
			final String outPathStr) throws TransformerException, SAXException,
			ParserConfigurationException, ApiException {
		try {
			final String projectId = HttpUtil.parseFromForm(exchange,
					Const.PROJECT_ID);
			final String id = HttpUtil.parseFromForm(exchange, Const._ID);
			final String reportType = HttpUtil.parseFromForm(exchange,
					Const.REPORT_TYPE);

			final FormData attachment = exchange
					.getAttachment(FormDataParser.FORM_DATA);
			final Deque<FormData.FormValue> fileValueLs = attachment
					.get("file");

			final FormData.FormValue fileValue = fileValueLs.getFirst();
			// TODO: remove deprecation
			final long fileLengthInBytes = fileValue.getFile().length();

			final Path file = fileValue.getPath();
			byte[] templateContent;

			templateContent = Files.readAllBytes(file);

			final ObjectMapper mapper = new ObjectMapper();
			final ByteArrayInputStream is = new ByteArrayInputStream(
					templateContent);
			final TemplateDTO testObj = mapper.readValue(is, TemplateDTO.class);
			is.close();
			testObj.setTemplateId(id);
			final ByteArrayOutputStream pdfFileContent = TemplateService
					.getPDFFromTemplate(testObj);
			final String outFileName = testObj.getOutPutName();

			// TODO: open it
			// String postedBy = HttpUtil.getTokenFromExchange(exchange);
			// TODO: remove it
			final String postedBy = "5b5450b7cb726d2bb8f60376";

			final byte[] fileContent = pdfFileContent.toByteArray();

			final Report report = DocMngService.saveReport(outFileName,
					fileContent, testObj.getProjectId(), testObj.getTaskId(),
					postedBy, fileLengthInBytes, reportType);

			final String mimeType = FileUtil.getFileType(outFileName);
			exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, mimeType);

			exchange.getResponseHeaders()
					// TODO: replace in fileName '+' sign with space
					// on client
					.put(Headers.CONTENT_DISPOSITION, "inline; filename=\""
							+ URLEncoder.encode(outFileName, "UTF-8") + "\"");
			exchange.getResponseSender()
					.send(ByteBuffer.wrap(pdfFileContent.toByteArray()));

			FileUtil.writeFile2disc(outPathStr, fileContent, report,
					Optional.of(projectId));
			log.info(
					"PDF report cretated, Report: {}, postedBy: {}, reportId: {}. ",
					outFileName, postedBy, report.getId());
		} catch (Exception e) {
			log.error("build PDF process error");
			Status status = new Status();
			status.setDescription("build PDF process error");
			ApiException ae = new ApiException(status, e);
			exchange.setStatusCode(ae.getStatus().getStatusCode());
			exchange.getResponseSender().send(ae.getStatus().toString());
		}
	}

}

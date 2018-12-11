package com.visoft.modules.docMng.handlers;

import java.net.URLEncoder;
import java.nio.ByteBuffer;

import com.visoft.modules.docMng.DocMngService;
import com.visoft.modules.docMng.model.Report;
import com.visoft.modules.docMng.repositories.ReportRepo;
import com.visoft.utils.FileUtil;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.PathTemplateMatch;

/**
 * @author vlad
 *
 */
public class ReportDownloadHandler implements HttpHandler {

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		PathTemplateMatch pathMatch = exchange
				.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
		String templateId = pathMatch.getParameters().get("reportId");

		Report report = ReportRepo.findById(templateId);
		String mimeType = FileUtil.getFileType(report.getFileName());
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, mimeType);

		byte[] contentHash = DocMngService.getReportContent(report);

		String fileName = report.getFileName();
		exchange.getResponseHeaders()
				// TODO: replace in fileName '+' sign with space
				// on client
				.put(Headers.CONTENT_DISPOSITION, "inline; filename=\""
						+ URLEncoder.encode(fileName, "UTF-8") + "\"");
		exchange.getResponseSender().send(ByteBuffer.wrap(contentHash));

	}

}
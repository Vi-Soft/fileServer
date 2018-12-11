package com.visoft.modules.docMng.handlers;

import java.net.URLEncoder;
import java.nio.ByteBuffer;

import com.visoft.modules.docMng.ipfs.IPFSUtil;
import com.visoft.modules.docMng.model.DocFile;
import com.visoft.modules.docMng.repositories.DocRepo;
import com.visoft.utils.FileUtil;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.PathTemplateMatch;

/**
 * @author vlad
 *
 */
public class FileDownloadHandler implements HttpHandler {

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		PathTemplateMatch pathMatch = exchange
				.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
		String fileId = pathMatch.getParameters().get("fileId");

		DocFile docFile = DocRepo.findById(fileId);
		
		String mimeType = FileUtil.getFileType(docFile.getFileName());
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, mimeType);

		byte[] contentHash = IPFSUtil.cat(docFile.getFileContentHash());

		String fileName = docFile.getFileName();
		// from
		// https://github.com/StubbornJava/StubbornJava/blob/master/stubbornjava-examples/src/main/java/com/stubbornjava/examples/undertow/contenttypes/ContentTypeHandlers.java
		exchange.getResponseHeaders()
				// TODO: replace in fileName '+' sign with space
				// on client
				.put(Headers.CONTENT_DISPOSITION, "inline; filename=\""
						+ URLEncoder.encode(fileName, "UTF-8") + "\"");
		exchange.getResponseSender().send(ByteBuffer.wrap(contentHash));
	}

}
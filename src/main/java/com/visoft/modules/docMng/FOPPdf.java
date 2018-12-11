package com.visoft.modules.docMng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.json.JSONObject;
import org.json.XML;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.xml.sax.SAXException;

import com.networknt.exception.ApiException;
import com.visoft.modules.docMng.ipfs.IPFSUtil;
import com.visoft.modules.docMng.model.DocTemplate;
import com.visoft.modules.docMng.model.TemplateDTO;

public class FOPPdf {

	// private final static String templatesRepository =
	// "C:/Users/vlad/AppData/Local/tmp";

	private final static String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>";

	private final static String xmlEnd = "</root>";

	public static ByteArrayOutputStream getPDFFile(TemplateDTO template)
			throws IOException, TransformerException, SAXException,
			ParserConfigurationException, ApiException {

		JSONObject jsonObject = new JSONObject(template);
		String str = xmlStart + XML.toString(jsonObject) + xmlEnd;
		StreamSource xmlSource = new StreamSource(
				new ByteArrayInputStream(str.getBytes()));

		final URL configFileUrl = FOPPdf.class.getClassLoader()
				.getResource("userconfig.xml");
		final File userConfigXml = new File(configFileUrl.getFile());
		FopFactory fopFactory = FopFactory.newInstance(userConfigXml);
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent,
					out);
			TransformerFactory factory = TransformerFactory.newInstance();

			DocTemplate docTemplate = DocMngService
					.getDocTemplate(template.getTemplateId());
			byte[] templateContent = IPFSUtil
					.cat(docTemplate.getFileContentHash());
			ByteArrayInputStream is = new ByteArrayInputStream(templateContent);
			StreamSource xsltSource = new StreamSource(is);
			is.close();

			Transformer transformer = factory.newTransformer(xsltSource);
			Result res = new SAXResult(fop.getDefaultHandler());
			transformer.transform(xmlSource, res);
			return out;

		} finally {
			out.close();
		}

	}
}

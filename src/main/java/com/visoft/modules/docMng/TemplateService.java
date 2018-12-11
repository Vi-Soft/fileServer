package com.visoft.modules.docMng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.networknt.exception.ApiException;
import com.visoft.exceptions.TemplateValidationException;
import com.visoft.modules.docMng.model.TemplateDTO;

public class TemplateService {

	private static final String NOT_EXIST = "Does not exist";
	private static final String NOT_ENOUGH_INFORMATION = "Not enough information ";

	public static ByteArrayOutputStream getPDFFromTemplate(TemplateDTO template)
			throws IOException, TransformerException, SAXException,
			ParserConfigurationException, ApiException {
		Map<String, String> templateValid = templateValidator(template);
		templateValid.putAll(checkBody(template));
		if (templateValid.isEmpty()) {
			return FOPPdf.getPDFFile(template);
		}
		throw new TemplateValidationException(NOT_ENOUGH_INFORMATION,
				templateValid);
	}

	private static Map<String, String> templateValidator(TemplateDTO template) {
		Map<String, String> templateValid = new HashMap<>();
		if (template.getTemplateName() == null
				|| template.getTemplateName().equals("")) {
			templateValid.put("templateName", NOT_EXIST);
		}
		if (template.getProjectId() == null
				|| template.getProjectId().equals("")) {
			templateValid.put("projectId", NOT_EXIST);
		}
		if (template.getOutPutName() == null
				|| template.getOutPutName().equals("")) {
			templateValid.put("outPutName", NOT_EXIST);
		}
		return templateValid;
	}

	private static Map<String, String> checkBody(TemplateDTO template) {
		Map<String, String> bodyValid = new HashMap<>();
		if (template.getBody() == null || template.getBody().equals("")) {
			bodyValid.put("body", NOT_EXIST);
		}
		return bodyValid;
	}

}

package com.visoft.modules.docMng.model;

import java.util.Map;

public class TemplateDTO {
	private String templateId;
	private String templateName;
	private String projectId;
	private String taskId;
	private String outPutName;
	private Map<String, String> templateBody;
	private Object body;

	public TemplateDTO() {
	}

	public TemplateDTO(String templateId, String templateName, String projectId,
			String taskId, String outPutName, Map<String, String> templateBody,
			Object body) {
		this.templateId = templateId;
		this.templateName = templateName;
		this.projectId = projectId;
		this.taskId = taskId;
		this.outPutName = outPutName;
		this.templateBody = templateBody;
		this.body = body;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setOutPutName(String outPutName) {
		this.outPutName = outPutName;
	}

	public void setTemplateBody(Map<String, String> templateBody) {
		this.templateBody = templateBody;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getOutPutName() {
		return outPutName;
	}

	public Map<String, String> getTemplateBody() {
		return templateBody;
	}

	public Object getBody() {
		return body;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result
				+ ((outPutName == null) ? 0 : outPutName.hashCode());
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result
				+ ((templateBody == null) ? 0 : templateBody.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
		result = prime * result
				+ ((templateName == null) ? 0 : templateName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateDTO other = (TemplateDTO) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (outPutName == null) {
			if (other.outPutName != null)
				return false;
		} else if (!outPutName.equals(other.outPutName))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (templateBody == null) {
			if (other.templateBody != null)
				return false;
		} else if (!templateBody.equals(other.templateBody))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TemplateDTO [templateId=" + templateId + ", templateName="
				+ templateName + ", projectId=" + projectId + ", taskId="
				+ taskId + ", outPutName=" + outPutName + ", templateBody="
				+ templateBody + ", body=" + body + "]";
	}

}

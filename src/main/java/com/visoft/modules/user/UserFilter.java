package com.visoft.modules.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author vlad
 *
 */
public class UserFilter {

	public static final String NAME_CONTAINS = "name_contains";
	public static final String EMAIL_CONTAINS = "email_contains";

	private String nameContains;
	private String emailContains;

	@JsonProperty(NAME_CONTAINS)
	public String getNameContains() {
		return nameContains;
	}

	public void setNameContains(String nameContains) {
		this.nameContains = nameContains;
	}

	@JsonProperty(EMAIL_CONTAINS)
	public String getEmailContains() {
		return emailContains;
	}

	public void setEmailContains(String emailContains) {
		this.emailContains = emailContains;
	}

	@Override
	public String toString() {
		return "UserFilter [nameContains=" + nameContains + ", emailContains="
				+ emailContains + "]";
	}

}

package com.visoft.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.GraphQLException;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;

/**
 * @author vlad
 *
 */
public final class HttpUtil {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	private HttpUtil() {

	}

	/**
	 * @param exchange
	 * @param fieldName
	 * @return
	 */
	public static String parseFromForm(final HttpServerExchange exchange,
			String fieldName) {
		final FormData data = exchange.getAttachment(FormDataParser.FORM_DATA);
		if (data != null) {
			final FormData.FormValue formValue = data.getFirst(fieldName);
			if (formValue != null) {
				return formValue.getValue();
			}
		}
		return null;
	}

	/**
	 * @param exchange
	 * @return String
	 */
	public static String getTokenFromExchange(
			final HttpServerExchange exchange) {
		final HeaderMap headerMap = exchange.getRequestHeaders();
		final HeaderValues headerValues = headerMap.get("Authorization");
		if (headerValues.isEmpty()) {
			throw new GraphQLException("The user is not authorised");
		}
		final String token = headerValues.getFirst();
		return token.replace("Bearer ", "");
	}

}

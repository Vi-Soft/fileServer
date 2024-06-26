package com.visoft.file.service.web.cors;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.visoft.file.service.web.cors.CorsHeaders.*;

public class CorsUtil {
    private static final Logger logger = LoggerFactory.getLogger(CorsUtil.class);

    public static boolean isCoreRequest(HeaderMap headers) {
        return headers.contains(ORIGIN)
                || headers.contains(ACCESS_CONTROL_REQUEST_HEADERS)
                || headers.contains(ACCESS_CONTROL_REQUEST_METHOD);
    }

    /**
     * Match the Origin header with the allowed origins.
     * If it doesn't match then a 403 response code is set on the response and it returns null.
     * @param exchange the current HttpExchange.
     * @param allowedOrigins list of sanitized allowed origins.
     * @return the first matching origin, null otherwise.
     * @throws Exception the checked exception
     */
    public static String matchOrigin(HttpServerExchange exchange, Collection<String> allowedOrigins) throws Exception {
        HeaderMap headers = exchange.getRequestHeaders();
        String[] origins = headers.get(Headers.ORIGIN).toArray();
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            for (String allowedOrigin : allowedOrigins) {
                for (String origin : origins) {
                    if (allowedOrigin.equalsIgnoreCase(sanitizeDefaultPort(origin))) {
                        return allowedOrigin;
                    }
                }
            }
        }
        String allowedOrigin = defaultOrigin(exchange);
        for (String origin : origins) {
            if (allowedOrigin.equalsIgnoreCase(sanitizeDefaultPort(origin))) {
                return allowedOrigin;
            }
        }
        logger.debug("Request rejected due to HOST/ORIGIN mis-match.");
        ResponseCodeHandler.HANDLE_403.handleRequest(exchange);
        return null;
    }

    /**
     * Determine the default origin, to allow for local access.
     * @param exchange the current HttpExchange.
     * @return the default origin (aka current server).
     */
    public static String defaultOrigin(HttpServerExchange exchange) {
        String host = NetworkUtils.formatPossibleIpv6Address(exchange.getHostName());
        String protocol = exchange.getRequestScheme();
        int port = exchange.getHostPort();
        //This browser set header should not need IPv6 escaping
        StringBuilder allowedOrigin = new StringBuilder(256);
        allowedOrigin.append(protocol).append("://").append(host);
        if (!isDefaultPort(port, protocol)) {
            allowedOrigin.append(':').append(port);
        }
        return allowedOrigin.toString();
    }

    private static boolean isDefaultPort(int port, String protocol) {
        return (("http".equals(protocol) && 80 == port) || ("https".equals(protocol) && 443 == port));
    }

    /**
     * Removes the port from a URL if this port is the default one for the URL's scheme.
     * @param url the url to be sanitized.
     * @return  the sanitized url.
     */
    public static String sanitizeDefaultPort(String url) {
        int afterSchemeIndex = url.indexOf("://");
        if(afterSchemeIndex < 0) {
            return url;
        }
        String scheme = url.substring(0, afterSchemeIndex);
        int fromIndex = scheme.length() + 3;
        //Let's see if it is an IPv6 Address
        int ipv6StartIndex = url.indexOf('[', fromIndex);
        if (ipv6StartIndex > 0) {
            fromIndex = url.indexOf(']', ipv6StartIndex);
        }
        int portIndex = url.indexOf(':', fromIndex);
        if(portIndex >= 0) {
            int port = Integer.parseInt(url.substring(portIndex + 1));
            if(isDefaultPort(port, scheme)) {
                return url.substring(0, portIndex);
            }
        }
        return url;
    }

    public static boolean isPreflightedRequest(HttpServerExchange exchange) {
        return Methods.OPTIONS.equals(exchange.getRequestMethod()) && isCoreRequest(exchange.getRequestHeaders());
    }
}

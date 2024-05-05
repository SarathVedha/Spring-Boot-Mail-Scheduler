package com.vedha.mail.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

public class AppUtil {

    private static final List<String> POSSIBLE_IP_HEADERS = List.of(
            "X-Forwarded-For",
            "HTTP_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_VIA",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "REMOTE_ADDR"
    );

    public static String extractRemoteIpAddress(HttpServletRequest httpServletRequest) {

        //return ipHeaders.stream().map(httpServletRequest::getHeader).filter(StringUtils::hasLength).findFirst().orElse("no ip found");
        for (String ipHeader : POSSIBLE_IP_HEADERS) {
            String headerValue = Collections.list(httpServletRequest.getHeaders(ipHeader)).stream()
                    .filter(StringUtils::hasLength)
                    .findFirst()
                    .orElse(null);

            if (headerValue != null) {
                return headerValue;
            }
        }

        return httpServletRequest.getRemoteAddr();
    }
}

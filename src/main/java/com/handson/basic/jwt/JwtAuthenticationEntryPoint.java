package com.handson.basic.jwt;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");

        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Access-Control-Allow-Headers, Content-Type, Authorization, Origin, Accept, Referer, User-Agent," +
                "Origin, sec-fetch-mode, sec-fetch-site, Accept, CloudFront-Viewer-Country, CloudFront-Is-Tablet-Viewer, CloudFront-Forwarded-Proto, "+
                "X-Forwarded-Proto, User-Agent, Referer, CloudFront-Is-Mobile-Viewer, CloudFront-Is-SmartTV-Viewer, Host, Accept-Encoding, Pragma, "+
                "X-Forwarded-Port, X-Amzn-Trace-Id, Via, Cache-Control, X-Forwarded-For, X-Amz-Cf-Id, Accept-Language, CloudFront-Is-Desktop-Viewer, "+
                "sec-fetch-dest"
        );
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

    }
}

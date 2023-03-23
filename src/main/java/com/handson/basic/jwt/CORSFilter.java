package com.handson.basic.jwt;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by SEyal on 3/24/2020.
 */
@Component
class CORSFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
      //  if (request.getHeader("Origin") != null && request.getHeader("Origin").toLowerCase().length() > 0) {
            response.setHeader("Access-Control-Allow-Origin", "*");//request.getHeader("Origin"));

            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            response.setHeader("Access-Control-Max-Age", "3600");

            if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
                response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Access-Control-Allow-Headers, Content-Type, Authorization, Origin, Accept, Referer, User-Agent," +
                        "Origin, sec-fetch-mode, sec-fetch-site, Accept, CloudFront-Viewer-Country, CloudFront-Is-Tablet-Viewer, CloudFront-Forwarded-Proto, " +
                        "X-Forwarded-Proto, User-Agent, Referer, CloudFront-Is-Mobile-Viewer, CloudFront-Is-SmartTV-Viewer, Host, Accept-Encoding, Pragma, " +
                        "X-Forwarded-Port, X-Amzn-Trace-Id, Via, Cache-Control, X-Forwarded-For, X-Amz-Cf-Id, Accept-Language, CloudFront-Is-Desktop-Viewer, " +
                        "sec-fetch-dest"
                );
            }
        //}

        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}

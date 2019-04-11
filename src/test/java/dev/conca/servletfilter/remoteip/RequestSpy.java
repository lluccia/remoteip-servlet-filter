package dev.conca.servletfilter.remoteip;

import javax.servlet.*;
import java.io.IOException;

class RequestSpy implements Filter {

    private String remoteAddr;
    private String remoteHost;

    @Override
    public void init(FilterConfig filterConfig) {
        // not used
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.getOutputStream().write("Test response body".getBytes());

        this.remoteAddr = request.getRemoteAddr();
        this.remoteHost = request.getRemoteHost();
    }

    @Override
    public void destroy() {
        // not used
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }
}

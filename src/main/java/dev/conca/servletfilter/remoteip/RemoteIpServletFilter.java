package dev.conca.servletfilter.remoteip;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteIpServletFilter implements Filter {

    private String[] remoteIpHeaders;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String remoteIpHeaders = filterConfig.getInitParameter("remoteIpHeaders");

        if (remoteIpHeaders != null)
            this.remoteIpHeaders = splitAndTrim(remoteIpHeaders);
        else
            this.remoteIpHeaders = new String[] {"x-forwarded-for"};
    }

    private String[] splitAndTrim(String remoteIpHeadersParam) {
        List<String> remoteIpHeaders = new ArrayList<>();

        for (String header: remoteIpHeadersParam.split(","))
            remoteIpHeaders.add(header.trim());

        return remoteIpHeaders.toArray(new String[0]);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest)
            servletRequest = new RemoteIpHttpRequestWrapper((HttpServletRequest) servletRequest, remoteIpHeaders);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}

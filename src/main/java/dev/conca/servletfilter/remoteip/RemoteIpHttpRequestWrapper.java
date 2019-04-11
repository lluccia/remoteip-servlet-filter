package dev.conca.servletfilter.remoteip;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

public class RemoteIpHttpRequestWrapper extends ServletRequestWrapper {

    private HttpServletRequest servletRequest;
    private String[] remoteIpHeaders;

    public RemoteIpHttpRequestWrapper(HttpServletRequest servletRequest, String[] remoteIpHeaders) {
        super(servletRequest);
        this.servletRequest = servletRequest;
        this.remoteIpHeaders = remoteIpHeaders;
    }

    @Override
    public String getRemoteAddr() {
        if (wasProxied())
            return getProxiedAddress();

        return super.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        if (wasProxied())
            return getRemoteAddr();

        return super.getRemoteHost();
    }

    private boolean wasProxied() {
        return getProxiedAddress() != null;
    }


    private String getProxiedAddress() {
        for (String remoteIpHeader : remoteIpHeaders)
            if (servletRequest.getHeader(remoteIpHeader) != null)
                return getFirstElement(servletRequest.getHeader(remoteIpHeader));

        return null;
    }

    private String getFirstElement(String remoteIpHeaderValue) {
        if (remoteIpHeaderValue.contains(","))
            return remoteIpHeaderValue.substring(0, remoteIpHeaderValue.indexOf(",")).trim();

        return remoteIpHeaderValue.trim();
    }
}

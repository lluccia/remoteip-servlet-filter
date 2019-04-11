# Remote IP servlet filter
Servlet filter for capturing the original client IP when the http request is proxied.

Based on [org.apache.catalina.filters.RemoteIpFilter](https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/catalina/filters/RemoteIpFilter.html)

## Usage
The Filter implements **javax.servlet.Filter** from Servlet API 2.5 and can be configured using `web.xml` descriptor.
```xml
<filter>
    <filter-name>remoteIpServletFilter</filter-name>
    <filter-class>dev.conca.servletfilter.remoteip.RemoteIpServletFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>remoteIpServletFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

## Init parameters

|Name|Description|Default|Example|
|-|-|-|-|
|remoteIpHeaders|Comma separated list of header names to lookup for remote IP|`x-forwarded-for`|`x-client-ip,x-forwarded-for`|

```xml
<filter>
    <filter-name>remoteIpServletFilter</filter-name>
    <filter-class>dev.conca.servletfilter.remoteip.RemoteIpServletFilter</filter-class>

    <init-param>
        <param-name>remoteIpHeaders</param-name>
        <param-value>x-client-ip,x-forwarded-for</param-value>
    </init-param>
</filter>
```
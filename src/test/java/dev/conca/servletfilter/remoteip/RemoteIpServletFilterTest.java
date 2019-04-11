package dev.conca.servletfilter.remoteip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.*;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class RemoteIpServletFilterTest {

	private MockHttpServletRequest httpServletRequest;

	private MockHttpServletResponse httpServletResponse;

	private MockFilterChain filterChain;

	private RemoteIpServletFilter proxiedRemoteAddressFilter;

	private RequestSpy requestSpy;

	@BeforeEach
	public void setUp() throws ServletException {
		httpServletRequest = new MockHttpServletRequest("GET", "http://localhost:8080/test");
		httpServletRequest.addHeader("Accept", "application/json");

		httpServletResponse = new MockHttpServletResponse();
		httpServletResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);


		MockFilterConfig filterConfig = new MockFilterConfig();
		proxiedRemoteAddressFilter = new RemoteIpServletFilter();
		proxiedRemoteAddressFilter.init(filterConfig);
				
		requestSpy = new RequestSpy();
		filterChain = new MockFilterChain(new HttpRequestHandlerServlet(), requestSpy);
	}

	@Test
	public void nonProxiedRequestResolvesOriginalAddress() throws IOException, ServletException {
		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("127.0.0.1");
		assertThat(requestSpy.getRemoteHost()).isEqualTo("localhost");
	}

	@Test
	public void defaultRemoteIpHeaderIsXForwardedFor() throws IOException, ServletException {
		httpServletRequest.addHeader("x-forwarded-for", "1.2.3.4");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("1.2.3.4");
		assertThat(requestSpy.getRemoteHost()).isEqualTo("1.2.3.4");
	}

	@Test
	public void headerNamesAreCaseInsensitive() throws IOException, ServletException {
		httpServletRequest.addHeader("X-FoRwArDeD-fOr", "1.2.3.4");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("1.2.3.4");
	}

	@Test
	public void headerValueMayContainMultipleIpsForClientAndProxies() throws IOException, ServletException {
		// https://en.wikipedia.org/wiki/X-Forwarded-For

		httpServletRequest.addHeader("x-forwarded-for", "client, proxy1, proxy2");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("client");
	}

	@Test
	public void remoteIpHeadersAreConfigurable() throws IOException, ServletException {
		initFilterWithRemoteIpHeaderConfig("X-Client-IP");

		httpServletRequest.addHeader("x-client-ip", "1.2.3.4");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("1.2.3.4");
	}

	@Test
	public void headersAreEvaluatedFromLeftToRight_matchFirst() throws IOException, ServletException {
		initFilterWithRemoteIpHeaderConfig("X-Client-IP,x-forwarded-for");

		httpServletRequest.addHeader("x-client-ip", "1.2.3.4");
		httpServletRequest.addHeader("x-forwarded-for", "4.3.2.1");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("1.2.3.4");
	}

	@Test
	public void headersAreEvaluatedFromLeftToRight_matchSecond() throws IOException, ServletException {
		initFilterWithRemoteIpHeaderConfig("X-Client-IP,x-forwarded-for");

		httpServletRequest.addHeader("x-forwarded-for", "4.3.2.1");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteAddr()).isEqualTo("4.3.2.1");
	}

	@Test
	public void headerConfigMayContainSpaces() throws IOException, ServletException {
		initFilterWithRemoteIpHeaderConfig("X-Client-IP, x-forwarded-for");

		httpServletRequest.addHeader("x-client-ip", "1.2.3.4");

		proxiedRemoteAddressFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		assertThat(requestSpy.getRemoteHost()).isEqualTo("1.2.3.4");
	}

	private void initFilterWithRemoteIpHeaderConfig(String headerConfig) throws ServletException {
		MockFilterConfig filterConfig = new MockFilterConfig();
		filterConfig.addInitParameter("remoteIpHeaders", headerConfig);

		proxiedRemoteAddressFilter = new RemoteIpServletFilter();
		proxiedRemoteAddressFilter.init(filterConfig);

		requestSpy = new RequestSpy();
		filterChain = new MockFilterChain(new HttpRequestHandlerServlet(), requestSpy);
	}

}
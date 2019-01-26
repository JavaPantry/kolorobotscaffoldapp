package com.avp.kolorobot.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component("AuditFilter")
public class AuditFilter  implements Filter {

	@Autowired
	RuntimeProfile runtimeProfile;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if(runtimeProfile != null){
			System.out.println("AuditFilter: runtimeProfile set in production? " + runtimeProfile.isProduction());
		} else {
			System.out.println("AuditFilter: runtimeProfile is NOT set");
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {}

	public void destroy() {}

}

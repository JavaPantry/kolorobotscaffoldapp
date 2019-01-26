
## Register Filter in Spring 4.x MVC project

###  Create new kolorobot scaffolding project
- Create new kolorobot scaffolding project [see](DayToDayNotes\2018\September\September11_SpringMvcScaffolding.md)
  - [kolorobot/spring-mvc-quickstart-archetype](https://github.com/kolorobot/spring-mvc-quickstart-archetype)
  - [Generated project @](C:\IntelliJ_WS_tutorials\kolorobotscaffoldapp)
  - add Tomcat Server configuration
    - add deploy WAR artifact
  - Run
  - SignIn admin/admin

### Push to git
- create [repository](https://github.com/JavaPantry/kolorobotscaffoldapp)

```
git init
git add .
git commit -m "first commit"
git remote add origin https://github.com/JavaPantry/kolorobotscaffoldapp.git
git push -u origin master
```

```
Alexei@Alexei-PC MINGW64 /c/IntelliJ_WS_tutorials/kolorobotscaffoldapp
$ git init
Initialized empty Git repository in c:/IntelliJ_WS_tutorials/kolorobotscaffoldapp/.git/

Alexei@Alexei-PC MINGW64 /c/IntelliJ_WS_tutorials/kolorobotscaffoldapp (master)
$ git add .

Alexei@Alexei-PC MINGW64 /c/IntelliJ_WS_tutorials/kolorobotscaffoldapp (master)
$ git commit -m "first commit"
[master (root-commit) e010339] first commit
 60 files changed, 2211 insertions(+)
 create mode 100644 .gitignore
 create mode 100644 ReadMe.md
 create mode 100644 pom.xml
 create mode 100644 src/main/java/com/avp/kolorobot/Application.java
 ...
 ...

Alexei@Alexei-PC MINGW64 /c/IntelliJ_WS_tutorials/kolorobotscaffoldapp (master)
$ git remote add origin https://github.com/JavaPantry/kolorobotscaffoldapp.git

Alexei@Alexei-PC MINGW64 /c/IntelliJ_WS_tutorials/kolorobotscaffoldapp (master)
$ git push -u origin master
Fatal: HttpRequestException encountered.
Username for 'https://github.com': JavaPantry
Counting objects: 99, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (83/83), done.
Writing objects: 100% (99/99), 195.08 KiB | 0 bytes/s, done.
Total 99 (delta 8), reused 0 (delta 0)
remote: Resolving deltas: 100% (8/8), done.
To https://github.com/JavaPantry/kolorobotscaffoldapp.git
 * [new branch]      master -> master
Branch master set up to track remote branch master from origin.

```

### Add filter to app
- [How to register a servlet filter in Spring MVC](https://www.mkyong.com/spring-mvc/how-to-register-a-servlet-filter-in-spring-mvc/)
- add fileter class AuditFilter

```
package com.avp.kolorobot.home;

import javax.servlet.*;
import java.io.IOException;


public class AuditFilter  implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("AuditFilter");
		chain.doFilter(request, response);
	}
	public void init(FilterConfig filterConfig) throws ServletException {}
	public void destroy() {}
}
```

- register in web.xml

```
<filter>
  <filter-name>AuditFilter</filter-name>
  <filter-class>com.avp.kolorobot.home.AuditFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>AuditFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```

### Add profiles to app

```
public interface RuntimeProfile {
	public boolean isProduction();
}


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component
@Profile("dev")
public class DebugProfileImpl implements RuntimeProfile {

	@Override
	public boolean isProduction() {
		return false;
	}
}

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component
@Profile("!dev")
public class ProdProfileImpl implements RuntimeProfile {

	@Override
	public boolean isProduction() {
		return true;
	}
}
```

## add Active profile in web.xml
- [spring-profiles](https://www.baeldung.com/spring-profiles)
- in web.xml
```
<context-param>
  <param-name>spring.profiles.active</param-name>
  <param-value>prod</param-value> <!-- [prod|dev] -->
</context-param>
```

- just to test activation in `com.avp.kolorobot.home.HomeController`

```
@Autowired
RuntimeProfile runtimeProfile;

@GetMapping("/")
String index(Principal principal, Model model) {
  model.addAttribute("springVersion", SpringVersion.getVersion());

  if(runtimeProfile != null){
    System.out.println("HomeController: runtimeProfile set in production? " + runtimeProfile.isProduction());
  } else {
    System.out.println("HomeController: runtimeProfile is NOT set");
  }

  return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
}
```

- confirm printing `HomeController: runtimeProfile set in production? true` every time you visit Home page

## Add runtimeProfile to Audit filter

- just to test activation in `com.avp.kolorobot.home.AuditFilter`
  - that will not bee enough because AuditFilter instantiated by servlet container from web.xml

```
@Component
public class AuditFilter  implements Filter {

	@Autowired
	RuntimeProfile runtimeProfile;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("AuditFilter");

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

```

- confirm prints `AuditFilter: runtimeProfile is NOT set`

## Add AuditFilter to WebAppInitializer
- comment filter registration in web.xml
- in com.avp.kolorobot.config.WebAppInitializer

```
protected Filter[] getServletFilters() {
  ...
  AuditFilter auditFilter = new AuditFilter();
  return new Filter[] {auditFilter, characterEncodingFilter, securityFilterChain};
}
```

- still not wired `AuditFilter: runtimeProfile is NOT set`
- in `com.avp.kolorobot.home.AuditFilter` add getter setter for `runtimeProfile`

- just to test activation in `com.avp.kolorobot.config.WebAppInitializer` add:

```    
@Autowired
RuntimeProfile runtimeProfile;

if(runtimeProfile != null){
    System.out.println("WebAppInitializer: runtimeProfile set in production? " + runtimeProfile.isProduction());
} else {
    System.out.println("WebAppInitializer: runtimeProfile is NOT set");
}
AuditFilter auditFilter = new AuditFilter(runtimeProfile);
return new Filter[] {auditFilter, characterEncodingFilter, securityFilterChain};
```

- In `RuntimeProfile` **RuntimeProfile not wired in**
  - NOT suppose to be wired during web app initialization
  - remove added to WebAppInitializer above


- [check how to register filter in spring](https://www.deadcoderising.com/2015-05-04-dependency-injection-into-filters-using-delegatingfilterproxy/)
  - [github](https://github.com/DeadCodeRising/inject-dependencies-into-your-filters)
  - [cloned](c:\IntelliJ_WS_tutorials\inject-dependencies-into-your-filters\)
- [DelegatingFilterProxy in Spring](https://www.baeldung.com/spring-delegating-filter-proxy)

## Finally AuditFilter wired with RuntimeProfile

### Wrap Audit filter into DelegatingFilterProxy

- define bean name `AuditFilter` in com.avp.kolorobot.home.AuditFilter
```
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

	// No Need for get/set: public RuntimeProfile getRuntimeProfile() {return runtimeProfile;}
	// No Need for get/set: public void setRuntimeProfile(RuntimeProfile runtimeProfile) {this.runtimeProfile = runtimeProfile;}
}
```


- in com.avp.kolorobot.config.WebAppInitializer instantiate DelegatingFilterProxy with bean name given to AuditFilter
```
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    ...
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        DelegatingFilterProxy securityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");

        DelegatingFilterProxy auditFilterProxy = new DelegatingFilterProxy("AuditFilter");

        return new Filter[] {auditFilterProxy, characterEncodingFilter, securityFilterChain};
    }
}
```



## Next TODO
- see how defined profiles in c:\IntelliJ_WS_tutorials\monolithic\pom.xml

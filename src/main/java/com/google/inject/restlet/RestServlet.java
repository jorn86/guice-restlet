package com.google.inject.restlet;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;

@Slf4j
@Singleton
public class RestServlet extends HttpServlet {
	private final ImmutableList<RestletMethod> getMethods;
	private final ImmutableList<RestletMethod> postMethods;
	private final ImmutableList<RestletMethod> putMethods;
	private final ImmutableList<RestletMethod> deleteMethods;
	private final Injector injector;
	
	@Inject
	public RestServlet(Injector injector, Set<RestletMethod> methods) {
		this.injector = injector;
		this.getMethods 	= methods(methods, WebMethod.GET);
		this.postMethods 	= methods(methods, WebMethod.POST);
		this.putMethods 	= methods(methods, WebMethod.PUT);
		this.deleteMethods 	= methods(methods, WebMethod.DELETE);
		
		log.info("Initialized {} with {} methods", getClass().getSimpleName(), methods.size());
	}
	
	private static ImmutableList<RestletMethod> methods(Set<RestletMethod> methods, final WebMethod method) {
		return ImmutableList.copyOf(Iterables.filter(methods, new Predicate<RestletMethod>() {
			@Override
			public boolean apply(RestletMethod input) {
				return input.getWebMethod() == method;
			}
		}));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(getMethods, req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(postMethods, req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(putMethods, req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(deleteMethods, req, resp);
	}
	
	private void handle(ImmutableList<RestletMethod> methods, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("Handling request {} {}", req.getMethod(), Strings.nullToEmpty(req.getRequestURL().toString()));
		
		RestletMethod method = findHandler(methods, req.getRequestURI());
		if (method == null) {
			log.warn("No method found for request to {}", req.getRequestURI());
			resp.sendError(404);
			return;
		}
		
		try {
			Object response = method.invoke(injector);
			if (response instanceof Response) {
				Response r = (Response) response;
				log.error("Got {}", r);
				resp.setStatus(r.getStatus());
				resp.getWriter().write(String.valueOf(r.getEntity()));
			}
			else {
				resp.getWriter().write(String.valueOf(response)); // TODO
			}
		}
		catch (WebApplicationException e) {
			log.error("Exception while handling request", e);
			resp.sendError(e.getResponse().getStatus(), String.valueOf(e.getResponse().getEntity()));
		}
		catch (RuntimeException e) {
			log.error("Exception while handling request", e);
			resp.sendError(500, e.getMessage());
		}
	}
	
	private RestletMethod findHandler(ImmutableList<RestletMethod> methods, String uri) {
		for (RestletMethod method : methods) {
			if (method.handles(uri)) {
				return method;
			}
		}
		return null;
	}
	
	private static final long serialVersionUID = 1L;
}

package com.google.inject.restlet;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import javax.ws.rs.Path;

import lombok.AllArgsConstructor;

import org.junit.Test;

public class PathTest {
	@Test
	public void testNoSlashes() {
		assertEquals("a/b/", RestletMethod.path("", new TestPath("a"), new TestPath("b")));
	}
	
	@Test
	public void testLeadingSlash() {
		assertEquals("a/b/", RestletMethod.path("", new TestPath("/a"), new TestPath("b")));
	}
	
	@Test
	public void testTrailingSlash() {
		assertEquals("a/b/", RestletMethod.path("", new TestPath("a"), new TestPath("b/")));
	}
	
	@Test
	public void testMiddleSlashes() {
		assertEquals("a/b/", RestletMethod.path("", new TestPath("a"), new TestPath("/b")));
		assertEquals("a/b/", RestletMethod.path("", new TestPath("a/"), new TestPath("b")));
		assertEquals("a/b/", RestletMethod.path("", new TestPath("a/"), new TestPath("/b")));
	}
	
	@Test
	public void testNullRestlet() {
		assertEquals("b/", RestletMethod.path("", null, new TestPath("b")));
	}
	
	@Test
	public void testNullMethod() {
		assertEquals("a/", RestletMethod.path("", new TestPath("a"), null));
	}
	
	@Test
	public void testNullBoth() {
		assertEquals("/", RestletMethod.path("", null, null));
	}
	
	@SuppressWarnings("all")
	@AllArgsConstructor
	private static class TestPath implements Path {
		private final String value;
		
		@Override
		public Class<? extends Annotation> annotationType() {
			return Path.class;
		}
		
		@Override
		public String value() {
			return value;
		}
	}
}

/*
 * Copyright (c) 2012 Unicode Collective London > Oxml contributors
 * This program is made available under the terms of the MIT License:
 * http://www.opensource.org/licenses/MIT
 */
package com.unicodecollective.oxml;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.unicodecollective.oxml.AbstractElementParser;
import com.unicodecollective.oxml.OxmlParser;

public class OxmlParserTests {

	private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
	
	@Test
	public void singleEmptyElement() throws XMLStreamException {
		assertEquals(new TopLevel(null), parse("<top-level/>"));
	}

	@Test
	public void singleEmptyElementWithAttribute() throws XMLStreamException {
		assertEquals(new TopLevel("myattr"), parse("<top-level attr=\"myattr\"/>"));
	}
	
	@Test
	public void singleEmptyElementWithWrongElementNameReturnsNull() throws XMLStreamException {
		assertNull(parse("<dont-know-about-this></dont-know-about-this>"));
		assertNull(parse("<nor-this/>"));
	}
	
	@Test
	public void singleElementWithCloseTag() throws XMLStreamException {
		assertEquals(new TopLevel("myattr"), parse("<top-level attr=\"myattr\"></top-level>"));
	}
	
	@Test
	public void singleElementWithContent() throws XMLStreamException {
		assertEquals(new TopLevel("myattr", "Content Here!"), parse("<top-level attr=\"myattr\">Content Here!</top-level>"));
	}
	
	@Test
	public void elementWithChild() throws XMLStreamException {
		assertEquals(new TopLevel("myattr", asList(new SecondLevel("Content!"))), parse("<top-level attr=\"myattr\"><second-level>Content!</second-level></top-level>"));
	}
	
	@Test
	public void elementWithMultipleChildren() throws XMLStreamException {
		assertEquals(new TopLevel("myattr", asList(new SecondLevel("One"), new SecondLevel("Two"))), 
				parse("<top-level attr=\"myattr\"><second-level>One</second-level><second-level>Two</second-level></top-level>"));
	}
	
	@Test
	public void unknownSecondLevelChildrenAreIgnored() throws XMLStreamException {
		assertEquals(new TopLevel("myattr", asList(new SecondLevel("One"))), 
				parse("<top-level attr=\"myattr\"><second-level>One</second-level><dont-know-this-one><nor-this><nope>Lost!</nope></nor-this></dont-know-this-one></top-level>"));
	}
	
	@Test
	public void unknownThirdLevelChildrenAreIgnored() throws XMLStreamException {
		assertEquals(new TopLevel("myattr", asList(new SecondLevel())), 
				parse("<top-level attr=\"myattr\"><second-level><dont-know-about-this>Nope!</dont-know-about-this></second-level></top-level>"));
	}

	@Test
	public void whiteSpaceIsIgnored() throws XMLStreamException {
		assertEquals(new TopLevel("myattr", asList(new SecondLevel())), 
				parse("  <top-level attr=\"myattr\">\t\n <second-level  >\n <dont-know-about-this>Nope!</dont-know-about-this></second-level>\t  </top-level>  "));
	}

	/**
	 * Note that any whitespace before the XML declaration will cause an error. It's not legal and the Stax parser say no. 
	 */		
	@Test
	public void xmlDeclarationIsIgnored() throws XMLStreamException {
		assertEquals(new TopLevel("myattr"),
				parse("<?xml version = \"1.0\" ?><top-level attr=\"myattr\"></top-level>"));
	}
	
	@Test
	public void dtdIsIgnored() throws XMLStreamException {
		assertEquals(new TopLevel("myattr"),
				parse("<!DOCTYPE top-level SYSTEM \"MY.DTD\"><top-level attr=\"myattr\"></top-level>"));
	}
	
	
	/* --- Private Helpers --- */
	
	private TopLevel parse(String xml) throws XMLStreamException {
		return new OxmlParser().parse(stringReader(xml), new TopLevelParser());
	}

	private XMLEventReader stringReader(String xml) throws XMLStreamException {
		return xmlInputFactory.createXMLEventReader(IOUtils.toInputStream(xml));
	}

	private static final class TopLevelParser extends AbstractElementParser<TopLevel> {
		
		private SecondLevelParser secondLevelParser = new SecondLevelParser();
		private List<SecondLevel> children;
		
		public TopLevelParser() {
			super("top-level");
		}

		@Override
		protected TopLevel buildObject() {
			return children != null ? 
					new TopLevel(attributes.get("attr"), content, children) : 
						new TopLevel(attributes.get("attr"), content);
		}
		
		@Override
		protected void processChild(XMLEventReader reader) throws XMLStreamException {
			if (children == null) children = new ArrayList<SecondLevel>();
			SecondLevel child = secondLevelParser.parse(reader);
			if (child != null) children.add(child);
		}
		
	}
	
	private static final class SecondLevelParser extends AbstractElementParser<SecondLevel> {
		
		public SecondLevelParser() {
			super("second-level");
		}
		
		@Override
		protected SecondLevel buildObject() {
			return new SecondLevel(content);
		}
		
	}

	private static final class TopLevel {

		private String attr;
		private String content;
		private List<SecondLevel> secondLevels;
		
		public TopLevel(String attr, String content, List<SecondLevel> secondLevels) {
			this.attr = attr;
			this.content = content;
			this.secondLevels = secondLevels;			
		}
		
		public TopLevel(String attr, List<SecondLevel> secondLevels) {
			this.attr = attr;
			this.secondLevels = secondLevels;
		}
		
		public TopLevel(String attr, String content) {
			this.attr = attr;
			this.content = content;
		}

		public TopLevel(String attr) {
			this.attr = attr;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof TopLevel))
				return false;
			TopLevel that = (TopLevel) obj;
			return (this.content != null ? this.content.equals(that.content) : that.content == null)
					&& (this.attr != null ? this.attr.equals(that.attr) : that.attr == null)
					&& (this.secondLevels != null ? this.secondLevels.equals(that.secondLevels) : that.secondLevels == null);
		}

		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + (content != null ? content.hashCode() : 0);
			result = 31 * result + (attr != null ? attr.hashCode() : 0);
			result = 31 * result + (secondLevels != null ? secondLevels.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return format("<%s: {attr: %s, content: %s, secondLevel: %s}>", TopLevel.class.getSimpleName(), attr, content, secondLevels);
		}

	}
	
	private static final class SecondLevel {
		
		private String content;
		
		public SecondLevel() { }

		public SecondLevel(String content) {
			this.content = content;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof SecondLevel)) return false;
			SecondLevel that = (SecondLevel) obj;
			return (this.content != null ? this.content.equals(that.content) : that.content == null);
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + (content != null ? content.hashCode() : 0);
			return result;
		}
		
		@Override
		public String toString() {
			return format("<%s: {content: %s}>", SecondLevel.class.getSimpleName(), content);
		}
		
	}

}

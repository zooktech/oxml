/*
 * Copyright (c) 2012 Unicode Collective London > Oxml contributors
 * This program is made available under the terms of the MIT License:
 * http://www.opensource.org/licenses/MIT
 */
package com.unicodecollective.oxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class OxmlParser {
	
	public <T> T parse(XMLEventReader reader, ElementParser<T> elementParser) throws XMLStreamException {
		ignoreContentBeforeFirstStartTag(reader);
		T object = elementParser.parse(reader);
		while (reader.hasNext())
			reader.nextEvent();
		return object;
	}

	private void ignoreContentBeforeFirstStartTag(XMLEventReader reader) throws XMLStreamException {
		while (reader.hasNext() && !reader.peek().isStartElement())
			reader.nextEvent();
	}

}

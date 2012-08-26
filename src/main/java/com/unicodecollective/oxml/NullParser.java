/*
 * Copyright (c) 2012 Unicode Collective London > Oxml contributors
 * This program is made available under the terms of the MIT License:
 * http://www.opensource.org/licenses/MIT
 */
package com.unicodecollective.oxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;


public class NullParser extends AbstractElementParser<Void> {
	
	public NullParser() {
		super(null);
	}
	
	@Override
	protected Void buildObject() {
		return null;
	}
	
	@Override
	public void processChild(XMLEventReader reader) throws XMLStreamException {
		NULL_PARSER.parse(reader);
	}

}

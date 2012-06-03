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

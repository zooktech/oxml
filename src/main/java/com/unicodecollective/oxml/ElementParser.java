package com.unicodecollective.oxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface ElementParser<T> {
	
	T parse(XMLEventReader reader) throws XMLStreamException;

}

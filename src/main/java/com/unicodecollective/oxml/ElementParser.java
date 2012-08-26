/*
 * Copyright (c) 2012 Unicode Collective London > Oxml contributors
 * This program is made available under the terms of the MIT License:
 * http://www.opensource.org/licenses/MIT
 */
package com.unicodecollective.oxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface ElementParser<T> {
	
	T parse(XMLEventReader reader) throws XMLStreamException;

}

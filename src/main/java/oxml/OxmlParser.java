package oxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class OxmlParser {
	
	public <T> T parse(XMLEventReader reader, ElementParser<T> elementParser) throws XMLStreamException {
		while (reader.hasNext() && reader.nextEvent().isStartElement())
			reader.nextEvent();
		T object = elementParser.parse(reader);
		while (reader.hasNext())
			reader.nextEvent();
		return object;
	}

}

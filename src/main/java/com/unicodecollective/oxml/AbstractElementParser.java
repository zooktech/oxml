package com.unicodecollective.oxml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class AbstractElementParser<T> implements ElementParser<T> {
	
	public static final NullParser NULL_PARSER = new NullParser();

	private final QName name;
	protected Map<String, String> attributes;
	protected String content;
	
	public AbstractElementParser(String nameSpaceURI, String localName) {
		if (localName ==  null) name = null;
		else if (nameSpaceURI == null) name = new QName(localName);
		else name = new QName(nameSpaceURI, localName);
	}

	public AbstractElementParser(String localName) {
		this(null, localName);
	}
	
	@Override
	public T parse(XMLEventReader reader) throws XMLStreamException {
		XMLEvent event = reader.nextEvent();
		StartElement startElement = event.asStartElement();
		boolean isKnownElement = startElement.getName().equals(name);
		attributes = getAttributes(startElement);
		if (reader.peek().isCharacters()) {
			Characters characters = reader.nextEvent().asCharacters();
			if (!characters.isWhiteSpace()) {
				content = characters.getData();
			}
		}
		while (ignoreWhiteSpace(reader) && reader.hasNext() && !reader.peek().isEndElement()) {
			processChild(reader);
		}
		T object = null;
		if (isKnownElement) {
			object = buildObject();
		}
		consumeEndElement(reader);
		return object;
	}

	protected abstract T buildObject();

	protected void processChild(XMLEventReader reader) throws XMLStreamException {
		NULL_PARSER.parse(reader);
	}

	private void consumeEndElement(XMLEventReader reader) throws XMLStreamException {
		reader.nextEvent();
	}

	private boolean ignoreWhiteSpace(XMLEventReader reader) throws XMLStreamException {
		while (reader.peek().isCharacters() && reader.peek().asCharacters().isWhiteSpace()) {
			reader.nextEvent();
		}
		return true;
	}

	private Map<String, String> getAttributes(StartElement startElement) {
		Map<String, String> attributes = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Iterator<Attribute> attributeIterator = startElement.getAttributes();
		while (attributeIterator.hasNext()) {
			Attribute attribute = attributeIterator.next();
			attributes.put(attribute.getName().getLocalPart(), attribute.getValue());
		}
		return attributes;
	}

}

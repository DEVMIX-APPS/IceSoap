package com.alexgilleran.icesoap.xml;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.xmlpull.v1.XmlSerializer;

public class XMLNode extends XMLElement {
	private Collection<XMLElement> subElements;
	
	public XMLNode(String namespace, String name) {
		super(namespace, name);
		
		subElements = new LinkedList<XMLElement>();
	}
	
	public Collection<XMLElement> getSubElements() {
		return subElements;
	}

	public XMLNode addElement(String namespace, String name) {
		XMLNode newNode = new XMLNode(namespace, name);
		
		subElements.add(newNode);
		
		return newNode;
	}
	
	public XMLElement addElement(XMLElement element) {
		subElements.add(element);
		
		return element;
	}
	
	public XMLLeaf addElement(String namespace, String name, String value) {
		XMLLeaf newLeaf = new XMLLeaf(namespace, name, value);
		
		subElements.add(newLeaf);
		
		return newLeaf;
	}
	
	@Override
	protected void serializeContent(XmlSerializer cereal) throws IllegalArgumentException, IllegalStateException, IOException {
		for (XMLElement element: subElements) {
			element.serialize(cereal);
		}
	}
}
/*******************************************************************************
 * Copyright (c)2016 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.schemaparser;

import java.util.Stack;

import nz.co.senanque.asserts.MaduraAsserts;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * Accumulate a jdom example document for this XSD.
 * Every top level element tag is used to create an XML object in the structure
 *
 * @author Roger Parkinson
 *
 */
public class SchemaVisitorJdom implements SchemaVisitor {
	
	private final Document document;
	private final Stack<Element> elements = new Stack<>();
	private final String location;
	private final String rootLocation;
	private Namespace defaultns;
	
	public SchemaVisitorJdom(Element r, String loc, String rloc) {
		document = new Document(r);
		location = loc;
		rootLocation = rloc;
	}

	@Override
	public void initialize(String xsdpackageName, String targetNamespace) {
		MaduraAsserts.assertNotNull("unexpected null value for xsdpackageName",xsdpackageName);
		MaduraAsserts.assertNotNull("unexpected null value for targetNamespace",targetNamespace);
		Element rootElement = document.getDocument().getRootElement();
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addNamespaceDeclaration(xsi);
		defaultns = Namespace.getNamespace( targetNamespace);
		Namespace tns = Namespace.getNamespace("tns", targetNamespace);
		rootElement.addNamespaceDeclaration(xsi);
		rootElement.addNamespaceDeclaration(tns);
		rootElement.addNamespaceDeclaration(defaultns);
		rootElement.setAttribute("schemaLocation", targetNamespace+" "+location+" "+rootElement.getNamespaceURI()+" "+rootLocation, xsi);
		elements.push(rootElement);
	}

	@Override
	public void beginObject(ObjectDescriptor objectDescriptor) {
		Element root = elements.peek();
		Element o = new Element(objectDescriptor.getName(),defaultns);
		root.addContent(o);
		elements.push(o);
	}

	@Override
	public void endObject(ObjectDescriptor objectDescriptor) {
		elements.pop();
	}

	@Override
	public void beginField(FieldDescriptor fieldDescriptor) {
		Element root = elements.peek();
		Element o = new Element(fieldDescriptor.getName(),defaultns);
		root.addContent(o);
		elements.push(o);
	}

	@Override
	public void endField(FieldDescriptor fieldDescriptor) {
		elements.pop();
	}

	@Override
	public void terminate() {
		elements.pop();
	}

	public Document getDocument() {
		return document;
	}

}

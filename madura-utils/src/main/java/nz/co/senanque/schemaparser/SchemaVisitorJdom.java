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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author Roger Parkinson
 *
 */
public class SchemaVisitorJdom implements SchemaVisitor {
	
	private final Document document;
	private final Stack<Element> elements = new Stack<>();
	private final String location;
	private final String rootLocation;
	
	public SchemaVisitorJdom(Element r, String loc, String rloc) {
		document = new Document(r);
		location = loc;
		rootLocation = rloc;
	}

	@Override
	public void initialize(String xsdpackageName, String targetNamespace) {
		Element rootElement = document.getDocument().getRootElement();
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addNamespaceDeclaration(xsi);
		if (targetNamespace != null) {
			Namespace tns = Namespace.getNamespace("tns", targetNamespace);
			rootElement.addNamespaceDeclaration(xsi);
			rootElement.setAttribute("schemaLocation", targetNamespace+" "+location+" "+rootElement.getNamespaceURI()+" "+rootLocation, xsi);
		}
		elements.push(rootElement);
	}

	@Override
	public void beginObject(ObjectDescriptor objectDescriptor) {
		Element root = elements.peek();
		Element o = root.addContent(new Element(objectDescriptor.getName()));
		elements.push(o);
	}

	@Override
	public void endObject(ObjectDescriptor objectDescriptor) {
		elements.pop();
	}

	@Override
	public void beginField(FieldDescriptor fieldDescriptor) {
		Element root = elements.peek();
		Element o = root.addContent(new Element(fieldDescriptor.getName()));
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

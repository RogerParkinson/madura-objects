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

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * Accumulate a dom4j example document for this XSD.
 * 
 * @author Roger Parkinson
 *
 */
public class SchemaVisitorDom4j implements SchemaVisitor {
	
	private final Document document = DocumentHelper.createDocument();
	private final Stack<Element> elements = new Stack<>();
	private final QName rootName;
	private final String location;
	private final String rootLocation;
	
	public SchemaVisitorDom4j(QName r, String loc, String rloc) {
		rootName = r;
		location = loc;
		rootLocation = rloc;
	}

	@Override
	public void initialize(String xsdpackageName, String targetNamespace) {
		Element root = document.addElement(rootName );
		Namespace xsi = DocumentHelper.createNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.add(xsi);
		if (targetNamespace != null) {
			Namespace tns = DocumentHelper.createNamespace("tns", targetNamespace);
			root.add(tns);
			QName schemaLocation = DocumentHelper.createQName("schemaLocation", xsi);
			Attribute attribute = DocumentHelper.createAttribute(root, schemaLocation, targetNamespace+" "+location+" "+rootName.getNamespaceURI()+" "+rootLocation);
			root.add(attribute);
		}
		elements.push(root);
	}

	@Override
	public void beginObject(ObjectDescriptor objectDescriptor) {
		Element root = elements.peek();
		Element o = root.addElement(objectDescriptor.getName());
		elements.push(o);
	}

	@Override
	public void endObject(ObjectDescriptor objectDescriptor) {
		elements.pop();
	}

	@Override
	public void beginField(FieldDescriptor fieldDescriptor) {
		Element root = elements.peek();
		Element o = root.addElement(fieldDescriptor.getName());
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

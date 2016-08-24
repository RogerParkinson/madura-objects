package nz.co.senanque.schemaparser;

import java.util.Map;

/**
 * Visitors to the traversal method must implement this interface
 * 
 * @author Roger Parkinson
 *
 */
public interface SchemaVisitor {

	void initialize(String xsdpackageName, String targetNamespace, Map<String, ObjectDescriptor> classes);

	void beginObject(ObjectDescriptor objectDescriptor);

	void endObject(ObjectDescriptor objectDescriptor);

	void beginField(FieldDescriptor fieldDescriptor);

	void endField(FieldDescriptor fieldDescriptor);

	void terminate();

}

package nz.co.senanque.schemaparser;

public interface SchemaVisitor {

	void initialize(String xsdpackageName, String targetNamespace);

	void beginObject(ObjectDescriptor objectDescriptor);

	void endObject(ObjectDescriptor objectDescriptor);

	void beginField(FieldDescriptor fieldDescriptor);

	void endField(FieldDescriptor fieldDescriptor);

	void terminate();

}

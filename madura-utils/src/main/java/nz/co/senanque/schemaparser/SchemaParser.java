package nz.co.senanque.schemaparser;

import java.util.Set;

public interface SchemaParser {

	public abstract FieldDescriptor findOperandInScope(String scope,
			String operand);

	public abstract Set<String> findOperandsInScope(String scope, String operand);

	public abstract EnumeratedConstant findConstantInScope(String currentScope,
			String constant);

	public abstract void traverse(SchemaVisitor visitor);

	public abstract String getTargetNamespace();

}
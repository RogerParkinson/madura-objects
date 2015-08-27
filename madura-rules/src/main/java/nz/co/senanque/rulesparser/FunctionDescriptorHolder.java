package nz.co.senanque.rulesparser;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public interface FunctionDescriptorHolder {

	public abstract void addFunction(FunctionDescriptor functionDescriptor);

	public abstract void addOperator(String operator, FunctionDescriptor fd);

	public abstract Set<Entry<String, FunctionDescriptor>> getOperators();

	public abstract Set<Entry<String, FunctionDescriptor>> getFunctions();

	public List<Class<?>> getExternalFunctionClasses();

}
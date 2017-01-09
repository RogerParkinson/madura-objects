package nz.co.senanque.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

/**
 * This helps when you want to pull properties from several sources and merge them into one, probably because
 * you want to inject them somewhere.
 * 
 * @author Roger Parkinson
 *
 */
public class PropertiesMerger implements FactoryBean<Properties> {
	
	private List<Properties> m_list = new ArrayList<Properties>();;

	public Properties getObject() throws Exception {
		Properties ret = new Properties();
		for (Properties properties: m_list) {
			ret.putAll(properties);
		}
		return ret;
	}

	public Class<Properties> getObjectType() {
		return Properties.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public List<Properties> getList() {
		return m_list;
	}

	public void setList(List<Properties> list) {
		m_list = list;
	}

}

/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
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
package nz.co.senanque.validationengine.metadata;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

import nz.co.senanque.validationengine.Property;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.annotations.ChoiceList;
import nz.co.senanque.validationengine.annotations.Description;
import nz.co.senanque.validationengine.annotations.Digits;
import nz.co.senanque.validationengine.annotations.History;
import nz.co.senanque.validationengine.annotations.Inactive;
import nz.co.senanque.validationengine.annotations.Label;
import nz.co.senanque.validationengine.annotations.Length;
import nz.co.senanque.validationengine.annotations.MapField;
import nz.co.senanque.validationengine.annotations.Range;
import nz.co.senanque.validationengine.annotations.ReadOnly;
import nz.co.senanque.validationengine.annotations.ReadPermission;
import nz.co.senanque.validationengine.annotations.Regex;
import nz.co.senanque.validationengine.annotations.Required;
import nz.co.senanque.validationengine.annotations.Secret;
import nz.co.senanque.validationengine.annotations.Unknown;
import nz.co.senanque.validationengine.annotations.WritePermission;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.choicelists.ChoiceListFactory;
import nz.co.senanque.validationengine.fieldvalidators.FieldValidator;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

/**
 * Generates the static metadata for all relevant classes
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.17 $
 */
@Component
public class AnnotationsMetadataFactory implements FactoryBean<EngineMetadata>, MessageSourceAware, BeanFactoryAware
{
	private static final Logger log = LoggerFactory.getLogger(AnnotationsMetadataFactory.class);
	
    private transient Set<Class<ValidationObject>> m_classes = new HashSet<Class<ValidationObject>>();
    @Value("${nz.co.senanque.validationengine.metadata.AnnotationsMetadataFactory.packages}")
    private transient String m_package;
    @Value("${nz.co.senanque.validationengine.metadata.AnnotationsMetadataFactory.choicesDocument:classpath:choices.xml}")
    private transient Resource m_choicesDocument;
    private transient Document m_choicesDoc;
    private transient Map<String,List<ChoiceBase>> m_choicesMap;
    private transient Map<String,ChoiceListFactory> m_choiceListFactories = new HashMap<String,ChoiceListFactory>();
    @Value("${nz.co.senanque.validationengine.metadata.AnnotationsMetadataFactory.fieldValidators:}")
    private String m_fieldValidators;
	private MessageSource m_messageSource;
	private DefaultListableBeanFactory m_beanFactory;


	public EngineMetadata getObject() throws Exception {
	    
	    final Map<Class<? extends Annotation>, Class<? extends FieldValidator<Annotation>>> validatorMap = getValidatorMap();
	    final Map<Class<?>,ClassMetadata> classMap = new HashMap<Class<?>,ClassMetadata>();

		for (Class<?> clazz: m_classes)
		{
			log.debug("class name {}",clazz);
			boolean classNeeded = true;
			final ClassMetadata classMetadata = new ClassMetadata();		
	        @SuppressWarnings("unchecked")
			Map<String,Property> propertyMap = ValidationUtils.getProperties((Class<? extends ValidationObject>) clazz);
	        for (Property property: propertyMap.values()) {
				Method method = property.getGetter();
                log.debug("method.getName() {}",method.getName());
                String mname = method.getName();
			    final String fieldName = property.getFieldName();
                final PropertyMetadataImpl fieldMetadata = new PropertyMetadataImpl(property, getMessageSource());
				boolean fieldNeeded = false;
				boolean foundDigits = false;
				boolean foundLength = false;
				int digitsLength = -1;
				for (Annotation fieldAnnotation: method.getAnnotations())
				{
					log.debug("field annotation {}",fieldAnnotation);
                    if (fieldAnnotation instanceof Label)
                    {
                        fieldMetadata.setLabelName(((Label)fieldAnnotation).labelName());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Inactive)
                    {
                        fieldMetadata.setInactive(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof ReadOnly)
                    {
                        fieldMetadata.setReadOnly(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Secret)
                    {
                        fieldMetadata.setSecret(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Unknown)
                    {
                        fieldMetadata.setUnknown(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Required)
                    {
                        fieldMetadata.setRequired(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Digits)
                    {
                        fieldMetadata.setFractionalDigits(((Digits)fieldAnnotation).fractionalDigits());
                        fieldNeeded = true;
                        foundDigits = true;
                        digitsLength = Integer.parseInt(((Digits)fieldAnnotation).integerDigits());
                        foundLength = true;
                    }
					if (fieldAnnotation instanceof Range)
					{
						fieldMetadata.setMinValue(((Range)fieldAnnotation).minInclusive());
						fieldMetadata.setMaxValue(((Range)fieldAnnotation).maxInclusive());
						fieldNeeded = true;
					}
					if (fieldAnnotation instanceof Description)
					{
						fieldMetadata.setDescription(((Description)fieldAnnotation).name());
						fieldNeeded = true;
					}
					if (fieldAnnotation instanceof History)
					{
						fieldMetadata.setHistory(((History)fieldAnnotation).expire(),((History)fieldAnnotation).entries());
						fieldNeeded = true;
					}
					if (fieldAnnotation instanceof Id)
					{
						fieldMetadata.setIdentifier(true);
					}
					if (fieldAnnotation instanceof Regex)
					{
						fieldMetadata.setRegex(((Regex)fieldAnnotation).pattern());
						fieldNeeded = true;
					}
//						if (fieldAnnotation instanceof BeanValidator)
//						{
//							fieldMetadata.setBean(((BeanValidator)fieldAnnotation).bean());
//							fieldMetadata.setParam(((BeanValidator)fieldAnnotation).param());
//							fieldNeeded = true;
//						}
                    if (fieldAnnotation instanceof MapField)
                    {
                        fieldMetadata.setMapField(((MapField)fieldAnnotation).name());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof WritePermission)
                    {
                        fieldMetadata.setPermission(((WritePermission)fieldAnnotation).name());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof ReadPermission)
                    {
                        fieldMetadata.setReadPermission(((ReadPermission)fieldAnnotation).name());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof ChoiceList)
                    {
                        final List<ChoiceBase> choiceList = m_choicesMap.get(((ChoiceList)fieldAnnotation).name());
                        fieldMetadata.setChoiceList(choiceList);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Length)
                    {
                        fieldMetadata.setMaxLength(((Length)fieldAnnotation).maxLength());
                        fieldNeeded = true;
                        foundLength = true;
                    }
                    Class<? extends FieldValidator<Annotation>> fvClass = validatorMap.get(fieldAnnotation.annotationType());
                    if (fvClass != null)
                    {
                        final FieldValidator<Annotation> fv = fvClass.newInstance();
                        fv.init(fieldAnnotation,fieldMetadata);
                        fieldMetadata.addConstraintValidator(fv);
                        fieldNeeded = true;
                   }                        
				}
				Column column = method.getAnnotation(Column.class);
				if (column != null) {
					int precision = column.precision();
					int fractional = column.scale();
					int length = column.length();
					if (!foundDigits) {
						if (fractional > 0) {
							fieldMetadata.setFractionalDigits(String.valueOf(fractional));
						}
					}
					if (!foundLength) {
						if (precision > 0 && length == 255) {
							length = column.precision()+((fractional >0)?1:0);
							fieldMetadata.setMaxLength(String.valueOf(length));
						} else {
							length = column.length();
							fieldMetadata.setMaxLength(String.valueOf(length));							
						}
					}
				}
				
				Field field;
				try {
					field = clazz.getField(mname);
					for (Annotation fieldAnnotation: field.getAnnotations())
					{
						if (fieldAnnotation instanceof XmlElement)
						{
							if (((XmlElement)fieldAnnotation).required())
							{
		                        fieldMetadata.setRequired(true);
		                        fieldNeeded = true;							
							}
						}
					}
				} catch (NoSuchFieldException e) {
					// ignore
				}
                Class<?> returnClass = method.getReturnType();
                Object[] t = returnClass.getEnumConstants();
                if (t != null)
                {
                    fieldNeeded = true;
                    fieldMetadata.setChoiceList(t);
                }
				if (!fieldNeeded)
				{
					if (m_classes.contains(returnClass) || returnClass.isAssignableFrom(List.class))
					{
						fieldNeeded = true;
					}
				}
				if (fieldNeeded)
				{
	                log.debug("fieldName added to metadata {}.{}",clazz.getName(),fieldName);
					classMetadata.addField(fieldName,fieldMetadata);
					classNeeded = true;
				} else {
					log.debug("fieldName not needed {}.{}",clazz.getName(),fieldName);
				}
			}
			if (classNeeded)
			{
                log.debug("Class added to metadata {}",clazz.getName());
				classMap.put(clazz, classMetadata);
			}
		}
		return new EngineMetadata(classMap, m_choicesDoc);
	}
	
//    private void figureTableRestriction(TableRestrictionBuilder tableRestrictionBuilder,Map<Class<?>,ClassMetadata> classMap) throws Exception
//    {
//        Class<?> clazz = tableRestrictionBuilder.getClazz();
//        ClassMetadata classMetadata = classMap.get(clazz);
//        if (classMetadata == null)
//        {
//            throw new RuntimeException("Invalid class specified: "+clazz);
//        }
//        Map<PropertyMetadataImpl,Object> fieldMap = new HashMap<PropertyMetadataImpl,Object>();
//        
//        for (String f:tableRestrictionBuilder.getColumns())
//        {
//            PropertyMetadataImpl pm = classMetadata.getField(f);
//            if (pm == null)
//            {
//                throw new RuntimeException("Invalid field name: "+f+" on class "+clazz);
//            }
//            fieldMap.put(pm,pm);
//        }
//        Set<PropertyMetadataImpl> fields = fieldMap.keySet();
//        PropertyMetadataImpl[] propertyMetadataArray = fields.toArray(new PropertyMetadataImpl[fields.size()]);
//        List<TableRestrictionRow> rows = tableRestrictionBuilder.populateTableRestriction(propertyMetadataArray);
//        TableRestriction tr = new TableRestriction(classMetadata,fieldMap,rows);
//        for (PropertyMetadata pm: fieldMap.keySet())
//        {
//            pm.addTableRestriction(tr);
//        }
//    }
//

	public Class<EngineMetadata> getObjectType() {
		return EngineMetadata.class;
	}

	public boolean isSingleton() {
		return true;
	}

    public Map<String,List<ChoiceBase>> getChoicesMap()
    {
        return m_choicesMap;
    }

    public Map<Class<? extends Annotation>, Class<? extends FieldValidator<Annotation>>> getValidatorMap()
    {
        Map<Class<? extends Annotation>,Class<? extends FieldValidator<Annotation>>> validators = new HashMap<Class<? extends Annotation>,Class<? extends FieldValidator<Annotation>>>();
		String basePackage = "nz/co/senanque/validationengine/fieldvalidators";
        try {
			scanPackageForValidators(basePackage,validators);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
        if (!StringUtils.isEmpty(m_fieldValidators)) {
        	StringTokenizer st = new StringTokenizer(m_fieldValidators,",");
        	while (st.hasMoreTokens()) {
        		basePackage = st.nextToken().trim();
        		try {
					scanPackageForValidators(basePackage,validators);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
        	}
        }
        return validators;
    }
    
    private void scanPackageForValidators(String basePackage, Map<Class<? extends Annotation>,Class<? extends FieldValidator<Annotation>>> validators) throws ClassNotFoundException {
    	
    	ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				true) {
			private String iface = FieldValidator.class.getCanonicalName();
			/**
			 * Check if the class has the right annotation
			 * @param metadataReader the ASM ClassReader for the class
			 * @return whether the class qualifies as a candidate component
			 */
			protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
				AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
				for (String n : metadata.getInterfaceNames()) {
					if (iface.equals(n)) {
						return true;
					}
				}
				return false;
			}

			/**
			 * Determine whether the given bean definition qualifies as candidate.
			 * <p>The default implementation checks whether the class is concrete
			 * (i.e. not abstract and not an interface). Can be overridden in subclasses.
			 * @param beanDefinition the bean definition to check
			 * @return whether the bean definition qualifies as a candidate component
			 */
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
			}
			
		};
		
//		String basePackage = "nz/co/senanque/validationengine/fieldvalidators";//nz.co.senanque.validationengine.fieldvalidators
		Set<BeanDefinition> components = provider
				.findCandidateComponents(basePackage.replace('.', '/'));
		for (BeanDefinition component : components) {
			@SuppressWarnings("unchecked")
			Class<? extends FieldValidator<Annotation>> class_ = (Class<? extends FieldValidator<Annotation>>) Class.forName(component.getBeanClassName());
            Type[] types = class_.getGenericInterfaces();
            ParameterizedType t0 = (ParameterizedType)types[0];
            @SuppressWarnings("unchecked")
			Class<? extends Annotation> p = (Class<? extends Annotation>)t0.getActualTypeArguments()[0];
            validators.put(p,class_);
		}
    }

    private void scanPackageForDomainObjects(String basePackage, Set<Class<ValidationObject>> domainObjects) throws ClassNotFoundException {
    	
    	ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				true) {
			private String iface = ValidationObject.class.getCanonicalName();
			/**
			 * Check if the class has the right annotation
			 * @param metadataReader the ASM ClassReader for the class
			 * @return whether the class qualifies as a candidate component
			 */
			protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
				AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
				for (String n : metadata.getInterfaceNames()) {
					if (iface.equals(n)) {
						return true;
					}
				}
				return false;
			}

			/**
			 * Determine whether the given bean definition qualifies as candidate.
			 * <p>The default implementation checks whether the class is concrete
			 * (i.e. not abstract and not an interface). Can be overridden in subclasses.
			 * @param beanDefinition the bean definition to check
			 * @return whether the bean definition qualifies as a candidate component
			 */
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
			}
			
		};
		
		Set<BeanDefinition> components = provider
				.findCandidateComponents(basePackage.replace('.', '/'));
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		for (BeanDefinition component : components) {
			@SuppressWarnings("unchecked")
			Class<ValidationObject> class_ = (Class<ValidationObject>)Class.forName(component.getBeanClassName(),true,cl);
            domainObjects.add(class_);
		}
    }

    public String getFieldValidators()
    {
        return m_fieldValidators;
    }
    public void setFieldValidators(String fieldValidators)
    {
        m_fieldValidators = fieldValidators;
    }

    @SuppressWarnings("unchecked")
	public void createChoiceMap(final Document document)
    {
        m_choicesMap = new HashMap<String,List<ChoiceBase>>();
        for (Element choicebases: (List<Element>)document.getRootElement().getChildren("ChoiceList"))
        {
            String name = choicebases.getAttributeValue("name");
            ChoiceListFactory clf = m_choiceListFactories.get(name);
            if (clf == null) {
            	m_choicesMap.put(name, defaultChoiceListFactory(choicebases));
            } else {
            	 m_choicesMap.put(name, clf.getChoiceList(m_messageSource));
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	private List<ChoiceBase> defaultChoiceListFactory(Element choicebases) {
        List<ChoiceBase> choiceBases = new ArrayList<ChoiceBase>();
        for (Element choicebase: (List<Element>)choicebases.getChildren("Choice"))
        {
            ChoiceBase choice = new ChoiceBase(choicebase.getAttributeValue("name"),choicebase.getText(),getMessageSource());
            choiceBases.add(choice);
        }
        return choiceBases;
    }

    public Map<String, ChoiceListFactory> getChoiceListFactories()
    {
        return m_choiceListFactories;
    }

    @PostConstruct
    public void init()
    {
    	try {
			StringTokenizer st = new StringTokenizer(m_package,",");
			while (st.hasMoreTokens()) {
				String basePackage = st.nextToken().trim();
				scanPackageForDomainObjects(basePackage,m_classes);
			}
			Map<String, ChoiceListFactory> map = m_beanFactory.getBeansOfType(ChoiceListFactory.class);
			m_choiceListFactories.putAll(map);
			SAXBuilder saxBuilder = new SAXBuilder();
			m_choicesDoc = saxBuilder.build(m_choicesDocument.getInputStream());
			createChoiceMap(m_choicesDoc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    public String getPackage()
    {
        return m_package;
    }

    public void setPackage(String package1)
    {
        m_package = package1;
    }

	public MessageSource getMessageSource() {
		return m_messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

	public Resource getChoicesDocument() {
		return m_choicesDocument;
	}

	public void setChoicesDocument(Resource choicesResource) {
		m_choicesDocument = choicesResource;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		m_beanFactory = (DefaultListableBeanFactory)beanFactory;
		
	}

	public void setChoiceListFactories(
			Map<String, ChoiceListFactory> choiceListFactories) {
		m_choiceListFactories = choiceListFactories;
	}
    
}

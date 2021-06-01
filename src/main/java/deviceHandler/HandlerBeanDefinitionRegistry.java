package deviceHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HandlerBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
	private ApplicationContext applicationContext;
	private Logger log = LoggerFactory.getLogger(HandlerBeanDefinitionRegistry.class);

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
		/**
		 * Get AutoImpl annotated interfaces that need to be implemented by default
		 * through dynamic agents
		 */
		Set<Class<?>> classes = getAutoImplClasses();
		for (Class<?> clazz : classes) {
			/**
			 * Get the generic typeName of the interface inherited from HandlerRouter, and
			 * pass it in to dynamicprroxybeanfactory It can be passed in to
			 * DynamicProxyBeanFactory to scan the implementation class of typeName, and
			 * then implement according to feign and url. Mode classification
			 */

			Type[] types = clazz.getGenericInterfaces();
			ParameterizedType type = (ParameterizedType) types[0];
			String typeName = type.getActualTypeArguments()[0].getTypeName();

			/**
			 * By injecting FactoryBean into spring container, HandlerInterfaceFactoryBean
			 * implements the following functions: 1.Calling dynamic proxy
			 * DynamicProxyBeanFactory to provide the default implementation of
			 * HandlerRouter sub interface 2.Inject the default implementation of the first
			 * step into the spring container
			 */

			HandlerRouterAutoImpl handlerRouterAutoImpl =clazz.getAnnotation(HandlerRouterAutoImpl.class);
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
			GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
			definition.getPropertyValues().add("interfaceClass", clazz);
			definition.getPropertyValues().add("typeName", typeName);
			definition.getPropertyValues().add("context", applicationContext);
			definition.setBeanClass(HandlerInterfaceFactoryBean.class);
			definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
			beanDefinitionRegistry.registerBeanDefinition(handlerRouterAutoImpl.name(), definition);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
			throws BeansException {
		log.info("------------------------>postProcessBeanFactory");
	}

	/**
	 * Scan all classes using HandlerRouterAutoImpl by reflection
	 * 
	 * @return
	 */
	private Set<Class<?>> getAutoImplClasses() {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage("deviceHandler"), new TypeAnnotationsScanner(),
				new SubTypesScanner());
		return reflections.getTypesAnnotatedWith(HandlerRouterAutoImpl.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		log.info("------------------->setApplicationContext");
	}

	/**
	 * Get all bean s of this type through class
	 * 
	 * @param <T>
	 *
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unused")
	private <T> Map<String, T> getBeans(Class<T> clazz) {
		return applicationContext.getBeansOfType(clazz);
	}

	@SuppressWarnings("unused")
	private String getYmlProperty(String propery) {
		return applicationContext.getEnvironment().getProperty(propery);
	}
}

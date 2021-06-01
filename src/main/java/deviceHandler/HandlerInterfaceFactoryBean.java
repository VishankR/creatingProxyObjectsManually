package deviceHandler;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class HandlerInterfaceFactoryBean<T> implements FactoryBean<T> {    
private Class<T> interfaceClass;
public Class<T> getInterfaceClass() {
	return interfaceClass;
}

public void setInterfaceClass(Class<T> interfaceClass) {
	this.interfaceClass = interfaceClass;
}

private String typeName;
public void setTypeName(String typeName) {
	this.typeName = typeName;
}

private ApplicationContext context;
public void setContext(ApplicationContext context) {
	this.context = context;
}

@Override
public T getObject() throws Exception {
    Object object = DynamicProxyBeanFactory.newMapperProxy(typeName, context, getInterfaceClass());
    return (T) object;
}

@Override
public Class<?> getObjectType() {
    return getInterfaceClass();
}

@Override
public boolean isSingleton() {
    return true;
}}

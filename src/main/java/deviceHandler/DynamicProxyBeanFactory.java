package deviceHandler;



import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicProxyBeanFactory implements InvocationHandler {

    private String className;
    private ApplicationContext applicationContext;
   private  Logger log = LoggerFactory.getLogger(DynamicProxyBeanFactory.class);

    private Map<ClientType, Object> clientMap = new HashMap<>(2);

    public DynamicProxyBeanFactory(String className, ApplicationContext applicationContext) {
        this.className = className;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (clientMap.size() == 0) {
            initClientMap();
        }
       
        Integer env = (Integer) args[0];
        return 1 == env.intValue() ? clientMap.get(ClientType.FEIGN) : clientMap.get(ClientType.URL);
    }

    private void initClientMap() throws ClassNotFoundException {
        //Get all implementation classes of the classStr interface
        Map<String,?> classMap = applicationContext.getBeansOfType(Class.forName(className));
        log.info("DynamicProxyBeanFactory className:{} impl class:{}",className,classMap);

        for (Map.Entry<String,?> entry : classMap.entrySet()) {
            //According to the ApiClientType annotation, the implementation classes are divided into two types: Feign and Url.
            ApiClient apiClient = entry.getValue().getClass().getAnnotation(ApiClient.class);
            if (apiClient == null) {
                continue;
            }
            clientMap.put(apiClient.type(), entry.getValue());
        }
        log.info("DynamicProxyBeanFactory clientMap:{}",clientMap);
    }


    public static <T> T newMapperProxy(String classStr,ApplicationContext applicationContext,Class<T> mapperInterface) {
        ClassLoader classLoader = mapperInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{mapperInterface};
        DynamicProxyBeanFactory proxy = new DynamicProxyBeanFactory(classStr,applicationContext);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }}
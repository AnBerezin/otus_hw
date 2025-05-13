package hw.otus.aop.ioc;

import hw.otus.aop.annotations.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class ProxyIoc {
    private static final Logger logger = LoggerFactory.getLogger(ProxyIoc.class);

    public static <T> T createClass(Class<?> interfaceName, Class<?> className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?> constructor = className.getConstructor();
        InvocationHandler handler = new ProxyInvocationHandler<>((T) constructor.newInstance(new Object[]{}));

        T result;
        try {
            result = (T) Proxy.newProxyInstance(ProxyIoc.class.getClassLoader(), new Class<?>[]{interfaceName}, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    static class ProxyInvocationHandler<T> implements InvocationHandler {
        private final T workerClass;
        private final Set<Method> logMarkedMethods = new HashSet<>();


        ProxyInvocationHandler(T myClass) {
            this.workerClass = myClass;
            discoveryClass(myClass.getClass());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (logMarked(method)) {
                logger.info("executed method: {}, param: {}", method.getName(), args);
            }
            return method.invoke(workerClass, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + "myClass=" + workerClass + '}';
        }

        private boolean logMarked(Method invokeMethod) {
            for (var method : logMarkedMethods) {
                if (method.getName().equals(invokeMethod.getName()) && Arrays.equals(method.getParameterTypes(), invokeMethod.getParameterTypes())) {
                    return true;
                }
            }
            return false;
        }

        private void discoveryClass(Class<?> clazz) {
            for (var method : clazz.getDeclaredMethods()) {
                for (var annotations : method.getAnnotations()) {
                    if (annotations instanceof Log) {
                        logMarkedMethods.add(method);
                    }
                }
            }
        }

    }
}

package hw.otus.aop.ioc;

import hw.otus.aop.annotations.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")
public class ProxyIoc {
    private static final Logger logger = LoggerFactory.getLogger(ProxyIoc.class);

    public static <T> T createClass(Class<?> interfaceName, Class<?> className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?> constructor = className.getConstructor();
        InvocationHandler handler = new ProxyInvocationHandler<>((T)constructor.newInstance(new Object[] {}));

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

        ProxyInvocationHandler(T myClass) {
            this.workerClass = myClass;
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

        private boolean logMarked(Method method) {
            Class<?> clazz = workerClass.getClass();
            Method classMethod;
            try {
                classMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return false;
            }

            for (var annotation : classMethod.getAnnotations()) {
                if (annotation instanceof Log) {
                    return true;
                }
            }
            return false;
        }
    }
}

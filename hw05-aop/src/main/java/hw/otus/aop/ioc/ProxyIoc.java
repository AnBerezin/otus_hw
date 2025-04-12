package hw.otus.aop.ioc;

import hw.otus.aop.WorkerImpl;
import hw.otus.aop.WorkerInterface;
import hw.otus.aop.annotations.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyIoc {
    private static final Logger logger = LoggerFactory.getLogger(ProxyIoc.class);

    public static WorkerInterface createClass() {

        InvocationHandler handler = new ProxyInvocationHandler(new WorkerImpl());
        return (WorkerInterface)
                Proxy.newProxyInstance(ProxyIoc.class.getClassLoader(), new Class<?>[]{WorkerInterface.class}, handler);
    }

    static class ProxyInvocationHandler implements InvocationHandler {
        private final WorkerInterface workerClass;

        ProxyInvocationHandler(WorkerInterface myClass) {
            this.workerClass = myClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (logMarked(method)) {
                logger.info("executed method:{}, param:{}", method, args);
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

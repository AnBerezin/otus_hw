package hw.otus.aop;

import hw.otus.aop.ioc.ProxyIoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WorkerDemo {
    private static final Logger logger = LoggerFactory.getLogger(WorkerDemo.class);

    public static void main(String[] args) {
        WorkerInterface worker = null;
        try {
            worker = ProxyIoc.createClass(WorkerInterface.class, WorkerImpl.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (worker != null) {
            worker.work("123");
            worker.work2("321", "123");
            worker.work3(1, "456", "654");
        }
    }
}

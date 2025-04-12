package hw.otus.aop;

import hw.otus.aop.ioc.ProxyIoc;

public class WorkerDemo {
    public static void main(String[] args) {
        WorkerInterface worker = ProxyIoc.createClass();
        worker.work("123");
        worker.work2("321", "123");
        worker.work3(1, "456", "654");
    }
}

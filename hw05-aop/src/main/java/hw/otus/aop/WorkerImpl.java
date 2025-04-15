package hw.otus.aop;

import hw.otus.aop.annotations.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerImpl implements WorkerInterface{
    @Log
    @Override
    public void work(String param) {

    }

    @Override
    public void work2(String param1, String param2) {

    }

    @Log
    @Override
    public void work3(int param1, String param2, String param3) {

    }
}

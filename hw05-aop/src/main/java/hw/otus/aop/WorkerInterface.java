package hw.otus.aop;

import hw.otus.aop.annotations.Log;

public interface WorkerInterface {
  void work(String param);
  void work2(String param1, String param2);
  void work3(int param1, String param2, String param3);
}

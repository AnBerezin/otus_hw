package hw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestFramework {
    private static final Logger log = LoggerFactory.getLogger(TestFramework.class);

    private static TestFramework instance;

    private final List<TestContainer> testContainerList = new ArrayList<>();

    public static void main(String... args) throws Exception {
        TestFramework.run(new String[]{"hw.CustomTest"});
    }

    public static void run(String[] classNames) throws ClassNotFoundException {
        if (instance == null) {
            instance = new TestFramework();
        }
        instance.loadTests(classNames);
        if (!instance.testContainerList.isEmpty()) {
            instance.runTest();
            instance.printResult();
        }
    }

    public void loadTests(String[] classNames) throws ClassNotFoundException {
        for (var className : classNames) {
            Class<?> clazz = Class.forName(className);
            testContainerList.add(new TestContainer(clazz));
        }
    }

    public void runTest() {
        testContainerList.forEach(TestContainer::runTest);
    }

    public void printResult() {
        List<TestResult> results = testContainerList.stream().flatMap(e -> e.getTestResultsList().stream()).toList();
        results.forEach(testResult -> log.info(testResult.toString()));
    }


}

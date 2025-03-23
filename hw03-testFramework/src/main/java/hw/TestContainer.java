package hw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

public class TestContainer {
    private static final Logger log = LoggerFactory.getLogger(TestContainer.class);

    private final Class<?> clazz;
    private Constructor<?> constructor;
    private static final String BEFORE_ANNOTATION = "@hw.Before()";
    private static final String TEST_ANNOTATION = "@hw.Test()";
    private static final String AFTER_ANNOTATION = "@hw.After()";

    //Контейнеры для методов
    private final List<Method> beforeMethodsList = new ArrayList<>();
    private final List<Method> afterMethodsList = new ArrayList<>();
    private final List<Method> testMethodsList = new ArrayList<>();

    //Контейнер результатов выполнения
    private final List<TestResult> testResultsList = new ArrayList<>();

    public List<TestResult> getTestResultsList() {
        return testResultsList;
    }

    public TestContainer(Class<?> clazz) {
        this.clazz = clazz;
        log.info("canonicalName:{}", clazz.getCanonicalName());
        try {
            discoveryClass();
        } catch (NoSuchMethodException e) {
            log.error("Method not found for Class {}", clazz.getCanonicalName());
            log.error(e.toString());
        }
    }


    // Метод для исследования класса
    // Сохраняет конструктор по умолчанию
    // Ищет методы с аннтоциями @Before, @After, @Test
    private void discoveryClass() throws NoSuchMethodException {
        this.constructor = clazz.getConstructor();

        for (var method : clazz.getDeclaredMethods()) {
            log.info(method.getName());
            for (var annotation : method.getAnnotations()) {
                log.info(annotation.toString());
                if (annotation.toString().equals(BEFORE_ANNOTATION)) {
                    beforeMethodsList.add(method);
                }
                if (annotation.toString().equals(AFTER_ANNOTATION)) {
                    afterMethodsList.add(method);
                }
                if (annotation.toString().equals(TEST_ANNOTATION)) {
                    testMethodsList.add(method);
                }
            }
        }
    }

    //Метод запуска тестов, если контейнер не пустой
    public void runTest() {
        if (!testMethodsList.isEmpty()) {
            testMethodsList.forEach(this::runTestMethods);
        }
    }

    //Метод по запуску методов с аннтоцией @Test
    //Использует конструктор без параметров
    //Создает новый экземпляр класса теста
    //Запускает все методы с аннотацией @Before
    //Выполняет один метод @Test
    //Запускает все методы с аннотацией @After
    private void runTestMethods(Method method) {
        Object classInstance;
        try {
            classInstance = constructor.newInstance();

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.toString());
            return;
        }
        if (runBeforeMethods(classInstance)) {
            TestResult testResult = new TestResult();
            testResult.setTestName(method.getName());
            try {
                runMethod(method, classInstance);
                testResult.setStatus(TestStatus.SUCCESS);
            } catch (RuntimeException | InvocationTargetException | IllegalAccessException e) {
                log.error(e.toString());
                testResult.setStatus(TestStatus.FAIL);
                testResult.setException(e);
            }
            testResultsList.add(testResult);
        }
        runAfterMethods(classInstance);
    }

    //Запуск всех методов с аннотацией @Before
    //Входной параметр инстанс класса, в котором будет вызываться метод
    private boolean runBeforeMethods(Object instance) {
        boolean result = true;
        if (!beforeMethodsList.isEmpty()) {
            for (var method : beforeMethodsList) {
                try {
                    runMethod(method, instance);
                } catch (RuntimeException | InvocationTargetException | IllegalAccessException e) {
                    log.error(e.toString());
                    result = false;
                    break;
                }

            }
        }
        return result;
    }

    //Запуск всех методов с аннотацией @After
    //Входной параметр инстанс класса, в котором будет вызываться метод
    private void runAfterMethods(Object instance) {
        if (!afterMethodsList.isEmpty()) {
            for (var method : afterMethodsList) {
                try {
                    runMethod(method, instance);
                } catch (RuntimeException | InvocationTargetException | IllegalAccessException e) {
                    log.error(e.toString());
                }
            }
        }
    }

    //Запуск метода
    //Входные параметры:
    // объект Method
    // инстанс класса, в котором будет вызываться метод
    private void runMethod(Method method, Object instance) throws RuntimeException, InvocationTargetException, IllegalAccessException {
        var result = method.invoke(instance);
    }

}

package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final Class<?> configClass;

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        configClass = initialConfigClass;
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        createComponents(getAnnotateMethodsSortedByOrder(configClass));
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private List<Method> getAnnotateMethodsSortedByOrder(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(method ->
                        method.getAnnotation(AppComponent.class).order()
                ))
                .collect(Collectors.toList());
    }

    private void createComponents(List<Method> configMethods) {
        try {
            Object instance = configClass.getDeclaredConstructor().newInstance();
            for (Method method : configMethods) {
                Object[] params = getParametersForMethod(method);
                Object component = method.invoke(instance, params);

                appComponents.add(component);
                if (!appComponentsByName.containsKey(method.getAnnotation(AppComponent.class).name())) {
                    appComponentsByName.put(method.getAnnotation(AppComponent.class).name(), component);
                } else {
                    throw new RuntimeException("В контексте не должно быть компонентов с одинаковым именем " + method.getAnnotation(AppComponent.class).name());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Object[] getParametersForMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Class<?> requiredParamType = parameters[i].getType();
            Object matchingParam = appComponents.stream()
                    .filter(requiredParamType::isInstance)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException(("Не найден подходящий параметр для " + requiredParamType.getName() + " (тип: " + requiredParamType.getSimpleName() + ")")));
            args[i] = matchingParam;
        }

        return args;
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> componentList = appComponents.stream()
                .filter(componentClass::isInstance)
                .toList();
        if (componentList == null) {
            throw new RuntimeException("Не найдено компонента для класс " + componentClass.getName());
        }

        if (componentList.size() > 1) {
            throw new RuntimeException("Найдено несколько экземпляров компонента для класс " + componentClass.getName());
        }

        return (C) componentList.get(0);
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.computeIfAbsent(componentName, key -> {
            throw new RuntimeException("Не найдено компонента для класс " + key);
        });
    }
}

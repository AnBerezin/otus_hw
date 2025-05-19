package ru.otus.dataprocessor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.otus.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        Map<String, Double> result = new TreeMap<>(Comparator.naturalOrder());
        // группирует выходящий список по name, при этом суммирует поля value
        var dataMap = data.stream().collect(Collectors.groupingBy(Measurement::name, Collectors.summingDouble(Measurement::value)));
        result.putAll(dataMap);
        return result;
    }
}

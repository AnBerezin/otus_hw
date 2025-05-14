package ru.otus.processor;

import ru.otus.model.Message;

public class ProcessorEvenSecondsException implements Processor{

    SecondsProvider secondsProvider;

    public ProcessorEvenSecondsException(SecondsProvider secondsProvider) {
        this.secondsProvider = secondsProvider;
    }

    @Override
    public Message process(Message message) {
        if (secondsProvider.getSeconds()%2 == 0) {
            throw new UnsupportedOperationException();
        } else {
            return message;
        }
    }
}

package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {

    Map<Long, Message> historyMap = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        historyMap.put(msg.getId(), msg.toBuilder().build());
        //throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        //throw new UnsupportedOperationException();
        return Optional.of(historyMap.get(id));
    }
}

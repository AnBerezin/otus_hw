package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage implements Cloneable{
    private List<String> data;

    public ObjectForMessage() {
    }

    private ObjectForMessage(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public ObjectForMessage clone() {
        return new ObjectForMessage(data == null ? null : new ArrayList<>(data));
    }
}

package com.mcstalker.setting;

import java.util.Locale;

public class Setting<T> {

    private final String name;
    private final String id;
    private final T defaultValue;
    private final Class<T> clazz;
    private T value;

    public Setting(String name, T defaultValue, Class<T> clazz) {
        this(name, name.toLowerCase(Locale.ENGLISH).replace(" ", "_"), defaultValue, clazz);
    }

    public Setting(String name, String id, T defaultValue, Class<T> clazz) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;
        this.clazz = clazz;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public T getDefaultValue(){
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return clazz;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", defaultValue=" + defaultValue +
                ", value=" + value +
                '}';
    }
}
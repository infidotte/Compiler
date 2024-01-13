package com.example.compiler.Tables;

import lombok.Getter;

import java.util.Map;

public class Table {
    protected final Map<String, Integer> keys;
    @Getter
    protected final Map<Integer, String> values;

    public Table(Map<String, Integer> keys, Map<Integer, String> values) {
        this.keys = keys;
        this.values = values;
    }

    public Integer get(String value) {
        return keys.get(value);
    }

    public String get(Integer key) {
        return values.get(key);
    }

    public boolean contains(String value) {
        return keys.containsKey(value) & values.containsValue(value);
    }

    public boolean contains(Integer key) {
        return keys.containsValue(key) & values.containsKey(key);
    }

    @Override
    public String toString() {
        return "Table{" +
                "keys=" + keys +
                ", values=" + values +
                '}';
    }
}

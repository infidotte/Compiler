package com.example.compiler.Tables;

import java.util.Map;

public class PutableTable extends Table {
    private int index = 0;

    public PutableTable(Map<String, Integer> keys, Map<Integer, String> values) {
        super(keys, values);
    }

    public int put(String value) {
        index++;
        if (values.putIfAbsent(index, value) == null &
                keys.putIfAbsent(value, index) == null) {
            return index;
        } else {
            values.remove(index);
            keys.remove(value);
            index--;
            return 0;
        }
    }
    public void clear(){
        this.keys.clear();
        this.values.clear();
    }
}

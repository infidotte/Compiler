package com.example.compiler.Util;

import com.example.compiler.Tables.Table;
import com.example.compiler.Tables.Tables;

public class Bracket {
    public final int table;
    public final int index;

    public final String word;

    public Bracket(String bracket) {
        int indexOfComma = bracket.indexOf(',');
        this.table = Integer.parseInt(bracket.substring(1, indexOfComma));
        this.index = Integer.parseInt(bracket.substring(indexOfComma + 1, bracket.length() - 1));
        word = word();
    }

    public String word() {
        Table t = table == 1 ? Tables.TW : table == 2 ? Tables.TL : table == 3 ? Tables.TI : Tables.TN;
        return t.get(index);
    }

    @Override
    public String toString() {
        return "(" + table + "," + index + ")";
    }
}

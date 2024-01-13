package com.example.compiler.Tables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Tables {

    //region all structures
    //main tables
    public static final Table TL;
    public static final Table TW;
    public static final PutableTable TI;
    public static final PutableTable TN;
    //lexical result list
    public static final List<String> LEXICAL_RESULT = new LinkedList<>();
    //syntactic result structures
    public static final Map<Integer, Integer> NUM_TYPES;
    public static final Map<Integer, Integer> ID_TYPES;
    public static final List<String> EXPRESSION = new LinkedList<>();
    public static final List<String> ASSIGMENT = new LinkedList<>();
    //semantic result structure
    public static final Map<String, Integer> EXP_TYPES;
    //endregion

    //region initialization
    static {
        TN = new PutableTable(new HashMap<>(), new HashMap<>());
        TI = new PutableTable(new HashMap<>(), new HashMap<>());
        ID_TYPES = new HashMap<>();
        EXP_TYPES = new HashMap<>();
        NUM_TYPES = new HashMap<>();
        TW = new Table(keysTW(), valuesTW());
        TL = new Table(keysTL(), valuesTL());
    }

    private static Map<String, Integer> keysTW() {
        Map<String, Integer> keys = new HashMap<>();
        keys.put("int", 1);
        keys.put("float", 2);
        keys.put("bool", 3);
        keys.put("end", 4);
        keys.put("let", 5);
        keys.put("if", 6);
        keys.put("then", 7);
        keys.put("else", 8);
        keys.put("end_else", 9);
        keys.put("for", 10);
        keys.put("do", 11);
        keys.put("while", 12);
        keys.put("loop", 13);
        keys.put("input", 14);
        keys.put("output", 15);
        keys.put("true", 16);
        keys.put("false", 17);
        return keys;
    }

    private static Map<Integer, String> valuesTW() {
        Map<Integer, String> values = new HashMap<>();
        values.put(1, "int");
        values.put(2, "float");
        values.put(3, "bool");
        values.put(4, "end");
        values.put(5, "let");
        values.put(6, "if");
        values.put(7, "then");
        values.put(8, "else");
        values.put(9, "end_else");
        values.put(10, "for");
        values.put(11, "do");
        values.put(12, "while");
        values.put(13, "loop");
        values.put(14, "input");
        values.put(15, "output");
        values.put(16, "true");
        values.put(17, "false");
        return values;
    }

    private static Map<String, Integer> keysTL() {
        Map<String, Integer> keys = new HashMap<>();
        keys.put("NE", 1);
        keys.put("EQ", 2);
        keys.put("LT", 3);
        keys.put("LE", 4);
        keys.put("GT", 5);
        keys.put("GE", 6);
        keys.put("plus", 7);
        keys.put("min", 8);
        keys.put("or", 9);
        keys.put("mult", 10);
        keys.put("div", 11);
        keys.put("and", 12);
        keys.put("~", 13);
        keys.put(":", 14);
        keys.put("\r\n", 15);
        keys.put(",", 16);
        keys.put("{", 17);
        keys.put("}", 18);
        keys.put("=", 19);
        keys.put("(", 20);
        keys.put(")", 21);
        keys.put(".", 22);
        keys.put(" ", 23);
        keys.put("*", 24);
        keys.put(";", 25);
        return keys;
    }

    private static Map<Integer, String> valuesTL() {
        Map<Integer, String> values = new HashMap<>();
        values.put(1, "NE");
        values.put(2, "EQ");
        values.put(3, "LT");
        values.put(4, "LE");
        values.put(5, "GT");
        values.put(6, "GE");
        values.put(7, "plus");
        values.put(8, "min");
        values.put(9, "or");
        values.put(10, "mult");
        values.put(11, "div");
        values.put(12, "and");
        values.put(13, "~");
        values.put(14, ":");
        values.put(15, "\r\n");
        values.put(16, ",");
        values.put(17, "{");
        values.put(18, "}");
        values.put(19, "=");
        values.put(20, "(");
        values.put(21, ")");
        values.put(22, ".");
        values.put(23, " ");
        values.put(24, "*");
        values.put(25, ";");
        return values;
    }
    //endregion

    //region support methods
    public static void clear() {
        LEXICAL_RESULT.clear();
        EXPRESSION.clear();
        ASSIGMENT.clear();
        NUM_TYPES.clear();
        ID_TYPES.clear();
        TI.clear();
        TN.clear();
    }
    //endregion
}

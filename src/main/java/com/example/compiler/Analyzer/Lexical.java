package com.example.compiler.Analyzer;

import com.example.compiler.Tables.Tables;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Lexical {
    private final String MODULE = "LexicalAnalyzer/";
    private final File program;
    //region support data structures
    private final Set<Character> decimal = new HashSet<>(
            Arrays.asList(
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
            ));
    private final Set<Character> numberSymbols = new HashSet<>(
            Arrays.asList(
                    'A', 'B', 'C', 'D', 'E', 'F', 'O', 'H',
                    'a', 'b', 'c', 'd', 'e', 'f', 'o', 'h', '0',
                    '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    '.', '+', '-'));
    private final Set<Character> types = new HashSet<>(Arrays.asList('B', 'b', 'O', 'o', 'D', 'd', 'H', 'h'));
    private final Set<Character> symbol = new HashSet<>(
            Arrays.asList(
                    '~', ';', ':', '\r', '\n', ',', '{', '}', '=', '(', ')', '.', ' ', '*'));
    private boolean end = false;
    //endregion
    //hold type of number for semantic
    private int typeOfNumber = 0;

    public Lexical(File program) {
        this.program = program;
    }

    /**
     * lexical analyzer's main method
     *
     * @return 0 if the analysis is successfully completed, otherwise -1
     */
    public int analyzing() throws IOException, RuntimeException {
        try (Reader reader = new BufferedReader(new FileReader(program))) {
            StringBuilder word = new StringBuilder();
            while (reader.ready()) {
                char c = (char) reader.read();
                if (Character.isLetter(c)) {
                    String w = readWord(reader, word, c);
                    if (addWord(w)) return 0;
                } else if (Character.isDigit(c) || c == '.') {
                    String n = readNumber(reader, word, c);
                    addNumber(checkNumber(n));
                } else if (symbol.contains(c)) {
                    String s = readSymbol(reader, word, c);
                    if (!checkSymbol(s))
                        throw new RuntimeException(MODULE
                                + "AnalyzingException: invalid character (" + c + ")");
                } else throw new RuntimeException(MODULE
                        + "AnalyzingException: invalid character (" + c + ")");
                word = new StringBuilder();
            }
            if (end) return 0;
            else throw new RuntimeException(MODULE
                    + "AnalyzingException: no 'end'");
        }
    }

    //region word analysis methods
    private String readWord(Reader reader, StringBuilder word, char c)
            throws IOException, RuntimeException {
        word.append(c);
        while (reader.ready()) {
            reader.mark(1);
            char next = (char) reader.read();
            if (Character.isLetterOrDigit(next) || next == '_') {
                word.append(next);
            } else if (symbol.contains(next)) {
                reader.reset();
                return word.toString();
            } else
                throw new RuntimeException(MODULE
                        + "ReadWordException: the word must begin with a letter or number," +
                        " received (" + next + ")");
        }
        return word.toString();
    }

    private boolean addWord(String word) {
        int index;
        int table;
        if (Tables.TW.contains(word)) {
            index = Tables.TW.get(word);
            table = 1;
            if (index == 4) {
                addToLexicalResult(table, index);
                return true;
            }
        } else {
            if (Tables.TL.contains(word)) {
                index = Tables.TL.get(word);
                table = 2;
            } else {
                if (Tables.TI.contains(word)) index = Tables.TI.get(word);
                else index = Tables.TI.put(word);
                table = 3;
            }
        }
        addToLexicalResult(table, index);
        return false;
    }
    //endregion

    //region number analysis methods
    private String readNumber(Reader reader, StringBuilder word, char c)
            throws IOException, RuntimeException {
        word.append("0").append(c);
        while (reader.ready()) {
            reader.mark(1);
            c = (char) reader.read();
            if (numberSymbols.contains(c)) {
                if (c == '+' || c == '-') {
                    if (word.toString().contains("E") || word.toString().contains("e"))
                        word.append(c);
                    else return word.toString();
                } else word.append(c);
            } else if (symbol.contains(c)) {
                reader.reset();
                return word.toString();
            } else throw new RuntimeException(MODULE
                    + "ReadNumberException: invalid character, received (" + c + ")");
        }
        return word.toString();
    }

    private String checkNumber(String num) throws RuntimeException {
        int typeIndex = num.length() - 1;
        String result;
        if (types.contains(num.charAt(typeIndex))) {
            String withoutType;
            char type;
            withoutType = num.substring(0, typeIndex);
            type = num.charAt(typeIndex);
            int radix = isBinary(withoutType, type) ? 2
                    : isOctal(withoutType, type) ? 8
                    : isDecimal(withoutType, type) ? 10
                    : isHex(withoutType, type) ? 16
                    : 0;
            if (radix != 0) {
                result = Long.toBinaryString(Long.parseLong(withoutType, radix));
                typeOfNumber = 1;
            } else
                throw new RuntimeException(MODULE
                        + "CheckNumberException: invalid number type, received (" + type + ")");
        } else {
            if (isDecimal(num)) {
                result = Long.toBinaryString(Long.parseLong(num, 10));
                typeOfNumber = 1;
            } else if (isFloat(num)) {
                result = Long.toBinaryString(Double.doubleToLongBits(Double.parseDouble(num)));
                typeOfNumber = 2;
            } else if (isExp(num)) {
                result = convertExp(num);
                typeOfNumber = 2;
            } else throw new RuntimeException(MODULE
                    + "CheckNumberException: invalid number, received (" + num + ")");
        }
        return result;
    }

    private String convertExp(String num) {
        int index = num.contains("E") ? num.indexOf("E") : num.indexOf("e");
        double base = Double.parseDouble(num.substring(0, index));
        String notParsedDegree = num.substring(index + 1);
        long degree = Long.parseLong(notParsedDegree.isEmpty() ? "1" : notParsedDegree);
        return Long.toBinaryString(Double.doubleToLongBits(base * Math.pow(10, degree)));
    }

    private void addNumber(String num) {
        int index;
        if (Tables.TN.contains(num)) index = Tables.TN.get(num);
        else index = Tables.TN.put(num);
        addToLexicalResult(4, index);
        Tables.NUM_TYPES.put(index, typeOfNumber);
    }
    //endregion

    //region check numeral system
    private boolean isBinary(String withoutType, char type) {
        final Set<Character> binary = new HashSet<>(
                Arrays.asList(
                        '0', '1'
                ));
        if (type == 'B' || type == 'b')
            return withoutType.chars().mapToObj(c -> (char) c)
                    .allMatch(binary::contains);
        return false;
    }

    private boolean isOctal(String withoutType, char type) {
        final Set<Character> octal = new HashSet<>(
                Arrays.asList(
                        '0', '1', '2', '3', '4', '5', '6', '7'
                ));
        if (type == 'O' || type == 'o')
            return withoutType.chars().mapToObj(c -> (char) c)
                    .allMatch(octal::contains);
        return false;
    }

    private boolean isDecimal(String num) {
        return num.chars().mapToObj(c -> (char) c)
                .allMatch(decimal::contains);
    }

    private boolean isDecimal(String withoutType, char type) {
        if (type == 'D' || type == 'd')
            return withoutType.chars().mapToObj(c -> (char) c)
                    .allMatch(decimal::contains);
        return false;
    }

    private boolean isHex(String withoutType, char type) {
        final Set<Character> hex = new HashSet<>(
                Arrays.asList(
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                        'A', 'B', 'C', 'D', 'E', 'F',
                        'a', 'b', 'c', 'd', 'e', 'f'
                ));
        if (type == 'H' || type == 'h')
            return withoutType.chars().mapToObj(c -> (char) c)
                    .allMatch(hex::contains);
        return false;
    }
    //endregion

    //region check floating-point number
    private boolean isFloat(String num) {
        int commaIndex = num.indexOf('.');
        if (commaIndex != num.lastIndexOf('.') || commaIndex == num.length() - 1) return false;
        return num.chars().mapToObj(c -> (char) c)
                .allMatch(o -> decimal.contains(o) || o == '.');
    }

    private boolean isExp(String num) {
        final Set<Character> expSymbols = new HashSet<>(
                Arrays.asList('E', 'e', '+', '-', '.',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        int exp = 0, operator = 0, dot = 0;
        char[] charArray = num.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Character c = charArray[i];
            if (!expSymbols.contains(c)) return false;
            if (c.equals('E') || c.equals('e')) {
                exp++;
                if (i == charArray.length - 1) return false;
                if (charArray[i - 1] == '.' || charArray[i + 1] == '.') return false;
            }
            if (c.equals('+') || c.equals('-')) operator++;
            if (c.equals('.')) {
                dot++;
                if (i == charArray.length - 1) return false;
            }
        }
        return (exp == 1 && operator <= 1 && dot <= 1);
    }
    //endregion

    //region symbol analysis methods
    private String readSymbol(Reader reader, StringBuilder word, char c)
            throws IOException, RuntimeException {
        if (c == '\r') {
            char next = (char) reader.read();
            if (next == '\n') {
                word.append(c).append(next);
            } else throw new RuntimeException(MODULE + "ReadSymbolException: " +
                    "there should be a line break after the carriage return," +
                    " received (" + next + ")");
        } else if (c == '(') {
            int state = 1;
            while (reader.ready()) {
                reader.mark(1);
                char next = (char) reader.read(); //(*test*)
                if (next == '*') {
                    if (state == 1) {
                        state = 2;
                        c = next;
                    } else if (state == 2) {
                        state = 3;
                    } else {
                        state = -1;
                    }
                } else if (next == ')') {
                    state = (state == 3) ? 0 : -1;
                } else {
                    if (state == 1) {
                        reader.reset();
                        word.append(c);
                        return word.toString();
                    }
                }
                if (state == 0) return "(**)";
                if (state == -1) throw new RuntimeException(MODULE
                        + "ReadSymbolException: invalid comment entry");
            }
        } else word.append(c);
        return word.toString();
    }

    private boolean checkSymbol(String s) {
        if (s.equals("(**)")) return true;
        if (Tables.TL.contains(s)) {
            int index = Tables.TL.get(s);
            addToLexicalResult(2, index);
            return true;
        }
        return false;
    }
    //endregion

    //region support methods
    private void addToLexicalResult(Integer tableNumber, Integer indexInTable) {
        Tables.LEXICAL_RESULT.add("(" + tableNumber + "," + indexInTable + ")");
    }
    //endregion
}

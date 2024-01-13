package com.example.compiler.Analyzer.LexicalTests;

import com.example.compiler.Analyzer.Lexical;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReadNumberTest {
    String PATH = "src/test/resources/program.txt";
    File file = new File(PATH);
    Lexical lexical = new Lexical(file);
    Reader reader = new BufferedReader(new FileReader(PATH));

    public ReadNumberTest() throws FileNotFoundException {
    }

    @Test
    public void readFloatNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = ".123";
        char startSymbol = '0';
        writeTest(test);
        Assertions.assertEquals("0" + startSymbol + test, readNumber(startSymbol));
    }
    @Test
    public void readStartWithDotFloatNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "123";
        char startSymbol = '.';
        writeTest(test);
        Assertions.assertEquals("0" + startSymbol + test, readNumber(startSymbol));
    }
    @Test
    public void readIntNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "23";
        char startSymbol = '1';
        writeTest(test);
        Assertions.assertEquals("0" + startSymbol + test, readNumber(startSymbol));
    }
    @Test
    public void readNumeralSystemNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "23H";
        char startSymbol = '1';
        writeTest(test);
        Assertions.assertEquals("0" + startSymbol + test, readNumber(startSymbol));
    }
    @Test
    public void readExpNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "23E2";
        char startSymbol = '1';
        writeTest(test);
        Assertions.assertEquals("0" + startSymbol + test, readNumber(startSymbol));
    }
    @Test
    public void readPlusNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "23+123";
        char startSymbol = '1';
        writeTest(test);
        Assertions.assertEquals("0" + startSymbol + test, readNumber(startSymbol));
    }
    private String readNumber(char startSymbol) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method readNumber = lexical.getClass().getDeclaredMethod("readNumber", Reader.class, StringBuilder.class, char.class);
        readNumber.setAccessible(true);
        StringBuilder builder = new StringBuilder();
        return (String) readNumber.invoke(lexical, reader, builder, startSymbol);
    }

    private void writeTest(String text) {
        try (Writer writer = new FileWriter(PATH, false)) {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

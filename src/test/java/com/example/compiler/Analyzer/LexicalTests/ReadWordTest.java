package com.example.compiler.Analyzer.LexicalTests;

import com.example.compiler.Analyzer.Lexical;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReadWordTest {
    String PATH = "src/test/resources/program.txt";
    File file = new File(PATH);
    Lexical lexical = new Lexical(file);
    Reader reader = new BufferedReader(new FileReader(PATH));

    public ReadWordTest() throws FileNotFoundException {
    }

    @Test
    public void readWordWithSmallLetter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "mallletter";
        char startSymbol = 's';
        writeTest(test);
        Assertions.assertEquals(startSymbol + test, readWord(startSymbol));
    }

    @Test
    public void readWordStartWithBigLetter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "IGLETTER";
        char startSymbol = 'B';
        writeTest(test);
        Assertions.assertEquals(startSymbol + test, readWord(startSymbol));
    }

    @Test
    public void readWordWith_() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "ordWith_";
        char startSymbol = 'w';
        writeTest(test);
        Assertions.assertEquals(startSymbol + test, readWord(startSymbol));
    }

    @Test
    public void readWordWithUnsupportedSymbol() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "ordWith+";
        char startSymbol = 'w';
        writeTest(test);
        Assertions.assertNull(readWord(startSymbol));
    }

    @Test
    public void readWordWithSupportedSymbol() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "ordWith=otherWord";
        char startSymbol = 'w';
        writeTest(test);
        Assertions.assertEquals("wordWith", readWord(startSymbol));
    }

    @Test
    public void readWordWithNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "ordWith123";
        char startSymbol = 'w';
        writeTest(test);
        Assertions.assertEquals(startSymbol + test, readWord(startSymbol));
    }

    private String readWord(char startSymbol) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method readWord = lexical.getClass().getDeclaredMethod("readWord",
                Reader.class, StringBuilder.class, char.class);
        readWord.setAccessible(true);
        StringBuilder builder = new StringBuilder();
        return (String) readWord.invoke(lexical, reader, builder, startSymbol);
    }

    private void writeTest(String text) {
        try (Writer writer = new FileWriter(PATH, false)) {
            writer.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

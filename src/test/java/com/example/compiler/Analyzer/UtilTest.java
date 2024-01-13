package com.example.compiler.Analyzer;

import com.example.compiler.Analyzer.Syntactic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UtilTest {
    private final Syntactic syntactic = new Syntactic();
    @Test
    public void checkNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String test = "(1,12)", toAssert = "loop";
        Method readNumber = syntactic.getClass().getDeclaredMethod("getValueFromBracket", String.class);
        readNumber.setAccessible(true);
        String result = (String) readNumber.invoke(syntactic, test);
        Assertions.assertEquals(toAssert, result);
    }
}

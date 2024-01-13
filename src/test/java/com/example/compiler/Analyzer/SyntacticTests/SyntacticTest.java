package com.example.compiler.Analyzer.SyntacticTests;

import com.example.compiler.Analyzer.Lexical;
import com.example.compiler.Analyzer.Syntactic;
import com.example.compiler.Tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class SyntacticTest {
    String FULL_PROGRAM_PATH = "src/test/resources/fine.txt";
    String SHORT_PROGRAM_PATH = "src/test/resources/program.txt";
    File file = new File(FULL_PROGRAM_PATH);
    @Test
    void analyzing() throws IOException {
        Lexical lexical = new Lexical(file);
        lexical.analyzing();
        Syntactic syntactic = new Syntactic();
        int result = syntactic.analyzing();
        System.out.println("Exp : " + Tables.EXPRESSION);
        System.out.println("Ass : " + Tables.ASSIGMENT);
        Assertions.assertEquals(0, result);
    }
}
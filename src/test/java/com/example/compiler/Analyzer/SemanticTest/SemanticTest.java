package com.example.compiler.Analyzer.SemanticTest;

import com.example.compiler.Analyzer.Lexical;
import com.example.compiler.Analyzer.Semantic;
import com.example.compiler.Analyzer.Syntactic;
import com.example.compiler.Tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SemanticTest {

    String FULL_PROGRAM_PATH = "src/test/resources/fine.txt";
    String SHORT_PROGRAM_PATH = "src/test/resources/program.txt";
    File file = new File(FULL_PROGRAM_PATH);

    @Test
    void analyzing() throws IOException {
        Lexical lexical = new Lexical(file);
        lexical.analyzing();
        Syntactic syntactic = new Syntactic();
        syntactic.analyzing();
        System.out.println("Exp : " + Tables.EXPRESSION);
        System.out.println("Ass : " + Tables.ASSIGMENT);
        Semantic semantic = new Semantic();
        int result = semantic.analyzing();
        Assertions.assertEquals(0, result);
    }
}
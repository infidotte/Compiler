package com.example.compiler.Analyzer.LexicalTests;

import com.example.compiler.Analyzer.Lexical;
import com.example.compiler.Tables.Table;
import com.example.compiler.Tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class LexicalTest {
    String FULL_PROGRAM_PATH = "src/test/resources/fine.txt";
    String SHORT_PROGRAM_PATH = "src/test/resources/program.txt";
    File file = new File(FULL_PROGRAM_PATH);

    @Test
    public void analyzing() throws IOException {
        Lexical lexical = new Lexical(file);
        int result = lexical.analyzing();
        System.out.println(Tables.LEXICAL_RESULT);
        for (String bracket : Tables.LEXICAL_RESULT) {
            System.out.print(new Bracket(bracket).word() + " ");
        }
        Assertions.assertEquals(0, result);
    }

    static class Bracket {
        private final int table;
        private final int index;

        public Bracket(String bracket) {
            int indexOfComma = bracket.indexOf(',');
            this.table = Integer.parseInt(bracket.substring(1, indexOfComma));
            this.index = Integer.parseInt(bracket.substring(indexOfComma + 1, bracket.length() - 1));
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
}

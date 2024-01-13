package com.example.compiler.Analyzer;

import com.example.compiler.Tables.Tables;
import com.example.compiler.Util.Bracket;

import java.util.ListIterator;

public class Syntactic {
    private final String MODULE = "SyntacticAnalyzer/";
    private final ListIterator<String> iterator = Tables.LEXICAL_RESULT.listIterator();
    private int end = 0;
    private boolean hasContent = false;


    //region main methods

    /**
     * syntactic analyzer's main method
     *
     * @return 0 if the analysis is successfully completed, otherwise -1
     */
    public int analyzing() {
        program();
        return end == 1 ? 0 : -1;
    }

    private void program() {
        while (iterator.hasNext()) {
            Bracket next = nextFree();
            if (isEnd(next)) {
                if (!hasContent) {
                    throw new RuntimeException(MODULE + "ProgramException: at least one description or operator expected before 'end'");
                }
                end++;
                return;
            }
            hasContent = true;
            description(next);
        }
    }

    private void description(Bracket next) throws RuntimeException {
        String exception = MODULE
                + "DescriptionException: ";
        int local_type;
        if (isType(next)) {
            local_type = next.index;
            next = nextNoSpace();
            if (next.table != 3) throw new RuntimeException(exception + "variable expected - " + next.word);
            Tables.ID_TYPES.put(next.index, local_type);
            while (iterator.hasNext()) {
                next = nextNoSpace();
                if (isEndSymbol(next)) program();
                else if (isComma(next)) {
                    next = nextNoSpace();
                    if (next.table != 3) throw new RuntimeException(exception + "variable expected - " + next.word);
                    Tables.ID_TYPES.put(next.index, local_type);
                } else throw new RuntimeException(exception + "illegal argument - " + next.word);
            }
        } else {
            operators(next);
            next = nextNoSpace();
            if (isEndSymbol(next)) {
                program();
            } else {
                throw new RuntimeException("illegal argument - " + next.word);
            }
        }
    }

    private void cycleDescription(int local_type, Bracket previous) {
        Bracket next;
        while (iterator.hasNext()) {
            next = nextNoSpace();

            if (!isEndSymbol(next)) {

                if (isComma(next)) previous = next;

                if (isComma(previous) & next.table == 3) {
                    Tables.ID_TYPES.put(next.index, local_type);
                }
            } else {
                if (isComma(previous)) throw new RuntimeException(
                        MODULE
                                + "DescriptionException: variable expected - " + next.word);
                program();
            }
        }
    }

    private void operators(Bracket next) throws RuntimeException {
        if (next.table == 2 & next.index == 17) {
            compoundOperator();
        } else if (next.table == 3) {
            assignmentOperator(next);
        } else if (next.table == 1) {
            switch (next.index) {
                case 5 -> assignmentOperator();
                case 6 -> conditionalOperator();
                case 10 -> fixedLoopOperator();
                case 11 -> conditionalLoopOperator();
                case 14 -> inputOperator();
                case 15 -> outputOperator();
                default -> throw new RuntimeException(MODULE
                        + "OperatorException: illegal argument - " + next.word);
            }
        } else {
            throw new RuntimeException(MODULE
                    + "OperatorException: illegal argument - " + next.word);
        }
    }
    //endregion

    //region operators
    private void compoundOperator() {
        operators(nextFree());
        Bracket next = nextFree();
        while (iterator.hasNext()) {
            if (isSemicolon(next)) {
                operators(nextFree());
            } else if (next.table == 2 && next.index == 18) {
                return;
            }
            next = nextFree();
        }
    }

    private void assignmentOperator(Bracket next) throws RuntimeException {
        StringBuilder builder = new StringBuilder();
        if (!(next.table == 3 && Tables.ID_TYPES.containsKey(next.index)))
            throw new RuntimeException(MODULE + "initialized variable is expected");
        builder.append(next);
        next = nextFree();
        if (!(next.table == 2 & next.index == 19))
            throw new RuntimeException(MODULE + "AssigmentOperatorException: \"=\" expected, received - " + next.word);
        Tables.ASSIGMENT.add(builder.append(next).append(expression()).toString());
    }

    private void assignmentOperator() {
        StringBuilder builder = new StringBuilder("(1,5)");
        Bracket next = nextFree();
        if (next.table != 3) throw new RuntimeException(MODULE + "AssigmentOperatorException: variable expected");
        builder.append(next);
        Tables.ID_TYPES.put(next.index, 4);
        next = nextFree();
        if (!(next.table == 2 & next.index == 19))
            throw new RuntimeException(MODULE + "AssigmentOperatorException: \"=\" expected, received - " + next.word);
        Tables.ASSIGMENT.add(builder.append(next).append(expression()).toString());
    }

    private void conditionalOperator() {
        Tables.EXPRESSION.add(expression());
        Bracket next = nextFree();
        if (!(next.table == 1 && next.index == 7))
            throw new RuntimeException(MODULE + "ConditionalOperatorException: \"then\" expected, received - " + next.word);
        operators(nextFree());
        next = nextFree();
        if (next.table == 1 && next.index == 8) {
            operators(nextFree());
            next = nextFree();
        }
        if (!(next.table == 1 && next.index == 9))
            throw new RuntimeException(MODULE + "ConditionalOperatorException: \"end_else\" expected, received - " + next.word);
    }

    private void fixedLoopOperator() throws RuntimeException {
        Bracket next = nextFree();
        if (!(next.table == 2 && next.index == 20))
            throw new RuntimeException(MODULE + "FixedLoopOperatorException: \"(\" expected, received - " + next.word);
        if (!isSemicolon(nextFree())) {
            previous();
            Tables.EXPRESSION.add(expression());
            if (!isSemicolon(nextFree()))
                throw new RuntimeException(MODULE + "FixedLoopOperatorException: \";\" expected, received - " + next.word);
        }
        if (!isSemicolon(nextFree())) {
            previous();
            Tables.EXPRESSION.add(expression());
            if (!isSemicolon(nextFree()))
                throw new RuntimeException(MODULE + "FixedLoopOperatorException: \";\" expected, received - " + next.word);
        }
        next = nextFree();
        if (!(next.table == 2 && next.index == 21)) {
            previous();
            Tables.EXPRESSION.add(expression());
            next = nextFree();
            if (!(next.table == 2 && next.index == 21))
                throw new RuntimeException(MODULE + "FixedLoopOperatorException: \")\" expected, received - " + next.word);
        }
        next = nextFree();
        operators(next);
    }

    private void conditionalLoopOperator() {
        Bracket next = nextFree();
        if (!(next.table == 1 && next.index == 12))
            throw new RuntimeException(MODULE + "ConditionalLoopOperatorException: \"while\" expected, received - " + next.word);
        Tables.EXPRESSION.add(expression());
        operators(nextFree());
        next = nextFree();
        if (!(next.table == 1 && next.index == 13))
            throw new RuntimeException(MODULE + "ConditionalLoopOperatorException: \"loop\" expected, received - " + next.word);
    }

    private void inputOperator() {
        Bracket next = nextFree();
        if (!(next.table == 2 && next.index == 20))
            throw new RuntimeException(MODULE + "InputOperatorException: \"(\" expected, received - " + next.word);
        next = nextFree();
        if (next.table != 3)
            throw new RuntimeException(MODULE + "InputOperatorException: Variable expected, received - " + next.word);
        next = next();
        while (iterator.hasNext()) {
            if (next.table == 2 && next.index == 23) {
                if (next().table != 3)
                    throw new RuntimeException(MODULE + "InputOperatorException: Variable expected, received - " + next.word);
                next = next();
            } else if (next.table == 2 && next.index == 21) {
                return;
            }
        }
    }

    private void outputOperator() {
        Bracket next = nextFree();
        if (!(next.table == 2 && next.index == 20))
            throw new RuntimeException(MODULE + "OutputOperatorException: \"(\" expected, received - " + next.word);
        Tables.EXPRESSION.add(expression());
        next = next();
        while (iterator.hasNext()) {
            if (next.table == 2 && next.index == 23) {
                Tables.EXPRESSION.add(expression());
                next = next();
            } else if (next.table == 2 && next.index == 21) {
                return;
            }
        }
    }
    //endregion

    //region expression, operand, summand and multiplier
    private String expression() {
        StringBuilder operation = new StringBuilder();
        operation.append(operand());
        while (iterator.hasNext()) {
            Bracket next = nextNoSpace();
            if (isRelationGO(next)) {
                operation.append(next).append(operand());
            } else {
                iterator.previous();
                return operation.toString();
            }
        }
        return operation.toString();
    }

    private String operand() {
        StringBuilder operation = new StringBuilder();
        operation.append(summand());
        while (iterator.hasNext()) {
            Bracket next = nextNoSpace();
            if (isAdditionGO(next)) {
                operation.append(next).append(summand());
            } else {
                iterator.previous();
                return operation.toString();
            }
        }
        return operation.toString();
    }

    private String summand() {
        StringBuilder operation = new StringBuilder();
        operation.append(multiplier());
        while (iterator.hasNext()) {
            Bracket next = nextNoSpace();
            if (isMultiplicationGO(next)) {
                operation.append(next).append(multiplier());
            } else {
                iterator.previous();
                return operation.toString();
            }
        }
        return operation.toString();
    }

    private String multiplier() {
        int state = 0;
        StringBuilder multiplier = new StringBuilder();
        Bracket current = nextFree();
        if (current.table == 3 || current.table == 4 ||
                (current.table == 1 && (current.index == 16 || current.index == 17))) {
            multiplier.append(current);
        } else if (current.table == 2) {
            while (iterator.hasNext()) {
                if (current.index == 13) {
                    return multiplier.append(current).append(multiplier()).toString();
                } else if (current.index == 20) {
                    if (state != 0) throw new RuntimeException(
                            MODULE + "MultiplierException: \"(\" already processed");
                    state = 1;
                    multiplier.append(current).append(expression());
                    current = nextFree();
                } else if (current.index == 21) {
                    if (state != 1)
                        throw new RuntimeException(
                                MODULE + "MultiplierException: \"(\" expected," +
                                        " received - " + current.word);
                    return multiplier.append(current).toString();
                } else
                    throw new RuntimeException(
                            MODULE + "MultiplierException: unexpected separator, " +
                                    "received - " + current.word);
            }
        } else throw new RuntimeException(
                MODULE + "MultiplierException: illegal argument - " + current.word);
        return multiplier.toString();
    }
    //endregion

    //region check operation
    private boolean isRelationGO(Bracket bracket) {
        return (bracket.table == 2) &
                (bracket.index == 1
                        || bracket.index == 2
                        || bracket.index == 3
                        || bracket.index == 4
                        || bracket.index == 5
                        || bracket.index == 6
                );
    }

    private boolean isAdditionGO(Bracket bracket) {
        return (bracket.table == 2) &
                (bracket.index == 7 || bracket.index == 8 || bracket.index == 9);
    }

    private boolean isMultiplicationGO(Bracket bracket) {
        return (bracket.table == 2) &
                (bracket.index == 10 || bracket.index == 11 || bracket.index == 12);
    }
    //endregion

    //region iterator
    private Bracket nextFree() {
        Bracket next = new Bracket(iterator.next());
        while (isSpace(next) || isLineBreak(next)) {
            next = new Bracket(iterator.next());
        }
        return next;
    }

    private Bracket nextNoSpace() {
        Bracket next = new Bracket(iterator.next());
        while (isSpace(next)) {
            next = new Bracket(iterator.next());
        }
        return next;
    }

    private Bracket next() {
        return new Bracket(iterator.next());
    }

    private void previous() {
        Bracket prev = new Bracket(iterator.previous());
        while (isSpace(prev)) {
            prev = new Bracket(iterator.previous());
        }
    }
    //endregion

    //region support methods
    private boolean isEnd(Bracket next) {
        return next.table == 1 & next.index == 4;
    }

    private boolean isEndSymbol(Bracket next) {
        return next.table == 2 & (next.index == 14 || next.index == 15);
    }

    private boolean isComma(Bracket next) {
        return next.table == 2 & next.index == 16;
    }

    private boolean isType(Bracket next) {
        return next.table == 1 &
                (next.index == 1 || next.index == 2 || next.index == 3);
    }

    private boolean isSpace(Bracket next) {
        return next.table == 2 && next.index == 23;
    }

    private boolean isLineBreak(Bracket next) {
        return next.table == 2 && next.index == 15;
    }

    private boolean isSemicolon(Bracket next) {
        return next.table == 2 && next.index == 25;
    }
    //endregion
}

package com.example.compiler.Analyzer;

import com.example.compiler.Tables.Tables;
import com.example.compiler.Util.Bracket;

import java.util.List;
import java.util.ListIterator;

public class Semantic {
    private final String MODULE = "SemanticAnalyzer/";

    //region main methods

    /**
     * semantic analyzer's main method
     *
     * @return 0 if the analysis is successfully completed, otherwise -1
     */
    public int analyzing() {
        return processExpressions() && processAssigment() ? 0 : -1;
    }

    private boolean processExpressions() {
        for (String expression :
                Tables.EXPRESSION) {
            if (!defineExpType(expression)) return false;
        }
        return true;
    }

    private boolean processAssigment() {
        for (String assigment :
                Tables.ASSIGMENT) {
            if (!defineAssigmentType(assigment)) return false;
        }
        return true;
    }
    //endregion

    //region check assigment
    private boolean defineAssigmentType(String assigment) {
        List<String> brackets = List.of(
                assigment.replace(")(", ");(").split(";"));
        ListIterator<String> iterator = brackets.listIterator();
        Bracket next = new Bracket(iterator.next());
        int leftType;
        if (next.table == 1 && next.index == 5) {
            next = new Bracket(iterator.next());
            if (next.table != 3)
                throw new RuntimeException(MODULE +
                        "DefineAssigmentTypeException : variable expected");
            int index = next.index;
            leftType = Tables.ID_TYPES.get(next.index);
            if (isEqualsSymbol(new Bracket(iterator.next())))
                throw new RuntimeException(MODULE +
                        "DefineAssigmentTypeException : " +
                        "semicolon expected instead of (" + next.word + ")");
            int rightType = expression(iterator);
            if (!checkTypes(leftType, rightType))
                throw new RuntimeException(MODULE +
                        "DefineAssigmentTypeException : incorrect type conversion");
            Tables.ID_TYPES.put(index, rightType);
            return true;
        } else {
            if (next.table != 3)
                throw new RuntimeException(MODULE +
                        "DefineAssigmentTypeException : variable expected");
            leftType = Tables.ID_TYPES.get(next.index);
            if (isEqualsSymbol(new Bracket(iterator.next())))
                throw new RuntimeException(MODULE +
                        "DefineAssigmentTypeException : " +
                        "semicolon expected instead of (" + next.word + ")");
            int rightType = expression(iterator);
            if (!checkTypes(leftType, rightType))
                throw new RuntimeException(MODULE +
                        "DefineAssigmentTypeException : incorrect type conversion");
            return true;
        }
    }
    //endregion

    //region check expression
    private boolean defineExpType(String expression) {
        List<String> brackets = List.of(
                expression.replace(")(", ");(").split(";"));
        ListIterator<String> iterator = brackets.listIterator();
        int type = expression(iterator);
        if (!isValidType(type))
            throw new RuntimeException(
                    MODULE + "DefineExpressionTypeException : " +
                            "invalid type (" + type + ")");
        if (!putExpression(expression, type))
            throw new RuntimeException(
                    MODULE + "DefineExpressionTypeException :  " +
                            "expression data types are different (" + type + ")");
        return true;
    }

    private boolean putExpression(String exp, int type) {
        int old = Tables.EXP_TYPES.getOrDefault(exp, 0);
        if (old != 0) {
            return old == type;
        }
        Tables.EXP_TYPES.putIfAbsent(exp, type);
        return true;
    }

    private int expression(ListIterator<String> iterator) {
        int type;
        type = multiplier(iterator);
        while (iterator.hasNext()) {
            Bracket next = new Bracket(iterator.next());
            if (isBothGO(next)) {
                if (!isValidType(type))
                    throw new RuntimeException(
                            MODULE + "ExpressionTypeException : " +
                                    "invalid type (" + type + ")");
            } else if (isNumGO(next)) {
                if (!(isValidType(type) && type != 3))
                    throw new RuntimeException(
                            MODULE + "ExpressionTypeException : " +
                                    "expected numeric(1, 2 or 4) before numeric operation" +
                                    " instead of (" + type + ")");
            } else if (isBoolGO(next)) {
                if (!(isValidType(type) && type == 3))
                    throw new RuntimeException(
                            MODULE + "ExpressionTypeException : " +
                                    "expected boolean(3) before boolean operation " +
                                    "instead of (" + type + ")");
            } else {
                int nextType = multiplier(iterator);
                if (checkTypes(type, nextType)) type = nextType;
            }
        }
        return type;
    }

    private int multiplier(ListIterator<String> iterator) {
        int type = 0;
        if (iterator.hasNext()) {
            Bracket next = new Bracket(iterator.next());
            if (next.table == 3) return idType(next);
            else if (next.table == 4) return numType(next);
            else if (next.table == 1) return 3;
            else {
                if (next.index == 13) {
                    type = 3;
                    int rightType = multiplier(iterator);
                    if (rightType != 3)
                        throw new RuntimeException(
                                MODULE +
                                        "MultiplierTypeException : " +
                                        "expected boolean(3) after \"~\" " +
                                        "instead of (" + rightType + ")");
                    return type;
                }
                if (next.index == 20) {
                    type = expression(iterator);
                    if (new Bracket(iterator.previous()).index != 21)
                        throw new RuntimeException(
                                MODULE +
                                        "MultiplierTypeException : " +
                                        "\")\" symbol expected" +
                                        " instead of (" + next.word + ")");
                    return type;
                }
            }
        }
        return type;
    }
    //endregion

    //region check operation
    private boolean isBothGO(Bracket bracket) {
        return (bracket.table == 2)
                & (bracket.index == 1
                || bracket.index == 2
        );
    }

    private boolean isNumGO(Bracket bracket) {
        return (bracket.table == 2)
                & (bracket.index == 3
                || bracket.index == 4
                || bracket.index == 5
                || bracket.index == 6
                || bracket.index == 7
                || bracket.index == 8
                || bracket.index == 10
                || bracket.index == 11
        );
    }

    private boolean isBoolGO(Bracket bracket) {
        return bracket.table == 2
                & (bracket.index == 9
                || bracket.index == 12
        );
    }
    //endregion

    //region check type
    private boolean isValidType(int type) {
        return type > 0 && type < 5;
    }

    private boolean checkTypes(int left, int right) {
        if (left == 3) return right == 3;
        else if (left == 2) return right == 2 || right == 1;
        else if (left == 1) return right == 1;
        else return right == 3 || right == 2 || right == 1;
    }
    //endregion

    //region support methods
    private int idType(Bracket next) {
        return Tables.ID_TYPES.get(next.index);
    }

    private int numType(Bracket next) {
        return Tables.NUM_TYPES.get(next.index);
    }

    private boolean isEqualsSymbol(Bracket next) {
        return next.table != 2 || next.index != 19;
    }
    //endregion
}

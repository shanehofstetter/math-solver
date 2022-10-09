package ch.shanehofstetter.calculator;


public final class Operators {

    public static final char OPEN_BRACE = '(';
    public static final char CLOSE_BRACE = ')';
    public static final char MULTIPLY = '*';
    public static final char DIVIDE = '/';
    public static final char FACTORIAL = '!';
    public static final char POWER = '^';
    public static final char MODULO = '%';
    public static final char MINUS = '-';
    public static final char PLUS = '+';
    public static final char WHITESPACE = ' ';
    public static final char EQUALITY = '=';

    public static final char[] OPERATORS = new char[]{OPEN_BRACE, CLOSE_BRACE, MULTIPLY, DIVIDE, FACTORIAL, POWER, MODULO, MINUS, PLUS, EQUALITY};

    private Operators() {
    }

    public static boolean isSupportedMathChar(char o) {
        if (!isSupportedOperator(o)) {
            switch (o) {
                case OPEN_BRACE:
                case CLOSE_BRACE:
                case WHITESPACE:
                    return true;
                default:
                    return false;
            }
        }
        return true;
    }

    public static boolean isSupportedOperator(char o) {
        switch (o) {
            case PLUS:
            case MINUS:
            case DIVIDE:
            case MULTIPLY:
            case MODULO:
            case POWER:
            case FACTORIAL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSecondaryOperator(char o) {
        switch (o) {
            case PLUS:
            case MINUS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPrimaryOperator(char o) {
        switch (o) {
            case DIVIDE:
            case MULTIPLY:
            case MODULO:
            case POWER:
            case FACTORIAL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTopPriorityOperator(char o) {
        switch (o) {
            case POWER:
            case FACTORIAL:
            case MODULO:
                return true;
            default:
                return false;
        }
    }

}

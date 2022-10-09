package ch.shanehofstetter.calculator;

import ch.shanehofstetter.calculator.Nodes.EquationNode;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.NodeNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static ch.shanehofstetter.calculator.Operators.*;

/**
 * Author: Shane Hofstetter
 */

public class MathNodeConverter {
    //All numberNodes in input-order
    ArrayList<MathNumberNode> numberNodes;
    //All operators in input-order
    ArrayList<Character> operators;
    private int mainIndex = 0;
    private int additionalOperatorSteps = 0;
    private boolean isEquation = false;
    private EquationNode equationNode;
    private Calculator calculator;
    private Character acceptedUnknowns[] = new Character[]{'x', 'y', 'z'};

    public MathNodeConverter(Calculator calculator) {
        Constants.init();
        setCalculator(calculator);
    }

    public MathNode convertToNode(String mathTerm) {
        mathTerm = insertWhiteSpace(mathTerm);
        try {
            scanTerm(mathTerm);
            return computeCharList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            printErrorMessage();
            calculator.showError(ex.getMessage());
            return null;
        }
    }

    public void setCalculator(Calculator calculator) {
        this.calculator = calculator;
    }

    private void scanTerm(String term) throws Exception {
        /*
         - split the string into numberNodes and operators with the Scanner-Class
         - detect negative numberNodes (e.g. -3) and turn them with *(-1)
         - remove unsupported characters (don't put them in operator-list)
         */
        numberNodes = new ArrayList<>();
        operators = new ArrayList<>();
        Scanner scanner = new Scanner(term);
        scanner.useLocale(Locale.ENGLISH);
        boolean isOperator = true;
        boolean isNumber = false;
        boolean turnNegative = false;
        Character unknownVariable = null;
        while (scanner.hasNext()) {
            try {
                if (scanner.hasNextDouble()) {
                    double num = scanner.nextDouble();
                    if (turnNegative) {
                        num = num * -1;
                        turnNegative = false;
                    }
                    numberNodes.add(new MathNumberNode(num));
                    isOperator = false;
                    isNumber = true;
                } else {
                    char termPiece;
                    termPiece = scanner.next().toCharArray()[0];
                    if (isOperator) { //last was Operator
                        if (termPiece == MINUS) { //negative now
                            turnNegative = true;
                        }
                    }
                    if (!turnNegative) {
                        if (isSupportedMathChar(termPiece)) {
                            operators.add(termPiece);
                        } else {
                            if (isAcceptedUnknownNumber(termPiece)) {
                                if (unknownVariable == null) {
                                    unknownVariable = termPiece;
                                } else if (termPiece != unknownVariable) {
                                    throw new Exception("Only one unknown variable allowed at the moment");
                                }
                                double amount = 1.0;
                                if (isNumber) {
                                    //last was a number, e.g. 2x
                                    amount = numberNodes.get(numberNodes.size() - 1).getNumber();
                                    numberNodes.remove(numberNodes.size() - 1);
                                }
                                System.out.println("adding " + amount + unknownVariable);
                                numberNodes.add(new UnknownNumberNode(termPiece, amount));
                                isOperator = false;

                            } else if (termPiece == EQUALITY) {
                                isEquation = true;
                                System.out.println("Equation detected!");
                                operators.add(termPiece);
                            } else if (Constants.constants.containsKey(termPiece)) {
                                System.out.println("Constant detected: " + termPiece);
                                numberNodes.add(new MathNumberNode(Constants.constants.get(termPiece)));
                            } else {
                                throw new Exception("Unsupported Character detected: " + termPiece);
                            }
                        }
                    }
                    if (termPiece != OPEN_BRACE && termPiece != CLOSE_BRACE && !isAcceptedUnknownNumber(termPiece)) {
                        isOperator = true;
                    }
                    isNumber = false;
                }
            } catch (NoSuchElementException exc) {
                System.out.println("Exception occurred: " + exc.getMessage());
                calculator.showError("An error occurred.");
            }
        }
    }

    private boolean isAcceptedUnknownNumber(char ch) {

        for (char c : acceptedUnknowns) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    private MathNode computeCharList() throws Exception {
        while (numberNodes.size() >= 2 && operators.size() > 0) {
            mainIndex = 0; //overall mainIndex
            additionalOperatorSteps = 0;
            cleanEmptyBraces();
            if (isEquation) {
                checkForEqualityOperator();
            }
            if (operators.size() > 1) {
                findNextOperation();
            }
            if (operators.size() >= 1) {
                doOperationStepAtIndex(mainIndex, additionalOperatorSteps);
            }
        }
        //Finished calculating
        if (isEquation) {
            makeEquationNode();
            isEquation = false;
            return equationNode;
        } else {
            return numberNodes.get(0);
        }
    }

    private void findNextOperation() {
        //save indexes for later check
        int storedIndex = mainIndex;
        int storedOperatorSteps = additionalOperatorSteps;
        checkOperatorPriority();
        findMostInnerCompleteTerm();
        //Second control for the case we stepped into braces
        //checkOperatorPriority();
        checkIfNextOperatorIsTopPriority();
        //if an index got changed, we moved forward in the maths-term -> check again
        if (storedIndex != mainIndex || storedOperatorSteps != additionalOperatorSteps) {
            findNextOperation();
        }
    }

    private void makeEquationNode() {
        if (numberNodes.size() == 1) {
            if (equationNode != null) {
                equationNode.setRightSideOfEquation(numberNodes.get(0));
            }
        }
    }

    private void checkForEqualityOperator() {
        if (operators.get(0).equals(EQUALITY)) {
            equationNode = new EquationNode(calculator);
            equationNode.setLeftSideOfEquation(numberNodes.get(0));
            operators.remove(0);
            numberNodes.remove(0);
        }
    }

    private void findMostInnerCompleteTerm() {
        //Look for most inner complete Term '...(3+(2+...'
        if (operators.size() > additionalOperatorSteps + mainIndex + 1) {
            while (operators.get(additionalOperatorSteps + mainIndex).equals(OPEN_BRACE)) {
                additionalOperatorSteps++;
                if (operators.size() <= additionalOperatorSteps + mainIndex + 1) {
                    break;
                }
            }
        }
        if (operators.size() > additionalOperatorSteps + mainIndex + 1) {
            while (operators.get(additionalOperatorSteps + mainIndex + 1).equals(OPEN_BRACE)) {
                mainIndex++; //move to next number
                additionalOperatorSteps++; //skip brace
                if (operators.size() <= additionalOperatorSteps + mainIndex + 1) {
                    break;
                }
            }
        }
    }

    private void checkIfNextOperatorIsTopPriority() {
        if (operators.size() > mainIndex + additionalOperatorSteps + 1) {
            //Power or Factorizing has top priority
            if (isPrimaryOperator(operators.get(mainIndex + additionalOperatorSteps))) {
                // if this operator was secondary, it already got incremented
                // by checkOperatorPriority, but because factorial or power priority
                // is higher than * / %, we need to check it here
                if (isTopPriorityOperator(operators.get(mainIndex + additionalOperatorSteps + 1))) {
                    // 3 * 5! or 3 * 5^2
                    //System.out.println("detected priority operator");
                    mainIndex++;
                }
            }
        }
    }

    private void doOperationStepAtIndex(int index, int additionalOperatorSteps) {
        try {
            int operatorIndex = index + additionalOperatorSteps;
            //printStep();
            OperationNode stepNode;
            if (operators.get(operatorIndex).equals(FACTORIAL)) {
                // Calculate factorial (special, because just one number is needed)
                stepNode = new FactorialNode(calculator);
                MathNumberNode num = numberNodes.get(index);
                if (num instanceof NodeNumberNode) {
                    ((FactorialNode) stepNode).setNumber(((NodeNumberNode) num).getNode());
                } else {
                    ((FactorialNode) stepNode).setNumber(num);
                }
                //node.childNode = numberNodes.get(mainIndex);
                //stepResult = factorize(numberNodes.get(mainIndex));
            } else {
                //Do next operation (with next 2 numberNodes)
                MathNumberNode firstNum = numberNodes.get(index);
                MathNumberNode secondNum = numberNodes.get(index + 1);
                char operator = operators.get(operatorIndex);
                //special-case with unknowns..
//                if (operator == MULTIPLY && ((firstNum instanceof UnknownNumberNode && MathNumberNode.isNumber(secondNum)) ||
//                        (secondNum instanceof UnknownNumberNode && MathNumberNode.isNumber(firstNum)))){
//                    //e.g. 2 * x or x * 2
//                    if (firstNum instanceof UnknownNumberNode){
//                        ((UnknownNumberNode) firstNum).setAmount(secondNum.getNumber());
//                    }
//                }
                stepNode = getOperatorNode(operator);
                stepNode = makeOperatorNode(stepNode, firstNum, secondNum);
                //Remove 2 numberNodes
                numberNodes.remove(index);
            }
            NodeNumberNode stepResultNode = new NodeNumberNode();
            stepResultNode.setNode(stepNode);
            numberNodes.remove(index);
            operators.remove(operatorIndex);
            numberNodes.add(index, stepResultNode);
        } catch (Exception e) {
            System.out.println("Error!: " + e.toString());
            printErrorMessage();
        }
    }

    private OperationNode makeOperatorNode(OperationNode node, MathNumberNode first, MathNumberNode second) {
        MathNode firstNumberNode = getNodeFromMathNumberNode(first);
        MathNode secondNumberNode = getNodeFromMathNumberNode(second);
        node.setNumbers(firstNumberNode, secondNumberNode);
        return node;
    }

    private MathNode getNodeFromMathNumberNode(MathNumberNode numberNode) {
        if (numberNode instanceof NodeNumberNode) {
            //Contains an OperationNode which we return now
            return ((NodeNumberNode) numberNode).getNode();
        } else {
            //Is just a Number
            return numberNode;
        }
    }

    private OperationNode getOperatorNode(char op) {
        switch (op) {
            case MULTIPLY:
                return new MultiplicationNode(calculator);
            case PLUS:
                return new SumNode(calculator);
            case MINUS:
                return new SubtractNode(calculator);
            case DIVIDE:
                return new DivisionNode(calculator);
            case MODULO:
                return new ModuloNode(calculator);
            case POWER:
                return new PowerNode(calculator);
            default:
                return null;
        }
    }

    private void checkOperatorPriority() {
        //Check operator order (primary before secondary operators)
        //First check before stepping into possible braces
        if (isSecondaryOperator(operators.get(mainIndex + additionalOperatorSteps))) {
            if (isPrimaryOperator(operators.get(mainIndex + additionalOperatorSteps + 1))) {
                if (operators.size() > (mainIndex + additionalOperatorSteps + 2)) {
                    // Braces open after primary operator, step into
                    if (operators.get(mainIndex + additionalOperatorSteps + 2).equals(OPEN_BRACE)) {
                        // 3 + 5 * (7 - 3)
                        mainIndex += 2;
                        additionalOperatorSteps++; // skip the opening brace
                    } else {
                        // 3 + 5 * 4
                        mainIndex++;
                    }
                } else {
                    //primary op detected
                    mainIndex++;
                }
            }
        }
    }

    private void cleanEmptyBraces() {
        //Clean empty braces
        for (int operatorIndex = 0; operatorIndex < operators.size(); operatorIndex++) {
            try {
                //Check should be save as long as user gives a correct math term
                if (operators.get(operatorIndex).equals(OPEN_BRACE) && operators.get(operatorIndex + 1).equals(CLOSE_BRACE)) {
                    operators.remove(operatorIndex);
                    operators.remove(operatorIndex);
                }
            } catch (IndexOutOfBoundsException e) {
                // can happen because of operators.get(j + 1)...
                // but should not happen in normal cases
                System.out.println("Error, incorrect maths term! (Braces must be closed)");
                calculator.showError("Error, incorrect maths term! (Braces must be closed)");
            }
        }
    }

    private String insertWhiteSpace(String term) {
        /*
         Add whitespace between operators and numberNodes
         */
        char whiteSpace = ' ';
        char comma = '.';
        ArrayList<Character> excludesList = new ArrayList<>();
        excludesList.add(whiteSpace);
        excludesList.add(comma);
        excludesList.add('E');

        ArrayList<Character> charList = makeCharListFromString(term);
        //replacePointsWithCommasIn(charList);
        replaceCommasWithPointsIn(charList);

        boolean isNumber;
        for (int char_index = 0; char_index < charList.size(); char_index++) {
            try {
                int number = Character.getNumericValue(charList.get(char_index));
                isNumber = number != -1;
            } catch (Exception e) {
                isNumber = charList.get(char_index) == comma;
            }
            if ((char_index + 1) < charList.size()) {
                if (isNumber) {
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        Double.valueOf(Character.toString(charList.get(char_index + 1)));
                    } catch (Exception e) {
                        isNumber = false;
                    }
                    if (!isNumber) {
                        if (!excludesList.contains(charList.get(char_index + 1))) {
                            if (charList.get(char_index) != 'E') {
                                charList.add(char_index + 1, whiteSpace);
                                char_index += 1;
                            }
                        }
                    }
                } else {
                    if (!excludesList.contains(charList.get(char_index))) {
                        if (charList.get(char_index + 1) != whiteSpace) {
                            if ((char_index - 1) > 0) {
                                if (charList.get(char_index - 1) != 'E') {
                                    charList.add(char_index + 1, whiteSpace);
                                    char_index += 1;
                                }
                            } else {
                                charList.add(char_index + 1, whiteSpace);
                                char_index += 1;
                            }
                        }
                    }
                }
            }
        }
        String cleanTerm = makeStringFromChars(charList);
        System.out.println("clean math term: [" + cleanTerm + "]");
        calculator.showOutput("clean math term: [" + cleanTerm + "]");
        return cleanTerm;
    }

    private void replacePointsWithCommasIn(ArrayList<Character> charList) {
        replaceInCharList('.', ',', charList);
    }

    private void replaceInCharList(Character oldChar, Character newChar, ArrayList<Character> charList) {
        while (charList.contains(oldChar)) {
            int pointIndex = charList.indexOf(oldChar);
            charList.remove(pointIndex);
            charList.add(pointIndex, newChar);
        }
    }

    private void replaceCommasWithPointsIn(ArrayList<Character> charList) {
        replaceInCharList(',', '.', charList);
    }

    private ArrayList<Character> makeCharListFromString(String string) {
        ArrayList<Character> charList = new ArrayList<>();
        for (char c : string.toCharArray()) {
            charList.add(c);
        }
        return charList;
    }

    private String makeStringFromChars(ArrayList<Character> charList) {
        StringBuilder string = new StringBuilder();
        for (char c : charList) {
            string.append(c);
        }
        return string.toString();
    }

    private void printErrorMessage() {
        System.out.println("Error in your input!");
        System.out.println("Please enter a valid mathematical term or equation.");
        calculator.showError("Please enter a valid mathematical term or equation.");
    }

}

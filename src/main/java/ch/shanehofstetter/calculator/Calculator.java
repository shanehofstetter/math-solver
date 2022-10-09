package ch.shanehofstetter.calculator;

import ch.shanehofstetter.calculator.Nodes.EquationNode;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.NodeNumberNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.OperationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Calculator {

    //TODO: when solving not linear equations, e.g. x^2 -> there need to be >1 results

    private final String exit = "exit";
    private final List<GuiListener> listeners = new ArrayList<>();

    public void startWaitingForInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            printDefaultMessage();
            while (scanner.hasNextLine()) { // scanner blocks on this line
                String line = scanner.nextLine();
                if (line.equals(exit)) {
                    break;
                }
                solveStringTerm(line);
                printDefaultMessage();
            }
        }
    }

    private void printDefaultMessage() {
        System.out.println("-------------------------------------------");
        System.out.printf("Quit with '%s'%n", exit);
        System.out.println("Enter your maths term (example: 2 * 3 + 4):");
    }

    public Double solveStringTerm(String term) {
        MathNodeConverter converter = new MathNodeConverter(this);
        MathNode node = converter.convertToNode(term);
        try {
            if (node instanceof EquationNode) {
                return computeEquationNode((EquationNode) node);
            } else {
                return computeNode(node);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            printErrorMessage();
            showError(ex.getMessage());
            return null;
        }
    }

    private double computeNode(MathNode result) throws Exception {
        if (result != null) {
            if (result instanceof NodeNumberNode) {
                //last node is a NodeNumberNode which contains an OperationNode
                System.out.println("\nmath term converted to tree:");
                ((NodeNumberNode) result).getNode().print();
                showTree(((NodeNumberNode) result).getNode());
                if (((NodeNumberNode) result).getNode() instanceof OperationNode) {
                    System.out.println("\nsolve tree:");

                    MathNode node = ((NodeNumberNode) result).getNode();
                    OperationNode resultNode = (OperationNode) node;
                    resultNode.doOperation();

                    printResult(((MathNumberNode) resultNode.getResult()).getNumber());
                    return ((MathNumberNode) resultNode.getResult()).getNumber();
                }
            } else {
                //last node is a MathNumber, user didn't give a real math-term..
                printResult(((MathNumberNode) result).getNumber());
                return ((MathNumberNode) result).getNumber();
            }
        }
        printErrorMessage();
        return Double.NaN;
    }

    private Double computeEquationNode(EquationNode equationNode) {
        if (equationNode != null) {
            System.out.println("\nequation converted to tree:");
            equationNode.print();
            showTree(equationNode);
            try {
                equationNode.solveEquation();
            } catch (Exception e) {
                printEquationErrorMessage(e.toString());
            }
            return equationNode.getResult();
        }
        printEquationError();
        return null;
    }

    private void printResult(double result) {
        System.out.println("RESULT\t= " + result);
        showResult("" + result);
    }

    private void printErrorMessage() {
        System.out.println("Error in your input!");
        System.out.println("Please enter a valid mathematical term or equation.");
        showError("Please enter a valid mathematical term or equation.");
    }

    private void printEquationErrorMessage(String errorMessage) {
        System.out.println("Error: " + errorMessage);
        System.out.println("Please enter a valid equation with only one unknown variable.");
        showError(errorMessage);
    }

    private void printEquationError() {
        System.out.println("Please enter a valid equation with only one unknown variable.");
    }

    public void addListener(GuiListener toAdd) {
        listeners.add(toAdd);
    }

    public void showOutput(String output) {
        for (GuiListener listener : listeners)
            listener.showOutput(output);
    }

    public void showResult(String result) {
        for (GuiListener listener : listeners)
            listener.showResult(result);
    }

    public void showError(String error) {
        for (GuiListener listener : listeners)
            listener.showError(error);
    }

    private void showTree(MathNode rootNode) {
        for (GuiListener listener : listeners)
            listener.showTree(rootNode);
    }
}

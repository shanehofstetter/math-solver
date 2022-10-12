package ch.shanehofstetter.calculator;

import ch.shanehofstetter.calculator.Nodes.EquationNode;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.NodeNumberNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.OperationNode;

import java.util.ArrayList;
import java.util.List;


public class Calculator {

    //TODO: when solving not linear equations, e.g. x^2 -> there need to be >1 results

    private final List<GuiListener> listeners = new ArrayList<>();

    public Double solveStringTerm(String term) {
        MathNodeConverter converter = new MathNodeConverter(this);
        MathNode node = converter.convertToNode(term);
        if (node == null) {
            printErrorMessage();
            return null;
        }
        try {
            if (node instanceof EquationNode) {
                return computeEquationNode((EquationNode) node);
            } else {
                return computeNode(node);
            }
        } catch (Exception ex) {
            printErrorMessage();
            showError(ex.getMessage(), ex);
            return null;
        }
    }

    private Double computeNode(MathNode result) throws Exception {
        if (result instanceof NodeNumberNode) {
            //last node is a NodeNumberNode which contains an OperationNode
            showTree(((NodeNumberNode) result).getNode());
            if (((NodeNumberNode) result).getNode() instanceof OperationNode) {
                showOutput("\nsolve tree:");

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
        return null;
    }

    private Double computeEquationNode(EquationNode equationNode) {
        if (equationNode != null) {
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
        showResult("" + result);
    }

    private void printErrorMessage() {
        showError("Please enter a valid mathematical term or equation.", null);
    }

    private void printEquationErrorMessage(String errorMessage) {
        showError(errorMessage, null);
    }

    private void printEquationError() {
        showError("Please enter a valid equation with only one unknown variable.", null);
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

    public void showError(String error, Exception exception) {
        for (GuiListener listener : listeners)
            listener.showError(error, exception);
    }

    private void showTree(MathNode rootNode) {
        for (GuiListener listener : listeners)
            listener.showTree(rootNode);
    }
}

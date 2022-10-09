package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;


public class OperationNode extends MathNode {
    Calculator parent;
    private MathNode result;

    public OperationNode(Calculator parent) {
        this.parent = parent;
    }

    public static boolean isKnownNumber(MathNode numberNode) {
        if (numberNode instanceof MathNumberNode) {
            return !(numberNode instanceof UnknownNumberNode);
        }
        return false;
    }

    public static boolean isSecondaryOperation(OperationNode operationNode) {
        return operationNode instanceof SumNode || operationNode instanceof SubtractNode;
    }

    public MathNode getResult() {
        if (result == null) {
            try {
                doOperation();
            } catch (Exception e) {
                System.out.println("Fehler: " + e.toString());
            }
        }
        return result;
    }

    protected void setResult(MathNode result) {
        this.result = result;
    }

    public void doOperation() throws Exception {

    }

    public void setNumbers(MathNode firstNumber, MathNode secondNumber) {

    }

    public OperationNode getCounterOperation() {
        return null;
    }

    public OperationNode getCounterOperation(boolean withUnknown) {
        return getCounterOperation();
    }

    public MathNode getCounterPart() {
        if (children.size() > 1) {
            return null;
        } else {
            return children.get(0);
        }
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {

    }

    public MathNode getUnknownPart() {
        for (MathNode node : children) {
            if (node instanceof OperationNode) {
                return node;
            }
        }
        for (MathNode node : children) {
            if (node instanceof UnknownNumberNode) {
                return node;
            }
        }
        return null;
    }

    public void setUnknownPart(MathNode unknownPart) {

    }

    public MathNode getKnownPart() {
        for (MathNode node : children) {
            if (node instanceof MathNumberNode) {
                if (!(node instanceof UnknownNumberNode)) {
                    return node;
                }
            }
        }
        return null;
    }

    public void setKnownPart(MathNode newKnownPart) {
    }

    public MathNode getKnownPartCorrected() {
        return getKnownPart();
    }

    protected boolean areBothNumberNodes() {
        if (children.size() < 2) {
            return false;
        }
        return children.get(0) instanceof MathNumberNode && children.get(1) instanceof MathNumberNode;
    }

    protected boolean areBothNumbersUnknown() {
        boolean bothUnknown = true;
        for (MathNode node : children) {
            if (isKnownNumber(node)) {
                bothUnknown = false;
            }
        }
        return bothUnknown;
    }

    protected boolean isOneNumberUnknown() {
        return children.get(0) instanceof UnknownNumberNode || children.get(1) instanceof UnknownNumberNode;
    }

    public MathNode performOperation(OperationNode operationToPerform) throws Exception {
        //is needed in equations with unknown parts
        //e.g. 2x = x + 3   |-x
        return null;
    }
}

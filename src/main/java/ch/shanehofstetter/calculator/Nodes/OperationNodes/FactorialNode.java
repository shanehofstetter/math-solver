package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;


public class FactorialNode extends OperationNode {
    private MathNode childNode;

    public FactorialNode(Calculator parent) {
        super(parent);
        setName("!");
    }

    public void setNumber(MathNode number) {
        setChildNode(number);
        children.add(number);
    }

    @Override
    public void doOperation() {
        if (getChildNode() instanceof OperationNode) {
            if (((OperationNode) getChildNode()).getResult() == null) {
                setResult(null);
                return;
            }
            setChildNode(((OperationNode) getChildNode()).getResult());
        }
        if (getChildNode() instanceof MathNumberNode && !isUnknownNumber()) {
            setResult(new MathNumberNode(factorize(((MathNumberNode) getChildNode()).number)));
        } else {
            setResult(null);
        }
    }

    private boolean isUnknownNumber() {
        return getChildNode() instanceof UnknownNumberNode;
    }

    public double factorize(Double n) {
        Double fact = 1.0; // this  will be the result
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        parent.showOutput(n + "! = " + fact);
        return fact;
    }

    public MathNode getChildNode() {
        return childNode;
    }

    public void setChildNode(MathNode childNode) {
        this.childNode = childNode;
    }

    public OperationNode getCounterOperation() {
        FactorialCounterNode counterNode = new FactorialCounterNode(parent);
        if (isKnownNumber(getChildNode())) {
            counterNode.setChildNode(getChildNode());
        }
        return counterNode;
    }
}

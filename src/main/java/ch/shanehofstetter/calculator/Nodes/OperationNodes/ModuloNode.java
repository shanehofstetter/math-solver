package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;


public class ModuloNode extends OperationNode {
    private MathNode firstNumber;
    private MathNode secondNumber;
    public ModuloNode(Calculator parent) {
        super(parent);
        setName("%");
    }

    public void setNumbers(MathNode first, MathNode second) {
        setFirstNumber(first);
        setSecondNumber(second);
        children.add(first);
        children.add(second);
    }

    @Override
    public void doOperation() {
        if (getFirstNumber() instanceof OperationNode) {
            setFirstNumber(((OperationNode) getFirstNumber()).getResult());
        }
        if (getSecondNumber() instanceof OperationNode) {
            setSecondNumber(((OperationNode) getSecondNumber()).getResult());
        }
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(modulo(((MathNumberNode) getFirstNumber()).number, ((MathNumberNode) getSecondNumber()).number)));
        } else {
            setResult(null);
        }

    }

    protected double modulo(double i, double j) {
        double result = i % j;
        System.out.println(i + " % " + j + " = " + result);
        parent.showOutput(i + " % " + j + " = " + result);
        return result;
    }

    public MathNode getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(MathNode firstNumber) {
        this.firstNumber = firstNumber;
    }

    public MathNode getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(MathNode secondNumber) {
        this.secondNumber = secondNumber;
    }

    public OperationNode getCounterOperation() {
        ModuloCounterNode counterNode = new ModuloCounterNode(parent);
        if (isKnownNumber(getFirstNumber())) {
            counterNode.setFirstNumber(getFirstNumber());
        } else if (isKnownNumber(getSecondNumber())) {
            counterNode.setSecondNumber(getSecondNumber());
        }
        return counterNode;
    }
}

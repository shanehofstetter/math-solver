package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;


public class MultiplicationNode extends OperationNode {
    private MathNode firstFactor;
    private MathNode secondFactor;
    public MultiplicationNode(Calculator parent) {
        super(parent);
        setName("*");
    }

    public void setNumbers(MathNode first, MathNode second) {
        setFirstFactor(first);
        setSecondFactor(second);
    }

    @Override
    public void doOperation() throws Exception {
        if (getFirstFactor() instanceof OperationNode) {
            if (((OperationNode) getFirstFactor()).getResult() == null) {
                setResult(null);
            } else {
                setFirstFactor(((OperationNode) getFirstFactor()).getResult());
            }
        }
        if (getSecondFactor() instanceof OperationNode) {
            if (((OperationNode) getSecondFactor()).getResult() == null) {
                setResult(null);
            } else {
                setSecondFactor(((OperationNode) getSecondFactor()).getResult());
            }
        }
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(multiplicate(((MathNumberNode) getFirstFactor()).number, ((MathNumberNode) getSecondFactor()).number)));
        } else if (areBothNumbersUnknown()) {
            //2x * x ==> 2x^2 ==> 2 * (x^2)
            //1. make a PowerNode
            //2. make a MultiplicationNode
            if (((UnknownNumberNode) getFirstFactor()).getUnknownName() == ((UnknownNumberNode) getSecondFactor()).getUnknownName()) {
                PowerNode powerNode = new PowerNode(parent);
                double _amountFirst = ((UnknownNumberNode) getFirstFactor()).getAmount();
                double _amountSecond = ((UnknownNumberNode) getSecondFactor()).getAmount();
                System.out.println(getFirstFactor().getName() + " * " + getSecondFactor().getName() + " = " + _amountFirst * _amountSecond + ((UnknownNumberNode) getFirstFactor()).getUnknownName() + "^2");

                ((UnknownNumberNode) getFirstFactor()).setAmount(1.0);
                ((UnknownNumberNode) getSecondFactor()).setAmount(1.0);
                powerNode.setFactor(getFirstFactor());
                powerNode.setPower(new MathNumberNode(2.0));

                MultiplicationNode multiplicationNode = new MultiplicationNode(parent);
                multiplicationNode.setNumbers(new MathNumberNode(multiplicate(_amountFirst, _amountSecond)), powerNode);
                setResult(multiplicationNode);
            } else {
                throw new Exception("different unknowns");
            }
        } else if (getUnknownPart() != null && getKnownPart() != null) {
            if (getUnknownPart() instanceof UnknownNumberNode) {
                setResult(makeUnknownWithAmount());
            } else {
                //unknown part is an operationNode
                //(3 - x) * 2
                //= 6 - 2x
                if (getUnknownPart() instanceof SumNode) {
                    SumNode operationNode = (SumNode) getUnknownPart();
                    MathNumberNode knownNumber = new MathNumberNode(multiplicate(operationNode.getKnownPart().getNumber(), ((MathNumberNode) getKnownPart()).getNumber()));
                    double _amountOriginal = ((UnknownNumberNode) operationNode.getUnknownPart()).getAmount();
                    UnknownNumberNode unknownNumberNode = ((UnknownNumberNode) operationNode.getUnknownPart());
                    unknownNumberNode.setAmount(multiplicate(_amountOriginal, ((MathNumberNode) getKnownPart()).getNumber()));
                    operationNode.setFirstSummand(knownNumber);
                    operationNode.setSecondSummand(unknownNumberNode);
                    setResult(operationNode);
                } else if (getUnknownPart() instanceof SubtractNode) {
                    SubtractNode subtractNode = (SubtractNode) getUnknownPart();
                    UnknownNumberNode unknownNumberNode = (UnknownNumberNode) subtractNode.getUnknownPart();
                    MathNumberNode knownNumber = (MathNumberNode) subtractNode.getKnownPart();
                    double _amountOriginal = unknownNumberNode.getAmount();
                    unknownNumberNode.setAmount(multiplicate(_amountOriginal, ((MathNumberNode) getKnownPart()).getNumber()));
                    subtractNode.setMinuend(unknownNumberNode);
                    knownNumber = new MathNumberNode(multiplicate(knownNumber.getNumber(), ((MathNumberNode) getKnownPart()).getNumber()));
                    subtractNode.setKnownPart(knownNumber);
                    subtractNode.setUnknownPart(unknownNumberNode);
                    setResult(subtractNode);
                }
            }
        } else if (getFirstFactor() instanceof OperationNode || getSecondFactor() instanceof OperationNode) {
            //( 3 - x ) * x  or ( 3 - x ) * ( x + 2 )
            throw new Exception("areBothNumbersUnknown not implemented");
        } else {
            setResult(null);
        }
    }

    private UnknownNumberNode makeUnknownWithAmount() {
        if (getFirstFactor() instanceof UnknownNumberNode) {
            ((UnknownNumberNode) getFirstFactor()).setAmount(multiplicate(((MathNumberNode) getSecondFactor()).getNumber(), ((UnknownNumberNode) getFirstFactor()).getAmount()));
            return (UnknownNumberNode) getFirstFactor();
        } else if (getSecondFactor() instanceof UnknownNumberNode) {
            ((UnknownNumberNode) getSecondFactor()).setAmount(multiplicate(((MathNumberNode) getFirstFactor()).getNumber(), ((UnknownNumberNode) getSecondFactor()).getAmount()));
            return (UnknownNumberNode) getSecondFactor();
        }
        return null;
    }

    private double multiplicate(double i, double j) {
        double result = i * j;
        parent.showOutput(i + " * " + j + " = " + result);
        return result;
    }

    public MathNode getFirstFactor() {
        return firstFactor;
    }

    public void setFirstFactor(MathNode firstFactor) {
        if (this.firstFactor != null) {
            children.remove(this.firstFactor);
        }
        children.add(firstFactor);
        this.firstFactor = firstFactor;
    }

    public MathNode getSecondFactor() {
        return secondFactor;
    }

    public void setSecondFactor(MathNode secondFactor) {
        if (this.secondFactor != null) {
            children.remove(this.secondFactor);
        }
        children.add(secondFactor);
        this.secondFactor = secondFactor;
    }

    public OperationNode getCounterOperation() {
        // Multiply
        // 4 * x = 8
        // x = 8 / 4
        DivisionNode counterNode = new DivisionNode(parent);
        if (isKnownNumber(getFirstFactor())) {
            counterNode.setDivisor(getFirstFactor());
        } else if (isKnownNumber(getSecondFactor())) {
            counterNode.setDivisor(getSecondFactor());
        }
        return counterNode;
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        if (getFirstFactor() == null) {
            setFirstFactor(counterNumberNode);
        } else {
            setSecondFactor(counterNumberNode);
        }
    }
}

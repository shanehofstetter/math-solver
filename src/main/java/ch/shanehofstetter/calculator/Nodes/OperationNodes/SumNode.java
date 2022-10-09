package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;


public class SumNode extends OperationNode {
    private MathNode firstSummand;
    private MathNode secondSummand;
    public SumNode(Calculator parent) {
        super(parent);
        setName("+");
    }

    public void setNumbers(MathNode first, MathNode second) {
        setFirstSummand(first);
        setSecondSummand(second);
    }

    @Override
    public void doOperation() throws Exception {
        if (getFirstSummand() instanceof OperationNode) {
            if (((OperationNode) getFirstSummand()).getResult() == null) {
                setResult(null);
            } else {
                setFirstSummand(((OperationNode) getFirstSummand()).getResult());
            }
        }
        if (getSecondSummand() instanceof OperationNode) {
            if (((OperationNode) getSecondSummand()).getResult() == null) {
                setResult(null);
            } else {
                setSecondSummand(((OperationNode) getSecondSummand()).getResult());
            }
        }
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(sum(((MathNumberNode) getFirstSummand()).number, ((MathNumberNode) getSecondSummand()).number)));
        } else if (areBothNumbersUnknown()) {
            if (getFirstSummand() instanceof UnknownNumberNode && getSecondSummand() instanceof UnknownNumberNode) {
                ((UnknownNumberNode) getFirstSummand()).setAmount(sum(((UnknownNumberNode) getFirstSummand()).getAmount(), ((UnknownNumberNode) getSecondSummand()).getAmount()));
                setResult(getFirstSummand());
            } else {
                setResult(null);
            }
        } else if (getUnknownPart() != null && getKnownPart() != null) {
            if (isOneNumberUnknown()) {
                setResult(null);
            } else {
                //(n ? x) + b
                if (isSecondaryOperation((OperationNode) getUnknownPart())) {
                    //(n +- x) + b
                    //simplifiable if operationNode has known Part
                    if (((OperationNode) getUnknownPart()).getKnownPart() != null) {
                        if (getUnknownPart() instanceof SumNode) {
                            SumNode sumNode = (SumNode) getUnknownPart();
                            sumNode.setKnownPart(new MathNumberNode(sum(getKnownPart().getNumber(), sumNode.getKnownPart().getNumber())));
                            setResult(sumNode);
                        } else {
                            SubtractNode subtractNode = (SubtractNode) getUnknownPart();
                            if (isKnownNumber(subtractNode.getMinuend())) {
                                //(n - x ) + b
                                //= n + b - x
                                //---->>> this does not work correctly...
                                subtractNode.setMinuend(new MathNumberNode(sum(getKnownPart().getNumber(), ((MathNumberNode) subtractNode.getMinuend()).getNumber())));
                                setResult(subtractNode);
                            } else {
                                //(x - n) + b
                                //x - n + b
                                if (((MathNumberNode) subtractNode.getKnownPart()).getNumber() < getKnownPart().getNumber()) {
                                    //x - 2 + 3
                                    //becomes an addition
                                    SubtractNode subNode = new SubtractNode(parent);
                                    subNode.setNumbers(getKnownPart(), subtractNode.getKnownPart());
                                    MathNumberNode resultNode = (MathNumberNode) subNode.getResult();
                                    setFirstSummand(subtractNode.getUnknownPart());
                                    setSecondSummand(resultNode);
                                    setResult(this);
                                }
                            }
                        }
                    }
                } else {
                    setResult(null);
                }
            }
        } else {
            setResult(null);
        }
    }

    private double sum(double i, double j) {
        double result = i + j;
        System.out.println(i + " + " + j + " = " + result);
        parent.showOutput(i + " + " + j + " = " + result);
        return result;
    }

    public MathNode getFirstSummand() {
        return firstSummand;
    }

    public void setFirstSummand(MathNode firstSummand) {
        if (this.firstSummand != null) {
            children.remove(this.firstSummand);
        }
        children.add(firstSummand);
        this.firstSummand = firstSummand;
    }

    public MathNode getSecondSummand() {
        return secondSummand;
    }

    public void setSecondSummand(MathNode secondSummand) {
        if (this.secondSummand != null) {
            children.remove(this.secondSummand);
        }
        children.add(secondSummand);
        this.secondSummand = secondSummand;
    }

    public OperationNode getCounterOperation(boolean withUnknown) {
        if (withUnknown) {
            if (getUnknownPart() == null) {
                return getCounterOperation();
            } else {
                SubtractNode counterNode = new SubtractNode(parent);
                counterNode.setSubtrahend(getUnknownPart());
                return counterNode;
            }
        } else {
            return getCounterOperation();
        }
    }

    public OperationNode getCounterOperation() {
        SubtractNode counterNode = new SubtractNode(parent);
        if (isKnownNumber(getFirstSummand())) {
            counterNode.setSubtrahend(getFirstSummand());
        } else if (isKnownNumber(getSecondSummand())) {
            counterNode.setSubtrahend(getSecondSummand());
        } else if (getFirstSummand() instanceof UnknownNumberNode) {
            counterNode.setSubtrahend(getFirstSummand());
        } else if (getSecondSummand() instanceof UnknownNumberNode) {
            counterNode.setSubtrahend(getSecondSummand());
        } else {
            counterNode.setSubtrahend(getUnknownPart());
        }
        return counterNode;
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        if (getFirstSummand() == null) {
            setFirstSummand(counterNumberNode);
        } else {
            setSecondSummand(counterNumberNode);
        }
    }

    @Override
    public MathNode performOperation(OperationNode operationToPerform) throws Exception {
        if (operationToPerform instanceof SubtractNode) {
            //perform a subtraction on one of the numbers
            MathNode subtrahend = ((SubtractNode) operationToPerform).getSubtrahend();
            if (subtrahend == null) {
                System.out.println("Minuend is: " + ((SubtractNode) operationToPerform).getMinuend().getName());
                System.out.println("this is: " + getName() + " " + getFirstSummand().getName() + " " + getSecondSummand().getName());
                throw new Exception("subtrahend is null, not supported");
            }
            if (subtrahend instanceof UnknownNumberNode) {
                //e.g. -x
                return performSubtractionOnUnknownPart((UnknownNumberNode) subtrahend);
            } else if (subtrahend instanceof MathNumberNode) {
                //e.g. -3
                MathNode knownPart = getKnownPart();
                ((SubtractNode) operationToPerform).setMinuend(knownPart);
                knownPart = operationToPerform.getResult();
                setKnownPart(knownPart);
                return this;
            }
        }
        if (operationToPerform instanceof SumNode) {
            try {
                if (operationToPerform.getKnownPart() != null) {
                    MathNumberNode summandToAdd = (MathNumberNode) operationToPerform.getKnownPart();
                    MathNumberNode summand = new MathNumberNode(sum(getKnownPart().getNumber(), summandToAdd.getNumber()));
                    setKnownPart(summand);
                } else {
                    UnknownNumberNode unknown = (UnknownNumberNode) operationToPerform.getUnknownPart();
                    if (getUnknownPart() instanceof UnknownNumberNode) {
                        if (((UnknownNumberNode) getUnknownPart()).getUnknownName() == unknown.getUnknownName()) {
                            SumNode sum = new SumNode(parent);
                            sum.setFirstSummand(new MathNumberNode(unknown.getAmount()));
                            sum.setSecondSummand(new MathNumberNode(((UnknownNumberNode) getUnknownPart()).getAmount()));
                            setUnknownPart(new UnknownNumberNode(unknown.getUnknownName(), ((MathNumberNode) sum.getResult()).getNumber()));
                        }
                    } else {
                        SumNode sumNode = new SumNode(parent);
                        sumNode.setFirstSummand(this);
                        sumNode.setSecondSummand(operationToPerform.getUnknownPart());
                        if (sumNode.getResult() == null) {
                            return sumNode;
                        } else {
                            return sumNode.getResult();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception in SumNode: " + e.toString());
            }

            return this;
        }
        if (operationToPerform instanceof MultiplicationNode) {
            MathNumberNode factor = (MathNumberNode) operationToPerform.getCounterPart();
            MultiplicationNode firstSummand = new MultiplicationNode(parent);
            firstSummand.setFirstFactor(factor);
            firstSummand.setSecondFactor(getFirstSummand());
            if (firstSummand.getResult() == null) {
                setFirstSummand(firstSummand);
            } else {
                setFirstSummand(firstSummand.getResult());
            }
            MultiplicationNode secondSummand = new MultiplicationNode(parent);
            secondSummand.setFirstFactor(factor);
            secondSummand.setSecondFactor(getSecondSummand());
            if (secondSummand.getResult() == null) {
                setSecondSummand(secondSummand);
            } else {
                setSecondSummand(secondSummand.getResult());
            }
            return this;
        }
        return null;
    }

    private MathNode performSubtractionOnUnknownPart(UnknownNumberNode unknownNumberNode) {
        UnknownNumberNode unknownToOperateOn;
        if (getFirstSummand() instanceof UnknownNumberNode) {
            unknownToOperateOn = (UnknownNumberNode) getFirstSummand();
        } else if (getSecondSummand() instanceof UnknownNumberNode) {
            unknownToOperateOn = (UnknownNumberNode) getSecondSummand();
        } else {
            return null;
        }
        if (unknownNumberNode.getUnknownName() == unknownNumberNode.getUnknownName()) {
            SubtractNode subtraction = new SubtractNode(parent);
            subtraction.setMinuend(unknownToOperateOn.getAmount());
            subtraction.setSubtrahend(unknownNumberNode.getAmount());
            if (((MathNumberNode) subtraction.getResult()).getNumber() == 0.0) {
                //eliminated unknown
                return getKnownPart();
            }
            unknownToOperateOn.setAmount(((MathNumberNode) subtraction.getResult()).getNumber());
            return this;
        }
        return null;
    }

    public MathNumberNode getKnownPart() {
        if (isKnownNumber(getFirstSummand())) {
            return (MathNumberNode) getFirstSummand();
        } else return (MathNumberNode) getSecondSummand();
    }

    public void setKnownPart(MathNode newKnownPart) {
        if (isKnownNumber(getFirstSummand())) {
            setFirstSummand(newKnownPart);
        } else {
            setSecondSummand(newKnownPart);
        }
    }

    public void setUnknownPart(MathNode unknownPart) {
        if (isKnownNumber(getFirstSummand()) && !isKnownNumber(getSecondSummand())) {
            setSecondSummand(unknownPart);
        } else if (isKnownNumber(getSecondSummand()) && !isKnownNumber(getFirstSummand())) {
            setFirstSummand(unknownPart);
        }
    }
}

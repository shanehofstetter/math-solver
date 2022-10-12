package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;


public class SubtractNode extends OperationNode {

    private MathNode minuend;
    private MathNode subtrahend;
    public SubtractNode(Calculator parent) {
        super(parent);
        setName("-");
    }

    public void setNumbers(MathNode first, MathNode second) {
        setMinuend(first);
        setSubtrahend(second);
    }

    @Override
    public void doOperation() throws Exception {
        if (getMinuend() instanceof OperationNode) {
            if (((OperationNode) getMinuend()).getResult() == null) {
                setResult(null);
            } else {
                setMinuend(((OperationNode) getMinuend()).getResult());
            }
        }
        if (getSubtrahend() instanceof OperationNode) {
            if (((OperationNode) getSubtrahend()).getResult() == null) {
                setResult(null);

            } else {
                setSubtrahend(((OperationNode) getSubtrahend()).getResult());
            }
        }
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(subtract(((MathNumberNode) getMinuend()).number, ((MathNumberNode) getSubtrahend()).number)));
        } else if (areBothNumbersUnknown()) {
            ((UnknownNumberNode) getMinuend()).setAmount(subtract(((UnknownNumberNode) getMinuend()).getAmount(), ((UnknownNumberNode) getSubtrahend()).getAmount()));
            setResult(getMinuend());
        } else if (getUnknownPart() != null && getKnownPart() != null) {
            if (isOneNumberUnknown()) {
                setResult(null);
            } else {
                if (isSecondaryOperation((OperationNode) getUnknownPart())) {
                    if (((OperationNode) getUnknownPart()).getKnownPart() != null) {
                        if (getUnknownPart() instanceof SumNode) {
                            //minuend - subtrahend
                            SumNode sumNode = (SumNode) getUnknownPart();
                            UnknownNumberNode unknown = (UnknownNumberNode) sumNode.getUnknownPart();
                            if (getMinuend() instanceof SumNode) {
                                //(4 + x) - 6          = x - 2
                                //or (x + 4) - 6       = x - 2
                                //(10 + x) - 4         = x + 6
                                SubtractNode subtractNode = new SubtractNode(parent);
                                subtractNode.setMinuend(sumNode.getKnownPart());
                                subtractNode.setSubtrahend(getSubtrahend());
                                MathNumberNode resultNumber = (MathNumberNode) subtractNode.getResult();
                                if (resultNumber.getNumber() > 0) {
                                    //sumNode
                                    sumNode.setKnownPart(resultNumber);
                                    setResult(sumNode);
                                } else if (resultNumber.getNumber() == 0) {
                                    //eliminated
                                    setResult(unknown);
                                } else {
                                    //subtractNode
                                    resultNumber.invertNumber();
                                    SubtractNode resultSubtractNode = new SubtractNode(parent);
                                    resultSubtractNode.setMinuend(unknown);
                                    resultSubtractNode.setSubtrahend(resultNumber);
                                    setResult(resultSubtractNode);
                                }
                            } else {
                                //6-(4+x)              = 2 - x
                                //2-(8+x)              = -6-x
                                SubtractNode subtractNode = new SubtractNode(parent);
                                subtractNode.setMinuend(getKnownPart());
                                subtractNode.setSubtrahend(sumNode.getKnownPart());
                                MathNumberNode resultNumber = (MathNumberNode) subtractNode.getResult();
                                SubtractNode resultSubtractNode = new SubtractNode(parent);
                                resultSubtractNode.setMinuend(resultNumber);
                                resultSubtractNode.setSubtrahend(unknown);
                                setResult(resultSubtractNode);
                            }
                        } else {
                            SubtractNode subtractNode = (SubtractNode) getUnknownPart();
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

    private double subtract(double i, double j) {
        double result = i - j;
        parent.showOutput(i + " - " + j + " = " + result);
        return result;
    }

    public MathNode getMinuend() {
        return minuend;
    }

    public void setMinuend(MathNode minuend) {
        if (this.minuend != null) {
            children.remove(this.minuend);
        }
        children.add(minuend);
        this.minuend = minuend;
    }

    public void setMinuend(double minuend) {
        setMinuend(new MathNumberNode(minuend));
    }

    public MathNode getSubtrahend() {
        return subtrahend;
    }

    public void setSubtrahend(double subtrahend) {
        setSubtrahend(new MathNumberNode(subtrahend));
    }

    public void setSubtrahend(MathNode subtrahend) {
        if (this.subtrahend != null) {
            children.remove(this.subtrahend);
        }
        children.add(subtrahend);
        this.subtrahend = subtrahend;
    }

    public OperationNode getCounterOperation() {
        // Minus , sides do matter:
        // 6 - x = 4
        // x = 6 - 4

        // x - 6 = 4
        // x = 4 + 6
        OperationNode counterNode = null;
//        System.out.println("this causes an error .. fix in equation node somehow, decide when to do which operation");

        if (isKnownNumber(getMinuend())) {
            counterNode = new SubtractNode(parent);
            ((SubtractNode) counterNode).setMinuend(getMinuend());
        } else if (isKnownNumber(getSubtrahend())) {
            counterNode = new SumNode(parent);
            ((SumNode) counterNode).setFirstSummand(getSubtrahend());
        }
        if (counterNode == null)
            System.out.println("return null counternode");
        return counterNode;
    }

    public OperationNode getCounterOperation(boolean withUnknown) {
        if (withUnknown) {
            if (!isKnownNumber(getSubtrahend()) && isKnownNumber(getMinuend())) {
                if (getUnknownPart() != null) {
                    MathNode unknown = getUnknownPart();
                    OperationNode counterNode = new SumNode(parent);
                    ((SumNode) counterNode).setFirstSummand(unknown);
                    return counterNode;
                } else {
                    System.out.println("unknown part is null");
                    return getCounterOperation();
                }
            } else {
                return getCounterOperation();
            }
        } else {
            return getCounterOperation();
        }
        //System.out.println("return null counternode with Unknown");
        //return null;
    }

    public MathNode performOperation(OperationNode operationToPerform) throws Exception {
        if (operationToPerform instanceof SubtractNode) {
            //e.g - 3
            //(x - 5) -3 => x -8
            //(5 - x) -3 => 2 - x
            MathNode subtrahend = ((SubtractNode) operationToPerform).getSubtrahend();
            if (subtrahend == null) {
//                if (((SubtractNode) operationToPerform).getMinuend() != null){
//
//                }
                System.out.println("minuend is: " + getMinuend().toString() + " " + getMinuend().getName());
                throw new Exception("subtrahend is null, not supported");
            }
            if (subtrahend instanceof UnknownNumberNode) {
                //e.g. -x
                return performSubtractionOnUnknownPart((UnknownNumberNode) subtrahend);
            } else if (subtrahend instanceof MathNumberNode) {
                //e.g. -3
                //(x -5) -3 => x - 8
                //(5 - x) -3 => 2 - x
                if (isKnownNumber(getMinuend())) {
                    MathNode knownPart = getKnownPart();
                    ((SubtractNode) operationToPerform).setMinuend(knownPart);
                    knownPart = operationToPerform.getResult();
                    setKnownPart(knownPart);
                } else {
                    SumNode sumNode = new SumNode(parent);
                    sumNode.setFirstSummand(getSubtrahend());
                    sumNode.setSecondSummand(subtrahend);
                    setSubtrahend(sumNode.getResult());
                }
                return this;
            }
        }
        if (operationToPerform instanceof SumNode) {
            //(x-5) + 3 => x -2
            //(x-5) + 8 => x + 3
            MathNumberNode summandToAdd = (MathNumberNode) operationToPerform.getKnownPart();
            double amount = summandToAdd.getNumber();
            if (isKnownNumber(getSubtrahend())) {
                //(x - 5) + 3
                double subtrahendAmount = ((MathNumberNode) getSubtrahend()).getNumber();
                if (amount > subtrahendAmount) {
                    //gets an addition
                    //(x - 5) + 8
                    summandToAdd.invertNumber();
                    SumNode sumNode = new SumNode(parent);
                    sumNode.setFirstSummand(summandToAdd);
                    sumNode.setSecondSummand(getSubtrahend());
                    SumNode resultSumNode = new SumNode(parent);
                    resultSumNode.setFirstSummand(getUnknownPart());
                    resultSumNode.setSecondSummand(sumNode.getResult());
                    return resultSumNode;
                } else {
                    SubtractNode subtractNode = new SubtractNode(parent);
                    subtractNode.setMinuend(getSubtrahend());
                    subtractNode.setSubtrahend(summandToAdd);
                    setSubtrahend(subtractNode.getResult());
                    return this;
                }
            } else if (isKnownNumber(getMinuend())) {
                //(5 - x) + 3
                SumNode sumNode = new SumNode(parent);
                sumNode.setFirstSummand(getMinuend());
                sumNode.setSecondSummand(summandToAdd);
                setMinuend(sumNode.getResult());
                return this;
            }
        }
        if (operationToPerform instanceof MultiplicationNode) {
            MathNumberNode factor = (MathNumberNode) operationToPerform.getCounterPart();
            MultiplicationNode minuend = new MultiplicationNode(parent);
            minuend.setFirstFactor(factor);
            minuend.setSecondFactor(getMinuend());
            if (minuend.getResult() == null) {
                setMinuend(minuend);
            } else {
                setMinuend(minuend.getResult());
            }
            MultiplicationNode subtrahend = new MultiplicationNode(parent);
            subtrahend.setFirstFactor(factor);
            subtrahend.setSecondFactor(getSubtrahend());
            if (subtrahend.getResult() == null) {
                setSubtrahend(subtrahend);
            } else {
                setSubtrahend(subtrahend.getResult());
            }
            return this;
        }
        return null;
    }

    private MathNode performSubtractionOnUnknownPart(UnknownNumberNode unknownNumberNode) {
        UnknownNumberNode unknownToOperateOn;
        boolean isSubtrahendUnknown = true;
        if (getSubtrahend() instanceof UnknownNumberNode) {
            //e.g. 3 - 2x (-x)
            unknownToOperateOn = (UnknownNumberNode) getSubtrahend();
        } else {
            //e.g. 2x - 34 - x
            isSubtrahendUnknown = false;
            unknownToOperateOn = (UnknownNumberNode) getMinuend();
            //System.out.println("performSubtractionOnUnknownPart : subtrahend is not an unknown number! returning null");
            //return null;
        }
        SubtractNode subtraction = new SubtractNode(parent);
        subtraction.setMinuend(unknownToOperateOn.getAmount());
        subtraction.setSubtrahend(unknownNumberNode.getAmount());
        if (((MathNumberNode) subtraction.getResult()).getNumber() == 0.0) {
            //eliminated unknown
            return getKnownPart();
        }
        unknownToOperateOn.setAmount(((MathNumberNode) subtraction.getResult()).getNumber());
        if (isSubtrahendUnknown) {
            setSubtrahend(unknownToOperateOn);
        } else {
            setMinuend(unknownToOperateOn);
        }
        return this;
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        if (getMinuend() == null) {
            setMinuend(counterNumberNode);
        } else {
            setSubtrahend(counterNumberNode);
        }
    }

    public void setKnownPart(MathNode newKnownPart) {
        if (isKnownNumber(getMinuend())) {
            setMinuend(newKnownPart);
        } else {
            setSubtrahend(newKnownPart);
        }
    }

    public void setUnknownPart(MathNode unknownPart) {
        if (isKnownNumber(getMinuend()) && !isKnownNumber(getSubtrahend())) {
            setSubtrahend(unknownPart);
        } else if (isKnownNumber(getSubtrahend()) && !isKnownNumber(getMinuend())) {
            setMinuend(unknownPart);
        }
    }

    public MathNode getKnownPartCorrected() {
        if (isKnownNumber(getSubtrahend())) {
            MathNumberNode subtrahend = (MathNumberNode) getSubtrahend();
            subtrahend.invertNumber();
            return subtrahend;
        } else if (isKnownNumber(getMinuend())) {
            return getMinuend();
        }
        return null;
    }
}

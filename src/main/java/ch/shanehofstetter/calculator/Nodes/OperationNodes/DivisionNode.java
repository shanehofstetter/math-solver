package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;


public class DivisionNode extends OperationNode {
    private MathNode dividend;
    private MathNode divisor;
    public DivisionNode(Calculator parent) {
        super(parent);
        setName("/");
    }

    public void setNumbers(MathNode first, MathNode second) {
        setDividend(first);
        setDivisor(second);
    }

    @Override
    public void doOperation() {
        if (getDividend() instanceof OperationNode) {
            if (((OperationNode) getDividend()).getResult() == null) {
                setResult(null);
            } else {
                setDividend(((OperationNode) getDividend()).getResult());
            }
        }
        if (getDivisor() instanceof OperationNode) {
            if (((OperationNode) getDivisor()).getResult() == null) {
                setResult(null);
            } else {
                setDivisor(((OperationNode) getDivisor()).getResult());
            }
        }
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(divide(((MathNumberNode) getDividend()).number, ((MathNumberNode) getDivisor()).number)));
        } else if (areBothNumbersUnknown()) {
            // 3x / 4x ==> 3/4
            double _dividend = ((UnknownNumberNode) getDividend()).getAmount();
            double _divisor = ((UnknownNumberNode) getDivisor()).getAmount();
            setResult(new MathNumberNode(divide(_dividend, _divisor)));
        } else if (isOneNumberUnknown()) {
            if (isKnownNumber(getDivisor()) && getDividend() instanceof UnknownNumberNode) {
                //e.g. 4x / 2
                // == 2x
                double unknownAmount = ((UnknownNumberNode) getDividend()).getAmount();
                UnknownNumberNode result = new UnknownNumberNode(((UnknownNumberNode) getDividend()).getUnknownName());
                result.setAmount(divide(unknownAmount, ((MathNumberNode) getDivisor()).getNumber()));
                setResult(result);
            } else {
                setResult(null);
            }
        } else {
            setResult(null);
        }

    }

    /**
     * divide two double numbers and return result
     *
     * @param dividend the number which gets divided
     * @param divisor  divide the divident with this number
     * @return result
     */
    private double divide(double dividend, double divisor) {
        double result = dividend / divisor;
        parent.showOutput(dividend + " / " + divisor + " = " + result);

        return result;
    }

    public OperationNode getCounterOperation() {
        /*
            Divide
            x / 2 = 4
            x = 4 * 2

            2 / x = 4
            x = 2 / 4
             */
        OperationNode counterNode = null;

        if (isKnownNumber(getDividend())) {
            counterNode = new DivisionNode(parent);
            ((DivisionNode) counterNode).setDividend(getDividend());
        } else if (isKnownNumber(getDivisor())) {
            counterNode = new MultiplicationNode(parent);
            ((MultiplicationNode) counterNode).setFirstFactor(getDivisor());
        }
        return counterNode;
    }

    @Override
    public MathNode performOperation(OperationNode operationToPerform) throws Exception {
        if (OperationNode.isSecondaryOperation(operationToPerform)) {
            MathNode unknownPart = operationToPerform.getUnknownPart();
            if (unknownPart != null) {
                if (unknownPart instanceof DivisionNode) {
                    if (getDivisor() instanceof UnknownNumberNode && ((DivisionNode) unknownPart).getDivisor() instanceof UnknownNumberNode) {
                        //e.g. 6/x + 7/x
                        UnknownNumberNode myUnknown = (UnknownNumberNode) getDivisor();
                        UnknownNumberNode counterUnknown = (UnknownNumberNode) ((DivisionNode) unknownPart).getDivisor();
                        if (myUnknown.getUnknownName() == counterUnknown.getUnknownName()) {
                            if (myUnknown.getAmount() != counterUnknown.getAmount()) {
                                //e.g. 6/x + 7/2x
                                // == 6/x + 3.5/x
                                DivisionNode dividend = new DivisionNode(parent);
                                dividend.setDividend(getDividend());
                                dividend.setDivisor(new MathNumberNode(myUnknown.getAmount()));
                                if (dividend.getResult() != null) {
                                    setDividend(dividend.getResult());
                                } else {
                                    setDividend(dividend);
                                }
                                myUnknown.setAmount(1.0);

                                DivisionNode counterDividend = new DivisionNode(parent);
                                counterDividend.setDividend(((DivisionNode) unknownPart).getDividend());
                                counterDividend.setDivisor(new MathNumberNode(counterUnknown.getAmount()));
                                if (counterDividend.getResult() != null) {
                                    ((DivisionNode) unknownPart).setDividend(counterDividend.getResult());
                                } else {
                                    ((DivisionNode) unknownPart).setDividend(counterDividend);
                                }
                                counterUnknown.setAmount(1.0);
                            }
                            OperationNode newDividend;
                            if (operationToPerform instanceof SumNode) {
                                newDividend = new SumNode(parent);
                                ((SumNode) newDividend).setFirstSummand(getDividend());
                                ((SumNode) newDividend).setSecondSummand(((DivisionNode) unknownPart).getDividend());
                            } else if (operationToPerform instanceof SubtractNode) {
                                newDividend = new SubtractNode(parent);
                                ((SubtractNode) newDividend).setMinuend(getDividend());
                                ((SubtractNode) newDividend).setSubtrahend(((DivisionNode) unknownPart).getDividend());
                            } else {
                                return this;
                            }
                            DivisionNode newDivision = new DivisionNode(parent);
                            if (newDividend.getResult() == null) {
                                newDivision.setDividend(newDividend);
                            } else {
                                newDivision.setDividend(newDividend.getResult());
                            }
                            newDivision.setDivisor(myUnknown);
                            return newDivision;
                        }
                    }
                }
            }
        }
        return this;
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        if (getDivisor() == null) {
            setDivisor(counterNumberNode);
        } else {
            setDividend(counterNumberNode);
        }
    }

    public MathNode getDividend() {
        return dividend;
    }

    public void setDividend(MathNode node) {
        if (dividend != null) {
            children.remove(dividend);
        }
        dividend = node;
        children.add(node);
    }

    public MathNode getDivisor() {
        return divisor;
    }

    public void setDivisor(MathNode node) {
        if (divisor != null) {
            children.remove(divisor);
        }
        divisor = node;
        children.add(node);
    }
}

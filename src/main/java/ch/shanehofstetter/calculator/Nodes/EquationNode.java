package ch.shanehofstetter.calculator.Nodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.NodeNumberNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.UnknownNumberNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.DivisionNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.MultiplicationNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.OperationNode;
import ch.shanehofstetter.calculator.Nodes.OperationNodes.SubtractNode;
import ch.shanehofstetter.calculator.Operators;

public class EquationNode extends MathNode {

    Calculator parent;
    private UnknownSide side;
    private Double result;
    private MathNode leftSideOfEquation;
    private MathNode rightSideOfEquation;
    private int checkSidesCounter = 0;
    public EquationNode(Calculator parent) {
        setName(Character.toString(Operators.EQUALITY));
        this.parent = parent;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
        parent.showResult(leftSideOfEquation.getName() + " = " + result);
    }

    public void solveEquation() throws Exception {
        System.out.println("\nsolving equation:");
        leftSideOfEquation = simplifySide(leftSideOfEquation, UnknownSide.LEFT);
        rightSideOfEquation = simplifySide(rightSideOfEquation, UnknownSide.RIGHT);
        checkSides();
    }

    private void checkSides() throws Exception {
        if ((side == UnknownSide.RIGHT && rightSideOfEquation instanceof UnknownNumberNode) || MathNumberNode.isNumber(leftSideOfEquation)) {
            switchSides();
        }
        if (leftSideOfEquation instanceof UnknownNumberNode && rightSideOfEquation instanceof MathNumberNode) {
            //already solved (simple equation)
            checkUnknownAmount();
            setResult(((MathNumberNode) rightSideOfEquation).getNumber());
            return;
        }
        if (leftSideOfEquation instanceof UnknownNumberNode && rightSideOfEquation instanceof OperationNode) {
            //unknown variable occurs on both sides..
            while (rightSideOfEquation instanceof OperationNode) {
                if (OperationNode.isSecondaryOperation((OperationNode) rightSideOfEquation)) {
                    //e.g. 2x = 3 + x
                    if (((OperationNode) rightSideOfEquation).getUnknownPart() instanceof OperationNode) {
                        rightSideOfEquation = simplifySide(rightSideOfEquation, UnknownSide.RIGHT);
                        checkSides();
                        return;
                    }
                    double _amountRight = ((UnknownNumberNode) ((OperationNode) rightSideOfEquation).getUnknownPart()).getAmount();
                    double _amountLeft = ((UnknownNumberNode) leftSideOfEquation).getAmount();
                    //subtract amountRight from both sides
                    SubtractNode subtractNode = new SubtractNode(parent);
                    subtractNode.setNumbers(leftSideOfEquation, ((OperationNode) rightSideOfEquation).getUnknownPart());
                    leftSideOfEquation = subtractNode.getResult();
                    rightSideOfEquation = ((OperationNode) rightSideOfEquation).getKnownPartCorrected();
                    boolean invert = false;
                    if (_amountRight < _amountLeft) {
                        //ok
                    } else if (_amountLeft < _amountRight) {
                        // x = 2x + 6
                        // -x = + 6
                        invert = true;
                    } else {
                        //x = x + 3 ... not true
                        throw new Exception("cannot solve equation - err1: same amount of unknown on both sides!");
                    }
                    if (invert) {
                        invertBothSidesOfEquation();
                    }
                } else if (rightSideOfEquation instanceof SubtractNode) {
                    //x = 2x - 3
                    throw new Exception("not implemented (err2)");
                } else {
                    throw new Exception("not implemented (err3)");
                }
            }
            if (OperationNode.isKnownNumber(rightSideOfEquation)) {
                //solved
                checkUnknownAmount();
                setResult(((MathNumberNode) rightSideOfEquation).getNumber());
                return;
            }

        }
        if (leftSideOfEquation instanceof OperationNode && rightSideOfEquation instanceof MathNumberNode) {
            // case could be: 2*x = 4
            // we need to bring the operations from left to right in top-down order (and invert the operation)
            while (leftSideOfEquation instanceof OperationNode) {
//                System.out.println("getting counter operation from left side");
                OperationNode rightStepOperation = ((OperationNode) leftSideOfEquation).getCounterOperation();
                rightStepOperation.setMissingNumberNode((MathNumberNode) rightSideOfEquation);
//                System.out.println("doing right step operation: "+rightStepOperation.getName());
                rightStepOperation.doOperation();
                if (rightStepOperation.getResult() == null) {
//                    System.out.println("result was null");
//                    throw new Exception("rightStepOperation Result is null");
                    rightSideOfEquation = rightStepOperation;
                    leftSideOfEquation = ((OperationNode) leftSideOfEquation).getUnknownPart();
                    switchSides();
                    checkSides();
                    return;
                } else {
                    rightSideOfEquation = rightStepOperation.getResult();
                }
                leftSideOfEquation = ((OperationNode) leftSideOfEquation).getUnknownPart();
            }
            if (leftSideOfEquation instanceof UnknownNumberNode) {
                //solved
                checkUnknownAmount();

                setResult(((MathNumberNode) rightSideOfEquation).getNumber());
                return;
            }
        }
        if (leftSideOfEquation instanceof OperationNode && rightSideOfEquation instanceof OperationNode) {
            // if both sides are operationNodes, maybe unknown var is on both sides, we need to
            // move it to same side and then solve like normal
            System.out.println("both sides are operations");
            while (leftSideOfEquation instanceof OperationNode && rightSideOfEquation instanceof OperationNode) {
                if (OperationNode.isSecondaryOperation((OperationNode) leftSideOfEquation) && OperationNode.isSecondaryOperation((OperationNode) rightSideOfEquation)) {
                    System.out.println("both of them are an addition or subtraction");
                    performCounterOperationOnRightSide();
                } else if (OperationNode.isSecondaryOperation((OperationNode) leftSideOfEquation) || OperationNode.isSecondaryOperation((OperationNode) rightSideOfEquation)) {
                    System.out.println("secondary and primary operation");
                    if (OperationNode.isSecondaryOperation((OperationNode) leftSideOfEquation)) {
                        switchSides();
                    }
                    if (leftSideOfEquation instanceof DivisionNode) {
                        System.out.println("left is a divisionnode ..");
                        if (((DivisionNode) leftSideOfEquation).getDivisor() instanceof UnknownNumberNode) {
                            System.out.println("unknown is the divisor");
                            //e.g. 6/x = 1 - 7/x
                            // == 6/x + 7/x = 1
                            MathNode unknownRightPart = ((OperationNode) rightSideOfEquation).getUnknownPart();
                            if (unknownRightPart instanceof UnknownNumberNode) {
                                //e.g. 6/x = 1 - x
                                System.out.println("results in quadratic equation, cannot solve yet...");
                                throw new Exception("not implemented err11");
                            } else if (unknownRightPart instanceof DivisionNode) {
                                System.out.println("right unknown part is a division");
                                if (((DivisionNode) unknownRightPart).getDivisor() instanceof UnknownNumberNode) {
                                    //e.g. 6/x = 1 - 7/x

                                    performCounterOperationOnLeftSide();
                                } else {
                                    //e.g. 6/x = 1 - x/7
                                    System.err.println("results in quadratic equation, cannot solve yet..");
                                }
                            }
                        } else {
//                            System.out.println("unknown is the dividend");
                            performCounterOperationOnRightSide();
                        }

                    }
                } else {
                    throw new Exception("not implemented err10");
                }
            }
            if (checkSidesCounter < 1000) {
                checkSidesCounter++;
                checkSides();
                return;
            }

            //example:
            /*
            ---------------------------
            ---------------------------
            ---------------------------
            (x-3)*x  = 24
            x^2 - 3x = 24
            ..quadratic with >1 occurrences of x, bring in pq- or abc-form:
            x^2 - 3x - 24 = 0
            ...solve with pq-formula or abc-formula
            ---------------------------
            (x-3)/(x+2) = 24
            x - 3 = 24 * ( x + 2 )
            x - 3 = 24x + 48
            x = 24x + 51
            24x + 51 = x
            24x = x - 51
            23x = -51
            x = -51/23
            ---------------------------
            ---------------------------
            ABC-Formula

            ax^2 + bx + c = 0
            x1 = (-b + sqrt(b^2 - 4ac)) / 2a
            x2 = (-b - sqrt(b^2 - 4ac)) / 2a

            Input Form:
            └── =
                ├── +
                │   ├── +
                │   │   ├── ^
                │   │   │   ├── ax
                │   │   │   └── 2.0
                │   │   └── bx
                │   └── c
                └── 0.0

             */
        }
        throw new Exception("could not solve equation");
    }

    private void performCounterOperationOnRightSide() throws Exception {
        System.out.println("performing counter op on right side");
        OperationNode counterOperation = ((OperationNode) leftSideOfEquation).getCounterOperation(true);
        boolean unknownCounter = counterOperation.getUnknownPart() != null;
        rightSideOfEquation = ((OperationNode) rightSideOfEquation).performOperation(counterOperation);
        if (!unknownCounter) {
            leftSideOfEquation = ((OperationNode) leftSideOfEquation).getUnknownPart();
        } else {
            leftSideOfEquation = ((OperationNode) leftSideOfEquation).getKnownPart();
        }
    }

    private void performCounterOperationOnLeftSide() throws Exception {
        System.out.println("performing counter op on left side");
        OperationNode counter = ((OperationNode) rightSideOfEquation).getCounterOperation(true);
        leftSideOfEquation = ((DivisionNode) leftSideOfEquation).performOperation(counter);
        boolean unknownCounter = counter.getUnknownPart() != null;
        if (!unknownCounter) {
            rightSideOfEquation = ((OperationNode) rightSideOfEquation).getUnknownPart();
        } else {
            rightSideOfEquation = ((OperationNode) rightSideOfEquation).getKnownPart();
        }
    }

    private void invertBothSidesOfEquation() {
        MultiplicationNode leftInversion = new MultiplicationNode(parent);
        leftInversion.setNumbers(leftSideOfEquation, new MathNumberNode(-1.0));
        leftSideOfEquation = leftInversion.getResult();
        MultiplicationNode rightInversion = new MultiplicationNode(parent);
        rightInversion.setNumbers(rightSideOfEquation, new MathNumberNode(-1.0));
        rightSideOfEquation = rightInversion.getResult();
    }

    private void checkUnknownAmount() {
        if (((UnknownNumberNode) leftSideOfEquation).getAmount() != 1.0) {
            DivisionNode divisionNode = new DivisionNode(parent);
            divisionNode.setDividend(rightSideOfEquation);
            divisionNode.setDivisor(new MathNumberNode(((UnknownNumberNode) leftSideOfEquation).getAmount()));
            rightSideOfEquation = divisionNode.getResult();
            ((UnknownNumberNode) leftSideOfEquation).setAmount(1.0);
        }
    }

    private void switchSides() {
        MathNode originalRight = getRightSideOfEquation();
        MathNode originalLeft = getLeftSideOfEquation();
        setLeftSideOfEquation(originalRight);
        setRightSideOfEquation(originalLeft);
    }

    private MathNode simplifySide(MathNode sideNode, UnknownSide side) throws Exception {
        if (sideNode != null) {
            if (sideNode instanceof UnknownNumberNode) {
                //cannot calculate, equation typed in like this: x = ... or ... = x
                //means the equation is solved, result = rightSide
                this.side = side;
            } else {
                if (sideNode instanceof OperationNode) {
                    //maybe unknown-var is in this tree
                    ((OperationNode) sideNode).doOperation();
                    //if after doOperation, we do not have a result, unknown must be in this tree

                    if (((OperationNode) sideNode).getResult() == null) {
                        this.side = side;
                    } else {
                        //we have a result for this side, means unknown is on other side
                        sideNode = ((OperationNode) sideNode).getResult();
                    }
                } else if (sideNode instanceof MathNumberNode) {
                    //side is solved, unknown must be on other side
                    return sideNode;
                }
            }
        }
        return sideNode;
    }

    public MathNode getLeftSideOfEquation() {
        return leftSideOfEquation;
    }

    public void setLeftSideOfEquation(MathNode leftSideOfEquation) {
        if (leftSideOfEquation instanceof NodeNumberNode) {
            this.leftSideOfEquation = ((NodeNumberNode) leftSideOfEquation).getNode();
        } else {
            this.leftSideOfEquation = leftSideOfEquation;
        }
        children.add(this.leftSideOfEquation);
    }

    public MathNode getRightSideOfEquation() {
        return rightSideOfEquation;
    }

    public void setRightSideOfEquation(MathNode rightSideOfEquation) {
        if (rightSideOfEquation instanceof NodeNumberNode) {
            this.rightSideOfEquation = ((NodeNumberNode) rightSideOfEquation).getNode();
        } else {
            this.rightSideOfEquation = rightSideOfEquation;
        }
        children.add(this.rightSideOfEquation);
    }

    private enum UnknownSide {
        LEFT,
        RIGHT
    }
}

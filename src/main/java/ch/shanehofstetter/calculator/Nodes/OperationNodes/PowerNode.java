package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;


public class PowerNode extends OperationNode {
    private MathNode factor;
    private MathNode power;
    public PowerNode(Calculator parent) {
        super(parent);
        setName("^");
    }

    public void setNumbers(MathNode first, MathNode second) {
        setFactor(first);
        setPower(second);
    }

    @Override
    public void doOperation() {
        if (getFactor() instanceof OperationNode) {
            if (((OperationNode) getFactor()).getResult() == null) {
                setResult(null);
            } else {
                setFactor(((OperationNode) getFactor()).getResult());
            }
        }
        if (getPower() instanceof OperationNode) {
            if (((OperationNode) getPower()).getResult() == null) {
                setResult(null);
            } else {
                setPower(((OperationNode) getPower()).getResult());
            }
        }
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(power(((MathNumberNode) getFactor()).number, ((MathNumberNode) getPower()).number)));
        } else {
            setResult(null);
        }
    }

    private double power(double i, double j) {
        double result = Math.pow(i, j);
        parent.showOutput(i + " ^ " + j + " = " + result);
        return result;
    }

    public MathNode getFactor() {
        return factor;
    }

    public void setFactor(MathNode factor) {
        if (this.factor != null) {
            children.remove(this.factor);
        }
        children.add(factor);
        this.factor = factor;
    }

    public MathNode getPower() {
        return power;
    }

    public void setPower(MathNode power) {
        if (this.power != null) {
            children.remove(this.power);
        }
        children.add(power);
        this.power = power;
    }

    public OperationNode getCounterOperation() {
        /*
        powerNode mit Kehrwert der Potenz zurückgeben:
        aus ^1/2 wird ^2;  aus ^2 wird ^1/2
        return powerNode with power = 1/power
         */
        if (isKnownNumber(getPower())) {
            // x ^2 = 4
            PowerNode powerNode = new PowerNode(parent);
            powerNode.setPower(new MathNumberNode(1.0 / ((MathNumberNode) getPower()).getNumber()));
            return powerNode;
        } else if (isKnownNumber(getFactor())) {
            // 2 ^ x = 4
            //=> log 4 of base 2

            /*
            log(4) base 10 / log(2) base 10
             */
            LogarithmNode logarithmNode = new LogarithmNode(parent);
            logarithmNode.setBase(getFactor());
            return logarithmNode;
        }
        return null;
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        if (getFactor() == null) {
            setFactor(counterNumberNode);
        } else {
            //not supported yet
        }
    }
}

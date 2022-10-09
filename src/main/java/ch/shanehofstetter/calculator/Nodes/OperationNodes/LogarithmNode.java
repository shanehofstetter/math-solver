package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;

/**
 * Created by Shane on 12.07.2015.
 */
public class LogarithmNode extends OperationNode {
    private MathNode base;
    private MathNode logarithm;

    public LogarithmNode(Calculator parent) {
        super(parent);
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        //needs to be logarithm
        setLogarithm(counterNumberNode);
    }

    @Override
    public void doOperation() {
        if (areBothNumberNodes() && !isOneNumberUnknown()) {
            setResult(new MathNumberNode(calculateLog(((MathNumberNode) getBase()).number, ((MathNumberNode) getLogarithm()).number)));
        } else {
            setResult(null);
        }
    }

    protected Double calculateLog(Double base, Double log) {
        Double result = Math.log10(log) / Math.log10(base);
        System.out.println("log " + log + " of base " + base + " = " + result);
        parent.showOutput("log " + log + " of base " + base + " = " + result);
        return result;
    }

    public MathNode getBase() {
        return base;
    }

    public void setBase(MathNode base) {
        children.add(base);
        this.base = base;
    }

    public MathNode getLogarithm() {
        return logarithm;
    }

    public void setLogarithm(MathNode logarithm) {
        children.add(base);
        this.logarithm = logarithm;
    }
}

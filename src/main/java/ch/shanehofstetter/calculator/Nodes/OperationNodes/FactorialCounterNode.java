package ch.shanehofstetter.calculator.Nodes.OperationNodes;


import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.MathNode;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;

public class FactorialCounterNode extends OperationNode {
    private MathNode childNode;

    public FactorialCounterNode(Calculator parent) {
        super(parent);
        setName("!**");
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
        if (getChildNode() instanceof MathNumberNode) {
            setResult(new MathNumberNode(deFactorize(((MathNumberNode) getChildNode()).number)));
        } else {
            setResult(null);
        }
    }

    private Double deFactorize(Double number) {
        //very limited function..
        FactorialNode factNode = new FactorialNode(parent);
        for (int i = 1; i < 25; i++) {
            Double factorial = factNode.factorize((double) i);
            if (Double.compare(factorial, number) == 0) {
                return (double) i;
            } else if (factorial > number) {
                break;
            }
        }
        return Double.NaN;
    }

    public MathNode getChildNode() {
        return childNode;
    }

    public void setChildNode(MathNode childNode) {
        this.childNode = childNode;
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        setChildNode(counterNumberNode);
    }
}

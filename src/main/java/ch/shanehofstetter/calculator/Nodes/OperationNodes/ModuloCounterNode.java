package ch.shanehofstetter.calculator.Nodes.OperationNodes;

import ch.shanehofstetter.calculator.Calculator;
import ch.shanehofstetter.calculator.Nodes.NumberNodes.MathNumberNode;

public class ModuloCounterNode extends ModuloNode {

    private Double neededModuloResult;

    public ModuloCounterNode(Calculator parent) {
        super(parent);
    }

    public void setMissingNumberNode(MathNumberNode counterNumberNode) {
        neededModuloResult = counterNumberNode.getNumber();
    }

    @Override
    public void doOperation() {
        if (getFirstNumber() == null) {
            // e.g. =>  x % 3 = 1
            setResult(new MathNumberNode(findModulo(null, ((MathNumberNode) getSecondNumber()).getNumber())));
        } else {
            //e.g. =>  4 % x = 1
            setResult(new MathNumberNode(findModulo(((MathNumberNode) getFirstNumber()).getNumber(), null)));
        }

    }

    private Double findModulo(Double first, Double second) {
        for (int i = 1; i < 1000; i++) {
            Double mod;
            if (first == null) {
                mod = modulo(i, second);
            } else {
                mod = modulo(first, i);
            }
            if (Double.compare(mod, neededModuloResult) == 0) {
                return mod;
            }
        }
        return Double.NaN;
    }
}

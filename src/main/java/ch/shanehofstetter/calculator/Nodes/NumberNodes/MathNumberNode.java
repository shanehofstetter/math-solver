package ch.shanehofstetter.calculator.Nodes.NumberNodes;

import ch.shanehofstetter.calculator.Nodes.MathNode;


public class MathNumberNode extends MathNode {

    public Double number;

    public MathNumberNode(Double num) {
        number = num;
        setName(number.toString());
    }

    public static boolean isNumber(MathNode numberNode) {
        if (numberNode instanceof MathNumberNode) {
            return !(numberNode instanceof UnknownNumberNode) && !(numberNode instanceof NodeNumberNode);
        }
        return false;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public void invertNumber() {
        setNumber(getNumber() * -1.0);
    }
}

package ch.shanehofstetter.calculator.Nodes.NumberNodes;

import ch.shanehofstetter.calculator.Nodes.MathNode;


public class NodeNumberNode extends MathNumberNode {
    public MathNode node;

    public NodeNumberNode() {
        super(Double.NaN);
    }

    public MathNode getNode() {
        return node;
    }

    public void setNode(MathNode node) {
        this.node = node;
    }


}

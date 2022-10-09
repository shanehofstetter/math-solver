package ch.shanehofstetter.calculator.Nodes.NumberNodes;


public class UnknownNumberNode extends MathNumberNode {
    private char unknownName;
    private double amount;

    public UnknownNumberNode(char unknownVarName) {
        super(Double.NaN);
        setUnknownName(unknownVarName);
        setName(Character.toString(unknownVarName));
        setAmount(1.0);
    }

    public UnknownNumberNode(char unknownVarName, double amount) {
        super(Double.NaN);
        setUnknownName(unknownVarName);
        setName(Character.toString(unknownVarName));
        setAmount(amount);
    }

    public char getUnknownName() {
        return unknownName;
    }

    public void setUnknownName(char unknownName) {
        this.unknownName = unknownName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
        if (amount == 1.0) {
            setName(Character.toString(unknownName));
        } else {
            if (amount % 1.0 > 0) {
                setName(amount.toString() + unknownName);
            } else {
                setName(amount.intValue() + "" + unknownName);
            }

        }
    }
}

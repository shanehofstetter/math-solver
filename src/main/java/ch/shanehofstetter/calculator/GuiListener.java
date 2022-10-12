package ch.shanehofstetter.calculator;

import ch.shanehofstetter.calculator.Nodes.MathNode;

public interface GuiListener {
    void showOutput(String output);

    void showTree(MathNode rootNode);

    void showResult(String result);

    void showError(String error, Exception exception);
}

package ch.shanehofstetter.calculator;

import ch.shanehofstetter.calculator.Nodes.MathNode;

public class StdoutListener implements GuiListener {
    @Override
    public void showOutput(String output) {
        System.out.println(output);
    }

    @Override
    public void showTree(MathNode rootNode) {
        System.out.println("\nmath term converted to tree:");
        rootNode.print();
    }

    @Override
    public void showResult(String result) {
        System.out.println("RESULT:\t" + result);
    }

    @Override
    public void showError(String error, Exception exception) {
        if (exception != null && !exception.getMessage().equals(error)) {
            System.out.println("Exception: " + exception.getMessage());
        }
        System.out.println(error);
    }
}

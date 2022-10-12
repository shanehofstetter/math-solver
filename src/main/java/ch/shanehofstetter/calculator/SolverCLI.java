package ch.shanehofstetter.calculator;

import java.util.Scanner;

public class SolverCLI {
    private final String exit = "exit";
    private Calculator calculator;

    public static void main(String[] args) {
        new SolverCLI().run();
    }

    public void run() {
        this.calculator = new Calculator();
        this.calculator.addListener(new StdoutListener());
        this.startWaitingForInput();
    }

    public void startWaitingForInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            printDefaultMessage();
            while (scanner.hasNextLine()) { // scanner blocks on this line
                String line = scanner.nextLine();
                if (line.equals(exit)) {
                    break;
                }
                this.calculator.solveStringTerm(line);
                printDefaultMessage();
            }
        }
    }

    private void printDefaultMessage() {
        System.out.println("-------------------------------------------");
        System.out.printf("Quit with '%s'%n", exit);
        System.out.println("Enter your maths term (example: 2 * 3 + 4):");
    }
}

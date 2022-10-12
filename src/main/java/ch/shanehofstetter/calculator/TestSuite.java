package ch.shanehofstetter.calculator;


public class TestSuite {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Calculator c = new Calculator();
        c.addListener(new StdoutListener());

        //Tests
        assert c.solveStringTerm("4 * 2 ^ ( 4 / 2 * 4 % 3 )") == 16.0;
        assert c.solveStringTerm("3+4*5 + 8^(1/3)") == 25.0;
        assert c.solveStringTerm("3.45+4.1235235*5.4523525+8^(1/3)") == 27.93290366403375;
        assert c.solveStringTerm("3 + (-2)^2") == 7.0;
        assert c.solveStringTerm("(3+4)*(3+6+7)") == 112.0;
        assert c.solveStringTerm("2*(1+(2+(3*5)-3)-2)") == 26.0;
        assert c.solveStringTerm("2*(1+(4!+(3+5*2-3)-3)-2)") == 60.0;
        assert c.solveStringTerm("2*(1+(4!+(3+5*2-3)-3)-2)") == 60.0;
        assert c.solveStringTerm("-6*4/(-3*2)+22%5*4^2-8") == 28.0;
        assert c.solveStringTerm("1 + ( 2 + ( 3 +( 4 + ( 5 + ( 4 *3) + 1))) + 2)") == 30.0;
        assert c.solveStringTerm("-6*4/(-3*2)+22%5*4^2-8+2*(1+(4!+(3+5*2-3)-3)-2)") == 88.0;
        assert c.solveStringTerm("(-3)*(-4)/(-2)*(-6)-7*8*9*(-10)/(14*18*(-20))+0/13") == 35.0;

        assert c.solveStringTerm("10E2") == 1000.0;
        assert c.solveStringTerm("10E2/4") == 250.0;

        //Equations
        assert c.solveStringTerm("2x = 4") == 2.0;
        assert c.solveStringTerm("2x * 2 = 4") == 1.0;
        assert c.solveStringTerm("3x / 4x") == 0.75;
        assert c.solveStringTerm("3x - x = 4") == 2.0;
        assert c.solveStringTerm("3x + x = 4") == 1.0;
        assert c.solveStringTerm("2x * x = 4") == 1.4142135623730951;
        assert c.solveStringTerm("2x = x + 3") == 3.0;
        assert c.solveStringTerm("x = 2x + 6") == -6.0;
        assert c.solveStringTerm("(2 + x) * 2 = x") == -4.0;
        assert c.solveStringTerm("4 + 2x = x + 7") == 3.0;
        assert c.solveStringTerm("3x + 4 = (x + 7)*2") == 10.0;
        assert c.solveStringTerm("3x + 4 = (x - 7)*2") == -18.0;
        assert c.solveStringTerm("3x - 4 = (x + 7)*2") == 18.0;
        assert c.solveStringTerm("3x - 4 = (x - 7)*2") == -10.0;
        assert c.solveStringTerm("3x - 4 = (x - 7)/2") == 0.2;
        assert c.solveStringTerm("2x + 4 = (x + 3) - 7") == -8.0;
        assert c.solveStringTerm("2 * (3 + 4x) + 2x = 56") == 5.0;
        assert c.solveStringTerm("2x * 4 - 2 = 3x - 3 + 3") == 0.4;
        assert c.solveStringTerm("6/2x = 1 - 7/x") == 10.0;
        assert c.solveStringTerm("6/x = 1 - 7/x") == 13.0;
        assert c.solveStringTerm("6/2x = 1 + 7/x") == -4.0;

        //quadratic .. solve with abc formula ---->>> 2 results!
        //assert c.solveStringTerm("1/x + 4 + 5/6 + 2x = 56") == 25.5637744062338;
        assert c.solveStringTerm("x + 4 + 5/6 + 2x = 56") == 17.055555555555554;

        //one occurrence of x
        assert c.solveStringTerm("2*8-12/3+4-x=3*4-1/2") == 4.5;
        assert c.solveStringTerm("2*8-12/(2-x)+4-2=3*4-1/2") == 0.15384615384615374;
        assert c.solveStringTerm("4 * 2 ^ ( 4 / x ) = 100") == 0.861353116146786;
        assert c.solveStringTerm("(x-10)^(1/2)-4=0") == 26.0;
    }
}

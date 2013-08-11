package jlc;

public class TestPow {
	public TestPow() {
		double e = 1f/3f;
		double x = .012;
		double r = Math.pow(x,e);
		System.out.println("r "+r+" e "+e);
	}
    public static void main(String[] args) {
		new TestPow();
    }
}


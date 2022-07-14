public class TestMutation {
    public String testingthisvalue = "thisisatest";
    public TestMutation(String name) {}

    // Two overloaded methods
    public boolean method1(int a, int b, int c, int d) {}
    public boolean method1(String a, String b, TestMutation testMutation) {}
    public boolean method1(boolean a, boolean b) {}

    // The method call
    public boolean method1(String a) {}

    public void test() {
        String a = "";
        method1(1,2,3,4);
        int b = 0;
        //boolean c = true;
        TestMutation testinga = new TestMutation("test");
    }
}
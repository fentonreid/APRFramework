public class Person {
    public int age = 10;

    public Person() {}
    public Person(String name) {}
    public Person(String name, int a) {}

    public int getAge() {
        return age;
    }
}

public class TestMutation {
    public boolean barMutation(int param) {
        int a = "";
        //Person test = new Person();
        method1(10);
    }

    public int method1(int x) {}
}
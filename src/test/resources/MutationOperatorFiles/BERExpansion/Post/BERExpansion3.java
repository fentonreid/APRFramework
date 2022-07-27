public class Person {
    int age = 22;

    public void main() {
        int tenYearsOlder = age + 10;

        if (age == 20 || tenYearsOlder <= age) {
            System.out.println("Over 20 years old!");
        }
    }
}
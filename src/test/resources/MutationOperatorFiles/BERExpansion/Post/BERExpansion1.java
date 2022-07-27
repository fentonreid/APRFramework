public class Person {
    int age = 22;

    public void main() {
        boolean isCitizen = true;

        if (age == 20 && !isCitizen) {
            System.out.println("Over 20 years old!");
        }
    }
}
public class Person {
    int age = 22;

    public boolean inTwenties() {
        if (age > 20 && age < 30) {
            return true;
        }

        return false;
    }
}
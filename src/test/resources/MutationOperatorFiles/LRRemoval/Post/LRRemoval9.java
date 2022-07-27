public class Person {
    int age = 20;

    public String getAgeAsString() {
        String ageString = String.valueOf(age);
    }

    public void main() {
        System.out.println(getAgeAsString());
    }
}
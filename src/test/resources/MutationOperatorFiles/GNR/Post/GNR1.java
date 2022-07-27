public class Person {

    public int agePlusTen(int age) {
        return age + 10;
    }

    public void getAge(int newAge) {
        int age = agePlusTen(newAge);

        if (age > 20 && age < 30) {
            System.out.println("In your 20's!");
        }
    }
}
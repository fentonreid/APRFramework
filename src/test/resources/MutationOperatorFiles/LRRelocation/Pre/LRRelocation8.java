public class Person {
    int age = 20;

    public void main() {

        int i = 1;
        do {
            System.out.println("You are not " + i + " year(s) old");
            i++;
        }
        while (i < age);
    }
}
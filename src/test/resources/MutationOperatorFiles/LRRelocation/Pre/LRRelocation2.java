public class Person {

    public String getName(String first, String last) {
        return first + " " + last;
    }

    public String getName(String first, String middle, String last) {
        return first + " " + middle + " " + last;
    }

    public void main() {

        System.out.println(getName("Fenton", "Reid"));
        System.out.println(getName("Fenton", "Saoirse", "Reid"));
    }
}
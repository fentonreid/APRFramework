public class Person {
    String name = "";

    public Person(String first, String last) {
        this.name = first + " " + last;
    }

    public Person(String first, String middle, String last) {
        this.name = first + " " + middle + " " + last;
    }

    public void main() {
        String first = "Fenton";
        String middle = "Saoirse";
        String last =  "Reid";

        Person person = new Person(first, last);
    }
}

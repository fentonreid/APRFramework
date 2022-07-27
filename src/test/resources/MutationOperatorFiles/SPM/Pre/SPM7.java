public class Person {
    String name = "";

    public Person(String first, String last) {
        this.name = first + " " + last;
    }

    public void main() {
        String first = "Fenton";
        String last =  "Reid";

        Person person = new Person(first, last);
    }
}
public class Person {
    String middleName = "Saoirse";

    public String getName() {
        return "Fenton Reid";
    }

    public String[] getName() {
        return new String[] { "Fenton", "Reid" };
    }

    public void main() {
        String name = getName();
    }
}

public class Person {

    public String getName() {
        return "Fenton Reid";
    }

    public String getName(String middleName) {
        return "Fenton " + middleName + " Reid";
    }

    public void main() {
        String name = getName();
    }
}
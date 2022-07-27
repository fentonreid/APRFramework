public class Person {
    String middleName = "Saoirse";

    public String getName(String first, String middle, String last) {
        return "Fenton" + middle + "Reid";
    }

    public void main() {
        String name = getName(middleName, middleName, middleName);
    }
}
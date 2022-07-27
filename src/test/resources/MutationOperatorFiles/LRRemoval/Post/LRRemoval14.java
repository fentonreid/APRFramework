public class Person {
    int age = 20;

    public String getAgeAsString() {
        String ageString = null;
        return ageString.toString();
    }

    public void main() {

        try {
            getAgeAsString();

        } finally {
            System.out.println("Error caught and action taken");
        }
    }
}

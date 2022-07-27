public class Person {
    int age = 20;

    public String getAgeAsString() {
        String ageString = null;
        return ageString.toString();
    }

    public void main() {

        try {
            throw new NullPointerException("Cannot cast properly");
            getAgeAsString();

        } catch (Exception ex) {
        }
    }
}
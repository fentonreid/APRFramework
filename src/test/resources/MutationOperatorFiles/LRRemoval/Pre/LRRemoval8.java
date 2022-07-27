public class Person {
    int age = 20;

    public String getAgeAsString() {
        String ageString = null;
        return ageString.toString();
    }

    public void main() {

        try {
            getAgeAsString();

        } catch (Exception ex) {
            throw new NullPointerException("Cannot cast properly");
        }
    }
}
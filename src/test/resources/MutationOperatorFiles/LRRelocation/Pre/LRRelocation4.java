public class Person {

    public String getAgeAsString() {
        String ageString = null;
        return ageString.toString();
    }

    public void main() {
        int age = 20;

        try {
            getAgeAsString();

        } catch (Exception ex) {
            throw new NullPointerException("Cannot cast properly");

        } finally {
            System.out.println("Error caught and action taken");
        }
    }
}

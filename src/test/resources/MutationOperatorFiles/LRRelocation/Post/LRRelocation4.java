public class Person {

    public String getAgeAsString() {
        String ageString = null;
        return ageString.toString();
    }

    public void main() {

        try {
            getAgeAsString();

        } catch (Exception ex) {
            throw new NullPointerException("Cannot cast properly");

        } finally {
            System.out.println("Error caught and action taken");
        }

        int age = 20;
    }
}

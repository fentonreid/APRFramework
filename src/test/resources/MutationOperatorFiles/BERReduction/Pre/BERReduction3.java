public class Person {
    int favouriteNumber = 4;

    public String favouriteNumber() {

        String message = favouriteNumber > 0 && favouriteNumber <= 6 ? "are you a dice" : "hmm, not a dice!";
        System.out.println(message);
    }
}
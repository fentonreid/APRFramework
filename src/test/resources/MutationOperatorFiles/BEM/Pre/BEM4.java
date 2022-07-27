public class Person {
    int favouriteNumber = 4;

    public String favouriteNumber() {
        String message = favouriteNumber > 0 && favouriteNumber <= 6 ? "Are you a dice?" : "Hmm, not a dice!";
        System.out.println(message);
    }
}
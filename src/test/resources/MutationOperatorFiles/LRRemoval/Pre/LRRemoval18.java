public class Person {
    String favouriteColour = "cyan";

    public void main() {
        switch (favouriteColour) {

            case "red":
                System.out.println("Anger");
                break;

            case "cyan":
                System.out.println("Tranquil");
                break;

            default:
                System.out.println("Could not determine your mood");
        }
    }
}
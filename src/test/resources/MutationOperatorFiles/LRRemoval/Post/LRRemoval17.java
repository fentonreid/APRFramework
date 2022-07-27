public class Person {
    String favouriteColour = "cyan";

    public void main() {

        switch (favouriteColour) {

            case "cyan":
                System.out.println("Tranquil");
                break;

            default:
                System.out.println("Could not determine your mood");
        }
    }
}
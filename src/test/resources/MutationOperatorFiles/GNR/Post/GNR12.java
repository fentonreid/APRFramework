public abstract class Colour {

    public static String getColour() {
        return "purple";
    }
}

public class Person {

    public void getColour() {
        String colour = Colour.getColour();
    }
}
public abstract class Colour {
    static String colour = "purple";
}

public class Person {

    public void getColour() {
        String colour = Colour.colour;
    }
}
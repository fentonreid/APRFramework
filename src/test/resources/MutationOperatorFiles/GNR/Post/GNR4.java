public class Car {
    String name = "ford";
    String reg = "BD51 SMR";
}

public class Person {

    public void main() {
        String carDetails = new Car().reg;
    }
}
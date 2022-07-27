public class Car {
    String name;

    public Car(String name) {
        this.name = name;
    }
}

public class Person {
    Car favouriteCar = new Car("ford");

    public void printName(String first, String last) {
        System.out.println(first + " " + last);
    }

    public void main() {
        Car bmw = favouriteCar;
    }
}
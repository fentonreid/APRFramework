public class Person {

    public String getName(String first, String last) {
        System.out.println("Get name without middle name");
        return first + " " + last;
    }

    public String getName(String first, String middle, String last) {
        System.out.println("Get name with middle name");
        return first + " " + middle + " " + last;
    }
}
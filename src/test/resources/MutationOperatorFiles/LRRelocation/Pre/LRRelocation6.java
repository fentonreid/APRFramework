public class Person {
    String[] hobbies = new String[] { "Skiing", "Gaming", "Swimming" };

    public void main() {
        int age = 20;

        for (String hobby : hobbies) {
            System.out.println("I like this hobby: " + hobby);
        }
    }
}
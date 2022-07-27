public class Person {
    String[] hobbies = new String[] { "Skiing", "Gaming", "Swimming" };

    public void main() {

        for (String hobby : hobbies) {
            System.out.println("I like this hobby: " + hobby);
        }

        int age = 20;
    }
}
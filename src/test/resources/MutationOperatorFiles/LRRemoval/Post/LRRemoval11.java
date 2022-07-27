public class Person {
    String[] hobbies = new String[] { "Skiing", "Gaming", "Swimming" };

    public void main() {

        for (String hobby : hobbies) {

            if (hobby.equals("Gaming")) {
                System.out.println("You found my favourite hobby: " + hobby);
            }

            System.out.println("I like this hobby: " + hobby);
        }
    }
}
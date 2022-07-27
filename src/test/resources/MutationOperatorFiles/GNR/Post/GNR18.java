public class Person {

    enum Emotion {
        ANGRY,
        HAPPY,
        SAD,
        CALM
    }

    Emotion emotion = Emotion.SAD;

    public void getEmotion() {
        System.out.println("Feeling " + emotion + " today");
    }
}
public enum Emotion {
    ANGRY,
    HAPPY,
    SAD,
    CALM
}

public class Person {
    Emotion emotion = Emotion.HAPPY;

    public void getEmotion() {
        System.out.println("Feeling " + emotion + " today");
    }
}
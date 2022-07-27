public enum Emotion {
    ANGRY,
    HAPPY,
    SAD,
    CALM
}

public class Person {
    Emotion emotion = Emotion.CALM;

    public void getEmotion() {
        System.out.println("Feeling " + emotion + " today");
    }
}
package Util.MutationHelperFiles;

enum Emotion {
    HAPPY,
    SAD,
    ANGRY
}

public class ResolveCollection {
    public int age = 22;

    public Emotion method1() {
        int newAge = age + 10;

        return Emotion.HAPPY;
    }
}

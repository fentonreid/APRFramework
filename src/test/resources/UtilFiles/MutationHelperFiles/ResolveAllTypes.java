package Util.MutationHelperFiles;

enum EmotionEnum {
    HAPPY,
    SAD,
    ANGRY
}

public class ResolveAllTypes {
    public int age = 22;

    public EmotionEnum method1() {
        int newAge = age + 10;

        return EmotionEnum.HAPPY;
    }
}

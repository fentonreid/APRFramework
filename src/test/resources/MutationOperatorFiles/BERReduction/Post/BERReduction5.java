public class Person {
    String pendingEmployment = "yes";
    boolean isEmployed = true;
    boolean isCitizen = true;

    public boolean canWork() {

        return !(isCitizen && (isEmployed));
    }
}
/**
 * Created by vyshakh.babji on 12/9/15.
 */
public class Platform {
    private static Platform ourInstance = new Platform();

    public static Platform getInstance() {
        return ourInstance;
    }

    private Platform() {
    }
}

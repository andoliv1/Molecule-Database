import io.javalin.Javalin;
import org.json.JSONObject;

public class webApp {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(7777);
    }
}

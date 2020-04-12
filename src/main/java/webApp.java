import io.javalin.Javalin;
import main.java.MoleculeAbstract;
import main.java.Operations;
import java.util.ArrayList;

public class webApp {
    public ArrayList<String> fileList = new ArrayList<>();
    public String fileName = "";

    public static void main(String[] args) {
        // Initialize DB and webApp
        Operations Ops = new Operations();
        webApp w = new webApp();

        // Initialize java web framework
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(7777);

        // reset file arraylist
        app.post("/reset", ctx -> {
            String s = ctx.body();
            if (s.equals("reset")) {
                w.fileList = new ArrayList<>();
            }
            ctx.status(201);
        });

        // add files to arraylist
        app.post("/add", ctx -> {
            w.fileName = ctx.body();
            w.fileList.add(w.fileName);
            System.out.println("Filename is " + w.fileName);
            ctx.status(201);
        });

        // add files to DB
        app.post("/addToDB", ctx -> {
            for (String fileName : w.fileList) {
                Ops.insert("molecules/"+ fileName);
            }
            System.out.println("add to db");
            ctx.status(201);
        });

        // check for isomorphism
        app.get("/isomorphism", ctx -> {
            System.out.println("Checking iso with fileName " + w.fileName);
            ArrayList<MoleculeAbstract> iso = Ops.find("molecules/" + w.fileName);
            String isoString = w.fileName + " is isomorphic with the following:<br>";
            System.out.println(isoString);
            for (MoleculeAbstract mol : iso)
                isoString += mol.moleculeName + "<br>";
            ctx.result(isoString);
        });
    }
}

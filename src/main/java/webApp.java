import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;
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
            ctx.status(201);
        });

        // add files to DB
        app.post("/addToDB", ctx -> {
            for (String fileName : w.fileList) {
                Ops.insert("molecules/"+ fileName);
            }
            ctx.status(201);
        });

        app.get( "/checkIfExists", ctx -> {
            MoleculeAbstract m = Ops.queryMolecule(w.fileName);
            if(m != null)
                ctx.result("!null");
            else
                ctx.result("null");
        });

        // check for isomorphism
        app.get("/isomorphism", ctx -> {
            if (w.fileName.length() != 0) {
                ArrayList<MoleculeAbstract> iso = Ops.find("molecules/" + w.fileName);
                String isoString = w.fileName + " is isomorphic with the following:<br>";
                for (MoleculeAbstract mol : iso)
                    isoString += mol.moleculeName + "<br>";
                ctx.result(isoString);
            }
            else{
                ctx.result("Please add molecule into DB first before searching");
            }
        });

        app.get("/isomorphismByName", ctx -> {
            if (w.fileName.length() != 0) {
                ArrayList<MoleculeAbstract> iso = Ops.findByName(w.fileName);
                String isoString = w.fileName + " is isomorphic with the following:<br>";
                for (MoleculeAbstract mol : iso)
                    isoString += mol.moleculeName + "<br>";
                ctx.result(isoString);
            }
            else{
                ctx.result("Please add molecule into DB first before searching");
            }
        });

        app.post( "/searchByName", ctx -> {
                        w.fileName = ctx.body();
                        ctx.status(201);
                    });

        app.post("/upload", ctx -> {
            try {
                ctx.uploadedFiles("files").forEach(uploadedFile -> {
                    FileUtil.streamToFile(uploadedFile.getContent(), "molecules/" + uploadedFile.getFilename());
                });
            } catch (NullPointerException n)
            {
                ctx.status(400);
            }
            ctx.status(201);
        });
    }
}

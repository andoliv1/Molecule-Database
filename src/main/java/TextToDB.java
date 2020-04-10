package main.java;

import java.io.File;
import java.sql.SQLException;

public class TextToDB {

    private H2DB MyH2DB;
    private File moleculesDir;
    private File[] listOfFiles;
    TextToDB(){
        MyH2DB = new H2DB();
        try {
            MyH2DB.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Folder
        moleculesDir = new File("molecules");
        listOfFiles = moleculesDir.listFiles();
        assert listOfFiles != null;
    }

    void addMolecules(){
        try {
            MyH2DB.insertMolecule(listOfFiles);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TextToDB T = new TextToDB();
        T.addMolecules();
    }
}

package test;

import main.java.H2DB;
import main.java.MoleculeDB;
import main.java.MoleculeText;
import main.java.searchDumb;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;

public class SmallSystemsTest {
    public static final String TABLE_LOGIN = "login";
    private FlatXmlDataSet loadedDataSet;
    public Connection jdbcConnection;

    H2DB h2;
    public IDatabaseConnection connection;
    public DatabaseConfig dbConfig;
    public SmallSystemsTest() {
        super();
        h2 = new H2DB();
    }

    @Test
    public void SystemsCheck() throws Exception{

        // Insert two molecules that are isomorphic
        h2.insertMolecule("carbon_dioxide.txt");
        h2.insertMolecule("carbon_dioxide2.txt");
//
//        // Query the two molecules
        MoleculeDB m1 = h2.queryMoleculeByName("carbon dioxide");
        MoleculeDB m2 = h2.queryMoleculeByName("second carbon dioxide");
//        MoleculeText m1 = new MoleculeText("carbon_dioxide.txt");
//        MoleculeText m2 = new MoleculeText("carbon_dioxide2.txt");

        System.out.println(m1.toString() + "\n" + m2.toString());

        System.out.println(Arrays.deepToString(m1.getAdjacencyMatrix()));
        System.out.println(Arrays.deepToString(m2.getAdjacencyMatrix()));

        boolean weakIso = searchDumb.isIsomorphicWithNumbers(m1, m2);
        assert (weakIso);

        boolean strongIso = searchDumb.verify_rigorous_isomorphism(m1, m2);
        assert(strongIso);
    }
}

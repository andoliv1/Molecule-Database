package test;

import jdk.internal.org.xml.sax.InputSource;
import org.dbunit.DatabaseTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;

@SuppressWarnings("deprecation")
public class SQLUnit extends DatabaseTestCase {
    public static final String TABLE_LOGIN = "login";
    private FlatXmlDataSet loadedDataSet;
    public Connection jdbcConnection;

    public IDatabaseConnection connection;
    public DatabaseConfig dbConfig;
    public SQLUnit() {
        super();
    }

    @Override
    protected IDatabaseConnection getConnection() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        jdbcConnection = DriverManager
                .getConnection("jdbc:mysql://localhost/moleculedb?serverTimezone=UTC",
                        "root",
                        "password");

        connection = new DatabaseConnection(jdbcConnection);
        dbConfig = connection.getConfig();

        return new DatabaseConnection(jdbcConnection);
    }


    @Override
    protected FlatXmlDataSet getDataSet() throws Exception{
        loadedDataSet =
                new FlatXmlDataSetBuilder().build(new FileInputStream("moleculedb.xml"));
        return loadedDataSet;
    }

    @Test
    public void testNumRowsMolecules() throws Exception{
        loadedDataSet = getDataSet();
        assertNotNull(loadedDataSet);
        int rowCount = loadedDataSet.getTable("molecules").getRowCount();
        assertEquals(6, rowCount);
        System.out.println("Passed");
    }

    @Test
    public void testNumRowsAtoms() throws Exception{
        loadedDataSet = getDataSet();
        assertNotNull(loadedDataSet);
        int rowCount = loadedDataSet.getTable("atoms").getRowCount();
        assertEquals(52, rowCount);
        System.out.println("Passed");
    }

    @Test
    public void testNumRowsEdges() throws Exception{
        loadedDataSet = getDataSet();
        assertNotNull(loadedDataSet);
        int rowCount = loadedDataSet.getTable("edges").getRowCount();
        assertEquals(50, rowCount);
        System.out.println("Passed");
    }
}
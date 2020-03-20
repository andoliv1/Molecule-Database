package test;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseExport {

    public static void main(String[] args) throws Exception
    {
        // database connection
        Class driverClass = Class.forName("com.mysql.cj.jdbc.Driver");
        Connection jdbcConnection = DriverManager
                                    .getConnection("jdbc:h2:~/moleculedb",
                                            "sa",
                                            "");
        DatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        DatabaseConfig dbConfig = connection.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

        // partial database export
        QueryDataSet moleculedb = new QueryDataSet(connection);
        moleculedb.addTable("molecules");
        moleculedb.addTable("atoms");
        moleculedb.addTable("edges");
        FlatXmlDataSet.write(moleculedb, new FileOutputStream("moleculedb.xml"));

    }
}

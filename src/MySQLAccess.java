import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MySQLAccess {
    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    MySQLAccess(){
        connect = null;
        statement = null;
        preparedStatement = null;
        resultSet = null;
    }

    /** Make connection to db.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void connect() throws ClassNotFoundException, SQLException {
        // Load the MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Setup the connection with  DB
        connect = DriverManager
                .getConnection("jdbc:mysql://localhost/moleculedb?serverTimezone=UTC",
                        "root",
                        "1nbra531");
        connect.setAutoCommit(false);

    }
    public void readDataBase() throws Exception {

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();


        // Result set get the result of the SQL query
        // This query statement will query all adjacency lists as well as show which atom is which vertex
        resultSet = statement
                .executeQuery("SELECT A1.mid AS mid, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2 FROM atoms A1 JOIN edges E ON (A1.mid=E.mid AND A1.vertex=E.vertex1) JOIN atoms A2 ON (A2.mid=E.mid AND A2.vertex=E.vertex2);");
        writeResultSet(resultSet);

    }


    /** Query the adjacency list from the 3 tables.
     *  First selects the atom table using mid
     *  Secondly Joins that new table onto the Edges table using mid
     *  Lastly Joins the newer table onto another Atoms table on the vertices
     *  Returns the adjacency list.
     *
     * @param mid mid of the molecule
     * @throws SQLException
     */
    public void queryAdjacencyList(int mid) throws SQLException {

        String sql = "SELECT A1.mid AS mid, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n"+
                " FROM\n"  +
                " (SELECT A.mid, A.atom, A.vertex\n" +
                " FROM atoms A WHERE A.mid = ?) A1 \n" +
                " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n" +
                " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid);";
        preparedStatement = connect
                .prepareStatement(sql);
        preparedStatement.setInt(1, mid);
        resultSet = preparedStatement.executeQuery();
        writeResultSet(resultSet);
        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());
        // Next step is how should we store this
        // What data structure?
    }

    /** Query the adjacency list from the 3 tables.
     *  First Joins the molecule table onto the atom table using mid and the molecule name
     *  Secondly Joins that new table onto the Edges table using mid
     *  Lastly Joins the newer table onto another Atoms table on the vertices
     *  Returns the adjacency list
     *
     * @param moleculeName name of the molecule
     * @throws SQLException
     */
    public void queryAdjacencyList(String moleculeName) throws SQLException {

        // This query will
        String sql = "SELECT AM.mid AS mid, AM.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n"+
                " FROM\n"  +
                " (SELECT A1.mid, M.name, A1.atom, A1.vertex\n" +
                " FROM molecules M JOIN atoms A1 ON M.mid=A1.mid WHERE M.name=?) AM\n" +
                " JOIN edges E ON (AM.vertex=E.vertex1 AND AM.mid=E.mid)\n" +
                " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid);";
        preparedStatement = connect
                .prepareStatement(sql);
        preparedStatement.setString(1, moleculeName);
        resultSet = preparedStatement.executeQuery();
        writeResultSet(resultSet);
        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());
        // Next step is how should we store this
        // What data structure?
    }

    /** Query the adjacency list from the 3 tables.
     *  First selects the atoms in the atom table using list of mids
     *  Secondly Joins that new table onto the Edges table using mid
     *  Lastly Joins the newer table onto another Atoms table on the vertices
     *  Returns all of the adjacency list
     *
     * @param mids Array of mids
     * @throws SQLException
     */
    public void queryAdjacencyList(ArrayList<Integer> mids) throws SQLException {

        // MySQL doesn't allow for using array
        // Work around for using array in the IN clause
        // Build string of ?,?, ...
        // Parametrized later
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < mids.size(); i++ ) {
            builder.append("?,");
        }

        String sql = "SELECT A1.mid AS mid, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n"+
                " FROM\n"  +
                " (SELECT A.mid, A.atom, A.vertex\n" +
                " FROM atoms A WHERE A.mid IN ("+  builder.deleteCharAt( builder.length() -1 ).toString()+  "))A1 \n" +
                " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n" +
                " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid);";
        preparedStatement = connect
                .prepareStatement(sql);
        int ii = 1;
        for(int mid : mids)
            preparedStatement.setInt(ii++, mid);
        resultSet = preparedStatement.executeQuery();
        writeResultSet(resultSet);
        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());
        // Next step is how should we store this
        // What data structure?
    }


    /** Finds mid's and names of molecules that have the same number of atoms.
     *
     * @param numAtoms - number of atoms in molecule
     * @return resultSet - SQL object that holds mid, name of molecule. Each row represents a molecule.
     * @throws SQLException
     */
    public ResultSet findSameNumAtoms(int numAtoms) throws SQLException {
        // This query will extract the mid's, molecule names of the molecules of the same number of atoms
        String sql = "SELECT mid, name\n"+
                " FROM molecules\n" +
                " WHERE num_atoms = ?";

        preparedStatement = connect
                .prepareStatement(sql);
        preparedStatement.setInt(1, numAtoms);
        resultSet = preparedStatement.executeQuery();
//        writeResultSet(resultSet);
        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());
        return resultSet;
    }

    /** Inserts molecule information into SQL via filename.
     *
     * @param filename
     * @throws SQLException
     */
    public void insertMolecule(String filename) throws SQLException {

        // Get molecule information from text file
        MoleculeText m = new MoleculeText(filename);

        // SQL
        // PreparedStatements can use variables and are more efficient
        preparedStatement = connect
                .prepareStatement("INSERT INTO molecules VALUES (default, ?, ?)");

        preparedStatement.setString(1, m.moleculeName);
        preparedStatement.setInt(2, m.numVertices);
        preparedStatement.executeUpdate();
        connect.commit();

        // Query the mid that was auto incremented in the last insert.
        preparedStatement = connect
                .prepareStatement("SELECT mid FROM molecules WHERE name = ?");
        preparedStatement.setString(1, m.moleculeName);
        resultSet = preparedStatement.executeQuery();

        resultSet.next();
        int mid = resultSet.getInt("mid");
        System.out.println(mid);

        for (int ii = 0; ii < m.numVertices; ii++) {
            // Atoms table
            preparedStatement = connect
                    .prepareStatement("INSERT INTO atoms VALUES (default, ?, ?, ?)");

            // mid, atom, vertex
            preparedStatement.setInt(1, mid);
            preparedStatement.setString(2, m.atoms[ii]);
            preparedStatement.setInt(3, ii);

            preparedStatement.executeUpdate();
            connect.commit();
        }

        for (int ii = 0; ii < m.numVertices; ii++){
            for (int vv: m.adjacencyList[ii]) {

                if (m.adjacencyList.length > 0) {
                    //Edges table
                    preparedStatement = connect
                            .prepareStatement("INSERT INTO edges VALUES (default, ?, ?, ?)");

                    // mid, vertex1, vertex2
                    preparedStatement.setInt(1, mid);
                    preparedStatement.setInt(2, ii);
                    preparedStatement.setInt(3, vv);

                    preparedStatement.executeUpdate();
                    connect.commit();
                }
            }
        }
    }

    /** Prints the results of the query along with their columns;
     * @param resultSet The query results are stored here.
     * @throws SQLException
     */
    private void writeResultSet(ResultSet resultSet) throws SQLException {
        int size=0;
        while (resultSet.next()) {
            size++;
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                String columnValue = resultSet.getString(i);
                System.out.println(rsmd.getColumnLabel(i) + " " + columnValue);
            }
            System.out.println("===============");
        }
        System.out.println("Number of rows in query: " + size);
    }

    /** Close the connections
     *
     */
    private void close() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }

        if (statement != null) {
            statement.close();
        }

        if (connect != null) {
            connect.close();
        }
    }

    public static void main(String[] args) throws Exception {
        MySQLAccess dao = new MySQLAccess();
        dao.connect();
//        dao.readDataBase();
        dao.queryAdjacencyList("acetylene");
//        dao.insertMolecule("butane.txt");
//        dao.insertMolecule("isobutane.txt");
        ResultSet rs = dao.findSameNumAtoms(14);
        ArrayList<Integer> mids = new ArrayList<>();
        while(rs.next()){
            mids.add(rs.getInt("mid"));
        }
        System.out.println(mids.size());
        dao.queryAdjacencyList(mids);

    }
}
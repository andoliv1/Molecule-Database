package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
                        "password");
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
    public ResultSet queryAdjacencyList(ArrayList<Integer> mids) throws SQLException {

        // MySQL doesn't allow for using array
        // Work around for using array in the IN clause
        // Build string of ?,?, ...
        // Parametrized later
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < mids.size(); i++ ) {
            builder.append("?,");
        }

        String sql = "SELECT A1.mid AS mid, M.name, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n"+
                " FROM\n"  +
                " (SELECT A.mid, A.atom, A.vertex\n" +
                " FROM atoms A WHERE A.mid IN ("+  builder.deleteCharAt( builder.length() -1 ).toString()+  "))A1 \n" +
                " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n" +
                " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid)\n" +
                " JOIN molecules M on A1.mid = m.mid;";
        preparedStatement = connect
                .prepareStatement(sql);
        int ii = 1;
        for(int mid : mids)
            preparedStatement.setInt(ii++, mid);
        resultSet = preparedStatement.executeQuery();
//        writeResultSet(resultSet);
        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());

        return resultSet;
    }


    /** Return Array of main.MoleculeDB's that have the same number of atoms and the same atoms.
     *
     * @param numAtoms - number of atoms in molecule
     * @param atoms - array list of atoms in molecule
     * @return resultSet - SQL object that holds mid, name of molecule. Each row represents a molecule.
     * @throws SQLException
     */
    public MoleculeDB[] findSameAtoms(int numAtoms, ArrayList<String> atoms) throws SQLException {
        // MySQL doesn't allow for using array
        // Work around for using array in the IN clause
        // Build string of ?,?, ...
        // Parametrized later
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < atoms.size(); i++ ) {
            builder.append("?,");
        }

        // This query will extract the mid's of the molecules
        // of the same number of atoms and of the same type of atoms
        String sql = "SELECT mid, name \n" +
                "FROM molecules \n" +
                "WHERE mid IN " +
                "(SELECT mid\n"+
                " FROM atoms\n" +
                " WHERE atom in (" +
                builder.deleteCharAt( builder.length() -1 ).toString() +
                ") " +
                "GROUP BY mid HAVING count(mid) = ?);";

        preparedStatement = connect
                .prepareStatement(sql);
        int ii = 1;
        for(String atom : atoms)
            preparedStatement.setString(ii++, atom);
        preparedStatement.setInt(ii, numAtoms);
        resultSet = preparedStatement.executeQuery();
        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());

        // mapMolecule is a dictionary that takes the  <key, value> = <mid, main.MoleculeDB>
        HashMap<Integer, MoleculeDB> mapMolecule = new HashMap<>();
        ArrayList<Integer> mids = new ArrayList<>();
        int mid;
        String name;
        // Iterate over each row from the SQL query
        // Instantiate a main.MoleculeDB
        // Their adjacency lists will be filled from the next query.
        while(resultSet.next()){
            mid = resultSet.getInt("mid");
            name = resultSet.getString("name");
            mapMolecule.put(mid, new MoleculeDB(mid, name, numAtoms));
            mids.add(mid);
        }

        // Use the mids that we found earlier.
        // Run queryAdjacencyList to grab all of the adjacency lists for those mids.
        resultSet = queryAdjacencyList(mids);

        // Populate the atoms, adjacency lists and matrices
        MoleculeDB molecule;
        int vertex1, vertex2;
        String atom1, atom2;
        while(resultSet.next()){
            mid = resultSet.getInt("mid");
            vertex1 = resultSet.getInt("vertex1");
            vertex2 = resultSet.getInt("vertex2");
            atom1 = resultSet.getString("atom1");
            atom2 = resultSet.getString("atom1");

            molecule = mapMolecule.get(mid);
            molecule.setAtom(vertex1, atom1);
            molecule.setAtom(vertex2, atom2);
            molecule.setEdge(vertex1, vertex2);
        }

        return mapMolecule.values().toArray(new MoleculeDB[0]);
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

        preparedStatement.setString(1, m.getMoleculeName());
        preparedStatement.setInt(2, m.getNumVertices());
        preparedStatement.executeUpdate();
        connect.commit();

        // Query the mid that was auto incremented in the last insert.
        preparedStatement = connect
                .prepareStatement("SELECT mid FROM molecules WHERE name = ?");
        preparedStatement.setString(1, m.getMoleculeName());
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
            preparedStatement.setString(2, m.getAtomList().get(ii));
            preparedStatement.setInt(3, ii);

            preparedStatement.executeUpdate();
            connect.commit();
        }

        LinkedList<Integer>[] adjacencyList = m.getAdjacencyList();
        for (int ii = 0; ii < m.numVertices; ii++){
            for (int vv: adjacencyList[ii]) {
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
//        dao.queryAdjacencyList("acetylene");
//        dao.insertMolecule("1-aminopropan-2-ol");
//        dao.insertMolecule("isobutane.txt");

        // You can provide the whole list of atoms
        // or you can just provide the UNIQUE list of atoms
        ArrayList<String> atoms = new ArrayList<>();
        atoms.add("C");
        atoms.add("H");

        MoleculeDB[] molecules = dao.findSameAtoms(14, atoms);

        for (MoleculeDB m : molecules){
            System.out.println(m.getMoleculeName() + "\t" + m.getNumVertices());
//            m.getAdjacencyList();
            m.getAdjacencyMatrix();
        }

    }
}
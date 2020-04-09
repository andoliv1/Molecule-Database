package main.java;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class H2DB {
    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    H2DB(){
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
        Class.forName("org.h2.Driver");
        // Setup the connection with  DB
        connect = DriverManager
                .getConnection("jdbc:h2:~/moleculedb",
                        "sa",
                        "");
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
//        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());
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
//        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());
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
     * @return ResultSet - results of query
     */
    public ResultSet queryAdjacencyList(ArrayList<Integer> mids) throws SQLException {
        // THIS QUERY WAS MODIFIED TO BE USED WITH H2 DB
        // Specifically for the WITH _ IN clause
        // https://github.com/h2database/h2database/issues/149
        // Timmy

        String sql = "SELECT A1.mid AS mid, M.name, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n"+
                " FROM\n"  +
                " (SELECT A.mid, A.atom, A.vertex\n" +
                " FROM atoms A WHERE A.mid IN (SELECT * FROM TABLE(x INTEGER = ? ) )) A1 \n" +
                " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n" +
                " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid)\n" +
                " JOIN molecules M on A1.mid = m.mid;";
        preparedStatement = connect
                .prepareStatement(sql);
        preparedStatement.setObject(1, mids.toArray());
        resultSet = preparedStatement.executeQuery();
//        writeResultSet(resultSet);
//        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());

        return resultSet;
    }

    /** Return Array of main.java.MoleculeDB's that have the same number of atoms and the same atoms.
     *
     * @param numAtoms - number of atoms in molecule
     * @param atoms - array list of atoms in molecule
     * @return resultSet - SQL object that holds mid, name of molecule. Each row represents a molecule.
     * @throws SQLException
     */
    public MoleculeDB[] findSameNumberAtoms(int numAtoms, ArrayList<String> atoms) throws SQLException {
        long startTime = System.nanoTime();

        // THIS QUERY WAS MODIFIED TO BE USED WITH H2 DB
        // Specifically for the WITH _ IN clause
        // https://github.com/h2database/h2database/issues/149
        // Timmy

        // This query will extract the mid's of the molecules
        // of the same number of atoms and of the same type of atoms
        String sql =
                "SELECT mid, name, num_atoms \n" +
                        "FROM molecules \n" +
                        "WHERE num_atoms = ?;";

        preparedStatement = connect
                .prepareStatement(sql);
        preparedStatement.setInt(1, numAtoms);
        resultSet = preparedStatement.executeQuery();
//        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());

        // mapMolecule is a dictionary that takes the  <key, value> = <mid, main.java.MoleculeDB>
        HashMap<Integer, MoleculeDB> mapMolecule = new HashMap<>();
        ArrayList<Integer> mids = new ArrayList<>();
        int mid;
        String name;
        // Iterate over each row from the SQL query
        // Instantiate a main.java.MoleculeDB
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
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Insert Time: " + duration/1000000 + "ms");
        return mapMolecule.values().toArray(new MoleculeDB[0]);
    }
    /** Return Array of main.java.MoleculeDB's that have the same number of atoms and the same atoms.
     *
     * @param numAtoms - number of atoms in molecule
     * @param atoms - array list of atoms in molecule
     * @return resultSet - SQL object that holds mid, name of molecule. Each row represents a molecule.
     * @throws SQLException
     */
    public MoleculeDB[] findSameAtoms(int numAtoms, ArrayList<String> atoms) throws SQLException {
        long startTime = System.nanoTime();

        // THIS QUERY WAS MODIFIED TO BE USED WITH H2 DB
        // Specifically for the WITH _ IN clause
        // https://github.com/h2database/h2database/issues/149
        // Timmy

        // This query will extract the mid's of the molecules
        // of the same number of atoms and of the same type of atoms
        String sql =
                "SELECT mid, name, num_atoms \n" +
                "FROM molecules \n" +
                "WHERE mid IN " +
                " (SELECT A.mid \n" +
                " FROM atoms A\n" +
                " WHERE A.atom IN (SELECT * FROM TABLE(x VARCHAR = ? ))" +
                " GROUP BY A.mid \n" +
                " HAVING COUNT(A.mid) = ?) " +
                "AND num_atoms = ?;";

        preparedStatement = connect
                .prepareStatement(sql);
        preparedStatement.setObject(1, atoms.toArray());
        preparedStatement.setInt(2, numAtoms);
        preparedStatement.setInt(3, numAtoms);
        resultSet = preparedStatement.executeQuery();
//        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());

        // mapMolecule is a dictionary that takes the  <key, value> = <mid, main.java.MoleculeDB>
        HashMap<Integer, MoleculeDB> mapMolecule = new HashMap<>();
        ArrayList<Integer> mids = new ArrayList<>();
        int mid;
        String name;
        // Iterate over each row from the SQL query
        // Instantiate a main.java.MoleculeDB
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
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Insert Time: " + duration/1000000 + "ms");
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
                .prepareStatement("INSERT INTO molecules VALUES (default, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, m.getMoleculeName());
        preparedStatement.setInt(2, m.getNumVertices());
        preparedStatement.executeUpdate();
        connect.commit();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        int mid = -1;
        if (generatedKeys.next()) {
            mid = generatedKeys.getInt("mid");
        } else{
            connect.rollback();
            preparedStatement.close();
            connect.close();
            throw new SQLException("Error inserting into molecules DB. The autogenerated mid was not outputted.");
        }

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

    /** Inserts many molecules into SQL via list of files.
     *
     * @param files
     * @throws SQLException
     */
    public void insertMolecule(File[] files) throws SQLException {
        long startTime = System.nanoTime();
        MoleculeText[] molecules = new MoleculeText[files.length];
        // Get molecule information from text file
        for(int i = 0; i < files.length; i++){
            molecules[i] = new MoleculeText(files[i].getAbsolutePath());
        }

        insertManyMolecules(molecules);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Insert Time: " + duration/1000000 + "ms");
    }

    public void insertRandomMolecules(int numberMolecules) throws SQLException {
        long startTime = System.nanoTime();
        MoleculeRandomized[] molecules = new MoleculeRandomized[numberMolecules];
        for (int i = 0; i< numberMolecules; i++){
            molecules[i] = new MoleculeRandomized();
        }
        insertManyMolecules(molecules);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Insert Time: " + duration/1000000 + "ms");
    }


    /** Inserts many rows in the DB
     *
     * @param molecules list of molecules
     * @throws SQLException
     */
    private void insertManyMolecules(MoleculeAbstract[] molecules) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for( int i = 0 ; i < molecules.length; i++ ) {
            sb.append("(?, ?),");
        }
        String stmt = "INSERT INTO molecules (name, num_atoms) VALUES "
                + sb.deleteCharAt( sb.length() -1 ).toString() + ";";


        // SQL
        // Return the auto incremented mids
        preparedStatement = connect
                .prepareStatement(stmt, PreparedStatement.RETURN_GENERATED_KEYS);

        for(int i = 0, j = 1; i < molecules.length; i++, j+=2){
            preparedStatement.setString(j, molecules[i].getMoleculeName());
            preparedStatement.setInt(j + 1, molecules[i].getNumVertices());
        }
        preparedStatement.executeUpdate();
        connect.commit();

        // Grab the generated keys i.e the auto-incremented mids from the molecules query
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        ArrayList<Integer> mids = new ArrayList<>(molecules.length);

        while (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            mids.add(id);
        }
        // Check the size of queried mids - make sure it matches the number of molecules we are adding
        // if they dont match, something went wrong. Need to rollback the database.
        if (mids.size() != molecules.length){
            System.out.println("mids size : " + mids.size());
            connect.rollback();
            preparedStatement.close();
            connect.close();
            throw new SQLException("Number of queried mids does not match the number of molecules in the directory.");
        }

        for(int i = 0; i < molecules.length; i++) {
            MoleculeAbstract m = molecules[i];
            for (int ii = 0; ii < m.numVertices; ii++) {
                // Atoms table
                preparedStatement = connect
                        .prepareStatement("INSERT INTO atoms VALUES (default, ?, ?, ?)");

                // mid, atom, vertex
                preparedStatement.setInt(1, mids.get(i));
                preparedStatement.setString(2, m.getAtomList().get(ii));
                preparedStatement.setInt(3, ii);

                preparedStatement.executeUpdate();
                connect.commit();
            }

            LinkedList<Integer>[] adjacencyList = m.getAdjacencyList();
            for (int ii = 0; ii < m.numVertices; ii++) {
                for (int vv : adjacencyList[ii]) {
                    //Edges table
                    preparedStatement = connect
                            .prepareStatement("INSERT INTO edges VALUES (default, ?, ?, ?)");

                    // mid, vertex1, vertex2
                    preparedStatement.setInt(1, mids.get(i));
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

        // Connect to Database
        H2DB dao = new H2DB();
        dao.connect();
//        for(int i = 0; i < 2; i++){
//            dao.insertRandomMolecules(1);
//            System.out.println("iter "+ i + " complete");
//        }
//        dao.insertMolecule("molecules");
//        // You can provide the whole list of atoms
//        // or you can just provide the UNIQUE list of atoms
        ArrayList<String> atoms = new ArrayList<>();
        atoms.add("C");
        atoms.add("H");

        int numAtoms = 30;

        // Find molecules that have only have Carbon and Hydrogen and have 14 total atoms.
        MoleculeDB[] molecules = dao.findSameAtoms(numAtoms, atoms);

        // Print out the molecule names, vertices, adj matrix
        for (MoleculeDB m : molecules){
            System.out.println(m.getMoleculeName() + "\t" + m.getNumVertices());
            m.getAdjacencyMatrix();
            System.out.println("------------------------------------------------------");
        }

    }
}
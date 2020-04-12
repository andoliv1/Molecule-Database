package main.java;


import org.h2.jdbcx.JdbcConnectionPool;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

public class H2DB {
    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    static final String User = "sa";
    static final String Pw = "";
    static final String URL = "jdbc:h2:~/moleculedb";
    static final String Driver = "org.h2.Driver";
    static JdbcConnectionPool cp;
    public H2DB(){
        cp = JdbcConnectionPool.create(URL, User, Pw);
        cp.setMaxConnections(50);
    }
    /** Make connection to db.
     *
     */
    public Connection connect() throws SQLException {
        // Load the MySQL driver
//        try {
//            Class.forName(Driver);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        // Setup the connection with  DB
//        Properties myProp = new Properties();
//        myProp.put("user", User);
//        myProp.put("password", Pw);
//        myProp.put("allowMultiQueries", "true");
//        try {
//            connect = DriverManager
//                    .getConnection(URL,
//                                    myProp);
//            connect.setAutoCommit(false);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return connect;
        return cp.getConnection();

    }


    public MoleculeDB queryMoleculeByName(String name) throws SQLException {
        MoleculeDB molecule;
        try {
            connect = connect();

            String sql = "WITH find_mid AS (SELECT mid, num_atoms \n" +
                         "FROM molecules WHERE name = ?)" +
                        "SELECT A1.mid AS mid, F.num_atoms, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n" +
                        " FROM\n" +
                        " find_mid F JOIN atoms A1" +
                        " ON F.mid = A1.mid \n" +
                        " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid)\n" +
                        " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n";
            preparedStatement = connect
                    .prepareStatement(sql);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            int mid;
            if (resultSet.next()){
                mid = resultSet.getInt("mid");
                int numAtoms = resultSet.getInt("num_atoms");
                 molecule = new MoleculeDB(mid, name, numAtoms);
            }
            else{
                System.out.println("ereras");
                // Found nothing
                return null;
            }
            int vertex1, vertex2;
            String atom1, atom2;
            while(resultSet.next()){
                if(resultSet.getInt("mid") != mid) break;
                vertex1 = resultSet.getInt("vertex1");
                vertex2 = resultSet.getInt("vertex2");
                atom1 = resultSet.getString("atom1");
                atom2 = resultSet.getString("atom2");
                molecule.setAtom(vertex1, atom1);
                molecule.setAtom(vertex2, atom2);
                molecule.setEdge(vertex1, vertex2);
            }
        } catch(SQLException e){
            e.printStackTrace();
            close();
            return null;
        } finally {
            close();
        }
        return molecule;
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
        try {
            connect = connect();

            String sql = "SELECT A1.mid AS mid, M.name, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n" +
                    " FROM\n" +
                    " (SELECT A.mid, A.atom, A.vertex\n" +
                    " FROM atoms A WHERE A.mid IN (SELECT * FROM TABLE(x INTEGER = ? ) )) A1 \n" +
                    " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n" +
                    " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid)\n" +
                    " JOIN molecules M on A1.mid = m.mid;";
            preparedStatement = connect
                    .prepareStatement(sql);
            preparedStatement.setObject(1, mids.toArray());
            resultSet = preparedStatement.executeQuery();
        } catch(SQLException e){
            close();
            return null;
        } finally {
            close();
        }
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
        // mapMolecule is a dictionary that takes the  <key, value> = <mid, main.java.MoleculeDB>
        HashMap<Integer, MoleculeDB> mapMolecule = new HashMap<>(1000);

        try {
            connect = connect();

            // THIS QUERY WAS MODIFIED TO BE USED WITH H2 DB
            // Specifically for the WITH _ IN clause
            // https://github.com/h2database/h2database/issues/149
            // Timmy

            // This query will extract the mid's of the molecules
            // of the same number of atoms and of the same type of atoms
            String sql =
                    "WITH same_num AS(SELECT mid, name \n" +
                            "FROM molecules \n" +
                            "WHERE num_atoms = ?) \n" +
                    "SELECT A1.mid AS mid, F.name, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n" +
                    " FROM\n" +
                    " same_num F JOIN atoms A1" +
                    " ON F.mid = A1.mid \n" +
                    " JOIN atoms A2 ON (A2.vertex=E.vertex2 AND A2.mid=E.mid)\n" +
                    " JOIN edges E ON (A1.vertex=E.vertex1 AND A1.mid=E.mid)\n";

            preparedStatement = connect
                    .prepareStatement(sql);
            preparedStatement.setInt(1, numAtoms);
            resultSet = preparedStatement.executeQuery();
            //        System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());


            // Populate the atoms, adjacency lists and matrices
            MoleculeDB molecule;
            int mid;
            String name;
            int vertex1, vertex2;
            String atom1, atom2;
            while (resultSet.next()) {
                mid = resultSet.getInt("mid");
                vertex1 = resultSet.getInt("vertex1");
                vertex2 = resultSet.getInt("vertex2");
                atom1 = resultSet.getString("atom1");
                atom2 = resultSet.getString("atom2");
                name = resultSet.getString("name");

                molecule = mapMolecule.get(mid);
                if (molecule == null) {
                    molecule = new MoleculeDB(mid, name, numAtoms);
                    mapMolecule.put(mid, molecule);
                }
                molecule.setAtom(vertex1, atom1);
                molecule.setAtom(vertex2, atom2);
                molecule.setEdge(vertex1, vertex2);
            }
        } catch(SQLException e){
            e.printStackTrace();
            close();
            return null;
        } finally {
            close();
        }

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
        HashMap<Integer, MoleculeDB> mapMolecule = new HashMap<>();

        try {
            connect = connect();

            // THIS QUERY WAS MODIFIED TO BE USED WITH H2 DB
            // Specifically for the WITH _ IN clause
            // https://github.com/h2database/h2database/issues/149
            // Timmy

            // This query will extract the mid's of the molecules
            // of the same number of atoms and of the same type of atoms
            String sql =
                    "WITH same_atoms  AS " +
                            " (SELECT A.atom, A.vertex, AA.mid, AA.name, A.id FROM" +
                            "(SELECT A.mid, M.name \n" +
                            " FROM molecules M JOIN atoms A ON M.mid = A.mid\n" +
                            " WHERE M.num_atoms=? AND A.atom IN (SELECT x FROM TABLE(x VARCHAR = ? ))" +
                            " GROUP BY A.mid \n" +
                            " HAVING COUNT(A.mid) = ?) AA join atoms A ON AA.mid = A.mid)" +
                    "SELECT A1.mid AS mid, A1.name, A1.atom AS atom1, A2.atom AS atom2, E.vertex1, E.vertex2\n " +
                    " FROM\n " +
                    " same_atoms A1 " +
                    " JOIN same_atoms A2 ON (A1.mid = A2.mid AND A1.id!=A2.id) \n" +
                    " JOIN edges E ON (A1.mid=E.mid)\n " +
                    " WHERE A1.vertex = E.vertex1 AND A2.vertex = E.vertex2 ";

            preparedStatement = connect
                    .prepareStatement(sql);
            preparedStatement.setObject(2, atoms.toArray());
            preparedStatement.setInt(1, numAtoms);
            preparedStatement.setInt(3, numAtoms);
            resultSet = preparedStatement.executeQuery();
//            System.out.println((t2-t1)/1000000);
//            System.out.println("QUERY THAT WAS RUN: \n" + preparedStatement.toString());

            // mapMolecule is a dictionary that takes the  <key, value> = <mid, main.java.MoleculeDB>
//            ArrayList<Integer> mids = new ArrayList<>();

            // Populate the atoms, adjacency lists and matrices
            MoleculeDB molecule;
            int vertex1, vertex2;
            String atom1, atom2;
            int mid;
            String name;

            while(resultSet.next()){
                mid = resultSet.getInt("mid");
                name = resultSet.getString("name");
                vertex1 = resultSet.getInt("vertex1");
                vertex2 = resultSet.getInt("vertex2");
                atom1 = resultSet.getString("atom1");
                atom2 = resultSet.getString("atom2");
//                System.out.println(name + " " + vertex1 + "   " + vertex2);
                molecule = mapMolecule.get(mid);
                if (molecule == null) {
                    molecule = new MoleculeDB(mid, name, numAtoms);
                    mapMolecule.put(mid, molecule);
                }
                molecule.setAtom(vertex1, atom1);
                molecule.setAtom(vertex2, atom2);
                molecule.setEdge(vertex1, vertex2);
            }
        } catch(SQLException e){
            close();
            e.printStackTrace();

            return null;
        } finally {
            close();
        }

        return mapMolecule.values().toArray(new MoleculeDB[0]);
    }

    /** Inserts molecule information into SQL via filename.
     *
     * @param filename
     * @throws SQLException
     */
    public void insertMolecule(String filename) throws SQLException {

        try {
            connect = connect();
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
            } else {
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
            for (int ii = 0; ii < m.numVertices; ii++) {
                for (int vv : adjacencyList[ii]) {
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
        } catch (SQLException e){
            close();
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /** Inserts many molecules into SQL via list of files.
     *
     * @param files
     * @throws SQLException
     */
    public void insertMolecule(File[] files) throws SQLException {
        long startTime = System.nanoTime();
        try {
            connect = connect();

            MoleculeText[] molecules = new MoleculeText[files.length];
            // Get molecule information from text file
            for (int i = 0; i < files.length; i++) {
                molecules[i] = new MoleculeText(files[i].getAbsolutePath());
            }

            insertManyMolecules(molecules);
        } catch (SQLException e){
            close();
            e.printStackTrace();
        } finally {
            close();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Insert Time: " + duration/1000000 + "ms");
    }

    public void insertRandomMolecules(int numberMolecules) throws SQLException {
        long startTime = System.nanoTime();
        try {
            connect = connect();
            MoleculeRandomized[] molecules = new MoleculeRandomized[numberMolecules];
            for (int i = 0; i< numberMolecules; i++){
                molecules[i] = new MoleculeRandomized(3,7);
            }
            insertManyMolecules(molecules);
        } catch (SQLException e){
            close();
            e.printStackTrace();
        } finally {
            close();
        }
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

        try {
            connect = connect();
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
        } catch (SQLException e){
            close();
            e.printStackTrace();
        } finally {
            close();
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

    public void SQLDump() throws SQLException {
        try {
            connect = connect();
            preparedStatement = connect
                    .prepareStatement("SCRIPT TO 'h2dump.sql'");
            preparedStatement.execute();
            connect.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            close();
        } finally {
            close();
        }
    }

        /** Close the connections
     *
     */
    public void close() throws SQLException {
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
//        dao.connect();
        for(int i = 0; i < 40; i++){
            dao.insertRandomMolecules(25000);
            System.out.println("iter "+ i + " complete");
        }
////        dao.insertMolecule("molecules");
////        // You can provide the whole list of atoms
////        // or you can just provide the UNIQUE list of atoms
//        ArrayList<String> atoms = new ArrayList<>();
//        atoms.add("C");
//        atoms.add("H");
//
//        int numAtoms = 30;
//
//        // Find molecules that have only have Carbon and Hydrogen and have 14 total atoms.
//        MoleculeDB[] molecules = dao.findSameAtoms(numAtoms, atoms);
//
//        // Print out the molecule names, vertices, adj matrix
//        for (MoleculeDB m : molecules){
//            System.out.println(m.getMoleculeName() + "\t" + m.getNumVertices());
//            m.getAdjacencyMatrix();
//            System.out.println("------------------------------------------------------");
//        }
//        MoleculeDB m = dao.queryMoleculeByName("carbondioxide");
//        System.out.println(m.getMoleculeName());

    }
}
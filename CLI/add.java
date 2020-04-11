import main.java.H2DB;

import java.io.File;
import java.sql.SQLException;

class add//extends a super class for add class
{
    private static H2DB database;

    public static int add_molecule(String filename) throws SQLException {
        //insert the function call of molecule
        String str = null;
        database.insertMolecule(filename);
        return 1;
    }
    // Your program begins with a call to main(). 
    // Prints "Hello, World" to the terminal window. 
    public static void main(String args[]) throws SQLException {
        System.out.println("Adding...");
        if (add_molecule(args[0])==1){
            System.out.println("Molecule Added Successful");

        }
        else{
            System.out.println("Molecule Added Unsuccessfully");
        }
        
    } 
} 
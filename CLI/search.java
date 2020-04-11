import main.java.*;
import main.java.H2DB;

import java.sql.SQLException;

class search  //extends a super class for add class
{
    private static H2DB database;

    public static String search_molecule(String filename) throws SQLException {
        String result = null;
        //insert the function call of molecule
        Operations Ops = new Operations();
        MoleculeText m = new MoleculeText(filename);

        MoleculeDB[] molecules = database.findSameAtoms(m.numVertices, m.getAtomList());

        for (MoleculeDB molecule : molecules) {
            if(Ops.checkIsomorphism(m, molecule)){
                System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
                result = molecule.moleculeName;
            }

        }

        return result;
    }
    // Your program begins with a call to main(). 
    // Prints "Hello, World" to the terminal window. 
    public static void main(String args[]) throws SQLException {
        System.out.println("searching...");
        String match=search_molecule(args[0]);
        if (match==null){
            System.out.println("Molecule Searched Successful");
            System.out.println("Molecule Searched: "+ match);

        }
        else{
            System.out.println("Molecule Searched Unsuccessfully");
        }
        
    } 
} 
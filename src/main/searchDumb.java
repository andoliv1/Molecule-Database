package main;

//Group9: Molecular Database
/*
Instructions: the code is well commented out and by running main you should see the tests we have created to check our
isomorphism algorithm.
 */
import javafx.util.Pair;

import java.util.*;

public class searchDumb{
    /*
    Compare method used to denote how strings are sorted
     */
    private static int compare(Pair<String,Integer> o1, Pair<String,Integer> o2) {
        if ((int) o1.getKey().charAt(0) > (int) o2.getKey().charAt(0)) {
            return 1;
        } else if ((int) o1.getKey().charAt(0) < (int) o2.getKey().charAt(0)) {
            return -1;
        } else if (o1.getKey().length() > 1 && o2.getKey().length() > 1) {
            if ((int) o1.getKey().charAt(1) > (int) o2.getKey().charAt(1)) {
                return 1;
            } else if ((int) o1.getKey().charAt(1) < (int) o2.getKey().charAt(1)) {
                return -1;
            } else {
                return 0;
            }
        } else if (o1.getKey().length() > 1) {
            return 1;
        } else {
            return -1;
        }
    }

    //this will sort the adjacencyList in alphabetical order meaning by first letter and if it contains a second letter
    //by second letter
    public static ArrayList<Pair<String,Integer>> sortAtomList(ArrayList<Pair<String,Integer>> atomList){
        Collections.sort(atomList, (Comparator<Pair<String,Integer>>) searchDumb::compare);
        return atomList;
    }


    /*
    This method should parse the SQL database to see where are the isomers (molecules that have the same atoms but the
    atoms are possibly under different arrangements i.e different adjacency matrices) to the molecule we are trying to
    search for.
    Comment: I am not quite sure how to query the SQL database yet so I need to figure out how to do this.
     */

//    @Override

    public static int[] findIDsSql(ArrayList<String> adjacencyList) {
        return new int[0];
    }

    /*
    The brute force idea I have for this method is simply to check each connection an atom and make sure the vertex from one
    molecule connects in the same way as another vertex in the other molecule. If that isn't true than the molecules can't
    be isomorphic and if there is a one to one mapping from all connection from one molecule to all connections in the other
    molecule then it is true.
     */
    public static boolean isIsomorphic(main.MoleculeAbstract molecule1, main.MoleculeAbstract molecule2) {
        //get the molecules adjacency matrix
        int[][] adj1 = molecule1.getAdjacencyMatrix();
        int[][] adj2 = molecule2.getAdjacencyMatrix();
        //get the molecules vertex list
        ArrayList<String> ato1 = molecule1.getAtomList();
        ArrayList<String> ato2 = molecule2.getAtomList();
        //note ato1 and ato2 should have the same size because we only look for isomorphisms of molecules that have same
        //list.
        int position = 0;
        boolean found = true;
        ArrayList<Integer> used = new ArrayList<>();
        int i = 0;
        //stop if a vertex from molecule1 wasn't matched to any of the vertices from molecule 2 or if all the vertices
        //were matched.
        while(found == true && i < ato1.size()) {
            found = false;
            int j = 0;
            //parse through all vertices of molecule2 to see if their connections matches to the connections in the vertex
            //i at molecule 1.
            while(found == false && j < ato2.size()) {
                //check if the vertex you are at in molecule2 has already been matched to a vertex in molecule1
                if (ato1.get(i).equals(ato2.get(j)) && (used.contains(j) == false)) {
                    //check the vertex connections
                    int[] connections = adj1[i];
                    int[] connections2 = adj2[j];
                    //get the atoms connected to the vertex with respect to their vertex list
                    ArrayList<Pair<String,Integer>> atom_connections = new ArrayList<>();
                    ArrayList<Pair<String,Integer>> atom_connections2 = new ArrayList<>();
                    for (int k = 0; k < connections.length; k++) {
                        if (connections[k] >= 1) {
                            atom_connections.add(new Pair(ato1.get(k),connections[k]));
                        }
                        if (connections2[k] >= 1) {
                            atom_connections2.add(new Pair(ato2.get(k),connections2[k]));
                        }
                    }
                    //sort both atom connections
                    sortAtomList(atom_connections);
                    sortAtomList(atom_connections2);
                    //System.out.println("Here are the atom1 connections " + atom_connections.toString());
                    //System.out.println("Here are the atom2 connections " + atom_connections2.toString());
                    int w = 0;
                    while (w < atom_connections.size()) {
                        //if the connections are not the same then we don't want to use the vertex
                        if (atom_connections.get(w).getKey().equals(atom_connections2.get(w).getKey()) == false &&
                                atom_connections.get(w).getValue().equals(atom_connections2.get(w).getValue()) == false) {
                            found = false;
                        }
                        w++;
                    }
                    //System.out.println("w " + w);
                    //System.out.println("atom connections size " + atom_connections.size());
                    //if the connections are the same store the fact that you are using this vertex to match to vertex i
                    //in molecule 1 and won't be using to describe other vertices in molecule1 even if they have the same
                    //atom connections
                    if (w == atom_connections.size()) {
                        //System.out.println("hello");
                        used.add(j);
                        found = true;
                    }
                }
                j++;
            }
            //if you parsed through all vertices in molecule2 and all vertices don't match the connections of vertex i
            //in molecule 1 than they are not isomorphic.
            if(found == false){
                return false;
            }
            i++;
        }
        //if we checked every vertex and could find a mapping from a vertex i in molecule 1 to a vertex j in molecule 2
        //then the molecules are isomorphic so return true.
        return true;
    }

    public static void main(String[] args){
        ArrayList<String> water_atoms = new ArrayList<String>();
        water_atoms.add("H");
        water_atoms.add("H");
        water_atoms.add("O");
        int[][] water_matrix = new int[3][3];
        water_matrix[0][2] = 1;
        water_matrix[1][2] = 1;
        water_matrix[2][0] = 1;
        water_matrix[2][1] = 1;
        ArrayList<String> water_atom2 = new ArrayList<String>();
        water_atom2.add("O");
        water_atom2.add("H");
        water_atom2.add("H");
        int[][] water_matrix2 = new int[3][3];
        water_matrix2[0][2] = 1;
        water_matrix2[0][1] = 1;
        water_matrix2[1][0] = 1;
        water_matrix2[2][0] = 1;
        MoleculeText water = new MoleculeText(water_matrix,water_atoms);
        MoleculeText water2 = new MoleculeText(water_matrix2,water_atom2);
        boolean isomorphic = isIsomorphic(water,water2);
        System.out.println("Are water1 and water2 isomorphic? " + isomorphic);

        ArrayList<String> salt_atoms = new ArrayList<String>();
        salt_atoms.add("Na");
        salt_atoms.add("Cl");
        int[][] salt_matrix = new int[2][2];
        salt_matrix[0][1] = 1;
        salt_matrix[1][0] = 1;
        ArrayList<String> salt_atoms2 = new ArrayList<String>();
        salt_atoms2.add("Na");
        salt_atoms2.add("K");
        int[][] salt_matrix2 = new int[2][2];
        salt_matrix2[0][1] = 1;
        salt_matrix2[1][0] = 1;
        MoleculeText salt_mol = new MoleculeText(salt_matrix,salt_atoms);
        MoleculeText salt_mol2 = new MoleculeText(salt_matrix2,salt_atoms2);
        boolean isomorphic2 = isIsomorphic(salt_mol,salt_mol2);
        System.out.println("Are sodium chloride and sodium potassium isomorphic? " + isomorphic2);

        ArrayList<String> methylene_atoms = new ArrayList<String>();
        methylene_atoms.add("C");
        methylene_atoms.add("H");
        methylene_atoms.add("H");
        int[][] methylene_matrix = new int[3][3];
        methylene_matrix[0][1] = 1;
        methylene_matrix[0][2] = 1;
        methylene_matrix[1][0] = 1;
        methylene_matrix[2][0] = 1;
        MoleculeText methylene_mol = new MoleculeText(methylene_matrix,methylene_atoms);
        boolean isomorphic3 = isIsomorphic(water2,methylene_mol);
        System.out.println("Are water and methylene isomorphic? " + isomorphic3);

        ArrayList<String> carbon_dioxide_atoms = new ArrayList<String>();
        carbon_dioxide_atoms.add("C");
        carbon_dioxide_atoms.add("O");
        carbon_dioxide_atoms.add("O");
        int[][] carbon_dioxide_matrix = new int[3][3];
        carbon_dioxide_matrix[0][1] = 2;
        carbon_dioxide_matrix[0][2] = 2;
        carbon_dioxide_matrix[1][0] = 2;
        carbon_dioxide_matrix[2][0] = 2;
        MoleculeText carbon_dioxide_mol = new MoleculeText(carbon_dioxide_matrix,carbon_dioxide_atoms);

        ArrayList<String> carbon_dioxide_atoms2 = new ArrayList<String>();
        carbon_dioxide_atoms2.add("C");
        carbon_dioxide_atoms2.add("C");
        carbon_dioxide_atoms2.add("O");
        int[][] carbon_dioxide_matrix2 = new int[3][3];
        carbon_dioxide_matrix2[0][1] = 2;
        carbon_dioxide_matrix2[0][2] = 2;
        carbon_dioxide_matrix2[1][0] = 2;
        carbon_dioxide_matrix2[2][0] = 2;
        MoleculeText carbon_dioxide_mol2 = new MoleculeText(carbon_dioxide_matrix2,carbon_dioxide_atoms2);
        boolean isomorphic4 = isIsomorphic(carbon_dioxide_mol,carbon_dioxide_mol2);
        System.out.println("Are carbon_dioxide_mol and carbon_dioxide_mol2 isomorphic? " + isomorphic4);

        ArrayList<String> carbon_dioxide_atoms3 = new ArrayList<String>();
        carbon_dioxide_atoms3.add("O");
        carbon_dioxide_atoms3.add("O");
        carbon_dioxide_atoms3.add("C");
        int[][] carbon_dioxide_matrix3 = new int[3][3];
        carbon_dioxide_matrix3[0][2] = 2;
        carbon_dioxide_matrix3[1][2] = 2;
        carbon_dioxide_matrix3[2][0] = 2;
        carbon_dioxide_matrix3[2][1] = 2;
        MoleculeText carbon_dioxide_mol3 = new MoleculeText(carbon_dioxide_matrix3,carbon_dioxide_atoms3);
        boolean isomorphic5 = isIsomorphic(carbon_dioxide_mol,carbon_dioxide_mol3);
        System.out.println("Are carbon_dioxide_mol and carbon_dioxide_mol3 isomorphic? " + isomorphic5);

    }
}

package main.java;

//Group9: Molecular Database
/*
Instructions: the code is well commented and by running main you should see the tests we have created to check our
isomorphism algorithm.
 */

import javafx.util.Pair;
import java.util.*;

public class searchDumb{
    private static Object NullPointerException;

    /**
     * Method used to compare two Pairs. A pair consists of the a 3 char string and an integer denoting the number of
     * connections made from an origin atom to the atom inside the Pair.
     * @param o1: pair 1
     * @param o2: pair 2
     * @return
     */
    private static int compareWithNumbers(Pair<String,Integer> o1, Pair<String,Integer> o2) {
        //if the initial character is bigger than the other then o1 > o2
        if ((int) o1.getKey().charAt(0) > (int) o2.getKey().charAt(0)) {
            return 1;
        }
        //if the opposite of condition 0 then o1 < o2
        else if ((int) o1.getKey().charAt(0) < (int) o2.getKey().charAt(0)) {
            return -1;
        }
        //if both have length greater then 1 then we need need to check the second and third characters of the String
        else if (o1.getKey().length() > 1 && o2.getKey().length() > 1) {
            // basically the same thing as the first two condition but for the second character
            if ((int) o1.getKey().charAt(1) > (int) o2.getKey().charAt(1)) {
                return 1;
            } else if ((int) o1.getKey().charAt(1) < (int) o2.getKey().charAt(1)) {
                return -1;
            }
            //now if we enter this condition it means we are checking a rigorous isomorphism meaning the we are now trying
            //to create a bijection between all vertices that are not distinct in the molecule. Vertices are not distinct
            //if they have the same atom and the same connections as the other vertices in the molecule.
            else if(o1.getKey().length() > 2 && o2.getKey().length() > 2){
                if((int) o1.getKey().charAt(2) > (int) o2.getKey().charAt(2)){
                    return 1;
                }
                else if( (int) o1.getKey().charAt(2) < (int) o2.getKey().charAt(2)){
                    return -1;
                }
                else {
                    return 0;
                }
            }
            //basic conditions
            else if(o1.getKey().length() >2 ){
                return 1;
            }
            else if(o2.getKey().length() > 2){
                return -1;
            }
            else{
                return 0;
            }
        }
        //ending conditions for comparing two atoms
        else if (o1.getKey().length() > 1) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * This method will sort all the atoms in the atomList of a given Molecule according to the compareWithNumbers
     * function above
     * @param atomList: all atoms in the atom list of a molecule and consider the numbers each atom may have.
     * @return
     */
    public static ArrayList<Pair<String,Integer>> sortAtomListNumbers(ArrayList<Pair<String,Integer>> atomList){
        Collections.sort(atomList, (Comparator<Pair<String,Integer>>) searchDumb::compareWithNumbers);
        return atomList;
    }

    /*
    This method should parse the SQL database to see where are the isomers (molecules that have the same atoms but the
    atoms are possibly under different arrangements i.e different adjacency matrices) to the molecule we are trying to
    search for.
    Comment: I am not quite sure how to query the SQL database yet so I need to figure out how to do this.
     */
    public static int[] findIDsSql(ArrayList<String> adjacencyList) {
        return new int[0];
    }


    /**
    Simple helper function for swaping the elements of an array
     **/
    public static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }


    /**
     * Here we want to generate all possible isomorphism mappings and verify that one of them work for the molecule. The int[] a holds
     * the numbers that need to be permuted and the int[] indices hold where the permuted numbers will be appended to the atom.
     * The concept is better explained in the function verify_rigorous isomorphism
     */
    public static boolean generate(int n, int[] a, MoleculeAbstract molecule1, MoleculeAbstract molecule2, int[] indices) {
        // Placeholder for swapping values
        int tmp;
        // If a new permutation has been found then change the respective indices of the vertices that needed to have a key assigned to it
        boolean isIso = false;
        if(n == 1) {
            //Change the labels of the respective vertices
            for(int i = 0; i < indices.length; i++){
                String str = molecule1.atoms.get(indices[i]);
                str = str.replaceAll("[0-9]","");
                str += a[i];
                molecule1.changeLabels(str,indices[i]);
            }
              //System.out.println("Molecule 1");
              //System.out.println(molecule1.toString());
              //System.out.println("Molecule 2");
              //System.out.println(molecule2.toString());
            //call the isomorphic function on the new molecules
            isIso = isIsomorphicWithNumbers( molecule1,  molecule2);
            //if the resulting permutation results in an Isomorphism than it means the two molecules are isomorphic
            if(isIso == true){
                return true;
            }
            else{
                return false;
            }
        }
        else {	// If a new permutation has not yet been found
            for(int i = 0; i < (n-1); i++) {
                if(generate(n-1, a,molecule1,molecule2,indices) == true){
                    isIso = true;
                }
                if(n % 2 == 0) {
                    // Swap entry i with entry n-1
                    tmp = a[i];
                    a[i] = a[n-1];
                    a[n-1] = tmp;
                }
                else {
                    // Swap entry 0 with entry n-1
                    tmp = a[0];
                    a[0] = a[n-1];
                    a[n-1] = tmp;
                }
            }
            if(generate(n-1, a,molecule1,molecule2,indices) == true){
                isIso = true;
            }
        }
        return isIso;
    }


    /**
     * This function has several parts to it.
     *
     * The first part of the function finds all the atoms in molecule 1 that generate ambiguity. That is we create a HashSet
     * with a specific connection denoted by the source atom and the outgoing connections from the atom (e.g : H2O would
     * have in the hash set O: H H). If we find that there are other atoms in the molecule that have the same source atom and outgoing
     * connections then we need to note that, which we do by storing the indices of those repeated connections in a duplicates Linked List.
     *
     * The second part of the function makes a call to a initialCorrespondence function. This function is essentially the same as the
     * isIsomorphic function but instead it returns an array of how the atoms in molecule2 would be mapped to the atoms in molecule1.
     * That is if we have an array [0 4 3 1 2] it means the 0th atom in molecule2 would be mapped to the 0th atom in molecule1
     * and the 1st atom in molecule2 would be mapped to the 4th atom in molecule1 and so on.
     *
     * Now for the third part of the function we are simply assigning indices to the atoms in molecule1 and molecule2 according to the atoms in the first part of the
     * function that we found could generate ambiguity. We can do this to molecule1 and molecule2 because we know the correspondence of molecule2 atoms to molecule1 calculated by the second part of the
     * function. Finally assigning numbers to the molecules will allow us to eliminate the ambiguity because it essentially creates a bijection between vertices of a molecule to another
     * molecule so the only way the molecules can be isomorphic is if the bijection between ambiguous (remember ambigous is defined as atoms that have the same
     * source atom and outgoing connections) atoms in the molecules exists.
     *
     * Lastly we create all possible bijections with the generate function. If any of those bijections
     * turns out to be a isomorphism then the molecules must be isomorphic.
     *
     * TL;DR:
     * The first part is finding all the vertices that might generate an error. Second part is creating an ambiguous correspondence between atoms in the first molecule1 to atoms in molecule2.
     * Finally generate specific correspondence from the ambiguous to confirm isomorphism.
     *
     * @param molecule1
     * @param molecule2
     * @returne
     */
    public static boolean verify_rigorous_isomorphism(MoleculeAbstract molecule1, MoleculeAbstract molecule2){
        int[][] adj1 = molecule1.getAdjacencyMatrix();
        int[][] adj2 = molecule2.getAdjacencyMatrix();
        //get the molecules vertex list
        ArrayList<String> ato1 = molecule1.getAtomList();
        ArrayList<String> ato2 = molecule2.getAtomList();
        //want to find duplicate atoms that have the same connections
        HashSet<String> atoms = new HashSet<>();
        //stores the atom and its connections
        HashSet<Pair<String,ArrayList<String>>> atom_connections = new HashSet<>();
        List<Integer> duplicates = new ArrayList<>();
        for(int i = 0; i < ato1.size(); i++){
            String atom = ato1.get(i);
            ArrayList<String> build_adj = new ArrayList<>();
            int[] this_adj = adj1[i];
            for(int k = 0; k < this_adj.length; k++){
                build_adj.add(ato1.get(i));
            }
            //if the atom is an ambiguous atom then we want to copy its index and put it in the duplicates list
            if(atoms.contains(atom)){
                if(atom_connections.contains(new Pair(atom,build_adj))){
                    duplicates.add(i);
                }
            }
            //else we want to store it for finding future atoms that might be ambiguous to this atom
            else{
                atoms.add(atom);
                atom_connections.add(new Pair(atom,build_adj));
            }
        }
        //From (Beginning) to (End) this just finds the initial atom that generated the ambiguous atoms in the duplicate array. So for each distinct atom in the duplicates array there is a prior
        //atom that resulted in the atom being ambiguous this block is just finding that atom.
        //(Beginning)
        HashSet<Integer> duplicates_final  = new HashSet<>();
        for(Integer dup : duplicates){
            int[] connec = adj1[dup];
            int counter= 0;
            int first_index = 0;
            boolean found = false;
            duplicates_final.add(dup);
            while(true) {
                int[] connec2 = adj1[counter];
                ArrayList<Pair<String, Integer>> atom_connec = new ArrayList<>();
                ArrayList<Pair<String, Integer>> atom_connec2 = new ArrayList<>();
                for (int k = 0; k < connec.length; k++) {
                    if (connec[k] >= 1) {
                        atom_connec.add(new Pair(ato1.get(k), connec[k]));
                    }
                    if (connec2[k] >= 1) {
                        atom_connec2.add(new Pair(ato2.get(k), connec2[k]));
                    }
                }
                //sort both atom connections
                sortAtomListNumbers(atom_connec);
                sortAtomListNumbers(atom_connec2);
                int w = 0;
                while (w < atom_connec.size() && w < atom_connec2.size()) {
                    //if the connections are not the same then we don't want to use the vertex
                    if (atom_connec.get(w).getKey().equals(atom_connec2.get(w).getKey()) == false &&
                            atom_connec.get(w).getValue().equals(atom_connec2.get(w).getValue()) == false) {
                        break;
                    }
                    w++;
                }
                if(w == atom_connec.size()){
                    //System.out.println("ok");
                    first_index = counter;
                    break;
                }
                counter++;
            }
            if(duplicates_final.contains(first_index) == false){
                duplicates_final.add(first_index);
            }
        }
        //(End)

        //Create the correspondence described by the function description
        ArrayList<Integer> initialCorrespondence = initialCorrespondence(molecule2,molecule1);
        int[] duplicates_to_array = new int[duplicates_final.size()];
        int counter = 0;
        for(Integer dup : duplicates_final){
            duplicates_to_array[counter] = dup;
            String newLabel = (String) ato1.get(dup) + dup;
            molecule1.changeLabels(newLabel,dup);
            counter++;
        }
        ato1 = molecule1.getAtomList();
        counter = 0;
        for(Integer corresp : initialCorrespondence){
            if(duplicates_final.contains(corresp)){
                molecule2.changeLabels(ato1.get(corresp),counter);
            }
            counter++;
        }
        String representation2  = molecule2.toString();
        String representation = molecule1.toString();
        //System.out.println("representation1");
        //System.out.println(representation);
        //System.out.println("repreentation2");
        //System.out.println(representation2);

        //make all specific bijections and check if any of them are isomorphic
        boolean isomorphic = generate(duplicates_to_array.length,duplicates_to_array, molecule1, molecule2, duplicates_to_array);
        return isomorphic;
    }

    /**
     * This method is the same as the isIsomorphicWithNumbers method but instead it simply returns a LinkedList with the mapping
     * of vertices from molecule1 to molecule2. For example if it returns [0 2 1] it means the 0th atom in molecule1 is mapped to the
     * 0th atom in molecule2 and the 1st atom in  molecule1 is mapped to the 2nd atom in molecule2 and lastly the 2nd atom in molecule1 is mapped
     * to the 1st atom in molecule2.
     * @param molecule1
     * @param molecule2
     * @return
     */
    public static ArrayList<Integer> initialCorrespondence(MoleculeAbstract molecule1, MoleculeAbstract molecule2) {
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
            //System.out.println("This is the atom we are trying to find a correspondence " + ato1.get(i) + " this is its index" + i);
            while(found == false && j < ato2.size()) {
                //System.out.println("This is the atom we are comparing " + ato2.get(j) + " this is its index" + j);
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
                    sortAtomListNumbers(atom_connections);
                    sortAtomListNumbers(atom_connections2);
                    int w = 0;
                    while (w < atom_connections.size() && w < atom_connections2.size()) {
                        //if the connections are not the same then we don't want to use the vertex
                        if (atom_connections.get(w).getKey().equals(atom_connections2.get(w).getKey()) == false &&
                                atom_connections.get(w).getValue().equals(atom_connections2.get(w).getValue()) == false) {
                            found = false;
                        }
                        w++;
                    }
                    //if the connections are the same store the fact that you are using this vertex to match to vertex i
                    //in molecule 1 and won't be using to describe other vertices in molecule1 even if they have the same
                    //atom connections
                    if (w == atom_connections.size() && w == atom_connections2.size()) {
                        used.add(j);
                        found = true;
                    }
                }
                j++;
            }
            //if you parsed through all vertices in molecule2 and all vertices don't match the connections of vertex i
            //in molecule 1 than they are not isomorphic.
            if(found == false){
                return null;
            }
            i++;
        }
        //if we checked every vertex and could find a mapping from a vertex i in molecule 1 to a vertex j in molecule 2
        //then the molecules are isomorphic so return true.
        return used;
    }



    /*
    The brute force idea I have for this method is simply to check each connection an atom and make sure the vertex from one
    molecule connects in the same way as another vertex in the other molecule. If that isn't true than the molecules can't
    be isomorphic and if there is a one to one mapping from all connection from one molecule to all connections in the other
    molecule then it is true.
     */
    public static boolean isIsomorphicWithNumbers(MoleculeAbstract molecule1, MoleculeAbstract molecule2){
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
            //System.out.println("This is the atom we are trying to find a correspondence " + ato1.get(i) + " this is its index" + i);
            found = false;
            int j = 0;
            //parse through all vertices of molecule2 to see if their connections matches to the connections in the vertex
            //i at molecule 1.
            while(found == false && j < ato2.size()) {
                //check if the vertex you are at in molecule2 has already been matched to a vertex in molecule1
                //System.out.println("This is the atom we are comparing " + ato2.get(j) + " this is its index" + j);
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
                    sortAtomListNumbers(atom_connections);
                    sortAtomListNumbers(atom_connections2);
//                    System.out.println("Here are the atom1 connections " + atom_connections.toString());
//                    System.out.println("Here are the atom2 connections " + atom_connections2.toString());
                    int w = 0;
                    while (w < atom_connections.size() && w < atom_connections2.size()) {
                        //if the connections are not the same then we don't want to use the vertex
//                        System.out.println(atom_connections.get(w).getKey());
//                        System.out.println(atom_connections2.get(w).getKey());
                        if (atom_connections.get(w).getKey().equals(atom_connections2.get(w).getKey()) == false ||
                                atom_connections.get(w).getValue().equals(atom_connections2.get(w).getValue()) == false) {
                            //System.out.println("entered false loop");
                            found = false;
                            break;
                        }
                        else {
                            w++;
                        }
                    }
                    //System.out.println("w " + w);
                    //System.out.println("atom connections size " + atom_connections.size());
                    //if the connections are the same store the fact that you are using this vertex to match to vertex i
                    //in molecule 1 and won't be using to describe other vertices in molecule1 even if they have the same
                    //atom connections
                    if (w == atom_connections.size() && w == atom_connections2.size()) {
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
        boolean weakIsomorphic = isIsomorphicWithNumbers(water,water2);
        boolean isomorphic;
        System.out.println("Molecule 1 name: water");
        System.out.println("representation: ");
        System.out.println(water.toString());
        System.out.println("Molecule 2 name: water2");
        System.out.println("representation: ");
        System.out.println(water2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            isomorphic = verify_rigorous_isomorphism(water,water2);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }
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
        System.out.println("Molecule 1 name: salt_mol");
        System.out.println("representation: ");
        System.out.println(salt_mol.toString());
        System.out.println("Molecule 2 name: salt_mol2");
        System.out.println("representation: ");
        System.out.println(salt_mol2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        weakIsomorphic = isIsomorphicWithNumbers(salt_mol,salt_mol2);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            isomorphic = verify_rigorous_isomorphism(salt_mol,salt_mol2);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }

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
        boolean isomorphic3 = isIsomorphicWithNumbers(water2,methylene_mol);
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
        System.out.println("Molecule 1 name: carbon_dioxide_mol");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol.toString());
        System.out.println("Molecule 2 name: carbon_dioxide_mol2");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        weakIsomorphic = isIsomorphicWithNumbers(carbon_dioxide_mol,carbon_dioxide_mol2);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            isomorphic = verify_rigorous_isomorphism(carbon_dioxide_mol,carbon_dioxide_mol2);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }

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
        System.out.println("Molecule 1 name: carbon_dioxide_mol");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol.toString());
        System.out.println("Molecule 2 name: carbon_dioxide_mol3");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol3.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        weakIsomorphic = isIsomorphicWithNumbers(carbon_dioxide_mol,carbon_dioxide_mol3);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            isomorphic = verify_rigorous_isomorphism(carbon_dioxide_mol,carbon_dioxide_mol3);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }

        /**
         * The following is an example by David Epstein on graph isomorphism and how two graphs with the same degree sequence
         * may not be isomorphic.
         */
        ArrayList<String> Na_Cl_atoms = new ArrayList<String>();
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Cl");
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Cl");
        Na_Cl_atoms.add("Cl");
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Na");
        int[][] Na_Cl_mol = new int[8][8];
        Na_Cl_mol[0][1] = 1;
        Na_Cl_mol[1][0] = 1;
        Na_Cl_mol[1][2] = 1;
        Na_Cl_mol[1][3] = 1;
        Na_Cl_mol[2][1] = 1;
        Na_Cl_mol[2][4] = 1;
        Na_Cl_mol[3][1] = 1;
        Na_Cl_mol[3][5] = 1;
        Na_Cl_mol[4][2] = 1;
        Na_Cl_mol[4][6] = 1;
        Na_Cl_mol[5][3] = 1;
        Na_Cl_mol[5][7] = 1;
        Na_Cl_mol[7][5] = 1;
        Na_Cl_mol[6][4] = 1;
        ArrayList<String> Na_Cl_atoms2 = new ArrayList<String>();
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Cl");
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Cl");
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Cl");
        Na_Cl_atoms2.add("Na");
        int[][] Na_Cl_mol2 = new int[8][8];
        Na_Cl_mol2[0][2] = 1;
        Na_Cl_mol2[1][2] = 1;
        Na_Cl_mol2[2][0] = 1;
        Na_Cl_mol2[2][1] = 1;
        Na_Cl_mol2[2][3] = 1;
        Na_Cl_mol2[3][2] = 1;
        Na_Cl_mol2[3][4] = 1;
        Na_Cl_mol2[4][3] = 1;
        Na_Cl_mol2[4][5] = 1;
        Na_Cl_mol2[5][4] = 1;
        Na_Cl_mol2[5][6] = 1;
        Na_Cl_mol2[6][5] = 1;
        Na_Cl_mol2[6][7] = 1;
        Na_Cl_mol2[7][6] = 1;
        MoleculeText Na_Cl= new MoleculeText(Na_Cl_mol,Na_Cl_atoms);
        MoleculeText Na_Cl2 = new MoleculeText(Na_Cl_mol2,Na_Cl_atoms2);
        System.out.println("Molecule 1 name: Na_Cl");
        System.out.println("representation: ");
        System.out.println(Na_Cl.toString());
        System.out.println("Molecule 2 name: Na_Cl2");
        System.out.println("representation: ");
        System.out.println(Na_Cl2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        weakIsomorphic = isIsomorphicWithNumbers(Na_Cl,Na_Cl2);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            isomorphic = verify_rigorous_isomorphism(Na_Cl,Na_Cl2);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }
    }
}

import javafx.util.Pair;

import java.util.*;

public class searchDumb{

    private static int compare(String o1, String o2) {
        if ((int) o1.charAt(0) > (int) o2.charAt(0)) {
            return 1;
        } else if ((int) o1.charAt(0) < (int) o2.charAt(0)) {
            return -1;
        } else if (o1.length() > 1 && o2.length() > 1) {
            if ((int) o1.charAt(1) > (int) o2.charAt(1)) {
                return 1;
            } else if ((int) o1.charAt(1) < (int) o2.charAt(1)) {
                return -1;
            } else {
                return 0;
            }
        } else if (o1.length() > 1) {
            return 1;
        } else {
            return -1;
        }
    }

    //this will sort the adjacencyList in alphabetical order meaning by first letter and if it contains a second letter
    //by second letter
    public static ArrayList<String> sortAtomList(ArrayList<String> atomList){
        Collections.sort(atomList, (Comparator<String>) searchDumb::compare);
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

    /*
    The brute force idea I have for this method is to simply create all possible permutations of the adjacency matrix
    that still satisfy the molecule arrangement and check if any of those matrices match the isomers we found in the
    findIDsSql method. This is an n! step for each isomer we have.
     */
    public static boolean isIsomorphic(moleculeChemSpider molecule1, moleculeChemSpider molecule2) {
        int[][] adj1 = molecule1.getAdjacencyMatrix();
        int[][] adj2 = molecule2.getAdjacencyMatrix();
        ArrayList<String> ato1 = molecule1.getAtomList();
        ArrayList<String> ato2 = molecule2.getAtomList();
        System.out.println(ato1.toString());
        //get all the repeating elements because they are the ones that dictate all the different mappings meaning if an
        //atom only occurs in the molecule once they all we have to do is replace it with the position that it was in the
        //original database molecule.
        ArrayList<Pair<Integer,Integer>> mappings = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();
        //note ato1 and ato2 should have the same size
        int position = 0;
        boolean found = true;
        ArrayList<Integer> used = new ArrayList<>();
        int i = 0;
        while(found == true && i < ato1.size()) {
            found = false;
            int j = 0;
            while(found == false && j < ato2.size()) {
                if (ato1.get(i).equals(ato2.get(j)) && (used.contains(j) == false)) {
                    int[] connections = adj1[i];
                    int[] connections2 = adj2[j];
                    ArrayList<String> atom_connections = new ArrayList<>();
                    ArrayList<String> atom_connections2 = new ArrayList<>();
                    for (int k = 0; k < connections.length; k++) {
                        if (connections[k] == 1) {
                            atom_connections.add(ato1.get(k));
                        }
                        if (connections2[k] == 1) {
                            atom_connections2.add(ato2.get(k));
                        }
                    }
                    sortAtomList(atom_connections);
                    sortAtomList(atom_connections2);
                    System.out.println("Here are the atom1 connections " + atom_connections.toString());
                    System.out.println("Here are the atom2 connections " + atom_connections2.toString());
                    int w = 0;
                    while (w < atom_connections.size()) {
                        if (atom_connections.get(w).equals(atom_connections2.get(w)) == false) {
                            found = false;
                        }
                        w++;
                    }
                    System.out.println("w " + w);
                    System.out.println("atom connections size " + atom_connections.size());
                    if (w == atom_connections.size()) {
                        System.out.println("hello");
                        used.add(j);
                        found = true;
                    }
                }
                j++;
            }
            if(found == false){
                return false;
            }
            i++;
        }

        return true;
    }

    /*
    This is the method that will create the permuted matrix while still keeping the connections the same i.e we can
    represent H2O with an adjacency list of : H H O and corresponding adjacency matrix or the adjacency list can be
    H O H with a corresponding adjacency matrix, the two graphs are isomorphic but matrices are different however if
    we permute the last H with O and shift the second adjacency matrix correspondigly then the two matrices of H H O and
    H O H should match exactly and this is the reason I want to create this permutation code. It is brute force though.
     */
    public int[][] matrixPermutation(int[][] adjacencyMatrix){
        return null;
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
        water_atom2.add("O");
        water_atom2.add("H");
        int[][] water_matrix2 = new int[3][3];
        water_matrix2[0][2] = 1;
        water_matrix2[0][1] = 1;
        water_matrix2[1][0] = 1;
        water_matrix2[2][0] = 1;
        moleculeChemSpider water = new moleculeChemSpider(water_matrix,water_atoms);
        moleculeChemSpider water2 = new moleculeChemSpider(water_matrix2,water_atom2);
        boolean ok = isIsomorphic(water,water2);
        System.out.println(ok);
        System.out.println(Arrays.deepToString(water.getAdjacencyMatrix()));
        System.out.println(water.getAtomList().toString());
    }
}

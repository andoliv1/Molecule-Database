package main;

import java.util.*;

public class searchDumb {

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
    public ArrayList<String> sortAtomList(ArrayList<String> atomList){
        Collections.sort(atomList, (Comparator<String>) searchDumb::compare);
        return atomList;
    }


    /*
    This method should parse the SQL database to see where are the isomers (molecules that have the same atoms but the
    atoms are possibly under different arrangements i.e different adjacency matrices) to the molecule we are trying to
    search for.
    Comment: I am not quite sure how to query the SQL database yet so I need to figure out how to do this.
     */

//    @Override
    public int[] findIDsSql(ArrayList<String> adjacencyList) {
        return new int[0];
    }

    /*
    The brute force idea I have for this method is to simply create all possible permutations of the adjacency matrix
    that still satisfy the molecule arrangement and check if any of those matrices match the isomers we found in the
    findIDsSql method. This is an n! step for each isomer we have.
     */
//    @Override
//    public boolean isIsomorphic(main.moleculeChemSpider molecule1, main.moleculeChemSpider molecule2) {
//        int[][] adj1 = molecule1.getAdjacencyMatrix();
//        int[][] adj2 = molecule2.getAdjacencyMatrix();
//        ArrayList<String> ato1 = molecule1.getAtomList();
//        ArrayList<String> ato2 = molecule2.getAtomList();
//
//        return false;
//    }

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
}

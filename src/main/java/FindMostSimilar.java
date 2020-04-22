package main.java;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import static java.lang.Math.abs;


/**
 * The following code implements a find most similar search for molecules. The structure of the code is the following:
 * rankZeroChange are denoted by all the molecules that are exactly isomorphic; rankOneChane denote any of the molecules
 * that make changes by relabelling the vertices in order to attain GI; rankTwoChange denotes any molecules that need a change of edges in the adjacency matrix
 * to attain GI; rankThreeChange denote any molecules that need a additional vertices to attain GI.
 *
 * Let the distance of two molecules be denoted delta:
 * delta(originalMolecule,rankZeroChange) = 0;
 * delta(originalMolecule,rankOneChange) = 1*(#of vertices changed);
 * delta(originalMolecule,rankTwoChange) = 4*(#of edges added or removed);
 * delta(originalMolecule,rankThreeChange) = 16*(#of vertices added with an edge configuration);
 *
 * Therefore:
 * delta(originalMolecule, anotherMolecule) = delta(originalMolecule,rankOneChange) + delta(originalMolecule,rankTwoChange)
 *  +delta(originalMolecule,rankThreeChange);
 *
 *
 * We theorized that this was a reasonable metric by looking back at the Data Structures that we used for GI. Getting the number of
 * different molecules only takes O(V) time hence the one multiplier in the rankOneChange. For adding edges we technically need to get two matrices and
 * how the atoms from one molecule should match the atoms for another, once that is done we can look at the minimal changes of edges we can make two have
 * the two molecules be GI. This is a lot more work that a rank one change so we decided to use the 4 multiplier in front of the rankTwoChange. For the rank three
 * change we decided to weigh it higher because it is the combination of a rankTwoChange and a rankOneChange.
 *
 * At the end of the code we use a min Heap data structure to store the mostSimilar molecules so that if the user wants
 * he can keep pulling the molecules until he finds one he likes (whatever that may be anyways).
 */
public class FindMostSimilar <T extends MoleculeAbstract>{
    private ArrayList<T> comparisonMolecules;
    private ArrayList<T> baseMolecules;
    public PriorityQueue<Pair<Integer,T>> minHeap;

    public FindMostSimilar(ArrayList<T> comparisonMolecules, ArrayList<T> baseMolecules) {
        this.comparisonMolecules = comparisonMolecules;
        this.baseMolecules = baseMolecules;
        this.minHeap = new PriorityQueue<>(new Comparator<Pair<Integer,T>>() {
            @Override
            public int compare(Pair<Integer,T> o1,Pair<Integer,T> o2) {
                return Integer.compare(o1.getKey(),o2.getKey());
            }});

        for (MoleculeAbstract compMol : comparisonMolecules) {
            int distance = 100000;
            int temp_distance = 0;
            for(MoleculeAbstract base : baseMolecules) {
                ArrayList<String> oldList = compMol.getAtomList();
                int rOneChange = rankOneChange(compMol, base);
                int rTwoChange = rankTwoChange(compMol, base);
                temp_distance = 1 * rOneChange + 4 * rTwoChange;
                int matrixTotal = getSumofMatrix(base.adjacencyMatrix);
                if(rTwoChange > matrixTotal){
                    rTwoChange = matrixTotal;
                }
                if(temp_distance < distance){

                    distance = temp_distance;
                    temp_distance = 0;
                }
                compMol.changeAtomList(oldList);
            }
            //System.out.println(distance + " for " + compMol.toString());
            minHeap.offer(new Pair(distance,compMol));
        }
    }
    public FindMostSimilar(ArrayList<T> comparisonMolecules, T base) {
        this.comparisonMolecules = comparisonMolecules;
        this.baseMolecules = baseMolecules;
        this.minHeap = new PriorityQueue<>(new Comparator<Pair<Integer,T>>() {
            @Override
            public int compare(Pair<Integer,T> o1,Pair<Integer,T> o2) {
                return Integer.compare(o1.getKey(),o2.getKey());
            }});

        for (MoleculeAbstract compMol : comparisonMolecules) {
            ArrayList<String> oldList = compMol.getAtomList();
            int rOneChange = rankOneChange(compMol, base);
            int rTwoChange = rankTwoChange(compMol, base);
            int matrixTotal = getSumofMatrix(base.adjacencyMatrix);
            if(rTwoChange > matrixTotal){
                rTwoChange = matrixTotal;
            }
            int distance = 1 * rOneChange + 4 * rTwoChange;
            compMol.changeAtomList(oldList);
            //System.out.println(distance + " for " + compMol.toString());
            minHeap.offer(new Pair(distance,compMol));
        }
    }

    public PriorityQueue<Pair<Integer,T>> getMinHeap(){
        return this.minHeap;
    }

    public Pair<Integer,T> extractNextClosest(){
        return this.minHeap.poll();
    }

    public void insertMore(MoleculeAbstract compMol){
        int distance = 1000000;
        int temp_distance = 0;
        for(MoleculeAbstract base : baseMolecules) {
            int rOneChange = rankOneChange(compMol, base);
            int rTwoChange = rankTwoChange(compMol, base);
            int matrixTotal = getSumofMatrix(base.adjacencyMatrix);
            if(rTwoChange > matrixTotal){
                rTwoChange = matrixTotal;
            }
            temp_distance = 1 * rOneChange + 4 * rTwoChange;
            if(temp_distance < distance){
                distance = temp_distance;
                temp_distance = 0;
            }
        }
        //System.out.println(distance + " for " + compMol.toString());
        minHeap.offer(new Pair(distance,compMol));
    }

    public static int rankOneChange(MoleculeAbstract compMol, MoleculeAbstract originalMolecule){
        ArrayList<String> inUseAtoms = compMol.getAtomList();
        //just want to check how many atoms
        String lastSeenVertex;
        ArrayList<Integer> used = new ArrayList<>();
        ArrayList<String> atom_changes = new ArrayList<>();
        int counter= 0;
        ArrayList<String> atomList = originalMolecule.getAtomList();
        for(int i = 0; i < atomList.size();i++){
            boolean found = false;
            String atom = atomList.get(i);
            //System.out.println("Trying " + atom);
            for(int j = 0 ; j < inUseAtoms.size(); j++){
                String atom2 = inUseAtoms.get(j);
                if(atom.equals(atom2) && used.contains(j) == false){
                    //System.out.println("Found atom" + atom2);
                    used.add(j);
                    //System.out.println(atom2);
                    found = true;
                    break;
                }
            }
            if(found == false){
                //System.out.println(atom);
                atom_changes.add(atom);
                counter++;
            }
        }
        counter = 0;
        //System.out.println(used.toString());
        for(String atom : atom_changes){
            if(used.contains(counter) == false){
                //System.out.println(atom);
                compMol.changeLabels(atom,counter);
                counter++;
            }
            else {
                while(used.contains(counter) == true && counter < inUseAtoms.size()){
                    counter++;
                }
                //System.out.println(atom);
                compMol.changeLabels(atom,counter);
                counter++;
            }
        }
        //System.out.println("Total Rank One Changes " + atom_changes.size());
        return atom_changes.size();
    }

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
        } else if (o2.getKey().length() > 1){
            return -1;
        }
        else{
            return 0;
        }
    }

    /**
     * This method will sort all the atoms in the atomList of a given Molecule according to the compareWithNumbers
     * function above
     * @param atomList: all atoms in the atom list of a molecule and consider the numbers each atom may have.
     * @return
     */
    public static ArrayList<Pair<String,Integer>> sortAtomListNumbers(ArrayList<Pair<String,Integer>> atomList){
        Collections.sort(atomList, (Comparator<Pair<String,Integer>>) FindMostSimilar::compareWithNumbers);
        return atomList;
    }


    public static ArrayList<Pair<Integer,Integer>> initialClosestCorrespondence(MoleculeAbstract molecule1, MoleculeAbstract molecule2) {
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
        ArrayList<Pair<Integer,Integer>> used = new ArrayList<>();
        int i = 0;
        //stop if a vertex from molecule1 wasn't matched to any of the vertices from molecule 2 or if all the vertices
        //were matched.
        while(i < ato1.size()) {
            found = false;
            int j = 0;
            //parse through all vertices of molecule2 to see if their connections matches to the connections in the vertex
            //i at molecule 1.
            //System.out.println("This is the atom we are trying to find a correspondence " + ato1.get(i) + " this is its index" + i);
            /**
             *Check this line later
             */
            int distance = 10000000;
            int temp_distance =1000000;
            Pair<Integer,Integer> to_be_used = new Pair<>(0,1000000);
            while(j < ato2.size()) {
                //System.out.println("This is the atom we are comparing " + ato2.get(j) + " this is its index" + j);
                //check if the vertex you are at in molecule2 has already been matched to a vertex in molecule1
                if (ato1.get(i).equals(ato2.get(j)) && (used.contains(j) == false)) {
                    temp_distance = 0;
                    //check the vertex connections
                    int[] connections = adj1[i];
                    int[] connections2 = adj2[j];
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
//                    System.out.println(atom_connections.toString());
//                    System.out.println(atom_connections2.toString());
                    //Get the overall distance between a row and another
                    int k = 0;
                    int k2 = 0;
                    while(k < atom_connections.size() || k2 < atom_connections2.size()){

                        if(k2 == atom_connections2.size()){
                            temp_distance += atom_connections.get(k).getValue();
                            k++;
                        }
                        else if(k == atom_connections.size()){
                            temp_distance += atom_connections2.get(k2).getValue();
                            k2++;
                        }
                        else if(compareWithNumbers(atom_connections.get(k),atom_connections2.get(k2)) == 0){

                            temp_distance += abs(atom_connections.get(k).getValue() - atom_connections2.get(k2).getValue());
                            k++;
                            k2++;
                        }
                        else{
                            //System.out.println(atom_connections.get(k).toString());
                            //System.out.println(atom_connections2.get(k2).toString());
                            if(compareWithNumbers(atom_connections.get(k),atom_connections2.get(k2)) > 0){

                                temp_distance += atom_connections2.get(k2).getValue();
                                k2++;
                            }
                            else {
                                //System.out.println(atom_connections.get(k).getValue());
                                temp_distance += atom_connections.get(k).getValue();
                                k++;
                            }
                        }
                    }

                    //if the connections are the same store the fact that you are using this vertex to match to vertex i
                    //in molecule 1 and won't be using to describe other vertices in molecule1 even if they have the same
                    //atom connections
                }
                if(distance > temp_distance && used.contains(j) == false){
                    distance = temp_distance;
//                    System.out.println("This is their distance");
//                    System.out.println(temp_distance);
                    Pair<Integer,Integer> to_be_used_new = new Pair<>(j,temp_distance);
                    to_be_used = to_be_used_new;
                }
                j++;
            }

            used.add(to_be_used);
            i++;
        }
        //if we checked every vertex and could find a mapping from a vertex i in molecule 1 to a vertex j in molecule 2
        //then the molecules are isomorphic so return true.
        return used;
    }

    public static int rankTwoChange(MoleculeAbstract compMol,MoleculeAbstract originalMolecule){
        int changes = 0;
        if(originalMolecule.getAtomList().size() == compMol.getAtomList().size()) {
            ArrayList<Pair<Integer,Integer>> closestCorrespondence = initialClosestCorrespondence(compMol,originalMolecule);
            for(Pair<Integer,Integer> dist : closestCorrespondence){
                //System.out.println(dist.getValue());
                changes += dist.getValue();
            }
        }
        //System.out.println("Total Rank Two Changes " + changes);
        return changes;
    }

    public int getSumofMatrix(int[][] adj){
        int sum = 0;
        for(int i = 0; i < adj.length; i++){
            for(int j =0; j < adj.length; j++){
                sum += adj[i][j];
            }
        }
        return sum;
    }

    public static void main(String[] args){

    }
}

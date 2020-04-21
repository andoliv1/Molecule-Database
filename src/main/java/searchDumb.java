package main.java;

//Group9: Molecular Database
/*
Instructions: the code is well commented and by running main you should see the tests we have created to check our
isomorphism algorithm.
 */
import javafx.util.Pair;
import java.util.*;

public class searchDumb{

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
        } else if(o2.getKey().length() > 1){
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
        Collections.sort(atomList, (Comparator<Pair<String,Integer>>) searchDumb::compareWithNumbers);
        return atomList;
    }


    /**
     * Here we want to generate all possible isomorphism mappings and verify that one of them work for the molecule. The int[] a holds
     * the numbers that need to be permuted and the int[] indices hold where the permuted numbers will be appended to the atom.
     * The concept is better explained in the function verify_rigorous isomorphism
     */
    public static boolean generate(int n, int[] a, MoleculeAbstract molecule1, MoleculeAbstract molecule2, int[] indices,Deque<MoleculeText> solutions) {
        // Placeholder for swapping values
        int tmp;
        // If a new permutation has been found then change the respective indices of the vertices that needed to have a key assigned to it
        boolean isIso = false;
        if(n == 1) {
            //Change the labels of the respective vertices
            MoleculeText newMolecule = new MoleculeText(molecule1) ;
            for(int i = 0; i < indices.length; i++){
                String str = newMolecule.atoms.get(indices[i]);
                str = str.replaceAll("[0-9]","");
                str += a[i];
                newMolecule.changeLabels(str,indices[i]);
            }
            //call the isomorphic function on the new molecules
            isIso = isIsomorphicWithNumbers( newMolecule,  molecule2);
            //if the resulting permutation results in an Isomorphism than it means the two molecules are isomorphic
            /**
             * This right here is the part I am having trouble with. Whenever I create a different labeling of a molecule that is
             * isomorphic and add it to the solutions object than the molecules that are currently in the solutions object get changed
             * all to be the same molecule which is the new molecule I created. PLEASE HELP LOL.
             */
            if(isIso){
                solutions.push(newMolecule);
                return true;
            }
            else{
                return false;
            }
        }
        else {	// If a new permutation has not yet been found
            for(int i = 0; i < (n-1); i++) {
                if(generate(n - 1, a, molecule1, molecule2, indices, solutions)){
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
            if(generate(n - 1, a, molecule1, molecule2, indices, solutions)){
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
    public static ArrayList<Integer> verify_rigorous_isomorphism(MoleculeAbstract molecule1, MoleculeAbstract molecule2){
        int[][] adj1 = molecule1.getAdjacencyMatrix();
        int[][] adj2 = molecule2.getAdjacencyMatrix();
        //get the molecules vertex list
        ArrayList<String> immutable_list = molecule1.getAtomList();
        ArrayList<String> immutable_list2 = molecule2.getAtomList();
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
                if(this_adj[k] != 0){
                    build_adj.add(ato1.get(k));
                }
            }
//            System.out.println(build_adj.toString());
            //if the atom is an ambiguous atom then we want to copy its index and put it in the duplicates list
            if(atoms.contains(atom)){
                //System.out.println("This is dup " + atom);
                if(checkStringAndArrayList(build_adj,atom,atom_connections)){
                    duplicates.add(i);
                }
            }
            //else we want to store it for finding future atoms that might be ambiguous to this atom
            else{
                //System.out.println("This is not dup " + atom);
                atoms.add(atom);
                atom_connections.add(new Pair(atom,build_adj));
            }
        }
        //From (Beginning) to (End) this just finds the initial atom that generated the ambiguous atoms in the duplicate array. So for each distinct atom in the duplicates array there is a prior
        //atom that resulted in the atom being ambiguous this block is just finding that atom.
        //(Beginning)
//        System.out.println(duplicates.toString() + " this is duplicates");
        HashSet<Integer> duplicates_final  = new HashSet<>();
        for(Integer dup : duplicates){
            int[] connec = adj1[dup];
            int counter= 0;
            boolean found = false;
            duplicates_final.add(dup);
            while(true) {
                if (ato1.get(counter).equals(ato1.get(dup))) {
                    int[] connec2 = adj1[counter];
                    ArrayList<Pair<String, Integer>> atom_connec = new ArrayList<>();
                    ArrayList<Pair<String, Integer>> atom_connec2 = new ArrayList<>();
                    for (int k = 0; k < connec.length; k++) {
                        if (connec[k] >= 1) {
                            atom_connec.add(new Pair<>(ato1.get(k), connec[k]));
                        }
                        if (connec2[k] >= 1) {
                            atom_connec2.add(new Pair<>(ato1.get(k), connec2[k]));
                        }
                    }
                    //sort both atom connections
                    sortAtomListNumbers(atom_connec);
                    sortAtomListNumbers(atom_connec2);

                    int w = 0;
                    while (w < atom_connec.size() && w < atom_connec2.size()) {
                        //if the connections are not the same then we don't want to use the vertex
                        if (!atom_connec.get(w).getKey().equals(atom_connec2.get(w).getKey()) &&
                                !atom_connec.get(w).getValue().equals(atom_connec2.get(w).getValue())) {
                            break;
                        }
                        w++;
                    }
                    if (w == atom_connec.size() && w == atom_connec2.size()) {
                        found = true;
                    }

                }
                if (!duplicates_final.contains(counter) && found) {
//                    System.out.println("ok");
                    duplicates_final.add(counter);
                    break;
                }
                else if (found){
                    break;
                }
                else{
                    counter++;
                }

            }

        }
        //(End)

        //Create the correspondence described by the function description
        ArrayList<Integer> initialCorrespondence = initialCorrespondence(molecule2,molecule1);
        int[] duplicates_to_array = new int[duplicates_final.size()];
        int counter = 0;
        for(Integer dup : duplicates_final){
            duplicates_to_array[counter] = dup;
            counter++;
        }
        //make all specific bijections and check if any of them are isomorphic
        int[] indices = new int[duplicates_to_array.length];
        HashSet<Integer> all_dup =  new HashSet<>();
        for(int i = 0; i < duplicates_to_array.length; i++){
            indices[i] = duplicates_to_array[i];
            all_dup.add(duplicates_to_array[i]);
        }
        HashSet<Integer> build_dup = new HashSet<>();
        ArrayList<String> atoms_to_make_sure_not_duplicate = new ArrayList<>();
        boolean isomorphic = false;
        //ListIterator<MoleculeAbstract> molecule = molecules_valid.currentSolutions.listIterator();
        Deque<MoleculeText> moleculeStack = new ArrayDeque<>();
        moleculeStack.add(new MoleculeText(molecule1));
        while(!moleculeStack.isEmpty()){
            /**
             * The following steps go until the "END"
             * The idea of the lines below:
             * 1) Isolate one group of ambiguous atoms that is H or (exclusive or) O, ... (any given atom in the molecule
             * that has ambiguous atoms)
             * 2) Create labels for the group of ambiguous atoms that you selected.
             * 3) After you create labels you will create an initial correspondence between molecules 1 and 2
             * 4) Change the molecules atoms list so they can include the correspondence
             */
            MoleculeText tempMolecule = moleculeStack.pop();
            ArrayList<Integer> a_list = new ArrayList<>();
            int i;
            for(i = 0; i < duplicates_to_array.length; i++) {
                boolean found = false;
                for (String atom : tempMolecule.getAtomList()) {
                    if (atom.indexOf(Integer.toString(duplicates_to_array[i])) != -1) {
                        found = true;
                        break;
                    }
                }
                if (found == false) {
                    break;
                }
            }
            a_list.add(duplicates_to_array[i]);
            String atom = tempMolecule.getAtomList().get(duplicates_to_array[i]);
            String atom_temp = new String(atom);
            atom_temp = atom_temp.replaceAll("[0-9]", "");
            for (int j = 0; j < ato1.size(); j++) {
                String str2 = ato1.get(j);
                str2 = str2.replaceAll("[0-9]", "");
                if (str2.equals(atom_temp) && j != duplicates_to_array[i] && duplicates_final.contains(j)) {
                    a_list.add(j);
                }
            }
            int[] a = new int[a_list.size()];
            int counter5 = 0;
            for (Integer a_el : a_list) {
                a[counter5] = a_el;
                counter5++;
            }
            int[] indices_dis = new int[a.length];
            for (int k = 0; k < a.length; k++) {
                indices_dis[k] = a[k];
            }

            for (int k = 0; k < a.length; k++) {
                String newLabel = (String) tempMolecule.getAtomList().get(a[k]) + a[k];
                tempMolecule.changeLabels(newLabel, a[k]);
                molecule2.changeLabels(newLabel, initialCorrespondence.indexOf(a[k]));
            }

            /**
             * End
             */

            /**
             * Now we want to be able to permutate the atoms that are ambiguous and so we call the function generate
             * which takes in the following important parameters:
             * a - this is the array of numbers (which corresponds to the indices in molecule 1 that have ambiguous atoms of one kind (and to be clear one kind meaning only one atom))
             * tempMolecule - molecule that will be permuted
             * molecule2 - a unique unchanged molecule
             * indices_dis - This is actually the same as parameter "a"  but this array won't be permuted and will serve to hold the spots that are permuted
             * molecules_valid - This is an object of type Solutions, every time the generate function finds a suitable solution given the permuted labels
             * it adds the solutions to the hashSet contained in molecules_valid.
             * After it adds all valid solutions then the HashSet grows (n/c)! in size worst case.
             * Also please refer to line (157) to see where my current problem is.
             */
            if(!hashSetsEqual(build_dup,all_dup)){
                for(int l = 0; l < indices_dis.length; l++){
                    build_dup.add(indices_dis[l]);
                }
            }
            isomorphic = generate(a.length, a, tempMolecule, molecule2, indices_dis, moleculeStack);
            if(hashSetsEqual(build_dup,all_dup)){
                if(isomorphic){
                    MoleculeText tempMolecule_prime = new MoleculeText(moleculeStack.pop());
                    ArrayList<Integer> final_correspondence = initialCorrespondence(molecule2,tempMolecule_prime);
                    molecule1.changeAtomList(immutable_list);
                    molecule2.changeAtomList(immutable_list2);
                    System.out.println("This is the bijection " + final_correspondence.toString());
                    return final_correspondence;
                }
            }
        }
        //generate(duplicates_to_array.length,indices, molecule1, molecule2, duplicates_to_array,break_out);
        molecule1.changeAtomList(immutable_list);
        molecule2.changeAtomList(immutable_list2);
        return null;
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
                    sortAtomListNumbers(atom_connections);
                    sortAtomListNumbers(atom_connections2);
                    int w = 0;
                    while (w < atom_connections.size() && w < atom_connections2.size()) {
                        //if the connections are not the same then we don't want to use the vertex
                        if (atom_connections.get(w).getKey().equals(atom_connections2.get(w).getKey()) == false ||
                                atom_connections.get(w).getValue().equals(atom_connections2.get(w).getValue()) == false) {
                            found = false;
                            w--;
                            break;
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
                    sortAtomListNumbers(atom_connections);
                    sortAtomListNumbers(atom_connections2);
                    int w = 0;
                    while (w < atom_connections.size() && w < atom_connections2.size()) {
                        //if the connections are not the same then we don't want to use the vertex
                        if (!atom_connections.get(w).getKey().equals(atom_connections2.get(w).getKey()) ||
                                !atom_connections.get(w).getValue().equals(atom_connections2.get(w).getValue())) {
                            found = false;
                            break;
                        }
                        else {
                            w++;
                        }
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
            if(!found){
                return false;
            }
            i++;
        }
        return true;
    }

    public static boolean hashSetsEqual(HashSet<Integer> build_dup, HashSet<Integer> all_dup){
        Iterator<Integer> iter = all_dup.iterator();
        while (iter.hasNext()){
            Integer element = iter.next();
            if(!build_dup.contains(element)){
                return false;
            }
        }
        return true;
    }

    public static boolean checkStringAndArrayList(ArrayList<String> list, String str, HashSet<Pair<String,ArrayList<String>>> connections){
        Iterator<Pair<String,ArrayList<String>>> iter = connections.iterator();
        while (iter.hasNext()){
            Pair<String,ArrayList<String>> base = iter.next();
            if(base.getKey().equals(str)){
                boolean found = true;
                for(String str2 : base.getValue()){
                    if(!list.contains(str2)){
                        found = false;
                    }
                }
                for(String str2 : list){
                    if(!base.getValue().contains(str2)){
                        found = false;
                    }
                }
                if(found == true){
                    return true;
                }
            }
        }
        return false;
    }
}

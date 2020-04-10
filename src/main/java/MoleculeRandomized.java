package main.java;


import java.util.*;

public class MoleculeRandomized extends MoleculeAbstract{

    Random rand = new Random();
    MoleculeRandomized() {
        this.numVertices = rand.nextInt(10000) + 1000; //20 - 70 vertices

        this.adjacencyList = new LinkedList[this.numVertices];
        this.adjacencyMatrix = new int[this.numVertices][this.numVertices];
        for (int ii = 0; ii<this.numVertices; ii++){
            this.adjacencyList[ii] = new LinkedList<>();
        }
        randomAtoms();
        randomEdges();
        makeConnections();
        this.moleculeName = generateName();
    }

    /**
     * Select random atoms for this molecule.
     * Tried to make probability distribution similar to this graph
     * http://www.astronoo.com/en/articles/abundance-of-the-elements.html
     */
    private void randomAtoms(){
        this.atoms = new ArrayList<>(this.numVertices);

        for(int i =0 ; i < numVertices; i++){

            // Randomly select an atom
            int prob = rand.nextInt(99); // 0-99 probability
            // HYGROGEN
            if(prob < CDF[0]){
                atoms.add(periodicTable[0]);
            }
            // HELIUM
            else if (prob >= CDF[0] && prob < CDF[1])
                atoms.add(periodicTable[1]);
            // Hydrogen
            else if (prob >= CDF[1] && prob < CDF[2])
                atoms.add(periodicTable[2]);
            // Lithium
            else if (prob >= CDF[2] && prob < CDF[3])
                atoms.add(periodicTable[3]);
            // Beryllium
            else if (prob >= CDF[3] && prob < CDF[4])
                atoms.add(periodicTable[4]);
            // Carbon
            else if (prob >= CDF[4] && prob < CDF[5])
                atoms.add(periodicTable[5]);
            //Nitrogen
            else if (prob >= CDF[5] && prob < CDF[6])
                atoms.add(periodicTable[6]);
            // Oxygen
            else if (prob >= CDF[6] && prob < CDF[7])
                atoms.add(periodicTable[7]);
            // Fluoride
            else if (prob >= CDF[7] && prob < CDF[8])
                atoms.add(periodicTable[8]);

            //OTHER ATOMS
            else{
                int randomAtomIndex = rand.nextInt(periodicTable.length - 9) + 9;
                atoms.add(periodicTable[randomAtomIndex]);
            }
        }
    }

    private void randomEdges(){
        for(int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                // Check for different vertices
                // Add edge with probability 0.5
                if (i!=j && rand.nextInt(10) > 5) {
                    addEdge(i,j);
                }
            }
        }
    }

    /**
     * Create a name based on the elements in the molecule.
     * @return string
     */
    private String generateName(){
        HashMap<String, Integer> hm = new HashMap<>();
        for (String atom : atoms){
            hm.merge(atom, 1, Integer::sum);
        }

        // Create some name like H4C2
        StringBuilder sb = new StringBuilder();
        for (String atom : hm.keySet()){
            sb.append(atom);
            sb.append(hm.get(atom));
        }
        return sb.toString().substring(0,10);
    }

    /**
     * Graph traversal - to be used for checking a connected graph
     * @param vertex
     * @param visited
     */
    private void DFS(int vertex, boolean[] visited){
        visited[vertex] = true;
        for(int i = 0; i < numVertices; i++){
            if(!visited[i] && adjacencyMatrix[vertex][i] != 0){
                DFS(i, visited);
            }
        }
    }

    /**
     * Connects disconnected graphs together
     * @return bool
     */
    private boolean makeConnections(){
        boolean[] visited = new boolean[numVertices];

        DFS(0, visited);

        int count = 0;
        // If the vertex has not been visited yet, that means it was disconnected
        for(int i = 0; i < numVertices; i++){
            if(!visited[i]){
                // Connect the node that was not connected
                if (i==0)
                    addEdge(i, i+1);
                else
                    addEdge(i, i-1);
            } else
                count++;
        }
        // There were some disconnected components
        // Recursively call makeConnections to check the graph is connected
        if (count != numVertices)
            return makeConnections();
        return true;
    }

    private void addEdge(int i, int j){
        this.adjacencyList[i].add(j);
        this.adjacencyMatrix[i][j] += 1;
        this.adjacencyMatrix[j][i] += 1;
    }

    @Override
    public String getMoleculeName() {
        return this.moleculeName;
    }

    @Override
    public Integer getNumVertices() {
        return this.numVertices;
    }

    @Override
    public LinkedList<Integer>[] getAdjacencyList() {
        return this.adjacencyList;
    }

    @Override
    public int[][] getAdjacencyMatrix() {
        return this.adjacencyMatrix;
    }

    @Override
    public ArrayList<String> getAtomList() {
        return this.atoms;
    }

    @Override
    public boolean changeLabels(String newlabel, int index){
        this.atoms.set(index, newlabel);
        return true;
    }

    public boolean changeAtomList(ArrayList<String> newList){
        this.atoms = newList;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(atoms.toString());
        representation.append("/n");
        representation.append(Arrays.deepToString(adjacencyMatrix));
        return representation.toString();
    }

    public final static String[] periodicTable = new String[]{  "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na",
                                                                "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca", "Sc", "Ti",
                                                                "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge",
                                                                "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru",
                                                                "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Cs", "Ba",
                                                                "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho",
                                                                "Er", "Tm", "Yb", "Lu", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt",
                                                                "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac",
                                                                "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm",
                                                                "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg",
                                                                "Cn", "Nh", "Fl", "Mc", "Lv", "Ts", "Og" };

                                                    //H  HE  Li  Be  B   C   N   O   F
    public final static double[] CDF = new double[]{25, 40, 44, 46, 50, 60, 70, 80, 85 };
    public static void main(String[] args) {
        MoleculeRandomized m = new MoleculeRandomized();
        System.out.println(m.getMoleculeName());
    }
}

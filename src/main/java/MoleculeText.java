package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class MoleculeText extends MoleculeAbstract {

    //constructor with only file name
    public MoleculeText(String filename){
        parseFile(filename);
    }

    //constructor that takes adjacency matrix and atoms
    public MoleculeText(int[][] matrix, ArrayList<String> vertices){
        this.adjacencyMatrix = matrix;
        this.atoms = vertices;
    }

    public MoleculeText(MoleculeAbstract mol){
        this.adjacencyMatrix = mol.adjacencyMatrix;
        this.atoms = mol.atoms;
    }

    public void parseFile(String filename) {
        try {
            System.out.println("Parsing...");
            BufferedReader br = new BufferedReader(new FileReader(filename));
            // First line is always the molecule name
            this.moleculeName = br.readLine();
            System.out.println(moleculeName);

            // Second line is always the number of vertices
            this.numVertices = Integer.parseInt(br.readLine());
            System.out.println(numVertices);
            // The next numVertices line are the atoms in the molecule
            this.atoms = new ArrayList<>(this.numVertices);
            this.adjacencyList = new LinkedList[this.numVertices];
            for (int ii = 0; ii<this.numVertices; ii++){
                this.atoms.add(br.readLine().trim());
                System.out.println(this.atoms.get(ii));
                this.adjacencyList[ii] = new LinkedList<>();
            }

            // Remainder of file is the adjacency list
            // init adjacency matrix nxn
            this.adjacencyMatrix = new int[this.numVertices][this.numVertices];
            String st;
            String[] delimited;
            int vertex1, vertex2;
            while ((st = br.readLine()) != null){
                delimited = st.split("\\W+");
                try {
                    vertex1 = Integer.parseInt(delimited[0]);
                    vertex2 = Integer.parseInt(delimited[1]);


                    this.adjacencyList[vertex1].add(vertex2);
                    this.adjacencyMatrix[vertex1][vertex2] += 1;
                    this.adjacencyMatrix[vertex2][vertex1] += 1;
                }
                catch(NumberFormatException e){
                    System.out.println("End of connection list");
                }

            }
            System.out.println("Adjacency List");
            for (int ii = 0; ii<this.numVertices; ii++){
                System.out.print("Vertex " + ii + ":\t");
                for (int s : adjacencyList[ii]) {
                    System.out.print(s +"\t");
                }
                System.out.println();
            }

            System.out.println("Adjacency Matrix");
            for (int ii = 0; ii<this.numVertices; ii++){
                for (int jj = 0; jj<this.numVertices; jj++){
                    System.out.print(adjacencyMatrix[ii][jj] +"\t");
                }
                System.out.println();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public String getMoleculeName(){
        return moleculeName;
    }

    public Integer getNumVertices(){
        return numVertices;
    }

    public ArrayList<String> getAtomList(){
        ArrayList<String> atoms_ret = new ArrayList<>(atoms);
        return atoms_ret;
    }

    public LinkedList<Integer>[] getAdjacencyList(){
        return adjacencyList;
    }

    public int[][] getAdjacencyMatrix(){
        return adjacencyMatrix;
    }

    public boolean changeLabels(String newlabel, int index){
        atoms.set(index,newlabel);
        return true;
    }

    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(atoms.toString());
        representation.append("/n");
        representation.append(Arrays.deepToString(adjacencyMatrix));
        return representation.toString();
    }

    public boolean changeAtomList(ArrayList<String> newList){
        this.atoms = newList;
        return true;
    }

    public static void main(String[] args) {
//        main.java.MoleculeText m = new main.java.MoleculeText("butane.txt");

    }
}

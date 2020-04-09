package main.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class MoleculeDB extends MoleculeAbstract {

    MoleculeDB(int mid, String name, int numVertices){
        this.mid = mid;
        this.moleculeName = name;
        this.numVertices = numVertices;
        this.atoms = new ArrayList<>(Arrays.asList(new String[numVertices]));
        this.adjacencyList = new LinkedList[numVertices];
        for (int ii = 0; ii<this.numVertices; ii++){
            this.adjacencyList[ii] = new LinkedList<>();
        }
        this.adjacencyMatrix = new int[numVertices][numVertices];
    }

    public String getMoleculeName(){
        return moleculeName;
    }
    public Integer getNumVertices(){
        return numVertices;
    }
    public ArrayList<String> getAtomList(){
        return atoms;
    }
    public LinkedList<Integer>[] getAdjacencyList(){
        return adjacencyList;
    }
    public int[][] getAdjacencyMatrix(){
        return adjacencyMatrix;
    }

    public void setMoleculeName(String moleculeName){
        this.moleculeName = moleculeName;
    }

    public void setNumVertices(Integer numVertices){
        this.numVertices = numVertices;
    }

    public void setAtom(int vertex, String atom){
        this.atoms.set(vertex, atom);
    }

    public void setEdge(int vertex1, int vertex2){
        this.adjacencyList[vertex1].add(vertex2);
        this.adjacencyMatrix[vertex1][vertex2]++;
        this.adjacencyMatrix[vertex2][vertex1]++;
    }
    public boolean changeLabels(String newlabel, int index){
        ArrayList<String> newAtomList = new ArrayList<>();
        for(int i = 0; i < atoms.size(); i++){
            if(i != index){
                newAtomList.add(atoms.get(i));
            }
            else{
                newAtomList.add(newlabel);
            }
        }
        atoms = newAtomList;
        return true;
    }

    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(atoms.toString());
        representation.append("/n");
        representation.append(Arrays.deepToString(adjacencyMatrix));
        return representation.toString();
    }

}

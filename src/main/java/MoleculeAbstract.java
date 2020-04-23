package main.java;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class MoleculeAbstract {
    protected Integer mid;
    public String moleculeName;
    public Integer numVertices;
    protected ArrayList<String> atoms;
    protected LinkedList<Integer>[] adjacencyList;
    protected  ArrayList<Integer> bijection;
    protected int[][] adjacencyMatrix;

    public abstract String getMoleculeName();
    public abstract Integer getNumVertices();
    public abstract LinkedList<Integer>[] getAdjacencyList();
    public abstract int[][] getAdjacencyMatrix();
    public abstract ArrayList<String> getAtomList();
    public abstract boolean changeLabels(String str, int index);
    public abstract String toString();
    public abstract boolean changeAtomList(ArrayList<String> newList);
}

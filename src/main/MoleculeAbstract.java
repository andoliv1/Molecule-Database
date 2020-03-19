package main;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class MoleculeAbstract {
    protected Integer mid;
    protected String moleculeName;
    protected Integer numVertices;
    protected ArrayList<String> atoms;
    protected LinkedList<Integer>[] adjacencyList;
    protected int[][] adjacencyMatrix;

    public abstract String getMoleculeName();
    public abstract Integer getNumVertices();
    public abstract LinkedList<Integer>[] getAdjacencyList();
    public abstract int[][] getAdjacencyMatrix();
    public abstract ArrayList<String> getAtomList();
}

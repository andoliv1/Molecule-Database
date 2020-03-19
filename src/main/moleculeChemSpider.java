package main;

import java.util.ArrayList;

public class moleculeChemSpider {

    private int[][] adjacencyMatrix;
    private ArrayList<String> atomList;

//    @Override
//    public int[][] getAdjacencyMatrix() {
//        return this.adjacencyMatrix;
//    }
//
//    @Override
//    public ArrayList<String> getAtomList() {
//        return this.atomList;
//    }

//    @Override
//    public Object convertToFormat(String encoding) {
//        return null;
//    }

    public moleculeChemSpider(int[][] adjacencyMatrix, ArrayList<String> atomList){
        this.adjacencyMatrix = adjacencyMatrix;
        this.atomList = atomList;
    }
}

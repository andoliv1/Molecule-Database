import java.util.ArrayList;

public class molecule{

    private int[][] adjacencyMatrix;
    private ArrayList<String> atomList;

    public int[][] getAdjacencyMatrix() {
        return this.adjacencyMatrix;
    }

    public ArrayList<String> getAtomList() {
        return this.atomList;
    }

    public Object convertToFormat(String encoding) {
        return null;
    }

    public molecule(int[][] adjacencyMatrix, ArrayList<String> atomList){
        this.adjacencyMatrix = adjacencyMatrix;
        this.atomList = atomList;
    }
}

import java.util.ArrayList;

public abstract class moleculeAbstract {
    public abstract int[][] getAdjacencyMatrix();

    public abstract ArrayList<String> getAtomList();

    public abstract Object convertToFormat(String encoding);
}

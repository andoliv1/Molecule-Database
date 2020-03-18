import java.util.ArrayList;

public interface Search {

    //This method should return the section of our SQL Database that the molecule can be in
    public static int[] findIDsSql(ArrayList<String> adjacencyList) {
        return new int[0];
    }

    //this method should return whether the two molecules are isomorphic
    public static boolean isIsomorphic(moleculeChemSpider molecule1, moleculeChemSpider molecule2) {
        return false;
    }

}

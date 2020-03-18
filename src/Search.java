import java.util.ArrayList;

public interface Search {

    //This method should return the section of our SQL Database that the molecule can be in
    public int[] findIDsSql(ArrayList<String> adjacencyList);

    //this method should return whether the two molecules are isomorphic
    public boolean isIsomorphic(moleculeChemSpider molecule1, moleculeChemSpider molecule2);

}

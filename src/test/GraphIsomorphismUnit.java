package test;

import main.java.MoleculeText;
import main.java.searchDumb;
import org.junit.Test;

import java.util.ArrayList;

public class GraphIsomorphismUnit {

    @Test
    public void GI() {
        ArrayList<String> water_atoms = new ArrayList<String>();
        water_atoms.add("H");
        water_atoms.add("H");
        water_atoms.add("O");
        int[][] water_matrix = new int[3][3];
        water_matrix[0][2] = 1;
        water_matrix[1][2] = 1;
        water_matrix[2][0] = 1;
        water_matrix[2][1] = 1;
        ArrayList<String> water_atom2 = new ArrayList<String>();
        water_atom2.add("O");
        water_atom2.add("H");
        water_atom2.add("H");
        int[][] water_matrix2 = new int[3][3];
        water_matrix2[0][2] = 1;
        water_matrix2[0][1] = 1;
        water_matrix2[1][0] = 1;
        water_matrix2[2][0] = 1;
        MoleculeText water = new MoleculeText(water_matrix,water_atoms);
        MoleculeText water2 = new MoleculeText(water_matrix2,water_atom2);
        boolean weakIso = searchDumb.isIsomorphicWithNumbers(water, water2);
        assert (weakIso);

        boolean strongIso = searchDumb.verify_rigorous_isomorphism(water, water2);
        assert(strongIso);
    }
}

package test;
import javafx.util.Pair;
import org.junit.Test;

import main.java.FindMostSimilar;
import main.java.MoleculeText;

import java.util.ArrayList;

public class FindMostSimilarUnitTest {
    @Test
    public void FM(){
        MoleculeText carbon_dioxide = new MoleculeText("carbon_dioxide.txt");
        MoleculeText water = new MoleculeText("water.txt");
        MoleculeText weird_mol1 = new MoleculeText("weirdmol1.txt");
        ArrayList<MoleculeText> base_mols = new ArrayList<>();
        base_mols.add(carbon_dioxide);
        base_mols.add(water);
        FindMostSimilar fd = new FindMostSimilar(base_mols, weird_mol1);
        System.out.println("First extraction");
        Pair<Integer,MoleculeText> first_extract = fd.extractNextClosest();
        System.out.println(first_extract.getValue().toString());
        assert(first_extract.getKey()==35);
        System.out.println("Second Extraction");
        Pair<Integer,MoleculeText> second_extract = fd.extractNextClosest();
        System.out.println(second_extract.getValue().toString());
        assert(second_extract.getKey()==35);
    }
}

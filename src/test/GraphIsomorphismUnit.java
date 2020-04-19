package test;

import main.java.MoleculeText;
import main.java.searchDumb;
import org.junit.Test;

import java.util.ArrayList;

import static main.java.searchDumb.isIsomorphicWithNumbers;
import static main.java.searchDumb.verify_rigorous_isomorphism;

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
        boolean weakIso = isIsomorphicWithNumbers(water, water2);
        assert (weakIso);
        boolean strongIso = verify_rigorous_isomorphism(water, water2);
        assert(strongIso);

        ArrayList<String> salt_atoms = new ArrayList<String>();
        salt_atoms.add("Na");
        salt_atoms.add("Cl");
        int[][] salt_matrix = new int[2][2];
        salt_matrix[0][1] = 1;
        salt_matrix[1][0] = 1;
        ArrayList<String> salt_atoms2 = new ArrayList<String>();
        salt_atoms2.add("Na");
        salt_atoms2.add("K");
        int[][] salt_matrix2 = new int[2][2];
        salt_matrix2[0][1] = 1;
        salt_matrix2[1][0] = 1;
        MoleculeText salt_mol = new MoleculeText(salt_matrix,salt_atoms);
        MoleculeText salt_mol2 = new MoleculeText(salt_matrix2,salt_atoms2);
        System.out.println("Molecule 1 name: salt_mol");
        System.out.println("representation: ");
        System.out.println(salt_mol.toString());
        System.out.println("Molecule 2 name: salt_mol2");
        System.out.println("representation: ");
        System.out.println(salt_mol2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?");
        boolean weakIsomorphic = isIsomorphicWithNumbers(salt_mol,salt_mol2);
        assert(!weakIsomorphic);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            boolean isomorphic = verify_rigorous_isomorphism(salt_mol,salt_mol2);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }

        ArrayList<String> methylene_atoms = new ArrayList<String>();
        methylene_atoms.add("C");
        methylene_atoms.add("H");
        methylene_atoms.add("H");
        int[][] methylene_matrix = new int[3][3];
        methylene_matrix[0][1] = 1;
        methylene_matrix[0][2] = 1;
        methylene_matrix[1][0] = 1;
        methylene_matrix[2][0] = 1;
        MoleculeText methylene_mol = new MoleculeText(methylene_matrix,methylene_atoms);
        boolean isomorphic3 = isIsomorphicWithNumbers(water2,methylene_mol);
        System.out.println("Are water and methylene isomorphic? " + isomorphic3);

        ArrayList<String> carbon_dioxide_atoms = new ArrayList<String>();
        carbon_dioxide_atoms.add("C");
        carbon_dioxide_atoms.add("O");
        carbon_dioxide_atoms.add("O");
        int[][] carbon_dioxide_matrix = new int[3][3];
        carbon_dioxide_matrix[0][1] = 2;
        carbon_dioxide_matrix[0][2] = 2;
        carbon_dioxide_matrix[1][0] = 2;
        carbon_dioxide_matrix[2][0] = 2;
        MoleculeText carbon_dioxide_mol = new MoleculeText(carbon_dioxide_matrix,carbon_dioxide_atoms);

        ArrayList<String> carbon_dioxide_atoms2 = new ArrayList<String>();
        carbon_dioxide_atoms2.add("C");
        carbon_dioxide_atoms2.add("C");
        carbon_dioxide_atoms2.add("O");
        int[][] carbon_dioxide_matrix2 = new int[3][3];
        carbon_dioxide_matrix2[0][1] = 2;
        carbon_dioxide_matrix2[0][2] = 2;
        carbon_dioxide_matrix2[1][0] = 2;
        carbon_dioxide_matrix2[2][0] = 2;
        MoleculeText carbon_dioxide_mol2 = new MoleculeText(carbon_dioxide_matrix2,carbon_dioxide_atoms2);
        System.out.println("Molecule 1 name: carbon_dioxide_mol");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol.toString());
        System.out.println("Molecule 2 name: carbon_dioxide_mol2");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        weakIsomorphic = isIsomorphicWithNumbers(carbon_dioxide_mol,carbon_dioxide_mol2);
        assert(!weakIsomorphic);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            boolean isomorphic = verify_rigorous_isomorphism(carbon_dioxide_mol,carbon_dioxide_mol2);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }

        ArrayList<String> carbon_dioxide_atoms3 = new ArrayList<String>();
        carbon_dioxide_atoms3.add("O");
        carbon_dioxide_atoms3.add("O");
        carbon_dioxide_atoms3.add("C");
        int[][] carbon_dioxide_matrix3 = new int[3][3];
        carbon_dioxide_matrix3[0][2] = 2;
        carbon_dioxide_matrix3[1][2] = 2;
        carbon_dioxide_matrix3[2][0] = 2;
        carbon_dioxide_matrix3[2][1] = 2;
        MoleculeText carbon_dioxide_mol3 = new MoleculeText(carbon_dioxide_matrix3,carbon_dioxide_atoms3);
        System.out.println("Molecule 1 name: carbon_dioxide_mol");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol.toString());
        System.out.println("Molecule 2 name: carbon_dioxide_mol3");
        System.out.println("representation: ");
        System.out.println(carbon_dioxide_mol3.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
        weakIsomorphic = isIsomorphicWithNumbers(carbon_dioxide_mol,carbon_dioxide_mol3);
        assert(weakIsomorphic);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            boolean isomorphic = verify_rigorous_isomorphism(carbon_dioxide_mol,carbon_dioxide_mol3);
            assert(isomorphic);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }

//        /**
//         * The following is an example by David Epstein on graph isomorphism and how two graphs with the same degree sequence
//         * may not be isomorphic.
//         */
        ArrayList<String> Na_Cl_atoms = new ArrayList<String>();
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Cl");
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Cl");
        Na_Cl_atoms.add("Cl");
        Na_Cl_atoms.add("Na");
        Na_Cl_atoms.add("Na");
        int[][] Na_Cl_mol = new int[8][8];
        Na_Cl_mol[0][1] = 1;
        Na_Cl_mol[1][0] = 1;
        Na_Cl_mol[1][2] = 1;
        Na_Cl_mol[1][3] = 1;
        Na_Cl_mol[2][1] = 1;
        Na_Cl_mol[2][4] = 1;
        Na_Cl_mol[3][1] = 1;
        Na_Cl_mol[3][5] = 1;
        Na_Cl_mol[4][2] = 1;
        Na_Cl_mol[4][6] = 1;
        Na_Cl_mol[5][3] = 1;
        Na_Cl_mol[5][7] = 1;
        Na_Cl_mol[7][5] = 1;
        Na_Cl_mol[6][4] = 1;
        ArrayList<String> Na_Cl_atoms2 = new ArrayList<String>();
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Cl");
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Cl");
        Na_Cl_atoms2.add("Na");
        Na_Cl_atoms2.add("Cl");
        Na_Cl_atoms2.add("Na");
        int[][] Na_Cl_mol2 = new int[8][8];
        Na_Cl_mol2[0][2] = 1;
        Na_Cl_mol2[1][2] = 1;
        Na_Cl_mol2[2][0] = 1;
        Na_Cl_mol2[2][1] = 1;
        Na_Cl_mol2[2][3] = 1;
        Na_Cl_mol2[3][2] = 1;
        Na_Cl_mol2[3][4] = 1;
        Na_Cl_mol2[4][3] = 1;
        Na_Cl_mol2[4][5] = 1;
        Na_Cl_mol2[5][4] = 1;
        Na_Cl_mol2[5][6] = 1;
        Na_Cl_mol2[6][5] = 1;
        Na_Cl_mol2[6][7] = 1;
        Na_Cl_mol2[7][6] = 1;
        MoleculeText Na_Cl= new MoleculeText(Na_Cl_mol,Na_Cl_atoms);
        MoleculeText Na_Cl2 = new MoleculeText(Na_Cl_mol2,Na_Cl_atoms2);
        System.out.println("Molecule 1 name: Na_Cl");
        System.out.println("representation: ");
        System.out.println(Na_Cl.toString());
        System.out.println("Molecule 2 name: Na_Cl2");
        System.out.println("representation: ");
        System.out.println(Na_Cl2.toString());
        System.out.println("Are the two different molecules weakly isomorphic?"); // weakly isomorphic means I can match a vertex to another vertex in the respective molecules this is not
        //a mathematical definition just something to illustrate how we will test isomorphism of different molecules.
         weakIsomorphic = isIsomorphicWithNumbers(Na_Cl,Na_Cl2);
        assert(weakIsomorphic);
        if(weakIsomorphic == true){
            System.out.println("Yes");
            System.out.println("Are they actually isomorphic?");
            boolean isomorphic = verify_rigorous_isomorphism(Na_Cl,Na_Cl2);
            assert(!isomorphic);
            if(isomorphic == true){
                System.out.println("The two molecules are actually isomorphic and you can find a bijection among them");
            }
            else{
                System.out.println("They are not isomorphic");
            }
        }
        else{
            System.out.println("The are not even weakly isomorphic");
        }
        MoleculeText m1 = new MoleculeText("molecules/carbon_dioxide.txt");
        MoleculeText m2 = new MoleculeText("molecules/carbon_dioxide2.txt");
        assert(isIsomorphicWithNumbers(m1,m2));
        assert(verify_rigorous_isomorphism(m1,m2));

        MoleculeText but = new MoleculeText("molecules/butane.txt");
        MoleculeText but2 = new MoleculeText("molecules/isobutane.txt");
        assert(!isIsomorphicWithNumbers(but,but2));
        assert(!verify_rigorous_isomorphism(but,but2));
    }

    @Test
    public void largeMoleculeTest(){
        MoleculeText fakeMol1 = new MoleculeText("molecules/fake_mol1.txt");
        MoleculeText fakeMol2 = new MoleculeText("molecules/fake_mol2.txt");
        assert(isIsomorphicWithNumbers(fakeMol1,fakeMol2));
        assert(verify_rigorous_isomorphism(fakeMol1,fakeMol2));

    }
}

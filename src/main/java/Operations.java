package main.java;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Operations {
    private static H2DB db;
    //    searchDumb search;

    public Operations(){
//        search = new searchDumb();
        db = new H2DB();
        try {
            db.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Helper function to insert into DB
     * @param filename
     */
    public void insert(String filename){
        try {
            db.insertMolecule(filename);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Finds isomorphic molecules
     * @param filename
     * @return
     */
    public ArrayList<MoleculeAbstract> find(String filename){
        ArrayList<MoleculeAbstract> isomorphicMolecules = new ArrayList<>(100);
        MoleculeText m = new MoleculeText(filename);
        System.out.println("Searching for Isomorphism for: " + m.getMoleculeName());
        try {
            MoleculeDB[] molecules = db.findSameAtoms(m.numVertices, m.getAtomList());
            for (MoleculeDB molecule : molecules) {
                if(this.checkIsomorphism(m, molecule)){
                    System.out.println(m.getMoleculeName() + " is isomorphic with "+ molecule.moleculeName);
                    isomorphicMolecules.add(molecule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return isomorphicMolecules;
    }


    public boolean checkIsomorphism(MoleculeAbstract m1, MoleculeAbstract m2){

        boolean weakIsomorphism = searchDumb.isIsomorphicWithNumbers(m1, m2);
        if(weakIsomorphism) return searchDumb.verify_rigorous_isomorphism(m1, m2);
        return false;
    }


    /**
     * Function to see if we are able to get ten operations per second.
     */
    public void tenOpsCheck(){
        Random rand = new Random();

        File moleculesDir = new File("molecules");
        File[] listOfFiles = moleculesDir.listFiles();
        System.out.println(listOfFiles[6].getAbsolutePath());
        for(int i = 0; i < 10; i++) {
            try {
                long startTime = System.nanoTime();

                // Five inserts
                insert(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                insert(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                insert(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                insert(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                insert(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());


                // 1 iso
//                MoleculeText m = new MoleculeText(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
//                MoleculeText m = new MoleculeText("isobutane.txt");
                MoleculeRandomized m = new MoleculeRandomized(3,7);
                MoleculeDB[] molecules = db.findSameAtoms(m.numVertices, m.getAtomList());

                for (MoleculeDB molecule : molecules) {
//                    System.out.println(molecule.getMoleculeName());
                    if(this.checkIsomorphism(m, molecule))
                        System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
                }

//                // 2 iso
//                m = new MoleculeText(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                m = new MoleculeRandomized(3,7);
                molecules = db.findSameAtoms(m.numVertices, m.getAtomList());

                for (MoleculeDB molecule : molecules) {
                    if(this.checkIsomorphism(m, molecule))
                        System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
                }

                // 3 iso
//                m = new MoleculeText(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                m = new MoleculeRandomized(3,7);
                molecules = db.findSameAtoms(m.numVertices, m.getAtomList());

                for (MoleculeDB molecule : molecules) {
                    if(this.checkIsomorphism(m, molecule))
                        System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
                }

                // 4 iso
//                m = new MoleculeText(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                m = new MoleculeRandomized(3,7);
                molecules = db.findSameAtoms(m.numVertices, m.getAtomList());

                for (MoleculeDB molecule : molecules) {
                    if(this.checkIsomorphism(m, molecule))
                        System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
                }

                // 5 iso
//                m = new MoleculeText(listOfFiles[rand.nextInt(listOfFiles.length)].getAbsolutePath());
                m = new MoleculeRandomized(3,7);
                molecules = db.findSameAtoms(m.numVertices, m.getAtomList());

                for (MoleculeDB molecule : molecules) {
                    if(this.checkIsomorphism(m, molecule))
                        System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
                }
//
                long endTime = System.nanoTime();
                long duration = (endTime - startTime);
                System.out.println("Operation Time: " + duration / 1000000 + "ms");
                System.out.println("--------------------------------------------");

            } catch (SQLException e){
                e.printStackTrace();
            }
        }

        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Operations Ops = new Operations();
//        Ops.tenOpsCheck();
        if (args[0].equals("--addMolecule"))
            Ops.insert(args[1]);
        else if (args[0].equals("--findMolecule")){
            ArrayList<MoleculeAbstract> Ms = Ops.find(args[1]);
            if (Ms.size() == 0)
                System.out.println("Not found");
        }
    }
}

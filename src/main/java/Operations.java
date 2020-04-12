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

    public MoleculeAbstract queryMolecule(String filename) throws SQLException {
        return db.queryMoleculeByName(filename);
    }


    public void insert(String filename){
        try {
            db.insertMolecule(filename);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<MoleculeAbstract> find(String filename){
        ArrayList<MoleculeAbstract> isomorphicMolecules = new ArrayList<>(100);
        MoleculeText m = new MoleculeText(filename);
        try {
            MoleculeDB[] molecules = db.findSameAtoms(m.numVertices, m.getAtomList());
            for (MoleculeDB molecule : molecules) {
                System.out.println(molecule.getMoleculeName());
                if(this.checkIsomorphism(m, molecule)){
                    System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
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
//        System.out.println(args[0]);
//        System.out.println(args[1]);
//
        if (args[0].equals("--addMolecule"))
            Ops.insert(args[1]);
        else if (args[0].equals("--findMolecule"))
            Ops.find(args[1]);

//        try{
//            String s = listOfFiles[6].getAbsolutePath();
//            Ops.insert(s);
//            MoleculeText m = new MoleculeText(s);
//             db.queryAdjacencyList(m.moleculeName);
//            System.out.println(m.numVertices);
//            System.out.println(m.toString());

//        try {
//            MoleculeDB[] molecules = db.findSameNumberAtoms(m.numVertices, m.getAtomList());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
////            System.out.println(molecules.length);
//            for (MoleculeDB molecule : molecules) {
//                m.resetAtoms();
//                if (m.getMoleculeName().equals(molecule.getMoleculeName())){
//                    System.out.println(m.getMoleculeName());
//                    System.out.println(m.toString());
//                    System.out.println(molecule.toString());
//                }
//                if(Ops.checkIsomorphism(m, molecule))
//                    System.out.println(m.getMoleculeName() + "is isomorphic with "+ molecule.moleculeName);
//        }
//        Ops.insert("isobutane.txt");
//        Ops.insert("butane.txt");
//        MoleculeText m1 = new MoleculeText("molecules/56aminopurin9yl4hydroxy2phosphonooxymethyltetrahydrofuran3yldihydrogenphosphate");
//        MoleculeText m1 = new MoleculeText("butane.txt");
//        MoleculeDB[] molecules = new MoleculeDB[0];
//        try {
//            molecules = db.findSameAtoms(m1.numVertices, m1.getAtomList());
//            for (MoleculeDB molecule : molecules) {
//                System.out.println(molecule.getMoleculeName());
//                if(Ops.checkIsomorphism(m1, molecule))
//                    System.out.println(m1.getMoleculeName() + " is isomorphic with "+ molecule.moleculeName);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//




    }
}

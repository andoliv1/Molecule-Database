package main.java;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger( App.class.getName() );
    private JPanel mainPanel;
    private JButton fileButton;
    private JRadioButton findButton;
    private JRadioButton addButton;
    private JLabel label;
    private JButton performActionButton;
    private JTextPane textPane1;
    Operations Ops;

    private String moleculeFile;

    public App(){

        // Initialize Operations class
        Ops = new Operations();
        moleculeFile = null;

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(addButton);
        group.add(findButton);


        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                int returnval = fc.showOpenDialog(mainPanel);
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    log.info("Opening: " + file.getName() + ".");
                    moleculeFile = file.getAbsolutePath();
                    label.setText("Selected: " + moleculeFile);


                } else {
                    log.info("Open command cancelled by user.");
                }
            }
        });
        performActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(moleculeFile==null){
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f,"No text file selected.");
                } else{
                    if (addButton.isSelected()){
                        label.setText("Added Molecule from: " + moleculeFile);
                        Ops.insert(moleculeFile);
                    } else if(findButton.isSelected()){
                        ArrayList<MoleculeAbstract> molecules = Ops.find(moleculeFile);
                        StyledDocument doc = textPane1.getStyledDocument();
                        if(molecules.size() > 0){
                            for (MoleculeAbstract m : molecules){
                                try {
                                    doc.insertString(doc.getLength(), m.moleculeName +"\n", null);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

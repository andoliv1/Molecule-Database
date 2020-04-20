package main.java;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;


public class App {
    private static final Logger log = Logger.getLogger( App.class.getName() );
    private JPanel mainPanel;
    private JButton fileButton;
    private JRadioButton findButton;
    private JRadioButton addButton;
    private JLabel label;
    private JButton performActionButton;
    private JTextPane textPane1;
    private JPanel graphPanel1;
    private JPanel graphPanel2;
    Operations Ops;
    JFrame frame;
    private String moleculeFile;

    public App() {

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
                    try {
                        moleculeFile = file.getCanonicalPath();
                    } catch (IOException e) {
                        moleculeFile = null;
                    }
                    label.setText("Selected: " + moleculeFile);


                } else {
                    log.info("Open command cancelled by user.");
                }
            }
        });
        performActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moleculeFile == null) {
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f, "No text file selected.");
                } else {
                    if (addButton.isSelected()) {
                        label.setText("Added Molecule from: " + moleculeFile);
                        Ops.insert(moleculeFile);
                        createGraph(graphPanel1, new MoleculeText(moleculeFile));
                    } else if (findButton.isSelected()) {
                        ArrayList<MoleculeAbstract> molecules = Ops.find(moleculeFile);
                        StyledDocument doc = textPane1.getStyledDocument();
                        if (molecules.size() > 0) {
                            for (MoleculeAbstract m : molecules) {
                                try {

                                    doc.insertString(doc.getLength(), m.moleculeName + "\n", null);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                                createGraph(graphPanel2, m);
                            }
                        }
                    }
                }

            }
        });
//        addMolecule("molecules/fake_mol1_13atom.txt");
        drawGraph(graphPanel1, new SingleGraph("Empty Graph", false, true));
        drawGraph(graphPanel2, new SingleGraph("Empty Graph", false, true));

    }

    public void createGraph(JPanel graphPanel, MoleculeAbstract m){

        Graph g = new SingleGraph(m.getMoleculeName(), false, true);
        for (int vertex = 0; vertex < m.numVertices; vertex++){
            String atom = m.getAtomList().get(vertex);
            Node n = g.addNode( atom + vertex); //C0, H1, ...
            n.addAttribute("ui.label", atom);
        }
        for (int vertex1 = 0; vertex1< m.getNumVertices(); vertex1++)
            for (int j = 0; j < m.getAdjacencyList()[vertex1].size(); j++){
                int vertex2 = m.getAdjacencyList()[vertex1].get(j);
                g.addEdge(m.atoms.get(vertex1)+vertex1 + m.atoms.get(vertex2)+vertex2, vertex1, vertex2);
            }

        drawGraph(graphPanel, g);
    }
    public void drawGraph(JPanel graphPanel, Graph graph){
//        Graph graph = new SingleGraph("Tutorial 1", false, true);
//        Node a = graph.addNode("A");
//        a.addAttribute("xy", 0, 0);
//        a.addAttribute("ui.label", "A");
//        Node b = graph.addNode("B");
//        b.addAttribute("xy", 10, 0);
//        b.addAttribute("ui.label", "B");
//
//        Node c = graph.addNode("C");
//        c.addAttribute("xy", 10, 10);
//        c.addAttribute("ui.label", "C");
//
//
//        graph.addEdge("AB", "A", "B");
//        graph.addEdge("BC", "B", "C");
//        graph.addEdge("CA", "C", "A");
////        graph.display();
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = viewer.addDefaultView(false);
        viewPanel.setLayout(new BorderLayout());
        viewPanel.setPreferredSize(new Dimension(800,600));
//        viewPanel.setSize(1000,1000);
        graphPanel.removeAll();
        graphPanel.add(viewPanel, BorderLayout.CENTER);
        graphPanel.revalidate();
        System.out.println(viewPanel);
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().mainPanel);
//        frame.add(mainPanel);
//        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

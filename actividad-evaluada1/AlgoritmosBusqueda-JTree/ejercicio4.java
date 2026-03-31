import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class ejercicio4 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Nodo raíz
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Algoritmos de Búsqueda");
            
            // Algoritmos
            DefaultMutableTreeNode noInformados = new DefaultMutableTreeNode("No informados");
            DefaultMutableTreeNode informados = new DefaultMutableTreeNode("Informados (heurísticos)");
            // Agregar algoritmos a la raíz
            root.add(noInformados);
            root.add(informados);
            
            // Algoritmos no informados
            noInformados.add(new DefaultMutableTreeNode("Búsqueda en anchura (BFS)"));
            noInformados.add(new DefaultMutableTreeNode("Búsqueda en profundidad (DFS)"));
            noInformados.add(new DefaultMutableTreeNode("Búsqueda de costo uniforme (UCS)"));
            // Algoritmos informados
            informados.add(new DefaultMutableTreeNode("Búsqueda Greedy"));
            informados.add(new DefaultMutableTreeNode("Algoritmo A*"));
            informados.add(new DefaultMutableTreeNode("Beam Search"));

            // Crear el árbol con la raíz
            JTree tree = new JTree(root);
            // Mostrar en ventana
            JFrame frame = new JFrame("Ejemplo JTree - Algoritmos de Búsqueda");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(tree), BorderLayout.CENTER);
            frame.setSize(300, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Ejercicio 1: Biblioteca
 * Representar en un JTree la jerarquía de una biblioteca con categorías y libros.
 */
public class Ejercicio1 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Nodo raíz
            DefaultMutableTreeNode biblioteca = new DefaultMutableTreeNode("Biblioteca");
            
            // Categorías
            DefaultMutableTreeNode literatura = new DefaultMutableTreeNode("Literatura");
            DefaultMutableTreeNode ciencia = new DefaultMutableTreeNode("Ciencia");
            // Agregar categorías a la raíz
            biblioteca.add(literatura);
            biblioteca.add(ciencia);
            
            // Libros de literatura
            literatura.add(new DefaultMutableTreeNode("Don Quijote"));
            literatura.add(new DefaultMutableTreeNode("Cien Años de Soledad"));
            // Libros de ciencia
            ciencia.add(new DefaultMutableTreeNode("El origen de las especies"));
            ciencia.add(new DefaultMutableTreeNode("Breve historia del tiempo"));

            // Crear el árbol con la raíz
            JTree tree = new JTree(biblioteca);
            // Mostrar en ventana
            JFrame frame = new JFrame("Ejercicio 1 - Biblioteca");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(tree), BorderLayout.CENTER);
            frame.setSize(300, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Ejercicio 2: Empresa
 * Mostrar la estructura jerárquica de una empresa con departamentos y empleados.
 */
public class Ejercicio2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Nodo raíz
            DefaultMutableTreeNode empresa = new DefaultMutableTreeNode("Empresa XYZ");
            
            // Categorías
            DefaultMutableTreeNode recursosHumanos = new DefaultMutableTreeNode("Recursos Humanos");
            DefaultMutableTreeNode tecnologia = new DefaultMutableTreeNode("Tecnología");
            // Agregar categorías a la raíz
            empresa.add(recursosHumanos);
            empresa.add(tecnologia);
            
            // Empleados de recursos humanos
            recursosHumanos.add(new DefaultMutableTreeNode("Ana López"));
            recursosHumanos.add(new DefaultMutableTreeNode("Carlos Pérez"));
            // Empleados de tecnologia
            tecnologia.add(new DefaultMutableTreeNode("María García"));
            tecnologia.add(new DefaultMutableTreeNode("José Ramírez"));

            // Crear el árbol con la raíz
            JTree tree = new JTree(empresa);
            // Mostrar en ventana
            JFrame frame = new JFrame("Ejercicio 2 - Empresa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(tree), BorderLayout.CENTER);
            frame.setSize(300, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

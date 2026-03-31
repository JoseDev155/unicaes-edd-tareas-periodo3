import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

// Clase para representar un producto simple
class Producto {
    String nombre;
    String descripcion;     // informativo (no se usa si hay HTML externo)
    double precio;          // informativo
    String[] rutaCategorias; // ej. {"Electrónica","Laptops"}
    String fichaPath;        // ej. "fichas/lenovo-ideapad-3.html"

    Producto(String nombre, String descripcion, double precio, String[] rutaCategorias, String fichaPath) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.rutaCategorias = rutaCategorias;
        this.fichaPath = fichaPath;
    }
}

// Clase principal
public class CatalogoInventario {

    private static String escapar(String s) {
        if (s == null) return "";
        String r = s;
        r = r.replace("&", "&amp;");
        r = r.replace("<", "&lt;");
        r = r.replace(">", "&gt;");
        r = r.replace("\"", "&quot;");
        r = r.replace("'", "&#39;");
        return r;
    }

    private static String htmlBienvenida() {
        return ""
        + "<!doctype html>"
        + "<html lang='es'><head><meta charset='utf-8'/>"
        + "<style>"
        + "body{font-family:Arial,Helvetica,sans-serif;background:#f5f7fb;margin:0;padding:24px;color:#222}"
        + ".card{max-width:720px;margin:40px auto;background:#fff;border-radius:16px;"
        + "box-shadow:0 10px 28px rgba(0,0,0,.08);padding:28px}"
        + ".title{font-size:22px;font-weight:bold;margin:0 0 10px}"
        + ".muted{color:#666;margin:8px 0 0}"
        + "</style></head><body>"
        + "<div class='card'>"
        + "<div class='title'>Catálogo / Inventario</div>"
        + "<p>Usa el árbol para navegar por <b>categorías</b> y seleccionar un <b>producto</b>.</p>"
        + "<p class='muted'>Las fichas se cargan desde archivos HTML en la carpeta <code>fichas/</code>.</p>"
        + "</div>"
        + "</body></html>";
    }

    private static String htmlCategoria(String nombreCategoria) {
        return ""
        + "<!doctype html>"
        + "<html lang='es'><head><meta charset='utf-8'/>"
        + "<style>"
        + "body{font-family:Arial,Helvetica,sans-serif;background:#f0f4f8;margin:0;padding:24px;color:#222}"
        + ".card{max-width:720px;margin:40px auto;background:#fff;border-radius:16px;"
        + "box-shadow:0 10px 28px rgba(0,0,0,.08);padding:28px}"
        + ".title{font-size:20px;font-weight:bold;margin:0 0 8px}"
        + ".muted{color:#666;margin:8px 0 0}"
        + "</style></head><body>"
        + "<div class='card'>"
        + "<div class='title'>Categoría: " + escapar(nombreCategoria) + "</div>"
        + "<p class='muted'>Selecciona una subcategoría o un producto para ver detalles.</p>"
        + "</div>"
        + "</body></html>";
    }

    private static String htmlError(String mensaje) {
        return ""
        + "<!doctype html>"
        + "<html lang='es'><head><meta charset='utf-8'/>"
        + "<style>"
        + "body{font-family:Arial,Helvetica,sans-serif;background:#fff3f3;margin:0;padding:24px;color:#222}"
        + ".card{max-width:720px;margin:40px auto;background:#fff;border:1px solid #ffc9c9;border-radius:16px;"
        + "box-shadow:0 8px 18px rgba(0,0,0,.06);padding:28px}"
        + ".title{font-size:18px;font-weight:bold;color:#8b1111;margin:0 0 8px}"
        + ".muted{color:#666;margin:8px 0 0}"
        + "</style></head><body>"
        + "<div class='card'>"
        + "<div class='title'>No se pudo cargar la ficha</div>"
        + "<p class='muted'>" + escapar(mensaje) + "</p>"
        + "</div>"
        + "</body></html>";
    }

    private static DefaultMutableTreeNode construirArbolDesdeProductos(ArrayList<Producto> productos) {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Catálogo de Productos");

        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);

            DefaultMutableTreeNode nodoActual = raiz;

            for (int nivel = 0; nivel < p.rutaCategorias.length; nivel++) {
                String etiquetaCategoria = p.rutaCategorias[nivel];

                DefaultMutableTreeNode hijoEncontrado = null;
                for (int c = 0; c < nodoActual.getChildCount(); c++) {
                    DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) nodoActual.getChildAt(c);
                    Object obj = hijo.getUserObject();
                    if (obj != null && obj.toString().equals(etiquetaCategoria)) {
                        hijoEncontrado = hijo;
                        break;
                    }
                }
                if (hijoEncontrado == null) {
                    hijoEncontrado = new DefaultMutableTreeNode(etiquetaCategoria);
                    nodoActual.add(hijoEncontrado);
                }
                nodoActual = hijoEncontrado;
            }

            DefaultMutableTreeNode hoja = new DefaultMutableTreeNode(p);
            nodoActual.add(hoja);
        }
        return raiz;
    }

    // Método main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) { }

                ArrayList<Producto> productos = new ArrayList<Producto>();

                productos.add(new Producto(
                        "Lenovo IdeaPad 3",
                        "Laptop 15.6\" FHD, Ryzen 5, 16GB RAM, 512GB SSD.",
                        549.99,
                        new String[]{"Electrónica", "Laptops"},
                        "actividad-evaluada2/sistema-catalogo-inventario-v2/fichas/lenovo-ideapad-3.html"
                ));
                productos.add(new Producto(
                        "HP Pavilion 15",
                        "Laptop 15\" FHD, Intel Core i5, 16GB RAM, 512GB SSD.",
                        629.90,
                        new String[]{"Electrónica", "Laptops"},
                        "actividad-evaluada2/sistema-catalogo-inventario-v2/fichas/hp-pavilion-15.html"
                ));
                productos.add(new Producto(
                        "Mouse Logitech M185",
                        "Mouse inalámbrico compacto con receptor USB.",
                        15.99,
                        new String[]{"Electrónica", "Accesorios"},
                        "actividad-evaluada2/sistema-catalogo-inventario-v2/fichas/mouse-logitech-m185.html"
                ));
                productos.add(new Producto(
                        "Teclado Redragon K552",
                        "Teclado mecánico tenkeyless con switches blue.",
                        39.95,
                        new String[]{"Electrónica", "Accesorios"},
                        "actividad-evaluada2/sistema-catalogo-inventario-v2/fichas/teclado-redragon-k552.html"
                ));
                productos.add(new Producto(
                        "Licuadora Oster",
                        "Vaso de vidrio, 3 velocidades, cuchillas de acero.",
                        49.50,
                        new String[]{"Hogar", "Cocina"},
                        "actividad-evaluada2/sistema-catalogo-inventario-v2/fichas/licuadora-oster.html"
                ));
                productos.add(new Producto(
                        "Aspiradora Philips",
                        "Compacta con filtro HEPA y depósito fácil de limpiar.",
                        89.00,
                        new String[]{"Hogar", "Limpieza"},
                        "actividad-evaluada2/sistema-catalogo-inventario-v2/fichas/aspiradora-philips.html"
                ));

                DefaultMutableTreeNode raiz = construirArbolDesdeProductos(productos);
                DefaultTreeModel modelo = new DefaultTreeModel(raiz);
                final JTree arbol = new JTree(modelo);
                arbol.setRootVisible(true);
                arbol.setShowsRootHandles(true);
                arbol.setRowHeight(22);

                JScrollPane scrollArbol = new JScrollPane(arbol);
                scrollArbol.setPreferredSize(new Dimension(360, 600));

                final JEditorPane visorHtml = new JEditorPane();
                visorHtml.setEditable(false);
                visorHtml.setContentType("text/html");
                visorHtml.setText(htmlBienvenida());

                JScrollPane scrollHtml = new JScrollPane(visorHtml);

                JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, scrollHtml);
                split.setDividerLocation(360);
                split.setResizeWeight(0.0);

                JPanel top = new JPanel(new BorderLayout());
                JLabel titulo = new JLabel("  Catálogo / Inventario — Fichas HTML externas");
                titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
                top.add(titulo, BorderLayout.WEST);

                JFrame frame = new JFrame("Catálogo / Inventario — Fichas externas");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(top, BorderLayout.NORTH);
                frame.add(split, BorderLayout.CENTER);
                frame.setSize(1120, 680);
                frame.setLocationRelativeTo(null);

                arbol.addTreeSelectionListener(new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent e) {
                        TreePath path = e.getPath();
                        if (path == null) {
                            visorHtml.setText(htmlBienvenida());
                            return;
                        }
                        Object ultimo = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                        if (ultimo instanceof Producto) {
                            Producto p = (Producto) ultimo;
                            try {
                                File f = new File(p.fichaPath);
                                if (!f.exists()) {
                                    visorHtml.setText(htmlError("No se encontró el archivo: " + escapar(p.fichaPath)));
                                    visorHtml.setCaretPosition(0);
                                    return;
                                }
                                String contenido = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                                visorHtml.setText(contenido);
                                visorHtml.setCaretPosition(0);
                            } catch (Exception ex) {
                                visorHtml.setText(htmlError("Error al cargar la ficha: " + escapar(ex.getMessage())));
                                visorHtml.setCaretPosition(0);
                            }
                        } else {
                            String nombreCategoria = (ultimo == null) ? "Categoría" : ultimo.toString();
                            visorHtml.setText(htmlCategoria(nombreCategoria));
                            visorHtml.setCaretPosition(0);
                        }
                    }
                });

                frame.setVisible(true);
            }
        });
    }
}

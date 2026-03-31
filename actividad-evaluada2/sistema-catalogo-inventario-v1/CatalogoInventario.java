import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;

// Clase para representar un producto simple
class Producto {
    String nombre;
    String descripcion;
    double precio;
    String[] rutaCategorias;

    Producto(String nombre, String descripcion, double precio, String[] rutaCategorias) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.rutaCategorias = rutaCategorias;
    }
}

// Clase principal
public class CatalogoInventario {
    // Utilidades HTML (estáticas)
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
        + "<div class='title'>Bienvenido/a al Catálogo</div>"
        + "<p>Usa el árbol de la izquierda para navegar por <b>categorías</b> "
        + "y seleccionar un <b>producto</b>.</p>"
        + "<p class='muted'>Al elegir un producto se mostrará aquí su ficha con nombre, descripción y precio.</p>"
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
        + "<p class='muted'>Selecciona una subcategoría o un producto para ver más detalles.</p>"
        + "</div>"
        + "</body></html>";
    }

    private static String construirHtmlProducto(Producto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>");
        sb.append("<html lang='es'><head><meta charset='utf-8'/>");
        sb.append("<style>");
        sb.append("body{font-family:Arial,Helvetica,sans-serif;background:#eef2f6;margin:0;padding:24px;color:#222}");
        sb.append(".card{max-width:800px;margin:40px auto;background:#ffffff;border-radius:18px;box-shadow:0 16px 40px rgba(0,0,0,.10);overflow:hidden}");
        sb.append(".banner{height:120px;background:linear-gradient(135deg,#3456d1,#6aa1ff)}");
        sb.append(".content{padding:24px 28px}");
        sb.append(".name{font-size:22px;font-weight:bold;margin:0 0 6px}");
        sb.append(".desc{color:#444;line-height:1.5;margin:8px 0 12px}");
        sb.append(".price{display:inline-block;font-weight:bold;padding:10px 14px;border-radius:12px;background:#f2f6ff;border:1px solid #cfe0ff}");
        sb.append(".crumbs{font-size:12px;color:#666;margin:0 0 8px}");
        sb.append("</style></head><body>");

        sb.append("<div class='card'>");
        sb.append("<div class='banner'></div>");
        sb.append("<div class='content'>");

        sb.append("<div class='crumbs'>");
        if (p.rutaCategorias != null && p.rutaCategorias.length > 0) {
            for (int i = 0; i < p.rutaCategorias.length; i++) {
                if (i > 0) sb.append(" &raquo; ");
                sb.append(escapar(p.rutaCategorias[i]));
            }
        } else {
            sb.append("Sin categoría");
        }
        sb.append("</div>");

        sb.append("<div class='name'>").append(escapar(p.nombre)).append("</div>");
        sb.append("<div class='desc'>").append(escapar(p.descripcion)).append("</div>");
        sb.append("<div class='price'>Precio: $ ").append(String.format(java.util.Locale.US, "%.2f", p.precio)).append("</div>");

        sb.append("</div></div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    // Construccion del arbol
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

            DefaultMutableTreeNode hojaProducto = new DefaultMutableTreeNode(p);
            nodoActual.add(hojaProducto);
        }
        return raiz;
    }

    // Método main
    public static void main(String[] args) {
        // Asegurar ejecución en el EDT, sin atajos
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    // ignorar para compatibilidad
                }

                // 1- Datos del ArrayList
                ArrayList<Producto> productos = new ArrayList<Producto>();

                productos.add(new Producto(
                        "Lenovo IdeaPad 3",
                        "Laptop 15.6\" FHD, Ryzen 5, 16GB RAM, 512GB SSD. Ideal para estudiantes y oficina.",
                        549.99,
                        new String[]{"Electrónica", "Laptops"}
                ));
                productos.add(new Producto(
                        "HP Pavilion 15",
                        "Laptop 15\" FHD, Intel Core i5, 16GB RAM, 512GB SSD. Equilibrio entre rendimiento y portabilidad.",
                        629.90,
                        new String[]{"Electrónica", "Laptops"}
                ));
                productos.add(new Producto(
                        "Mouse Logitech M185",
                        "Mouse inalámbrico compacto con receptor USB. Batería de larga duración.",
                        15.99,
                        new String[]{"Electrónica", "Accesorios"}
                ));
                productos.add(new Producto(
                        "Teclado Redragon K552",
                        "Teclado mecánico tenkeyless con switches blue. Construcción robusta.",
                        39.95,
                        new String[]{"Electrónica", "Accesorios"}
                ));
                productos.add(new Producto(
                        "Licuadora Oster",
                        "Vaso de vidrio, 3 velocidades, cuchillas de acero. Perfecta para smoothies y salsas.",
                        49.50,
                        new String[]{"Hogar", "Cocina"}
                ));
                productos.add(new Producto(
                        "Aspiradora Philips",
                        "Aspiradora compacta con filtro HEPA y depósito fácil de limpiar.",
                        89.00,
                        new String[]{"Hogar", "Limpieza"}
                ));

                // 2 - Construcción del árbol (JTree)
                DefaultMutableTreeNode raiz = construirArbolDesdeProductos(productos);
                DefaultTreeModel modelo = new DefaultTreeModel(raiz);
                JTree arbol = new JTree(modelo);
                arbol.setRootVisible(true);
                arbol.setShowsRootHandles(true);
                arbol.setRowHeight(22);

                JScrollPane scrollArbol = new JScrollPane(arbol);
                scrollArbol.setPreferredSize(new Dimension(360, 600));

                // 3 - Visor HTML de la ficha (derecha)
                JEditorPane visorHtml = new JEditorPane();
                visorHtml.setEditable(false);
                visorHtml.setContentType("text/html");
                visorHtml.setText(htmlBienvenida());

                JScrollPane scrollHtml = new JScrollPane(visorHtml);

                // 4 - Split pane y barra superior
                JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, scrollHtml);
                split.setDividerLocation(360);
                split.setResizeWeight(0.0);

                JPanel top = new JPanel(new BorderLayout());
                JLabel titulo = new JLabel("  Catálogo / Inventario — Selecciona un producto para ver su ficha");
                titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
                top.add(titulo, BorderLayout.WEST);

                // 5 - JFrame
                JFrame frame = new JFrame("Catálogo / Inventario — Swing + JTree + HTML");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(top, BorderLayout.NORTH);
                frame.add(split, BorderLayout.CENTER);
                frame.setSize(1120, 680);
                frame.setLocationRelativeTo(null);

                // 6 - Listener: render de ficha al seleccionar
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
                            String html = construirHtmlProducto(p);
                            visorHtml.setText(html);
                            visorHtml.setCaretPosition(0);
                        } else {
                            String nombreCategoria = (ultimo == null) ? "Categoría" : ultimo.toString();
                            visorHtml.setText(htmlCategoria(nombreCategoria));
                            visorHtml.setCaretPosition(0);
                        }
                    }
                });

                // Mostrar
                frame.setVisible(true);
            }
        });
    }
}
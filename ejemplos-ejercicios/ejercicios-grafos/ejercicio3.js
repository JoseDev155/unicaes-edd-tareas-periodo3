// Crear el mapa
const redTransporte = new Map();

// Agregar conexiones al mapa
redTransporte.set("Terminal Central", ["Mercado", "Universidad"]);
redTransporte.set("Mercado", ["Terminal Central", "Estadio"]);
redTransporte.set("Universidad", ["Terminal Central", "Biblioteca"]);
redTransporte.set("Biblioteca", ["Universidad"]);
redTransporte.set("Estadio", ["Mercado"]);

// Aplicar BFS
/**
 * Recorrer el mapa desde "Terminal Central"
 * y mostrar el prden en que se visitan las paradas
 */
function recorridoBFS(grafo, nodoInicio) {
    console.log(`Iniciando recorrido BFS desde: ${nodoInicio}\n`);

    // Una cola para gestionar los nodos a visitar (FIFO: First In, First Out)
    let cola = [nodoInicio];
    
    // Un conjunto para llevar registro de lo ya visitado y evitar ciclos infinitos
    let visitados = new Set();
    visitados.add(nodoInicio);

    while (cola.length > 0) {
        // Sacamos el primer elemento de la cola
        const nodoActual = cola.shift(); 
        
        console.log(`Visitando: ${nodoActual}`);

        // Obtenemos los vecinos del nodo actual
        const vecinos = grafo.get(nodoActual) || [];

        for (const vecino of vecinos) {
            // Si no hemos visitado al vecino, lo agregamos a la cola y al set de visitados
            if (!visitados.has(vecino)) {
                visitados.add(vecino);
                cola.push(vecino);
            }
        }
        
        // Imprimimos el estado actual de la cola como pide la imagen
        // (Usamos JSON.stringify para que se vea como un array de strings limpio)
        console.log(`Cola actual: ${JSON.stringify(cola)}\n`);
    }
}

recorridoBFS(redTransporte, "Terminal Central");
// Crear el mapa
const redTransporte = new Map();

// Agregar conexiones al mapa
redTransporte.set("Terminal Central", ["Mercado", "Universidad"]);
redTransporte.set("Mercado", ["Terminal Central", "Estadio"]);
redTransporte.set("Universidad", ["Terminal Central", "Biblioteca"]);
redTransporte.set("Biblioteca", ["Universidad"]);
redTransporte.set("Estadio", ["Mercado"]);

// Aplicar DFS
// Implementar DFS recursivo para recorrer desde "Terminal Central"
function recorridoDFS(nodoActual, visitados = new Set()) {
    // Marcamos el nodo actual como visitado para no repetir
    visitados.add(nodoActual);
    
    // Imprimimos el nodo que estamos pisando
    console.log(`Visitando: ${nodoActual}`);

    // Obtenemos los vecinos
    const vecinos = redTransporte.get(nodoActual) || [];

    // Iteramos sobre cada vecino
    for (const vecino of vecinos) {
        // Si el vecino NO ha sido visitado, profundizamos en él
        if (!visitados.has(vecino)) {
            console.log(` Desde ${nodoActual} voy hacia ${vecino}`);
            
            // LLAMADA RECURSIVA: La función se llama a sí misma con el nuevo nodo
            recorridoDFS(vecino, visitados);
        }
    }
}

recorridoDFS("Terminal Central");
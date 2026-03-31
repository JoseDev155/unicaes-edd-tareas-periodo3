// Crear el mapa
const redTransporte = new Map();

// Agregar conexiones al mapa
redTransporte.set("Terminal Central", ["Mercado", "Universidad"]);
redTransporte.set("Mercado", ["Terminal Central", "Estadio"]);
redTransporte.set("Universidad", ["Terminal Central", "Biblioteca"]);
redTransporte.set("Biblioteca", ["Universidad"]);
redTransporte.set("Estadio", ["Mercado"]);

// Mostrar cuantas rutas (vecinos) tiene cada parada
console.log("Cantidad de rutas por parada:");

for (const [parada, conexiones] of redTransporte) {
    if (conexiones.length == 2) {
        console.log(`- La parada "${parada}" tiene ${conexiones.length} rutas.`);
    } else {
        console.log(`- La parada "${parada}" tiene ${conexiones.length} ruta.`);
    }
}

// Mostrar todas las conexiones completas
console.log("Conexiones completas del sistema de transporte:");

for (const [parada, conexiones] of redTransporte) {
    console.log(`- Desde "${parada}" se puede ir a: ${conexiones.join(", ")}`);
}
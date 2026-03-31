const visitas = new Map();

function registrarVisita(pagina) {
    if (visitas.has(pagina)) {
        visitas.set(pagina, visitas.get(pagina) + 1);
    } else {
        visitas.set(pagina, 1);
    }
}

// Simular visitas
registrarVisita("Inicio");
registrarVisita("Inicio");
registrarVisita("Contacto");
registrarVisita("Inicio");
registrarVisita("Tienda");
registrarVisita("Tienda");

console.log("Visitas totales por página:");
for (const [pagina, cantidad] of visitas) {
    console.log(`${pagina}: ${cantidad} visitas`);
}
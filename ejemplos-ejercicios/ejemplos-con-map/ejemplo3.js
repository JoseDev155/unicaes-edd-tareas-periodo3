const productos = new Map([
    ["Manzana", 0.5],
    ["Pan", 1.2],
    ["Leche", 1.8],
    ["Huevos", 2.0]
]);

// Mostrar productos disponibles
for (const [nombre, precio] of productos) {
    console.log(`${nombre} cuesta $${precio}`);
}

// Calcular total de una compra
const compra = ["Pan", "Leche", "Huevos"];
let total = 0;

for (const item of compra) {
    total += productos.get(item);
}

console.log("Total a pagar: $", total);
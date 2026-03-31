// Crear el mapa
const redAmigos = new Map();

// Agregar amigos
redAmigos.set("Ana", ["Beto", "Carla"]);
redAmigos.set("Beto", ["Ana", "Diego"]);
redAmigos.set("Carla", ["Ana"]);
redAmigos.set("Diego", ["Beto"]);

// Imprimir amigos de Ana y de Diego
console.log("Amigos de Ana:", redAmigos.get("Ana"));
console.log("Amigos de Diego:", redAmigos.get("Diego"));
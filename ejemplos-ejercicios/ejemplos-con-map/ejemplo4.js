const amigos = new Map();

amigos.set("Ana", ["Beto", "Carla"]);
amigos.set("Beto", ["Ana", "Diego"]);
amigos.set("Carla", ["Ana"]);
amigos.set("Diego", ["Beto"]);

// Mostrar los amigos de cada persona
for (const [persona, lista] of amigos) {
    console.log(`${persona} tiene como amigos a: ${lista.join(", ")}`);
}

// Verificar si Ana es amiga de Carla
console.log("¿Ana y Carla son amigas?", amigos.get("Ana").includes("Carla"));
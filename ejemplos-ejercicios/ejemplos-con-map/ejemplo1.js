// Crear el mapa
const agenda = new Map();

// Agregar contactos
agenda.set("Ana", "7777-1111");
agenda.set("Beto", "7777-2222");
agenda.set("Carla", "7777-3333");

// Obtener un número específico
console.log("Teléfono de Ana:", agenda.get("Ana"));

// Verificar si un contacto existe
console.log("¿Existe Pedro?", agenda.has("Pedro"));

// Mostrar todos los contactos
for (const [nombre, telefono] of agenda) {
    console.log(nombre, "=>", telefono); 
}
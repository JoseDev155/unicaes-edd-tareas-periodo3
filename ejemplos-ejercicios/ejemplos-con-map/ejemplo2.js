const calificaciones = new Map(); 

// Agregar datos
calificaciones.set("Luis", 8.5);
calificaciones.set("María", 9.2);
calificaciones.set("Pedro", 7.9);

// Mostrar todos los estudiantes
for (const [nombre, nota] of calificaciones) {
    console.log(`${nombre} tiene una nota de ${nota}`);
}

// Calcular el promedio general
let suma = 0;
for (const nota of calificaciones.values()) {
    suma += nota;
}
const promedio = suma / calificaciones.size;
console.log("Promedio general:", promedio.toFixed(2));
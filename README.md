# PMDM · Unidad 2 · Hito 2

**Módulo:** 0489 · Programación multimedia y dispositivos móviles (DAM2)
**Autor:** Alfonso Bercedo Núñez

Repositorio con las cinco prácticas de la Unidad 2. Cada carpeta numerada es un proyecto
completo de Android Studio (Kotlin + layouts XML + View Binding): se abre con
**File → Open** seleccionando la carpeta.

## Estructura

```
PMDM_U2_Bercedo/
├── README.md
├── .gitignore
├── 1_EnrutamientoAvanzado/   Fragmentos, Navigation, SafeArgs y deep link
├── 2_MisTareas/              RecyclerView, Adapter, ViewHolder y los tres menús
├── 3_MisTareas2/             SharedPreferences, AlertDialog, DatePicker, Toast y Snackbar
├── 4_MiBiblioteca/           Room (entidad, DAO, base de datos) e hilos
├── 5_NoticiasFP/             Material Design: tema, Toolbar, Drawer, CardView, Chip y FAB
└── evidencias/
    └── Hito2_Evidencias_Bercedo.pdf
```

Configuración común: `compileSdk 35`, `minSdk 24`, `targetSdk 34`, Android Gradle Plugin 8.7.3,
Kotlin 2.0.21, Gradle 8.11.1. Todos los textos visibles están en `res/values/strings.xml`.

## 1 · Enrutamiento avanzado

- `MainActivity` aloja un `NavHostFragment` con el grafo `res/navigation/nav_graph.xml`.
- `NuevaNotaFragment` («Nueva nota»): si el título está vacío muestra «Ingresa un título primero».
  Si no, navega con la clase generada por SafeArgs `NuevaNotaFragmentDirections`.
- `DetalleNotaFragment` («Detalle de la nota») lee los argumentos con `navArgs()`.
- Deep link `notas://detalle/{tituloNota}` declarado en el grafo y en el manifiesto (`<nav-graph>`).

Probar el deep link con el emulador abierto:

```
adb shell am start -W -a android.intent.action.VIEW -d "notas://detalle/MiPrimeraNota" com.example.enrutamientoavanzado
```

## 2 · Mis Tareas 1

- Lista de tarjetas (`MaterialCardView`) con la franja y el texto del color de la prioridad.
- Casilla de completada: el título se tacha. El listener se quita antes de reciclar el ViewHolder.
- Menú de opciones: cambiar a cuadrícula de 2 columnas (`GridLayoutManager`) y **Borrar completadas**
  (Toast con el número de tareas borradas).
- Menú popup (botón ⋮ de cada tarjeta): cambiar la prioridad.
- Menú contextual (pulsación larga): **Duplicar** y **Eliminar**.

## 3 · Mis Tareas 2

Copia de Mis Tareas 1 ampliada:

- Primer arranque: diálogo «¿Cómo te llamas?». La barra pasa a mostrar «Tareas de …».
- El nombre y la vista (lista o cuadrícula) se guardan en SharedPreferences, en el fichero
  `/data/data/com.example.mistareas2/shared_prefs/ajustes_mis_tareas.xml` (se ve en Device Explorer).
- FAB «+»: diálogo **Nueva tarea** con validación («Escribe un título»), prioridad y `DatePickerDialog`
  («Fecha límite: dd/mm/aaaa»).
- Eliminar pide confirmación («¿Eliminar?») y muestra un Snackbar con **Deshacer**.

## 4 · Mi Biblioteca

- `datos/Libro.kt` (entidad, tabla `libros`), `datos/LibroDao.kt` (DAO) y
  `datos/BibliotecaDatabase.kt` (base de datos `biblioteca.db`, patrón singleton).
- Todas las consultas se hacen en un hilo secundario (`Executors.newSingleThreadExecutor()`) y
  el resultado se pinta con `runOnUiThread`.
- Lista ordenada por título, contador «N libros · M leídos», «Aún no hay libros» cuando está vacía,
  errores si faltan el título o el autor, y borrado con Snackbar **Deshacer**.
- La tabla se ve en **App Inspection → Database Inspector**.

## 5 · Noticias FP

- Tema Material 3 propio con colores en `values/colors.xml` y `values-night/colors.xml` (modo oscuro).
- `MaterialToolbar` con botón ☰ (`ActionBarDrawerToggle`) y `NavigationView` con cabecera y secciones.
- Al elegir una sección cambia el título y se filtran las noticias. «Mis favoritas» muestra las
  marcadas con la estrella.
- Tarjetas `MaterialCardView` con `Chip` de categoría.
- `ExtendedFloatingActionButton` «Nueva»: se encoge al desplazar la lista y añade una noticia con
  Snackbar **Deshacer**.

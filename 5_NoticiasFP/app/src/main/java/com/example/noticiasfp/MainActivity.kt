package com.example.noticiasfp

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.noticiasfp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoticiaAdapter
    private val viewModel: NoticiasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar como barra de la app + botón ☰ que abre el Drawer
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.abrir_menu, R.string.cerrar_menu
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item -> elegirSeccion(item) }

        // Botón atrás: si el panel está abierto, primero lo cierra
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }.also { callback ->
            binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerOpened(drawerView: android.view.View) { callback.isEnabled = true }
                override fun onDrawerClosed(drawerView: android.view.View) { callback.isEnabled = false }
            })
        })

        adapter = NoticiaAdapter { noticia -> cambiarFavorita(noticia) }
        binding.rvNoticias.adapter = adapter

        // El FAB extendido se encoge al bajar y se estira al subir
        binding.rvNoticias.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && binding.fabNueva.isExtended) binding.fabNueva.shrink()
                else if (dy < 0 && !binding.fabNueva.isExtended) binding.fabNueva.extend()
            }
        })
        binding.fabNueva.setOnClickListener { anadirNoticiaNueva() }

        if (viewModel.noticias.isEmpty()) cargarNoticiasDeEjemplo()

        binding.navView.setCheckedItem(viewModel.seccionActual)
        mostrarSeccion()
    }

    private fun cargarNoticiasDeEjemplo() {
        val titulos = resources.getStringArray(R.array.titulos_noticias)
        val resumenes = resources.getStringArray(R.array.resumenes_noticias)
        val categorias = resources.getStringArray(R.array.categorias_noticias)
        titulos.forEachIndexed { i, titulo ->
            viewModel.noticias.add(
                Noticia(
                    id = viewModel.nuevoId(),
                    titulo = titulo,
                    resumen = resumenes[i],
                    categoria = Categoria.valueOf(categorias[i])
                )
            )
        }
    }

    // ---------- Drawer ----------

    private fun elegirSeccion(item: MenuItem): Boolean {
        viewModel.seccionActual = item.itemId
        mostrarSeccion()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /** Cambia el título de la barra y filtra la lista según la sección elegida. */
    private fun mostrarSeccion() {
        val (titulo, filtradas) = when (viewModel.seccionActual) {
            R.id.nav_tecnologia -> R.string.categoria_tecnologia to porCategoria(Categoria.TECNOLOGIA)
            R.id.nav_empleo -> R.string.categoria_empleo to porCategoria(Categoria.EMPLEO)
            R.id.nav_deportes -> R.string.categoria_deportes to porCategoria(Categoria.DEPORTES)
            R.id.nav_cultura -> R.string.categoria_cultura to porCategoria(Categoria.CULTURA)
            R.id.nav_favoritas -> R.string.seccion_favoritas to viewModel.noticias.filter { it.favorita }
            else -> R.string.seccion_portada to viewModel.noticias.toList()
        }
        supportActionBar?.setTitle(titulo)
        adapter.submitList(filtradas)
        binding.tvVacio.setText(
            if (viewModel.seccionActual == R.id.nav_favoritas) R.string.sin_favoritas else R.string.sin_noticias
        )
        binding.tvVacio.visibility = if (filtradas.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun porCategoria(categoria: Categoria) = viewModel.noticias.filter { it.categoria == categoria }

    // ---------- Favoritas ----------

    private fun cambiarFavorita(noticia: Noticia) {
        val indice = viewModel.noticias.indexOfFirst { it.id == noticia.id }
        if (indice == -1) return
        val actualizada = noticia.copy(favorita = !noticia.favorita)
        viewModel.noticias[indice] = actualizada
        mostrarSeccion()

        val mensaje = if (actualizada.favorita) R.string.marcada_favorita else R.string.quitada_favorita
        Snackbar.make(binding.coordinador, mensaje, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.fabNueva)
            .show()
    }

    // ---------- FAB ----------

    private fun anadirNoticiaNueva() {
        val categoria = when (viewModel.seccionActual) {
            R.id.nav_empleo -> Categoria.EMPLEO
            R.id.nav_deportes -> Categoria.DEPORTES
            R.id.nav_cultura -> Categoria.CULTURA
            else -> Categoria.TECNOLOGIA
        }
        val nueva = Noticia(
            id = viewModel.nuevoId(),
            titulo = getString(R.string.noticia_nueva_titulo),
            resumen = getString(R.string.noticia_nueva_resumen),
            categoria = categoria,
            favorita = viewModel.seccionActual == R.id.nav_favoritas
        )
        viewModel.noticias.add(0, nueva)
        mostrarSeccion()
        binding.rvNoticias.scrollToPosition(0)

        Snackbar.make(binding.coordinador, R.string.noticia_anadida, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.fabNueva)
            .setAction(R.string.deshacer) {
                viewModel.noticias.removeAll { it.id == nueva.id }
                mostrarSeccion()
            }
            .show()
    }
}

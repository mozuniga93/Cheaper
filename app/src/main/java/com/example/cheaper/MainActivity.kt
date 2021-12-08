package com.example.cheaper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cheaper.fragments.*
import com.example.cheaper.repositorios.UsuarioRepositorio
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    val tag = "[Manati] Main"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inicioFragment = InicioFragment()
        val dashboardFragment = DashboardFragment()
        val buscarFragment = BuscarFragment()
        val favoritosFragment = FavoritosFragment()
        val perfilFragment = PerfilFragment()
        val resennaFragment = ResennaFragment()
        val perfilProdutoFragment = PerfilProductoFragment()
        val noPerfilFragment = NoPerfilFragment()

        makeCurrentFragment(dashboardFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_inicio -> makeCurrentFragment(dashboardFragment)
                R.id.ic_buscar -> makeCurrentFragment(buscarFragment)
                R.id.ic_favoritos -> makeCurrentFragment(favoritosFragment)
                R.id.ic_perfil -> {
                    if(UsuarioRepositorio.usuarioEstaLogueado())
                        makeCurrentFragment(perfilFragment)
                    else
                        makeCurrentFragment(noPerfilFragment)
                }
            }
            true
        }

    }



      fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }


//   private fun makeCurrentActivity() {
//
//
//    }
}
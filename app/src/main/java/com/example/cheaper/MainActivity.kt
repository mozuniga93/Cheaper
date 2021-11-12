package com.example.cheaper

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.cheaper.fragments.*
import com.example.cheaper.model.Usuario
import com.example.cheaper.repositorios.UsuarioRepositorio
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    val tag = "[Manati] Main"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inicioFragment = InicioFragment()
        val buscarFragment = BuscarFragment()
        val favoritosFragment = FavoritosFragment()
        val perfilFragment = PerfilFragment()
        val noPerfilFragment = NoPerfilFragment()
        val lst = ProductsListActivity()

        makeCurrentFragment(inicioFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_inicio -> makeCurrentFragment(inicioFragment)
                R.id.ic_buscar -> makeCurrentFragment(buscarFragment)
                R.id.ic_favoritos -> makeCurrentActivity(lst)
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

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    private fun makeCurrentActivity(fragment: Activity) {

        val intent = Intent(this, ProductsListActivity::class.java)
        startActivity(intent)

    }
}
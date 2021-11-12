package com.example.cheaper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cheaper.fragments.BuscarFragment
import com.example.cheaper.fragments.FavoritosFragment
import com.example.cheaper.fragments.InicioFragment
import com.example.cheaper.fragments.PerfilFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val tag = "[Manati] Main"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inicioFragment = InicioFragment()
        val buscarFragment = BuscarFragment()
        val favoritosFragment = FavoritosFragment()
        val perfilFragment = PerfilFragment()
        val lst = ProductsListActivity()
        val resennaFragment = ResennaFragment()
        val perfilProdutoFragment = PerfilProductoFragment()

        makeCurrentFragment(inicioFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_inicio -> makeCurrentFragment(inicioFragment)
                R.id.ic_buscar -> makeCurrentFragment(buscarFragment)
                R.id.ic_favoritos -> makeCurrentFragment(perfilProdutoFragment)
                R.id.ic_perfil -> makeCurrentFragment(perfilFragment)
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
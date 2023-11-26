package com.example.museumexplore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.museumexplore.databinding.MainActivityBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum

class MainActivity : AppCompatActivity() {

    var museums = arrayListOf<Museum>()

    private lateinit var binding: MainActivityBinding
    private  var  adapter = MuseumAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        museums.add(Museum("Museumxzy 111111111111"))
        museums.add(Museum("Museumxyz"))
        museums.add(Museum("Museumxzy1"))
        museums.add(Museum("Museumxyz1"))
        museums.add(Museum("Museumxzy2"))
        museums.add(Museum("Museumxyz2"))
        museums.add(Museum("Museumxzy3"))
        museums.add(Museum("Museumxyz3"))

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gridViewMuseums.adapter = adapter
    }

    inner class MuseumAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return museums.size
        }

        override fun getItem(position: Int): Any {
            return museums[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = MuseumDisplayBinding.inflate(layoutInflater)
            rootView.textViewMuseumName.text = museums[position].name

            /*rootView.root.setOnClickListener{
                val intent = Intent(this@MainActivity, ProductActivity::class.java)
                intent.putExtra(ProductActivity.EXTRA_ID, shoppingLists[position].id)
                resultLauncher.launch(intent)
            }*/

            return rootView.root
        }

    }
}
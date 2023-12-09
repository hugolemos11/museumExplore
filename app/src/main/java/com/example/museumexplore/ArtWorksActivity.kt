package com.example.museumexplore

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.museumexplore.databinding.ArtWorksBinding
import com.example.museumexplore.databinding.ArtWorksDisplayBinding
import com.example.museumexplore.modules.ArtWorks

class ArtWorksActivity : AppCompatActivity() {

    var artWorks = arrayListOf<ArtWorks>()

    private lateinit var binding: ArtWorksBinding
    private  var  adapter = ArtWorksAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        artWorks.add(ArtWorks("aa", "ArtWork1", "Cubism", R.drawable.art_work1))
        artWorks.add(ArtWorks("ab", "ArtWork2", "Cubism", R.drawable.art_work2))
        artWorks.add(ArtWorks("ac", "ArtWork3", "Cubism", R.drawable.art_work3))
        artWorks.add(ArtWorks("ba", "ArtWork4", "Cubism", R.drawable.art_work4))
        artWorks.add(ArtWorks("bb", "ArtWork5", "Cubism", R.drawable.art_work1))
        artWorks.add(ArtWorks("bc", "ArtWork6", "Cubism", R.drawable.art_work2))

        binding = ArtWorksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gridViewMuseums.adapter = adapter


    }

    inner class ArtWorksAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return artWorks.size
        }

        override fun getItem(position: Int): Any {
            return artWorks[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = ArtWorksDisplayBinding.inflate(layoutInflater)

            val drawableId = artWorks[position].image
            val drawable = ContextCompat.getDrawable(this@ArtWorksActivity, drawableId)

            rootView.imageViewArtWork.setImageDrawable(drawable)
            rootView.textViewArtWorkName.text = artWorks[position].name
            rootView.textViewCategory.text = artWorks[position].category

            /*rootView.root.setOnClickListener{
                val intent = Intent(this@ArtWorksActivity, MuseumDetailsActivity::class.java)
                intent.putExtra(MuseumDetailsActivity.EXTRA_NAME, museums[position].name)
                intent.putExtra(MuseumDetailsActivity.EXTRA_DESCRIPTION, museums[position].description)
                startActivity(intent)
            }*/

            return rootView.root
        }

    }
}
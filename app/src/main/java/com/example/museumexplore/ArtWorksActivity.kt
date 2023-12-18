package com.example.museumexplore

import android.content.Intent
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

        artWorks.add(ArtWorks("aa", "ArtWork1", "Picasso", 1986 ,"Cubism", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum." ,R.drawable.art_work1))
        artWorks.add(ArtWorks("ab", "ArtWork2", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work2))
        artWorks.add(ArtWorks("ac", "ArtWork3", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work3))
        artWorks.add(ArtWorks("ba", "ArtWork4", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work4))
        artWorks.add(ArtWorks("bb", "ArtWork5", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work1))
        artWorks.add(ArtWorks("bc", "ArtWork6", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work2))

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
            rootView.textViewArtWorkName.text = artWorks[position].artWorkName
            rootView.textViewCategory.text = artWorks[position].category

            rootView.root.setOnClickListener{
                val intent = Intent(this@ArtWorksActivity, ArtWorksDetailsActivity::class.java)
                intent.putExtra(ArtWorksDetailsActivity.EXTRA_IMAGE, artWorks[position].image)
                intent.putExtra(ArtWorksDetailsActivity.EXTRA_NAME, artWorks[position].artWorkName)
                intent.putExtra(ArtWorksDetailsActivity.EXTRA_ARTIST, artWorks[position].artistName)
                intent.putExtra(ArtWorksDetailsActivity.EXTRA_YEAR, artWorks[position].year)
                intent.putExtra(ArtWorksDetailsActivity.EXTRA_CATEGORY, artWorks[position].category)
                intent.putExtra(ArtWorksDetailsActivity.EXTRA_DESCRIPTION, artWorks[position].description)
                startActivity(intent)
            }

            return rootView.root
        }

    }
}
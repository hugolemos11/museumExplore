package com.example.museumexplore.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.museumexplore.R
import com.example.museumexplore.databinding.TicketsCarouselBinding
import com.example.museumexplore.showToast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/*class TicketAdapter(private val list: ArrayList<Ticket>, val context: Context) :
    RecyclerView.Adapter<TicketAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: TicketsCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Ticket, bitmap: Bitmap) {
            binding.apply {
                imageViewTicket.setImageBitmap(bitmap)
                textViewTicketType.text = model.title
                textViewTicketPrice.text = model.price.toString()
                textViewTicketDescription.text = model.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TicketAdapter.ItemViewHolder {
        return ItemViewHolder(
            TicketsCarouselBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TicketAdapter.ItemViewHolder, position: Int) {
        val model = list[position]
        model.pathToImage?.let { imagePath ->
            // Load the image from Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            val ONE_MEGABYTE: Long = 10 * 1024 * 1024
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                holder.bind(model, bitmap)
            }.addOnFailureListener {
                // Handle any errors
                Log.e("MuseumDetailsFragment", "Failed to load image from Firebase Storage")
            }
        }

    }

    override fun getItemCount() = list.size
}*/

class TicketAdapter (private val list: ArrayList<Ticket>, private val context: Context)
    : RecyclerView.Adapter<TicketAdapter.ImageViewHolder>(){

    inner class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageViewTicket: ImageView = view.findViewById(R.id.imageViewTicket)
        val textViewTicketType: TextView = view.findViewById(R.id.textViewTicketType)
        val textViewTicketPrice: TextView = view.findViewById(R.id.textViewTicketPrice)
        val textViewTicketDescription: TextView = view.findViewById(R.id.textViewTicketDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketAdapter.ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tickets_carousel, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketAdapter.ImageViewHolder, position: Int) {
        val model = list[position]

        model.pathToImage?.let { imagePath ->
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            pathReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .into(holder.imageViewTicket)
                holder.textViewTicketType.text = model.title
                holder.textViewTicketPrice.text = "${model.price}€"
                holder.textViewTicketDescription.text = model.description
            }.addOnFailureListener {
                showToast("Errooooooooooooooooooooo!!!!!!!!!", context)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
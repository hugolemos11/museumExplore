package com.example.museumexplore.modules

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.museumexplore.R
import com.example.museumexplore.setImage

class TicketAdapter(private val list: ArrayList<TicketType>, private val context: Context) :
    RecyclerView.Adapter<TicketAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageViewTicket: ImageView = view.findViewById(R.id.imageViewTicket)
        val textViewTicketType: TextView = view.findViewById(R.id.textViewTicketType)
        val textViewTicketPrice: TextView = view.findViewById(R.id.textViewTicketPrice)
        val textViewTicketDescription: TextView = view.findViewById(R.id.textViewTicketDescription)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TicketAdapter.ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tickets_carousel, parent, false)
        return ImageViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TicketAdapter.ImageViewHolder, position: Int) {
        val model = list[position]

        setImage(model.pathToImage, holder.imageViewTicket, context)

        holder.textViewTicketType.text = model.type
        holder.textViewTicketPrice.text = "${model.price}€"
        holder.textViewTicketDescription.text = model.description
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
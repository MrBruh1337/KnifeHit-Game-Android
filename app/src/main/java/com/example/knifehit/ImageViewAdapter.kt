package com.example.knifehit
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private val phoneOwner = PhoneOwner

class ImageViewAdapter(private val imageList: List<ImageItem>,private val itemClickListener : ItemClickListener,private val context : Context) :
    RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>() {
    interface ItemClickListener {
        fun onItemClick(imageItem : ImageItem)
    }
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_shop)
        val costTextView : TextView = itemView.findViewById(R.id.cost_text)
        init {
            imageView.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val imageResource = imageList[position]
                    itemClickListener.onItemClick(imageResource)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResource = imageList[position]
        holder.imageView.setImageResource(imageResource.imageResource)
        holder.imageView.rotation= 45f
        val costItem = imageResource.costItem
        if(phoneOwner.getSavedKnifes(context).contains(imageResource.imageResource.toString())){
            if(phoneOwner.default_knife_skin_for_player1 == imageResource.imageResource){
                holder.costTextView.text= "Equiped"
            }else{
                holder.costTextView.text = "Bought"
            }
        }else{
            holder.costTextView.text = costItem.toString()
        }


    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
data class ImageItem(val imageResource : Int , val costItem : Int)
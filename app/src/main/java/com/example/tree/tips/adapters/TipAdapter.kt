//package com.example.tree.tips.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.tree.R
//import com.example.tree.tips.models.Tip
//import com.example.tree.tips.onProductTipClickListener
//import com.google.android.material.textview.MaterialTextView
//
//class TipAdapter(
//    private val listener: onProductTipClickListener
//) : ListAdapter<Tip, TipAdapter.ProductTipViewHolder>(ProductTipComparators()) {
//
//
//
//    inner class ProductTipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.tip_list_item_image)
//        val titleView: MaterialTextView = itemView.findViewById(R.id.tip_list_item_title)
//        val voteView : MaterialTextView = itemView.findViewById(R.id.tip_list_item_date_vote)
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): TipAdapter.ProductTipViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val template : View = layoutInflater.inflate(R.layout.tip_list_item, parent, false)
//        return ProductTipViewHolder(template)
//    }
//
//    override fun onBindViewHolder(holder: TipAdapter.ProductTipViewHolder, position: Int) {
//        val tip = getItem(position)
//        holder.titleView.text = tip.title
//        holder.voteView.text = tip.vote_count.toString() + " votes"
//        Glide.with(holder.itemView)
//            .load(tip.imageList[0])
//            .into(holder.imageView)
//
//        holder.itemView.setOnClickListener{
//            listener.onProductTipClick(tip)
//        }
//    }
//
//    class ProductTipComparators : DiffUtil.ItemCallback<Tip>() {
//        override fun areItemsTheSame(oldItem: Tip, newItem: Tip): Boolean {
//            return oldItem === newItem
//        }
//        override fun areContentsTheSame(oldItem: Tip, newItem: Tip): Boolean {
//            return oldItem == newItem
//        }
//    }
//}
package com.example.thewallpaperapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.example.thewallpaperapp.interfaceImplement.OnProductClick
import com.example.thewallpaperapp.ui.HomeActivity
import com.krafty.android.R

class DonateAdapter(
    var homeActivity: HomeActivity,
    var skuDetailsList: List<SkuDetails>,
    var billingClient: BillingClient
) : RecyclerView.Adapter<DonateAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(homeActivity.baseContext)
            .inflate(R.layout.donate_product, parent, false)

        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return skuDetailsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txt_product_name.text = skuDetailsList[position].title

        holder.setClick(object : OnProductClick {

            override fun OnProductClick(view: View, pos: Int) {

                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList[position])
                    .build()
                val responseCode = billingClient.launchBillingFlow(homeActivity, flowParams)

            }
        })
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        internal var txt_product_name: TextView
        private lateinit var onProductClick: OnProductClick


        fun setClick(OnProduct: OnProductClick) {
            this.onProductClick = onProductClick
        }

        init {
            txt_product_name = itemView.findViewById(R.id.txt_product_name) as TextView
        }


        override fun onClick(v: View?) {
            onProductClick.OnProductClick(v!!, adapterPosition)
        }


    }
}
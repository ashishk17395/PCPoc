package com.ta.pcpoc.displayOverOTherApps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ta.pcpoc.R
import java.util.*

class AppLockListAdapter(private val itemClickInterface: RecyclerItemClickInterface, val context: Context) : RecyclerView.Adapter<AppLockListAdapter.RecyclerViewHolder>() {

    private lateinit var allAppsArrayList: ArrayList<LockAppEntity>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.app_list_recycler_item, parent, false)

        return AppLockListAdapter.RecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allAppsArrayList.size
    }

    override fun onBindViewHolder(holder: AppLockListAdapter.RecyclerViewHolder, position: Int) {
        holder.appNameTv.text = allAppsArrayList[position].appName
        if(allAppsArrayList[position].appLock)
            holder.appLockIconIv.setImageDrawable(context.getDrawable(R.drawable.ic_lock))
        else
            holder.appLockIconIv.setImageDrawable(context.getDrawable(R.drawable.ic_lock_open))
        holder.itemRl.setOnClickListener {
            if(allAppsArrayList[position].appLock)
                holder.appLockIconIv.setImageDrawable(context.getDrawable(R.drawable.ic_lock_open))
            else
                holder.appLockIconIv.setImageDrawable(context.getDrawable(R.drawable.ic_lock))
            itemClickInterface.onItemClick(position)
        }
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appNameTv: TextView = itemView.findViewById(R.id.appName)
        var appLockIconIv: ImageView = itemView.findViewById(R.id.appLockIconIv)
        var itemRl: RelativeLayout = itemView.findViewById(R.id.itemRl)
    }

    fun addAll(objects: ArrayList<LockAppEntity>) {
        allAppsArrayList = objects
        notifyDataSetChanged()
    }


}

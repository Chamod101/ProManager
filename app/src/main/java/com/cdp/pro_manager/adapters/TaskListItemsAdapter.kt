package com.cdp.pro_manager.adapters

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.activities.TaskListActivity
import com.cdp.pro_manager.models.Task

open class TaskListItemsAdapter (private val context: Context, private var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task,parent,false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp()).toPx(),0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size-1){
                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.requireViewById<LinearLayout>(R.id.ll_task_item).visibility =View.GONE
            }else{
                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.requireViewById<LinearLayout>(R.id.ll_task_item).visibility =View.VISIBLE
            }
            holder.itemView.requireViewById<TextView>(R.id.tv_task_list_title).text = model.title
            holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {
                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.requireViewById<CardView>(R.id.cv_add_task_list_name).visibility =View.VISIBLE
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {

                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.requireViewById<CardView>(R.id.cv_add_task_list_name).visibility =View.GONE
            }
            holder.itemView.requireViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {

                val listName = holder.itemView.requireViewById<EditText>(R.id.et_task_list_name).text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter List Name.",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int=
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int=
        (this * Resources.getSystem().displayMetrics.density).toInt()


    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)


}
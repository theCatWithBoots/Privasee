package com.example.privasee.ui.monitor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privasee.database.model.Record
import com.example.privasee.databinding.RecyclerItemMonitorRecordsBinding
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*


class MonitorAccessRecordsAdapter(): RecyclerView.Adapter<MonitorAccessRecordsAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: RecyclerItemMonitorRecordsBinding): RecyclerView.ViewHolder(binding.root)
    private var recordList = emptyList<Record>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemMonitorRecordsBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentRecord = recordList[position]

        if(currentRecord.day > 0) {
            val day = currentRecord.day
            val month = Month.of(currentRecord.month).name
            val year = currentRecord.year
            val appName = currentRecord.packageName
            val time = currentRecord.time

            val dateFormat = "$month $day $year"

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val timeString = sdf.format(Date(time))

            holder.binding.apply {
                tvRecordsDate.text = dateFormat
                tvRecordsTime.text = timeString
                tvRecordsAppName.text = appName
            }
        } else {
            val emptyString = "-"
            val tempString = "Empty Record"
            holder.binding.apply {
                tvRecordsDate.text = emptyString
                tvRecordsTime.text = emptyString
                tvRecordsAppName.text = tempString
            }
        }
    }

    override fun getItemCount(): Int {
        return recordList.count()
    }

    fun setData(data: List<Record>) {
        this.recordList = data
        notifyDataSetChanged()
    }

}
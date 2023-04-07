package com.example.privasee.ui.monitor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.privasee.database.model.Record
import com.example.privasee.database.model.User
import com.example.privasee.databinding.RecyclerItemMonitorRecordsBinding
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*


class MonitorAccessRecordsAdapter(): RecyclerView.Adapter<MonitorAccessRecordsAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: RecyclerItemMonitorRecordsBinding): RecyclerView.ViewHolder(binding.root)
    private var recordList = emptyList<Record>()
    private var userList = emptyList<User>()

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
            val status = currentRecord.status
            val imageString = currentRecord.image

          /*  //convert it to byte array
            val data = Base64.decode(imageString, Base64.DEFAULT)
            //now convert it to bitmap
            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)

            val bmp2 = reduceBitmapSize(bmp,  1000)
            */

            val bitmap = BitmapFactory.decodeFile(imageString)

            val dateFormat = "$month $day $year"

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val timeString = sdf.format(Date(time))

            holder.binding.apply {
                tvRecordsDate.text = dateFormat
                tvRecordsTime.text = timeString
                tvRecordsAppName.text = appName
                tvRecordsStatus.text = status
                tvImageView.setImageBitmap(bitmap)
            }
        } else {
            val emptyString = "-"
            val tempString = "Empty Record"
            holder.binding.apply {
                tvRecordsDate.text = emptyString
                tvRecordsTime.text = emptyString
                tvRecordsAppName.text = tempString
                tvRecordsStatus.text = emptyString
            }
        }


        holder.binding.apply {
            RecyclerItemMonitorRecords.setOnClickListener {
                val action = MonitoringAccessRecordsDirections.actionAccessRecordsToViewImage(currentRecord)
                RecyclerItemMonitorRecords.findNavController().navigate(action)
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
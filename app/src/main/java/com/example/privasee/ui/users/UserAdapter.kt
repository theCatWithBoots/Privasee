package com.example.privasee.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.privasee.R
import com.example.privasee.database.model.User
import com.example.privasee.databinding.RowUserBinding
import kotlinx.android.synthetic.main.row_user.view.*

class UserAdapter(): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: RowUserBinding) :RecyclerView.ViewHolder(binding.root)
    private var userList = emptyList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var currentUser = userList[position]

//        holder.binding.apply {
//            tvUserName.text = currentUser.name
//
//            rowUser.setOnClickListener{
//                val action = UserFragmentDirections.actionUsersFragmentToAddUserFragment(currentUser)
//                rowUser.findNavController().navigate(action)
//            }
//        }
    }

    override fun getItemCount(): Int {
        return userList.count()
    }

    fun setData(user: List<User>) {
        this.userList = user
        notifyDataSetChanged()
    }

}
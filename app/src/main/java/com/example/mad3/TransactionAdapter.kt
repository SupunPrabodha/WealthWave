package com.example.mad3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mad3.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit,
    private val userPreferences: UserPreferences
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTransactionClick(getItem(position))
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.apply {
                labelText.text = transaction.label
                categoryText.text = transaction.category
                dateText.text = dateFormat.format(transaction.date)
                
                val formattedAmount = if (transaction.amount >= 0) {
                    "+Rs. %.2f".format(transaction.amount)
                } else {
                    "-Rs. %.2f".format(kotlin.math.abs(transaction.amount))
                }
                amountText.text = formattedAmount

                // Set color based on transaction type
                val color = if (transaction.amount >= 0) {
                    android.graphics.Color.GREEN
                } else {
                    android.graphics.Color.RED
                }
                amountText.setTextColor(color)

                if (transaction.description.isNotEmpty()) {
                    descriptionText.text = transaction.description
                    descriptionText.visibility = ViewGroup.VISIBLE
                } else {
                    descriptionText.visibility = ViewGroup.GONE
                }
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
package com.example.mad3

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: ((Transaction) -> Unit)? = null
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val labelText: TextView = view.findViewById(R.id.labelText)
        private val amountText: TextView = view.findViewById(R.id.amountText)
        private val categoryText: TextView = view.findViewById(R.id.categoryText)
        private val descriptionText: TextView = view.findViewById(R.id.descriptionText)
        private val dateText: TextView = view.findViewById(R.id.dateText)
        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun bind(transaction: Transaction, onItemClick: ((Transaction) -> Unit)?) {
            labelText.text = transaction.label
            amountText.text = "$%.2f".format(transaction.amount)
            amountText.setTextColor(
                itemView.context.getColor(
                    if (transaction.amount >= 0) R.color.green else R.color.red
                )
            )
            categoryText.text = transaction.category
            
            if (transaction.description.isNotEmpty()) {
                descriptionText.text = transaction.description
                descriptionText.visibility = View.VISIBLE
            } else {
                descriptionText.visibility = View.GONE
            }

            // Format date
            val relativeDate = DateUtils.getRelativeTimeSpanString(
                transaction.date.time,
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS
            )
            dateText.text = "$relativeDate, ${timeFormat.format(transaction.date)}"

            // Set click listener
            itemView.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position], onItemClick)
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
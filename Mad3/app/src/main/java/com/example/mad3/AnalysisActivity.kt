package com.example.mad3

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad3.database.TransactionDatabase
import com.example.mad3.databinding.ActivityAnalysisBinding
import com.example.mad3.databinding.ItemCategoryBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Calendar

class AnalysisActivity : BaseActivity() {
    private lateinit var binding: ActivityAnalysisBinding
    private lateinit var db: TransactionDatabase
    private lateinit var categoryList: RecyclerView
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TransactionDatabase(this)
        categoryList = binding.categoryList

        setupNavigation()
        setupFilters()
        updateCharts()
    }

    private fun setupFilters() {
        // Setup Year Spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 2..currentYear).toList()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = yearAdapter
        binding.yearSpinner.setSelection(years.indexOf(currentYear))

        // Setup Month Spinner
        val months = arrayOf("January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthSpinner.adapter = monthAdapter
        binding.monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH))

        // Setup Listeners
        binding.yearSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedYear = years[position]
                updateCharts()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.monthSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedMonth = position
                updateCharts()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_analysis
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_analysis -> true
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateCharts() {
        val transactions = db.getAllTransactions()
        
        // Filter transactions by selected month and year
        val filteredTransactions = transactions.filter { transaction ->
            val calendar = Calendar.getInstance().apply { timeInMillis = transaction.date.time }
            calendar.get(Calendar.YEAR) == selectedYear && 
            calendar.get(Calendar.MONTH) == selectedMonth
        }
        
        updateIncomeExpenseChart(filteredTransactions)
        updateExpensePieChart(filteredTransactions)
    }

    private fun updateIncomeExpenseChart(transactions: List<Transaction>) {
        // Group transactions by day
        val dailyData = (1..31).associate { day ->
            day to Pair(0.0, 0.0) // Income, Expense
        }.toMutableMap()

        transactions.forEach { transaction ->
            val calendar = Calendar.getInstance().apply { timeInMillis = transaction.date.time }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val currentPair = dailyData[day] ?: Pair(0.0, 0.0)
            
            dailyData[day] = if (transaction.amount > 0) {
                Pair(currentPair.first + transaction.amount, currentPair.second)
            } else {
                Pair(currentPair.first, currentPair.second + kotlin.math.abs(transaction.amount))
            }
        }

        val incomeEntries = dailyData.map { (day, value) ->
            Entry(day.toFloat(), value.first.toFloat())
        }.sortedBy { it.x }

        val expenseEntries = dailyData.map { (day, value) ->
            Entry(day.toFloat(), value.second.toFloat())
        }.sortedBy { it.x }

        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
            color = Color.parseColor("#006D77")
            setCircleColor(Color.parseColor("#006D77"))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
            color = Color.parseColor("#E29578")
            setCircleColor(Color.parseColor("#E29578"))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.incomeExpenseChart.apply {
            data = LineData(incomeDataSet, expenseDataSet)
            description.isEnabled = false
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = value.toInt().toString()
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            legend.textColor = Color.parseColor("#2C3333")
            animateX(1000)
            invalidate()
        }
    }

    private fun updateExpensePieChart(transactions: List<Transaction>) {
        // Expense Chart
        val expenseByCategory = transactions
            .filter { it.amount < 0 }
            .groupBy { it.category }
            .mapValues { kotlin.math.abs(it.value.sumOf { transaction -> transaction.amount }) }

        val expenseEntries = expenseByCategory.map { 
            PieEntry(it.value.toFloat(), it.key)
        }

        val expenseDataSet = PieDataSet(expenseEntries, "Expense Categories").apply {
            colors = listOf(
                Color.parseColor("#006D77"),  // primary teal
                Color.parseColor("#2A858F"),  // darker teal
                Color.parseColor("#549EA6"),  // medium teal
                Color.parseColor("#E29578"),  // coral
                Color.parseColor("#D1816B"),  // darker coral
                Color.parseColor("#C06D5E"),  // medium coral
                Color.parseColor("#4C8589"),  // muted teal
                Color.parseColor("#B15A4B"),  // muted coral
                Color.parseColor("#7E9B9E")   // gray teal
            )
            valueTextColor = Color.parseColor("#2C3333")
            valueTextSize = 12f
            valueFormatter = PercentFormatter()
        }

        binding.expenseChart.apply {
            data = PieData(expenseDataSet)
            description.isEnabled = false
            setEntryLabelColor(Color.parseColor("#2C3333"))
            setUsePercentValues(true)
            legend.isEnabled = true
            legend.textColor = Color.parseColor("#2C3333")
            legend.textSize = 12f
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            animateY(1000)
            invalidate()
        }

        // Update category list with expense data
        val categoryData = expenseByCategory.map { CategoryItem(it.key, it.value, false) }
            .sortedByDescending { it.amount }
        
        setupCategoryList(categoryData)
    }

    private fun setupCategoryList(categories: List<CategoryItem>) {
        categoryList.layoutManager = LinearLayoutManager(this)
        categoryList.adapter = CategoryAdapter(categories)
    }

    // Add color adjustment utility function
    private fun Int.adjustBrightness(factor: Float): Int {
        val a = Color.alpha(this)
        val r = (Color.red(this) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(this) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(this) * factor).toInt().coerceIn(0, 255)
        return Color.argb(a, r, g, b)
    }
}

data class CategoryItem(
    val category: String,
    val amount: Double,
    val isIncome: Boolean
)

class CategoryAdapter(private val categories: List<CategoryItem>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryItem) {
            binding.categoryName.text = item.category
            binding.categoryAmount.text = String.format("$%.2f", item.amount)
            binding.categoryAmount.setTextColor(
                if (item.isIncome) Color.parseColor("#4CAF50")
                else Color.parseColor("#F44336")
            )
            binding.categoryName.setTextColor(Color.BLACK)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
} 
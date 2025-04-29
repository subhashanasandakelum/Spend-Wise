package com.subhashana.spendwise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.subhashana.spendwise.MainActivity
import com.subhashana.spendwise.R
import com.subhashana.spendwise.data.TransactionManager
import com.subhashana.spendwise.data.CurrencyManager
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.LineChart
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.NumberFormat
import java.util.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var transactionManager: TransactionManager
    private lateinit var currencyManager: CurrencyManager
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var progressBudget: LinearProgressIndicator
    private lateinit var pieChart: PieChart
    private lateinit var incomeChart: LineChart
    private lateinit var expenseChart: LineChart
    private lateinit var categoryColors: Map<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        transactionManager = (activity as MainActivity).getTransactionManager()
        currencyManager = CurrencyManager(requireContext())
        initializeViews(view)
        initializeCategoryColors()
        setupPieChart()
        setupLineChart()
        
        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun initializeViews(view: View) {
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses)
        tvBalance = view.findViewById(R.id.tvBalance)
        tvBudgetStatus = view.findViewById(R.id.tvBudgetStatus)
        progressBudget = view.findViewById(R.id.progressBudget)
        pieChart = view.findViewById(R.id.pieChart)
        incomeChart = view.findViewById(R.id.incomeChart)
        expenseChart = view.findViewById(R.id.expenseChart)
    }

    private fun initializeCategoryColors() {
        categoryColors = mapOf(
            getString(R.string.category_food) to Color.rgb(255, 152, 0),
            getString(R.string.category_transport) to Color.rgb(76, 175, 80),
            getString(R.string.category_bills) to Color.rgb(33, 150, 243),
            getString(R.string.category_entertainment) to Color.rgb(156, 39, 176),
            getString(R.string.category_shopping) to Color.rgb(244, 67, 54),
            getString(R.string.category_other) to Color.rgb(158, 158, 158)
        )
    }

    private fun getThemeTextColor(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        return typedValue.data
    }

    private fun setupPieChart() {
        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawEntryLabels(true)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.apply {
                isEnabled = true
                textSize = 12f
                textColor = Color.BLACK
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                yOffset = 10f
                xOffset = 10f
                form = Legend.LegendForm.CIRCLE
            }
        }
        updatePieChart()
    }

    private fun setupLineChart() {
        setupLineChart(incomeChart, true)
        setupLineChart(expenseChart, false)
    }

    private fun setupLineChart(chart: LineChart, isIncome: Boolean) {
        val chartColor = if (isIncome) Color.rgb(76, 175, 80) else Color.rgb(244, 67, 54) // Green for income, Red for expense
        
        chart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setPinchZoom(false)
            setScaleEnabled(true)
            
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
                textColor = getThemeTextColor()
                gridColor = Color.LTGRAY
                axisLineColor = getThemeTextColor()
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return currencyManager.formatAmount(value.toDouble())
                    }
                }
            }
            
            axisRight.isEnabled = false
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(true)
                textColor = getThemeTextColor()
                axisLineColor = getThemeTextColor()
                granularity = 1f
            }

            legend.apply {
                isEnabled = true
                textColor = getThemeTextColor()
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                form = Legend.LegendForm.LINE
            }

            setNoDataText(if (isIncome) "No income data available" else "No expense data available")
            setNoDataTextColor(getThemeTextColor())
            
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            // Add marker
            marker = object : com.github.mikephil.charting.components.MarkerView(context, R.layout.custom_marker) {
                private val tvContent: TextView = findViewById(R.id.tvContent)
                private var xAxisLabels: List<String> = emptyList()

                override fun refreshContent(e: Entry?, highlight: com.github.mikephil.charting.highlight.Highlight?) {
                    e?.let {
                        val index = it.x.toInt()
                        if (index in xAxisLabels.indices) {
                            val date = xAxisLabels[index]
                            val amount = currencyManager.formatAmount(it.y.toDouble())
                            tvContent.text = "$date\n$amount"
                        }
                    }
                    super.refreshContent(e, highlight)
                }

                override fun getOffset(): com.github.mikephil.charting.utils.MPPointF {
                    return com.github.mikephil.charting.utils.MPPointF(-width / 2f, -height - 10f)
                }
            }
        }
        updateLineChart(chart, isIncome)
    }

    private fun updateLineChart(chart: LineChart, isIncome: Boolean) {
        val entries = ArrayList<Entry>()
        val xAxisLabels = ArrayList<String>()
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault()) // Changed to include year
        val chartColor = if (isIncome) Color.rgb(76, 175, 80) else Color.rgb(244, 67, 54)
        
        // Get data for the last 6 months
        for (i in 5 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -i)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val monthStart = calendar.timeInMillis

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val monthEnd = calendar.timeInMillis
            
            val monthlyAmount = if (isIncome) {
                transactionManager.getIncomeForPeriod(monthStart, monthEnd)
            } else {
                transactionManager.getExpenseForPeriod(monthStart, monthEnd)
            }
            
            Log.d("HomeFragment", "Month: ${monthFormat.format(calendar.time)}, ${if (isIncome) "Income" else "Expense"}: $monthlyAmount")
            
            entries.add(Entry((5 - i).toFloat(), monthlyAmount.toFloat()))
            xAxisLabels.add(monthFormat.format(calendar.time))
        }

        if (entries.isNotEmpty()) {
            val dataSet = com.github.mikephil.charting.data.LineDataSet(entries, if (isIncome) "Monthly Income" else "Monthly Expenses")
            dataSet.apply {
                color = chartColor
                valueTextColor = getThemeTextColor()
                valueTextSize = 12f
                lineWidth = 2f
                circleRadius = 4f
                circleHoleRadius = 2f
                setCircleColor(chartColor)
                mode = com.github.mikephil.charting.data.LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                setDrawFilled(true)
                fillColor = chartColor
                fillAlpha = 50
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return currencyManager.formatAmount(value.toDouble())
                    }
                }
            }

            val data = com.github.mikephil.charting.data.LineData(dataSet)

            chart.apply {
                this.data = data
                xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                (marker as? com.github.mikephil.charting.components.MarkerView)?.let { marker ->
                    (marker as? com.github.mikephil.charting.components.MarkerView)?.let { 
                        it.javaClass.getDeclaredField("xAxisLabels").apply {
                            isAccessible = true
                            set(it, xAxisLabels)
                        }
                    }
                }
                animateXY(1000, 1000)
                invalidate()
            }
            
            Log.d("HomeFragment", "${if (isIncome) "Income" else "Expense"} chart updated with ${entries.size} entries")
        } else {
            Log.d("HomeFragment", "No ${if (isIncome) "income" else "expense"} data available")
            chart.clear()
            chart.invalidate()
        }
    }

    private fun updateUI() {
        try {
            val totalIncome = transactionManager.getTotalIncome()
            val totalExpenses = transactionManager.getTotalExpenses()
            val balance = totalIncome - totalExpenses
            val monthlyBudget = transactionManager.getMonthlyBudget()

            val currencyFormat = NumberFormat.getCurrencyInstance(currencyManager.getCurrencyLocale())
            tvTotalIncome.text = getString(R.string.total_income, currencyFormat.format(totalIncome))
            tvTotalExpenses.text = getString(R.string.total_expenses, currencyFormat.format(totalExpenses))
            tvBalance.text = getString(R.string.balance, currencyFormat.format(balance))
            tvBudgetStatus.text = getString(R.string.budget_status, 
                currencyFormat.format(totalExpenses), 
                currencyFormat.format(monthlyBudget))

            if (monthlyBudget > 0) {
                val progress = ((totalExpenses / monthlyBudget) * 100).toInt()
                progressBudget.progress = progress.coerceIn(0, 100)
            } else {
                progressBudget.progress = 0
            }

            updatePieChart()
            updateLineChart(incomeChart, true)
            updateLineChart(expenseChart, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePieChart() {
        val categoryExpenses = transactionManager.getCategoryExpenses()
        val totalExpenses = categoryExpenses.values.sum()

        if (totalExpenses > 0) {
            val entries = categoryExpenses.map { (category, amount) ->
                com.github.mikephil.charting.data.PieEntry(
                    amount.toFloat(),
                    category
                )
            }

            val dataSet = com.github.mikephil.charting.data.PieDataSet(entries, "")
            dataSet.apply {
                colors = categoryExpenses.keys.map { category ->
                    categoryColors[category] ?: Color.GRAY
                }
                valueTextColor = Color.BLACK
                valueTextSize = 12f
                valueLineColor = Color.BLACK
                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.4f
                yValuePosition = com.github.mikephil.charting.data.PieDataSet.ValuePosition.OUTSIDE_SLICE
            }

            val data = com.github.mikephil.charting.data.PieData(dataSet)
            data.setValueTextColor(Color.BLACK)
            data.setValueTextSize(12f)

            pieChart.data = data
            pieChart.invalidate()
            pieChart.visibility = View.VISIBLE
        } else {
            // Show empty state
            pieChart.clear()
            pieChart.setNoDataText("No expenses recorded")
            pieChart.setNoDataTextColor(Color.BLACK)
            pieChart.invalidate()
            pieChart.visibility = View.VISIBLE
        }
    }
} 
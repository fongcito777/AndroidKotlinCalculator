package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var tvCurrentNumber: TextView
    private var firstNumber: Double = 0.0
    private var operation: String = ""
    private var newNumber: Boolean = true
    private var currentExpression: String = ""
    private var isCalculationComplete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCurrentNumber = findViewById(R.id.tv_currentNumber)

        // Number buttons
        val numberButtons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        )

        numberButtons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                onNumberClick((it as Button).text.toString())
            }
        }

        // Operation buttons
        findViewById<Button>(R.id.btn_plus).setOnClickListener { onOperationClick("+") }
        findViewById<Button>(R.id.btn_minus).setOnClickListener { onOperationClick("-") }
        findViewById<Button>(R.id.btn_times).setOnClickListener { onOperationClick("×") }
        findViewById<Button>(R.id.btn_divide).setOnClickListener { onOperationClick("÷") }
        findViewById<Button>(R.id.btn_equals).setOnClickListener { onEqualsClick() }
        findViewById<Button>(R.id.btn_dot).setOnClickListener { onDotClick() }
        findViewById<Button>(R.id.btn_ac).setOnClickListener { onACClick() }
    }

    @SuppressLint("SetTextI18n")
    private fun onNumberClick(number: String) {
        if (isCalculationComplete) {
            onACClick()
            isCalculationComplete = false
        }

        if (newNumber) {
            tvCurrentNumber.text = number
            if (operation.isEmpty()) {
                currentExpression = number
            } else {
                currentExpression += number
            }
            newNumber = false
        } else {
            val currentText = tvCurrentNumber.text.toString()
            if (currentText == "0" && number != ".") {
                tvCurrentNumber.text = number
                currentExpression = currentExpression.dropLast(1) + number
            } else {
                tvCurrentNumber.text = currentText + number
                currentExpression += number
            }
        }
    }

    private fun onOperationClick(op: String) {
        try {
            if (operation.isNotEmpty() && !newNumber) {
                onEqualsClick()
            }

            if (!newNumber || isCalculationComplete) {
                firstNumber = tvCurrentNumber.text.toString().toDouble()
            }

            operation = op
            currentExpression = "$firstNumber $op "
            tvCurrentNumber.text = firstNumber.formatNumber()
            newNumber = true
            isCalculationComplete = false
        } catch (e: NumberFormatException) {
            onACClick()
        }
    }

    private fun onEqualsClick() {
        if (operation.isEmpty()) return

        try {
            val parts = currentExpression.trim().split(" ")
            if (parts.size < 2) return

            val secondNumber = if (newNumber) {
                firstNumber
            } else {
                tvCurrentNumber.text.toString().toDouble()
            }

            val result = when (operation) {
                "+" -> firstNumber + secondNumber
                "-" -> firstNumber - secondNumber
                "×" -> firstNumber * secondNumber
                "÷" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.POSITIVE_INFINITY
                else -> return
            }

            tvCurrentNumber.text = result.formatNumber()
            currentExpression = result.formatNumber()
            firstNumber = result
            operation = ""
            newNumber = true
            isCalculationComplete = true

        } catch (e: NumberFormatException) {
            onACClick()
        }
    }

    private fun onDotClick() {
        if (isCalculationComplete) {
            onACClick()
            isCalculationComplete = false
        }

        if (newNumber) {
            tvCurrentNumber.text = "0."
            if (operation.isEmpty()) {
                currentExpression = "0."
            } else {
                currentExpression += "0."
            }
            newNumber = false
        } else if (!tvCurrentNumber.text.contains(".")) {
            tvCurrentNumber.append(".")
            currentExpression += "."
        }
    }

    private fun onACClick() {
        tvCurrentNumber.text = "0"
        firstNumber = 0.0
        operation = ""
        newNumber = true
        currentExpression = ""
        isCalculationComplete = false
    }

    private fun Double.formatNumber(): String {
        return if (this == this.toLong().toDouble()) {
            this.toLong().toString()
        } else {
            // Limit decimal places to 8 and remove trailing zeros
            "%.8f".format(this).trimEnd('0').trimEnd('.')
        }
    }
}

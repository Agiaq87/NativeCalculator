package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private val numericBuilder: StringBuilder = java.lang.StringBuilder()

    private var setDecimalActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coroutine.launch {
            binding.progressBar.visibility = View.INVISIBLE
        }

        // Example of a call to a native method
        binding.currentOperation.text = stringFromJNI()
    }

    fun touchButton(view: View) {
        coroutine.launch {
            binding.progressBar.visibility = View.VISIBLE
        }
        when(view.id) {
            R.id.buttonOne,
            R.id.buttonTwo,
            R.id.buttonThree,
            R.id.buttonFour,
            R.id.buttonFive,
            R.id.buttonSix,
            R.id.buttonSeven,
            R.id.buttonEight,
            R.id.buttonNine,
            R.id.buttonZero -> {
                numericBuilder.append((view as Button).text.toString())
                coroutine.launch {
                    updateCurrentOperationUI(numericBuilder.toString())
                }
            }
            R.id.buttonCancelLast -> {
                val temp = binding.currentOperation.text.toString()

                if (temp.isNotEmpty()) {
                    numericBuilder.clear()
                    numericBuilder.append(temp.substring(0, temp.length - 1))
                    coroutine.launch {
                        Snackbar.make(binding.root, R.string.deleted_last, Snackbar.LENGTH_SHORT).show()
                    }
                    coroutine.launch {
                        updateCurrentOperationUI(numericBuilder.toString())
                    }
                    coroutine.launch {
                        resetLastOperationUI()
                    }
                } else {
                    coroutine.launch {
                        Snackbar.make(view, R.string.nothing_to, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.buttonDeleteAll -> {
                numericBuilder.clear()
                coroutine.launch {
                    Snackbar.make(binding.root, R.string.deleted_all, Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.buttonPlus,
            R.id.buttonMinus,
            R.id.buttonMultiply,
            R.id.buttonDiv -> {
                val value: String = numericBuilder.toString()
                coroutine.launch {
                    updateLastUI(
                        when(view.id) {
                            R.id.buttonPlus -> "+"
                            R.id.buttonMinus -> "-"
                            R.id.buttonMultiply -> "*"
                            R.id.buttonDiv -> "/"
                            else -> "NAN"
                        },
                        value
                    )
                }

                numericBuilder.clear()
            }
            R.id.buttonComma -> {
                setDecimalActive = true
            }
            R.id.buttonFact -> {
                when {
                    numericBuilder.contains(", -".toRegex()) -> coroutine.launch {
                        Snackbar.make(view, R.string.fact_comma, Snackbar.LENGTH_SHORT).show()
                    }
                    numericBuilder.isEmpty() -> coroutine.launch {
                        Snackbar.make(view, R.string.insert_fact, Snackbar.LENGTH_SHORT).show()
                    }
                    numericBuilder.toString().isNotEmpty() && numericBuilder[0] == '0' -> coroutine.launch {
                        Snackbar.make(view, R.string.fact_not_value, Snackbar.LENGTH_SHORT).show()
                    }
                    else -> coroutine.launch {
                        updateSpecialOp("fact", numericBuilder.toString())
                    }
                }

            }
            else -> coroutine.launch {
                Snackbar.make(view, R.string.nothing_to, Snackbar.LENGTH_SHORT).show()
            }
        }

        coroutine.launch {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun resetLastOperationUI() {
        binding.lastOperation.text = ""
        binding.lastValue.text = ""
    }

    private fun updateCurrentOperationUI(value: String) {
        binding.currentOperation.text = value
    }

    private suspend fun updateLastUI(op: String, value: String) = if(binding.lastValue.text.isNotEmpty()) {
        val first = binding.lastValue.text.toString()
        val result = calculateInteger(first, value, op[0])
        binding.lastValue.text = "$first $op $value"
        binding.lastOperation.text = "="
        binding.currentOperation.text = result
    } else {
        binding.lastOperation.text = op
        binding.lastValue.text = value
    }

    private suspend fun updateSpecialOp(op: String, first: String, second: String = "") {
        when (op) {
            "fact" -> {
                binding.lastValue.text = "fact($first)"
                binding.lastOperation.text = "="
                binding.currentOperation.text = fact(first.toLong())
            }
        }
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun calculateInteger(first: String, second: String, op: Char): String

    external fun fact(value: Long): String

    companion object {
        // Used to load the 'myapplication' library on application startup.
        init {
            System.loadLibrary("myapplication")
        }
    }
}
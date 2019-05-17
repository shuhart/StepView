package com.shuhart.stepview.sample.examples.customise

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast

import com.shuhart.stepview.StepView
import com.shuhart.stepview.sample.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

import java.util.ArrayList

class CustomiseActivity : AppCompatActivity() {
    private var currentStep = 0
    private var stepView: StepView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customise)
        stepView = findViewById(R.id.step_view)
        setupStepView()
        setupCustomisation()
    }

    private fun setupStepView() {
        stepView!!.getState().typeface(Typeface.createFromAsset(assets, "font/LemonSansRegular.otf")).commit()
        stepView!!.setOnStepClickListener(object : StepView.OnStepClickListener {
            override fun onStepClick(step: Int) {
                Toast.makeText(this@CustomiseActivity, "Step $step", Toast.LENGTH_SHORT).show()
            }
        })
        findViewById<View>(R.id.next).setOnClickListener {
            if (currentStep < stepView!!.stepCount - 1) {
                currentStep++
                stepView!!.go(currentStep, true)
            } else {
                stepView!!.done(true)
            }
        }
        findViewById<View>(R.id.back).setOnClickListener {
            if (currentStep > 0) {
                currentStep--
            }
            stepView!!.done(false)
            stepView!!.go(currentStep, true)
        }
        val sw = findViewById<Switch>(R.id.next_circle_switch)
        sw.setOnCheckedChangeListener { buttonView, isChecked -> stepView!!.getState().nextStepCircleEnabled(isChecked).commit() }
        val steps = ArrayList<String>()
        for (i in 0..4) {
            steps.add("Step " + (i + 1))
        }
        stepView!!.setSteps(steps)
    }

    private fun setupCustomisation() {
        setupSelectCircleColorCustomisation()
        setupSelectTextColorCustomisation()
        setupNextCircleColorCustomisation()
    }

    private fun setupSelectCircleColorCustomisation() {
        val selectedCircleColorEditText = findViewById<EditText>(R.id.selected_circle_color_hex)
        val selectedCircleColorSampleImageView = findViewById<ImageView>(R.id.selected_circle_color_sample)

        selectedCircleColorEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // empty
            }

            override fun afterTextChanged(s: Editable) {
                var candidateColorHex = s.toString()
                if (!candidateColorHex.contains("#")) {
                    candidateColorHex = "#$candidateColorHex"
                }
                try {
                    val color = Color.parseColor(candidateColorHex)
                    selectedCircleColorSampleImageView.setImageDrawable(ColorDrawable(color))
                    stepView!!.getState().selectedCircleColor(color).commit()
                } catch (ignore: IllegalArgumentException) {
                }

            }
        })

        val color = ContextCompat.getColor(this, R.color.stepview_circle_selected)
        val hex = Integer.toHexString(color).toUpperCase().substring(2)
        selectedCircleColorEditText.setText(hex)

        selectedCircleColorSampleImageView.setOnClickListener {
            showColorPickerDialog(object : ColorPickListener {
                override fun onColorPicked(hex: String) {
                    selectedCircleColorEditText.setText(hex)
                }
            })
        }
    }

    private fun setupSelectTextColorCustomisation() {
        val selectedTextColorEditText = findViewById<EditText>(R.id.selected_text_color_hex)
        val selectedTextColorSampleImageView = findViewById<ImageView>(R.id.selected_text_color_sample)

        selectedTextColorEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // empty
            }

            override fun afterTextChanged(s: Editable) {
                var candidateColorHex = s.toString()
                if (!candidateColorHex.contains("#")) {
                    candidateColorHex = "#$candidateColorHex"
                }
                try {
                    val color = Color.parseColor(candidateColorHex)
                    selectedTextColorSampleImageView.setImageDrawable(ColorDrawable(color))
                    stepView!!.getState().selectedTextColor(color).commit()
                } catch (ignore: IllegalArgumentException) {
                }

            }
        })

        val color = ContextCompat.getColor(this, R.color.stepview_circle_selected)
        val hex = Integer.toHexString(color).toUpperCase().substring(2)
        selectedTextColorEditText.setText(hex)

        selectedTextColorSampleImageView.setOnClickListener {
            showColorPickerDialog(object : ColorPickListener {
                override fun onColorPicked(hex: String) {
                    selectedTextColorEditText.setText(hex)
                }
            })
        }
    }

    private fun setupNextCircleColorCustomisation() {
        val hext = findViewById<EditText>(R.id.next_circle_color_hex)
        val sample = findViewById<ImageView>(R.id.next_circle_color_sample)

        hext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // empty
            }

            override fun afterTextChanged(s: Editable) {
                var candidateColorHex = s.toString()
                if (!candidateColorHex.contains("#")) {
                    candidateColorHex = "#$candidateColorHex"
                }
                try {
                    val color = Color.parseColor(candidateColorHex)
                    sample.setImageDrawable(ColorDrawable(color))
                    stepView!!.getState().nextStepCircleColor(color).commit()
                } catch (ignore: IllegalArgumentException) {
                }

            }
        })

        val color = Color.GRAY
        val hex = Integer.toHexString(color).toUpperCase().substring(2)
        hext.setText(hex)

        sample.setOnClickListener {
            showColorPickerDialog(object : ColorPickListener {
                override fun onColorPicked(hex: String) {
                    hext.setText(hex)
                }
            })
        }
    }

    private fun showColorPickerDialog(listener: ColorPickListener) {
        val builder = ColorPickerDialog.Builder(this@CustomiseActivity, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        builder.setTitle("ColorPicker Dialog")
        builder.setPositiveButton(getString(R.string.confirm), ColorEnvelopeListener { envelope, fromUser -> listener.onColorPicked(envelope.hexCode) })
        builder.setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    internal interface ColorPickListener {
        fun onColorPicked(hex: String)
    }
}

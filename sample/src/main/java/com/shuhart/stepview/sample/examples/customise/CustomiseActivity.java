package com.shuhart.stepview.sample.examples.customise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.shuhart.stepview.StepView;
import com.shuhart.stepview.sample.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.List;

public class CustomiseActivity extends AppCompatActivity {
    private int currentStep = 0;
    private StepView stepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise);
        stepView = findViewById(R.id.step_view);
        setupStepView();
        setupCustomisation();
    }

    private void setupStepView() {
        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                Toast.makeText(CustomiseActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep < stepView.getStepCount() - 1) {
                    currentStep++;
                    stepView.go(currentStep, true);
                } else {
                    stepView.done(true);
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep > 0) {
                    currentStep--;
                }
                stepView.done(false);
                stepView.go(currentStep, true);
            }
        });
        Switch sw = findViewById(R.id.next_circle_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stepView.getState().nextStepCircleEnabled(isChecked).commit();
            }
        });
        List<String> steps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            steps.add("Step " + (i + 1));
        }
        stepView.setSteps(steps);
    }

    private void setupCustomisation() {
        setupSelectCircleColorCustomisation();
        setupSelectTextColorCustomisation();
        setupNextCircleColorCustomisation();
    }

    private void setupSelectCircleColorCustomisation() {
        final EditText selectedCircleColorEditText = findViewById(R.id.selected_circle_color_hex);
        final ImageView selectedCircleColorSampleImageView = findViewById(R.id.selected_circle_color_sample);

        selectedCircleColorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                String candidateColorHex = s.toString();
                if (!candidateColorHex.contains("#")) {
                    candidateColorHex = "#" + candidateColorHex;
                }
                try {
                    int color = Color.parseColor(candidateColorHex);
                    selectedCircleColorSampleImageView.setImageDrawable(new ColorDrawable(color));
                    stepView.getState().selectedCircleColor(color).commit();
                } catch (IllegalArgumentException ignore) {
                }
            }
        });

        int color = ContextCompat.getColor(this, R.color.stepview_circle_selected);
        String hex = Integer.toHexString(color).toUpperCase().substring(2);
        selectedCircleColorEditText.setText(hex);

        selectedCircleColorSampleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog(new ColorPickListener() {
                    @Override
                    public void onColorPicked(String hex) {
                        selectedCircleColorEditText.setText(hex);
                    }
                });
            }
        });
    }

    private void setupSelectTextColorCustomisation() {
        final EditText selectedTextColorEditText = findViewById(R.id.selected_text_color_hex);
        final ImageView selectedTextColorSampleImageView = findViewById(R.id.selected_text_color_sample);

        selectedTextColorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                String candidateColorHex = s.toString();
                if (!candidateColorHex.contains("#")) {
                    candidateColorHex = "#" + candidateColorHex;
                }
                try {
                    int color = Color.parseColor(candidateColorHex);
                    selectedTextColorSampleImageView.setImageDrawable(new ColorDrawable(color));
                    stepView.getState().selectedTextColor(color).commit();
                } catch (IllegalArgumentException ignore) {
                }
            }
        });

        int color = ContextCompat.getColor(this, R.color.stepview_circle_selected);
        String hex = Integer.toHexString(color).toUpperCase().substring(2);
        selectedTextColorEditText.setText(hex);

        selectedTextColorSampleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog(new ColorPickListener() {
                    @Override
                    public void onColorPicked(String hex) {
                        selectedTextColorEditText.setText(hex);
                    }
                });
            }
        });
    }

    private void setupNextCircleColorCustomisation() {
        final EditText hext = findViewById(R.id.next_circle_color_hex);
        final ImageView sample = findViewById(R.id.next_circle_color_sample);

        hext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                String candidateColorHex = s.toString();
                if (!candidateColorHex.contains("#")) {
                    candidateColorHex = "#" + candidateColorHex;
                }
                try {
                    int color = Color.parseColor(candidateColorHex);
                    sample.setImageDrawable(new ColorDrawable(color));
                    stepView.getState().nextStepCircleColor(color).commit();
                } catch (IllegalArgumentException ignore) {
                }
            }
        });

        int color = Color.GRAY;
        String hex = Integer.toHexString(color).toUpperCase().substring(2);
        hext.setText(hex);

        sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog(new ColorPickListener() {
                    @Override
                    public void onColorPicked(String hex) {
                        hext.setText(hex);
                    }
                });
            }
        });
    }

    private void showColorPickerDialog(final ColorPickListener listener) {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(CustomiseActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("ColorPicker Dialog");
        builder.setPositiveButton(getString(R.string.confirm), new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                listener.onColorPicked(envelope.getHexCode());
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    interface ColorPickListener {
        void onColorPicked(String hex);
    }
}

package com.shuhart.stepview.sample.examples.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shuhart.stepview.StepView;
import com.shuhart.stepview.sample.R;

import java.util.ArrayList;
import java.util.List;

public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.DummyHolder> implements CurrentStepListener {
    private List<Integer> currentSteps = new ArrayList<Integer>(){{
        for (int i = 0; i < 20; i++){
            add(0);
        }
    }};

    @NonNull
    @Override
    public DummyAdapter.DummyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DummyHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_dummy, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DummyAdapter.DummyHolder holder, int position) {
        if (position % 2 == 0) {
            List<String> steps = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                steps.add("Step " + (i + 1));
            }
            holder.stepView.setSteps(steps);
        } else {
            holder.stepView.setStepsNumber(5);
        }
        holder.stepView.go(currentSteps.get(position), false);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull DummyHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.listener = this;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DummyHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.listener = null;
    }

    @Override
    public int getItemCount() {
        return currentSteps.size();
    }

    @Override
    public void update(int adapterPosition, int step) {
        currentSteps.set(adapterPosition, step);
    }

    static class DummyHolder extends RecyclerView.ViewHolder {
        StepView stepView;
        CurrentStepListener listener;

        private int currentStep = 0;

        DummyHolder(final View itemView) {
            super(itemView);
            stepView = itemView.findViewById(R.id.step_view);
            stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
                @Override
                public void onStepClick(int step) {
                    Toast.makeText(itemView.getContext(), "Step " + step, Toast.LENGTH_SHORT).show();
                }
            });
            itemView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentStep < stepView.getStepCount() - 1) {
                        currentStep++;
                        stepView.go(currentStep, true);
                        listener.update(getAdapterPosition(), currentStep);
                    } else {
                        stepView.done(true);
                    }
                }
            });
            itemView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentStep > 0) {
                        currentStep--;
                        listener.update(getAdapterPosition(), currentStep);
                    }
                    stepView.done(false);
                    stepView.go(currentStep, true);
                }
            });
        }
    }
}

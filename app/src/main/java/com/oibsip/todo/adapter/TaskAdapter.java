package com.oibsip.todo.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oibsip.unitconverter.R;
import com.oibsip.todo.model.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onStatusToggled(TaskItem task, boolean isCompleted);
        void onDeleteRequested(TaskItem task);
    }

    private final List<TaskItem> taskList = new ArrayList<>();
    private final OnTaskActionListener listener;

    public TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<TaskItem> newTasks) {
        taskList.clear();
        if (newTasks != null) {
            taskList.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItem task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbCompleted;
        private final TextView tvTitle;
        private final TextView tvNotes;
        private final TextView tvDate;
        private final ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvNotes = itemView.findViewById(R.id.tvTaskNotes);
            tvDate = itemView.findViewById(R.id.tvTaskDate);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
        }

        public void bind(final TaskItem task, final OnTaskActionListener listener) {
            tvTitle.setText(task.getTitle());
            tvDate.setText(task.getFormattedDate());

            if (task.getNotes() != null && !task.getNotes().trim().isEmpty()) {
                tvNotes.setText(task.getNotes());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Remove listener before setting checked state to avoid accidental trigger
            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(task.isCompleted());

            updateStrikethrough(task.isCompleted());

            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateStrikethrough(isChecked);
                if (listener != null) {
                    listener.onStatusToggled(task, isChecked);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteRequested(task);
                }
            });
        }

        private void updateStrikethrough(boolean completed) {
            if (completed) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setAlpha(0.55f);
                tvNotes.setAlpha(0.55f);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setAlpha(1.0f);
                tvNotes.setAlpha(0.85f);
            }
        }
    }
}

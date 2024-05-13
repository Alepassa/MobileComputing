package com.example.myapplicationtask;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.example.myapplicationtask.databinding.TaskListItemBinding;

import java.util.List;

/*
recyclerview permette di creare item dinamici senza distruggere quelli precedenti quando sono fuori dallo schermo
permette rispetto agli altri di migliorare le prestazioni e reattività della app
 */


//tasklistadapater è un adattatore della recyclerview
//estende la classe recyclerview.adapter e che come viewholder utilizzerà taskviewholder per gestire i singoli elementi della recyclerView
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private List<Task> tasks;
    // come costruttore passaremo una lista di task precedenemtne salvati e che veranno visualizzati nella recyclerview
    public TaskListAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }


    //crea un nuovo viewHolder per rappresentare un singolo elemento della lista
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskListItemBinding binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    // metodo chiamato per associare i dati del dataset al ViewHolder
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bindItem(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    //lo scopo di questa classe sarà quello di gestire la visualizzazione
    // di un singolo elemento della lista della RecyclerView
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        //per tenere riferimento agli oggetti del del layout xml
        //in modo da accedere a tali oggetti e impostare i loro valori
        public TaskListItemBinding binding;


        public TaskViewHolder(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        //associa i dati di un oggetto task agli elemento UI all'interno
        // del viewHolder, impostano il checkbox a vero o falso a seconda
        //se la task è stata completata e il short name della task!
        public void bindItem(Task task) {
            binding.checkBox3.setChecked(task.isDone());
            binding.TaskNameTextView.setText(task.getShortName());
        }
    }

}

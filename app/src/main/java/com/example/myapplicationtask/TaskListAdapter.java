package com.example.myapplicationtask;


import android.view.LayoutInflater;
import android.view.View;
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

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }
    private List<Task> tasks;
    // come costruttore passaremo una lista di task precedenemtne salvati e che veranno visualizzati nella recyclerview

    private OnTaskSelectedListener listener;

    public TaskListAdapter(List<Task> tasks,OnTaskSelectedListener listener) {
        this.tasks = tasks;
        this.listener= listener;
    }


    //crea un nuovo viewHolder per rappresentare un singolo elemento della lista
    @NonNull
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

        holder.binding.checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the task object's done property
                task.setDone(holder.binding.checkBox3.isChecked());
            }
        });
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
            //quando un elemento della lista viene cliccato
            // per ogni elemento v, TaskListAdapter.this.listener vuol dire che fa riferimento
            // a questa classe per gestire l'elemento quando un elemento viene cliccato
            // e in questa classe appunto è presente l'elemento onTaskSelected che gestisce questo evento
            // accetta un item Task. Quando esso viene cliccato l'oggetto task viene passato come parametro
            itemView.setOnClickListener(v -> TaskListAdapter.this.listener.onTaskSelected(task));
        }
    }
}


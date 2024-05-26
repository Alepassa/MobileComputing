package com.example.myapplicationtask;

import java.util.ArrayList;
import java.util.List;

public class FilteredTasksAdapter {

    private List<Task> allTasks;
    private List<Task> filteredTasks;

    private boolean showUnfinished;

    public FilteredTasksAdapter(List<Task> tasks){
        this.allTasks = tasks;
        this.filteredTasks = new ArrayList<>(tasks);
        showUnfinished = false;
    }

    public void setShowUnfinished(boolean showUnfinished){
        this.showUnfinished = showUnfinished;
        filterTasks();
    }

    public List<Task> getFilteredTasks() {
        return filteredTasks;
    }

    private void filterTasks(){
        if(!showUnfinished){ //torna array di tutte le task
            filteredTasks = new ArrayList<>(allTasks);
        }else{ //se no torna array di task non completate
            filteredTasks= new ArrayList<>();
            for(Task t : allTasks){
                if(!t.isDone()) filteredTasks.add(t);
            }
        }
    }


    public void deleteFinishedTasks(){
        List<Task> taskNotFinished= new ArrayList<>();
        for(Task t : allTasks){
            if(!t.isDone()) taskNotFinished.add(t);
        }
        allTasks = taskNotFinished;
        filterTasks();
    }
}

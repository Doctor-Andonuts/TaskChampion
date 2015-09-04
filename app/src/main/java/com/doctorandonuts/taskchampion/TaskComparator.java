package com.doctorandonuts.taskchampion;

import com.doctorandonuts.taskchampion.task.Task;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task lhs, Task rhs) {
    return rhs.getUrgency().compareTo(lhs.getUrgency());
    }
}

package application.example.taskwatcher;

import android.widget.LinearLayout;
import android.widget.TextView;

public class Task {
    public String name;
    public long seconds;
    public boolean running;
    public TextView tv;
    public long start;
    public LinearLayout layout;
    public Task(String n){
        name=n;
    }
}

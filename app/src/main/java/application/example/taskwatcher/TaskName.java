package application.example.taskwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import application.example.taskwatcher.R;

public class TaskName extends AppCompatActivity {
    public ArrayList<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_name);
        names=getIntent().getStringArrayListExtra(MainActivity.key);
    }
    public void done(View view){
        EditText et=(EditText)findViewById(R.id.taskName);
        String tname=et.getText().toString();
        if(tname.indexOf(';')!=-1||tname.indexOf(':')!=-1){
            Toast.makeText(this, "No ';' or ':' is allowed in the name!",
                    Toast.LENGTH_LONG).show();
        }
        else if(!names.contains(tname)){
            Intent intent=new Intent(this,MainActivity.class);
            intent.putExtra("stak",tname);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "This task is already present, give another name",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void back(View view){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("stak",";;;;");
        startActivity(intent);
    }
}

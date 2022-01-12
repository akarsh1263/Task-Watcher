package application.example.taskwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import application.example.taskwatcher.R;

public class Records extends AppCompatActivity {
    public ArrayList<ArrayList<Task>> days;
    public String removal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        days=new ArrayList<ArrayList<Task>>();
        removal="";
        Intent intent=getIntent();
        String dn=intent.getStringExtra(MainActivity.recname);
        String dt=intent.getStringExtra(MainActivity.tiname);
        StringTokenizer st1=new StringTokenizer(dn,":");
        StringTokenizer st2=new StringTokenizer(dt,":");
        int n=st1.countTokens();
        for(int i=0;i<n;i++){
            days.add(accumulate(decompact(st1.nextToken()),decompact(st2.nextToken())));
        }
        LinearLayout llp=findViewById(R.id.founder);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params2.weight=1f;
        LinearLayout.LayoutParams params3=new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        params3.weight=1f;
        for(int i=days.size()-1;i>=0;i--){
            final LinearLayout par=new LinearLayout(this);
            par.setLayoutParams(params);
            par.setOrientation(LinearLayout.VERTICAL);
            TextView day=new TextView(this);
            day.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
            day.setText("Day "+(i+1));
            day.setLayoutParams(params2);
            par.addView(day);
            for(int j=0;j<days.get(i).size();j++){
                TextView name=new TextView(this);
                name.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                name.setLayoutParams(params3);
                name.setText(days.get(i).get(j).name);
                TextView time=new TextView(this);
                time.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                time.setLayoutParams(params3);
                long seconds=days.get(i).get(j).seconds;
                int hours = (int) seconds/ 3600;
                int minutes = (int) (seconds % 3600) / 60;
                int secs = (int) seconds % 60;
                String timetext
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);
                time.setText(timetext);
                Button del=new Button(this);
                del.setText("Delete");
                del.setLayoutParams(params3);

                final LinearLayout hor=new LinearLayout(this);
                hor.setOrientation(LinearLayout.HORIZONTAL);
                hor.setLayoutParams(params2);
                hor.addView(name);
                hor.addView(time);
                hor.addView(del);
                par.addView(hor);
                final int ii=i;
                final String jj=days.get(ii).get(j).name;
                del.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        par.removeView(hor);
                        removal+=ii+"-"+jj+";";
                    }
                });
            }
            llp.addView(par);
        }
        Button back=new Button(this);
        back.setLayoutParams(params);
        back.setText("Save/Back");
        llp.addView(back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Records.this,MainActivity.class);
                String sem=removal;
                intent.putExtra("removes",sem);
                startActivity(intent);
            }
        });
    }
    public ArrayList<String> decompact(String comp){
        StringTokenizer st=new StringTokenizer(comp,";");
        ArrayList<String> ret=new ArrayList<String>();
        int n=st.countTokens();
        for(int i=0;i<n;i++){
            ret.add(st.nextToken());
        }
        return ret;
    }
    public ArrayList<Task> accumulate(ArrayList<String> names,ArrayList<String> times){
        ArrayList<Task> ret=new ArrayList<Task>();
        for(int i=0;i<names.size();i++){
            Task t=new Task(names.get(i));
            t.seconds=Long.parseLong(times.get(i));
            ret.add(t);
        }
        return ret;
    }
}


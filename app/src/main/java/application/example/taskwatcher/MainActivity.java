package application.example.taskwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
    public ArrayList<Task> current=new ArrayList<Task>();
    public ArrayList<ArrayList<Task>> days=new ArrayList<ArrayList<Task>>();
    public static final String key="arkey";
    public static final String recname="recordname";
    public static final String tiname="timename";
    public boolean tba=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current=new ArrayList<Task>();
        days=new ArrayList<ArrayList<Task>>();
        Intent intent = getIntent();
        String tname=intent.getStringExtra("stak");
        if(tname!=null){
            tname=tname.trim();
        }
        String rema=intent.getStringExtra("removes");
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String ns = prefs.getString("names", null);
        String ts = prefs.getString("times", null);
        String rs = prefs.getString("runs", null);
        String ss= prefs.getString("start",null);
        String dn=prefs.getString("dayname",null);
        String dt=prefs.getString("daytime",null);
        tba=prefs.getBoolean("bool",true);
        if(tname!=null) {
            if (!tname.equals(";;;;")&&tname.length()>0) {
                ns += tname;
                ts += "0";
                rs += "false";
                ss+="0";
            }
        }
        if (ns != null) {
            if(ns.length()>0) {
                current = accumulate(decompact(ns), decompact(ts), decompact(rs), decompact(ss));
            }
        }
        if(dn!=null){
            if(dn.length()>0) {
                StringTokenizer st1 = new StringTokenizer(dn, ":");
                StringTokenizer st2 = new StringTokenizer(dt, ":");
                int n = st1.countTokens();
                for (int i = 0; i < n; i++) {
                    ArrayList<Task> temp = accumulate(decompact(st1.nextToken()), decompact(st2.nextToken()));
                    days.add(temp);
                }
            }
        }
        if(rema!=null) {
            if (rema.length() > 0) {
                StringTokenizer st = new StringTokenizer(rema, ";");
                int n = st.countTokens();
                for (int i = 0; i < n; i++) {
                    StringTokenizer st2 = new StringTokenizer(st.nextToken(), "-");
                    int ii = Integer.parseInt(st2.nextToken());
                    String jj = st2.nextToken();
                    ArrayList<Task> temp=days.get(ii);
                    temp.remove(getText(temp).indexOf(jj));
                }

            }
        }

        if (current != null) {
            if (current.size() != 0) {
                for (int i = 0; i < current.size(); i++) {
                    addLayout(current.get(i).name);
                }
            }
        }
        else {
            current = new ArrayList<Task>();
        }
    }
    public void endDay(View view){
        if(!current.isEmpty()) {
            LinearLayout lt=(LinearLayout)findViewById(R.id.founder);
            for (int i = 0; i < current.size(); i++) {
                if(current.get(i).running){
                    current.get(i).seconds+=(System.currentTimeMillis()-current.get(i).start)/1000;
                }
                if(days.isEmpty()) {
                    ArrayList<Task> ad=new ArrayList<Task>();
                    Task temp=new Task(current.get(i).name);
                    temp.seconds=current.get(i).seconds;
                    ad.add(temp);
                    days.add(ad);
                    tba=false;
                }
                else{
                    if(!tba) {
                        if (getText(days.get(days.size() - 1)).contains(current.get(i).name)) {
                            ArrayList<Task> exp = days.get(days.size() - 1);
                            int index = getText(exp).indexOf(current.get(i).name);
                            exp.get(index).seconds += current.get(i).seconds;
                            days.set(days.size() - 1, exp);
                        } else {
                            Task temp = new Task(current.get(i).name);
                            temp.seconds = current.get(i).seconds;
                            days.get(days.size() - 1).add(temp);
                        }
                    }
                    else{
                        tba=false;
                        ArrayList<Task> ad=new ArrayList<Task>();
                        Task temp = new Task(current.get(i).name);
                        temp.seconds = current.get(i).seconds;
                        ad.add(temp);
                        days.add(ad);
                    }
                }
                lt.removeView(current.get(i).layout);
            }
            current=new ArrayList<Task>();
        }
        tba=true;
    }
    public void records(View view){
        Intent intent=new Intent(this,Records.class);
        String dn="";
        String dt="";
        for(int i=0;i<days.size();i++){
            dn+=compact(getText(days.get(i)))+":";
            dt+=compact(getTime(days.get(i)))+":";
        }
        intent.putExtra(recname,dn);
        intent.putExtra(tiname,dt);
        startActivity(intent);
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("names",compact(getText(current)));
        editor.putString("times",compact(getTime(current)));
        editor.putString("runs",compact(getRun(current)));
        editor.putString("start",compact(getStart(current)));
        editor.putBoolean("bool", tba);
        String dn="";
        String dt="";
        for(int i=0;i<days.size();i++){
            dn+=compact(getText(days.get(i)))+":";
            dt+=compact(getTime(days.get(i)))+":";
        }
        editor.putString("dayname",dn);
        editor.putString("daytime",dt);
        editor.apply();
    }
    public String compact(ArrayList<String> list){
        String ret="";
        for(int i=0;i<list.size();i++){
            ret+=list.get(i)+";";
        }
        return ret;
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
    public void taskAdd(View view){
        Intent intent=new Intent(this,TaskName.class);
        intent.putStringArrayListExtra(key,getText(current));
        startActivity(intent);
    }
    public ArrayList<String> getText(ArrayList<Task> cur){
        ArrayList<String> ret=new ArrayList<String>();
        for(int i=0;i<cur.size();i++){
            ret.add(cur.get(i).name);
        }
        return ret;
    }
    public ArrayList<String> getTime(ArrayList<Task> cur){
        ArrayList<String> ret=new ArrayList<String>();
        for(int i=0;i<cur.size();i++){
            ret.add(Long.toString(cur.get(i).seconds));
        }
        return ret;
    }
    public ArrayList<String> getStart(ArrayList<Task> cur){
        ArrayList<String> ret=new ArrayList<String>();
        for(int i=0;i<cur.size();i++){
            ret.add(Long.toString(cur.get(i).start));
        }
        return ret;
    }
    public ArrayList<String> getRun(ArrayList<Task> cur){
        ArrayList<String> ret=new ArrayList<String>();
        for(int i=0;i<cur.size();i++){
            if(cur.get(i).running){
                ret.add("true");
            }
            else{
                ret.add("false");
            }
        }
        return ret;
    }
    public ArrayList<Task> accumulate(ArrayList<String> names,ArrayList<String> times,ArrayList<String> runs, ArrayList<String> starts){
        ArrayList<Task> ret=new ArrayList<Task>();
        for(int i=0;i<names.size();i++){
            Task t=new Task(names.get(i));
            t.seconds=Long.parseLong(times.get(i));
            t.start=Long.parseLong(starts.get(i));
            if(runs.get(i).equals("true")){
                t.running=true;
            }
            else{
                t.running=false;
            }
            ret.add(t);
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
    public void addLayout(final String nm){
        final TextView watch=new TextView(this);
        watch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params.weight=1f;
        watch.setLayoutParams(params);
        if(current.get(getText(current).indexOf(nm)).start==0&&!(current.get(getText(current).indexOf(nm)).running)){
            int hours = 0;
            int minutes = 0;
            int secs = 0;
            String time
                    = String
                    .format(Locale.getDefault(),
                            "%d:%02d:%02d", hours,
                            minutes, secs);
            watch.setText(time);
        }
        else if(!(current.get(getText(current).indexOf(nm)).running)) {
            int hours = (int) current.get(getText(current).indexOf(nm)).seconds/ 3600;
            int minutes = (int) (current.get(getText(current).indexOf(nm)).seconds % 3600) / 60;
            int secs = (int) current.get(getText(current).indexOf(nm)).seconds % 60;
            String time
                    = String
                    .format(Locale.getDefault(),
                            "%d:%02d:%02d", hours,
                            minutes, secs);
            watch.setText(time);
        }
        else{
            watch.setText("Task on");
        }
        watch.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        current.get(getText(current).indexOf(nm)).tv=watch;
        TextView tkname=new TextView(this);
        tkname.setText(current.get(getText(current).indexOf(nm)).name);
        tkname.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tkname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
        tkname.setLayoutParams(params);
        Button start=new Button(this);
        start.setText("Start");
        start.setLayoutParams(params);
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!current.get(getText(current).indexOf(nm)).running) {
                    current.get(getText(current).indexOf(nm)).running = true;
                    current.get(getText(current).indexOf(nm)).start = System.currentTimeMillis();
                    watch.setText("Task on");
                }
            }
        });
        Button pause=new Button(this);
        pause.setText("Pause");
        pause.setLayoutParams(params);
        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (current.get(getText(current).indexOf(nm)).running){
                    current.get(getText(current).indexOf(nm)).running = false;
                    current.get(getText(current).indexOf(nm)).seconds += (System.currentTimeMillis() - current.get(getText(current).indexOf(nm)).start) / 1000;
                    int hours = (int) current.get(getText(current).indexOf(nm)).seconds / 3600;
                    int minutes = (int) (current.get(getText(current).indexOf(nm)).seconds % 3600) / 60;
                    int secs = (int) current.get(getText(current).indexOf(nm)).seconds % 60;
                    String time
                            = String
                            .format(Locale.getDefault(),
                                    "%d:%02d:%02d", hours,
                                    minutes, secs);
                    watch.setText(time);
                }
            }
        });
        Button saveReset=new Button(this);
        saveReset.setText("Save and Reset");
        saveReset.setLayoutParams(params);
        saveReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(current.get(getText(current).indexOf(nm)).running) {
                    current.get(getText(current).indexOf(nm)).running = false;
                    current.get(getText(current).indexOf(nm)).seconds += (System.currentTimeMillis() - current.get(getText(current).indexOf(nm)).start) / 1000;
                }
                if(days.isEmpty()){
                    ArrayList<Task> ad=new ArrayList<Task>();
                    Task temp=new Task(current.get(getText(current).indexOf(nm)).name);
                    temp.seconds=current.get(getText(current).indexOf(nm)).seconds;
                    ad.add(temp);
                    days.add(ad);
                    tba=false;
                }
                else {
                    if(!tba) {
                        if (getText(days.get(days.size() - 1)).contains(current.get(getText(current).indexOf(nm)).name)) {
                            ArrayList<Task> exp = days.get(days.size() - 1);
                            int index = getText(exp).indexOf(current.get(getText(current).indexOf(nm)).name);
                            exp.get(index).seconds += current.get(getText(current).indexOf(nm)).seconds;
                            days.set(days.size() - 1, exp);
                        } else {
                            Task temp = new Task(current.get(getText(current).indexOf(nm)).name);
                            temp.seconds = current.get(getText(current).indexOf(nm)).seconds;
                            days.get(days.size() - 1).add(temp);
                        }
                    }
                    else{
                        tba=false;
                        ArrayList<Task> ad=new ArrayList<Task>();
                        Task temp = new Task(current.get(getText(current).indexOf(nm)).name);
                        temp.seconds = current.get(getText(current).indexOf(nm)).seconds;
                        ad.add(temp);
                        days.add(ad);
                    }
                }
                current.get(getText(current).indexOf(nm)).seconds=0;
                current.get(getText(current).indexOf(nm)).start=0;
                int hours = (int) current.get(getText(current).indexOf(nm)).seconds/ 3600;
                int minutes = (int) (current.get(getText(current).indexOf(nm)).seconds % 3600) / 60;
                int secs = (int) current.get(getText(current).indexOf(nm)).seconds % 60;
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);
                watch.setText(time);
            }
        });
        Button reset=new Button(this);
        reset.setText("Reset");
        reset.setLayoutParams(params);
        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                current.get(getText(current).indexOf(nm)).running=false;
                current.get(getText(current).indexOf(nm)).seconds=0;
                current.get(getText(current).indexOf(nm)).start=0;
                int hours = (int) current.get(getText(current).indexOf(nm)).seconds/ 3600;
                int minutes = (int) (current.get(getText(current).indexOf(nm)).seconds % 3600) / 60;
                int secs = (int) current.get(getText(current).indexOf(nm)).seconds % 60;
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);
                watch.setText(time);
            }
        });
        LinearLayout l1=new LinearLayout(this);
        l1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout l2=new LinearLayout(this);
        l2.setOrientation(LinearLayout.VERTICAL);
        LinearLayout l3=new LinearLayout(this);
        l3.setOrientation(LinearLayout.VERTICAL);
        l1.addView(watch);
        l1.addView(tkname);
        l2.addView(start);
        l2.addView(pause);
        l3.addView(saveReset);
        l3.addView(reset);
        LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        params2.weight=1f;
        l1.setLayoutParams(params2);
        l2.setLayoutParams(params2);
        l3.setLayoutParams(params2);
        LinearLayout lh=new LinearLayout(this);
        lh.setOrientation(LinearLayout.HORIZONTAL);
        lh.addView(l1);
        lh.addView(l2);
        lh.addView(l3);
        Button remove=new Button(this);
        remove.setText("Remove");
        LinearLayout.LayoutParams params3=new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params3.weight=2f;
        LinearLayout.LayoutParams params4=new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params4.weight=1f;
        lh.setLayoutParams(params3);
        remove.setLayoutParams(params4);
        final LinearLayout son=new LinearLayout(this);
        son.setOrientation(LinearLayout.VERTICAL);
        son.addView(lh);
        son.addView(remove);
        LinearLayout.LayoutParams params5=new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        son.setLayoutParams(params5);
        final LinearLayout par=(LinearLayout)findViewById(R.id.founder);
        par.addView(son);
        current.get(getText(current).indexOf(nm)).layout=son;
        remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                par.removeView(son);
                current.remove(getText(current).indexOf(nm));
            }
        });
    }
}

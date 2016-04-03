package com.amathew.kidtimer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.amathew.kidtimer.counter.ConvertResult;
import com.amathew.kidtimer.counter.Counter;

public class DisplayMessageActivity extends AppCompatActivity {
    private static final String LOG_CAT = DisplayMessageActivity.class.getName();

    private static Counter counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String countExtra = intent.getStringExtra(MyActivity.EXTRA_COUNT);
        String timeExtra = intent.getStringExtra(MyActivity.EXTRA_TIME);
//        textView.setText(countExtra);

        ConvertResult convertResult = getCounter().startCount(countExtra, timeExtra);
        Log.e(LOG_CAT, convertResult.toString());
        if (convertResult.valid()) {
            Integer countVal = (int) convertResult.count().get();
            Integer timeVal = (int) convertResult.time().get();
            Log.e(LOG_CAT, countVal.toString());
            Log.e(LOG_CAT, timeVal.toString());
        }
        Log.e(LOG_CAT, "All messages" + convertResult.allMsgs());
//        com.amathew.kidtimer.counter.countdown(countVal, timeVal);
//        tempSc.countdown((Integer)convertResult.count().get(), (Integer)convertResult.time().get());

//        TextView textView = new TextView(this);
//        textView.setText((com.amathew.kidtimer.counter.a()));
//        textView.setTextSize(40);
//        setContentView(textView);

//        setContentView(R.layout.activity_display_message);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment()).commit();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Counter getCounter() {
        if (counter == null) {
            synchronized (this.getClass()) {
                if (counter == null) counter = new Counter(getApplicationContext());
            }
        }
        return counter;
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() { }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_display_message,
//                    container, false);
//            return rootView;
//        }
//    }
}

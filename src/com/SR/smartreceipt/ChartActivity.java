package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.SR.data.FeedReaderDbHelper;
import com.SR.data.User;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ChartActivity extends Activity {
    
    float costs[];

	int[] COLORS;
    
    FeedReaderDbHelper mDbHelper;
    User user;
    SQLiteDatabase db;
    
    ArrayList<String> groups_names;
    ArrayList<String> groups_costs;
    
    TextView total_cost;
    
    float cost;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		Bundle extras = getIntent().getExtras();
		
		COLORS = new int[6];
		COLORS[0]=getResources().getColor(R.color.brown);
		COLORS[1]=getResources().getColor(R.color.blue);
		COLORS[2]=getResources().getColor(R.color.light_green);
		COLORS[3]=getResources().getColor(R.color.purple);
		COLORS[4]=getResources().getColor(R.color.yellow);
		COLORS[5]=getResources().getColor(R.color.light_red);
		
		groups_names = extras.getStringArrayList("groups names");
		groups_costs = extras.getStringArrayList("groups costs");
		
	
		costs = new float[groups_costs.size()];
		
		for (int i=0; i<groups_costs.size(); i++){
			costs[i] = Float.parseFloat(groups_costs.get(i));
			cost += costs[i];
		}
		
		total_cost = (TextView)findViewById(R.id.cost);
		total_cost.setText("" + cost);
		
        
		LinearLayout linear=(LinearLayout) findViewById(R.id.categories);
		
		for (int i=0; i<groups_names.size(); i++){
			TextView group = new TextView(this);
			group.setText(groups_names.get(i) + ": " + groups_costs.get(i) + "€");
			group.setTextColor(COLORS[i]);
			linear.addView(group);
		}
		
		LinearLayout chart_activity=(LinearLayout) findViewById(R.id.chart_activity);
        costs=calculateData(costs);
        chart_activity.addView(new MyGraphview(this,costs,COLORS));
        


	}

    private float[] calculateData(float[] data) {
        float total=0;
        for(int i=0;i<data.length;i++)
        {
            total+=data[i];
        }
        for(int i=0;i<data.length;i++)
        {
        data[i]=360*(data[i]/total);
        Log.w("", "" + data[i]);
        }
        return data;

    }
	
	
	
	/**
	 * Set up the ActionBar, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Inflate the menu; this adds items to the action bar if it is present.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return true;
	}

	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
	    		Intent intent = new Intent(this, SearchActivity.class);
	    		startActivity(intent);
	            return true;
	        case R.id.action_logout:
	        	mDbHelper = new FeedReaderDbHelper(this);
	    		db = mDbHelper.getWritableDatabase();
	    		
	        	user = new User(db);
	        	user.userLogout(this);
	        	
	        	mDbHelper.close();
	        	
	        	Intent intent1 = new Intent(this, LoginActivity.class);
	    		startActivity(intent1);
	            return true;
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				//NavUtils.navigateUpFromSameTask(this);
				
				Intent upIntent = new Intent(this, SearchResultsActivity.class);
				upIntent.putExtras(getIntent().getExtras());
				NavUtils.navigateUpTo(this, upIntent);
				
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Set up the Overflow menu.
	 */
	private void getOverflowMenu() {

	     try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

    public class MyGraphview extends View
    {
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] value_degree;
        private int[] COLORS;
        RectF rectf = new RectF (10, 10, 200, 200);
        int temp=0;
        
        public MyGraphview(Context context, float[] values, int[] colors_table) {

            super(context);
            COLORS = colors_table;
            value_degree=new float[values.length];
            for(int i=0;i<values.length;i++)
            {
                value_degree[i]=values[i];
            }
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
                if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, 0, value_degree[i], true, paint);
                } 
                else
                {
                        temp += (int) value_degree[i - 1];
                        paint.setColor(COLORS[i]);
                        canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                }
            }
        }
    }
	
}

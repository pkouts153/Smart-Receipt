package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.SR.data.FeedReaderDbHelper;
import com.SR.data.User;
import com.SR.processes.MyApplication;

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
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;


/**
 * Activity that displays the chart screen for the selection results
 * 
 * @author Παναγιώτης Κουτσαυτίκης 8100062
 *
 */
public class ChartActivity extends Activity implements OnClickListener{
   
	/**
	 * The cost of each group that specifies the group area on the pie chart
	 */
    float costs[];

    /**
     * The colors to be displayed for the area of each group
     */
	int[] COLORS;
    
	// data variables
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
    User user;
    
    ArrayList<String> groups_names;
    ArrayList<String> groups_costs;
    
    // UI component
    TextView total_cost;
    ImageButton results;
    
    // the total cost that is displayed in the right upper corner
    float cost;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		try{
			results = (ImageButton)findViewById(R.id.results_button);
			results.setOnClickListener(this);
	
			// get all the color names from resources
			Field[] fields = R.color.class.getFields();
			String[] allColorNames = new String[fields.length];
			for (int  i =0; i < fields.length; i++) {           
				allColorNames[i] = fields[i].getName();
			}
	
			int resId;
			
			// initialize the colors with the names created above
			// the colors for the pie chart in the resources are 6 at this point
			COLORS = new int[allColorNames.length];
			for (int  i =0; i < allColorNames.length; i++) { 
				resId = getResources().getIdentifier(allColorNames[i], "color", this.getPackageName());
				COLORS[i] = getResources().getColor(resId);
			}
			
			Bundle extras = getIntent().getExtras();
			
			groups_names = extras.getStringArrayList("group_names");
			groups_costs = extras.getStringArrayList("group_cost");
	
			// find the total cost and set it in the total cost textview
			costs = new float[groups_costs.size()];
	
			for (int i=0; i<groups_costs.size(); i++){
				costs[i] = Float.parseFloat(groups_costs.get(i));
				cost += costs[i];
			}
	
			total_cost = (TextView)findViewById(R.id.cost);
			total_cost.setText("" + cost);
			
	        // create the frame with the group names and their costs
			LinearLayout linear=(LinearLayout) findViewById(R.id.categories);
			
			for (int i=0; i<groups_names.size(); i++){
				TextView group = new TextView(this);
				group.setText(groups_names.get(i) + ": " + groups_costs.get(i) + "€");
				group.setTextColor(COLORS[i]);
				linear.addView(group);
			}
			
			// create and add the pie chart
			LinearLayout chart_activity=(LinearLayout) findViewById(R.id.chart_activity);
	        costs=calculateData(costs);
	        chart_activity.addView(new PieChart(this,costs,COLORS));
		} catch (Exception e){
			//setContentView(R.layout.activity_search_results_no_tabs);
		}
	}

	/**
	 * Updates the values of the costs table with the values that are appropriate for the PieChart
	 * and specify the area of each group
	 * 
	 * @param data the costs table
	 * @return the updated costs table
	 */
    private float[] calculateData(float[] data) {
        float total=0;
        for(int i=0;i<data.length;i++)
            total+=data[i];
        
        for(int i=0;i<data.length;i++)
	        data[i]=360*(data[i]/total);
        
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
				// This action represents the Home or Up button which leads the user to the previous screen
				NavUtils.navigateUpFromSameTask(this);
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, SearchResultsActivity.class);
		Bundle b = getIntent().getExtras();
		b.putString("activity", "chart");
		intent.putExtras(b);
		startActivity(intent);
	}
	
	/**
	 * Class that represents a pie chart for the groups of the search results
	 * 
	 * @author Παναγιώτης Κουτσαυτίκης 8100062
	 *
	 */
    public class PieChart extends View {
    	/** Holds the color information of the chart */
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        
        /** The group costs */
        private float[] value_degree;
        
        /** The group colors */
        private int[] COLORS;
        
        /** Holds the coordinates of the chart */
        RectF rectf = new RectF (0, 0, 210, 210);
        
        int temp=0;
        
        /**
         * Class constructor
         * @param context
         * @param values	the costs of the groups
         * @param colors_table	the colors table
         */
        public PieChart(Context context, float[] values, int[] colors_table) {

            super(context);
            COLORS = colors_table;
            value_degree=values;
        }
        
        /**
         *  draw the pie chart
         *  
         *  @param canvas
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // set the color and position of each group in the chart
            for (int i = 0; i < value_degree.length; i++) {
            	if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, 0, value_degree[i], true, paint);
                } 
                else {
                    temp += (int) value_degree[i - 1];
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                }
            }
        }
    }
    
	@Override
    protected void onResume() {
    	super.onResume();
    	MyApplication.activityResumed();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MyApplication.activityPaused();
    
    	if (mDbHelper != null)
    		mDbHelper.close();
    }
}


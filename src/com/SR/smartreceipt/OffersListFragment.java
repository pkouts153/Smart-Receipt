package com.SR.smartreceipt;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OffersListFragment extends ListFragment{
	// the columns of the cursor
	static String[] columns;
	
	// the ui components to display each of the columns
	static int[] textviews;
	
	/**
	 * An easy adapter to map columns from the cursor to TextViews 
	 */
	static SimpleCursorAdapter simpleCursorAdapter;
	
	public static OffersListFragment newInstance(int section, Cursor cursor, Context context, ViewGroup container){

		OffersListFragment listFragment = new OffersListFragment();
	
		columns = cursor.getColumnNames();
		
		textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.discount,R.id.until_date,R.id.store_category};

		cursor.moveToFirst();
		
		simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.fragment_offers_list_row, cursor, columns, textviews, 0);
		simpleCursorAdapter.bindView(container, context, cursor);
        
        listFragment.setListAdapter(simpleCursorAdapter);
        
        return listFragment;
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        // Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.fragment_offers_list, container, false);
    	
        return view;
  
    }
    
    /**
     * Custom adapter to bind list rows to cursor and 
     * set CheckBoxes depending on the product's check condition
     * 
     * @author Panagiotis Koutsaftikis
     *
     */
    private class CustomListAdapter extends SimpleCursorAdapter {

		private Context context;
        private int id;
        private Cursor cursor;
        private int section;
        /**
         * class constructor
         */
        public CustomListAdapter(int section, Context con, int layout, Cursor c,
				String[] from, int[] to) {
			super(con, layout, c, from, to, 0);
			this.section = section;
			cursor = c;
			context = con;
			id = layout;
		}
        
        /**
         * Gets each row of the list, checks the product's check condition 
         * and sets the CheckBox accordingly
         */
        public View getView(int position, View v, ViewGroup parent)
        {
        	View mView = v ;
        	if(mView == null){
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }
        	
        	TextView hint = (TextView)mView.findViewById(R.id.store_category_hint);
        	
    		if (section==1)
    			hint.setText("store");
    		else
    			hint.setText("category");
    			
    		bindView(mView, context, cursor);
			return mView;
        }
    }
}

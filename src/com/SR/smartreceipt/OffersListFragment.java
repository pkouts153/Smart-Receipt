package com.SR.smartreceipt;

import com.SR.data.FeedReaderContract.FeedList;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class OffersListFragment extends ListFragment{
	// the columns of the cursor
	static String[] columns;
	//static String[] columns1;
	
	// the ui components to display each of the columns
	static int[] textviews;
	
	/**
	 * An easy adapter to map columns from the cursor to TextViews 
	 */
	static SimpleCursorAdapter simpleCursorAdapter;
	
	public static OffersListFragment newInstance(int section, Cursor cursor, Context context, ViewGroup container){

		//List<String> temp = new ArrayList<String>();
		OffersListFragment listFragment = new OffersListFragment();
	
		
		//columns = new String[cursor.getColumnCount()+1];
		columns = cursor.getColumnNames();
		
		/*columns1 = new String[cursor.getColumnCount()+1];
		
		for (int i=0; i<columns1.length; i++){
			if (i==cursor.getColumnCount()){
				if (section==1)
					columns1[i] = "category";
				else
					columns1[i] = "store";
			}
			else
				columns1[i]=columns[i];
		}*/
		
		cursor.moveToFirst();
		
		textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.discount,R.id.until_date,R.id.store_category};
		
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
        
        /**
         * class constructor
         */
        public CustomListAdapter(Context con, int layout, Cursor c,
				String[] from, int[] to) {
			super(con, layout, c, from, to, 0);
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
        	
        	CheckBox checkbox = (CheckBox)mView.findViewById(R.id.delete_product);
        	
    		if (cursor.moveToPosition(position))
    			if (cursor.getInt(cursor.getColumnIndexOrThrow(FeedList.IS_CHECKED))==1)
    				checkbox.setChecked(true);
    			
    		bindView(mView, context, cursor);
			return mView;
        }
    }
}

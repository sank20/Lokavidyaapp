package com.iitb.mobileict.lokavidya.ui.shotview;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;



public class ViewShotAdapter extends BaseAdapter {
	
    private Context context;
    private List<ViewShotItemData> itemList, itemListOrig;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

    public ViewShotAdapter(Context context, List<ViewShotItemData> itemList) {


        this.context = context;
        this.itemList = itemList;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sharedPref.edit();

    }

    public int getCount() {                        
        return itemList.size();
    }

    public Object getItem(int position) {     
        return itemList.get(position);
    }

	public void remove(ViewShotItemData item) {
		itemList.remove(item);
		notifyDataSetChanged();
	}


	public void remove(int position) {
		itemList.remove(position);
		notifyDataSetChanged();

		String project = sharedPref.getString("projectname","");


		for(int i=0;i<itemList.size();i++)
		{


			editor.putString(project + "image_path" + (i + 1), itemList.get(i).getImageRes());
			editor.putString(project+"image_name" + (i+1),itemList.get(i).getLabel());
			editor.commit();
		}
		editor.putInt(project,itemList.size());
		editor.putInt("check2", itemList.size());
		editor.commit();
	}
    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    { 
    	ViewShotItemData itemData = itemList.get(position);
        View v = new ViewShotItemView(this.context, itemData );

        v.setOnClickListener((OnClickListener) parent);
        if (((ViewShotItemView) v).getFavoriteView() != null) {
        	((ViewShotItemView) v).getFavoriteView().setId(ViewShotItemView.FAVICONID);
        	((ViewShotItemView) v).getFavoriteView().setOnClickListener((OnClickListener) parent);
        }
        v.setOnLongClickListener((OnLongClickListener) parent);//(OnLongClickListener) context);
        v.setOnTouchListener ((OnTouchListener) parent);
        return v;
    }
    

	public void set(int position, ViewShotItemData item) {
		itemList.set(position, item);
		notifyDataSetChanged();

		String project = sharedPref.getString("projectname","");

		for(int i=0;i<itemList.size();i++)
		{


			editor.putString(project + "image_path" + (i + 1), itemList.get(i).getImageRes());
			editor.putString(project+"image_name" + (i+1),itemList.get(i).getLabel());
			editor.commit();
		}


	}
	
	public void swapItems(int positionOne, int positionTwo) {
		ViewShotItemData temp = (ViewShotItemData) getItem(positionOne);
		set(positionOne, (ViewShotItemData) getItem(positionTwo));
		set(positionTwo, temp);
	} 
	
	 public Filter getFilter() {
 	    return new Filter() {

 	        @Override
 	        protected FilterResults performFiltering(CharSequence constraint) {
 	            final FilterResults oReturn = new FilterResults();

 	            final ArrayList<ViewShotItemData> results = new ArrayList<ViewShotItemData>();
 	            if (itemListOrig == null) itemListOrig = itemList;
 	            if (constraint != null) {
 	                if (itemListOrig != null && itemListOrig.size() > 0) {
 	                    for (final ViewShotItemData g : itemListOrig) {

 	                        if (g.getLabel().toLowerCase().contains(constraint.toString().toLowerCase()))
 	                            results.add(g);
 	                    }
 	                }
 	                oReturn.values = results;
 	            }
 	            return oReturn;
 	        }

 	        @SuppressWarnings("unchecked")
 	        @Override
 	        protected void publishResults(CharSequence constraint,
 	                FilterResults results) {
 	        	itemList = (ArrayList<ViewShotItemData>) results.values;
 	            notifyDataSetChanged();
 	        }
 	    };
 	} 
}

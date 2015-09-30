package com.iitb.mobileict.lokavidya.ui.shotview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;





class ViewShotItemView extends RelativeLayout implements DragSource, DropTarget {
	public static final int FAVICONID = -5;
	ViewShotItemData mitem;
	ImageView ivFavorite;
	
	public ImageView getFavoriteView() {
		return ivFavorite;
	}
	
	public ViewShotItemView(Context context, ViewShotItemData item)
	{
		super( context );
		mitem = item;

		setId(item.getItemId());
		
		ImageView ivBack = new ImageView(context);
		ivBack.setImageResource(item.getBackgroundRes());
		LayoutParams lp_ivBack = new LayoutParams(
				mitem.getWidth(), mitem.getHeight());

		lp_ivBack.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(ivBack, lp_ivBack);
		

		RelativeLayout panel = new RelativeLayout(context);
		panel.setPadding(mitem.getPadding(), mitem.getPadding(), mitem.getPadding(), mitem.getPadding());
		LayoutParams lp_PV = new LayoutParams(
				mitem.getWidth(), mitem.getHeight());

		lp_PV.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(panel, lp_PV);
		

		ImageView ivLogo;
		ivLogo = new ImageView(context);
		ivLogo.setId(100);
		LayoutParams lp_logo = new LayoutParams(
				mitem.getWidth() - 2*mitem.getPadding(), LayoutParams.WRAP_CONTENT);
		lp_logo.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp_logo.addRule(RelativeLayout.CENTER_HORIZONTAL);



		Bitmap myBitmap = BitmapFactory.decodeFile(item.getImageRes());
		ivLogo.setImageBitmap(myBitmap);

		
		
		
		panel.addView(ivLogo, lp_logo);
		
		if (item.getFavoriteStateShow()) {
			if(item.getFavoriteState())
			{
				ivFavorite = new ImageView(context);
				ivFavorite.setImageResource(item.getFavoriteOnRes());
				LayoutParams lp_ivFav = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp_ivFav.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp_ivFav.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				addView(ivFavorite, lp_ivFav);
			} else {
				ivFavorite = new ImageView(context);
				ivFavorite.setImageResource(item.getFavoriteOffRes());
				LayoutParams lp_ivFav = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp_ivFav.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp_ivFav.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				addView(ivFavorite, lp_ivFav);
			}
		}

		TextView textName = new TextView( context );
		textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

		textName.setTextColor(Color.GRAY);
		textName.setShadowLayer(2, 1, 1, Color.BLACK);

		textName.setText( item.getLabel());
		textName.setGravity(Gravity.CENTER);
		LayoutParams lp_text = new LayoutParams(
				mitem.getWidth() - 2*mitem.getPadding(), LayoutParams.WRAP_CONTENT);
		lp_text.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp_text.addRule(RelativeLayout.CENTER_HORIZONTAL);
		panel.addView(textName, lp_text);
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}


	public void onDrop(
			DragSource source,
			int x, int y, int xOffset, int yOffset, DragView dragView,
			Object dragInfo) {
	}

	public void onDragEnter(
			DragSource source,
			int x, int y, int xOffset, int yOffset, DragView dragView,
			Object dragInfo) {
	}

	public void onDragOver(
			DragSource source,
			int x, int y, int xOffset, int yOffset, DragView dragView,
			Object dragInfo) {

	}

	public void onDragExit(
			DragSource source,
			int x, int y, int xOffset, int yOffset, DragView dragView,
			Object dragInfo) {
	}

	public boolean acceptDrop(
			DragSource source,
			int x, int y, int xOffset, int yOffset, DragView dragView,
			Object dragInfo) {
		return true;
	}

	public Rect estimateDropLocation(
			DragSource source,
			int x, int y, int xOffset, int yOffset, DragView dragView,
			Object dragInfo, Rect recycle) {
		return null;
	}


	public boolean allowDrag() {

		return mitem.getAllowDrag();
	}

	public void setDragController(DragController dragger) {

	}


	public void onDropCompleted(View target, boolean success) {		

	}

	 public boolean onDown(MotionEvent e) {

	  return true;
	 }

	 public boolean onSingleTapUp(MotionEvent e) {

	  return true;
	 }

}

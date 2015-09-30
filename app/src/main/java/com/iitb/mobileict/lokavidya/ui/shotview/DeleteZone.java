package com.iitb.mobileict.lokavidya.ui.shotview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



public class DeleteZone extends ImageView implements DropTarget
{
	int m_nWidth = -1, 
		m_nHeight = -1;

	
	public DeleteZone (Context context) {
	   super  (context);
	}
	public DeleteZone (Context context, int w, int h) {
		   super  (context);
		   m_nWidth = w;
		   m_nHeight = h;
	}
	public DeleteZone (Context context, AttributeSet attrs) {
		super (context, attrs);
	}
	public DeleteZone (Context context, AttributeSet attrs, int style) 
	{
		super (context, attrs, style);
	}
	

	
	private DragController mDragController;
	private boolean mEnabled = true;
	

	
	public DragController getDragController ()
	{
	   return mDragController;
	}

	
	public void setDragController (DragController newValue)
	{
	   mDragController = newValue;
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (m_nWidth != -1 && m_nHeight != -1)
			this.setMeasuredDimension(m_nWidth, m_nHeight);
	}
	

	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
	        DragView dragView, Object dragInfo) {

	}
	

	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
	        DragView dragView, Object dragInfo) {

	    if (isEnabled ()) setImageLevel (2);
	}
	

	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
	        DragView dragView, Object dragInfo) {
		
	}
	

	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
	        DragView dragView, Object dragInfo) {
	    if (isEnabled ()) setImageLevel (1);
	}
	

	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
	        DragView dragView, Object dragInfo)
	{
	    return isEnabled ();
	}
	

	public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset,
	            DragView dragView, Object dragInfo, Rect recycle)
	{
	    return null;
	}
	

	public boolean isEnabled ()
	{
	   return mEnabled && (getVisibility () == View.VISIBLE);
	}
	

	
	public void setup (DragController controller)
	{
	    mDragController = controller;
	
	    if (controller != null) {
	       controller.addDropTarget (this);
	    }
	}
	

	
	public void toast (String msg)
	{
	    Toast.makeText (getContext (), msg, Toast.LENGTH_SHORT).show ();
	}
	

}

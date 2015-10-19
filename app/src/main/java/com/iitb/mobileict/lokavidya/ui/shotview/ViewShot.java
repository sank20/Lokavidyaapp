package com.iitb.mobileict.lokavidya.ui.shotview;



import android.content.Context;

import android.support.v7.internal.view.menu.MenuView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.GridView;
import android.widget.Toast;


public class ViewShot extends GridView
	implements DragController.DragListener, View.OnLongClickListener, View.OnTouchListener, View.OnClickListener {


	ViewShotAdapter m_gridviewAdapter		= null;

	private DragController 			mDragController			= null;
	private DynGridViewListener		mListener				= null;
	private boolean 				mLongClickStartsDrag 	= true;
	private DeleteZone 				mDeleteView				= null;
	boolean							mDragging				= false;
	private GestureDetector 		gestureDetector;
	private boolean					mSwipeEnabled			 = false;
	
	public ViewShot(Context context) {
		super(context);
		gestureDetector = new GestureDetector(context, new SwipeGestureDetector());
	}
	
	public void setSwipeEnabled(boolean mode) {
		mSwipeEnabled = mode;
	}
	
	public void setDeleteView(DeleteZone deleteView)
	{
	   mDeleteView = deleteView;
	   if (mDeleteView != null) {
           mDeleteView.setVisibility(View.INVISIBLE);
       }
	} 
	
	public void setDragController(DragController dragController) {
		if (dragController == null) {
			return;
		}
		mDragController = dragController;
		mDragController.setDragListener(this);
	}
	
	public DragController getDragController() {
		return mDragController;
	}
	
	private class SwipeGestureDetector extends SimpleOnGestureListener {

	    private static final int SWIPE_MIN_DISTANCE = 120;
	    private static final int SWIPE_MAX_OFF_PATH = 200;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    	try {
	    		float diffAbs = Math.abs(e1.getY() - e2.getY());
				float diffx = e1.getX() - e2.getX();
				float diffy = e1.getY() - e2.getY();

				if (diffx > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) return onLeftSwipe();
				else if (-diffx > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) return onRightSwipe();
				else if (diffy > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) return onUpSwipe();
				else if (-diffy > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) return onDownSwipe();
	    	} catch (Exception e) {

	    	}
	    	return false;
	    }
	}


	private boolean onLeftSwipe() {

		if (mListener != null) mListener.onSwipeLeft();
		return true;
	}

	private boolean onRightSwipe() {

		if (mListener != null) mListener.onSwipeRight();
		return true;
	}

	private boolean onUpSwipe() {

		if (mListener != null) mListener.onSwipeUp();
		return true;
	}
	private boolean onDownSwipe() {

		if (mListener != null) mListener.onSwipeDown();
		return true;
	}
	

    public interface DynGridViewListener {

        public void onItemClick(View v, int position, int id);

        public void onItemFavClick(View v, int positionForView, int id);

        public void onDragStart();

        public void onDragStop();

        public void onItemsChanged(int positionOne, int positionTwo);

        public void onItemDeleted(int position, int id);
        
		public void onSwipeLeft();
		
		public void onSwipeRight();
		
		public void onSwipeUp();
		
		public void onSwipeDown();
        
    }
   

    public void setDynGridViewListener(DynGridViewListener l) {
        mListener = l;
    }
    
    


    public void toast (String msg)
    {

        Toast.makeText (getContext (), msg, Toast.LENGTH_SHORT).show ();
    }
    

	public boolean startDrag (View v)
	{
	    DragSource dragSource = (DragSource) v;

	    mDragController.startDrag (v, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);

	    return true;
	}
	

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
		if (mDragController == null || !mDragging ) {
			return super.dispatchKeyEvent(event);
		}

		return mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
		
    }

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mDragController == null || !mDragging) {
			if (mDragController!=null) mDragController.onInterceptTouchEvent(ev);
			if (mSwipeEnabled) {
				gestureDetector.onTouchEvent(ev);
				return false;
			}
			else
				return super.onInterceptTouchEvent(ev);
		}
		else

    	return mDragController.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	if (mDragController == null || !mDragging) {

    			return super.onTouchEvent(ev);
    	}

        return mDragController.onTouchEvent(ev);
    }

    /**
     * 
     */
    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
    	if (mDragController == null || !mDragging) {
    		return super.dispatchUnhandledMove(focused, direction);	
    	}

    	return mDragController.dispatchUnhandledMove(focused, direction);
    }


    public void onDragStart(DragSource source, Object info, int dragAction) 
    {
    	if (mDragController == null ) return;

    	mDragging = true;


        
    	int numVisibleChildren = getChildCount();
    	for ( int i = 0; i < numVisibleChildren; i++ ) {
    		DropTarget view = (DropTarget) getChildAt (i);
    		mDragController.addDropTarget (view);
    	}
        

    	if (mDeleteView != null) {
    		mDeleteView.setVisibility(View.VISIBLE);
    		mDragController.addDropTarget (mDeleteView);
    	}
    	

        if (mListener!=null) mListener.onDragStart();
    }

    public void onDragEnd() 
    {
    	if (mDragController == null) return;
    	mDragging = false;
        mDragController.removeAllDropTargets ();
        if (mDeleteView != null) {
            mDeleteView.setVisibility(View.INVISIBLE);
        }

        if (mListener!=null) mListener.onDragStop();
    }

	public void onDropCompleted(View source, View target) {

		if (target.getId() != -1) {
			int positionOne = getPositionForView(source), positionTwo = getPositionForView(target);

	        if (mListener!=null) mListener.onItemsChanged(positionOne, positionTwo);
			((ViewShotAdapter) getAdapter()).swapItems(positionOne, positionTwo);
		}
		else {
			int position = getPositionForView(source);

	        if (mListener!=null) mListener.onItemDeleted(position, source.getId());
			((ViewShotAdapter) getAdapter()).remove(position);
		} 
	}

	public boolean onLongClick(View v) {
		if (mDragController!= null && mLongClickStartsDrag) {

	        if (!v.isInTouchMode()) {

	           return false;
	        }
	        return startDrag (v);
	    }


	    return false;
	}
	

	public boolean onTouch (View v, MotionEvent ev) 
	{
		final int action = ev.getAction();
		

	    if (mLongClickStartsDrag) {

	    	return false;
	    }

	    boolean handledHere = false;

	    if (mDragController != null && action == MotionEvent.ACTION_DOWN) {
	       handledHere = startDrag (v);
	    }	    
	    return handledHere;
	}

	public void onClick(View v) {
		if (mListener!=null) { 
			if (v.getId() == ViewShotItemView.FAVICONID)
				mListener.
				onItemFavClick(v, 
						getPositionForView(v), 
						v.getId());
			else {
				if (v!=null) mListener.
				onItemClick(v, 
					getPositionForView(v), 
					v.getId());
			}
		}
	}  
}

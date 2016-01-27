package com.iitb.mobileict.lokavidya.ui.shotview;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

//>>>>>>> 70badf7cbf99d940fd0e9dc02b0e259363158f1c
//=======
import android.content.SharedPreferences;


//>>>>>>> ef2a05a3746ae3de8c8c5ab687be17fe59263157
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.Share;
import com.iitb.mobileict.lokavidya.Stitch;
import com.iitb.mobileict.lokavidya.ui.UploadProject;
import com.iitb.mobileict.lokavidya.ui.ViewVideo;


public class 																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							ViewShots extends Activity implements   OnClickListener,
	ViewShot.DynGridViewListener{

	final static int		idTopLayout = Menu.FIRST + 100,
							idBack 		= Menu.FIRST + 101,
							idBotLayout	= Menu.FIRST + 102,
							idToggleScroll=Menu.FIRST+ 103,
							idToggleFavs = Menu.FIRST+ 104,
			                idToggleFavs2= Menu.FIRST+105;

	ViewShotAdapter m_gridviewAdapter		= null;
	DeleteZone 				mDeleteZone				= null;
	ArrayList<ViewShotItemData> itemList			= null;
	ViewShot gv						= null;
	boolean					mToggleScroll			= false,
							mToggleFavs				= false;

	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	String project;
	int savedstate;
	int check;
	final int REQUEST_CODE_1=1,REQUEST_CODE_2=2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPref.edit();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		project = intent.getStringExtra("projectname");

		editor.putString("projectname", project);
		editor.commit();



        RelativeLayout global_panel = new RelativeLayout (this);
		global_panel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		global_panel.setBackground(getResources().getDrawable(R.drawable.fresh_snow));

		setContentView(global_panel);


		RelativeLayout ibMenu = new RelativeLayout(this);
     	ibMenu.setId(idTopLayout);
		ibMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.line));
     	int ibMenuPadding = (int) 6;
     	ibMenu.setPadding(ibMenuPadding, ibMenuPadding, ibMenuPadding, ibMenuPadding);
     	RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
     	topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
     	global_panel.addView(ibMenu, topParams);

		TextView cTV = new TextView(this);
		cTV.setText(R.string.shot);
		cTV.setTextColor(Color.rgb(255, 255, 255));
		int nTextH =  18;
		cTV.setTextSize(nTextH);
		cTV.setTypeface(Typeface.create("arial", Typeface.BOLD));
		RelativeLayout.LayoutParams lpcTV = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpcTV.addRule(RelativeLayout.CENTER_IN_PARENT);
		ibMenu.addView(cTV, lpcTV);

		Button m_bCancel = new Button(this);
		m_bCancel.setId(idBack);
		m_bCancel.setOnClickListener((OnClickListener) this);
		m_bCancel.setText(R.string.preview);
		nTextH =  12;
        m_bCancel.setTextColor(Color.WHITE);
		m_bCancel.setTextSize(nTextH);
		m_bCancel.setTypeface(Typeface.create("arial", Typeface.BOLD));
		RelativeLayout.LayoutParams lpb =
			new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpb.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lpb.addRule(RelativeLayout.CENTER_VERTICAL);
		ibMenu.addView(m_bCancel, lpb);

		Button m_bToggleScroll = new Button(this);
		m_bToggleScroll.setId(idToggleScroll);
		m_bToggleScroll.setOnClickListener((OnClickListener) this);
		m_bToggleScroll.setText(R.string.share);
		nTextH =  12;
		m_bToggleScroll.setTextSize(nTextH);
		m_bToggleScroll.setTextColor(Color.WHITE);
		m_bToggleScroll.setTypeface(Typeface.create("arial", Typeface.BOLD));
		lpb = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpb.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lpb.addRule(RelativeLayout.CENTER_VERTICAL);
		ibMenu.addView(m_bToggleScroll, lpb);

		RelativeLayout ibMenuBot = new RelativeLayout(this);
		ibMenuBot.setId(idBotLayout);
		ibMenuBot.setBackgroundDrawable(getResources().getDrawable(R.drawable.line));
		ibMenuBot.setPadding(ibMenuPadding, ibMenuPadding, ibMenuPadding, ibMenuPadding);
		RelativeLayout.LayoutParams botParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		botParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		global_panel.addView(ibMenuBot, botParams);


		Button m_bToggleFavs = new Button(this);
		m_bToggleFavs.setId(idToggleFavs);
		m_bToggleFavs.setOnClickListener((OnClickListener) this);
		m_bToggleFavs.setText(R.string.stitch);

		nTextH =  12;
		m_bToggleFavs.setTextSize(nTextH);
        m_bToggleFavs.setTextColor(Color.WHITE);
		m_bToggleFavs.setTypeface(Typeface.create("arial", Typeface.BOLD));
		lpb = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpb.addRule(RelativeLayout.CENTER_VERTICAL);
		lpb.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		ibMenuBot.addView(m_bToggleFavs, lpb);

		Button m_bToggleFavs2 = new Button(this);
		m_bToggleFavs2.setId(idToggleFavs2);
		m_bToggleFavs2.setOnClickListener((OnClickListener) this);
		m_bToggleFavs2.setText(R.string.viewvideo);
		nTextH =  12;
		m_bToggleFavs2.setTextSize(nTextH);
		m_bToggleFavs2.setTextColor(Color.WHITE);
		m_bToggleFavs2.setTypeface(Typeface.create("arial", Typeface.BOLD));
		lpb = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpb.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lpb.addRule(RelativeLayout.CENTER_VERTICAL);
		ibMenuBot.addView(m_bToggleFavs2, lpb);

		mDeleteZone = new DeleteZone(this);

		LevelListDrawable a  = new LevelListDrawable();
		a.addLevel(0, 1, getResources().getDrawable(R.drawable.delete_icon));
		a.addLevel(1, 2, getResources().getDrawable(R.drawable.delete_icon_red));
		mDeleteZone.setImageDrawable(a);

		RelativeLayout.LayoutParams lpbDel =
				new RelativeLayout.LayoutParams(100,100);
		lpbDel.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lpbDel.addRule(RelativeLayout.CENTER_VERTICAL);
		ibMenuBot.addView(mDeleteZone, lpbDel);




		LinearLayout midLayout = new LinearLayout (this);
		midLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		midLayout.setOrientation(LinearLayout.VERTICAL);

		gv = new ViewShot(this);
		RelativeLayout.LayoutParams midParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		midParams.addRule(RelativeLayout.ABOVE,ibMenuBot.getId());
		midParams.addRule(RelativeLayout.BELOW, ibMenu.getId());
		global_panel.addView(gv, midParams);


		project=sharedPref.getString("projectname","");
		int count = sharedPref.getInt(project,0);
		String images[]= new String[count];
		String texts[] = new String[count];

		for(int i=0;i<count;i++)
		{

			images[i]= sharedPref.getString(project+"image_path" + (i+1),"");
			texts[i]=  sharedPref.getString(project+"image_name" + (i+1),"");

		}




		itemList = new ArrayList<ViewShotItemData>();
		for (int i=0;i<count;i++) {
			ViewShotItemData item = new ViewShotItemData(
					texts[i],
					150, 150, 15,
					R.drawable.item2,
					R.drawable.favon,
					R.drawable.favoff,
					true,
					mToggleFavs,
					images[i],
					i
					);


			itemList.add(item);
		}


		m_gridviewAdapter = new ViewShotAdapter(this, itemList);


		gv.setAdapter(m_gridviewAdapter);

		gv.setNumColumns(4);
		gv.setSelection(2);
		gv.setDynGridViewListener((ViewShot.DynGridViewListener) this);



        gv.setDeleteView(mDeleteZone);
        DragController dragController = new DragController(this);

        gv.setDragController(dragController);


        gv.setSwipeEnabled(mToggleScroll);

    }


	@Override
	protected void onStart() {
		super.onStart();
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = sharedPref.edit();
		project=sharedPref.getString("projectname", "");
		savedstate = sharedPref.getInt("savedView",0);
		int count = sharedPref.getInt(project,0);
		String images[];
		String texts[] ;



		if(savedstate==0){

			File sdCard = Environment.getExternalStorageDirectory();



			File audioDir = new File (sdCard.getAbsolutePath() + "/lokavidya"+"/"+project+"/audio");

			if(audioDir.exists()&& audioDir.isDirectory()) {

				File file[] = audioDir.listFiles();
				File image_file;



				count=0;
				for(int i=0;i<file.length;i++)


				{

					String imagefileName = file[i].getName();

					imagefileName=imagefileName.replace(".wav","");

					image_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+project+"/images", imagefileName + ".png");


					editor.putString(project+"image_path" + (i+1),image_file.getAbsolutePath());
					editor.putString(project+"image_name" + (i+1),imagefileName+".png");
					editor.commit();
					count++;
				}

				editor.putInt(project,count);
				editor.commit();



			}

            texts = new String[count];
			images = new String[count];




			for(int i=0;i<count;i++)
			{

				images[i]= sharedPref.getString(project+"image_path" + (i+1),"");
				texts[i]=  sharedPref.getString(project+"image_name" + (i+1),"");
				System.out.println("inside IF : image:"+ images[i]+ " text: "+ texts[i]);
			}

			itemList = new ArrayList<ViewShotItemData>();
			for (int i=0;i<count;i++) {
				ViewShotItemData item = new ViewShotItemData(
						texts[i],
						200, 200, 20,
						R.drawable.item2,
						R.drawable.favon,
						R.drawable.favoff,
						true,
						mToggleFavs,
						images[i],
						i

				);


				itemList.add(item);

		}
			check = itemList.size();





		}

		else{
			count = sharedPref.getInt(project,0);
			images= new String[count];
			texts = new String[count];

			for(int i=0;i<count;i++)
			{

				images[i]= sharedPref.getString(project+"image_path" + (i+1),"");
				texts[i]=  sharedPref.getString(project+"image_name" + (i+1),"");


			}




			itemList = new ArrayList<ViewShotItemData>();
			for (int i=0;i<count;i++) {
				ViewShotItemData item = new ViewShotItemData(
						texts[i],
						200, 200, 20,
						R.drawable.item2,
						R.drawable.favon,
						R.drawable.favoff,
						true,
						mToggleFavs,
						images[i],
						i
				);


				itemList.add(item);
			}

			check=itemList.size();


		}

		m_gridviewAdapter = new ViewShotAdapter(this, itemList);
		gv.setAdapter(m_gridviewAdapter);
		gv.setNumColumns(4);
		gv.setSelection(2);
		gv.setDynGridViewListener((ViewShot.DynGridViewListener) this);

		gv.setDeleteView(mDeleteZone);
		DragController dragController = new DragController(this);

		gv.setDragController(dragController);





	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();

		sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		project = sharedPref.getString("projectname", "");

		if (id == idBack) {
			if (check > 0) {

				Intent i = new Intent(ViewShots.this, ViewVideo.class);
				startActivity(i);
			} else {
				Toast.makeText(this, "Pick at least one shot", Toast.LENGTH_SHORT).show();
			}

		}
		if (id == idToggleFavs2) {




			File sdCard = Environment.getExternalStorageDirectory();
			File final_file = new File(sdCard.getAbsolutePath() + "/lokavidya/" + project + "/tmp/final.mp4");
			if (final_file.exists()){
				String newVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + project + "/tmp/final.mp4";
				System.out.println("PPPAAAATTTTHHHHHHH"+newVideoPath+"");

				//play using video view
				/*Intent intent = new Intent(getApplicationContext(), PlayVideo.class);
				intent.putExtra("path", newVideoPath);
				startActivity(intent);*/

				//Earlier code
				/*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
				intent.setDataAndType(Uri.parse(newVideoPath), "video");
				startActivity(intent);*/

				//recent changes meant to handle view video crash
				Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
				File file = new File(newVideoPath);
				String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
				String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
				myIntent.setDataAndType(Uri.fromFile(file), mimetype);
				startActivity(myIntent);

			}
			else{
				if(check>0){
					String project;
					Intent intent = getIntent();
					project = intent.getStringExtra("projectname");


					Intent i = new Intent(getApplicationContext(), Stitch.class);
					i.putExtra("projectname", project);
					startActivityForResult(i, REQUEST_CODE_1);



				}
				else{
					Toast.makeText(this, "Pick at least one shot", Toast.LENGTH_SHORT).show();

				}

			}

		}

		if (id == idToggleScroll) {
			if (check > 0) {
				final String sharevid = getString(R.string.shareVideo);
				final String shareproj = getString(R.string.shareProject);
				final String uploadvid= getString(R.string.uploadVideo);

				AlertDialog.Builder builderShare = new AlertDialog.Builder(ViewShots.this);
				List<String> features = new ArrayList<String>();
				//features.add(sharevid);
				features.add(sharevid);
				features.add(shareproj);
                features.add(uploadvid);
				final CharSequence[] y = features.toArray(new CharSequence[features.size()]);
				builderShare.setTitle(R.string.whatShareDialog)
						.setItems(y, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogShare, int whichShare) {
                                if (whichShare == 2) {
                                    File sdCard = Environment.getExternalStorageDirectory();
                                    File final_file = new File(sdCard.getAbsolutePath() + "/lokavidya/" + project + "/tmp/final.mp4");
                                    if (final_file.exists()) {
										Intent upload= new Intent(getThisActivity(), UploadProject.class);
                                        startActivity(upload);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please stitch the project", Toast.LENGTH_SHORT).show();

                                    }

                                } else {
                                    System.out.println(project + whichShare);
                                    Share.SendOptions(whichShare, getThisActivity(), getApplicationContext(), project);
                                }
                            }
                        });
				builderShare.create().show();
			}
			else{
				Toast.makeText(this, "Empty Project", Toast.LENGTH_SHORT).show();
			}
		}
	if (id == idToggleFavs) {

			if (check > 0) {
				String project;
				Intent intent = getIntent();
				project = intent.getStringExtra("projectname");

				Intent i = new Intent(getApplicationContext(), Stitch.class);
				i.putExtra("projectname", project);
				startActivityForResult(i, REQUEST_CODE_2);

			}else
			 {
				Toast.makeText(this, "Pick at least one shot", Toast.LENGTH_SHORT).show();
			}
		}
	}
	public void onItemClick(View v, int position, int id) {
		String text = "Click on:"+id+ " " +
				((ViewShotItemData)m_gridviewAdapter.getItem(position)).getLabel();

	}

	public void onItemFavClick(View v, int position, int id) {
		itemList.get(position).setFavoriteState(!itemList.get(position).getFavoriteState());
		m_gridviewAdapter.notifyDataSetChanged();
		gv.invalidateViews();

		String text = "Item:"+position+ " fav state:" +
				((ViewShotItemData)m_gridviewAdapter.getItem(position)).getFavoriteState();

	}

	public void onDragStart() {
	}

	public void onDragStop() {
	}

	public void onItemsChanged(int positionOne, int positionTwo) {
		String text = "You've changed item " + positionOne + " with item "+ positionTwo;

	}

	public void onItemDeleted(int position, int id) {
		String text = "You've deleted item " + id + " " +
				((ViewShotItemData)m_gridviewAdapter.getItem(position)).getLabel();

	}

	public void onSwipeLeft() {
		String text = "Swipe LEFT detected";

	}

	public void onSwipeRight() {
		String text = "Swipe RIGHT detected";

	}


	public void onSwipeUp() {
		String text = "Swipe UP detected";

	}

	public void onSwipeDown() {
		String text = "Swipe DOWN detected";

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		if(requestCode==REQUEST_CODE_1)
		{
			String newVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + project + "/tmp/final.mp4";
			System.out.println("PPPAAAATTTTHHHHHHH"+newVideoPath+"");

			//play using video view
				/*Intent intent = new Intent(getApplicationContext(), PlayVideo.class);
				intent.putExtra("path", newVideoPath);
				startActivity(intent);*/

			//Earlier code
				/*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
				intent.setDataAndType(Uri.parse(newVideoPath), "video");
				startActivity(intent);*/

			//recent changes meant to handle view video crash
			Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
			File file = new File(newVideoPath);
			String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
			String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			myIntent.setDataAndType(Uri.fromFile(file),mimetype);
			startActivity(myIntent);

		}


	}
	public Activity getThisActivity(){
		return this;
	}

	}

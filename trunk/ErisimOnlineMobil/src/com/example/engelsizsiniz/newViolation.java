package com.example.engelsizsiniz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import overlay.ItemOverlay;

import com.example.adress.otomatikAdres;
import com.example.engelsizsiniz.login.LoginUser;
import com.example.engelsizsiniz.registration.RegisterUser;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.MailTo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class newViolation extends MapActivity {

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 100; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 60000; // in Milliseconds
	public static double latitude, longitude;

	protected LocationManager locationManager;
	protected Location location;
	protected GeoPoint point;
	protected Button retrieveLocationButton;
	protected MapView mapView;
	protected MapController mapController;
	protected ItemOverlay itemoverlay;
	protected List<Overlay> mapOverlays;

	//protected Spinner disabilityType, city, district, street;
	protected Spinner disabilityType, avValSpin;
	public static int spinPos = 0;
	protected Button backMenu, uploadButton, photoButton, submitButton;
	protected EditText noteText, titleText, avValText;
	protected ImageView  imageView;
	protected TextView path, adres;
	protected Uri imageUri;

	public static String fileName;
	public static String filePathIntent ="", titleIntent="", noteIntent="";
	public static String noteDB, titleDB, spinnerDB, filePathDB, idDB, usernameDB, usermailDB;
	public static ArrayList<String> fileDeleted = new ArrayList<String>();
	public static int avCount;

	public static HashMap avListHash = new HashMap ();

	public otomatikAdres otm;
	protected static File photo;

	JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog;

	public ArrayList<String> avList = new ArrayList();

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TERMS = "terms";
	private static final String TAG_TERMSID = "term_id";
	private static final String TAG_NAME = "name";

	// products JSONArray
	JSONArray products = null;


	private static String url_newViolation = "http://swe.cmpe.boun.edu.tr/fall2012g4/newViolation.php";
	private static String url_all_AVtypes = "http://swe.cmpe.boun.edu.tr/fall2012g4/avTypes.php";
	private static String url_all_Violations = "http://swe.cmpe.boun.edu.tr/fall2012g4/allAVID.php";
	private static String url_Term = "http://swe.cmpe.boun.edu.tr/fall2012g4/newTerm.php";
	private static String url_Pos = "http://swe.cmpe.boun.edu.tr/fall2012g4/newPos.php";
	private static String url_TermMeta = "http://swe.cmpe.boun.edu.tr/fall2012g4/newTermMeta.php";
	private static String url_subscribeNewViolation = "http://swe.cmpe.boun.edu.tr/fall2012g4/subscribeNewViolation.php";

	public static String streetName ="", districtName ="", cityName = "", countryName = "", postCode = "", mahalle = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//AssetManager mngr = getApplicationContext().getAssets();
		//otm = new otomatikAdres(mngr);
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
		{
			filePathIntent = savedInstanceState.getString("filePathIntent");
			filePathDB = filePathIntent;
			titleIntent =  savedInstanceState.getString("title");
			noteIntent = savedInstanceState.getString("note");
			spinPos = savedInstanceState.getInt("spinner");
			idDB = savedInstanceState.getString("id");
			usernameDB = savedInstanceState.getString("username");
			usermailDB = savedInstanceState.getString("usermail");
		}
		else{
			idDB = getIntent().getExtras().getString("id");
			usernameDB = getIntent().getExtras().getString("username");
			usermailDB = getIntent().getExtras().getString("usermail");
			spinPos = 0;
			filePathDB = "";
			filePathIntent = "";
			titleIntent = "";
			noteIntent = "";
		}

		setContentView(R.layout.activity_new_violation);
		defineGUI();
		setListeners();
		setMap();
		showCurrentLocation();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("filePathIntent", filePathIntent);
		outState.putString("title", titleText.getText().toString());
		outState.putString("note", noteText.getText().toString());
		outState.putInt("spinner", spinPos);
		outState.putString("id", idDB);
		outState.putString("username", usernameDB);
		outState.putString("usermail", usermailDB);
		//savedInstanceState.putInt("title", );
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_violation, menu);
		return true;
	}

	protected void setListeners() {


		uploadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, 1);
			}

		});


		submitButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				//your network connection goes here
				submission();
			}
		});



		photoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
				fileName = "IMG_" + dateFormat.format(new Date()) + ".jpg";
				fileName = fileName.replaceAll("-", "_");
				photo = new File(Environment.getExternalStorageDirectory(),fileName);
				filePathIntent = photo.getAbsolutePath();
				filePathDB = filePathIntent;
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				startActivityForResult(i, 2);
			}
		});

		/*
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		      public void onCheckedChanged(RadioGroup arg0, int id) {
		        switch (id) {
		        case R.id.currentLocation:
		        	setSpinners(false);
		          break;
		        case R.id.otherLocation:
		        	setSpinners(true);
		          break;
		        default:
		          break;
		        }
		      }
		    });
		 */

		disabilityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
				// Your code here
				spinPos = i;
				createAVInput(spinPos);
			} 

			public void onNothingSelected(AdapterView<?> adapterView) {
				spinPos = 0;
				return;
			} 
		}); 

	}

	public void createAVInput(int pos) {

		
		// bu noktada se�ili av tipine g�re textbox veya spinner g�z�kecek ve i�erisinde valuelar olacak
		if (pos != 0) {
			if(pos % 2 == 0)
			{
				avValSpin.setVisibility(View.VISIBLE);
				avValText.setVisibility(View.GONE);
			}
			else {
				avValSpin.setVisibility(View.GONE);
				avValText.setVisibility(View.VISIBLE);
			}
		}

	}


	public void submission () {
		//create toast to show message
		Toast toast;

		//get values
		titleDB = this.titleText.getText().toString();
		noteDB = this.noteText.getText().toString();
		spinnerDB = this.disabilityType.getSelectedItem().toString();

		//check values
		if (titleDB.length() < 1)
		{
			toast = Toast.makeText(getApplicationContext(), "Ba�l�k en az 1 karakterden olu�mal�d�r", Toast.LENGTH_SHORT);
			toast.show();
		}

		else if (noteDB.length() < 1)
		{
			toast = Toast.makeText(getApplicationContext(), "L�tfen a��klama giriniz", Toast.LENGTH_SHORT);
			toast.show();
		}


		else if (spinnerDB == null || spinnerDB == "")
		{
			toast = Toast.makeText(getApplicationContext(), "AV Tipi Se�iniz", Toast.LENGTH_SHORT);
			toast.show();
		}

		else if (filePathDB == null || filePathDB == "")
		{
			toast = Toast.makeText(getApplicationContext(), "Resim Se�iniz", Toast.LENGTH_SHORT);
			toast.show();
		}

		else
		{
			// values correct save it to external hd of android
			new LoadAllAV().execute();
			//uploadFile();
			
			
		}
	}


	public void setSpinners(boolean status)
	{
		/*
		if(status) {
			city.setEnabled(true);
			district.setEnabled(true);
			street.setEnabled(true);
		}
		else {
			city.setEnabled(false);
			district.setEnabled(false);
			street.setEnabled(false);
		}
		 */

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		InputStream inputStream = null;

		if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			filePathDB = picturePath;
			cursor.close();
			String [] pictureArray = picturePath.toString().split("/");
			//path.setText(selectedImage.getPath());
			Bitmap thumbnail;
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();  
			bitmapOptions.inSampleSize = 4;  
			thumbnail = BitmapFactory.decodeFile(picturePath, bitmapOptions);
			imageView.setImageBitmap(thumbnail);
		}

		else if (requestCode == 2 && resultCode == RESULT_OK) {

			if(data != null)
			{
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				bm = Bitmap.createScaledBitmap(bm,100, 100,true);
				imageView.setImageBitmap(bm); // Display image in the View
			}
		}
	}


	protected void setMap ()
	{

		try {
			LocationListener mylocationListener = new MyLocationListener();

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			//get first location
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, true);
			System.out.println(provider);
			//locationManager.requestSingleUpdate(provider, mylocationListener);
			locationManager.requestLocationUpdates(
					provider,
					MINIMUM_TIME_BETWEEN_UPDATES,
					MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
					mylocationListener
					);

			location = locationManager.getLastKnownLocation(provider);
			//float val = location.getAccuracy();
			//System.out.println("afdafa " + val );


			mapView = (MapView) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			mapController = mapView.getController();

			mapOverlays = mapView.getOverlays();
			Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
			itemoverlay = new ItemOverlay(drawable, this);
		} catch (NotFoundException e) {
			Toast.makeText(newViolation.this, "L�tfen Ba�lant� Ayarlar�n�z� Kontrol Ediniz",
					Toast.LENGTH_LONG).show();
		}

		catch (Exception e) {
			Toast.makeText(newViolation.this, "L�tfen Ba�lant� Ayarlar�n�z� Kontrol Ediniz",
					Toast.LENGTH_LONG).show();
		}

	}



	protected void showCurrentLocation() {

		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			String message = String.format(
					"Koordinatlar \n boylam: %1$s \n enlem : %2$s",
					longitude, latitude
					);
			Toast.makeText(newViolation.this, message,
					Toast.LENGTH_LONG).show();

			updateMap(latitude, longitude);
		}

	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			/*String message = String.format(
					"Yeni koordinat \n Boylam: %1$s \n Enlem: %2$s",
					location.getLongitude(), location.getLatitude()
					);*/
			//updateMap(location.getLatitude(), location.getLongitude());
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void updateMap(double latitude, double longitude)
	{
		point = new GeoPoint(
				(int) (latitude * 1E6), 
				(int) (longitude * 1E6));

		mapController.animateTo(point);
		//add overlay items
		String location = getLocation();
		if(location != ""){
			OverlayItem overlayitem = new OverlayItem(point, "Bulundu�unuz Adres" , getLocation());
			itemoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemoverlay);
		}
		// zoom to position
		mapController.setZoom(17);
		//reload map
		mapView.invalidate();
	}

	protected String getLocation() {

		try {
			//forDongusuAdress();
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			Address add = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
			int value = add.getMaxAddressLineIndex();
			StringBuffer str = new StringBuffer();

			for (int i = 0; i < value; i ++) {
				str.append(add.getAddressLine(i));
				str.append(" ");
			}

			if (value > 1)
				streetName = add.getAddressLine(0) + add.getAddressLine(1);
			else
				streetName = add.getAddressLine(0);

			adres.setText(str.toString());

			if (add.getCountryName() != null)
				countryName = add.getCountryName();
			if (add.getPostalCode() != null)
				postCode = add.getPostalCode();
			if (add.getSubLocality() != null)
				mahalle = add.getSubLocality();
			if (add.getLocality() != null)
				districtName = add.getLocality();


			String val = add.getAddressLine(value - 1);
			if (val.contains("/")) {
				String[] strings = val.split("/");
				cityName = strings[strings.length - 1];
			}

			String result = str.toString();
			return result;
		} 
		catch (Exception e) {
			Toast.makeText(newViolation.this, "Adres Bilgisi Al�namad�",
					Toast.LENGTH_LONG).show();
			// closing this screen
			backMenu();
			return null;
		}
	}

	public void backMenu ()
	{
		Toast toast;
		toast = Toast.makeText(getApplicationContext(), "Eklenemedi. L�tfen ba�lant�n�z� kontrol ediniz.", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
		Intent myIntent = new Intent(getApplicationContext(), home.class);
		startActivityForResult(myIntent, 0);
		finish();
	}

	protected void defineGUI(){
		new LoadAVTypes().execute();
		disabilityType = (Spinner) findViewById(R.id.disabilitySpin);
		avValSpin = (Spinner) findViewById(R.id.AVValspinner);
		avValText = (EditText) findViewById(R.id.AVValeditText);
		backMenu = (Button) findViewById(R.id.mainmenu);
		uploadButton = (Button) findViewById(R.id.uploadButton);
		submitButton = (Button) findViewById(R.id.button1);
		photoButton = (Button) findViewById(R.id.photoButton);
		path = (TextView) findViewById(R.id.filePath);
		adres = (TextView) findViewById(R.id.bulunduguAdres);
		imageView = (ImageView) findViewById(R.id.imageView1);
		noteText = (EditText) findViewById(R.id.noteText);
		titleText = (EditText) findViewById(R.id.titleText);

		if(noteIntent != null || noteIntent != "")
		{
			noteText.setText(noteIntent);
		}

		if(titleIntent != null || titleIntent != "")
		{
			titleText.setText(titleIntent);
		}
		if (filePathIntent != null || filePathIntent != "")
		{
			Bitmap thumbnail;
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();  
			bitmapOptions.inSampleSize = 16;  
			thumbnail = BitmapFactory.decodeFile(filePathIntent, bitmapOptions);
			imageView.setImageBitmap(thumbnail);
		}
		//city.setEnabled(false);
		//district.setEnabled(false);
		//street.setEnabled(false);
	}

	protected void setSpinnerAdapters()
	{
		/*
		//city value
		String[] city_spinner=new String[otm.iller.size()];
		//Toast.makeText(newViolation.this, otm.iller.get(1).getIlAdi() ,Toast.LENGTH_LONG).show();
		for (int i = 0 ; i < otm.iller.size(); i ++)
		{
			city_spinner[i] = otm.iller.get(i).getIlAdi();
		}
		ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, city_spinner);
		city.setAdapter(cityadapter);

		//district value
		String[] dist_spinner=new String[1];
		dist_spinner[0] = districtName;
		ArrayAdapter<String> distadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dist_spinner);
		district.setAdapter(distadapter);

		//street value
		String[] street_spinner = new String[1];
		street_spinner[0] = streetName;
		ArrayAdapter<String> streetadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, street_spinner);
		street.setAdapter(streetadapter);
		 */
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class submitViolation extends AsyncTask<String, String, String> {

		JSch jsch;
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(newViolation.this);
			pDialog.setMessage("Violation Ekleniyor...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		public String convertPostName (String s){

			s = s.toLowerCase();
			s = s.replaceAll(" ", "-");
			s = s.replace("�", "u");
			s = s.replace("�", "i");
			s = s.replace("�", "o");
			s = s.replace("�", "s");
			s = s.replace("�", "g");
			s = s.replace("�", "c");
			s = s.replace("�", "g");
			return s;
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {

			Date date = new Date( );
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
			String time = ft.format(date).toString();
			System.out.println(time);
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("post_author", idDB));
			params.add(new BasicNameValuePair("post_date", time));
			params.add(new BasicNameValuePair("post_date_gmt", time));
			params.add(new BasicNameValuePair("post_content", noteDB));
			params.add(new BasicNameValuePair("post_title", titleDB));
			params.add(new BasicNameValuePair("post_status", "publish"));
			params.add(new BasicNameValuePair("comment_status", "open"));
			params.add(new BasicNameValuePair("ping_status", "open"));
			params.add(new BasicNameValuePair("post_name", convertPostName(titleDB)));
			params.add(new BasicNameValuePair("post_parent", "0"));
			params.add(new BasicNameValuePair("guid", "http://swe.cmpe.boun.edu.tr/fall2012g4/?post_type=ad_listing&#038;p=" + avCount));
			params.add(new BasicNameValuePair("menu_order", "0"));
			params.add(new BasicNameValuePair("post_type", "ad_listing"));
			params.add(new BasicNameValuePair("post_mime_type", ""));
			params.add(new BasicNameValuePair("comment_count", "0"));
			//params.add(new BasicNameValuePair("display_name", usernameDB));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_newViolation,
					"GET", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					if(submitForAttachment()){
						new insertCategory().execute();
					}
				} else {
					// failed to create product
					backMenu();
				}
			} catch (JSONException e) {
				backMenu();
				e.printStackTrace();
			}

			return null;
		}

		public void uploadFile(){

			String SFTPHOST = "swe.cmpe.boun.edu.tr";
			int    SFTPPORT = 22;
			String SFTPUSER = "fall2012g4";
			String SFTPPASS = "axc25";
			String SFTPWORKINGDIR = "/home/fall2012g4/www/wp-content/uploads/2012/11/";
			Session     session     = null;
			Channel     channel     = null;
			ChannelSftp channelSftp = null;
			try{
				jsch = new JSch();
				session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
				session.setPassword(SFTPPASS);
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				config.put("compression.s2c", "zlib,none");
				config.put("compression.c2s", "zlib,none");
				session.setConfig(config);
				session.connect();
				channel = session.openChannel("sftp");
				channel.connect();
				channelSftp = (ChannelSftp)channel;
				channelSftp.cd(SFTPWORKINGDIR);
				//File f = new File(filePathDB);
				File f = createResizedCopy(210, 210, true);
				channelSftp.put(new FileInputStream(f), f.getName());
				//f.delete();
				f = createResizedCopy(75, 75, false);
				channelSftp.put(new FileInputStream(f), f.getName());

				channelSftp.disconnect();
				session.disconnect();
				//f.delete();
			}catch(Exception ex){
				//backMenu();
				ex.printStackTrace();
			}
		}

		public File createResizedCopy(int scaledWidth, int scaledHeight, boolean original)
		{
			Bitmap scaledphoto = null;
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			int height = scaledHeight;
			int width = scaledWidth;    

			Bitmap photo = BitmapFactory.decodeFile( filePathDB );
			scaledphoto = Bitmap.createScaledBitmap(photo, height, width, true);

			try {
				if (!original){
					scaledphoto.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
					String[] names = filePathDB.split("/");
					String fileNameValue = names[names.length-1];
					String[] onlyname = fileNameValue.split("\\.");
					fileNameValue = onlyname[0];
					fileNameValue = fileNameValue.replaceAll("-", "_");
					File f = new File(Environment.getExternalStorageDirectory(),fileNameValue+"-"+scaledWidth+"x"+scaledHeight+".jpg");
					f.createNewFile();
					fileDeleted.add(f.getAbsolutePath());
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
					return f;
				}
				else {
					scaledphoto.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
					String[] names = filePathDB.split("/");
					String fileNameValue = names[names.length-1];
					fileNameValue = fileNameValue.replaceAll("-", "_");
					File f = new File(Environment.getExternalStorageDirectory(),fileNameValue);
					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
					return f;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				backMenu();
				return null;
			}
		}


		public boolean submitForAttachment(){
			Date date = new Date( );
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
			String time = ft.format(date).toString();
			String[] names = filePathDB.split("/");
			String fileNameValue = names[names.length-1];
			fileNameValue = fileNameValue.replaceAll("-", "_");
			uploadFile();
			// Building Parameters

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("post_author", idDB));
			params.add(new BasicNameValuePair("post_date", time));
			params.add(new BasicNameValuePair("post_date_gmt", time));
			params.add(new BasicNameValuePair("post_content", ""));
			params.add(new BasicNameValuePair("post_title", titleDB));
			params.add(new BasicNameValuePair("post_status", "inherit"));
			params.add(new BasicNameValuePair("comment_status", "open"));
			params.add(new BasicNameValuePair("ping_status", "open"));
			params.add(new BasicNameValuePair("post_name", convertPostName(titleDB)));
			params.add(new BasicNameValuePair("post_parent", Integer.toString(avCount)));
			params.add(new BasicNameValuePair("guid", "http://swe.cmpe.boun.edu.tr/fall2012g4/wp-content/uploads/2012/11/" + fileNameValue));
			params.add(new BasicNameValuePair("menu_order", "0"));
			params.add(new BasicNameValuePair("post_type", "attachment"));
			params.add(new BasicNameValuePair("post_mime_type", "image/jpeg"));
			params.add(new BasicNameValuePair("comment_count", "0"));
			//params.add(new BasicNameValuePair("display_name", usernameDB));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_newViolation,
					"GET", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			int success;
			try {
				success = json.getInt(TAG_SUCCESS);
				if (success == 1)
					return true;
				else{
					backMenu();
					return false;
				}
			} catch (JSONException e) {
				backMenu();
				return false;
			}
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			Toast toast;
			toast = Toast.makeText(getApplicationContext(), "Violation Eklendi", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			deleteFiles();

			Intent myIntent = new Intent(getApplicationContext(), home.class);
			startActivityForResult(myIntent, 0);
			finish();
		}

		public void deleteFiles()
		{
			File file = null;
			for(int i = 0; i < fileDeleted.size(); i ++){
				file = new File(fileDeleted.get(i));
				file.delete();
			}
		}

	}


	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAVTypes extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				// getting JSON string from URL
				JSONObject json = jsonParser.makeHttpRequest(url_all_AVtypes, "GET", params);
				// Check your log cat for JSON reponse
				//Log.d("All Products: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_TERMS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						//String id = c.getString(TAG_TERMSID);
						String name = c.getString(TAG_NAME);
						String ID = c.getString("term_id");

						avListHash.put(new Integer(i), ID);
						//System.out.println(name);

						// creating new HashMap
						//HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						//map.put(TAG_TERMSID, id);
						//map.put(TAG_NAME, name);

						// adding HashList to ArrayList
						avList.add(name);
					}
				} else {
					// do nothing
					backMenu();
				}
			} catch (JSONException e) {
				backMenu();
			}

			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					if(avList != null) {
						String[] avListString=new String[avList.size()];
						//Toast.makeText(newViolation.this, otm.iller.get(1).getIlAdi() ,Toast.LENGTH_LONG).show();
						for (int i = 0 ; i < avList.size(); i ++)
						{
							avListString[i] = avList.get(i);
						}
						ArrayAdapter<String> avAdapter = new ArrayAdapter<String>(newViolation.this,
								android.R.layout.simple_spinner_item, avListString);
						disabilityType.setAdapter(avAdapter);
						disabilityType.setSelection(spinPos);

					}
				}
			});

		}
	}

	class LoadAllAV extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				// getting JSON string from URL
				JSONObject json = jsonParser.makeHttpRequest(url_all_Violations, "GET", params);
				// Check your log cat for JSON reponse
				//Log.d("All Products: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray("avler");
					JSONObject c = products.getJSONObject(products.length()-1);
					avCount = c.getInt("ID") + 1;
					// adding HashList to ArrayList

				} else {
					backMenu();
					// do nothing
				}
			} catch (JSONException e) {
				backMenu();
			}

			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			new submitViolation().execute();
		}
	}

	class insertCategory extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				// getting JSON string from URL
				params.add(new BasicNameValuePair("object_id", Integer.toString(avCount)));
				String value = (String)avListHash.get(spinPos);
				params.add(new BasicNameValuePair("term_taxonomy_id", value));
				params.add(new BasicNameValuePair("term_order", "0"));
				JSONObject json = jsonParser.makeHttpRequest(url_Term, "POST", params);
				// Check your log cat for JSON reponse
				//Log.d("All Products: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					// adding HashList to ArrayList
					new insertPosition().execute();

				} else {
					// do nothing
					backMenu();
				}
			} catch (JSONException e) {
				backMenu();
			}

			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// updating UI from Background Thread
		}
	}

	class insertPosition extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				// getting JSON string from URL
				params.add(new BasicNameValuePair("post_id", Integer.toString(avCount)));
				params.add(new BasicNameValuePair("category", disabilityType.getItemAtPosition(spinPos).toString()));
				params.add(new BasicNameValuePair("lat", Double.toString(latitude)));
				params.add(new BasicNameValuePair("lng", Double.toString(longitude)));
				JSONObject json = jsonParser.makeHttpRequest(url_Pos, "POST", params);
				// Check your log cat for JSON reponse
				//Log.d("All Products: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);


				if (success == 1) {
					new insertMeta().execute("cp_sys_ad_duration","365");
					new insertMeta().execute("cp_sys_expire_date","11/11/2050 14:00:00");
					new insertMeta().execute("cp_country", convertAddName(cityName));
					new insertMeta().execute("cp_state", convertAddName(districtName));
					new insertMeta().execute("cp_city", convertAddName(streetName));
					new insertMeta().execute("cp_street", convertAddName(streetName));
					new insertMeta().execute("cp_zipcode", postCode);
					new insertMeta().execute("cp_sys_ad_duration", "500");
					new SubscribeViolation().execute();
				} else {
					// do nothing
					backMenu();
				}
			} catch (JSONException e) {
				backMenu();
			}

			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// updating UI from Background Thread
			
		}

		public String convertAddName (String s){

			s = s.toLowerCase();
			s = s.replace("�", "u");
			s = s.replace("�", "i");
			s = s.replace("�", "o");
			s = s.replace("�", "s");
			s = s.replace("�", "g");
			s = s.replace("�", "c");
			s = s.replace("�", "g");
			return s;
		}
	}

	class insertMeta extends AsyncTask<String, String, String> {


		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				// getting JSON string from URL
				params.add(new BasicNameValuePair("post_id", Integer.toString(avCount)));
				params.add(new BasicNameValuePair("meta_key", args[0]));
				params.add(new BasicNameValuePair("meta_value",args[1]));

				JSONObject json = jsonParser.makeHttpRequest(url_TermMeta, "POST", params);
				// Check your log cat for JSON reponse
				//Log.d("All Products: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					// adding HashList to ArrayList

				} else {
					// do nothing
					backMenu();
				}
			} catch (JSONException e) {
				backMenu();
			}

			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// updating UI from Background Thread
		}
	}
	
class SubscribeViolation extends AsyncTask<String, String, String> {

		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			
		}

		

		/**
		 * Creating subscribtion
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", idDB));
			params.add(new BasicNameValuePair("post_id", Integer.toString(avCount)));
			params.add(new BasicNameValuePair("username", usernameDB));
			params.add(new BasicNameValuePair("usermail", usermailDB));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_subscribeNewViolation,
					"GET", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
			
				} else {
					// failed to create product
					backMenu();
				}
			} catch (JSONException e) {
				backMenu();
				e.printStackTrace();
			}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
		}

		

	}

}



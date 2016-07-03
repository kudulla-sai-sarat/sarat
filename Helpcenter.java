package com.example.maps;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Helpcenter extends Activity implements OnClickListener {

	private int colorcode;
	private TextView tv_reportpettitle;
	private String title;
	private int titlecode;
	private ImageView iv_report_submit, iv_takepic;
	private EditText et_report_name, et_report_mobile, et_report_email,
			et_report_title, et_location, et_report_description,
			et_report_question;
	private CheckBox checkbox_do, checkbox_need;

	private String mCurrentPhotoPath;
	private Button btn_browse, bt_submit;
	private Uri fileUri;
	private Uri selectedImage;
	private Bitmap photo;
	private String picturePath;
	private ImageView setimage;

	private static final String LOG_TAG = "Google Places Autocomplete";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	int PLACE_PICKER_REQUEST = 1;
	int CAMERA_REQUEST = 2;
	private double latitude, longitude;
	private String address;
	private GPSTracker gpstracker;
	private static final int GPSLOCATION_REQUEST_CODE = 103;
	private String ba1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpcenter);
		Validations.ctx = Helpcenter.this;
		Validations.slide();
		System.out.println("Validations.statusFlowFlag: "
				+ Validations.statusFlowFlag);
		ImageView iv_menu2 = (ImageView) findViewById(R.id.iv_menu2);
		iv_menu2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Validations.statusFlowFlag = "dashboard_helpcenter";
				Intent person = new Intent(Helpcenter.this, personlist.class);
				person.putExtra("flow", "dashboard_helpcenter");
				startActivity(person);
			}
		});

		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// Build the alert dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Location Services Not Active");
			builder.setMessage("Please enable Location Services and GPS");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int i) {
							// Show location settings when the user acknowledges
							// the alert dialog
							startActivityForResult(new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS),
									GPSLOCATION_REQUEST_CODE);

						}
					});
			Dialog alertDialog = builder.create();
			alertDialog.setCancelable(false);
			alertDialog.show();
		} else {
			gpstracker = new GPSTracker(Helpcenter.this);
			latitude = gpstracker.getLatitude();
			longitude = gpstracker.getLongitude();
			address = gpstracker.ConvertPointAddress(gpstracker.getLatitude(),
					gpstracker.getLongitude());

			tv_reportpettitle = (TextView) findViewById(R.id.tv_reportpettitle);
			title = getIntent().getStringExtra("title");
			titlecode = getIntent().getIntExtra("titlecode", 0);
			tv_reportpettitle.setText(title);

			checkbox_do = (CheckBox) findViewById(R.id.checkbox_do);
			checkbox_need = (CheckBox) findViewById(R.id.checkbox_need);
			et_report_name = (EditText) findViewById(R.id.et_report_name);
			et_report_mobile = (EditText) findViewById(R.id.et_report_mobile);
			et_report_title = (EditText) findViewById(R.id.et_report_title);
			et_report_description = (EditText) findViewById(R.id.et_report_description);
			et_report_email = (EditText) findViewById(R.id.et_report_email);

			et_report_description.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub

					if (v.getId() == R.id.et_report_description) {
						v.getParent().requestDisallowInterceptTouchEvent(true);
						switch (event.getAction() & MotionEvent.ACTION_MASK) {
						case MotionEvent.ACTION_UP:
							v.getParent().requestDisallowInterceptTouchEvent(
									false);
							break;
						}
					}

					return false;
				}
			});
			checkbox_do
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {

								colorcode = 0;
								checkbox_do.setChecked(true);

								checkbox_need.setChecked(false);
							}

						}
					});
			checkbox_need
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								colorcode = 1;
								checkbox_need.setChecked(true);

								checkbox_do.setChecked(false);
							}

						}
					});

			et_report_question = (EditText) findViewById(R.id.et_report_question);
			et_report_question.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent in = new Intent(Helpcenter.this, Mapaddress.class);

					startActivityForResult(in, PLACE_PICKER_REQUEST);
				}
			});
			setimage = (ImageView) findViewById(R.id.setimage);
			bt_submit = (Button) findViewById(R.id.bt_submit);
			bt_submit.setOnClickListener(this);
			iv_takepic = (ImageView) findViewById(R.id.iv_takepic);
			iv_takepic.setOnClickListener(this);
		}

	}

	private void validate() {
		if (et_report_name.getText().toString().trim().equals("")) {
			Validations.MyAlertBox(Helpcenter.this, "Please Enter  Name");
			et_report_name.requestFocus();
		} else if (et_report_mobile.getText().toString().trim().equals("")) {
			Validations.MyAlertBox(Helpcenter.this,
					"Please Enter  Mobile Number");
			et_report_mobile.requestFocus();

		} else if (checkbox_do.isChecked() == false
				&& checkbox_need.isChecked() == false) {
			Validations.MyAlertBox(Helpcenter.this, "Please Select Do or Need");

		} else if (et_report_email.getText().toString().trim().equals("")) {
			Validations.MyAlertBox(Helpcenter.this, "Please Enter Your Email");
			et_report_email.requestFocus();
		} else if (et_report_title.getText().toString().trim().equals("")) {
			Validations.MyAlertBox(Helpcenter.this, "Please Enter Category");
			et_report_title.requestFocus();
		} else if (et_report_question.getText().toString().trim().equals("")) {
			Validations.MyAlertBox(Helpcenter.this,
					"Please Enter Your Location");
			et_report_question.requestFocus();

		} else if (et_report_description.getText().toString().trim().equals("")) {
			Validations.MyAlertBox(Helpcenter.this, "Please Enter Description");
			et_report_description.requestFocus();
		} else {
			System.out.println("mCurrentPhotoPath: " + mCurrentPhotoPath);
			if (mCurrentPhotoPath == null || mCurrentPhotoPath.equals("")) {
				// validations.MyAlertBox(AddCategory.this,
				// "Please Take Photo");
				new uploadToServer().execute();
			} else {
				upload();
			}
		}
	}

	private void upload() {
		// TODO Auto-generated method stub
		Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
		bm = Base64.resizeBitMapImage1(mCurrentPhotoPath, 250, 250);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
		byte[] ba = bao.toByteArray();
		ba1 = Base64.encodeBytes(ba);

		// Upload image to server
		new uploadToServer().execute();
	}

	private void captureImage() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				ex.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, CAMERA_REQUEST);
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {
				setPic();
				/*
				 * System.out.println("data"+data);
				 * 
				 * selectedImage = data.getData(); photo = (Bitmap)
				 * data.getExtras().get("data");
				 * 
				 * // Cursor to get image uri to display
				 * 
				 * String[] filePathColumn = { MediaStore.Images.Media.DATA };
				 * System.out.println("selectedImage:::::::" + selectedImage);
				 * Cursor cursor = getContentResolver().query(selectedImage,
				 * filePathColumn, null, null, null); cursor.moveToFirst();
				 * 
				 * int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				 * picturePath = cursor.getString(columnIndex); cursor.close();
				 * 
				 * Bitmap photo = (Bitmap) data.getExtras().get("data");
				 * 
				 * setimage.setImageBitmap(photo);
				 */
			}
		} else if (requestCode == PLACE_PICKER_REQUEST) {
			System.out.println("in place request" + resultCode);
			if (resultCode == 1) {
				Validations.ctx = Helpcenter.this;
				Validations.slide();
				latitude = data.getExtras().getDouble("searchLat");
				longitude = data.getExtras().getDouble("searchLong");
				address = data.getExtras().getString("searchaddressmsg");

				System.out.println("latitude: " + latitude);
				System.out.println("longitude: " + longitude);
				System.out.println("address: " + address);
				addressdetails();

			}
		}
	}

	private void addressdetails() {
		// TODO Auto-generated method stub
		if (address.contains(",")) {
			System.out.println("address.length(): " + address.length());
			for (int i = 0; i < address.split(",").length; i++) {
				if (!address.split(",")[i].equals("")) {
					/*
					 * int k = address.split(",").length -
					 * (address.split(",").length - i);
					 */
					if (address.split(",").length == 4) {
						et_report_question.setText(address.split(",")[1]);
						// et_city.setText(address.split(",")[1]);
						// et_state.setText(address.split(",")[2]);
						// et_country.setText(address.split(",")[3]);
					} else if (address.split(",").length == 5) {
						et_report_question.setText(address.split(",")[0] + ","
								+ address.split(",")[1]);
						// et_city.setText(address.split(",")[2]);
						// et_state.setText(address.split(",")[3]);
						// et_country.setText(address.split(",")[4]);
					}
				}
			}
		}

	}

	// }

	private void LoadPlacePicker(int PlacePickerRequest) {
		try {

			PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
			Intent intent = intentBuilder.build(Helpcenter.this);
			// Start the Intent by requesting a result, identified by a request
			// code.
			startActivityForResult(intent, PlacePickerRequest);

		} catch (GooglePlayServicesRepairableException e) {
			GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(),
					Helpcenter.this, 0);
		} catch (GooglePlayServicesNotAvailableException e) {
			Toast.makeText(Helpcenter.this,
					"Google Play Services is not available.", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void clickpic() {
		// Check Camera
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// Open default camera
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

			// start the image capture Intent
			startActivityForResult(intent, CAMERA_REQUEST);

		} else {
			Toast.makeText(getApplication(), "Camera not supported",
					Toast.LENGTH_LONG).show();
		}
	}

	public class uploadToServer extends AsyncTask<Void, Void, JSONObject> {

		private ProgressDialog pd;
		private JSONObject jObject;

		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(Helpcenter.this);
			pd.setMessage("Loading..");
			pd.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			if (checkbox_do.isChecked() == true) {
				nameValuePairs.add(new BasicNameValuePair("request",
						"PostToHelpCenter"));
			} else if (checkbox_need.isChecked() == true) {
				nameValuePairs.add(new BasicNameValuePair("request",
						"FindOnHelpCenter"));
			}

			nameValuePairs.add(new BasicNameValuePair("name", et_report_name
					.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("phone", et_report_mobile
					.getText().toString().trim()));

			nameValuePairs.add(new BasicNameValuePair("title", et_report_title
					.getText().toString().trim()));

			nameValuePairs.add(new BasicNameValuePair("description",
					et_report_description.getText().toString().trim()));

			nameValuePairs.add(new BasicNameValuePair("landmark",
					et_report_question.getText().toString().trim()));

			nameValuePairs.add(new BasicNameValuePair("email", et_report_email
					.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("photo", ba1));
			nameValuePairs
					.add(new BasicNameValuePair("latitude", "" + latitude));
			nameValuePairs.add(new BasicNameValuePair("longitude", ""
					+ longitude));

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Validations.url);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				String st = EntityUtils.toString(response.getEntity());
				jObject = new JSONObject(st);
				Log.v("log_tag", "In the try Loop" + st);

			} catch (Exception e) {
				Log.v("log_tag", "Error in http connection " + e.toString());
			}
			return jObject;

		}

		protected void onPostExecute(JSONObject json) {

			// pd.hide();
			pd.dismiss();
			try {
				System.out.println("json in myprofile: " + json);
				if (json != null) {
					int status = json.getInt("status");
					String msg = json.getString("result");
					// Getting JSON Array
					if (status == 1) {
						Validations.MyAlertBoxIntent(Helpcenter.this, msg,
								Dashboard_Helpcenter.class);

					} else {
						Validations.MyAlertBox(Helpcenter.this, msg);
					}
				} else {
					Toast.makeText(Helpcenter.this, "Check Data is Null",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	private void setPic() {
		// Get the dimensions of the View
		int targetW = setimage.getWidth();
		int targetH = setimage.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		setimage.setImageBitmap(bitmap);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		Log.e("Getpath", "Cool" + mCurrentPhotoPath);
		return image;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == bt_submit) {
			validate();
		} else if (v == iv_takepic) {
			captureImage();
		}

	}

	public void onItemClick(AdapterView adapterView, View view, int position,
			long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public static ArrayList autocomplete(String input) {
		ArrayList resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + Validations.placesapikey);
			sb.append("&components=country:gr");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			System.out.println("sb: " + sb.toString());
			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString(
						"description"));
				System.out
						.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements
			Filterable {
		private ArrayList resultList;

		public GooglePlacesAutocompleteAdapter(Context context,
				int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return (String) resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		View v = getCurrentFocus();

		if (v != null
				&& (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE)
				&& v instanceof EditText
				&& !v.getClass().getName().startsWith("android.webkit.")) {
			int scrcoords[] = new int[2];
			v.getLocationOnScreen(scrcoords);
			float x = ev.getRawX() + v.getLeft() - scrcoords[0];
			float y = ev.getRawY() + v.getTop() - scrcoords[1];

			if (x < v.getLeft() || x > v.getRight() || y < v.getTop()
					|| y > v.getBottom())
				Validations.hideKeyboard(this);
		}
		return super.dispatchTouchEvent(ev);
	}
}

package com.throbit.throbitapp;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ArrayList<Story> listData;
	public ListItemAdapter itemAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listData = new ArrayList<Story>();
		
		new RssFeedHandler().execute("http://feeds.wired.com/wired/index");
		ListView listview = (ListView) this.findViewById(R.id.postListView);
		itemAdapter = new ListItemAdapter(this, R.layout.listitem, listData);
		Log.d("dg", Integer.toString(listData.size()));
		listview.setAdapter(itemAdapter);
		
		listview.setOnClickListener(new OnClickListener()
		
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void setNewFeed(String url){
		for(int i = 0; i < listData.size(); i++){
			listData.remove(i);
		}
		new RssFeedHandler().execute(url);
	}
	
	public enum RSSXMLTag {
		TITLE,DATE,LINK,CONTENT,GUID,IGNORETAG;
	}
	
	private class RssFeedHandler extends AsyncTask<String, Void, ArrayList<Story>>{
		private RSSXMLTag currTag;
		
		
		@Override
		protected ArrayList<Story> doInBackground(String... params) {
			String urlStr = params[0];
			InputStream is = null;
			ArrayList<Story> storyList = new ArrayList<Story>();
			try{
				URL url = new URL(urlStr);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(100000);
				conn.setConnectTimeout(10000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				int response = conn.getResponseCode();
				Log.d("debug", "The response is: " + response);
				is = conn.getInputStream();
				
				XmlPullParserFactory fact = XmlPullParserFactory.newInstance();
				fact.setNamespaceAware(true);
				XmlPullParser xpp = fact.newPullParser();
				xpp.setInput(is, null);
				
				int eventType = xpp.getEventType();
				Story sData = null;
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, DD MMM yyyy HH:mm:ss");
				
				while(eventType != XmlPullParser.END_DOCUMENT){
					if(eventType == XmlPullParser.START_DOCUMENT){
						
					} else if(eventType == XmlPullParser.START_TAG){
						if(xpp.getName().equals("item")){
							sData = new Story();
							currTag = RSSXMLTag.IGNORETAG;
						} else if(xpp.getName().equals("title")){
							currTag = RSSXMLTag.TITLE;
						} else if(xpp.getName().equals("link")){
							currTag = RSSXMLTag.LINK;
						} else if(xpp.getName().equals("pubDate")){
							currTag = RSSXMLTag.DATE;
						}
					} else if(eventType == XmlPullParser.END_TAG){
						if(xpp.getName().equals("item")){
							Date publDate = dateFormat.parse(sData.storyDate);
							sData.storyDate = dateFormat.format(publDate);
							storyList.add(sData);
						} else {
							currTag = RSSXMLTag.IGNORETAG;
						}
					} else if(eventType == XmlPullParser.TEXT){
						String content = xpp.getText();
						content = content.trim();
						Log.d("debug", content);
						if(sData != null){
							switch (currTag){
							case TITLE:
								if(content.length() != 0){
									if(sData.storyTitle != null){
										sData.storyTitle += content;
									} else {
										sData.storyTitle = content;
									}
								}
								break;
							case LINK:
								if(content.length() != 0){
									if(sData.storyURL != null){
										sData.storyURL += content;
									} else {
										sData.storyURL = content;
									}
								}
								break;
							case DATE:
								if(content.length() != 0){
									if(sData.storyDate != null){
										sData.storyDate += content;
									} else {
										sData.storyDate = content;
									}
								}
								break;
							default:
								break;
							}
						}
					}
					
					eventType = xpp.next();
				}
				Log.d("tst", String.valueOf(storyList.size()));
			} catch(Exception e){
				e.printStackTrace();
			}
			return storyList;
		}
	
		protected void onPostExecute(ArrayList<Story> result){
			for(int i = 0; i < result.size(); i++){
				listData.add(result.get(i));
			}
			itemAdapter.notifyDataSetChanged();
		}
		
	}

}

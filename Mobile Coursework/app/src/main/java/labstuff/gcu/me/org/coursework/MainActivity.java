package labstuff.gcu.me.org.coursework;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button button1;
    private Button button2;
    private String currentLink = "";
    ListView lvRss;
    ListView lvRss2;
    ArrayList<String> titles;
    ArrayList<String> links;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvRss = (ListView) findViewById(R.id.lvRss);
        lvRss2 = (ListView) findViewById(R.id.lvRss2);
        button1=(Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvRss.setVisibility(View.VISIBLE);
                button1.setVisibility(View.VISIBLE);
            }

        });
        button2=(Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLink = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
                new ProcessInBackground().execute("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx");
                lvRss2.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);

            }

        });
        titles = new ArrayList<String>();
        links = new ArrayList<String>();

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);


            }
        });

        new ProcessInBackground().execute("https://trafficscotland.org/rss/feeds/currentincidents.aspx");
    }


    public InputStream getInputStream(URL url)
    {
        try
        {

            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<String, Void, Exception>
    {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Busy loading rss feed...please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(String... params) {

            try
            {
                URL url = new URL(params [0]);


                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);


                XmlPullParser xpp = factory.newPullParser();


                xpp.setInput(getInputStream(url), "UTF_8");


                boolean insideItem = false;


                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {

                    if (eventType == XmlPullParser.START_TAG)
                    {

                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                        }

                        else if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            if (insideItem)
                            {

                                titles.add(xpp.nextText());
                            }
                        }

                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if (insideItem)
                            {

                                links.add(xpp.nextText());
                            }
                        }
                    }

                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }


            }
            catch (MalformedURLException e)
            {
                exception = e;
            }
            catch (XmlPullParserException e)
            {
                exception = e;
            }
            catch (IOException e)
            {
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);

            lvRss.setAdapter(adapter);

                                                                                                          // Jack Robertson S1430612
            progressDialog.dismiss();
        }
    }
}
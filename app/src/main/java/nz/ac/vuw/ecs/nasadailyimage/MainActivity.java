package nz.ac.vuw.ecs.nasadailyimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * <h1>Nasa Daily Image Main Activity</h1>
 * The program implements an application that
 * simply acquires the latest daily image from NASA RSS and displays the image
 * with its relative information (e.g, title, description and date) on the screen.
 * <p/>
 * <b>Note:</b>
 * 1. The program currently doesn't run correctly due to some codes are missing, please complete the
 *    program in the lab session.
 * 2. All the missing parts are marked by the label "##Missing##", please search in the entire
 *    project by using the keyword to assure completeness.
 * 3. Please demo your work to your lab tutor by running the application successfully.
 *
 * @author Aaron Chen
 * @version 1.0
 * @since 2015-08-31
 */
public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
    private Image image = null;
    private Bitmap bitmapImage;

    private Button button;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.emailShare);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setType("application/image");
                intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, image.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, image.getDescription() + "\n" + image.getUrl());
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(image.getUrl()));

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        new MainTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to reset the display on screen after
     * retrieving the image from RSS.
     *
     * @param
     * @return Nothing.
     */

    public void resetDisplay() {

        //##Missing##
        //Update the text content of the TextView widget "imageTitle
        TextView title = (TextView) findViewById(R.id.imageTitle);
        title.setText(image.getTitle());


        //##Missing##
        //Update the text content of the TextView widget "imageDate"
        TextView date = (TextView) findViewById(R.id.imageDate);
        date.setText(image.getDate());

        //##Missing##
        //Update the text content of the TextView widget "imageDescription"
        TextView desc = (TextView) findViewById(R.id.imageDescription);
        desc.setText(image.getDescription());

        //##Missing##
        //Here we missed a WebView widget.
        //Update the content of the WebView widget "imageView"
        //Please create a WebView widget on the main layout (i.e., activity_main.xml) to
        //connect with the following commented codes.

        /*
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmapImage);
        */

        WebView imageView = (WebView) findViewById(R.id.webView);

        //Resize the content of the WebView widget
        imageView.setInitialScale(1);
        imageView.getSettings().setJavaScriptEnabled(true);
        imageView.getSettings().setLoadWithOverviewMode(true);
        imageView.getSettings().setUseWideViewPort(true);
        imageView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        imageView.setScrollbarFadingEnabled(false);

        //Display the image by its url
        imageView.loadUrl(image.getUrl());


    }

    /**
     * This inner class inherits from AsyncTask which performs background
     * operations and publish results on the UI thread.
     */
    public class MainTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //##Missing##
            //Invoke the function to retrieve the image from NASA RSS feed.

            processFeed();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            //##Missing##
            //Invoke the function to reset display after the latest daily image obtained.
            resetDisplay();
        }

        /**
         * This method is used to retrieve the latest daily image from NASA RSS feed.
         * @param
         * @return Nothing.
         */
        public void processFeed() {
            try {
                SAXParserFactory saxParserFactory =
                        SAXParserFactory.newInstance();
                SAXParser parser = saxParserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                IotdHandler iotdHandler = new IotdHandler();
                reader.setContentHandler(iotdHandler);

                InputStream inputStream = new URL(URL).openStream();
                reader.parse(new InputSource(inputStream));

                image = iotdHandler.getImage();

                //bitmapImage = decodeFile(image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Bitmap decodeFile(Image image){
            try {
                //Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new URL(image.getUrl()).openStream());

                //The new size we want to scale to
                final int REQUIRED_SIZE=70;

                //Find the correct scale value. It should be the power of 2.
                int scale=1;
                while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                    scale*=2;

                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize=scale;
                return BitmapFactory.decodeStream(new URL(image.getUrl()).openStream(), null, o2);

            } catch (FileNotFoundException e) {} catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

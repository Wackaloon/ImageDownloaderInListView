package com.example.wackaloon.myapplication;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.Transport;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ImageListAdapter imageAdapter;
    private List<ImageItem> imageList;
    private SwipeRefreshLayout swipeLayout;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageList = new ArrayList<>();
        // связь с адаптером
        populateListView(imageList);
        //Поток для подгрузки изображений
        new DownloadFilesTask().execute(imageList);

        // делаем обновление красивым
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.theme_accent_2,
                R.color.theme_accent_1,
                R.color.theme_primary);
        swipeLayout.setEnabled(false);

        // для корректной работы swipeRefresh, чтобы обновлялось только когда виден самый верх
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeLayout.setEnabled(firstVisibleItem == 0 ? true : false);
            }
        });



    }

    private void populateListView(List<ImageItem> imageList) {
        ListView list = (ListView)findViewById(R.id.listView);
        imageAdapter = new ImageListAdapter(MainActivity.this, imageList);
        list.setAdapter(imageAdapter);
    }

    @Override
    public void onRefresh() {
        // начать подгружать новый список изображений и отобразить прогресс
        swipeLayout.setRefreshing(true);
        new DownloadFilesTask().execute(imageList);
        // ждать 3 секунды и спрятать прогресс
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 3000);
    }


    private class DownloadFilesTask extends AsyncTask< List<ImageItem>, Void, List<ImageItem> > {

        @Override
        // для обновления отображения после добавления элементов
        protected void onPostExecute(List<ImageItem> itemList) {
            super.onPostExecute(itemList);

            for (int i = 0; i < itemList.size(); ++i){
                imageList.add(itemList.get(i));
            }

            imageAdapter.notifyDataSetChanged();
        }

        @Override
        // подгрузка списка изображений из поисковика
        protected List<ImageItem> doInBackground(List<ImageItem>... imageList) {
            String apiKey = "e7cd50a038e91315443981956be6d5d4";
            String secret = "3f1e386eab6185a1";
            List<ImageItem> images = imageList[0];
            Transport transportObject = null;

            try {
                transportObject = new REST();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Flickr flickrObject = new Flickr(apiKey, secret, transportObject);

            PhotosInterface photoInterface = flickrObject.getPhotosInterface();
            SearchParameters parametersOfSearch = new SearchParameters();
            PhotoList photos = new PhotoList();
            String[] tags = {"cats"};

            parametersOfSearch.setTags(tags);

            try {
                photos = photoInterface.search(parametersOfSearch, 20, images.size()/20 + 1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            }

            List<ImageItem> itemList = new ArrayList<>();

            for (int i = 0; i < photos.size(); ++i) {
                ImageItem item = new ImageItem();
                item.setImgUrl((photos.get(i).getSmallSquareUrl()));
                item.setImgText(photos.get(i).getTitle());
                itemList.add(item);
            }

            return itemList;
        }
    }
}

package org.upv.audiolibros.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.upv.audiolibros.AudioBooks;
import org.upv.audiolibros.R;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.view.BookListActivity;

public class BooksPlayerAppWidgetConfigureActivity extends AppCompatActivity {
    private static final String TAG = "ConfigureWidget";
    private SharedPreferences appPreferences;
    private BooksDatabase database;
    private RecyclerView.Adapter adapter;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            final Context context = BooksPlayerAppWidgetConfigureActivity.this;

            Book item = (Book) view.getTag();
            Log.d(TAG, "Selected: "+ item.getTitle());

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("WIDGET_SELECTOR", MODE_PRIVATE);
            preferences.edit().putString(mAppWidgetId +"", item.getId()).commit();

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BooksPlayerAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public BooksPlayerAppWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setContentView(R.layout.books_player_app_widget_configure);

        database = ((AudioBooks) getApplication()).getDatabase();
        appPreferences =  ((AudioBooks) getApplication()).getAppPreferences();

        setupToolbar();

        View recyclerView = findViewById(R.id.book_list);
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupToolbar() {
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Select Book");
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AudioBooks app = (AudioBooks) getApplication();
        adapter = new BooksConfigureRecyclerViewAdapter(app.getImageLoader(), database, mOnClickListener);

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager;
        manager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(manager);
    }
}


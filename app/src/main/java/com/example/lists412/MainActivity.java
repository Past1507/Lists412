package com.example.lists412;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView list;
    public static final String KEY_TEXT = "text";
    public static final String KEY_COUNT = "count";
    private String[] array_from = {KEY_TEXT, KEY_COUNT};
    private int[] array_to = {R.id.textList, R.id.countList};
    private SwipeRefreshLayout swipe_refresh;
    public static final String APP_PREFERENCES = "listText";
    private List<Map<String, String>> list_content;
    private SharedPreferences mSettings;
    private SimpleAdapter newAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.list);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        initPreference();
        initList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                list_content.remove(i);
                newAdapter.notifyDataSetChanged();
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                newAdapter.notifyDataSetChanged();
                swipe_refresh.setRefreshing(false);
            }
        });
    }

    private void initList() {
        list_content = prepareContent();
        newAdapter = createAdapter(list_content);
        list.setAdapter(newAdapter);
    }

    private SimpleAdapter createAdapter(List<Map<String, String>> content) {
        return new SimpleAdapter(this, content, R.layout.list_item, array_from, array_to);
    }

    private void initPreference() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Map<String, ?> result = mSettings.getAll();
        if (result.size() == 0) {
            SharedPreferences.Editor editor = mSettings.edit();
            String[] result_text = getString(R.string.large_text).split("\n\n");
            for (int i = 0; i < result_text.length; i++) {
                String setting_name = "text_"+i;
                if (!mSettings.contains(setting_name)) {
                    editor.putString(setting_name, result_text[i]);
                    editor.apply();
                }
            }
            editor.commit();
        }
    }

    private List<Map<String, String>> prepareContent() {
        List<Map<String, String>> listObject = new ArrayList();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Map<String, ?> result = mSettings.getAll();
        for (int i = 0; i < result.size(); i++) {
            HashMap<String, String> resultItem = new HashMap();
            String setting_name = "text_"+i;
            String pref_value = (String) result.get(setting_name);
            int count = pref_value.length();
            resultItem.put(KEY_TEXT, pref_value);
            resultItem.put(KEY_COUNT, Integer.toString(count));
            listObject.add(resultItem);
        }
        return listObject;
    }
}
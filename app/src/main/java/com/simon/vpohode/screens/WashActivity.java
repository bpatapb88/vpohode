package com.simon.vpohode.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import com.simon.vpohode.CustomAdapter;
import com.simon.vpohode.ListViewItemCheckboxBaseAdapter;
import com.simon.vpohode.ListViewItemDTO;
import com.simon.vpohode.Managers.ListViewManager;
import com.simon.vpohode.R;
import com.simon.vpohode.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class WashActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private CustomAdapter itemAdapter;
    private ListView listViewWithCheckbox;
    private ArrayList<Integer> selectedId = new ArrayList<>();
    private Cursor cursor;
    private CardView washSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("theme", true)){
            getTheme().applyStyle(R.style.AppTheme,true);
        }else{
            getTheme().applyStyle(R.style.OverlayThemeRose,true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash);
        washSelected = findViewById(R.id.wash_selected);
        listViewWithCheckbox = findViewById(R.id.list);
        final List<ListViewItemDTO> initItemList = this.getInitViewItemDtoList();
        final ListViewItemCheckboxBaseAdapter listViewDataAdapter = new ListViewItemCheckboxBaseAdapter(getApplicationContext(), initItemList);
        listViewDataAdapter.notifyDataSetChanged();
        System.out.println("Count - "+listViewDataAdapter.getCount());
        listViewWithCheckbox.setAdapter(listViewDataAdapter);
        listViewWithCheckbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);
                ListViewItemDTO itemDto = (ListViewItemDTO)itemObject;
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.list_view_item_checkbox);
                if(itemDto.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                }else
                {
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }
            }
        });

        washSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = initItemList.size();
                for(int i=0;i<size;i++)
                {
                    ListViewItemDTO dto = initItemList.get(i);
                    dto.setChecked(true);
                }

                listViewDataAdapter.notifyDataSetChanged();
            }
        });

        ListViewManager.getListViewSize(listViewWithCheckbox);
        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    @Override
    public void onResume() {
        super.onResume();
        // open connection
        db = databaseHelper.getReadableDatabase();
        //get cursor from db
        // fill list depends of: is item top or not? 1=top 0=bottom

        /*itemAdapter = LayoutManager.configListOfItemsInWash(this,db);

        listViewWithCheckbox.setAdapter(itemAdapter);

        ListViewManager.getListViewSize(listViewWithCheckbox);*/
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
    }

    public void goHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private List<ListViewItemDTO> getInitViewItemDtoList()
    {
        String itemTextArr[] = {"Android", "iOS", "Java", "JavaScript", "JDBC", "JSP", "Linux", "Python", "Servlet", "Windows"};

        List<ListViewItemDTO> ret = new ArrayList<ListViewItemDTO>();

        int length = itemTextArr.length;

        for(int i=0;i<length;i++)
        {
            String itemText = itemTextArr[i];

            ListViewItemDTO dto = new ListViewItemDTO();
            dto.setChecked(false);
            dto.setItemText(itemText);

            ret.add(dto);
            System.out.println("itemText - " + itemText);
        }

        return ret;
    }
}

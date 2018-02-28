package com.example.tuan_dong.map.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuan_dong.map.Database.Address;
import com.example.tuan_dong.map.Database.DBHelper;
import com.example.tuan_dong.map.Database.PhoneNoAdapter;
import com.example.tuan_dong.map.Database.PhoneNoDB;
import com.example.tuan_dong.map.Database.PhoneNumber;
import com.example.tuan_dong.map.R;
import com.example.tuan_dong.map.Utils.MainMenuUtils;

import java.util.Arrays;

public class PhoneNumberActivity extends AppCompatActivity {
    private PhoneNoDB m_phoneNoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        m_phoneNoDB = new PhoneNoDB(getApplicationContext());
        if (m_phoneNoDB.getAllNumbersIntoList().size() == 0) {
            m_phoneNoDB.init();
        }
        this.loadListView();
    }

    private void loadListView() {
        PhoneNoAdapter adapter = new PhoneNoAdapter(getApplicationContext(), m_phoneNoDB.getAllNumbers(), true);
        ListView phoneList = (ListView) findViewById(R.id.lv_phone_list);
        phoneList.setAdapter(adapter);
        this.registerForContextMenu(phoneList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MainMenuUtils.declareMainMenu(this, id);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListView listView = (ListView) findViewById(R.id.lv_phone_list);
        Cursor cursor = (Cursor) listView.getItemAtPosition(info.position);
        final int id = cursor.getInt(0);
        try {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    m_phoneNoDB.delete(id);
                    return true;
                case R.id.action_edit:
                    final Dialog dialog = new Dialog(this);
                    dialog.setTitle("Cập Nhật Số Điện Thoại");
                    dialog.setContentView(R.layout.update_phone_no_dialog);
                    final EditText phoneName = (EditText) dialog.findViewById(R.id.et_phone_name);
                    final EditText phoneNo = (EditText) dialog.findViewById(R.id.et_phone_no);
                    Button btnCancel = (Button) dialog.findViewById(R.id.btn_phone_cancel);
                    Button btnUpdate = (Button) dialog.findViewById(R.id.btn_phone_update);

                    final PhoneNumber number = m_phoneNoDB.getPhoneNumber(id);
                    phoneName.setText(number == null ? "" : number.getName());
                    phoneNo.setText(number == null ? "" : number.getNumber());

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PhoneNumber temp = new PhoneNumber(id, phoneName.getText().toString().replace("-", ""), phoneNo.getText().toString().replace("-", ""));
                            m_phoneNoDB.update(temp);
                            dialog.dismiss();
                            PhoneNumberActivity.this.loadListView();
                        }
                    });

                    dialog.show();
                    return true;
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage() + "\n" + ex.getClass() + "\n" + id, Toast.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }
}

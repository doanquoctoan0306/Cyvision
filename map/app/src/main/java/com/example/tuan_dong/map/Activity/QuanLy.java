package com.example.tuan_dong.map.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;
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
import com.example.tuan_dong.map.Database.AddressAdapter;
import com.example.tuan_dong.map.Database.AddressModify;
import com.example.tuan_dong.map.R;
import com.example.tuan_dong.map.Utils.MainMenuUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class QuanLy extends AppCompatActivity {
    private static int ADDRESS_QUANTITY = 6;
    private AddressModify addressModify;
    private AddressAdapter addressAdapter;
    private ListView lvDS;
    Intent intent;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location m_current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly);
        lvDS = (ListView) findViewById(R.id.lvDS);
        addressModify = new AddressModify(QuanLy.this);
        if (addressModify.getPlaceArrayList().size() == 0) {
            for (int i = 0; i < ADDRESS_QUANTITY; i++) {
                addressModify.insert();
            }
        }
        // TODO get location in onCreate can make stored value not exactly, it depend on situation and specification
        getDeviceLocation();
        display();
        registerForContextMenu(lvDS);

    }

    public void display() {
        addressAdapter = new AddressAdapter(this, addressModify.getPlaceList(), true);
        lvDS.setAdapter(addressAdapter);
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

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(QuanLy.this);
        m_current = null;
        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        m_current = currentLocation;
                    } else {
                        Toast.makeText(QuanLy.this, "Khong xac dinh duoc", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(QuanLy.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Cursor cursor = (Cursor) lvDS.getItemAtPosition(info.position);
        final int id = cursor.getInt(0);

        switch (item.getItemId()) {
            case R.id.action_delete:
                addressModify.delete(id);
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                display();
                return true;
            case R.id.action_edit:
                final Dialog dialog = new Dialog(this);
                dialog.setTitle("Cập Nhật Địa Điểm");
                dialog.setContentView(R.layout.show_dialog);
                final EditText edtLatitude, edtLongitude, edtName;
                Button btnCancel, btnUpdate;
                ImageButton btnlocation;
                edtLatitude = (EditText) dialog.findViewById(R.id.edtLatitude);
                edtLongitude = (EditText) dialog.findViewById(R.id.edtLongitude);
                edtName = (EditText) dialog.findViewById(R.id.edtName);

                btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);
                btnlocation = (ImageButton) dialog.findViewById(R.id.btnlocation);

                final Address adress = addressModify.fetchPlaceByID(id);
                edtLatitude.setText(String.valueOf(adress.getLat()));
                edtLongitude.setText(String.valueOf(adress.getLng()));
                edtName.setText(adress.getName());

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnlocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        edtLatitude.setText(String.valueOf(m_current.getLatitude()));
                        edtLongitude.setText(String.valueOf(m_current.getLongitude()));
                    }
                });

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Address adress1 = new Address(id, Double.parseDouble(edtLatitude.getText().toString()), Double.parseDouble(edtLongitude.getText().toString()), edtName.getText().toString());
                        addressModify.update(adress1);
                        display();
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return true;
        }

        return super.onContextItemSelected(item);
    }
}

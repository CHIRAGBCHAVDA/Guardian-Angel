package com.example.guardianangel;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    Button btnAdd,btnDelete,btnView;
    EditText edtPhone;
    ListView list;
    SQLiteOpenHelper sl;
    SQLiteDatabase sqliteDB;
    DBHandler myDBHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnView = (Button) findViewById(R.id.btnView);
        list = (ListView) findViewById(R.id.list);

        myDBHandler = new DBHandler(this);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sr = edtPhone.getText().toString();
                addData(sr);
                Toast.makeText(Register.this,"DATA ADDED",Toast.LENGTH_SHORT).show();
                edtPhone.setText("");

            }

            private void addData(String newEntry) {
                boolean insertData = myDBHandler.addData(newEntry);

                if (insertData) Toast.makeText(Register.this, "Data Entered Successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(Register.this, "Error Occured while entering data", Toast.LENGTH_SHORT).show();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqliteDB = myDBHandler.getWritableDatabase();
                String x = edtPhone.getText().toString();
                deleteData(x);
                Toast.makeText(Register.this, x + "Deleted Data", Toast.LENGTH_SHORT).show();
            }

            private boolean deleteData(String x) {
                return sqliteDB.delete(DBHandler.TABLE_NAME,DBHandler.COL2 + "=?", new String []{x})>0;
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }

            private void loadData() {
                ArrayList<String> theList = new ArrayList<>();
                Cursor data = myDBHandler.getListContents();
                if(data.getCount()==0){
                    Toast.makeText(Register.this,"There is no Content",Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toast.makeText(Register.this, theList.toString(), Toast.LENGTH_SHORT).show();

                    while (data.moveToNext()){
                        theList.add(data.getString(1));
                        ListAdapter listAdapter = new ArrayAdapter<>(Register.this,android.R.layout.simple_list_item_1,theList);
                        list.setAdapter(listAdapter);
                    }
                    Toast.makeText(Register.this, theList.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
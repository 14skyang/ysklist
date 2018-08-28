package com.ysklist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.cn.contact.ChooseModel;
import com.cn.contact.ContactInfo;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 主Activity，用于接收选择的联系人
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<ContactInfo> mContactList;
    private ContactAdapter1 mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
                  //检查是否授权，高版本手机必须有这个再次检查授权，否则会闪退
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else {
            EventBus.getDefault().register(this);
            initView();
            initData();
            initClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void initData() {
        mContactList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mContactAdapter = new ContactAdapter1(this, mContactList, ChooseModel.MODEL_SINGLE);
        mRecyclerView.setAdapter(mContactAdapter);
    }

    private void initClick() {
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_SINGLE);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_MULTI);
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactEvent event) {
        ArrayList<ContactInfo> contactInfos = event.getContactInfos();
        Collections.sort(contactInfos, new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo o1, ContactInfo o2) {
                //升序排列
                if (o1.getLetter().equals("#") || o2.getLetter().equals("#")) {
                    return 1;
                }
                return o1.getLetter().compareTo(o2.getLetter());
            }
        });

        mContactAdapter.setContactList(contactInfos);
    }

}

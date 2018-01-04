package com.guoguang.testbluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private TextView receiveInfo;

    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread acceptThread;

    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String connectName="Bluetooth_Socket";

    private final String TAG="ServerSocket";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveInfo=(TextView)findViewById(R.id.receiveInfo);
        acceptThread=new AcceptThread();
        acceptThread.start();

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String data=(String)msg.obj;
            receiveInfo.setText(data);
            //Log.d(TAG,"data="+data);
            Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
        }
    };

    private class AcceptThread extends Thread{
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream is;
        private OutputStream os;

        public AcceptThread(){
            BluetoothServerSocket tmp=null;
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            try {
                tmp=mBluetoothAdapter.listenUsingRfcommWithServiceRecord(connectName,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket=tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket=null;
            while (true){
                try {
                    Log.d(TAG,"start");
                    socket=serverSocket.accept();
                    is=socket.getInputStream();
                    os=socket.getOutputStream();
                    while(true){
                        byte[] buffer=new byte[128];
                        int count=is.read(buffer);
                        if(count!=0){
                            Message msg=new Message();
                            msg.obj=new String(buffer,0,count,"utf-8");
                            handler.sendMessage(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

package com.example.tcpip;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private Handler handler = new Handler();
    private static int SERVERPORT = 0;
    private static String SERVER_IP = null;

    EditText etIP, etPort, etMessage;
    Button buConnect, buSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIP = (EditText) findViewById(R.id.etIP);
        etPort = (EditText) findViewById(R.id.etPort);
        etMessage = (EditText) findViewById(R.id.etMessage);
        buConnect = (Button) findViewById(R.id.buConnect);
        buSend = (Button) findViewById(R.id.buSend);
    }

    //Send Button Codes
    public void Send(View v){
        String command = etMessage.getText().toString() + "\r\n";
        BackgroundSend bgs = new BackgroundSend();
        bgs.execute(command);
    }

    //Connect Button Codes
    public void Connect(View v){
        if(!TextUtils.isEmpty(etIP.getText()) && !TextUtils.isEmpty(etPort.getText())) {
            SERVER_IP = etIP.getText().toString();
            SERVERPORT = Integer.parseInt(etPort.getText().toString());

            BackgroundConnect bgc = new BackgroundConnect();
            bgc.execute();
        }else {
            Toast.makeText(getApplicationContext(), "Please fill the inputs", Toast.LENGTH_SHORT).show();
        }
    }

    //This class used to connect to the server in background with AsyncTask
    class BackgroundConnect extends AsyncTask<String, Void, Void> {
        Handler h = new Handler();

        @Override
        protected Void doInBackground(String... strings) {
            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddress, SERVERPORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if(socket.isConnected()) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
                            etMessage.setEnabled(true);
                            buSend.setEnabled(true);
                        }
                    });

                    new Thread(Input).start();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }
    }

    //This is a thread for listening the answer of the server
    Runnable Input = new Runnable() {
        public void run() {
            try {
                //In this part we listen to answer of the server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    final String str = in.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //showing answer of the server
                            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    //This class used to send our message to server in background with AsyncTask
    class BackgroundSend extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {

            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.print(strings[0]);
                out.flush();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }
    }
}
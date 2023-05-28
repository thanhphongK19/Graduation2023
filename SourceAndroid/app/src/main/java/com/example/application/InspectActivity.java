package com.example.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.application.databinding.ActivityInspectBinding;

public class InspectActivity extends AppCompatActivity {
    ActivityInspectBinding binding;

    private HandlerThread stream_thread1,flash_thread1;
    private Handler stream_handler1,flash_handler1;

    private HandlerThread stream_thread2,flash_thread2;
    private Handler stream_handler2,flash_handler2;

    /* IP local of esp32 Cam */

    String ip_text2 = "192.168.137.235";

    private final int ID_CONNECT1 = 200;
    private final int ID_FLASH1 = 201;
    private final int ID_CONNECT2 = 200;
    private final int ID_FLASH2 = 201;
    private boolean flash_on_off1 = false;
    private boolean flash_on_off2 = false;


    /* IP local of esp32 Cam */
    String ip_text1 = "192.168.137.193";



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityInspectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        queryButton();

        stream_thread1 = new HandlerThread("http");
        stream_thread1.start();
        stream_handler1 = new HttpHandler1(stream_thread1.getLooper());

        flash_thread1 = new HandlerThread("http");
        flash_thread1.start();
        flash_handler1 = new HttpHandler1(flash_thread1.getLooper());


        stream_thread2 = new HandlerThread("http");
        stream_thread2.start();
        stream_handler2 = new HttpHandler2(stream_thread2.getLooper());

        flash_thread2 = new HandlerThread("http");
        flash_thread2.start();
        flash_handler2 = new HttpHandler2(flash_thread2.getLooper());

        ActionBar actionBar  = getSupportActionBar();
        actionBar.setTitle("Giám sát");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        queryButton();
    }


    void queryButton()
    {
        binding.switchConnect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stream_handler1.sendEmptyMessage(ID_CONNECT1);
                binding.statusCamera1.setText("Đã kết nối");
            }
        });
        binding.switchFlash1.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(binding.switchFlash1.isChecked()){
                            flash_on_off1 = true;
                            flash_handler1.sendEmptyMessage(ID_FLASH1);
                            binding.statusFlash1.setText("Bật");
                        }
                        else{
                            flash_on_off1 = false;
                            flash_handler1.sendEmptyMessage(ID_FLASH1);
                            binding.statusFlash1.setText("Tắt");
                        }
                    }
                }
        );


        binding.switchConnect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stream_handler2.sendEmptyMessage(ID_CONNECT2);
                binding.statusCamera2.setText("Đã kết nối");
            }
        });
        binding.switchFlash2.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(binding.switchFlash2.isChecked()){
                            flash_on_off2 = true;
                            flash_handler2.sendEmptyMessage(ID_FLASH2);
                            binding.statusFlash2.setText("Bật");
                        }
                        else{
                            flash_on_off2 = false;
                            flash_handler2.sendEmptyMessage(ID_FLASH2);
                            binding.statusFlash2.setText("Tắt");
                        }
                    }
                }
        );

    }

    private class HttpHandler1 extends Handler
    {
        public HttpHandler1(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ID_CONNECT1:
                    VideoStream1();
                    break;
                case ID_FLASH1:
                    SetFlash1();
                    break;
                default:
                    break;
            }
        }

    }



    private void VideoStream1()
    {
        String stream_url1 = "http://" + ip_text1 + ":81/stream";

        BufferedInputStream bis1 = null;
        FileOutputStream fos1 = null;
        try
        {

            URL url1 = new URL(stream_url1);

            try
            {
                HttpURLConnection huc1 = (HttpURLConnection) url1.openConnection();
                huc1.setRequestMethod("GET");
                huc1.setConnectTimeout(1000 * 5);
                huc1.setReadTimeout(1000 * 5);
                huc1.setDoInput(true);
                huc1.connect();

                if (huc1.getResponseCode() == 200)
                {
                    InputStream in1 = huc1.getInputStream();

                    InputStreamReader isr1 = new InputStreamReader(in1);
                    BufferedReader br1 = new BufferedReader(isr1);

                    String data1;

                    int len1;
                    byte[] buffer1;

                    while ((data1 = br1.readLine()) != null)
                    {
                        if (data1.contains("Content-Type:"))
                        {
                            data1 = br1.readLine();

                            len1 = Integer.parseInt(data1.split(":")[1].trim());

                            bis1 = new BufferedInputStream(in1);
                            buffer1 = new byte[len1];

                            int t = 0;
                            while (t < len1)
                            {
                                t += bis1.read(buffer1, t, len1 - t);
                            }

                            Bytes2ImageFile1(buffer1, getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");

                            final Bitmap bitmap1 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    binding.monitor1.setImageBitmap(bitmap1);
                                }
                            });

                        }


                    }
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (bis1 != null)
                {
                    bis1.close();
                }
                if (fos1 != null)
                {
                    fos1.close();
                }

                stream_handler1.sendEmptyMessageDelayed(ID_CONNECT1,3000);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void SetFlash1()
    {
        String flash_url;
        if(flash_on_off1){
            flash_url = "http://" + ip_text1 + ":80/led?var=flash&val=1";
        }
        else {
            flash_url = "http://" + ip_text1 + ":80/led?var=flash&val=0";
        }

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void Bytes2ImageFile1(byte[] bytes, String fileName)
    {
        try
        {
            File file1 = new File(fileName);
            FileOutputStream fos1 = new FileOutputStream(file1);
            fos1.write(bytes, 0, bytes.length);
            fos1.flush();
            fos1.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private class HttpHandler2 extends Handler
    {
        public HttpHandler2(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ID_CONNECT2:
                    VideoStream2();
                    break;
                case ID_FLASH2:
                    SetFlash2();
                    break;
                default:
                    break;
            }
        }

    }





    private void Bytes2ImageFile2(byte[] bytes, String fileName)
    {
        try
        {
            File file2 = new File(fileName);
            FileOutputStream fos2 = new FileOutputStream(file2);
            fos2.write(bytes, 0, bytes.length);
            fos2.flush();
            fos2.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void VideoStream2()
    {
        String stream_url2 = "http://" + ip_text2 + ":81/stream";

        BufferedInputStream bis2 = null;
        FileOutputStream fos2 = null;
        try
        {

            URL url2 = new URL(stream_url2);

            try
            {
                HttpURLConnection huc2 = (HttpURLConnection) url2.openConnection();
                huc2.setRequestMethod("GET");
                huc2.setConnectTimeout(1000 * 5);
                huc2.setReadTimeout(1000 * 5);
                huc2.setDoInput(true);
                huc2.connect();

                if (huc2.getResponseCode() == 200)
                {
                    InputStream in2 = huc2.getInputStream();

                    InputStreamReader isr2 = new InputStreamReader(in2);
                    BufferedReader br2 = new BufferedReader(isr2);

                    String data2;

                    int len2;
                    byte[] buffer2;

                    while ((data2 = br2.readLine()) != null)
                    {
                        if (data2.contains("Content-Type:"))
                        {
                            data2 = br2.readLine();

                            len2 = Integer.parseInt(data2.split(":")[1].trim());

                            bis2 = new BufferedInputStream(in2);
                            buffer2 = new byte[len2];

                            int t = 0;
                            while (t < len2)
                            {
                                t += bis2.read(buffer2, t, len2 - t);
                            }

                            Bytes2ImageFile2(buffer2, getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0B.jpg");

                            final Bitmap bitmap2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0B.jpg");

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    binding.monitor2.setImageBitmap(bitmap2);
                                }
                            });

                        }


                    }
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (bis2 != null)
                {
                    bis2.close();
                }
                if (fos2 != null)
                {
                    fos2.close();
                }

                stream_handler2.sendEmptyMessageDelayed(ID_CONNECT2,3000);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void SetFlash2()
    {
        String flash_url;
        if(flash_on_off2){
            flash_url = "http://" + ip_text2 + ":80/led?var=flash&val=1";
        }
        else {
            flash_url = "http://" + ip_text2 + ":80/led?var=flash&val=0";
        }

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}

package com.example.mal90.appqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private AsyncTask hebra;
    private boolean first = true;
    String key;
    Integer seconds;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("s");

    String currentTimeStamp = dateFormat.format(new Date());
    String currentTimeStamp2 = dateFormat2.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        key = (String)extras.get("key");



        hebra = new QrGenerator().execute();
    }

    public void ejecutar(){
        QrGenerator qrGenerator = new QrGenerator();
        qrGenerator.execute();
    }

    public class QrGenerator extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentTimeStamp2 = dateFormat2.format(new Date());
            seconds = 60 - parseInt(currentTimeStamp2);
            if(first) {
                try {
                    Thread.sleep(seconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                first = false;
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HmacSha256();
            return true;

        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //textView.setText(currentTimeStamp + " " + currentTimeStamp2+ " Seconds restantes: " + seconds);
            textView.setText("Segundos restantes: " + seconds);
            //Toast.makeText(MainActivity.this, "Cada 10 segundos", Toast.LENGTH_SHORT).show();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                //Se calcula la Hmac con la key y la hora actualizada por minuto
                //y se genera el codigo QR
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                String encode = new String(Hex.encodeHex(sha256_HMAC.doFinal(currentTimeStamp.getBytes("UTF-8"))));

                BitMatrix bitMatrix = multiFormatWriter.encode(encode, BarcodeFormat.QR_CODE,200,200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }
            ejecutar();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    //Se calcula la hora actual y duerme la hebra un minuto
    public String HmacSha256() {
        textView = (TextView) this.findViewById(R.id.textView);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    //Cuando se pulsa el botón atrás se detiene la hebra
    @Override
    public void onBackPressed()
    {
        hebra.cancel(true);
        //finish();
        onBackPressed();  // Invoca al método
    }
}

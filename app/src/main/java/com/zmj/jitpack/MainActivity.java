package com.zmj.jitpack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    /**
     * 扫描二维码
     *
     * @param view
     */
    public void scan(View view) {
        Intent it = new Intent(this, CaptureActivity.class);
        startActivityForResult(it, 100);//有返回值的跳转
    }

    /**
     * 创建一个没有logo的二维码
     *
     * @param view
     */
    public void create(View view) {
        String content = editText.getText().toString();
        //参数1.内容。参数2.宽高，参数3,图片
        Bitmap bitmap = EncodingUtils.createQRCode(content, 600, 600, null);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 创建一个有logo的二维码
     *
     * @param view
     */
    public void createLogo(View view) {
        String content = editText.getText().toString();
        //参数1.内容。参数2.宽高，参数3,图片
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Bitmap bitmap = EncodingUtils.createQRCode(content, 600, 600, logo);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 保存文件到sd卡
     *
     * @param bitmap
     * @throws IOException
     */
    private void savePath(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        //
        FileOutputStream outputStream = new FileOutputStream("文件路径");
        outputStream.write(bytes);
        outputStream.close();
        outputStream.flush();
    }

    /**
     * 解析二维码
     *
     * @param view
     * @throws FormatException
     * @throws ChecksumException
     * @throws NotFoundException
     */
    public void know(View view) throws FormatException, ChecksumException, NotFoundException {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        //准备好解析的图片
        QRCodeReader codeReader = new QRCodeReader();
        //宽和高,像素
        int[] pixs = new int[bitmap.getWidth() * bitmap.getHeight()];//像素
        bitmap.getPixels(pixs, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source =
                new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixs);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        Result result = codeReader.decode(binaryBitmap);
        editText.setText(result.toString());
    }

    /**
     * 扫描成功获取返回值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String str = bundle.get("result").toString();
            editText.setText(str);
        }
    }
}

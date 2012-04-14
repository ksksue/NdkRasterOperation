package jp.kobegtug.codesprint.ndkrasteroperation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class NdkRasterOperationActivity extends Activity {
    private static final String TAG = "NdkRasterOperationActivity";
    private static final int MEASURE_TIMES = 100;
    private TextView mResultTime;
    private ImageView mImageViewSRC;
    private ImageView mImageViewMASK;
    private ImageView mImageViewRESULT;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.chrome);
//                R.drawable.ic_launcher);
        long start,stop;
        
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int pixels[] = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        final Bitmap.Config c = Bitmap.Config.ARGB_8888;
        Bitmap mask = Bitmap.createBitmap(width, height, c);
        Bitmap result = Bitmap.createBitmap(width, height, c);

        Canvas canvas = new Canvas();
        canvas.setBitmap(mask);
        canvas.drawColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(width / 2, height / 2, width / 3, paint);

        int maskpixels[] = new int[width * height];
        mask.getPixels(maskpixels, 0, width, 0, 0, width, height);

// -------------------------------------------------------------------------------
// NDK Raster
// -------------------------------------------------------------------------------
        double totalTimeNDK = 0.0;
        for(int i=0; i<MEASURE_TIMES; ++i) {
        	bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        	start = System.nanoTime();
        	raster(pixels, maskpixels, width, height, 1); // NDK Raster
        	stop = System.nanoTime();
        	totalTimeNDK += (stop-start)/1000.0; // us
        }
        double aveTimeNDK = totalTimeNDK/MEASURE_TIMES;
// -------------------------------------------------------------------------------

        result.setPixels(pixels, 0, width, 0, 0, width, height);

//        mResultTime = (TextView) findViewById(R.id.tvResultTime);
//        mResultTime.setText("NDK Raster Time : " + (stop-start)/1000.0 + " us  ");
        mImageViewSRC = (ImageView) findViewById(R.id.imageviewSRC);
        mImageViewMASK = (ImageView) findViewById(R.id.imageviewMASK);
        mImageViewRESULT = (ImageView) findViewById(R.id.imageviewRESULT);
        mImageViewSRC.setImageBitmap(bmp);
        mImageViewMASK.setImageBitmap(mask);
        mImageViewRESULT.setImageBitmap(result);
                
// -------------------------------------------------------------------------------
// Java Raster
// -------------------------------------------------------------------------------
        double totalTimeJava = 0.0;
        for(int i=0; i<MEASURE_TIMES; ++i) {
        	bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        	start = System.nanoTime();
        	rasterJ(pixels, maskpixels, width, height, 1); // Java Raster
        	stop = System.nanoTime();
        	totalTimeJava += (stop-start)/1000.0; // us
        }
        double aveTimeJava = totalTimeJava/MEASURE_TIMES;
// -------------------------------------------------------------------------------
        
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        
        mResultTime = (TextView) findViewById(R.id.tvResultTime);
        mResultTime.setText("Total Times : " + MEASURE_TIMES + "\nNDK Raster Time : " + aveTimeNDK + " us\nJava Raster Time : " + aveTimeJava + " us");
        mImageViewSRC = (ImageView) findViewById(R.id.imageviewSRCJ);
        mImageViewMASK = (ImageView) findViewById(R.id.imageviewMASKJ);
        mImageViewRESULT = (ImageView) findViewById(R.id.imageviewRESULTJ);
        mImageViewSRC.setImageBitmap(bmp);
        mImageViewMASK.setImageBitmap(mask);
        mImageViewRESULT.setImageBitmap(result);

    }

    public void rasterJ(int[] src, int[] dst, int width, int height, int ope)
    {
    	for (int h=0; h<height;h++ ) {
    		int ofst = h * width;
    		for (int w=0;w<width;w++ ) {
    			switch (ope) {
    			case 0: // copy
    				src[ofst+w] = dst[ofst+w];
    				break;
    			case 1: // and
    				src[ofst+w] &= dst[ofst+w];
    				break;
    			case 2: // or
    				src[ofst+w] |= dst[ofst+w];
    				break;
    			case 3: // xor
    				src[ofst+w] ^= dst[ofst+w];
    				break;
    			}
    		}
    	}
    }
    
    public native int raster(int[] src, int[] dst, int width, int height,
            int ope);

    static {
        System.loadLibrary("raster");
    }
}

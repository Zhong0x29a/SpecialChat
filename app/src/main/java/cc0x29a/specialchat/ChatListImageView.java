package cc0x29a.specialchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ChatListImageView extends ImageView{
	
	private static int mRadius;
	public static int new_msg_num=0;
	
	public ChatListImageView(Context context) {
		super(context);
	}
	
	public ChatListImageView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ChatListImageView(Context context,@Nullable AttributeSet attrs,int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//由于是圆形，宽高应保持一致
		int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadius = size / 2;
		setMeasuredDimension(size, size);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		
		//画笔
		Paint mPaint=new Paint();
		
		Drawable drawable = getDrawable();
		
		if (null != drawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			
			//初始化BitmapShader，传入bitmap对象
			BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			//计算缩放比例
			//图片的宿放比例
			float mScale=(mRadius*2.0f)/Math.min(bitmap.getHeight(),bitmap.getWidth());
			
			Matrix matrix = new Matrix();
			matrix.setScale(mScale,mScale);
			bitmapShader.setLocalMatrix(matrix);
			mPaint.setShader(bitmapShader);
			//画圆形，指定好坐标，半径，画笔
			canvas.drawPaint(mPaint);
			
			if(new_msg_num==0){
				Paint paint=new Paint();
				paint.setColor(Color.rgb(244,88,88));
				int pos=2*mRadius-18;
				
				paint.setTextSize(23);
				paint.setUnderlineText(true);
				canvas.drawCircle(12,12,12,paint);
			}
			
		} else {
			super.onDraw(canvas);
		}
	}
	
}

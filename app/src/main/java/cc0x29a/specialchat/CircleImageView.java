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

/**
 * By URL:(https://blog.csdn.net/RockyHua/article/details/79416085)
 */

@SuppressLint("AppCompatCustomView")
public class CircleImageView extends ImageView {
	
	//圆形图片的半径
	private static int mRadius;
	
	private static Canvas canvas;
	
	public int draw=0;
//	private int saveDef;
	
	public CircleImageView(Context context) {
		super(context);
	}
	
	public CircleImageView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CircleImageView(Context context,@Nullable AttributeSet attrs,int defStyleAttr) {
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
			canvas.drawCircle(mRadius, mRadius, mRadius,mPaint);
			
			CircleImageView.canvas=canvas;
			
			if(draw==1){
				Paint paint=new Paint();
				paint.setColor(Color.rgb(244,244,244));
				int pos=2*mRadius-18;
				
				paint.setTextSize(23);
				paint.setUnderlineText(true);
				canvas.drawText("Edit",pos-32,pos,paint);
			}
			
		} else {
			super.onDraw(canvas);
		}
	}
	
}

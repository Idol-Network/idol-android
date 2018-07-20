package com.yiju.ldol.ui.view.likeanimation;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;

import com.yiju.idol.ui.view.ZanView;
import com.yiju.idol.utils.LogUtils;

import java.util.Random;

@SuppressLint("NewApi") @TargetApi(Build.VERSION_CODES.HONEYCOMB)

	  
	public class ZanBean {  
	  
	    /** 
	     * 心的当前坐标 
	     */  
	    public Point point;
	    /** 
	     * 移动动画 
	     */  
	    private ValueAnimator moveAnim;
	    /** 
	     * 放大动画 
	     */  
	    private ValueAnimator zoomAnim;
	    /** 
	     * 透明度 
	     */  
	    public int alpha = 255;//  
	    /** 
	     * 心图 
	     */  
	    private Bitmap bitmap;
	    /** 
	     * 绘制bitmap的矩阵  用来做缩放和移动的 
	     */  
	    private Matrix matrix = new Matrix();
	    /** 
	     * 缩放系数 
	     */  
	    private float sf = 0;

		private AnimEnd animEnd;
	    /** 
	     * 产生随机数 
	     */  
	    private Random random;
	    public boolean isEnd = false;//是否结束  
	  
	    public ZanBean(Context context, int resId, ZanView zanView, long time) {
	        random = new Random();
	        this.bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
			int width;
			if(null == zanView){
				width = 240;
			}else{
				width = zanView.getWidth();
			}
	        init(new Point(width / 2, zanView.getHeight()), new Point((random.nextInt(width)), 0),time);
	    }  
	  
	    public ZanBean(Bitmap bitmap, ZanView zanView, long time) {
	        random = new Random();
	        this.bitmap = bitmap;
			int width;
			if(null == zanView){
				width = 240;
			}else{
				width = zanView.getWidth();
			}
	        init(new Point((width / 2)+80, zanView.getHeight()), new Point((random.nextInt(width)), 0),time);
	    }

	public void setAnimEndListener(AnimEnd animEndListener){
		this.animEnd = animEndListener;
	}


//	  1000/5=200   i=0   0,200,400,600,800
	    private void init(final Point startPoint, Point endPoint, final long time) {
	        moveAnim = ValueAnimator.ofObject(new BezierEvaluator(new Point(random.nextInt(startPoint.x * 2), Math.abs(endPoint.y - startPoint.y) / 2)), startPoint, endPoint);
	        moveAnim.setStartDelay(time);
			moveAnim.setDuration(2500);
			moveAnim.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					LogUtils.i("anims","onAnimationStart");
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					LogUtils.i("anims","onAnimationEnd");
					animEnd.isAnimEnd(true);
//					animation.end();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					LogUtils.i("anims","onAnimationCancel");

				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					LogUtils.i("anims","onAnimationRepeat");
				}
			});
	        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	            @Override
	            public void onAnimationUpdate(ValueAnimator animation) {
	                point = (Point) animation.getAnimatedValue();
	                alpha = (int) ((float) point.y / (float) startPoint.y * 255);  
	            }  
	        });  
	        moveAnim.start();  
	        zoomAnim = ValueAnimator.ofFloat(0, 1.1f,1.0f).setDuration(700);
			zoomAnim.setStartDelay(time);
	        zoomAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	            @Override
	            public void onAnimationUpdate(ValueAnimator animation) {
	                Float f = (Float) animation.getAnimatedValue();
	                sf = f.floatValue();
	            }


	        });  
	        zoomAnim.start();  
	    }  
	  
	    public void pause(){  
	        if(moveAnim !=null&& moveAnim.isRunning()){  
	            moveAnim.pause();  
	        }  
	        if(zoomAnim !=null&& zoomAnim.isRunning()){  
	            zoomAnim.pause();  
	        }  
	    }  
	  
	    @SuppressLint("NewApi") public void resume(){
	        if(moveAnim !=null&& moveAnim.isPaused()){  
	            moveAnim.resume();  
	        }  
	        if(zoomAnim !=null&& zoomAnim.isPaused()){  
	            zoomAnim.resume();  
	        }  
	    }  
	  
	    public void stop() {  
	        if (moveAnim != null) {  
	            moveAnim.cancel();  
	            moveAnim = null;  
	        }  
	        if (zoomAnim != null) {  
	            zoomAnim.cancel();  
	            zoomAnim = null;  
	        }  
	    }  
	  
	    /**
	     * 主要绘制函数 
	     */  
	    public void draw(Canvas canvas, Paint p) {
	        if (bitmap != null && alpha > 0) {  
	            p.setAlpha(alpha);  
	            matrix.setScale(sf, sf, bitmap.getWidth() / 2, bitmap.getHeight() / 2);  
	            matrix.postTranslate(point.x - bitmap.getWidth() / 2, point.y - bitmap.getHeight() / 2);  
	            canvas.drawBitmap(bitmap, matrix, p);
//				bitmap.recycle();
//				canvas.restore();
	        } else {  
	            isEnd = true;  
	        }  
	    }  
	  
	    /** 
	     * 二次贝塞尔曲线 
	     */  
	    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	    private class BezierEvaluator implements TypeEvaluator<Point> {
	  
	        private Point centerPoint;
	  
	        public BezierEvaluator(Point centerPoint) {
	            this.centerPoint = centerPoint;  
	        }  
	  
	        @Override
	        public Point evaluate(float t, Point startValue, Point endValue) {
	            int x = (int) ((1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * centerPoint.x + t * t * endValue.x);  
	            int y = (int) ((1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * centerPoint.y + t * t * endValue.y);  
	            return new Point(x, y);
	        }  
	    }

	public interface AnimEnd{
		public void isAnimEnd(boolean isEnd);
	}
}

package it.moondroid.seekbarhint.library;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.SeekBar;

public class SeekBarHint extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_FADE_DURATION = 500;
    private int mFadeDuration;

    private View mHintView;
    private OnSeekBarChangeListener mInternalListener;
    private OnSeekBarChangeListener mExternalListener;

    private OnSeekBarHintProgressChangeListener mProgressChangeListener;

    public interface OnSeekBarHintProgressChangeListener {
        public void onProgressChanged(SeekBarHint seekBarHint, int progress);
    }

    public SeekBarHint (Context context) {
        super(context);
        init(context, null);
    }

    public SeekBarHint (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public SeekBarHint (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

        setOnSeekBarChangeListener(this);

        if (mHintView!=null){
            initHintView();
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarHint);
        mFadeDuration = a.getInt(R.styleable.SeekBarHint_fadeDuration, DEFAULT_FADE_DURATION);

    }



    private void initHintView(){
        if (mProgressChangeListener!=null){
            mProgressChangeListener.onProgressChanged(this, getProgress());
        }

        mHintView.setX(getXPosition(this));
        mHintView.setVisibility(View.GONE);

        if(mHintView.getBackground()==null){
//        float radius = 10.0f;
//        ShapeDrawable sd = new ShapeDrawable(new RoundRectShape(new float[] { radius, radius, radius, radius, radius, radius, radius, radius }, null, null));
//        sd.getPaint().setColor(Color.BLACK);
            mHintView.setBackgroundResource(R.drawable.hint_drawable);
        }

    }

    public void setHintView(View view){
        mHintView = view;
        initHintView();

    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        if (mInternalListener==null){
            mInternalListener = l;
            super.setOnSeekBarChangeListener(l);
        }else {
            mExternalListener = l;
        }
    }

    public void setOnProgressChangeListener(OnSeekBarHintProgressChangeListener l){
        mProgressChangeListener = l;
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        float thumb_x = ( (float)getProgress()/(float)getMax() ) * getWidth();
//
//        int middle = getHeight()/2;
//
//        int value = getProgress();
//        String valueString = value + "";
//
//        Paint paint = new Paint();
//        paint.setTextSize(16.0f);
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setColor(Color.BLACK);

        //Log.d("SeekBarHint", "thumb_x: "+thumb_x);
        //canvas.drawText(valueString, thumb_x, middle - 20.0f, paint);

        super.onDraw(canvas);
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (mProgressChangeListener!=null){
            mProgressChangeListener.onProgressChanged(this, getProgress());
        }

        if(mExternalListener !=null){
            mExternalListener.onProgressChanged(seekBar, progress, b);
        }

        mHintView.setX(getXPosition(seekBar));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mExternalListener !=null){
            mExternalListener.onStartTrackingTouch(seekBar);
        }
        mHintView.setX(getXPosition(seekBar));
        Animation fadeAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeAnimation.setFillBefore(true);
        fadeAnimation.setFillAfter(true);
        fadeAnimation.setDuration(mFadeDuration);
        mHintView.startAnimation(fadeAnimation);
        mHintView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mExternalListener !=null){
            mExternalListener.onStopTrackingTouch(seekBar);
        }
        mHintView.setX(getXPosition(seekBar));
        Animation fadeAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation.setFillBefore(true);
        fadeAnimation.setFillAfter(true);
        fadeAnimation.setDuration(mFadeDuration);
        mHintView.startAnimation(fadeAnimation);
        mHintView.setVisibility(View.GONE);
    }


    private float getXPosition(SeekBar seekBar){
        float val = (((float)seekBar.getProgress() * (float)(seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax());
        float offset = (seekBar.getX()+seekBar.getThumbOffset());

        float textCenter = (mHintView.getWidth()/2.0f);

        float newX = val+offset - textCenter;
        if (newX < seekBar.getX()){
            newX = seekBar.getX();
        }
        if (newX+ mHintView.getWidth() >  seekBar.getX()+seekBar.getWidth()){
            newX = seekBar.getX()+seekBar.getWidth()- mHintView.getWidth();
        }
        return newX;
    }
}

package com.example.crazysnake;  
  
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * CSDN博客：http://blog.csdn.net/dawanganban
 * @author 阳光小强
 */
public class MySnake extends View {  
    private Paint paint; 
    private Paint textPaint;
    private RectF rect;  
      
    private static int boxSize = 20;  
    
	private static int xMaxBoxCount;  //x轴方向最多的box数量
	private static int yMaxBoxCount;  //y轴方向最多的box数量
      
    private List<Box> boxs = new ArrayList<Box>(); 
    
    private Box appleBox;
    private Random random;
    
    private boolean isGroup = false;
      
    private static final int[] colors = {  
        Color.RED,  
        Color.BLUE,   
        Color.GRAY,  
        Color.YELLOW  
    };  
      
    private enum Derectory{  
        LEFT,  
        RIGHT,  
        TOP,  
        BOTTOM;  
    }  
    
    public enum State{
    	READY,    //就绪
    	PAUSE,    //暂停
    	RUNNING,  //运行
    	LOSE      //失败
    }
      
    private Derectory currentDerect = Derectory.LEFT;  
    private State currentState = State.READY;
    
    private RefreshHandler mRefreshHandler = new RefreshHandler();
    class RefreshHandler extends Handler{
    	@Override
    	public void handleMessage(Message msg) {
    		MySnake.this.update();
    		MySnake.this.invalidate();
    		
    	}
    	
    	public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
    }
    
    public MySnake(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        paint = new Paint(); 
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(60);
        rect = new RectF(); 
        random = new Random();  
    } 
    
    public void setCurrentState(State state){
    	currentState = state;
    }

    private void update(){
    	if(currentState == State.RUNNING){
			move();
			mRefreshHandler.sleep(150);
    	}
    }
      
    private void initData(){  
        Box box;  
        for(int i=xMaxBoxCount - 5; i<xMaxBoxCount; i++){  
            box = new Box(i, 3);  
            boxs.add(box);  
        }  
    }  
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	xMaxBoxCount = (int) Math.floor(w / boxSize);
		yMaxBoxCount = (int) Math.floor(h / boxSize);
    }
      
    private float mDownX;  
    private float mDownY;  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("onTouch");  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            mDownX = event.getX();  
            mDownY = event.getY();  
            break;  
        case MotionEvent.ACTION_UP:  
            float disX = event.getX() - mDownX;  
            float disY = event.getY() - mDownY;  
            System.out.println("disX = " + disX);  
            System.out.println("dixY = " + disY);  
            if(Math.abs(disX) > Math.abs(disY)){  
                if(disX > 0){  
                	if(currentDerect != Derectory.LEFT){
                		currentDerect = Derectory.RIGHT;  
                	}
                }else{ 
                	if(currentState == State.PAUSE){
                		currentState = State.RUNNING;
                		update();
                	}else if(currentState != State.RUNNING){
                		currentState = State.RUNNING;
                		currentDerect = Derectory.LEFT;
                		boxs.clear();
                		initData();
                		addAppleBox();
                		update();
                	}else if(currentDerect != Derectory.RIGHT){		
                		currentDerect = Derectory.LEFT;  
                	}
                }  
            }else{  
                if(disY > 0){  
                	if(currentDerect != Derectory.TOP){
                		currentDerect = Derectory.BOTTOM;  
                	}
                }else{ 
                	if(currentDerect != Derectory.BOTTOM){
                		currentDerect = Derectory.TOP;  
                	}
                }  
            }  
            break;  
        }  
        return true;  
    }  
    
    private void move(){  
        Box headBox = new Box(0, 0);  
        switch (currentDerect) {
        case LEFT:  
        	headBox = new Box(boxs.get(0).getX() - 1, boxs.get(0).getY());
            break;  
        case RIGHT:  
            headBox = new Box(boxs.get(0).getX() + 1, boxs.get(0).getY());  
            break;  
        case TOP:  
        	headBox = new Box(boxs.get(0).getX(), boxs.get(0).getY() - 1); 
            break;  
        case BOTTOM:  
        	headBox = new Box(boxs.get(0).getX(), boxs.get(0).getY() + 1);
            break;  
        } 
        
        //判断是否撞墙了
        if(headBox.getX() < 0 || headBox.getY() < 0 || 
        		headBox.getX() > xMaxBoxCount || headBox.getY() > yMaxBoxCount){
        	currentState = State.LOSE;
        }
        
        //判断是否装到自己身上了
        for(int i=0; i<boxs.size(); i++){
        	if(boxs.get(i).getX() == headBox.getX() && 
        			boxs.get(i).getY() == headBox.getY()){
        		currentState = State.LOSE;
        	}
        }
        
        isGroup = false;
        //判断是否吃到苹果了~~
        for(int i=0; i<boxs.size(); i++){
        	if(boxs.get(i).getX() == appleBox.getX() && 
        			boxs.get(i).getY() == appleBox.getY()){
        		isGroup = true;
        		addAppleBox();
        	}
        }
        
        boxs.add(0, headBox);
        if(!isGroup){
        	boxs.remove(boxs.size() - 1);
        }
    }  
    
    private void addAppleBox(){
    	int randomX = random.nextInt(xMaxBoxCount);
        int randomY = random.nextInt(yMaxBoxCount);
        for(int i=0; i<boxs.size(); i++){
        	if(boxs.get(i).getX() == randomX){
        		addAppleBox();
        		break;
        	}
        	if(boxs.get(i).getY() == randomY){
        		addAppleBox();
        		break;
        	}
        	appleBox = new Box(randomX, randomY);
        }	
    }
    
    public Bundle saveState(){
    	Bundle map = new Bundle();
    	map.putSerializable("apple", appleBox);
    	map.putIntArray("boxs", coordArrayListToArray(boxs));
    	map.putSerializable("direction", currentDerect);
    	return map;
    }
    
    public void restoreState(Bundle bundle){
    	setCurrentState(State.PAUSE);
    	appleBox = (Box) bundle.getSerializable("apple");
    	boxs = coordArrayToArrayList(bundle.getIntArray("boxs"));
    	currentDerect = (Derectory) bundle.getSerializable("direction");
    }
    
	private int[] coordArrayListToArray(List<Box> boxs) {
		int count = boxs.size();
		int[] rawArray = new int[count * 2];
		for (int index = 0; index < count; index++) {
			Box box = boxs.get(index);
			rawArray[2 * index] = box.getX();
			rawArray[2 * index + 1] = box.getY();
		}
		return rawArray;
	}
	
	private ArrayList<Box> coordArrayToArrayList(int[] rawArray) {
		ArrayList<Box> coordArrayList = new ArrayList<Box>();

		int coordCount = rawArray.length;
		for (int index = 0; index < coordCount; index += 2) {
			Box c = new Box(rawArray[index], rawArray[index + 1]);
			coordArrayList.add(c);
		}
		return coordArrayList;
	}
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        for(int i=0; i<boxs.size(); i++){  
            paint.setColor(colors[i % colors.length]);  
            rect.set(boxs.get(i).getX() * boxSize, boxs.get(i).getY() * boxSize, 
            		(boxs.get(i).getX() + 1) * boxSize, (boxs.get(i).getY() + 1) * boxSize);  
            canvas.drawRect(rect, paint);  
        } 
        
        if(appleBox != null){
	        paint.setColor(Color.RED);
	        rect.set(appleBox.getX() * boxSize, appleBox.getY() * boxSize, 
	        		(appleBox.getX() + 1) * boxSize, (appleBox.getY() + 1) * boxSize);
	        canvas.drawRect(rect, paint);
        }
        
        if(currentState == State.READY){
        	canvas.drawText("请向左滑动", (xMaxBoxCount * boxSize - textPaint.measureText("请向左滑动")) / 2,
        			xMaxBoxCount * boxSize / 2, textPaint);
        }
        
        if(currentState == State.LOSE){
        	canvas.drawText("失败！左滑继续", (xMaxBoxCount * boxSize - textPaint.measureText("失败！左滑继续")) / 2,
        			(float)xMaxBoxCount * boxSize / 2, textPaint);
        	canvas.drawText("长度:" + boxs.size() , (xMaxBoxCount * boxSize - textPaint.measureText("长度:" + boxs.size())) / 2,
        			(float)xMaxBoxCount * boxSize / 4 * 3, textPaint);
        }
        
        if(currentState == State.PAUSE){
        	canvas.drawText("已暂停，左滑继续", (xMaxBoxCount * boxSize - textPaint.measureText("已暂停，左滑继续")) / 2,
        			xMaxBoxCount * boxSize / 2, textPaint);
        }
    }  
}
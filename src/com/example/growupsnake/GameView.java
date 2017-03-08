package com.example.growupsnake;

import java.util.ArrayList;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	private SharedPreferences sp1,sp2,sp3;
    private long MoveDelay=600; //移动延时
    private long LastMove; //最后一次移动的时间，用于计算时间距。
	private GestureDetector mGD; 
	protected static int[] arr=new int[4];
    protected static boolean gameover=false,tap=false,stop=false; 
    protected static int Direction=2,direction=2,score=0; //方向变量及分数
	protected static int m,n,xOffset,yOffset,fx,fy,bx,by,sx,sy,dx,dy,count=0;    
	private boolean food=false,rotation=false,boom=false,shelf=false,drug=false,buff=false; 
	private Bitmap Food,Head,Body,Boom,Shelf,Drug;
    private Paint paint=new Paint();
    private DisplayMetrics dm= new DisplayMetrics();
    //蛇身链表及其他游戏道具链表
    private ArrayList<Coordinate> SnakeTrail = new ArrayList<Coordinate>();  
    private ArrayList<Coordinate> FoodList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> BoomList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> DrugList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> ShelfList = new ArrayList<Coordinate>();
    private static final Random RNG = new Random();  //随机函数，用于食物等的随机出现
	public GameView(Context context,AttributeSet attrs) {
		super(context,attrs);
		sp1=this.getContext().getSharedPreferences("ScoreData1", 0);
		sp2=this.getContext().getSharedPreferences("ScoreData2", 0);
		sp3=this.getContext().getSharedPreferences("ScoreData3", 0);
		//手势识别监听触屏事件
		 mGD = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {    
             public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {    
           	  float x = e2.getX() - e1.getX();  
	             float y = e2.getY() - e1.getY();  
	             /**
	              * 触屏滑动改变方向变量,改变方向。
	              * 根据滑动前的起始坐标与滑动后的结束坐标，算出坐标差，判断滑动的方向。
	              * 若分数不为零，即蛇身长度超过1时，不可逆向运动。
	              */
	             if (Math.abs(x)> Math.abs(y)) {
	         		 if(velocityX>0){
	         			 if(score>0&&Direction==2){
	         				 direction=2;
	         				 Direction=direction;
	         			 }else{
	         			     direction=3;
	         			     Direction=direction;
	         			 }
	         		 }else{
	         			 if(score>0&&Direction==3){
	         				 direction=3;
	         				 Direction=direction;
	         			 }else{
	         			     direction=2;
	         			     Direction=direction;
	         			 }
	         		 }
	             } else{     	
	            	 if(velocityY>0){
	            		 if(score>0&&Direction==0){
	         				 direction=0;
	         				 Direction=direction;
	         			 }else{
	         			     direction=1;
	         			     Direction=direction;
	         			 }
	             		 }else{
	             			 if(score>0&&Direction==1){
		         				 direction=1;
		         				 Direction=direction;
		         			 }else{
		         			     direction=0;
		         			     Direction=direction;
		         			 }
	             		 }
	             }  
	             return true;  
	         }
             @Override
            public void onLongPress(MotionEvent e) {
            	super.onLongPress(e);
            	initGame();
            }
             @Override
            public boolean onDoubleTap(MotionEvent e) {
            	 if(!tap){
            		 stop=true;
            		 tap=true;
            		 handler.removeMessages(0);
            	 }else{
            		 stop=false;
            		 tap=false;
            		 handler.sleep(MoveDelay);
            	 }
            	return super.onDoubleTap(e);
            }
         });    
		 //初始游戏对话框
		initDia();
	}
	 @Override   
	    public boolean onTouchEvent(MotionEvent event) {      
	        mGD.onTouchEvent(event);    
	        return true;    
	    }    
	    public boolean onInterceptTouchEvent(MotionEvent event) {    
	        return mGD.onTouchEvent(event);    
	    } 
    public void initDia(){
    	AlertDialog.Builder DStart=new AlertDialog.Builder(getContext());
		DStart.setTitle("游戏信息");
		DStart.setMessage("双击屏幕可暂停游戏或恢复游戏呦~~");
		DStart.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				 //初始化游戏
				initGame();
			}
		});
		DStart.create().show();
    }
	    /**
	     * 初始化游戏数据，初始化游戏
	     */
	public void initGame() {
		dm=getResources().getDisplayMetrics();
		m=dm.widthPixels*35/854;    //每一小格的宽
		n=dm.heightPixels*35/480;   //每一小格的高
		MoveDelay=600;
		score=0;
		Direction=2;
		direction=2;
		gameover=false;
		stop=false;
		tap=false;
		food=false;
		buff=false;
		shelf=false;
		drug=false;
		boom=false;
		xOffset=dm.widthPixels*60/854;   //游戏边界的起始x坐标
		yOffset=dm.heightPixels*65/480;  //游戏边界的起始y坐标
		FoodList.clear();
		BoomList.clear();
		DrugList.clear();
		ShelfList.clear();
		SnakeTrail.clear();
		SnakeTrail.add(new Coordinate(10, 5));
		addRandomFood();
		addRandomBoom();
		update();
	}
	/**
	 * 用Handler机制实现定时刷新
	 */
	 protected RefreshHandler handler = new RefreshHandler();  
	  
	    @SuppressLint("HandlerLeak")
		class RefreshHandler extends Handler {  
	        //获取消息并处理  
	        @Override  
	        public void handleMessage(Message msg) {  
	            GameView.this.update();  
	            GameView.this.invalidate(); //刷新view为基类的界面  
	        }  
	        //定时发送消息给UI线程，以此达到更新的效果。  
	        public void sleep(long delayMillis) {  
	            this.removeMessages(0); //清空消息队列，Handler进入对新消息的等待  
	            sendMessageDelayed(obtainMessage(0), delayMillis); //定时发送新消息,激活handler  
	        }  
	    }; 
	    
	protected void update() {
		long now = System.currentTimeMillis(); 
		if(!gameover){
			if(!stop){
		if (now - LastMove > MoveDelay) {
			updateFood();
			updateSnake();
			updateBoom();
			if(DrugList.size()!=0){
				updateDrug();
			}
			if(ShelfList.size()!=0){
				updateShelf();
			}
			LastMove=now;
		}
		handler.sleep(MoveDelay);
			}
		}
	}
	private void updateSnake() {
		boolean growSnake = false;   //蛇长长的标志
        Coordinate head = SnakeTrail.get(0);  //头部很重要，只有头部可能碰到食物。  
        Coordinate newHead = new Coordinate(1, 1);  
        switch (direction) {  
        case 3: {  
            newHead = new Coordinate(head.x + 1, head.y); 
            rotation=false;
            break;  
        }  
        case 2: {  
            newHead = new Coordinate(head.x - 1, head.y);
            rotation=false;
            break;  
        }  
        case 1: {  
            newHead = new Coordinate(head.x, head.y + 1);  
            rotation=true;
            break;  
        }  
        case 0: {  
            newHead = new Coordinate(head.x, head.y - 1);  
            rotation=true;
            break;  
        }  
        }  
        //撞墙检测  ,如果是在无敌状态则穿墙，否则游戏结束
        if ((newHead.x < 0) || (newHead.y < 0) || (newHead.x >20)  
                || (newHead.y > 9)) {  
        	if(buff){
        		switch(direction){
        		 case 3: {  
        			 newHead = new Coordinate(0, head.y); 
        	            break;  
        	        }  
        	        case 2: {  
        	          newHead = new Coordinate(20, head.y); 
        	            break;  
        	        }  
        	        case 1: {  
        	          newHead = new Coordinate(head.x, 0);  
        	            break;  
        	        }  
        	        case 0: {  
        	          newHead = new Coordinate(head.x, 9);  
        	            break;  
        	        }  
        		}
        	}else{
        	Gameover();
            return;
        	} 
        }   
        //撞自己检测  
        int snakelength = SnakeTrail.size();  
        for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {  
            Coordinate c = SnakeTrail.get(snakeindex);  
            if (c.equals(newHead)) {  
            	Gameover();
                return;  
            }  
        }  
        //检测是否与食物坐标重叠，重叠则加分，提升蛇移动的速度，添加新的食物，长长一截蛇身
        int foodcount = FoodList.size();  
        for (int foodindex = 0; foodindex < foodcount; foodindex++) {  
            Coordinate c = FoodList.get(foodindex);  
            if (c.equals(newHead)) {  
                FoodList.remove(c);  
                addRandomFood();
                score=score+100;  
                if(score%500==0&&!buff&&ShelfList.size()==0){
                	addRandomShelf();
                }
                if(score%800==0){
                	addRandomDrug();
                }
                MoveDelay *= 0.9;     //延时缩短为原来的90%
                growSnake = true;
                //如果在无敌状态吃到了三个食物，无敌状态消失
                if(buff){
                	count++;
                	if(count==3){
                		buff=false;
                		count=0;
                	}
                }
            }
        }  
      //检测是否与炸弹坐标重叠，重叠则游戏结束
        int boomcount = BoomList.size();  
        for (int boomindex = 0; boomindex < boomcount; boomindex++) {  
            Coordinate c = BoomList.get(boomindex); 
            if(growSnake){
            	BoomList.remove(c);
            	addRandomBoom();
            }
            if (c.equals(newHead)&&!buff) {  
            	Gameover();
            }
        }  
        //检测是否与药丸坐标重叠，重叠则蛇身减短三节
        int drugcount = DrugList.size();  
        for (int drugindex = 0; drugindex < drugcount; drugindex++) {  
            Coordinate c = DrugList.get(drugindex);  
            if (c.equals(newHead)) { 
            	DrugList.remove(c);
            	drug=false;
            	for(int i=1;i<4;i++){
            		SnakeTrail.remove(SnakeTrail.size() - i);
            	}
            }
        }  
        //检测是否与盾牌坐标重叠，重叠则蛇身在吃到三个食物内无敌
        int shelfcount = ShelfList.size();  
        for (int shelfindex = 0; shelfindex < shelfcount; shelfindex++) {  
            Coordinate c = ShelfList.get(shelfindex);  
            if (c.equals(newHead)) { 
            	ShelfList.remove(c);
            	shelf=false;
            	buff=true;
            }
        }  
        //前进  ，加一截新的作为头
        SnakeTrail.add(0, newHead); 
        //如果不需要长身体则移除蛇身的最后一截
        if (!growSnake) {  
            SnakeTrail.remove(SnakeTrail.size() - 1);  
        }  
	}
	
	/**
	 * 生成食物坐标
	 */
	private void addRandomFood() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false;  
            //防止与蛇身坐标重叠
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与炸弹坐标重叠
            int boomlength = BoomList.size();  
            for (int index = 0; index < boomlength; index++) {  
                if (BoomList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与药丸坐标重叠
            int druglength = DrugList.size();  
            for (int index = 0; index < druglength; index++) {  
                if (DrugList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与盾牌坐标重叠
            int shelflength = ShelfList.size();  
            for (int index = 0; index < shelflength; index++) {  
                if (ShelfList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            found = !collision;  
        }   
        FoodList.add(newCoord); 
	}
	/**
	 * 添加食物
	 */
	private void updateFood() {
		 for (Coordinate c : FoodList) {  
	            SetMap(0, c.x, c.y);  
	        }  	
	}
	/**
	 * 生成炸弹坐标
	 */
	private void addRandomBoom() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false;  
            //防止与蛇身坐标重叠
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与食物坐标重叠
            int foodlength = FoodList.size();  
            for (int index = 0; index < foodlength; index++) {  
                if (FoodList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与药丸坐标重叠
            int druglength = DrugList.size();  
            for (int index = 0; index < druglength; index++) {  
                if (DrugList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与盾牌坐标重叠
            int shelflength = ShelfList.size();  
            for (int index = 0; index < shelflength; index++) {  
                if (ShelfList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            found = !collision;  
        }   
        BoomList.add(newCoord); 
	}
	/**
	 * 添加炸弹
	 */
	private void updateBoom() {
		 for (Coordinate c : BoomList) {  
	            SetMap(1, c.x, c.y);  
	        }  	
	}
	/**
	 * 生成药丸坐标
	 */
	private void addRandomDrug() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false;  
            //防止与蛇身坐标重叠
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与食物坐标重叠
            int foodlength = FoodList.size();  
            for (int index = 0; index < foodlength; index++) {  
                if (FoodList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与炸弹坐标重叠
            int boomlength = BoomList.size();  
            for (int index = 0; index < boomlength; index++) {  
                if (BoomList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与盾牌坐标重叠
            int shelflength = ShelfList.size();  
            for (int index = 0; index < shelflength; index++) {  
                if (ShelfList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            found = !collision;  
        }   
        DrugList.add(newCoord); 
	}
	/**
	 * 添加药丸
	 */
	private void updateDrug() {
		 for (Coordinate c : DrugList) {  
	            SetMap(2, c.x, c.y);  
	        }  	
	}
	/**
	 * 生成盾牌坐标
	 */
	private void addRandomShelf() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false; 
            //防止与蛇身坐标重叠
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与食物坐标重叠
            int foodlength = FoodList.size();  
            for (int index = 0; index < foodlength; index++) {  
                if (FoodList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与药丸坐标重叠
            int druglength = DrugList.size();  
            for (int index = 0; index < druglength; index++) {  
                if (DrugList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //防止与炸弹坐标重叠
            int boomlength = BoomList.size();  
            for (int index = 0; index < boomlength; index++) {  
                if (BoomList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            found = !collision;  
        }   
        ShelfList.add(newCoord); 
	}
	/**
	 * 添加盾牌
	 */
	private void updateShelf() {
		 for (Coordinate c : ShelfList) {  
	            SetMap(3, c.x, c.y);  
	        }  	
	}
	private void SetMap(int key, int xCount, int yCount) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
    	opts.inDensity = getResources().getDisplayMetrics().densityDpi;
    	opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
		switch(key){
		case 0:
			food=true;
			fx=xCount;
		    fy=yCount;
			Food=BitmapFactory.decodeResource(getResources(), R.drawable.food, opts);
		    break;
		case 1:
			boom=true;
			bx=xCount;
		    by=yCount;
			Boom=BitmapFactory.decodeResource(getResources(), R.drawable.boom2, opts);
		    break;
		case 2:
			drug=true;
			dx=xCount;
		    dy=yCount;
			Drug=BitmapFactory.decodeResource(getResources(), R.drawable.drug, opts);
		    break;
		case 3:
			shelf=true;
			sx=xCount;
			sy=yCount;
			Shelf=BitmapFactory.decodeResource(getResources(), R.drawable.shelf, opts);
		}
	}
	/**
	 * 
	 * 游戏结束，传递分数
	 */
	private void Gameover() {
		gameover=true;
		int s1=sp1.getInt("hscore1", 0);
		int s2=sp2.getInt("hscore2", 0);
		int s3=sp3.getInt("hscore3", 0);
		arr[0]=s1;
		arr[1]=s2;
		arr[2]=s3;
		arr[3]=score;
		for(int i=0;i<arr.length-1;i++){
			for(int j=i+1;j<arr.length;j++){
				if(arr[i]<arr[j]){
					int temp=arr[i];
					arr[i]=arr[j];
					arr[j]=temp;
				}
			}
		}
		sp1.edit().putInt("hscore1", arr[0]).commit();
		sp2.edit().putInt("hscore2", arr[1]).commit();
		sp3.edit().putInt("hscore3", arr[2]).commit();
		AlertDialog.Builder Dia=new AlertDialog.Builder(getContext());
		Dia.setTitle("游戏信息");
		Dia.setMessage("你输啦,是否重新开始游戏？");
		Dia.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				initGame();
			}
		});
		Dia.setNegativeButton("否", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {	
			}
		});
		Dia.create().show();
	}
	
	/**
	 * 
	 * 蛇与游戏道具的绘制
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		BitmapFactory.Options opts = new BitmapFactory.Options();
    	opts.inDensity = getResources().getDisplayMetrics().densityDpi;
    	opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
    	Head= BitmapFactory.decodeResource(getResources(), R.drawable.head, opts);
    	Body=BitmapFactory.decodeResource(getResources(), R.drawable.body, opts);
    	paint.setAntiAlias(true);
    	paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
    	paint.setStrokeWidth(2);
    	paint.setTextSize(dm.widthPixels*30/854);
    	String str=String.valueOf(score);
    	canvas.drawText(str,dm.widthPixels*10/854,dm.heightPixels*50/480, paint);
    	if(buff){
    		String msg="盾牌菌 X1";
    		canvas.drawText(msg,dm.widthPixels*120/854,dm.heightPixels*50/480, paint);
    	}
    	if(food){
    		canvas.drawBitmap(Food,new Rect(0,0,35,35),new Rect(xOffset+fx*m,yOffset+fy*n, xOffset+fx*m+m,yOffset+fy*n+n), paint);
    	}
    	if(boom){
    		canvas.drawBitmap(Boom,new Rect(0,0,35,35), new Rect(xOffset+bx*m, yOffset+by*n,xOffset+bx*m+m, yOffset+by*n+n), paint);
    	}
    	if(drug){
    		canvas.drawBitmap(Drug,new Rect(0,0,35,35) ,new Rect(xOffset+dx*m, yOffset+dy*n,xOffset+dx*m+m, yOffset+dy*n+n), paint);
    	}
    	if(shelf){
    		canvas.drawBitmap(Shelf,new Rect(0,0,35,35), new Rect(xOffset+sx*m, yOffset+sy*n,xOffset+sx*m+m, yOffset+sy*n+n), paint);
    	}
    	//循环画蛇的身体，如果是链表的首个，画头的图片，不是则画身体的图片
    	 int index = 0;  
         for (Coordinate c : SnakeTrail) {  
              if(index==0){
          		if(rotation){
      			//图片旋转
      			Matrix matrix=new Matrix();
      			matrix.postRotate(90);
      			int width=Head.getWidth();
      			int height=Head.getHeight();
      			Bitmap newhead=Bitmap.createBitmap(Head,0,0,width,height,matrix,true);
      			canvas.drawBitmap(newhead,new Rect(0, 0, 35, 35),new Rect(xOffset+c.x*m,yOffset+c.y*n,xOffset+c.x*m+m,yOffset+c.y*n+n), paint);
          		}else{
          		canvas.drawBitmap(Head, new Rect(0, 0, 35, 35),new Rect(xOffset+c.x*m,yOffset+c.y*n,xOffset+c.x*m+m,yOffset+c.y*n+n), paint);
          		}
              }else{
            	  if(rotation){
            	  //图片旋转
            	  Matrix matrix=new Matrix();
            	  matrix.postRotate(90);
            	  int width=Body.getWidth();
            	  int height=Body.getHeight();
            	  Bitmap newBody=Bitmap.createBitmap(Body,0,0,width,height,matrix,true);
            	  canvas.drawBitmap(newBody,new Rect(0, 0, 35, 35),new Rect(xOffset+c.x*m,yOffset+c.y*n,xOffset+c.x*m+m,yOffset+c.y*n+n), paint);
            	  }else{
             	  canvas.drawBitmap(Body, new Rect(0, 0, 35, 35),new Rect(xOffset+c.x*m,yOffset+c.y*n,xOffset+c.x*m+m,yOffset+c.y*n+n), paint);
            	  }
              }
         	 index++;
         }
}
	/**
	 * 创建Coordinate类存储坐标
	 */
	private class Coordinate {  
        public int x;  
        public int y;  
        public Coordinate(int newX, int newY) {  
            x = newX;  
            y = newY;  
        }  
		public boolean equals(Coordinate other) {  
            if (x == other.x && y == other.y) {  
                return true;  
            }  
            return false;  
        }  
        @Override  
        public String toString() {  
            return "Coordinate: [" + x + "," + y + "]";  
        }  
    }  
}

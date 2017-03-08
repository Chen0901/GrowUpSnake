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
    private long MoveDelay=600; //�ƶ���ʱ
    private long LastMove; //���һ���ƶ���ʱ�䣬���ڼ���ʱ��ࡣ
	private GestureDetector mGD; 
	protected static int[] arr=new int[4];
    protected static boolean gameover=false,tap=false,stop=false; 
    protected static int Direction=2,direction=2,score=0; //�������������
	protected static int m,n,xOffset,yOffset,fx,fy,bx,by,sx,sy,dx,dy,count=0;    
	private boolean food=false,rotation=false,boom=false,shelf=false,drug=false,buff=false; 
	private Bitmap Food,Head,Body,Boom,Shelf,Drug;
    private Paint paint=new Paint();
    private DisplayMetrics dm= new DisplayMetrics();
    //��������������Ϸ��������
    private ArrayList<Coordinate> SnakeTrail = new ArrayList<Coordinate>();  
    private ArrayList<Coordinate> FoodList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> BoomList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> DrugList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> ShelfList = new ArrayList<Coordinate>();
    private static final Random RNG = new Random();  //�������������ʳ��ȵ��������
	public GameView(Context context,AttributeSet attrs) {
		super(context,attrs);
		sp1=this.getContext().getSharedPreferences("ScoreData1", 0);
		sp2=this.getContext().getSharedPreferences("ScoreData2", 0);
		sp3=this.getContext().getSharedPreferences("ScoreData3", 0);
		//����ʶ����������¼�
		 mGD = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {    
             public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {    
           	  float x = e2.getX() - e1.getX();  
	             float y = e2.getY() - e1.getY();  
	             /**
	              * ���������ı䷽�����,�ı䷽��
	              * ���ݻ���ǰ����ʼ�����뻬����Ľ������꣬��������жϻ����ķ���
	              * ��������Ϊ�㣬�������ȳ���1ʱ�����������˶���
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
		 //��ʼ��Ϸ�Ի���
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
		DStart.setTitle("��Ϸ��Ϣ");
		DStart.setMessage("˫����Ļ����ͣ��Ϸ��ָ���Ϸ��~~");
		DStart.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				 //��ʼ����Ϸ
				initGame();
			}
		});
		DStart.create().show();
    }
	    /**
	     * ��ʼ����Ϸ���ݣ���ʼ����Ϸ
	     */
	public void initGame() {
		dm=getResources().getDisplayMetrics();
		m=dm.widthPixels*35/854;    //ÿһС��Ŀ�
		n=dm.heightPixels*35/480;   //ÿһС��ĸ�
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
		xOffset=dm.widthPixels*60/854;   //��Ϸ�߽����ʼx����
		yOffset=dm.heightPixels*65/480;  //��Ϸ�߽����ʼy����
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
	 * ��Handler����ʵ�ֶ�ʱˢ��
	 */
	 protected RefreshHandler handler = new RefreshHandler();  
	  
	    @SuppressLint("HandlerLeak")
		class RefreshHandler extends Handler {  
	        //��ȡ��Ϣ������  
	        @Override  
	        public void handleMessage(Message msg) {  
	            GameView.this.update();  
	            GameView.this.invalidate(); //ˢ��viewΪ����Ľ���  
	        }  
	        //��ʱ������Ϣ��UI�̣߳��Դ˴ﵽ���µ�Ч����  
	        public void sleep(long delayMillis) {  
	            this.removeMessages(0); //�����Ϣ���У�Handler���������Ϣ�ĵȴ�  
	            sendMessageDelayed(obtainMessage(0), delayMillis); //��ʱ��������Ϣ,����handler  
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
		boolean growSnake = false;   //�߳����ı�־
        Coordinate head = SnakeTrail.get(0);  //ͷ������Ҫ��ֻ��ͷ����������ʳ�  
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
        //ײǽ���  ,��������޵�״̬��ǽ��������Ϸ����
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
        //ײ�Լ����  
        int snakelength = SnakeTrail.size();  
        for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {  
            Coordinate c = SnakeTrail.get(snakeindex);  
            if (c.equals(newHead)) {  
            	Gameover();
                return;  
            }  
        }  
        //����Ƿ���ʳ�������ص����ص���ӷ֣��������ƶ����ٶȣ�����µ�ʳ�����һ������
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
                MoveDelay *= 0.9;     //��ʱ����Ϊԭ����90%
                growSnake = true;
                //������޵�״̬�Ե�������ʳ��޵�״̬��ʧ
                if(buff){
                	count++;
                	if(count==3){
                		buff=false;
                		count=0;
                	}
                }
            }
        }  
      //����Ƿ���ը�������ص����ص�����Ϸ����
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
        //����Ƿ���ҩ�������ص����ص��������������
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
        //����Ƿ�����������ص����ص��������ڳԵ�����ʳ�����޵�
        int shelfcount = ShelfList.size();  
        for (int shelfindex = 0; shelfindex < shelfcount; shelfindex++) {  
            Coordinate c = ShelfList.get(shelfindex);  
            if (c.equals(newHead)) { 
            	ShelfList.remove(c);
            	shelf=false;
            	buff=true;
            }
        }  
        //ǰ��  ����һ���µ���Ϊͷ
        SnakeTrail.add(0, newHead); 
        //�������Ҫ���������Ƴ���������һ��
        if (!growSnake) {  
            SnakeTrail.remove(SnakeTrail.size() - 1);  
        }  
	}
	
	/**
	 * ����ʳ������
	 */
	private void addRandomFood() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false;  
            //��ֹ�����������ص�
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ը�������ص�
            int boomlength = BoomList.size();  
            for (int index = 0; index < boomlength; index++) {  
                if (BoomList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ҩ�������ص�
            int druglength = DrugList.size();  
            for (int index = 0; index < druglength; index++) {  
                if (DrugList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ����������ص�
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
	 * ���ʳ��
	 */
	private void updateFood() {
		 for (Coordinate c : FoodList) {  
	            SetMap(0, c.x, c.y);  
	        }  	
	}
	/**
	 * ����ը������
	 */
	private void addRandomBoom() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false;  
            //��ֹ�����������ص�
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ʳ�������ص�
            int foodlength = FoodList.size();  
            for (int index = 0; index < foodlength; index++) {  
                if (FoodList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ҩ�������ص�
            int druglength = DrugList.size();  
            for (int index = 0; index < druglength; index++) {  
                if (DrugList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ����������ص�
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
	 * ���ը��
	 */
	private void updateBoom() {
		 for (Coordinate c : BoomList) {  
	            SetMap(1, c.x, c.y);  
	        }  	
	}
	/**
	 * ����ҩ������
	 */
	private void addRandomDrug() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false;  
            //��ֹ�����������ص�
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ʳ�������ص�
            int foodlength = FoodList.size();  
            for (int index = 0; index < foodlength; index++) {  
                if (FoodList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ը�������ص�
            int boomlength = BoomList.size();  
            for (int index = 0; index < boomlength; index++) {  
                if (BoomList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ����������ص�
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
	 * ���ҩ��
	 */
	private void updateDrug() {
		 for (Coordinate c : DrugList) {  
	            SetMap(2, c.x, c.y);  
	        }  	
	}
	/**
	 * ���ɶ�������
	 */
	private void addRandomShelf() {
		Coordinate newCoord = null;  
        boolean found = false;  
        while (!found) {  
            int newX = RNG.nextInt(21);  
            int newY = RNG.nextInt(10);  
            newCoord = new Coordinate(newX, newY);   
            boolean collision = false; 
            //��ֹ�����������ص�
            int snakelength = SnakeTrail.size();  
            for (int index = 0; index < snakelength; index++) {  
                if (SnakeTrail.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ʳ�������ص�
            int foodlength = FoodList.size();  
            for (int index = 0; index < foodlength; index++) {  
                if (FoodList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ҩ�������ص�
            int druglength = DrugList.size();  
            for (int index = 0; index < druglength; index++) {  
                if (DrugList.get(index).equals(newCoord)) {  
                    collision = true;  
                }  
            } 
            //��ֹ��ը�������ص�
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
	 * ��Ӷ���
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
	 * ��Ϸ���������ݷ���
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
		Dia.setTitle("��Ϸ��Ϣ");
		Dia.setMessage("������,�Ƿ����¿�ʼ��Ϸ��");
		Dia.setPositiveButton("��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				initGame();
			}
		});
		Dia.setNegativeButton("��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {	
			}
		});
		Dia.create().show();
	}
	
	/**
	 * 
	 * ������Ϸ���ߵĻ���
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
    		String msg="���ƾ� X1";
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
    	//ѭ�����ߵ����壬�����������׸�����ͷ��ͼƬ�������������ͼƬ
    	 int index = 0;  
         for (Coordinate c : SnakeTrail) {  
              if(index==0){
          		if(rotation){
      			//ͼƬ��ת
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
            	  //ͼƬ��ת
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
	 * ����Coordinate��洢����
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

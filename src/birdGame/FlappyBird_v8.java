package birdGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 使用面向对象的方式进行设计的版本
 * @author Administrator
 *
 *该类是用来运行程序，创建窗体的
 *
 *该版本实现了控制功能
 */
public class FlappyBird_v8 {

	public static void main(String[] args) throws InterruptedException, IOException {
		JFrame frame = new JFrame();
		//面板
		BirdPanel_v8 panel = new BirdPanel_v8();
		frame.add(panel);
		frame.setSize(432, 644+20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口结束程序
		frame.setResizable(false);//设置不可更改屏幕大小
		frame.setLocationRelativeTo(null);//居中
		frame.setVisible(true);
		//添加鼠标监听器，通过鼠标控制游戏 注意要放在step()方法上面
		frame.addMouseListener(panel);
		panel.step();//开始游戏移动效果
	}

}


class BirdPanel_v8 extends JPanel implements MouseListener{
	Ground ground;//地板对象
	Bird bird;//小鸟对象
	Column column1;//水管对象
	Column column2;
	Image background;//背景图片
	//判断游戏是否结束  true表示结束   false表示没有
	boolean gameOver;
	Image gameOverImage;//游戏结束的图片
	int score;//计算得分
	Image gameStartedImage;//游戏开始前图片
	boolean started;//游戏是否开始
	int hightScore;
	
	public BirdPanel_v8() throws IOException{//面板的无参构造方法，用来初始化成员变量(属性)
		ground = new Ground();
		bird = new Bird();
		column1 = new Column(1);
		column2 = new Column(2);
		background = new ImageIcon("img/bg.png").getImage();//加载图片到计算机内存
		gameOver = false;//默认游戏没有结束
		gameOverImage = new ImageIcon("img/gameover.png").getImage();
		score = 0;
		gameStartedImage = new ImageIcon("img/start.png").getImage();
		started = false;
		hightScore = readScore();
	}
	//绘图功能  只负责画出图片
	@Override
	public void paint(Graphics g) {
		super.paint(g);//先执行父类的画图方法
		g.drawImage(background, 0, 0, null);//画背景
		
		//画水管
		//x,y是图片左上角点坐标  需要跟据水管中心点计算得到
		g.drawImage(column1.image, column1.x-column1.width/2, 
				column1.y-column1.height/2, null);
		g.drawImage(column2.image, column2.x-column2.width/2, 
				column2.y-column2.height/2, null);
		g.drawImage(ground.image, ground.x, ground.y, null);//绘制地板
		//设置字体为加粗、大小为50像素
		Font font = new Font("Consolas",Font.BOLD,50);
		g.setFont(font);
		//在屏幕的50,50位置将分数画出来
		g.drawString(""+score, 150, 50);//score是int类型   ""+score可以拼接成字符串类型
		//设置白色字体
		g.setColor(Color.WHITE);
		g.drawString(""+score, 150-2, 50-2);
		//设置字体  画最高分数
		Font font1 = new Font("Consolas",Font.BOLD,30);
		g.setFont(font1);
		g.setColor(Color.black);
		//在屏幕的50,50位置将分数画出来
		g.drawString("NO.1: "+hightScore, 262,32);
		//设置白色字体
		g.setColor(Color.WHITE);
		g.drawString("NO.1: "+hightScore, 260,30);
		
		//画结束游戏画面
		if(gameOver){
			g.drawImage(gameOverImage, 0, 0, null);
		}
		//游戏还没有开始，显示start.png
		if(!started){
			g.drawImage(gameStartedImage, 0, 0, null);
		}
		
		/**
		 * Graphics2D是java提供的绘制2d特效的画笔工具类，
		 * 该工具类继承自Graphics类
		 * 大类型到小类型的赋值过程，需要强制类型转换
		 */
		Graphics2D graphic2D = (Graphics2D) g;
		/**
		 * 指定图片旋转的角度
		 * 第一个参数设置角度值，第二个和第三个参数设置图片的中心点
		 * 根据中心点旋转角度
		 */
		graphic2D.rotate(-bird.alpha, bird.x, bird.y);
		g.drawImage(bird.image, bird.x-bird.width/2, bird.y-bird.height/2, null);
	}
	//实现面板上的物体移动       游戏移动功能
	public void step() throws InterruptedException, IOException{
		while(true){
			//如果撞上了，则游戏结束
			if(bird.hit(ground)||bird.hit(column1)
					||bird.hit(column2)){
				gameOver = true;
				//最高分刷新判断逻辑
				if(score>hightScore){
					//写入文件中
					write(score+"");
				}
			}
			
			//判断是否能够计分，实现过了水管计分的处理
			//  ==用于比较两个表达式的值是否相等
			//  基本类型的两个表达式比较，就是比较值
			if(bird.x-bird.width/2==column1.x+column1.width/2
					||bird.x-bird.width/2==column2.x+column2.width/2){
				score++;
			}
			//System.out.println(score);//打印输出数据到控制台
			
			//如果游戏没有结束，则可以继续移动
			/**
			 * !gameOver   ！表示取反
			 */
			if(started){//游戏开始了后，可以进入下面的处理
				if(!gameOver){//游戏没有结束
					ground.step();//地板移动一步
					column1.step();//水管1移动一步
					column2.step();//水管2移动一步
					bird.step();//小鸟运动一步
					Thread.sleep(18);//休眠20毫秒 (线程相关)
				}
			}
			repaint();//尽快调用重画面板的方法
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
//		System.out.println(e.getX()+";"+e.getY());
		
		
		//重新开始游戏的处理功能 
		if(gameOver){
			score = 0;//分数置空
			ground = new Ground();
			bird = new Bird();
			column1 = new Column(1);
			column2 = new Column(2);
			gameOver = false;//游戏重新设置为未结束
			started = false;//重新开始的时候，小鸟等物体不能马上运行
			try {
				hightScore = readScore();
			} catch (IOException e1) {
				e1.printStackTrace();
			}//重新读取最高分
			repaint();
		}else{
			bird.flappy();
			started = true;//点击鼠标后，游戏即开始
			repaint();
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 读取一个文件中的分数数据
	 * 需要先在当前的项目的根目录下创建一个名叫score.txt的文件
	 * @return
	 * @throws IOException 
	 */
	public int readScore() throws IOException{
		FileInputStream fis = new FileInputStream("score.txt");
		//FileInputStream的作用是从一个文件中，按照字节为单位读取到文件中的内容
		//进入程序中
		byte[] bytes = new byte[1024];
		fis.read(bytes);//将数据读取放入字节数组中
		fis.close();//关闭流
		//trim()去除空格
		String str = new String(bytes).trim();//根据字节数组组成一个字符串
		int hightScore=0;
		//Integer.valueOf(str)将字符串值转成整数
		if(str.length()>0){
			hightScore = Integer.valueOf(str);
		}
		
		return hightScore;
	}
	
	/**
	 * 写出最高分到文件中
	 * @param score
	 * @throws IOException 
	 */
	public void write(String score) throws IOException{
		FileOutputStream fos = new FileOutputStream("score.txt");
		fos.write(score.getBytes());//将字符串转成字节数组，写出到文件中
		fos.flush();//刷新数据到硬盘
		fos.close();//关闭流
	}
	
	
}//自定义的游戏面板类结束

/**
 * 地板，用于对应屏幕上的地板
 * @author Administrator
 *
 */
class Ground{
	int x,y;//同时声明两个变量，中间用,隔开     地板图片的左上角在面板的x,y坐标位置
	int width;//宽
	int height;//高
	Image image;//地板图片对象
	/**
	 * 构造方法（构造器）  java类创建对象的时候会自动调用的一种方法
	 * 该方法是特殊方法   1.无返回值类型  2.方法名与类名一致
	 * 构造方法是用来初始化对象身上的属性值的
	 */
	public Ground(){
		width=800;
		height=146;
		x=0;
		y=498;//644-146
		image = new ImageIcon("img/ground.png").getImage();
	}
	//添加移动功能     该方法作用是描述地面如何移动
	public void step(){
		x--;//地板往左移
		if(x<= - (width-432)){//左移到边界值，复位
			x=0;
		}
	}	
}//地板类结束
/**
 * 水管，对应屏幕上的多个水管对象
 * 1.声明成员变量
 * 2.在构造方法中初始化成员变量
 * 3.通过一些普通自定义的方法，操作成员变量
 * @author Administrator
 *
 */
class Column{
	Image image;//水管图
	int x,y;//水管的缝隙中心点的坐标
	int width,height;//水管宽高
	int gap;//缝隙间隙
	int distance;//水管之间的距离
	/**
	 * 类的构造方法：在创建对象的时候会调用   无返回值类型、方法名与类名相同
	 * 水管对象两个，水管1和水管2       1表示水管1      2表示水管2
	 */
	public Column(int n){
		image = new ImageIcon("img/column.png").getImage();
		width = image.getWidth(null);//获取图片宽度
		height= image.getHeight(null);
		gap = 144;//缝隙值
		distance = 245;
		//   1:432+0*245    2:   432+1*245
		x = 432+width/2 + (n-1)*245;//第一个水管位于屏幕宽度外，第二个水管距离第一个水管245
		y = randomY();//随机生产y坐标位置
	}
	
	/**
	 * 水管运动功能(计算方法   算法) 
	 * 1.x的值递减
	 * 2.减少后判断x是否到达边界值，如果到达，则将x和y重新赋值
	 * 3.如果没有到达则不对x和y的值做处理，当前的运动一步功能结束   
	 */
	public void step(){
		x--;//x值减少1
		if(x<=-width/2){
			x = -width/2+distance*2;
			y = randomY();//随机生产y坐标
		}
	}
	/**
	 * 随机生产一个水管y坐标值
	 * [120,370)   
	 * @return
	 */
	public int randomY(){
		Random random = new Random();//随机数对象
		int y = random.nextInt(370-120)+120;//nextInt(bound):产生[0,bound)直接的整数
		return y;
	}
	
	
}//水管类结束
/**
 * 对应屏幕上的小鸟
 * @author Administrator
 *
 */
class Bird{
	Image image;//小鸟图案
	int x,y;//小鸟图片的中心点坐标
	int width;//宽
	int height;//高
	double g;//g是垂直运动的重力加速度
	double v0;//初始速度 初始速度越大，移动高度越高
	//运动一步经过的时间    根据该运动时间，进行画面的重画  该值越小，表示更快的重画内容
	//小鸟运动会更顺畅
	double t;
	double vt;//经过时间t以后的速度
	double s;//经过时间t后的移动距离  运动一步经过的距离
	//小鸟图片数组，是实现小鸟翅膀扇动的动画效果基础图片
	Image[] images;
	int index;//小鸟图片数组的下标值
	double alpha;//倾角值，用来表示小鸟运动的过程中的小鸟图片的角度
	
	public Bird(){
		image = new ImageIcon("img/0.png").getImage();
		x=100;
		y=200;
		width=image.getWidth(null);
		height=image.getHeight(null);
		g=9.8;// m/s
		v0=30;// 自定义   
		t=0.12;
		s=0;
		vt=0;
		//初始化数组对象  ，里面有8个图片对象组成 
		//[null,null,null,null,null,null,null,null]
		images = new Image[8];
		//初始化数组中的8个图片对象
		for(int i=0;i<images.length;i++){
			images[i] = new ImageIcon("img/"+i+".png").getImage();
		}
		index=0;//下标赋值
		alpha=0;
	}
	
	/**
	 * 小鸟移动一步
	 * 1.计算位移距离
	 * 2.得到位移后的小鸟的新的y值
	 * 3.判断小鸟的y值，达到边界值后，自动的上扬
	 */
	public void step(){
		s = v0*t-g*t*t/2;//计算位移
		y = y - (int)s;// (int)s将s从double类型转换成int类型
		vt = v0-g*t;//计算经过时间t以后的速度，是下一次计算的初始速度
//		System.out.println(y+",vt:"+vt);
//		if(y>=300){
//			v0=50;//改变下一次计算的初始速度为50
//		}else{
			v0 = vt; //改变下一次计算的初始速度
//		}
		fly();//移动完后飞一下
	}
	
	/**
	 * 小鸟向上飞扬的功能，通过设定初始上抛速度为正值可以实现向上飞扬
	 */
	public void flappy(){
		v0=30;
	}
	
	/**
	 * 每次移动一步，飞一下
	 * 通过取余运算，得到一个区间值[0~7)，该值是图片数组的下标
	 * 由于循环的时候过快的更换图片(每飞一下就更换图片)，会导致图片更换过频繁，画面不流畅
	 * 解决方案：
	 * 先将index除于一个固定整数，如3，可以实现飞三次后才更换一次图片
	 */
	public void fly(){
		image = images[index/3%8];
		//原版：index%8:0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7...
		//改进版：0 0 0 1 1 1 2 2 2 3 3 3 4 4 4...
		index++;
		/**
		 * 计算每飞一次的时候的小鸟图片的角度
		 * 角度值需要通过java的反正切函数来计算
		 * Math.atan(参数) 是java提供的计算该值的api 
		 */
		alpha = Math.atan(s/3);	
//		System.out.println(alpha);
	}
	
	/**
	 * 用于判断小鸟是否碰撞地面算法
	 * 如果返回true则表示碰撞了
	 * 返回false则表示没有碰上
	 * @return
	 */
	public boolean hit(Ground ground){
		/*
		 * 边界值：  小鸟的中心y值+小鸟的高的一半=地板的y
		 */
		boolean hit = y+height/2>=ground.y;
		if(hit){//撞上后，旋转图片    对小鸟的y值处理回到边界位置
//			y=ground.y-height/2;
			alpha = - 3.14;
		}
		return hit;
	}
	
	/**
	 * 判断小鸟是否碰撞水管算法
	 * true表示碰撞了
	 * false表示没有碰撞上
	 * @param column
	 * @return
	 */
	public boolean hit(Column c){
		boolean hit;
		//判断是否在水管范围
		if(x+width/2-3>c.x-c.width/2&&
				x-width/2<c.x+c.width/2){
			//满足条件表示在水管范围
			//检测是否在缝隙范围
			if(y-height/2+3>c.y-c.gap/2&&
					y+height/2<c.y+c.gap/2){
				hit = false;
			}else{
				hit = true;
			}
		}else{//不在水管范围不会撞
			hit = false;
		}
		return hit;
	}
}//小鸟类结束



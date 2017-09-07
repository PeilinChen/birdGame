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
 * ʹ���������ķ�ʽ������Ƶİ汾
 * @author Administrator
 *
 *�������������г��򣬴��������
 *
 *�ð汾ʵ���˿��ƹ���
 */
public class FlappyBird_v8 {

	public static void main(String[] args) throws InterruptedException, IOException {
		JFrame frame = new JFrame();
		//���
		BirdPanel_v8 panel = new BirdPanel_v8();
		frame.add(panel);
		frame.setSize(432, 644+20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�رմ��ڽ�������
		frame.setResizable(false);//���ò��ɸ�����Ļ��С
		frame.setLocationRelativeTo(null);//����
		frame.setVisible(true);
		//�������������ͨ����������Ϸ ע��Ҫ����step()��������
		frame.addMouseListener(panel);
		panel.step();//��ʼ��Ϸ�ƶ�Ч��
	}

}


class BirdPanel_v8 extends JPanel implements MouseListener{
	Ground ground;//�ذ����
	Bird bird;//С�����
	Column column1;//ˮ�ܶ���
	Column column2;
	Image background;//����ͼƬ
	//�ж���Ϸ�Ƿ����  true��ʾ����   false��ʾû��
	boolean gameOver;
	Image gameOverImage;//��Ϸ������ͼƬ
	int score;//����÷�
	Image gameStartedImage;//��Ϸ��ʼǰͼƬ
	boolean started;//��Ϸ�Ƿ�ʼ
	int hightScore;
	
	public BirdPanel_v8() throws IOException{//�����޲ι��췽����������ʼ����Ա����(����)
		ground = new Ground();
		bird = new Bird();
		column1 = new Column(1);
		column2 = new Column(2);
		background = new ImageIcon("img/bg.png").getImage();//����ͼƬ��������ڴ�
		gameOver = false;//Ĭ����Ϸû�н���
		gameOverImage = new ImageIcon("img/gameover.png").getImage();
		score = 0;
		gameStartedImage = new ImageIcon("img/start.png").getImage();
		started = false;
		hightScore = readScore();
	}
	//��ͼ����  ֻ���𻭳�ͼƬ
	@Override
	public void paint(Graphics g) {
		super.paint(g);//��ִ�и���Ļ�ͼ����
		g.drawImage(background, 0, 0, null);//������
		
		//��ˮ��
		//x,y��ͼƬ���Ͻǵ�����  ��Ҫ����ˮ�����ĵ����õ�
		g.drawImage(column1.image, column1.x-column1.width/2, 
				column1.y-column1.height/2, null);
		g.drawImage(column2.image, column2.x-column2.width/2, 
				column2.y-column2.height/2, null);
		g.drawImage(ground.image, ground.x, ground.y, null);//���Ƶذ�
		//��������Ϊ�Ӵ֡���СΪ50����
		Font font = new Font("Consolas",Font.BOLD,50);
		g.setFont(font);
		//����Ļ��50,50λ�ý�����������
		g.drawString(""+score, 150, 50);//score��int����   ""+score����ƴ�ӳ��ַ�������
		//���ð�ɫ����
		g.setColor(Color.WHITE);
		g.drawString(""+score, 150-2, 50-2);
		//��������  ����߷���
		Font font1 = new Font("Consolas",Font.BOLD,30);
		g.setFont(font1);
		g.setColor(Color.black);
		//����Ļ��50,50λ�ý�����������
		g.drawString("NO.1: "+hightScore, 262,32);
		//���ð�ɫ����
		g.setColor(Color.WHITE);
		g.drawString("NO.1: "+hightScore, 260,30);
		
		//��������Ϸ����
		if(gameOver){
			g.drawImage(gameOverImage, 0, 0, null);
		}
		//��Ϸ��û�п�ʼ����ʾstart.png
		if(!started){
			g.drawImage(gameStartedImage, 0, 0, null);
		}
		
		/**
		 * Graphics2D��java�ṩ�Ļ���2d��Ч�Ļ��ʹ����࣬
		 * �ù�����̳���Graphics��
		 * �����͵�С���͵ĸ�ֵ���̣���Ҫǿ������ת��
		 */
		Graphics2D graphic2D = (Graphics2D) g;
		/**
		 * ָ��ͼƬ��ת�ĽǶ�
		 * ��һ���������ýǶ�ֵ���ڶ����͵�������������ͼƬ�����ĵ�
		 * �������ĵ���ת�Ƕ�
		 */
		graphic2D.rotate(-bird.alpha, bird.x, bird.y);
		g.drawImage(bird.image, bird.x-bird.width/2, bird.y-bird.height/2, null);
	}
	//ʵ������ϵ������ƶ�       ��Ϸ�ƶ�����
	public void step() throws InterruptedException, IOException{
		while(true){
			//���ײ���ˣ�����Ϸ����
			if(bird.hit(ground)||bird.hit(column1)
					||bird.hit(column2)){
				gameOver = true;
				//��߷�ˢ���ж��߼�
				if(score>hightScore){
					//д���ļ���
					write(score+"");
				}
			}
			
			//�ж��Ƿ��ܹ��Ʒ֣�ʵ�ֹ���ˮ�ܼƷֵĴ���
			//  ==���ڱȽ��������ʽ��ֵ�Ƿ����
			//  �������͵��������ʽ�Ƚϣ����ǱȽ�ֵ
			if(bird.x-bird.width/2==column1.x+column1.width/2
					||bird.x-bird.width/2==column2.x+column2.width/2){
				score++;
			}
			//System.out.println(score);//��ӡ������ݵ�����̨
			
			//�����Ϸû�н���������Լ����ƶ�
			/**
			 * !gameOver   ����ʾȡ��
			 */
			if(started){//��Ϸ��ʼ�˺󣬿��Խ�������Ĵ���
				if(!gameOver){//��Ϸû�н���
					ground.step();//�ذ��ƶ�һ��
					column1.step();//ˮ��1�ƶ�һ��
					column2.step();//ˮ��2�ƶ�һ��
					bird.step();//С���˶�һ��
					Thread.sleep(18);//����20���� (�߳����)
				}
			}
			repaint();//��������ػ����ķ���
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
//		System.out.println(e.getX()+";"+e.getY());
		
		
		//���¿�ʼ��Ϸ�Ĵ����� 
		if(gameOver){
			score = 0;//�����ÿ�
			ground = new Ground();
			bird = new Bird();
			column1 = new Column(1);
			column2 = new Column(2);
			gameOver = false;//��Ϸ��������Ϊδ����
			started = false;//���¿�ʼ��ʱ��С������岻����������
			try {
				hightScore = readScore();
			} catch (IOException e1) {
				e1.printStackTrace();
			}//���¶�ȡ��߷�
			repaint();
		}else{
			bird.flappy();
			started = true;//���������Ϸ����ʼ
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
	 * ��ȡһ���ļ��еķ�������
	 * ��Ҫ���ڵ�ǰ����Ŀ�ĸ�Ŀ¼�´���һ������score.txt���ļ�
	 * @return
	 * @throws IOException 
	 */
	public int readScore() throws IOException{
		FileInputStream fis = new FileInputStream("score.txt");
		//FileInputStream�������Ǵ�һ���ļ��У������ֽ�Ϊ��λ��ȡ���ļ��е�����
		//���������
		byte[] bytes = new byte[1024];
		fis.read(bytes);//�����ݶ�ȡ�����ֽ�������
		fis.close();//�ر���
		//trim()ȥ���ո�
		String str = new String(bytes).trim();//�����ֽ��������һ���ַ���
		int hightScore=0;
		//Integer.valueOf(str)���ַ���ֵת������
		if(str.length()>0){
			hightScore = Integer.valueOf(str);
		}
		
		return hightScore;
	}
	
	/**
	 * д����߷ֵ��ļ���
	 * @param score
	 * @throws IOException 
	 */
	public void write(String score) throws IOException{
		FileOutputStream fos = new FileOutputStream("score.txt");
		fos.write(score.getBytes());//���ַ���ת���ֽ����飬д�����ļ���
		fos.flush();//ˢ�����ݵ�Ӳ��
		fos.close();//�ر���
	}
	
	
}//�Զ������Ϸ��������

/**
 * �ذ壬���ڶ�Ӧ��Ļ�ϵĵذ�
 * @author Administrator
 *
 */
class Ground{
	int x,y;//ͬʱ���������������м���,����     �ذ�ͼƬ�����Ͻ�������x,y����λ��
	int width;//��
	int height;//��
	Image image;//�ذ�ͼƬ����
	/**
	 * ���췽������������  java�ഴ�������ʱ����Զ����õ�һ�ַ���
	 * �÷��������ⷽ��   1.�޷���ֵ����  2.������������һ��
	 * ���췽����������ʼ���������ϵ�����ֵ��
	 */
	public Ground(){
		width=800;
		height=146;
		x=0;
		y=498;//644-146
		image = new ImageIcon("img/ground.png").getImage();
	}
	//����ƶ�����     �÷���������������������ƶ�
	public void step(){
		x--;//�ذ�������
		if(x<= - (width-432)){//���Ƶ��߽�ֵ����λ
			x=0;
		}
	}	
}//�ذ������
/**
 * ˮ�ܣ���Ӧ��Ļ�ϵĶ��ˮ�ܶ���
 * 1.������Ա����
 * 2.�ڹ��췽���г�ʼ����Ա����
 * 3.ͨ��һЩ��ͨ�Զ���ķ�����������Ա����
 * @author Administrator
 *
 */
class Column{
	Image image;//ˮ��ͼ
	int x,y;//ˮ�ܵķ�϶���ĵ������
	int width,height;//ˮ�ܿ��
	int gap;//��϶��϶
	int distance;//ˮ��֮��ľ���
	/**
	 * ��Ĺ��췽�����ڴ��������ʱ������   �޷���ֵ���͡���������������ͬ
	 * ˮ�ܶ���������ˮ��1��ˮ��2       1��ʾˮ��1      2��ʾˮ��2
	 */
	public Column(int n){
		image = new ImageIcon("img/column.png").getImage();
		width = image.getWidth(null);//��ȡͼƬ���
		height= image.getHeight(null);
		gap = 144;//��϶ֵ
		distance = 245;
		//   1:432+0*245    2:   432+1*245
		x = 432+width/2 + (n-1)*245;//��һ��ˮ��λ����Ļ����⣬�ڶ���ˮ�ܾ����һ��ˮ��245
		y = randomY();//�������y����λ��
	}
	
	/**
	 * ˮ���˶�����(���㷽��   �㷨) 
	 * 1.x��ֵ�ݼ�
	 * 2.���ٺ��ж�x�Ƿ񵽴�߽�ֵ����������x��y���¸�ֵ
	 * 3.���û�е����򲻶�x��y��ֵ��������ǰ���˶�һ�����ܽ���   
	 */
	public void step(){
		x--;//xֵ����1
		if(x<=-width/2){
			x = -width/2+distance*2;
			y = randomY();//�������y����
		}
	}
	/**
	 * �������һ��ˮ��y����ֵ
	 * [120,370)   
	 * @return
	 */
	public int randomY(){
		Random random = new Random();//���������
		int y = random.nextInt(370-120)+120;//nextInt(bound):����[0,bound)ֱ�ӵ�����
		return y;
	}
	
	
}//ˮ�������
/**
 * ��Ӧ��Ļ�ϵ�С��
 * @author Administrator
 *
 */
class Bird{
	Image image;//С��ͼ��
	int x,y;//С��ͼƬ�����ĵ�����
	int width;//��
	int height;//��
	double g;//g�Ǵ�ֱ�˶����������ٶ�
	double v0;//��ʼ�ٶ� ��ʼ�ٶ�Խ���ƶ��߶�Խ��
	//�˶�һ��������ʱ��    ���ݸ��˶�ʱ�䣬���л�����ػ�  ��ֵԽС����ʾ������ػ�����
	//С���˶����˳��
	double t;
	double vt;//����ʱ��t�Ժ���ٶ�
	double s;//����ʱ��t����ƶ�����  �˶�һ�������ľ���
	//С��ͼƬ���飬��ʵ��С�����ȶ��Ķ���Ч������ͼƬ
	Image[] images;
	int index;//С��ͼƬ������±�ֵ
	double alpha;//���ֵ��������ʾС���˶��Ĺ����е�С��ͼƬ�ĽǶ�
	
	public Bird(){
		image = new ImageIcon("img/0.png").getImage();
		x=100;
		y=200;
		width=image.getWidth(null);
		height=image.getHeight(null);
		g=9.8;// m/s
		v0=30;// �Զ���   
		t=0.12;
		s=0;
		vt=0;
		//��ʼ���������  ��������8��ͼƬ������� 
		//[null,null,null,null,null,null,null,null]
		images = new Image[8];
		//��ʼ�������е�8��ͼƬ����
		for(int i=0;i<images.length;i++){
			images[i] = new ImageIcon("img/"+i+".png").getImage();
		}
		index=0;//�±긳ֵ
		alpha=0;
	}
	
	/**
	 * С���ƶ�һ��
	 * 1.����λ�ƾ���
	 * 2.�õ�λ�ƺ��С����µ�yֵ
	 * 3.�ж�С���yֵ���ﵽ�߽�ֵ���Զ�������
	 */
	public void step(){
		s = v0*t-g*t*t/2;//����λ��
		y = y - (int)s;// (int)s��s��double����ת����int����
		vt = v0-g*t;//���㾭��ʱ��t�Ժ���ٶȣ�����һ�μ���ĳ�ʼ�ٶ�
//		System.out.println(y+",vt:"+vt);
//		if(y>=300){
//			v0=50;//�ı���һ�μ���ĳ�ʼ�ٶ�Ϊ50
//		}else{
			v0 = vt; //�ı���һ�μ���ĳ�ʼ�ٶ�
//		}
		fly();//�ƶ�����һ��
	}
	
	/**
	 * С�����Ϸ���Ĺ��ܣ�ͨ���趨��ʼ�����ٶ�Ϊ��ֵ����ʵ�����Ϸ���
	 */
	public void flappy(){
		v0=30;
	}
	
	/**
	 * ÿ���ƶ�һ������һ��
	 * ͨ��ȡ�����㣬�õ�һ������ֵ[0~7)����ֵ��ͼƬ������±�
	 * ����ѭ����ʱ�����ĸ���ͼƬ(ÿ��һ�¾͸���ͼƬ)���ᵼ��ͼƬ������Ƶ�������治����
	 * ���������
	 * �Ƚ�index����һ���̶���������3������ʵ�ַ����κ�Ÿ���һ��ͼƬ
	 */
	public void fly(){
		image = images[index/3%8];
		//ԭ�棺index%8:0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7...
		//�Ľ��棺0 0 0 1 1 1 2 2 2 3 3 3 4 4 4...
		index++;
		/**
		 * ����ÿ��һ�ε�ʱ���С��ͼƬ�ĽǶ�
		 * �Ƕ�ֵ��Ҫͨ��java�ķ����к���������
		 * Math.atan(����) ��java�ṩ�ļ����ֵ��api 
		 */
		alpha = Math.atan(s/3);	
//		System.out.println(alpha);
	}
	
	/**
	 * �����ж�С���Ƿ���ײ�����㷨
	 * �������true���ʾ��ײ��
	 * ����false���ʾû������
	 * @return
	 */
	public boolean hit(Ground ground){
		/*
		 * �߽�ֵ��  С�������yֵ+С��ĸߵ�һ��=�ذ��y
		 */
		boolean hit = y+height/2>=ground.y;
		if(hit){//ײ�Ϻ���תͼƬ    ��С���yֵ����ص��߽�λ��
//			y=ground.y-height/2;
			alpha = - 3.14;
		}
		return hit;
	}
	
	/**
	 * �ж�С���Ƿ���ײˮ���㷨
	 * true��ʾ��ײ��
	 * false��ʾû����ײ��
	 * @param column
	 * @return
	 */
	public boolean hit(Column c){
		boolean hit;
		//�ж��Ƿ���ˮ�ܷ�Χ
		if(x+width/2-3>c.x-c.width/2&&
				x-width/2<c.x+c.width/2){
			//����������ʾ��ˮ�ܷ�Χ
			//����Ƿ��ڷ�϶��Χ
			if(y-height/2+3>c.y-c.gap/2&&
					y+height/2<c.y+c.gap/2){
				hit = false;
			}else{
				hit = true;
			}
		}else{//����ˮ�ܷ�Χ����ײ
			hit = false;
		}
		return hit;
	}
}//С�������



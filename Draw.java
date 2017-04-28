import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Draw extends KeyAdapter implements ActionListener{

	private JFrame frame;
	
	private int imageX = 690;
	private int imageY = 690;
	private int mouseX;
	private int mouseY;
	
	private Color currentBackgroundColor = Color.LIGHT_GRAY;
	private Color currentBrushColor = Color.BLACK;
	
	private byte currentpointShape;
	private byte currentSize;

	private boolean cursor = false;
	private boolean fileHasBeenOpened = false;
	private boolean fileIsOpened = false;
	private boolean fastclean = true;
	
	private ArrayList<Point>drawPoints = new ArrayList<Point>();
	private ArrayList<Color>pointColor = new ArrayList<Color>();
	private ArrayList<Byte>pointShape = new ArrayList<Byte>();
	private ArrayList<Byte>pointSize = new ArrayList<Byte>();
	
	private BufferedImage image = new BufferedImage(imageX,imageY,BufferedImage.TYPE_3BYTE_BGR);
	private BufferedImage toSet;
	
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
	
	private PaintGraphics Paint = new PaintGraphics();
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup groupForShape = new ButtonGroup();
	
	private JMenu fileMenu;
	private JMenu mnColors;
	private JMenu mnBrushSize;
	private JMenu mnEdit;
	private JMenu mnSettings;
	private JMenu clearMenu;
	private JMenu setSize;
	private JMenu brushShape;
	
	private JMenuItem openMenu;	
	private JMenuItem newMenu;
	private JMenuItem saveMenu;
	private JMenuItem backgroundMenu;
	private JMenuItem brushMenu;
    private JMenuItem clearlastMenu;
	
	private JCheckBox specialCur;
	private JCheckBox imageSize;
	
	private JRadioButton fastClear;
	private JRadioButton preciseClear;
	
	private JLabel label;
	
	private JSpinner sizeSpinner;
	
	private JRadioButton ovalShape;
	private JRadioButton squareShape;
	
	
	
    //frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("RED.PNG")));
    //newMenu.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("RED.PNG"))));
	/**
	 * drawPoint = point
	 * byte pointShapes = 1 - CIRCLE
	 * byte pointShapes = 2 - SQUARE
	 * byte pointSize 5-100 
	 * 
	 * 
	 */
	
	
	
	//TODO add tooltips
	//TODO add proper special cursor
	
	
	public static void main(String[] args) {
         Draw window = new Draw();
		 window.frame.setVisible(true);
	}

	
	
	public Draw() {
		initialize();
	}

	private void New(){
		drawPoints.clear();
		pointColor.clear();
		pointShape.clear();
		pointSize.clear();
		imageX = 690;
		imageY = 690;
		currentSize = 10;
		currentpointShape = 1;
		sizeSpinner.setValue(10);
        cursor = false;
        fileHasBeenOpened = false;
        fileIsOpened = false;
        fastclean = true;
		currentBrushColor = Color.BLACK;
		currentBackgroundColor = Color.LIGHT_GRAY;
		frame.setSize(700, 700);
		frame.setLocationRelativeTo(null);
		
	}
	private void initialize() {
	
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1){}
	    
		frame = new JFrame("Draw");
		sizeSpinner = new JSpinner();
		New();
		
	 	frame.getContentPane().add(Paint);	
		JMenuBar menuBar = new JMenuBar();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("mainIcon.png")));	
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		newMenu = new JMenuItem("New                           Ctrl + N");
		
		fileMenu.add(newMenu);
		
		openMenu = new JMenuItem("Open                          Ctrl + F");
		fileMenu.add(openMenu);
		
		saveMenu = new JMenuItem("Save                           Ctrl + S");
		fileMenu.add(saveMenu);
		
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		clearlastMenu = new JMenuItem("Clear last element                            Ctrl + Z");
		mnEdit.add(clearlastMenu);
		
		clearMenu = new JMenu("Clearing type");
		mnEdit.add(clearMenu);
		
		fastClear = new JRadioButton("Fast");
		fastClear.setSelected(true);
		buttonGroup.add(fastClear);
		clearMenu.add(fastClear);
		
		preciseClear = new JRadioButton("Precise");
		buttonGroup.add(preciseClear);
		clearMenu.add(preciseClear);
		
		mnColors = new JMenu("Colors");
		menuBar.add(mnColors);
		
		backgroundMenu = new JMenuItem("Background                 Ctrl + C");
		mnColors.add(backgroundMenu);
		
		brushMenu = new JMenuItem("Brush                            Ctrl + B");
		mnColors.add(brushMenu);
		
		mnBrushSize = new JMenu("Brush Tools");
		menuBar.add(mnBrushSize);
		
		setSize = new JMenu("Brush size");
		mnBrushSize.add(setSize);
		
		sizeSpinner = new JSpinner();
		sizeSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				currentSize = Byte.parseByte(sizeSpinner.getValue().toString());
			}
		});
		sizeSpinner.setModel(new SpinnerNumberModel(10, 5, 100, 1));
		setSize.add(sizeSpinner);
		
		brushShape = new JMenu("Brush shape");
		mnBrushSize.add(brushShape);
		
		ovalShape = new JRadioButton("Oval");
		ovalShape.setSelected(true);
		groupForShape.add(ovalShape);
		brushShape.add(ovalShape);
		
		squareShape = new JRadioButton("Square");
		groupForShape.add(squareShape);
		brushShape.add(squareShape);
		
		
		mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);
		
		
		specialCur = new JCheckBox("Display special cursor");;
		mnSettings.add(specialCur);
		
		imageSize = new JCheckBox("Display image size");
		mnSettings.add(imageSize);
		
		label = new JLabel("                                    Image size   X : "+imageX+" Y : "+imageY);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		label.setVisible(false);
		menuBar.add(label);
		
		
		//Listeners
		clearlastMenu.addActionListener(this);
		specialCur.addActionListener(this);
		ovalShape.addActionListener(this);
		squareShape.addActionListener(this);
		imageSize.addActionListener(this);
		saveMenu.addActionListener(this);
		newMenu.addActionListener(this);
		fastClear.addActionListener(this);
		preciseClear.addActionListener(this);
		openMenu.addActionListener(this);
		backgroundMenu.addActionListener(this);
		brushMenu.addActionListener(this);
		frame.addKeyListener(this);
		frame.addComponentListener(
				new ComponentAdapter(){
					public void componentResized(ComponentEvent e) {       
				        imageX = frame.getSize().width - 10;
				        imageY = frame.getSize().height - 10;
				        label.setText("                                    Image size   X : "+imageX+" Y : "+imageY);

				        if(imageX !=670 || imageY !=670)
				        	if(!fileIsOpened){
				        		image = new BufferedImage(imageX,imageY,BufferedImage.TYPE_3BYTE_BGR);
				        		label.setText("                                    Image size   X : "+imageX+" Y : "+imageY);
				        	}

				        	
				        				        
				    }
				}
				);
		Paint.addMouseListener(
				new MouseAdapter(){
					@Override
					public void mousePressed(MouseEvent arg0){
						drawPoints.add(new Point(arg0.getPoint()));
						pointColor.add(currentBrushColor);
						pointShape.add(currentpointShape);
						pointSize.add(currentSize);	
					}
					public void mouseClicked(MouseEvent e){
						//ONCLICK : for pointShapes only;
					}
				}
				);
		Paint.addMouseMotionListener(
				new MouseMotionListener(){
					@Override
					public void mouseDragged(MouseEvent arg0) {
						drawPoints.add(new Point(arg0.getPoint()));
						pointColor.add(currentBrushColor);
						pointShape.add(currentpointShape);
						pointSize.add(currentSize);	
					}

					@Override
					public void mouseMoved(MouseEvent arg0) {
						if(!cursor)
							specialCur.setSelected(false);
						mouseX = arg0.getX();
						mouseY = arg0.getY();
					}		
				}
				);
	}
	
	private class PaintGraphics extends JPanel{
		
		protected void paintComponent(Graphics g){

			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        g2.drawImage(image, 0, 0, null);
			
	          if(!fileHasBeenOpened){
				g2.setColor(currentBackgroundColor);
				g2.fillRect(0, 0, imageX, imageY);
				}
	          g2.setColor(currentBrushColor);
	          if(cursor){
					if(currentpointShape == 1) g2.drawOval(mouseX-(currentSize / 2), mouseY-(currentSize / 2), currentSize, currentSize);		
					else g2.drawRect(mouseX-(currentSize / 2), mouseY-(currentSize / 2), currentSize, currentSize);	
				}
				for(int i = 0;i<drawPoints.size();i++){
					g2.setColor(pointColor.get(i));
					
					if(pointShape.get(i) == 1){
						//Circle
						g2.fillOval(drawPoints.get(i).x-(pointSize.get(i) / 2), drawPoints.get(i).y-(pointSize.get(i) / 2),pointSize.get(i),pointSize.get(i));
						
					}else{
						//Square
						g2.fillRect(drawPoints.get(i).x-(pointSize.get(i) / 2), drawPoints.get(i).y-(currentSize / 2),pointSize.get(i),pointSize.get(i));
					}
					
				}
				
				this.repaint();
						
		}

		
		public void Save() throws IOException{
		
		     if(cursor)
		    	 cursor = false; 
			 JFileChooser fileChooser = new JFileChooser();
			 fileChooser.setFileFilter(filter);
			 int choice = fileChooser.showSaveDialog(null);
			 if(choice ==JFileChooser.APPROVE_OPTION){
				 if(fileChooser.getSelectedFile().toString().endsWith(".PNG") || fileChooser.getSelectedFile().toString().endsWith(".png")){
					 ImageIO.write(getImage(Paint), "PNG", new File(fileChooser.getSelectedFile().getAbsolutePath()));	
				 }
				 else if(fileChooser.getSelectedFile().toString().endsWith(".jpg") || fileChooser.getSelectedFile().toString().endsWith(".JPG")){
					 ImageIO.write(getImage(Paint), "JPG", new File(fileChooser.getSelectedFile().getAbsolutePath()));
				 }
				 else if(fileChooser.getSelectedFile().toString().endsWith(".jpeg") || fileChooser.getSelectedFile().toString().endsWith(".JPEG")){
					 ImageIO.write(getImage(Paint), "JPEG", new File(fileChooser.getSelectedFile().getAbsolutePath()));
				 }else{
					 ImageIO.write(getImage(Paint), "PNG", new File(fileChooser.getSelectedFile().getAbsolutePath()+".PNG"));
				 }
				 
			 }	
		}
		
		public void Open() throws IOException{
			 New();
			 JFileChooser fileChooser = new JFileChooser();
			 fileChooser.setFileFilter(filter);
			 int choice =fileChooser.showOpenDialog(null);
			 if(choice ==JFileChooser.APPROVE_OPTION){
				 toSet = ImageIO.read(new File(fileChooser.getSelectedFile().getAbsolutePath()));
				 frame.setLocation(0, 0);
				 frame.setSize(toSet.getWidth()+10, toSet.getHeight()+10);
				 image = ImageIO.read(new File(fileChooser.getSelectedFile().getAbsolutePath())); 
				 fileHasBeenOpened = true;
				 fileIsOpened = true; 
			 } 
		}
	}
    private BufferedImage getImage(JPanel panel){
    	int w = panel.getWidth();
    	int h = panel.getHeight();
    	BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
    	Graphics2D g = bi.createGraphics();
    	panel.paint(g);
    	return bi;
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() ==saveMenu){
			try {Paint.Save();}catch(IOException e1){}
	
		}
		if(e.getSource() ==backgroundMenu){
			Color toSet;
			toSet = JColorChooser.showDialog(null,"Pick Color for background",currentBackgroundColor);
			if(toSet !=null)
				currentBackgroundColor = toSet;
			
		}
		if(e.getSource() == newMenu){
			New();
		}
		if(e.getSource() ==brushMenu){
			Color toSet;
			toSet = JColorChooser.showDialog(null, "Pick Color for brush",currentBrushColor);
			if(toSet !=null)
				currentBrushColor = toSet;
			
		}
		if(e.getSource() ==clearlastMenu){
			if(!drawPoints.isEmpty()){
				drawPoints.remove(drawPoints.size() - 1);
				pointColor.remove(pointColor.size() - 1);
				pointShape.remove(pointShape.size() - 1);
				pointSize.remove(pointSize.size() - 1);
				
			}
		}
		if(e.getSource() ==specialCur){
			if(specialCur.isSelected())
				cursor = true;
			else
				cursor = false;
		}
		if(e.getSource() ==openMenu){
			try {Paint.Open();}catch(IOException e1) {}
		}
		if(e.getSource() ==imageSize){
			if(imageSize.isSelected())
				label.setVisible(true);
			else
				label.setVisible(false);
		}
		if(e.getSource() ==fastClear)
			fastclean = true;
		if(e.getSource() ==preciseClear)
			fastclean = false;
		if(e.getSource() ==ovalShape)
			currentpointShape = 1;
		if(e.getSource() ==squareShape)
			currentpointShape = 2;
	}
 
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.isControlDown() && arg0.getKeyCode() ==KeyEvent.VK_Z){
			if(!fastclean){
				if(!drawPoints.isEmpty()){
					drawPoints.remove(drawPoints.size() - 1);
					pointColor.remove(pointColor.size() - 1);
					pointShape.remove(pointShape.size() - 1);
					pointSize.remove(pointSize.size() - 1);	
				}	
			}else{
				if(!drawPoints.isEmpty()){
					if(!(drawPoints.size()<5)){
						for(int i =0;i<5;i++){
							drawPoints.remove(drawPoints.size() - 1);
							pointColor.remove(pointColor.size() - 1);
							pointShape.remove(pointShape.size() - 1);
							pointSize.remove(pointSize.size() - 1);	
						}
					}else{
						drawPoints.remove(drawPoints.size() - 1);
						pointColor.remove(pointColor.size() - 1);
						pointShape.remove(pointShape.size() - 1);
						pointSize.remove(pointSize.size() - 1);	
					}
				}
			}
				
		}
		if(arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_F)
			try {Paint.Open();}catch(IOException e1) {}
		
		if(arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_S)
			try {Paint.Save();}catch(IOException e1){}
		if(arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_C)
			backgroundMenu.doClick();
		if(arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_B)
			brushMenu.doClick();
		if(arg0.isControlDown() && arg0.getKeyCode() ==KeyEvent.VK_N){
			New();
		}
		
	}




	
}

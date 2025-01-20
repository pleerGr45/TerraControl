package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
/**
 * <b>Класс панели загрузки файлов</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class FileLoaderPanel extends JPanelBack {
	
	/**
	 * <b>Путь к папке</b>
	 */
	public final String path;
	/**
	 * <b>Колекция файлов</b>
	 */
	private List<File> files;
	/**
	 * <b>Выбранный файл</b>
	 */
	private File currentFile;
	/**
	 * <b>Сдвиг в файловой системе</b>
	 */
	private int shift;
	/**
	 * <b>Текущая страница</b>
	 */
	private int list;
	/**
	 * <b>Директория</b>
	 */
	private File dir;
	
	public File getCurrentFile() {return currentFile;}
	public List<File> getFiles() {return files;}
	public void setCurrentFile(File file) {this.currentFile = file;}
	
	/**
	 * <b>Метод отрисовки</b>
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setFont(new Font("Times new roman", 1, 16));
		g.drawString("Страница "+(list+1)+" из "+((files.size()/6)+1), 20, 541);
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 551, 834, 561);
		
		switch(files.size()-(list*6) > 6 ? 6 : files.size()-(list*6)) {
		case 6:	drawFile(g, 585, 320, 5);
		case 5:	drawFile(g, 340, 320, 4);
		case 4:	drawFile(g, 95, 320, 3);
		case 3:	drawFile(g, 585, 85, 2);
		case 2:	drawFile(g, 340, 85, 1);
		case 1:	drawFile(g, 95, 85, 0);
		}
	} 
	
	/**
	 * <b>Конструктор класса {@link FileLoaderPanel}</b>
	 */
	public FileLoaderPanel(Image image, String path) {
		super(image);
		
		this.path = path;
		files = new ArrayList<>();
		shift = 0;
		list = 0;
		
		setLayout(null);
		
		dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		
		 recallFiles();
		 
		 JButton prevButton = new JButton("<");
		 prevButton.setBackground(Color.LIGHT_GRAY);
		 prevButton.setForeground(Color.DARK_GRAY);
		 prevButton.setBounds(680, 490, 50, 50);
		 prevButton.setFocusable(false);
		 prevButton.addActionListener(a -> {
			 list = list-1 > 0 ? list-1 : 0;
			 repaint();
		 });
		 add(prevButton);
		 
		 JButton nextButton = new JButton(">");
		 nextButton.setBackground(Color.LIGHT_GRAY);
		 nextButton.setForeground(Color.DARK_GRAY);
		 nextButton.setBounds(740, 490, 50, 50);
		 nextButton.setFocusable(false);
		 nextButton.addActionListener(a -> {
			 list = list+1 >= files.size()/6 ? (files.size()/6) : list+1;
			 repaint();
		 });
		 add(nextButton);
		 
		 addMouseListener(new MouseAdapter() {
			 
			 @Override
			 public void mousePressed(MouseEvent e) {
				 int x = e.getX();
				 int y = e.getY();
				 
				 int value = -1;
				 
				 if(y >= 85 && y <= 245) {
					 if(x >= 95 && x <= 245) {
						 value = (list*6);
					 } else if(x >= 340 && x <= 490) {
						 value = (list*6)+1;
					 } else if(x >= 585 && x <= 735) {
						 value = (list*6)+2;
					 }
				 } else if(y >= 320 && y <= 470) {
					 if(x >= 95 && x <= 245) {
						 value = (list*6)+3;
					 } else if(x >= 340 && x <= 490) {
						 value = (list*6)+4;
					 } else if(x >= 585 && x <= 735) {
						 value = (list*6)+5;
					 }
				 }
				 
				 if(value != -1) {
					 shift = value >= files.size() ? files.size()-1 : value; 
				 }
				 
				 repaint();
				 if(files.size() != 0) currentFile = files.get(shift);
			 }
		 });
	}

	/**
	 * <b>Метод отрисовки одного файла</b>
	 * @param g - <i>Графический компонент</i>
	 * @param sX - <i>Сдвиг по x</i>
	 * @param sY - <i>Сдвиг по y</i>
	 * @param shift - <i>Сдвиг в файловой системе</i>
	 */
	private void drawFile(Graphics g, int sX, int sY, int shift) {
		g.setColor(this.shift == shift+(list*6) ? new Color(83, 0, 150) : Color.DARK_GRAY);
		g.fillRect(sX, sY, 150, 150);
		
		g.setColor(this.shift == shift+(list*6) ? new Color(252, 180, 213) : Color.BLACK);
		g.setFont(new Font("Times new roman", 1, 14));
		g.drawString(files.get(shift+(list*6)).getName().replaceAll(".world", ""), sX+20, sY+20);
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(sX+10, sY+30, 130, 110);
		
		Image img = GraphicsPack.imageLoader(path+"/"+files.get(shift+(list*6)).getName()+".png");
		
		if(img != null) g.drawImage(img, sX+12, sY+32, 126, 106, null);
	}
	
	/**
	 * <b>Метод перевызова файлов</b>
	 */
	public void recallFiles() {
		files.clear();
		
		File[] absFiles = dir.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return getStringPastSeparator(name, '.').equals("world") ? true : false;
		}
		});
		
		if(absFiles.length == 0) return;
		
		for(int i = 0; i < absFiles.length; i++) {
		files.add(absFiles[i]);
		}
		
		shift = 0;
		currentFile = files.get(shift);
		repaint();
	}
	
	/**
	 * <b>Метод получения списка имён файлов</b>
	 * @return Массив типа {@link String} с именами файлов {@link FileLoaderPanel#files}
	 */
	public String[] getFilesName() {
		String[] strings = new String[files.size()+1];
		
		strings[0] = "Генерация";
		
		for(int i = 1; i < files.size()+1; i++) {
			strings[i] = files.get(i-1).getName();
		}
		
		return strings;
	}
	
	/**
	 * <b>Метод получения строки после разделителя</b>
	 * @param string - <i>Строка</i>
	 * @param separator - <i>Разделитель</i>
	 * @return Массив символов идущий после разделителя separator в строке string
	 */
	public static String getStringPastSeparator(String string, char separator) {
		String str = "";
		boolean b = string.charAt(0) == separator ? true : false;
		
		for(int i = 0; i < string.length(); i++) {
			if(b) str += string.charAt(i);
			
			if(string.charAt(i) == separator) b = true;
		}
		
		return str;
	}
	
	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = -4878800297904632452L;
}

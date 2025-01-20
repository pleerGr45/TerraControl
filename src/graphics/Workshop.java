package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

import com.writer.FileStringWriter;

import logic.cells.AbstractCell;
import logic.cells.ActiveCell;
import logic.cells.Cell;
import logic.cells.Content;
import logic.cells.VoidCell;
import logic.process.IOStreamLogic;
import logic.process.LO;
import logic.user.Bot;
import logic.user.Player;
import logic.user.User;
import logic.user.UserPoolExecutor;

/**
 * <b>Класс панели редактора карт</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class Workshop extends JPanelBack {

	/**
	 * <b>Панель загрузки файлов</b>
	 */
	private FileLoaderPanel filePanel;
	/**
	 * <b>Массив клеток</b>
	 */
	private AbstractCell[][] cells;
	/**
	 * <b>Обработчик пользователей</b>
	 */
	private UserPoolExecutor users;
	/**
	 * <b>Таймер отрисовки</b>
	 */
	private Timer timer;
	/**
	 * <b>Панель</b>
	 */
	private JPanel actionPanel, fieldPanel, contentPanel;
	/**
	 * <b>Панель отрисовки пользователей</b>
	 */
	private JPlayerStatPanel userPanel;
	/**
	 * <b>Кнопка</b>
	 */
	private JButton saveButton, createButton, changeButton, closeButton, renameButton, deleteButton;
	/**
	 * <b>Размеры для отрисовки</b>
	 */
	byte scale, size;
	/**
	 * <b>Сдвиг отрисовки</b>
	 */
	int shiftX, shiftY;
	/**
	 * <b>Коллекция переключаемых кнопок</b>
	 */
	private List<JToggleButton> buttons;
	/**
	 * <b>Объект изменения клетки</b>
	 */
	private Object obj;
	/**
	 * <b>Проверка на изменение цвета клеток при смене цвета игрока</b>
	 */
	private boolean coloredCellByUser;
	
	public void setColoredCell(boolean coloredCellByUser) {this.coloredCellByUser = coloredCellByUser;}
	public FileLoaderPanel getFilePanel() {return filePanel;}
	
	/**
	 * <b>Конструктор класса {@link Workshop}</b>
	 * @param image - <i>Изображение фона</i>
	 * @param coloredCellByUser - <i>Проверка на измениение цвета</i>
	 */
	public Workshop(Image image, boolean coloredCellByUser) {
		super(image);
		
		scale = 20;
		shiftX = shiftY = 0;
		obj = Content.VOID;
		buttons = new ArrayList<>();
		
		this.coloredCellByUser = coloredCellByUser;
		
		filePanel = new FileLoaderPanel(image, "assets/resource/worlds");
		filePanel.setBounds(0, 0, 834, 561);
		add(filePanel);
		
		filePanel.setVisible(true);
		
		//Панели
		actionPanel = new JPanel();
		actionPanel.setBounds(0, 0, 834, 600);
		actionPanel.setLayout(null);
		actionPanel.setVisible(false);
		add(actionPanel);
		
		fieldPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(cells == null) return;
				
				//Задний фон
				g.drawImage(image, 0, 0, null);
				
				//Размер изображения
				size = (byte)cells[0][0].getColor().getImage().getScaledInstance(scale, scale, 0).getWidth(null);
				
				//Цикл отрисовки клеток с фильтром
				for(byte x = 0; x < cells.length; x++) {
					for(byte y = 0; y < cells[x].length; y++) {
						
						//Временные переменные, хранящие положение клетки на эране
						int fx = (y % 2 == 0 ? x*size : x*size+size/2)+shiftX, fy = (y*(size/4)*3)+shiftY;
						
						//Отрисовка клетки
						g.drawImage(cells[x][y].getColor().getImage(), fx, fy, scale, scale, null);
						
						//Отрисовка
						if(cells[x][y] instanceof ActiveCell) g.drawImage(((ActiveCell)cells[x][y]).getContent().getImage(), fx, fy, scale, scale, null);
					}
				}
				
				//Прекрашение использования ресурсов компьютера за данный ход перерисовки
				g.dispose();
			}
		};
		fieldPanel.setBounds(0, 0, 670, 450);
		fieldPanel.setFocusable(true);
		actionPanel.add(fieldPanel);
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 450, 670, 150);
		contentPanel.setLayout(null);
		contentPanel.setBackground(Color.GRAY);
		actionPanel.add(contentPanel);
		
		userPanel = new JPlayerStatPanel(image, users);
		userPanel.setBounds(670, 0, 164, 600);
		userPanel.setBackground(Color.DARK_GRAY);
		actionPanel.add(userPanel);
		
		//Кнопки
		saveButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/saveButton_icon.png")));
		saveButton.setBounds(10, 610, 40, 40);
		saveButton.setBackground(Color.LIGHT_GRAY);
		saveButton.addActionListener(a -> {
			
			IOStreamLogic.inputStream(new FileStringWriter(filePanel.getCurrentFile().getParent().replace('\\', '/'), filePanel.getCurrentFile().getName(), cells.length+"\n"+cells[0].length), cells, users); 
			
			filePanel.setVisible(true);
			createButton.setVisible(true);
			changeButton.setVisible(true);
			renameButton.setVisible(true);
			deleteButton.setVisible(true);
			actionPanel.setVisible(false);
			saveButton.setVisible(false);
			closeButton.setVisible(false);
		});
		saveButton.setVisible(false);
		add(saveButton);
		
		closeButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/closeButton_icon.png")));
		closeButton.setBounds(60, 610, 40, 40);
		closeButton.setBackground(Color.LIGHT_GRAY);
		closeButton.addActionListener(a -> {
			filePanel.setVisible(true);
			createButton.setVisible(true);
			changeButton.setVisible(true);
			renameButton.setVisible(true);
			deleteButton.setVisible(true);
			actionPanel.setVisible(false);
			saveButton.setVisible(false);
			closeButton.setVisible(false);
		});
		closeButton.setVisible(false);
		add(closeButton);
		
		createButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/createButton_icon.png")));
		createButton.setBounds(10, 610, 40, 40);
		createButton.setBackground(Color.LIGHT_GRAY);
		createButton.addActionListener(a -> {
			String input = JOptionPane.showInputDialog(this, "Введите название файла", "Ввод данных", 1);
			
			File file = new File(filePanel.path+"/"+input+".world");
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			filePanel.recallFiles();
			filePanel.setCurrentFile(file);
		});
		add(createButton);
		
		changeButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/changeButton_icon.png")));
		changeButton.setBounds(60, 610, 40, 40);
		changeButton.setBackground(Color.LIGHT_GRAY);
		changeButton.addActionListener(a -> {
			try {
				Object[] obj = IOStreamLogic.outputStream(new Scanner(filePanel.getCurrentFile()));
				
				cells = ((AbstractCell[][])obj[0]);
				users = ((UserPoolExecutor)obj[1]);
				
			} catch (Exception e1) {
				
				byte sizeX = Byte.parseByte(JOptionPane.showInputDialog(this, "Введите размер карты по X:", "Ввод данных", 1));
				byte sizeY = Byte.parseByte(JOptionPane.showInputDialog(this, "Введите размер карты по Y:", "Ввод данных", 1));
				byte playerQ = Byte.parseByte(JOptionPane.showInputDialog(this, "Введите количество игроков:", "Ввод данных", 1));
				byte botQ = Byte.parseByte(JOptionPane.showInputDialog(this, "Введите количество ботов:", "Ввод данных", 1));
				
				if(sizeX < 7 || sizeX > 30) {sizeX = 15;}
				if(sizeY < 7 || sizeY > 30) {sizeY = 15;}
				if(playerQ+botQ < 2 || playerQ+botQ > 12) {playerQ = 1; botQ = 3;}
				
				cells = new AbstractCell[sizeY][sizeX];
				users = UserPoolExecutor.randomize(playerQ, botQ);
				UserPoolExecutor pool = new UserPoolExecutor(null, false);
				
				for(byte i = 0; i < users.getPool().size(); i++) {
					pool.getPool().add(users.getPool().get(i));
				}
				
				cells = LO.generate((byte)sizeX, (byte)sizeY, pool);
			}
			
			userPanel.setUsers(users);
			
			filePanel.setVisible(false);
			createButton.setVisible(false);
			changeButton.setVisible(false);
			renameButton.setVisible(false);
			deleteButton.setVisible(false);
			actionPanel.setVisible(true);
			saveButton.setVisible(true);
			closeButton.setVisible(true);
		});
		add(changeButton);
		
		renameButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/renameButton_icon.png")));
		renameButton.setBounds(110, 610, 40, 40);
		renameButton.setBackground(Color.LIGHT_GRAY);
		renameButton.addActionListener(a -> {
			try {
				String input = JOptionPane.showInputDialog(this, "Введите название файла", "Ввод данных", 1);
				String oldPath = filePanel.getCurrentFile().getAbsolutePath();
				filePanel.getCurrentFile().renameTo(new File(filePanel.path+"/"+input+".world"));
				File pngFile = new File(oldPath+".png");
				if(pngFile.exists()) {
					 pngFile.renameTo(new File(filePanel.path+"/"+input+".world.png"));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			filePanel.recallFiles();
		});
		add(renameButton);
		
		deleteButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/deleteButton_icon.png")));
		deleteButton.setBounds(160, 610, 40, 40);
		deleteButton.setBackground(Color.LIGHT_GRAY);
		deleteButton.addActionListener(a -> {
			try {
				
				if(JOptionPane.showConfirmDialog(this, "Вы действительно хотите удалить данный файл?", "Подтверждение", JOptionPane.YES_NO_OPTION) == 0 && filePanel.getCurrentFile().delete()) {
					new File(filePanel.getCurrentFile().getAbsolutePath()+".png").delete();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			filePanel.recallFiles();
		});
		add(deleteButton);
		
		createSwitchButton(new JToggleButton(), 10, 10, new ImageIcon(logic.cells.Color.getImage((byte)0)), logic.cells.Color.VOID);
		createSwitchButton(new JToggleButton(), 50, 10, new ImageIcon(logic.cells.Color.getImage((byte)1)), logic.cells.Color.NITRAL);
		createSwitchButton(new JToggleButton(), 90, 10, new ImageIcon(logic.cells.Color.getImage((byte)2)), logic.cells.Color.RED);
		createSwitchButton(new JToggleButton(), 130, 10, new ImageIcon(logic.cells.Color.getImage((byte)3)), logic.cells.Color.ORANGE);
		createSwitchButton(new JToggleButton(), 170, 10, new ImageIcon(logic.cells.Color.getImage((byte)4)), logic.cells.Color.YELLOW);
		createSwitchButton(new JToggleButton(), 210, 10, new ImageIcon(logic.cells.Color.getImage((byte)5)), logic.cells.Color.LIME);
		createSwitchButton(new JToggleButton(), 250, 10, new ImageIcon(logic.cells.Color.getImage((byte)6)), logic.cells.Color.GREEN);
		createSwitchButton(new JToggleButton(), 290, 10, new ImageIcon(logic.cells.Color.getImage((byte)7)), logic.cells.Color.BLUE);
		createSwitchButton(new JToggleButton(), 330, 10, new ImageIcon(logic.cells.Color.getImage((byte)8)), logic.cells.Color.LIGHT_BLUE);
		createSwitchButton(new JToggleButton(), 370, 10, new ImageIcon(logic.cells.Color.getImage((byte)9)), logic.cells.Color.CYAN);
		createSwitchButton(new JToggleButton(), 410, 10, new ImageIcon(logic.cells.Color.getImage((byte)10)), logic.cells.Color.PINK);
		createSwitchButton(new JToggleButton(), 450, 10, new ImageIcon(logic.cells.Color.getImage((byte)11)), logic.cells.Color.PURPUR);
		createSwitchButton(new JToggleButton(), 490, 10, new ImageIcon(logic.cells.Color.getImage((byte)12)), logic.cells.Color.PURPLE);
		createSwitchButton(new JToggleButton(), 530, 10, new ImageIcon(logic.cells.Color.getImage((byte)13)), logic.cells.Color.WHITE);

		createSwitchButton(new JToggleButton(), 10, 50, new ImageIcon(Content.getImage((byte)0)), Content.VOID);
		createSwitchButton(new JToggleButton(), 50, 50, new ImageIcon(Content.getImage((byte)1)), Content.BASE);
		createSwitchButton(new JToggleButton(), 90, 50, new ImageIcon(Content.getImage((byte)2)), Content.DEFENS_I);
		createSwitchButton(new JToggleButton(), 130, 50, new ImageIcon(Content.getImage((byte)3)), Content.DEFENS_II);
		createSwitchButton(new JToggleButton(), 170, 50, new ImageIcon(Content.getImage((byte)4)), Content.ATTACK_I);
		createSwitchButton(new JToggleButton(), 210, 50, new ImageIcon(Content.getImage((byte)5)), Content.ATTACK_II);
		createSwitchButton(new JToggleButton(), 250, 50, new ImageIcon(Content.getImage((byte)6)), Content.ATTACK_III);
		createSwitchButton(new JToggleButton(), 290, 50, new ImageIcon(Content.getImage((byte)7)), Content.ATTACK_IV);
		createSwitchButton(new JToggleButton(), 330, 50, new ImageIcon(Content.getImage((byte)8)), Content.MORTAR);
		createSwitchButton(new JToggleButton(), 370, 50, new ImageIcon(Content.getImage((byte)9)), Content.MINE_I);
		createSwitchButton(new JToggleButton(), 410, 50, new ImageIcon(Content.getImage((byte)10)), Content.MINE_II);
		createSwitchButton(new JToggleButton(), 450, 50, new ImageIcon(Content.getImage((byte)11)), Content.MINE_III);
		
		createSwitchButton(new JToggleButton(), 10, 100, new ImageIcon(logic.cells.Color.getImage((byte)1)), Cell.class);
		createSwitchButton(new JToggleButton(), 50, 100, new ImageIcon(Content.getImage((byte)0)), VoidCell.class);
		
		JButton redButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/changeButton_icon.png")));
		redButton.setBounds(620, 10, 40, 40);
		redButton.setBackground(Color.LIGHT_GRAY);
		redButton.addActionListener(a -> {
			
			User user = null;
			
			try {
				user = users.get(Byte.parseByte(JOptionPane.showInputDialog(this, "Введите индекс пользователя: ", "Ввод данных", 1)));
			} catch (Exception e) {JOptionPane.showMessageDialog(this, "Данный индекс не является корректным, или такого индекса не существует", "Ошибка ввода", 0); return;}
			
			String name = JOptionPane.showInputDialog(this, "Введите имя пользователя: ", "Ввод данных", 1);
			logic.cells.Color oldColor = user.getColor();
			logic.cells.Color color = logic.cells.Color.getColorByString(JOptionPane.showInputDialog(this, "Введите цвет пользователя: ", "Ввод данных", 1));
			int coins = Integer.parseInt(JOptionPane.showInputDialog(this, "Введите число монет пользователя: ", "Ввод данных", 1));
			
			user.setName(name);
			user.setColor(color);
			user.setCoins(coins);
			
			if(this.coloredCellByUser) {
				for(byte x = 0; x < cells.length; x++) {
					for(byte y = 0; y < cells[x].length; y++) {
						if(cells[x][y].getColor() == oldColor) {
							cells[x][y].setColor(color);
						}
					}
				}
			}
		});
		redButton.setFocusable(false);
		contentPanel.add(redButton);
		
		JButton userButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/createButton_icon.png")));
		userButton.setBounds(620, 55, 40, 40);
		userButton.setBackground(Color.LIGHT_GRAY);
		userButton.addActionListener(a -> {
			char c;
			
			try {
			c = JOptionPane.showInputDialog(this, "Кем является пользователь (B - бот, P - игрок): ", "Ввод данных", 1).charAt(0);
			} catch (Exception e) {JOptionPane.showMessageDialog(this, "Данный индекс не является корректным, или такого индекса не существует", "Ошибка ввода", 0); return;}
			
			String name = JOptionPane.showInputDialog(this, "Введите имя пользователя: ", "Ввод данных", 1);
			logic.cells.Color color = logic.cells.Color.getColorByString(JOptionPane.showInputDialog(this, "Введите цвет пользователя: ", "Ввод данных", 1));
			int coins = Integer.parseInt(JOptionPane.showInputDialog(this, "Введите число монет пользователя: ", "Ввод данных", 1));
				
			users.add(c == 'P' ? new Player(color, coins, name, false) : new Bot(color, coins, name, false));
				
		});
		userButton.setFocusable(false);
		contentPanel.add(userButton);
		
		JButton delButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/deleteButton_icon.png")));
		delButton.setBounds(620, 100, 40, 40);
		delButton.setBackground(Color.LIGHT_GRAY);
		delButton.addActionListener(a -> {
			try {
				users.getPool().remove(Byte.parseByte(JOptionPane.showInputDialog(this, "Введите индекс удаляемого пользователя: ", "Ввод данных", 1)));
			} catch (Exception e) {JOptionPane.showMessageDialog(this, "Данный индекс не является корректным, или такого индекса не существует", "Ошибка ввода", 0);}
		});
		delButton.setFocusable(false);
		contentPanel.add(delButton);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				
				if(!actionPanel.isVisible()) return;
				
				//полежение курсора на эране переводится в положение клетки в масиве
				int y = (e.getY()-shiftY)/((size/4)*3);
				int x = y % 2 == 0 ? (e.getX()-shiftX)/size : ((e.getX()-shiftX)-(size/2))/size;
				
				//Проверка на ошибки
				if(x < 0 || x >= cells.length || y < 0 || y >= cells[x].length) return;
				
				AbstractCell cell = cells[x][y];
				
				if(obj instanceof logic.cells.Color) {
					cell.setColor((logic.cells.Color)obj);
				} else if(obj instanceof Content && cell instanceof Cell) {
					((Cell)cell).setContent((Content)obj);
				} else if(obj.equals(Cell.class)) {
					cells[x][y] = new Cell(logic.cells.Color.NITRAL, Content.VOID, false);
				} else if(obj.equals(VoidCell.class)) {
					cells[x][y] = new VoidCell(logic.cells.Color.VOID, false);
				} 
		}});
		
		timer = new Timer(10, a -> {
			repaint();
			userPanel.repaint();
		});
		timer.start();
	}
	
	/**
	 * <b>Запуск отрисовки</b>
	 */
	public void startPainting() {timer.start();}
	
	/**
	 * <b>Остановка отрисовки</b>
	 */
	public void stopPainting() {timer.stop();}
	
	/**
	 * <b>Создание переключаемой кнопки</b>
	 * @param button - <i>Кнопка</i>
	 * @param x - <i>Положение по x</i>
	 * @param y - <i>Положение по y</i>
	 * @param icon - <i>Изображение</i>
	 * @param obj - <i>Объект изменения клетки</i>
	 */
	private void createSwitchButton(JToggleButton button, int x, int y, Icon icon, Object obj) {
		button.setBounds(x, y, 40, 40);
		button.setBackground(Color.LIGHT_GRAY);
		button.setIcon(icon);
		button.addActionListener(a -> {
			for(byte i = 0; i < buttons.size(); i++) {
				buttons.get(i).setSelected(false);
			}
			this.obj = obj;
			button.setSelected(true);	
		});
		button.setSelected(false);
		button.setFocusable(false);
		contentPanel.add(button);
		
		buttons.add(button);
	}
	
	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = -5376258520000732723L;
}

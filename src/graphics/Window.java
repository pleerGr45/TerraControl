package graphics;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import com.writer.FileStringWriter;

import logic.cells.AbstractCell;
import logic.cells.ActiveCell;
import logic.process.IOStreamLogic;
import logic.process.LO;
import logic.process.Logic;
import logic.user.Player;
import logic.user.UserPoolExecutor;
import main.Main;

import java.awt.Color;

/**
 * <b>Класс окна игры</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class Window extends JFrame {

	/**
	 * <b>Кнопка</b>
	 */
	private JButton playButton,
	levelButton,
	masterButton,
	settingsButton,
	exitButton;
	/**
	 * <b>Главная панель</b>
	 */
	private JPanelBack mainPanel;
	/**
	 * <b>Панель настроек</b>
	 */
	private JPanelBack settingsPanel;
	/**
	 * <b>Панель уровней</b>
	 */
	private LevelPanel levelPanel;
	/**
	 * <b>Панель редактора карт</b>
	 */
	private Workshop masterPanel;
	/**
	 * <b>Панель</b>
	 */
	private JPanel actionPanel, activePanel;
	/**
	 * <b>Игровая панель</b>
	 */
	private GamePanel gamePanel;
	/**
	 * <b>Изображение заднего фона</b>
	 */
	private Image back, ground;
	/**
	 * <b>Карта</b>
	 */
	private AbstractCell[][] map;
	/**
	 * <b>Набор пользователей</b>
	 */
	private UserPoolExecutor users;
	/**
	 * <b>Количество пользователей при генерации</b>
	 */
	private byte playerQuantity, botQuantity;
	/**
	 * <b>Размер карты при генерации</b>
	 */
	private int sizeX, sizeY;
	/**
	 * <b>Проверка на уничтожение</b>
	 */
	private boolean defeat;
	
	/**
	 * <b>Логика</b>
	 * <p>Поле общего доступа с модификатором static. <i>(Одна логика для всей программы => одновременно может проходить только одна игра)</i> 
	 * @see Logic
	 */
	public static Logic l = new Logic(null, null, true, (byte)50);
	
	public void setCells(AbstractCell[][] cells) {this.map = cells;}
	public void setUsers(UserPoolExecutor pool) {this.users = pool;}
	public GamePanel getGamePanel() {return gamePanel;}
	
	/**
	 * <b>Конструктор класса {@link Window}</b>
	 */
	public Window() {
		
		//Загрузка заднего фона
		back = GraphicsPack.imageLoader("assets/images/backgrounds/menu_background.png");
		ground = GraphicsPack.imageLoader("assets/images/backgrounds/game_back.png");
		
		//Первоначальные настройки окна
		setBounds(700, 200, 850, 700);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setFocusable(true);
		setTitle("Terra control");
		try {setIconImage(ImageIO.read(new File("assets/images/attributes/icons/window_icon.png")));
		} catch (IOException e1) {Main.logger.warning("Файл не загрузился");}
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				
				switch(e.getKeyCode()) {
				case 107: masterPanel.scale = gamePanel.scale += gamePanel.scale + 20 > 80 ? 0 : 20; break;
				case 109: masterPanel.scale = gamePanel.scale -= gamePanel.scale - 20 < 20 ? 0 : 20; break;
				case 38: masterPanel.shiftY = gamePanel.shiftY+= gamePanel.shiftY+11*(gamePanel.scale/20) <= 36*(gamePanel.scale/20) ? 11*(gamePanel.scale/20) : 0; break;
				case 37: masterPanel.shiftX = gamePanel.shiftX+= gamePanel.shiftX+11*(gamePanel.scale/20) <= 50*(gamePanel.scale/20) ? 11*(gamePanel.scale/20) : 0; break;
				case 40: masterPanel.shiftY = gamePanel.shiftY-= gamePanel.shiftY-11*(gamePanel.scale/20) >= -360*(gamePanel.scale/20) ? 11*(gamePanel.scale/20) : 0; break;
				case 39: masterPanel.shiftX = gamePanel.shiftX-= gamePanel.shiftX-11*(gamePanel.scale/20) >= -500*(gamePanel.scale/20) ? 11*(gamePanel.scale/20) : 0; break;
				case 79: masterPanel.shiftX = gamePanel.shiftX=0; masterPanel.shiftY = gamePanel.shiftY=0; break;
				case 27: gamePanel.setMoveClick(false); l.setSelectedCell(null); break;
				case 32: if(l.getSelectedCell() != null) l.sell(((ActiveCell)l.getSelectedCell()).getContent(), l.getCurrentPlayer(), l.getSelectedCell()); break;
				case 16: gamePanel.getMoveButton().doClick(); break;
				case 87: l.setSelectedCell((byte)0, (byte)-1); break;
				case 65: l.setSelectedCell((byte)-1, (byte)0); break;
				case 83: l.setSelectedCell((byte)0, (byte)+1); break;
				case 68: l.setSelectedCell((byte)+1, (byte)0); break;
				case 91: gamePanel.setUserShift((byte)0); break;
				case 93: gamePanel.setUserShift((byte)1); break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
		
		//                                    \\
		//        СОЗДАНИЕ КОМПОНЕНТОВ        \\
		//                                    \\
		mainPanel = new JPanelBack(back);
		settingsPanel = new JPanelBack(ground);
		actionPanel = new JPanel();
		levelPanel = new LevelPanel(ground, this);
		masterPanel = new Workshop(ground, true);
		activePanel = new JPanel();
		
		//                                \\
		//        НАСТРОЙКА КНОПОК        \\
		//                                \\
		playButton = new JButton("Играть");
		playButton.setBackground(Color.LIGHT_GRAY);
		playButton.setBounds(342, 85, 150, 70);
		playButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		playButton.setFocusable(false);
		
		levelButton = new JButton("Уровни");
		levelButton.setBackground(Color.LIGHT_GRAY);
		levelButton.setBounds(342, 185, 150, 70);
		levelButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		levelButton.setFocusable(false);
		
		masterButton = new JButton("Редактор");
		masterButton.setBackground(Color.LIGHT_GRAY);
		masterButton.setBounds(342, 285, 150, 70);
		masterButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		masterButton.setFocusable(false);
		
		settingsButton = new JButton("Настройки");
		settingsButton.setBackground(Color.LIGHT_GRAY);
		settingsButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		settingsButton.setBounds(342, 385, 150, 70);
		settingsButton.setFocusable(false);
		
		exitButton = new JButton("Выход");
		exitButton.setBackground(Color.LIGHT_GRAY);
		exitButton.setBounds(342, 485, 150, 70);
		exitButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		exitButton.setFocusable(false);
		
		//                                 \\
		//        НАСТРОЙКА ПАНЕЛЕЙ        \\
		//                                 \\
		
		//Главная панель
		mainPanel.setBounds(0, 0, 834, 661);
		mainPanel.setLayout(null);
		mainPanel.add(playButton);
		mainPanel.add(levelButton);
		mainPanel.add(masterButton);
		mainPanel.add(settingsButton);
		mainPanel.add(exitButton);
		mainPanel.setLayout(null);
		mainPanel.setFocusable(false);
		getContentPane().add(mainPanel);
		mainPanel.setVisible(true);
		
		//Панель настроек
		settingsPanel.setBounds(0, 0, 834, 661);
		settingsPanel.setLayout(null);
		
		JButton returnButton1 = new JButton("Назад");
		returnButton1.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsPanel.setVisible(false);
				mainPanel.setVisible(true);
				Main.logger.info("Панель настроек закрыта");
			}
		});
		returnButton1.setFocusable(false);
		returnButton1.setBackground(Color.LIGHT_GRAY);
		returnButton1.setForeground(Color.DARK_GRAY);
		returnButton1.setBounds(724, 601, 100, 50);
		settingsPanel.add(returnButton1);
		
		settingsPanel.setFocusable(false);
		getContentPane().add(settingsPanel);
		settingsPanel.setVisible(false);
		
		String[] model = {"Рандом ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
		
		JSpinner spinSX = new JSpinner(new SpinnerNumberModel(7, 7, 30, 1));
		JSpinner spinSY = new JSpinner(new SpinnerNumberModel(7, 7, 30, 1));
		JSpinner spinSpeed = new JSpinner(new SpinnerNumberModel(50, 1, 50, 1));
		JSpinner spinPQ = new JSpinner(new SpinnerListModel(model));
		JSpinner spinBQ = new JSpinner(new SpinnerListModel(model));
		JComboBox<String> menuList = new JComboBox<>();
		
		String[] strings = masterPanel.getFilePanel().getFilesName();
		
		menuList.setModel(new DefaultComboBoxModel<String>(strings));
		
		createLable(new JLabel("Настройки"), settingsPanel, 300, 25, 250, 50, 35);
		createLable(new JLabel("Настраиваемая генерация"), settingsPanel, 50, 75, 275, 50, 20);
		createLable(new JLabel("Размер карты по X: "), settingsPanel, 50, 125, 175, 50, 17);
		createLable(new JLabel("Размер карты по Y: "), settingsPanel, 50, 160, 175, 50, 17);
		createLable(new JLabel("Количество игроков: "), settingsPanel, 50, 225, 180, 50, 17);
		createLable(new JLabel("Количество ботов: "), settingsPanel, 50, 260, 175, 50, 17);
		createLable(new JLabel("Настройки графики"), settingsPanel, 50, 325, 275, 50, 20);
		createLable(new JLabel("Смерть клетки: "), settingsPanel, 50, 375, 175, 50, 17);
		createLable(new JLabel("Взрыв клетки: "), settingsPanel, 50, 410, 175, 50, 17);
		
		createLable(new JLabel("Загрузка карты"), settingsPanel, 500, 75, 275, 50, 20);
		createLable(new JLabel("Загрузить карту из: "), settingsPanel, 500, 125, 175, 50, 17);
		createLable(new JLabel("Настройки редактора"), settingsPanel, 500, 190, 275, 50, 20);
		createLable(new JLabel("Менять цвет клеток: "), settingsPanel, 500, 240, 175, 50, 17);
		createLable(new JLabel("Игровые правила"), settingsPanel, 500, 305, 275, 50, 20);
		createLable(new JLabel("Скорость процесса: "), settingsPanel, 500, 355, 175, 50, 17);
		
		JRadioButton rbset = new JRadioButton(), rb2set = new JRadioButton(), rb3set = new JRadioButton();
		createRadioButton(rbset, settingsPanel, Color.LIGHT_GRAY, true, 190, 390, 20, 20);
		createRadioButton(rb2set, settingsPanel, Color.LIGHT_GRAY, true, 190, 425, 20, 20);
		createRadioButton(rb3set, settingsPanel, Color.LIGHT_GRAY, true, 680, 255, 20, 20);
		
		spinSX.setBounds(230, 140, 75, 25);
		settingsPanel.add(spinSX);
		
		spinSY.setBounds(230, 175, 75, 25);
		settingsPanel.add(spinSY);
		
		spinPQ.setBounds(230, 240, 75, 25);
		settingsPanel.add(spinPQ);
		
		spinBQ.setBounds(230, 275, 75, 25);
		settingsPanel.add(spinBQ);
		
		menuList.setBounds(680, 140, 100, 25);
		settingsPanel.add(menuList);
		
		spinSpeed.setBounds(670, 370, 75, 25);
		settingsPanel.add(spinSpeed);
		
		File dir = new File("assets/resource/settings/");
		File file = new File("assets/resource/settings/settings.txt");
		
		if(!dir.exists()) dir.mkdirs();
		if(!file.exists()) try {file.createNewFile();} catch (IOException e1) {e1.printStackTrace();}
		
		Scanner out = null;
		byte i = 0;
		
		try {
			out = new Scanner(file);
		} catch (Exception e) {}
		
		breakPoint: while(out.hasNextLine()) {
			switch(i) {
			case 0: spinSX.setValue(Integer.parseInt(out.nextLine())); break;
			case 1: spinSY.setValue(Integer.parseInt(out.nextLine())); break;
			case 2: spinPQ.setValue(out.nextLine()); break;
			case 3: spinBQ.setValue(out.nextLine()); break;
			case 4: rbset.setSelected(out.nextLine().equals("true") ? true : false); break;
			case 5: rb2set.setSelected(out.nextLine().equals("true") ? true : false); break;
			case 6: menuList.setSelectedItem(out.nextLine()); break;
			case 7: rb3set.setSelected(out.nextLine().equals("true") ? true : false); break;
			case 8: spinSpeed.setValue(Integer.parseInt(out.nextLine())); break;
			default: break breakPoint;
			}
			i++;
		}
		
		out.close();
		
		JButton saveButton = new JButton("Сохранить");
		saveButton.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				try { 
					
					FileStringWriter settings = new FileStringWriter("assets/resource/settings", "settings.txt", 
							spinSX.getValue()+"\n"+
						    spinSY.getValue()+"\n"+
						    spinPQ.getValue()+"\n"+
						    spinBQ.getValue()+"\n"+
						    rbset.isSelected()+"\n"+
						    rb2set.isSelected()+"\n"+
						    menuList.getSelectedItem().toString()+"\n"+
						    rb3set.isSelected()+"\n"+
						    spinSpeed.getValue());
					settings.close(0);
					
					Main.logger.info("Сохранение настроек");
				} catch (Exception ex) {Main.logger.info("Ошибка при сохранении настроек");}
			}
		});
		saveButton.setFocusable(false);
		saveButton.setBackground(Color.LIGHT_GRAY);
		saveButton.setForeground(Color.DARK_GRAY);
		saveButton.setBounds(570, 601, 150, 50);
		settingsPanel.add(saveButton);
		
		masterPanel.setBounds(0, 0, 834, 661);
		masterPanel.setLayout(null);
		
		JButton returnButton3 = new JButton("Назад");
		returnButton3.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				masterPanel.stopPainting();
				masterPanel.setVisible(false);
				mainPanel.setVisible(true);
				Main.logger.info("Панель настроек закрыта");
			}
		});
		returnButton3.setFocusable(false);
		returnButton3.setBackground(Color.LIGHT_GRAY);
		returnButton3.setBounds(724, 601, 100, 50);
		
		masterPanel.add(returnButton3);
		masterPanel.setFocusable(false);
		getContentPane().add(masterPanel);
		masterPanel.setVisible(false);
		
		levelPanel.setBounds(0, 0, 834, 661);
		levelPanel.setLayout(null);
		
		JButton returnButton4 = new JButton("Назад");
		returnButton4.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				levelPanel.setVisible(false);
				mainPanel.setVisible(true);
				Main.logger.info("Панель настроек закрыта");
			}
		});
		returnButton4.setFocusable(false);
		returnButton4.setBounds(724, 601, 100, 50);
		returnButton4.setBackground(Color.LIGHT_GRAY);
		levelPanel.add(returnButton4);
		
		levelPanel.setFocusable(false);
		getContentPane().add(levelPanel);
		levelPanel.setVisible(false);
		
		actionPanel.setLayout(null);
		actionPanel.setBounds(0, 0, 834, 661);
		actionPanel.setFocusable(false);
		getContentPane().add(actionPanel);
		actionPanel.setVisible(false);
	
		activePanel.setBounds(0, 0, 670, 550);
		actionPanel.setFocusable(false);
		activePanel.setLayout(null);
		actionPanel.add(activePanel);
		
		gamePanel = new GamePanel(actionPanel, mainPanel, true);
		gamePanel.setFocusable(true);
		activePanel.add(gamePanel);
		
		//                                        \\
		//        СЛУШАТЕЛИ ГЛАВНЫХ КНОПОК        \\
		//                                        \\
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sizeX = ((int)spinSX.getValue());
				sizeY = ((int)spinSY.getValue());
				playerQuantity = getSpinnerValue(spinPQ);
				botQuantity = getSpinnerValue(spinBQ);
				
				if(playerQuantity+botQuantity > 12 || playerQuantity+botQuantity < 2) {
					playerQuantity = (byte)(Math.random()*5+2);
					botQuantity = (byte)(Math.random()*5+2);
				}
				
				users = UserPoolExecutor.randomize(playerQuantity, botQuantity);
				UserPoolExecutor pool = new UserPoolExecutor(null, false);
				
				for(byte i = 0; i < users.getPool().size(); i++) {
					pool.getPool().add(users.getPool().get(i));
				}
				
				map = LO.generate((byte)sizeX, (byte)sizeY, pool);
				
				if(!menuList.getSelectedItem().equals("Генерация")) {
					try {
						Object[] obj = IOStreamLogic.outputStream(new Scanner(new File("assets/resource/worlds/"+menuList.getSelectedItem().toString())));
						
						map = ((AbstractCell[][])obj[0]);
						users = ((UserPoolExecutor)obj[1]);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				start(rbset.isSelected(), rb2set.isSelected(), playerCount() == 0 ? Byte.parseByte(spinSpeed.getValue().toString()) : (byte)50);
		}});
		settingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.setVisible(false);
				settingsPanel.setVisible(true);
				Main.logger.info("Открыто меню настроек");
		}});
		levelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.setVisible(false);
				levelPanel.setVisible(true);
				Main.logger.info("Открыто меню уровней");
		}});
		masterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.setVisible(false);
				masterPanel.setVisible(true);
				masterPanel.setColoredCell(rb3set.isSelected());
				masterPanel.startPainting();
				Main.logger.info("Открыта мастерская");
		}});
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.logger.info("Выход осуществлён");
				Main.logger.close(0);
				System.exit(0);
		}});
		
		//Видимость
		setVisible(true);
	}
	
	/**
	 * <b>Метод оформления надписи</b>
	 * @param lable - <i>Надпись</i>
	 * @param parent - <i>Родительский swing компонент</i>
	 * @param x - <i>Положение по x</i>
	 * @param y - <i>Положение п y</i>
	 * @param sizeX - <i>Размер по x</i>
	 * @param sizeY - <i>Размер по y</i>
	 * @param textSize - <i>Размер текста</i>
	 */
	private static void createLable(JLabel lable, JPanel parent, int x, int y, int sizeX, int sizeY, int textSize) {
		lable.setFont(new Font("Akshar", Font.BOLD, textSize));
		lable.setBounds(x, y, sizeX, sizeY);
		parent.add(lable);
	}
	
	/**
	 * <b>Метод создания переключаемой кнопки</b>
	 * @param rb - <i>Переключаемая кнопка</i>
	 * @param parent - <i>Родительский swing компонент</i>
	 * @param back - <i>Цвет заднего фона</i>
	 * @param selected - <i>Значение по умолчанию</i>
	 * @param x - <i>Положение по x</i>
	 * @param y - <i>Положение п y</i>
	 * @param sizeX - <i>Размер по x</i>
	 * @param sizeY - <i>Размер по y</i>
	 */
	private static void createRadioButton(JRadioButton rb, JPanel parent, Color back, boolean selected, int x, int y, int sizeX, int sizeY) { 
		rb.setBounds(x, y, sizeX, sizeY);
		rb.setBackground(back);
		rb.setSelected(selected);
		parent.add(rb);
	}
	
	/**
	 * <b>Метод получения значения из {@link JSpinner}</b>
	 * @param spin - <i>Данный компонент</i>
	 * @return Значение данного компонента
	 */
	private static byte getSpinnerValue(JSpinner spin) {
		return switch(((String)spin.getValue())) 
		{
		case "Рандом "-> (byte)(Math.random()*5+2);
		default -> Byte.parseByte(((String)spin.getValue()));
		};
	}
	
	/**
	 * <b>Подсчёт количества игроков</b>
	 * @return количество игроков в обработчике {@link Window#users}
	 */
	public byte playerCount() {
		byte q = 0;
		
		for(byte i = 0; i < users.getPool().size(); i++) {
			if(users.get(i) instanceof Player) {
				q++;
			}
		}
		
		return q;
	}
	
	/**
	 * <b>Метод запуска игры</b>
	 * @param def - <i>Проверка на уничтожение</i>
	 * @param expl - <i>Проверка на взрыв</i>
	 * @param speed - <i>Скорость игры (Работает только если в игре все пользователи - боты)</i>
	 */
	public void start(boolean def, boolean expl, byte speed) {
		defeat = def;
		gamePanel.setExplosion(expl);
		
		mainPanel.setVisible(false);
		levelPanel.setVisible(false);
		l.setMap(map);
		l.setUsersPool(users);
		l.setDefeat(defeat);
		l.setSpeed(speed);
		l.startGame();
		l.start();
		gamePanel.startPainting();
		actionPanel.setVisible(true);
	}
	
	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = 3884050913929122330L;
}
	
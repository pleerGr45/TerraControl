package graphics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import dop.gif.Gif;
import logic.cells.AbstractCell;
import logic.cells.ActiveCell;
import logic.cells.Cell;
import logic.cells.Color;
import logic.cells.Content;
import logic.process.LO;
import main.Main;

/**
 * <b>Класс панели - игрового поля</b>
 * @author vlad_matveev
 */
public class GamePanel extends JPanel {

	//                                 \\
	//           ПОЛЯ КЛАССА           \\
	//                                 \\
	/**
	 * <b>Панели игры</b>
	 */
	private JPanel parentPanel, mainPanel, interactPanel, playerstatPanel;
	/**
	 * <b>Кнопка пропуска хода игры</b>
	 */
	private JButton move, abort, restart, exit;
	/**
	 * <b>Фоновые изображения</b>
	 */
	private Image back, skull, radius_1, radius_2, radius_3;
	/**
	 * <b>Гиф-файл</b>
	 */
	private Gif moveGif, explosionGif;
	/**
	 * <b>Таймер перерисовки</b>
	 */
	private Timer timer;
	/**
	 * <b>Размеры для отрисовки</b>
	 */
	byte scale, size;
	/**
	 * <b>Сдвиг отрисовки</b>
	 */
	int shiftX, shiftY;
	/**
	 * <b>Тип хода</b>
	 * <br><b>true</b> - Ход передвижения юнита
	 * <br><b>false</b> - Обычный ход
	 */
	private boolean moveClick;
	/**
	 * <b>Передвигаемый юнит</b>
	 */
	private Content moveContent;
	/**
	 * <b>Клетка, из которой произошло передвижение юнита</b>
	 */
	private ActiveCell moveCell;
	/**
	 * <b>Текущий фильтр</b>
	 */
	private String filter;
	/**
	 * <b>Адаптер мыши</b>
	 */
	private MouseAdapter mouseAdapter;
	/**
	 * <b>Очередь отрисовки взрывов</b>
	 */
	private List<byte[]> explosionGifQueue;
	/**
	 * <b>Взрыв клетки</b>
	 */
	private boolean explosion;
	/**
	 * <b>Показатель сдвига в отрисовке массива пользователей</b>
	 */
	private byte userShift;
	/**
	 * <b>Изображения для фильтров</b>
	 */
	static final Image[][] FILTER_IMAGES = new Image[3][5];
	
	//                                \\
	//           SET-МЕТОДЫ           \\
	//                                \\
	public void setMoveClick(boolean moveClick) {this.moveClick = moveClick;}
	public void setExplosion(boolean explosion) {this.explosion = explosion;}
	public void setUserShift(byte userShift) {
		if(Window.l.getUsers().length == 12) {
			this.userShift = userShift == 0 || userShift == 1 ? userShift : 0;
		}
	}
	
	//                                \\
	//           GET-МЕТОДЫ           \\
	//                                \\
	public boolean isMoveClick() {return moveClick;}
	public Content getMoveContent() {return moveContent;}
	public Image getBack() {return back;}
	public JButton getMoveButton() {return move;}
	
	/**
	 * <b>Метод отрисовки</b>
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Если конец игры
		if(Window.l.isFinalized()) {
			drawEnd(g);
			return;
		};
		
		//Массив клеток
		AbstractCell[][] cells = Window.l.getCells();
		//Положение выбранной клетки
		byte sx = -1, sy = -1;
		//Размер изображения
		size = (byte)cells[0][0].getColor().getImage().getScaledInstance(scale, scale, 0).getWidth(null);
		
		//Задний фон
		g.drawImage(back, 0, 0, null);
		
		//Цикл отрисовки клеток с фильтром
		for(byte x = 0; x < cells.length; x++) {
			for(byte y = 0; y < cells[x].length; y++) {
				
				//Временные переменные, хранящие положение клетки на эране
				int fx = (y % 2 == 0 ? x*size : x*size+size/2)+shiftX, fy = (y*(size/4)*3)+shiftY;
				
				//Отрисовка клетки
				g.drawImage(cells[x][y].getColor().getImage(), fx, fy, scale, scale, null);
				
				//Отрисовка фильтров
				switch(filter) {
				case "income":
					if(!(cells[x][y] instanceof ActiveCell)) continue;
					if(Content.getContentType(((ActiveCell)cells[x][y]).getContent()) == 'm' || ((ActiveCell)cells[x][y]).getContent() == Content.BASE) g.drawImage(((ActiveCell)cells[x][y]).getContent().getImage(), fx, fy, scale, scale, null);
					else if(cells[x][y].getColor() != Color.NITRAL) g.drawImage(FILTER_IMAGES[1][((ActiveCell)cells[x][y]).getContent().getIncome() > 0 ? 0 : ((ActiveCell)cells[x][y]).getContent().getIncome() < -1 ? 1 : 2], fx, fy, scale, scale, null);
					else g.drawImage(FILTER_IMAGES[1][3], fx, fy, scale, scale, null);
				break;
				case "energy":
					if(!(cells[x][y] instanceof ActiveCell)) continue;
					if(Content.getContentType(((ActiveCell)cells[x][y]).getContent()) == 'd' || ((ActiveCell)cells[x][y]).getContent() == Content.BASE) g.drawImage(((ActiveCell)cells[x][y]).getContent().getImage(), fx, fy, scale, scale, null);
					else if(cells[x][y].getColor() != Color.NITRAL) g.drawImage(FILTER_IMAGES[0][((ActiveCell)cells[x][y]).getProtectionLevel()], fx, fy, scale, scale, null);
					else g.drawImage(FILTER_IMAGES[1][3], fx, fy, scale, scale, null);
				break;	
				default:
					if(cells[x][y] instanceof ActiveCell) g.drawImage(((ActiveCell)cells[x][y]).getContent().getImage(), fx, fy, scale, scale, null);
					if(cells[x][y].equals(Window.l.getSelectedCell())) {sx = x; sy = y;}
				break;
				}		
			}
		}
		
		//Отрисовка очереди взрывов
		if(explosion) for(short i = 0; i < explosionGifQueue.size(); i++) {
			g.drawImage(explosionGif.i(), (explosionGifQueue.get(i)[1] % 2 == 0 ? explosionGifQueue.get(i)[0]*size : explosionGifQueue.get(i)[0]*size+size/2)+shiftX+((scale/20)/2), (explosionGifQueue.get(i)[1]*(size/4)*3)+shiftY+((scale/20)/2), scale-(scale/20), scale-(scale/20), null);
			if((short)(explosionGif.getImageShift()) >= 9) {
				explosionGif.setImageShift(0);
				explosionGifQueue.remove(i);
			}
		}
		
		//Отрисовка очереди уничтожения
		List<Object[]> qeue = Window.l.getDestroyGifQueue();
		
		for(short i = 0; i < qeue.size(); i++) {
			Gif gif = ((Gif)qeue.get(i)[1]);
			byte[] coords = ((byte[])qeue.get(i)[0]);
			g.drawImage(gif.i(), (coords[1] % 2 == 0 ? coords[0]*size : coords[0]*size+size/2)+shiftX+((scale/20)/2), (coords[1]*(size/4)*3)+shiftY+((scale/20)/2), scale-(scale/20), scale-(scale/20), null);
			if((byte)(gif.getImageShift()) >= 13) {
				gif.setImageShift(0);
				qeue.remove(i);
			}
		}
		
		
		//Отрисовка радиуса
		if(filter.equals("s") && moveClick && moveCell.isActivity()) {g.drawImage(getRadiusBack(moveContent.getRadius()),  (sy % 2 == 0 ? sx*size : sx*size+size/2)-(890*(scale/20))+shiftX, (sy*(size/4)*3)-(890*(scale/20))+shiftY, 1800*(scale/20), 1800*(scale/20), null);}
		//Отрисовка выбранной клетки
		if(sx >= 0 || sy >= 0) g.drawImage(Color.SELECTED.getImage(), (sy % 2 == 0 ? sx*size : sx*size+size/2)+shiftX , (sy*(size/4)*3)+shiftY, scale, scale, null);
		
		//Прекрашение использования ресурсов компьютера за данный ход перерисовки
		g.dispose();
	}
	
	/**
	 * <b>Конструктор класса {@link GamePanel}</b>
	 * @param parent - <i>Родительская панель</i>
	 * @param main - <i>Главная панель</i>
	 * @param explosion - <i>Графическая настройка взрывов</i>
	 */
	public GamePanel(JPanel parent, JPanel main, boolean explosion) {
		
		//Первоначальная настройкаa
		setBounds(0, 0, 2550, 2100);
		
		//Начальные значения
		this.parentPanel = parent;
		this.mainPanel = main;
		this.explosion = explosion;
		moveClick = false;
		scale = 20;
		filter = "s";
		explosionGifQueue = new ArrayList<>();
		
		//Гиф файлы
		moveGif = new Gif(0.05, 0, "assets/images/attributes/icons/move_icon/", 4, "png");
		explosionGif = new Gif(0.2, 0, "assets/images/attributes/icons/explosion/", 10, "png");
		
		//Изображения
		back = GraphicsPack.imageLoader("assets/images/backgrounds/game_back.png");
		skull = GraphicsPack.imageLoader("assets/images/attributes/icons/skull.png");
		radius_1 = GraphicsPack.imageLoader("assets/images/attributes/grounds/radius_1.png");
		radius_2 = GraphicsPack.imageLoader("assets/images/attributes/grounds/radius_2.png");
		radius_3 = GraphicsPack.imageLoader("assets/images/attributes/grounds/radius_3.png");
		
		//Настройка фильтров
		for(byte i = 0; i < 5; i++) FILTER_IMAGES[0][i] = GraphicsPack.imageLoader("assets/images/cells/energy/"+i+".png");
		for(byte i = 0; i < 4; i++) FILTER_IMAGES[1][i] = GraphicsPack.imageLoader("assets/images/cells/income/"+i+".png");
		
		//Настройка интерактивной панели
		interactPanel = new JPanel();
		interactPanel.setBounds(0, 550, 850, 150);
		interactPanel.setBackground(java.awt.Color.GRAY);
		interactPanel.setFocusable(false);
		interactPanel.setLayout(null);
		parentPanel.add(interactPanel);
		
		//Объявление и создание кнопок ввода
		JButton sellB = new JButton("Продать"), 
				def1B = new JButton("Защита I (10)"), 
				def2B = new JButton("Защита II (25)"), 
				at1B = new JButton("Атака I (10)"), 
				at2B = new JButton("Атака II (20)"), 
				at3B = new JButton("Атака III (30)"), 
				at4B = new JButton("Атака IV (40)"), 
				mrtB = new JButton("Артиллерия (50)"), 
				min1B = new JButton("Шахта I (12)"), 
				min2B = new JButton("Шахта II (20)"), 
				min3B = new JButton("Шахта III (28)");
		
		//Настройка кнопок ввода
		createContentButton(def1B, interactPanel, Content.DEFENS_I,   new ImageIcon("assets/images/cells/content/defens_1.png"),           10, 10, 130, 30,  (byte)1);
		createContentButton(def2B, interactPanel, Content.DEFENS_II,  new ImageIcon("assets/images/cells/content/defens_2.png"),           10, 50, 130, 30,  (byte)1);
		createContentButton(at1B,  interactPanel, Content.ATTACK_I,   new ImageIcon("assets/images/cells/content/attack_1.png"),           150, 10, 130, 30, (byte)0);
		createContentButton(at2B,  interactPanel, Content.ATTACK_II,  new ImageIcon("assets/images/cells/content/attack_2.png"),           150, 50, 130, 30, (byte)0);
		createContentButton(at3B,  interactPanel, Content.ATTACK_III, new ImageIcon("assets/images/cells/content/attack_3.png"),           290, 10, 130, 30, (byte)0);
		createContentButton(at4B,  interactPanel, Content.ATTACK_IV,  new ImageIcon("assets/images/cells/content/attack_4.png"),           290, 50, 130, 30, (byte)0);
		createContentButton(mrtB,  interactPanel, Content.MORTAR,     new ImageIcon("assets/images/cells/content/mortar.png"),             440, 10, 140, 30, (byte)0);
		createContentButton(min1B, interactPanel, Content.MINE_I,     new ImageIcon("assets/images/cells/content/mine_1.png"),             440, 50, 130, 30, (byte)1);
		createContentButton(min2B, interactPanel, Content.MINE_II,    new ImageIcon("assets/images/cells/content/mine_2.png"),             590, 10, 130, 30, (byte)1);
		createContentButton(min3B, interactPanel, Content.MINE_III,   new ImageIcon("assets/images/cells/content/mine_3.png"),             590, 50, 130, 30, (byte)1);
		createContentButton(sellB, interactPanel, Content.VOID,       new ImageIcon("assets/images/attributes/icons/sellButton_icon.png"), 730, 50, 100, 30, (byte)2);
		
		//Настройка кнопки хода
		move = new JButton("Ход");
		move.setBackground(java.awt.Color.DARK_GRAY);
		move.setForeground(java.awt.Color.BLACK);
		move.setBounds(730, 10, 100, 30);
		move.setFocusable(false);
		move.addActionListener(a -> {
			moveClick = false;
			moveContent = null;
			moveCell = null;
			Window.l.start();
		});
		interactPanel.add(move);
		
		//Настройка кнопки перезапуска
		restart = new JButton("Играть");
		restart.setBackground(java.awt.Color.DARK_GRAY);
		restart.setForeground(java.awt.Color.BLACK);
		restart.setFont(new Font("Tahoma", Font.BOLD, 15));
		restart.setBounds(90, 400, 150, 50);
		restart.setFocusable(false);
		restart.addActionListener(a -> {
			moveClick = false;
			moveCell = null;
			moveContent = null;
			filter = "s";
			Window.l.startGame();
			Window.l.start();
			this.remove(restart);
			this.remove(exit);
		});
		
		//Настройка кнопки покидания игры
		abort = new JButton("Прервать");
		abort.setBackground(java.awt.Color.DARK_GRAY);
		abort.setForeground(java.awt.Color.BLACK);
		abort.setFont(new Font("Tahoma", Font.BOLD, 15));
		abort.setBounds(700, 80, 130, 30);
		abort.setFocusable(false);
		abort.addActionListener(a -> {
			moveClick = false;
			moveCell = null;
			moveContent = null;
			filter = "s";
			Window.l.stop();
			parentPanel.setVisible(false);
			mainPanel.setVisible(true);
			
		});
		interactPanel.add(abort);
		
		//Настройка кнопки выхода в главное меню
		exit = new JButton("Меню");
		exit.setBackground(java.awt.Color.DARK_GRAY);
		exit.setForeground(java.awt.Color.BLACK);
		exit.setFont(new Font("Tahoma", Font.BOLD, 15));
		exit.setBounds(300, 400, 150, 50);
		exit.setFocusable(false);
		exit.addActionListener(a -> {
			moveClick = false;
			moveCell = null;
			moveContent = null;
			filter = "s";
			Window.l.stop();
			this.remove(restart);
			this.remove(exit);
			parentPanel.setVisible(false);
			mainPanel.setVisible(true);
			
		});
		
		//Настройка кнопок фильтров
		JButton filtStandard = new JButton(), filtEnergy = new JButton(), filtIncome = new JButton();
		createFilterButton(filtStandard, interactPanel, this, new ImageIcon("assets/images/attributes/filter/standard.png"), 10, 80, 30, 30, "s"     );
		createFilterButton(filtEnergy,   interactPanel, this, new ImageIcon("assets/images/attributes/filter/energy.png"),   40, 80, 30, 30, "energy");
		createFilterButton(filtIncome,   interactPanel, this, new ImageIcon("assets/images/attributes/filter/income.png"),   70, 80, 30, 30, "income");
		
		//Создание и настройка панели отображения статистики игроков
		playerstatPanel = new JPanel() {
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				for(byte i = 0; i < Window.l.getUsers().length-userShift; i++) {
					
					byte p = (byte) (i + userShift);
					
					g.setFont(new Font("", Font.BOLD, 16));
					g.setColor(Color.parseJavaColor(Window.l.getUsers()[p].getColor()));
					g.fillRect(1, i*50+1, 161, 50);
					g.setColor(java.awt.Color.BLACK);
					g.drawRect(1, i*50+1, 161, 50);
					if(Window.l.getUsers()[p].equals(Window.l.getCurrentPlayer())) {
						g.drawImage(moveGif.i(), 120, i*50+15, 28, 28, null);
					}
					g.drawString(Window.l.getUsers()[p].getName(), 5, i*50+20);
					g.fillRect(4, i*50+26, 10*((Window.l.getUsers()[p].getCoins()+"").length()+1), 22);
					g.setColor(new java.awt.Color(255, 215, 0));
					g.setFont(new Font("", Font.BOLD, 10));
					g.drawString(Window.l.getUsers()[p].getCoins()+"⏣", 5, i*50+35);
					g.setColor(Window.l.getUsers()[p].getIncome() > 0 ? java.awt.Color.GREEN : java.awt.Color.RED);
					g.drawString(Window.l.getUsers()[p].getIncome() > 0 ? (Window.l.getUsers()[p].getIncome()+"")+"▲" : (Window.l.getUsers()[p].getIncome()+"").replaceAll("-", "")+"▼", 6, i*50+45);
					if(!Window.l.getUsers()[p].isAlive()) {
						g.drawImage(skull, 135, i*50+3, 25, 25, null);
					}
				}
			}
			
			/**
			 * <b>Серийный номер</b>
			 */
			private static final long serialVersionUID = 3622483109731413785L;
		};
		playerstatPanel.setBackground(java.awt.Color.DARK_GRAY);
		playerstatPanel.setBounds(670, 0, 180, 550);
		playerstatPanel.setFocusable(false);
		playerstatPanel.setLayout(null);
		parentPanel.add(playerstatPanel);
		
		//Атаптер мыше
		mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				
				if(Window.l.getCurrentPlayer() == null) return;
				
				//полежение курсора на эране переводится в положение клетки в масиве
				int y = (e.getY()-shiftY)/((size/4)*3);
				int x = y % 2 == 0 ? (e.getX()-shiftX)/size : ((e.getX()-shiftX)-(size/2))/size;
				
				//Проверка на ошибки
				if(x < 0 || x >= Window.l.getCells().length || y < 0 || y >= Window.l.getCells()[x].length || !(Window.l.getCells()[x][y] instanceof ActiveCell)) return;

				//Получение данной клетки
				ActiveCell cell = (ActiveCell)Window.l.getCells()[x][y];
				
				//Пометка выбранной клетки
				Window.l.setSelectedCell(cell);
				
				//Проверка на тип хода
				if(moveClick) {
					if(!(cell.getColor() == moveCell.getColor() && cell.getContent() == moveContent && moveContent == Content.ATTACK_IV)) {
					//проверки на соответствие с правилами
					if(cell instanceof ActiveCell
					&& (LO.cellAvailableToOwner(cell, Window.l.getCells(), Window.l.getCurrentPlayer().getColor()) 
					|| moveContent == Content.MORTAR)
					&& LO.cellContainsInCellRadius(moveCell, cell, Window.l.getCells(), moveContent.getRadius())
					&& moveCell.isActivity()
					&& !cell.equals(moveCell)) {
						//Если переходный контент = Content.MORTAR
						if(moveContent == Content.MORTAR) {
							if((moveCell.getProtectionLevel()+moveContent.getAttacktLevel()) > cell.getContent().getProtectionLevel() 
									&& cell.getColor() != moveCell.getColor() 
									&& cell.getContent() != Content.BASE) {
								Window.l.setSelectedCell(moveCell);
								byte[] c = {LO.getCellCoords(cell, Window.l.getCells())[0], LO.getCellCoords(cell, Window.l.getCells())[1]};
								explosionGifQueue.add(c);
								cell.setContent(Content.VOID);
								moveCell.setActivity(false);
							}
						//Иначе
						} else if(((Cell)cell).capture(Window.l.getCurrentPlayer(), moveContent)) {
							if(cell.getContent() == moveContent) 
								cell.setContent(Content.getAttackContentByPower((byte)(cell.getContent().getAttacktLevel()+1)));
							else ((Cell)cell).setContent(moveContent);
								 moveCell.setContent(Content.VOID);
								 ((Cell)cell).setActivity(false);
						}
					}
					}
					//Завершение хода
					moveClick = false;
					moveContent = null;
					moveCell = null;
					Window.l.recalculateProtectionLevel();
				} else {
					//проверки на соответствие с правилами
					if(cell.getColor() == Window.l.getCurrentPlayer().getColor()) {
						if(Content.getContentType(cell.getContent()) == 'a') {
							moveClick = true; 
							moveContent = ((ActiveCell)cell).getContent();
							moveCell = (ActiveCell)cell;
						}
					}
				}
			}
		};
		
		//Добавление адапрера мыши
		addMouseListener(mouseAdapter);
		
		//Создание таймера перерисовки
		timer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				parent.repaint();
				playerstatPanel.repaint();
			}
		});
		
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
	 * <b>Метод получения изображения радиуса</b>
	 * @param radius - <i>Радиус</i>
	 * @return Изображение радиуса
	 */
	private Image getRadiusBack(byte radius) {
		return switch (radius) {
		case 1 -> radius_1; 
		case 2 -> radius_2;
		case 3 -> radius_3;
		default -> {Main.logger.error("Возникновение несуществующего радиуса: graphics.GamePanel#getRadiusBack()"); yield null;}
		};
	}
	
	/**
	 * <b>Метод настройки кнопки фильтра</b>
	 * @param b - <i>Кнопка</i>
	 * @param parent - <i>Родительская панель</i>
	 * @param object - <i>Данный объект</i>
	 * @param i - <i>Изображение на кнопке</i>
	 * @param x - <i>Положение по x</i>
	 * @param y - <i>Положение по y</i>
	 * @param sizeX - <i>Размер по x</i>
	 * @param sizeY - <i>Размер по y</i>
	 * @param filter - <i>Тип устанавливаемого фильтра</i>
	 */
	private static void createFilterButton(JButton b, JPanel parent, GamePanel object, Icon i, int x, int y, int sizeX, int sizeY, String filter) {
		b.setBounds(x, y, sizeX, sizeY);
		b.setBackground(java.awt.Color.DARK_GRAY);
		b.setIcon(i);
		b.setFocusable(false);
		b.addActionListener(a -> {
			object.filter = filter;
		});		
		parent.add(b);
	}
	
	/**
	 * <b>Метод настройки кнопки содержимого</b>
	 * @param b - <i>Кнопка</i>
	 * @param parent - <i>Родительская панель</i>
	 * @param i - <i>Изображение на кнопке</i>
	 * @param x - <i>Положение по x</i>
	 * @param y - <i>Положение по y</i>
	 * @param sizeX - <i>Размер по x</i>
	 * @param sizeY - <i>Размер по y</i>
	 * @param type - <i>Тип кнопки</i>
	 */
	private static void createContentButton(JButton b, JPanel parent, Content content, Icon i, int x, int y, int sizeX, int sizeY, byte type) {
		b.setBounds(x, y, sizeX, sizeY);
		b.setIcon(i);
		b.setForeground(java.awt.Color.WHITE);
		b.setBackground(java.awt.Color.DARK_GRAY);
		b.setFont(new Font("", 0, 10));
		b.setHorizontalAlignment(SwingConstants.LEFT);
		b.setFocusable(false);
		switch(type) {
		case 0: b.addActionListener((a) -> {Window.l.addAttackContent(content, Window.l.getCurrentPlayer(), Window.l.getSelectedCell());}); break;
		case 1: b.addActionListener((a) -> {Window.l.addBuildingContent(content, Window.l.getCurrentPlayer(), Window.l.getSelectedCell());}); break;
		case 2: b.addActionListener((a) -> {if(Window.l.getSelectedCell() != null) Window.l.sell(((ActiveCell)Window.l.getSelectedCell()).getContent(), Window.l.getCurrentPlayer(), Window.l.getSelectedCell());}); break;
		}
		parent.add(b);
	}
	
	/**
	 * <b>Метод отрисовки конца игры</b>
	 * @param g - <i>Графический компонент</i>
	 */
	private void drawEnd(Graphics g) {

		g.drawImage(back, 0, 0, null);
		removeMouseListener(mouseAdapter);
		stopPainting();
		for(byte i = 0; i < Window.l.getUsers().length; i++) {
			if(Window.l.getUsers()[i].isAlive()) {
				//Голова
				g.setColor(java.awt.Color.BLACK);
				g.setFont(new Font("Bahnschrift SemiBold" , Font.BOLD, 48));
				g.drawString("Победил", 100, 60);
				g.setColor(Color.parseJavaColor(Window.l.getUsers()[i].getColor()));
				g.fillRect(325, 20, 185, 50);
				g.setColor(java.awt.Color.BLACK);
				g.setFont(new Font("Bahnschrift SemiBold" , Font.BOLD, 25));
				g.drawString(Window.l.getUsers()[i].getName(), 335, 50);
				//Статистика
				g.setFont(new Font("Bahnschrift SemiBold" , Font.BOLD, 23));
				g.drawString("массивность:", 90, 120);
				g.drawString("общая ценность:", 90, 145);
				g.drawString("общая защищённость:", 90, 170);
				g.drawString("клеток захвачено:", 90, 195);
				g.drawString("юнитов построено:", 90, 220);
				g.drawString("зданий построено:", 90, 245);
				g.drawString("зданий продано:", 90, 270);
				g.drawString("ходов сделано:", 90, 295);
				g.fillRect(400, 100, 50, 22);
				g.setColor(Color.parseJavaColor(Window.l.getUsers()[i].getColor()));
				g.drawString((int)((((double)(LO.countWeight(Window.l.getCells(), Window.l.getUsers()[i].getColor()))/(Window.l.getCells().length*Window.l.getCells()[0].length))*100))+"%", 400, 120);
				g.setColor(java.awt.Color.YELLOW);
				g.drawString(LO.getGlobalValue(Window.l.getCells(), Window.l.getUsers()[i].getColor())+"", 400, 145);
				g.setColor(java.awt.Color.BLACK);
				g.drawString("|", 500, 145);
				g.setColor(java.awt.Color.ORANGE);
				g.drawString((Window.l.getUsers()[i].getIncome())+"", 525, 145);
				g.setColor(java.awt.Color.DARK_GRAY);
				g.drawString(LO.getGlobalProtection(Window.l.getCells(), Window.l.getUsers()[i].getColor())+"", 400, 170);
				g.drawString(Window.l.getUsers()[i].getStats((byte)0)+"", 400, 195);
				g.drawString(Window.l.getUsers()[i].getStats((byte)1)+"", 400, 220);
				g.drawString(Window.l.getUsers()[i].getStats((byte)2)+"", 400, 245);
				g.drawString(Window.l.getUsers()[i].getStats((byte)3)+"", 400, 270);
				g.drawString(Window.l.getMoves()+"", 400, 295);
			}
		}
		
		this.add(restart);
		this.add(exit);
		startPainting();
		addMouseListener(mouseAdapter);
	}
	
	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = 1L;
}

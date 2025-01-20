package logic.process;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import dop.gif.Gif;
import logic.cells.AbstractCell;
import logic.cells.ActiveCell;
import logic.cells.Cell;
import logic.cells.Color;
import logic.cells.Content;
import logic.cells.VoidCell;
import logic.user.Bot;
import logic.user.Player;
import logic.user.User;
import logic.user.UserPoolExecutor;

/**
 * <b>Класс логики игры</b>
 * @author vlad_matveev
 */
public class Logic {

	//                                 \\
	//           ПОЛЯ КЛАССА           \\
	//                                 \\
	/**
	 * <b>Карта игры</b>
	 */
	private AbstractCell[][] map;
	/**
	 * <b>Начальные игроки</b>
	 */
	private UserPoolExecutor usersPool;
	/**
	 * <b>Массив клеток</b>
	 * @see AbstractCell
	 */
	private AbstractCell[][] cells;
	/**
	 * <b>Анимация смерти клетки</b>
	 */
	private boolean defeat;
	/**
	 * <b>Массив пользователей</b>
	 * @see User
	 * @see Player
	 * @see Bot
	 */
	private User[] users;
	/**
	 * <b>Текущий игрок</b>
	 */
	private Player player;
	/**
	 * <b>Выбранная клетка</b>
	 */
	private AbstractCell selectedCell;
	/**
	 * <b>Таймер игры</b>
	 */
	private Timer gameTimer;
	/**
	 * <b>Сдвиг в массиве игроков</b>
	 */
	private byte shift;
	/**
	 * <b>Заверщённость игры</b>
	 */
	private boolean finalized;
	/**
	 * <b>Количество ходов</b>
	 */
	private int moves;
	/**
	 * <b>Общее состояние игры</b>
	 * <pre>
	 * <b>0</b> - <i>Начало игры</i>
	 * <b>1</b> - <i>Основная игра</i>
	 */
	private byte state;
	/**
	 * <b>Скорость логического процесса</b>
	 */
	private byte speed;
	/**
	 * <b>Очередь уничтожений</b>
	 */
	private List<Object[]> destroyGifQueue;
	
	//                                \\
	//           GET-МЕТОДЫ           \\
	//                                \\
	public boolean isFinalized() {return finalized;}
	public AbstractCell[][] getCells() {return cells;}
	public User[] getUsers() {return users;}
	public Player getCurrentPlayer() {return player;}
	public AbstractCell getSelectedCell() {return selectedCell;}
	public int getMoves() {return moves;}
	public byte getState() {return state;}
	public List<Object[]> getDestroyGifQueue() {return destroyGifQueue;}
	
	//                                \\
	//           SET-МЕТОДЫ           \\
	//                                \\
	public void setMap(AbstractCell[][] map) {this.map = map;}
	public void setUsersPool(UserPoolExecutor users) {this.usersPool = users;}
	public void setDefeat(boolean defeat) {this.defeat = defeat;}
	public void setSpeed(byte speed) {this.speed = speed; this.gameTimer.setDelay(speed);}
	public void setSelectedCell(AbstractCell cell) {
		if(!(cell instanceof VoidCell))
		this.selectedCell = cell;
	}
	public void setSelectedCell(byte x, byte y) {
		if(selectedCell == null) return;
		byte[] cords = LO.getCellCoords(selectedCell, cells);
		if((cords[0]+x >= 0 && cords[0]+x < cells.length && cords[1]+y >= 0 && cords[1]+y < cells[0].length) && cells[cords[0]+x][cords[1]+y] instanceof ActiveCell) this.selectedCell = cells[cords[0]+x][cords[1]+y];
	}
	
	/**
	 * <b>Конструктор класса {@link Logic}</b>
	 * @param map - <i>Карта</i>
	 * @param users - <i>Массив пользователей</i>
	 */
	public Logic(AbstractCell[][] map, UserPoolExecutor users, boolean defeat, byte speed) {
		
		this.map = map;
		this.usersPool = users;
		this.defeat = defeat;
		this.speed = speed;
		
		startGame();
		
		recalculateAlivePlayers(this.defeat);
		
		//Таймер игры
		//-----------------------------------------------------
		gameTimer = new Timer(this.speed, e -> {
			recalculateAttackContent(this.users[shift]);
			recalculateProtectionLevel();
			//Если ползователь жив, предоставить ему ход
			if(this.users[shift].isAlive()) {
				//Если игрок, то сделать его текущим
				if(this.users[shift] instanceof Player) {player = (Player)this.users[shift];}
				//Пересчёт живых пользователей
				recalculateAlivePlayers(this.defeat);
				//Проверка на завершенность
				if(getAliveUsers() <= 1) {finalized = true; stop();}
				//Ход пользователя
				this.users[shift].doMove();
			}
			//Пересчёты
			recalculateCoins();
			setNewMove(this.users[shift]);
			recalculateAttackContent(this.users[shift]);
			recalculateProtectionLevel();
			recalculateSuppliedCells();
			recalculateAlivePlayers(this.defeat);
			//Проверка на завершенность
			if(getAliveUsers() <= 1) {finalized = true; stop();}
			//Смена сдвига
			shift = (byte)((shift+1 < this.users.length) ? shift+1 : 0);
			//Пересчёты за ход
			if(shift == this.users.length-1) {
				//Пересчёт хода
				moves++;
				//Смена состояния игры
				switch (moves) {
				case 0  : state = 0; break;
				case 50 : state = 1; break;
				}
			}
		});
		//-----------------------------------------------------
	}
	
	/**
	 * <b>Запуск главного прцесса игры</b>
	 */
	public void start() {gameTimer.start();}
	
	/**
	 * <b>Остановка главного прцесса игры</b>
	 */
	public void stop() {gameTimer.stop();}
	
	/**
	 * <b>Метод постановки стандартных значений</b>
	 */
	public void startGame() {
		//Начальная настройка
		finalized = false;
		destroyGifQueue = new ArrayList<>();
		moves = 0;
		
		//Пустой обработчик пользователь
		UserPoolExecutor pool = null;
		
		//Проверка на корректность полученных данных о пользователях
		if(usersPool == null) {
			pool = UserPoolExecutor.randomize((byte)(Math.random()), (byte)(Math.random()*4+2));
			this.users = new User[pool.getPool().size()];
			for(byte i = 0; i < this.users.length; i++) {this.users[i] = pool.getPool().get(i);}
		} else {
			this.users = new User[usersPool.getPool().size()];
			for(byte i = 0; i < usersPool.getPool().size(); i++) {
				this.users[i] = usersPool.get(i);
				this.users[i].nulling();
			}
		}
				
		//Переопределения пользователей из обработчика в массив
		for(byte i = 0; i < this.users.length; i++) {
			if(this.users[i] instanceof Player) {
				player = (Player)this.users[i];
				break;
			}
		}
		
		/**
		 * <b>Пороверка/генерация карты</b>
		 */
		this.cells = map != null ? cloneMap(map) : LO.generate((byte)(Math.random()*23+7), (byte)(Math.random()*23+7), pool);
		
		//Начальные настройки игры
		shift = 0;
		state = 0;
		recalculateProtectionLevel();
		
	}
	
	/**
	 * <b>Метод создания содержимого типа постройки</b>
	 * @param contentType - <i>Содержимое</i>
	 * @param user - <i>Пользователь</i>
	 * @param cell - <i>Клетка</i>
	 */
	public boolean addBuildingContent(Content contentType, User user, AbstractCell cell) {
		//Проверки на соответствие с правилами игры и на внутренние ошибки
		if(user == null || cell == null || cell.getColor() != user.getColor() || !(cell instanceof ActiveCell)) {return false;}
		
		//Процесс добавления
		if(user.getCoins() >= -contentType.getCost() && ((ActiveCell)cell).getContent() == Content.VOID) {
			user.addCoins(contentType.getCost(), false);
			user.addIncome(contentType.getIncome());
			((ActiveCell)cell).setContent(contentType);
			recalculateProtectionLevel();
			user.addStats((byte)2);
			return true;
		}
		return false;
	}
	
	/**
	 * <b>Метод создания содержимого типа атаки</b>
	 * @param contentType - <i>Содержимое</i>
	 * @param user - <i>Пользователь</i>
	 * @param cell - <i>Клетка</i>
	 */
	public boolean addAttackContent(Content contentType, User user, AbstractCell cell) {
		//Проверки на соответствие с правилами игры и на внутренние ошибки
		if(cell == null ||
		   user == null ||
		   !LO.cellAvailableToOwner(cell, cells, user.getColor()) || 
		   !(cell instanceof ActiveCell) || 
			(((ActiveCell)cell).getContent() == null)) {return false;}
		
		//Процесс добавления
		if(user.getCoins() > -contentType.getCost() && (((ActiveCell)cell).getContent() == Content.VOID || user.getColor() != ((ActiveCell)cell).getColor())) {
			boolean notActivity = ((Cell)cell).capture(user, contentType);
		
			if(notActivity) {
				user.addCoins(contentType.getCost(), false);
				user.addIncome(contentType.getIncome());
				((ActiveCell)cell).setContent(contentType); 
				((ActiveCell)cell).setActivity(notActivity ? false : true);
				user.setIncome(LO.countIncome(cells, user.getColor()));
				recalculateProtectionLevel();
				user.addStats((byte)1);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * <b>Метод продажи содержимого типа постройки</b>
	 * @param contentType - <i>Содержимое</i>
	 * @param user - <i>Пользователь</i>
	 * @param cell - <i>Клетка</i>
	 */
	public void sell(Content contentType, User user, AbstractCell cell) {
		//Проверки на соответствие с правилами игры и на внутренние ошибки
		if(cell != null && user != null && user.getColor() == cell.getColor()) {
			//Процесс продажи
			if((Content.getContentType(contentType) != 'a' || contentType == Content.MORTAR) && Content.getContentType(contentType) != 'n') {
				user.addIncome(-contentType.getIncome());
				((ActiveCell)cell).setContent(Content.VOID);
				user.addStats((byte)3);
			}
		}
	}
	
	/**
	 * <b>Метод установления нового ход</b>
	 * <br>(Активизация клеток)
	 * @param user - <i>Поьзователь</i>
	 */
	public void setNewMove(User user) {
		//Для каждой клетки массива
		for(byte i = 0; i < cells.length; i++) {
			for(byte j = 0; j < cells[i].length; j++) {
				//Проверки на соответствие с правилами игры и на внутренние ошибки
				if(cells[i][j] instanceof ActiveCell && cells[i][j].getColor() == user.getColor()) {
					//Установление активности (true)
					((ActiveCell)cells[i][j]).setActivity(true);
				}
			}
		}
	}
	
	/**
	 * <b>Метод получения количесва живых пользователей</b>
	 * @return количество живых пользователей
	 */
	private byte getAliveUsers() {
		//Количетво живых пользователей
		byte aliveUsers = 0;
		
		//Для каждого пльзователя
		for(byte i = 0; i < users.length; i++) {
			//Если пользователь жив aliveUsers++
			if(users[i].isAlive()) aliveUsers++;
		}
		
		//Возвращение результата
		return aliveUsers;
	}
	
	/**
	 * <b>Пересчёт уровня защиты</b>
	 */
	public void recalculateProtectionLevel() {
		//Для каждой клетки массива
		for(byte x = 0; x < cells.length; x++) {
			for(byte y = 0; y < cells[x].length; y++) {
				//Проверки на внутренние ошибки
				if(cells[x][y] instanceof ActiveCell) {
					//Установка уровня защиты
					((ActiveCell)cells[x][y]).setProtectionLevel((byte)(((ActiveCell)cells[x][y]).getContent().getProtectionLevel()+recalculateProtectionLevel(LO.cellNeighbors(cells, cells[x][y], false))));
				}
			}
		}
	}
	
	/**
	 * <b>Получения уровня защиты</b>
	 * @param list - <i>Коллекция клеток</i>
	 */
	private static byte recalculateProtectionLevel(List<ActiveCell> list) {
		//Уровень защиты данной клетки
		byte protectionLevel = 0;
		
		//Для каждой соседней клетки
		for(byte i = 0; i < list.size(); i++) {
			if(Content.getContentType(list.get(i).getContent()) == 'd' || list.get(i).getContent() == Content.BASE) {
				//Увеличение уровня защиты данной клетки на уровень защиты соседней
				protectionLevel += list.get(i).getContent().getProtectionLevel();
			}
		}
		
		//Возвращение результата
		return protectionLevel;
	}
	
	/**
	 * <b>Пересчёт снабжаемости клеток</b>
	 * <p><b>LCI</b> - <i>Лимит круговых итераций (Limit of Circular Iterations)</i>
	 */
	private void recalculateSuppliedCells() {
		
		final byte LCI = (byte)(4 * LO.findMore((byte)cells.length, (byte)cells[0].length));
		boolean[][] supCells = new boolean[cells.length][cells[0].length];
		
		//Пометка баз true
		for(byte x = 0; x < supCells.length; x++) {
			for(byte y = 0; y < supCells[x].length; y++) {
				if((cells[x][y] instanceof ActiveCell && ((ActiveCell)cells[x][y]).getContent() == Content.BASE) || cells[x][y] instanceof VoidCell) {
					supCells[x][y] = true;
				}
			}
		}
		
		//Пометка связанных с базой клеткок true
		for(byte ci = 0; ci < LCI; ci++) {
			for(byte x = 0; x < supCells.length; x++) {
				for(byte y = 0; y < supCells[x].length; y++) {
					if(!supCells[x][y]) {
						List<ActiveCell> neigh = LO.cellNeighbors(cells, (ActiveCell)cells[x][y], false);
						
						for(byte n = 0; n < neigh.size(); n++) {
							byte[] crds = LO.getCellCoords(neigh.get(n), cells);
							byte xn = crds[0], yn = crds[1];
							
							if(supCells[xn][yn]) {
								supCells[x][y] = true;
								break;
							}
						}
					}
				}
			}
		}
		
		//Удаление всех имеющих метку false клеток на поле
		for(byte x = 0; x < supCells.length; x++) {
			for(byte y = 0; y < supCells[x].length; y++) {
				if(!supCells[x][y]) {
					cells[x][y].setColor(Color.NITRAL);
					((ActiveCell)cells[x][y]).setContent(Content.VOID);
				}
			}
		}
	}
	
	/**
	 * <b>Пересчёт монет для текущего пользователя</b>
	 */
	private void recalculateCoins() {
		users[shift].setIncome(LO.countIncome(cells, users[shift].getColor()));
		users[shift].addCoins(users[shift].getIncome(), true);
	}
	
	/**
	 * <b>Пересчёт живых игроков</b>
	 * @param defeat - <i>Проверка на уничтожение</i>
	 */
	private void recalculateAlivePlayers(boolean defeat) {
		//Для каждого пльзователя
		for(byte i = 0; i < users.length; i++) {
			boolean alive = false;
			//Для каждой клетки массива
			for(byte j = 0; j < cells.length; j++) {
				for(byte k = 0; k < cells[j].length; k++) {
					//Проверки на соответствие с правилами игры и на внутренние ошибк, если да, то alive = true
					if(cells[j][k] instanceof ActiveCell && cells[j][k].getColor() == users[i].getColor() && ((ActiveCell)cells[j][k]).getContent() == Content.BASE) alive = true;
				}
			}
			//если alive = false, то users[i].setAlive(false)
			if(!alive) {
				
				List<ActiveCell> ownCells = LO.getOwnCells(cells, users[i].getColor());
					
				for(short c = 0; c < ownCells.size(); c++) {
					if(ownCells.get(c).getContent() != Content.VOID) {
						ownCells.get(c).setContent(Content.VOID);
						if(defeat) {
							Object[] objects = {LO.getCellCoords(ownCells.get(c), cells), new Gif(0.2, 0, "assets/images/attributes/icons/destroy/", 14, "png")};
							destroyGifQueue.add(objects);
						}
					}
				}
				
				users[i].setAlive(false);
			}
		}
	}
	
	/**
	 * <b>Пересчёт атакующих клеток</b>
	 * @param user - <i>Пользователь</i>
	 */
	private void recalculateAttackContent(User user) {
		if(user.getCoins() <= 0) {
			List<ActiveCell> ownCells = LO.getOwnCells(cells, user.getColor());
			
			for(short i = 0; i < ownCells.size(); i++) {
				if(Content.getContentType(ownCells.get(i).getContent()) == 'a') {
					user.addIncome(-ownCells.get(i).getContent().getIncome());
					ownCells.get(i).setContent(Content.VOID);
					if(defeat) {
						Object[] objects = {LO.getCellCoords(ownCells.get(i), cells), new Gif(0.2, 0, "assets/images/attributes/icons/destroy/", 14, "png")};
						destroyGifQueue.add(objects);
					}
				}
			}
		}
	}
	
	/**
	 * <b>Клонирование карты</b>
	 * @param cells - <i>Массив клеток</i>
	 */
	private static AbstractCell[][] cloneMap(AbstractCell[][] cells) {
		AbstractCell[][] newCells = new AbstractCell[cells.length][cells[0].length];
		
		for(byte i = 0; i < newCells.length; i++) {
			for(byte j = 0; j < newCells[i].length; j++) {
				newCells[i][j] = (AbstractCell)cells[i][j].clone();
			}
		}
		
		return newCells;
	}
}

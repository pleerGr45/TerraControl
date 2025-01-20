package logic.user;

import graphics.Window;
import logic.cells.Color;
import logic.interfaces.StringMeta;
import logic.process.LO;

/**
 * <b>Абстрактный класс пользователя</b>
 * <p>Данный класс описывает то, каким будет любой играющий в программе
 * @author vlad_matveev
 * @see Player
 * @see Bot
 * @version 1.0
 */
public abstract class User implements StringMeta {
	
	/**
	 * <b>Счётчик пользователей</b>
	 */
	private static int users = 0;
	/**
	 * <b>Имя пользователя</b>
	 */
	protected String name;
	/**
	 * <b>Уникальный универсальный идентификатор</b>
	 */
	protected int userUUID;
	/**
	 * <b>Цвет пользователя</b>
	 */
	protected Color color;
	/**
	 * <b>Количество монет</b>
	 */
	protected int coins;
	/**
	 * <b>Размер хранилища монет</b>
	 */
	protected int storage;
	/**
	 * <b>Доход за ход</b>
	 */
	protected int income;
	/**
	 * <b>Статус жизни</b>
	 */
	protected boolean alive;
	/**
	 * <b>Массив хранения личной статистики игрока</b>
	 * <b>[0]</b> - Клеток захвачено
	 * <b>[1]</b> - Построено юнитов
	 * <b>[2]</b> - Построено зданий
	 * <b>[3]</b> - Продано зданий
	 */
	private int[] stats;

	//  							  \\
	//           GET-МЕТОДЫ           \\
	//                                \\
	public int 		getIncome() {return income;}
	public String 	getName() {return name;}
	public Color 	getColor() {return color;}
	public int 		getCoins() {return coins;}
	public int 		getStorage() {return storage;}
	public int 		getUserUUID() {return userUUID;}
	public int 		getStats(byte i) {return stats[i];}
	public boolean 	isAlive() {return alive;}
	/**
	 * <b>Метод получения количества пользователей</b>
	 * @return {@link User#users}
	 */
	public static int getUsers() {return users;}
	
	//  							  \\
	//           SET-МЕТОДЫ           \\
	//                                \\
	public void setIncome(int income) {this.income = income;}
	public void setName(String name) {this.name = name;}
	public void setColor(Color color) {this.color = color;}
	public void setCoins(int coins) {this.coins = coins;}
	public void setStorage(int storage) {this.storage = storage;}
	public void setUserUUID(int userUUID) {this.userUUID = userUUID;}
	public void setAlive(boolean alive) {this.alive = alive;}
	
	//  							  \\
	//           ADD-МЕТОДЫ           \\
	//                                \\
	public boolean addCoins(int coins, boolean nullable) {
		if(this.coins + coins >= 0) {
			this.coins += coins;
			countStorage();
			return true;
		} else if(nullable) this.coins = 0;
		return false;
	}
	public void addIncome(int income) {this.income += income;}
	public void addStats(byte i) {this.stats[i]++;}
	
	/**
	 * <b>Конструктор класса {@link User} </b>
	 * @param color - <i>Цвет пользователя</i>
	 * @param coins - <i>Количество монет пользователя</i>
	 * @param name - <i>Имя пользователя</i>
	 * @param isCloneable - <i>Клонирование пользователя</i>
	 */
	public User(Color color, int coins, String name, boolean isCloneable) {
		this.color = color;
		this.coins = coins;
		this.alive = true;
		
		this.name = name != null ? name : generateName();
		
		stats = new int[4];
		
		if(!isCloneable) {
			userUUID = users;
			users++;
		}
	}
	
	/**
	 * <b>Абстрактный метод совершения хода</b>
	 * <p>Определяется в дочерних классах, является основным отличительным действием играющих
	 * @see Player#doMove()
	 * @see Bot#doMove()
	 * @since 0.1
	 */
	public abstract void doMove();
	
	/**
	 * <b>Метод установления стандартных значений</b>
	 */
	public void nulling() {
		coins = 10;
		for(byte i = 0; i < stats.length; i++) {
			stats[i] = 0;
		}
		alive = true;
	}
	
	/**
	 * <b>Метод подсчёта хранилища {@link User#storage}</b>
	 */
	public void countStorage() {
		storage = LO.countWeight(Window.l.getCells(), color) * 12;
		
		if(coins > storage) coins = storage;
	}
	
	/**
	 * <b>Метод генерации случайного имени пользователя</b>
	 * <p>Сначала метод определяет, длину имени (от 4 до 7). 
	 * Потом идёт случайный выбор префикса (X_, Oo__, _). 
	 * Следующим шагом метод генерирует в зависимости от замера имени и от индекса символа буквы (в цикле).
	 * В конце генерируется суффикс в зависимости от префикса или случайный символ '_'. 
	 * Также если суффикс не был сгенерирован в зависимотси от префикса, то может с шансом 2 к 10 добавится число от 1999 до 2011
	 * @return случайное имя
	 */
	public static String generateName() {
		String name = "";
		byte length = (byte)(Math.random()*4+3);
		
		byte suffix_status = 0;
		
		final char[] consonants = {'d', 's', 'k', 'm', 'n', 'b', 't', 'r', 'h'};
		final char[] vowels = {'a', 'y', 'e', 'i', 'o'};
		
		if(Math.random()*100 < 10) name += "_";
		else if(Math.random()*100 > 95) {name += "I_"; suffix_status = 1;}
		else if(Math.random()*100 > 97) {name += "Oo__"; suffix_status = 2;}
		
		for(byte i = 0; i <= length ;i++) {
			if(length % 2 == 0) if(i == length) {name += vowels[(byte) (Math.random()*2)]; continue;}
			name+= i % 2 == 0 ? consonants[(byte) (Math.random()*consonants.length)] : vowels[(byte) (Math.random()*vowels.length)];
		}
		
		
		if(suffix_status == 1) name += "_I";
		else if(suffix_status == 2) name += "__oO";
		else if(Math.random()*100 > 80) name += "_";
		
		if(Math.random()*100 < 20 && suffix_status == 0) name += ""+(short)(Math.random()*12+1999);
		
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof User ? this.userUUID == ((User)obj).userUUID ? true : false : false;
	}
	
	@Override
	public String hashStringMeta() {
		return (this instanceof Player ? "P" : "B")+C+""+userUUID+""+C+name+C+coins+C+color.getID()+S; 
	}
}

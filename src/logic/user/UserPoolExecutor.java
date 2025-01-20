package logic.user;

import java.util.ArrayList;
import java.util.List;

import logic.cells.Color;

/**
 * <b>Обработчик пользователь</b>
 * @author vlad_matveev
 * @see User
 * @version 1.0
 */
public class UserPoolExecutor {

	/**
	 * <b>Колекция пользователей</b>
	 */
	private List<User> users;
	
	/**
	 * <b>Конструктор класса {@link UserPoolExecutor}</b>
	 */
	public UserPoolExecutor() {
		this(null, true);
	}
	
	/**
	 * <b>Конструктор класса {@link UserPoolExecutor}</b>
	 * @param users - <i>Колекция пользователей</i>
	 * @param randomize - <i>Рандомизация при null</i>
	 */
	public UserPoolExecutor(List<? extends User> users, boolean randomize) {
		this(users, randomize, (byte)1, (byte)(Math.random()*5+1));
	}
	
	/**
	 * <b>Конструктор класса {@link UserPoolExecutor}</b>
	 * @param users - <i>Колекция пользователей</i>
	 * @param randomize - <i>Рандомизация при null</i>
	 * @param playerQuantity - <i>Количество игроков при рандомизации</i>
	 * @param botQuantity - <i>Количество ботов при рандомизации</i>
	 */
	public UserPoolExecutor(List<? extends User> users, boolean randomize, byte playerQuantity, byte botQuantity) {
		this.users = new ArrayList<>();
		
		if(users != null) {
			for(byte i = 0; i < users.size(); i++) {
				add(users.get(i));
			}
		} else if(randomize) {
			UserPoolExecutor pool = randomize(playerQuantity, botQuantity);
			
			for(byte i = 0; i < pool.getPool().size(); i++) {
				add(pool.get(i));
			}
		}
	}
	
	/**
	 * <b>Метод добавления пользователя в колекцию</b>
	 * @param user - <i>Пользователь</i>
	 * @return Успех - true, неудача - false
	 */
	public boolean add(User user) {
		for(byte i = 0; i < users.size(); i++) if(users.get(i).getColor() == user.getColor()) return false;
		users.add(user);
		return true;
	}

	/**
	 * <b>Метод удаления пользователя из колекции</b>
	 * @param user - <i>Пользователь</i>
	 * @return Успех - true, неудача - false
	 */
	public boolean delet(User user) {
		for(byte i = 0; i < users.size(); i++) if(users.get(i).equals(user)) {users.remove(i); return true;}
		return false;
	}
	
	/**
	 * <b>Метод получения поьзователя из колекции</b>
	 * @param i - <i>индекс пользователя в колекции</i>
	 * @return Пользователь с индексом i в колекции {@link UserPoolExecutor}
	 */
	public User get(byte i) {
		return i < 0 || i > users.size() ? null : users.get(i);
	}
	
	/**
	 * <b>Метод получения колекции пользователей</b>
	 * @return {@link UserPoolExecutor#users}
	 */
	public List<User> getPool() {
		return users;
	}
	
	/**
	 * <b>Метод установления колекции пользователей</b>
	 */
	public void setPool(List<User> users) {
		this.users = users;
	}
	
	/**
	 * <b>Метод рандомизации пользователей</b>
	 * @param players - <i>Количество игроков при рандомизации</i>
	 * @param bots - <i>Количество ботов при рандомизации</i>
	 * @return Случайно сгенерированная колекция пользователей 
	 */
	public static UserPoolExecutor randomize(byte players, byte bots) {
		
		UserPoolExecutor pool = new UserPoolExecutor(null, false);
		Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME, 
						  Color.GREEN, Color.LIGHT_BLUE, Color.BLUE, Color.CYAN, 
						  Color.PINK, Color.PURPUR, Color.PURPLE, Color.WHITE};
		
		if(players < 0 || players > 12 || bots < 0 || bots > 12) {throw new NumberFormatException("Количество не соответствует (0 <= x <= 12)");}
		
		if(players+bots < 2 || players+bots > 12) {throw new NumberFormatException("Общее количество не соответствует (2 <=*/ x <= 12)");}
		
		byte j = 0;
		
		for(byte i = 0; i < bots;) {
			for(j = 0; j < colors.length; j++) {
				if(colors[j] != null && Math.random()*100 < 25) {
					pool.add(new Bot(colors[j], 10, null, false));
					i++;
					colors[j] = null;
					break;
				}
			}
		}
		
		for(byte i = 0; i < players;) {
			for(j = 0; j < colors.length; j++) {
				if(colors[j] != null && Math.random()*100 < 25) {
					pool.add(new Player(colors[j], 10, null, false));
					i++;
					colors[j] = null;
					break;
				}
			}
		}
		
		
		return pool;
	}
}

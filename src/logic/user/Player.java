package logic.user;

import graphics.Window;
import logic.cells.Color;

/**
 * <b>Класс игрока</b>
 * @author vlad_matveev
 * @see User
 * @version 1.0
 */
public class Player extends User {

	/**
	 * <b>Конструктор класса {@link Player} </b>
	 * @param color - <i>Цвет игрока</i>
	 * @param coins - <i>Количество монет игрока</i>
	 * @param name - <i>Имя игрока</i>
	 * @param isCloneable
	 */
	public Player(Color color, int coins, String name, boolean isCloneable) {
		super(color, coins, name, isCloneable);
	}

	/**
	 * <b>Метод совершения хода</b>
	 */
	@Override
	public void doMove() {
		Window.l.stop();
	}
	
	@Override
	public Player clone() {
		Player player = new Player(color, coins, name, true);
		
		player.alive = alive;
		player.income = income;
		player.userUUID = userUUID;
		
		return player;
	}
}

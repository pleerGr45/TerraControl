package logic.interfaces;

import logic.cells.Content;
import logic.user.User;

/**
 * <b>Интерфейс, описывающий поведение захватываемых клетое</b>
 * @author vlad_matveev
 * @see Upgradable
 * @see StringMeta
 * @version 1.0
 */
public interface Capturable {

	/**
	 * <b>Метод захвата клетки</b>
	 * @return Успех - true, Неудача - false
	 */
	public boolean capture(User user, Content content);
}

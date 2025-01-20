package logic.cells;

import logic.interfaces.Capturable;
import logic.user.User;

/**
 * <b>Класс описывающий специальные клетки</b>
 * @deprecated Данный класс в процессе разработки стал неиспользуемым
 * @author vlad_matveev
 * @see ActiveCell
 * @version 0.0
 */
@Deprecated
public class SpecialCell extends ActiveCell implements Capturable {
	
	/**
	 * <b>Конструктор класса {@link SpecialCell}</b>
	 * @param color - <i>Цвет пользователя</i>
	 * @param content - <i>Содержимое клетки</i>
	 * @param owner - <i>Владелец клетки</i>
	 * @param isCloneable - <i>Клонирование клетки</i>
	 */
	public SpecialCell(Color color, User owner, Content content, boolean isCloneable) {
		super(color, content, isCloneable);
	}

	@Override
	public boolean capture(User user, Content content) {
		return false;
	}

}

package logic.cells;

import graphics.Window;
import logic.interfaces.Capturable;
import logic.interfaces.Upgradable;
import logic.process.LO;
import logic.user.Bot;
import logic.user.User;

/**
 * <b>Класс клетки</b>
 * <p>Данный класс описывает стандартную пользовательскую клетку
 * @author vlad_matveev
 * @see ActiveCell
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class Cell extends ActiveCell implements Capturable, Upgradable {

	/**
	 * <b>Конструктор класса {@link Cell}</b>
	 * @param color - <i>Цвет клетки</i>
	 * @param content - <i>Содержимое клетки</i>
	 * @param isCloneable - <i>Клонирование клетки</i>
	 */
	public Cell(Color color, Content content, boolean isCloneable) {
		super(color, content, isCloneable);
	}

	@Override
	public boolean capture(User user, Content content) {
		if(user.getColor() != color) {
			if(content.getAttacktLevel() > protectionLevel) {
				if(LO.getColorUser(color, Window.l.getUsers()) instanceof Bot) {
					((Bot)LO.getColorUser(color, Window.l.getUsers())).addDisappearedCell(this);
					((Bot)LO.getColorUser(color, Window.l.getUsers())).addPotentialTarget(user);
				}
				setColor(user.getColor()); user.addStats((byte)0); return true;	
			}
		} else if(this.content == Content.VOID ||(Content.getContentType(this.content) == 'a' && this.content.getAttacktLevel() == content.getAttacktLevel())) return true;
		
		return false;
	}

	@Override
	@Deprecated
	public boolean upgrade(Content content) {
		return false;
	}
	
	@Override
	public Object clone() {
		Cell cell = new Cell(color, content ,true);
		
		cell.id = this.id;
		
		return cell;
	}
	
	@Override
	public String hashStringMeta() {
		return "C"+C+""+id+""+C+color.getID()+C+content.getID()+S;
	}
}

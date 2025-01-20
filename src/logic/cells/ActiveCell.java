package logic.cells;

import logic.user.User;

/**
 * <b>Абстрактный класс активной клетки</b>
 * <p>Данный класс описывает, то какой будет любая клетка, способная к активации и содержанию
 * @author vlad_matveev
 * @see AbstractCell
 * @version 1.0
 */
public abstract class ActiveCell extends AbstractCell {

	/**
	 * <b>Конструктор класса {@link ActiveCell}</b>
	 * @param color - <i>Цвет клетки</i>
	 * @param content - <i>Содержимое клетки</i>
	 * @param isCloneable - <i>Клонирование клетка</i>
	 */
	public ActiveCell(Color color, Content content, boolean isCloneable) {
		super(color, isCloneable);
		
		this.content = content;
		contentActivity = true;
	}
	
	/**
	 * <b>Владелец клетки</b>
	 * @deprecated Данное поле в процеесе разработки стало неиспользуемым
	 */
	@Deprecated
	protected User owner;
	/**
	 * <b>Уровень защиты клетки</b>
	 */
	protected byte protectionLevel;
	/**
	 * <b>Содержимое клетки</b>
	 */
	protected Content content;
	/**
	 * <b>Активность содержимого</b>
	 */
	protected boolean contentActivity;

	public byte getProtectionLevel() {return protectionLevel;}
	public Content getContent() {return content;}
	public boolean isActivity() {return contentActivity;}
	@Deprecated
	public User getOwner() {return owner;}
	
	public void setProtectionLevel(byte protectionLevel) {this.protectionLevel = protectionLevel < 4 ? protectionLevel : 4;}
	public void setContent(Content content) {this.content = content;}
	public void setActivity(boolean contentActivity) {this.contentActivity = contentActivity;}
	@Deprecated
	public void setOwner(User owner) {this.owner = owner;}
	
	@Override
	public Object clone() {
		ActiveCell cell = new ActiveCell(color, content, true) {};
		
		cell.id = this.id;
		
		return cell;
	}
	
	@Override
	public String hashStringMeta() {
		return "A"+C+""+id+""+C+color.getID()+C+content.getID()+S;
	}
}

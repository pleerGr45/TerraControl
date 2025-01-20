package logic.cells;

/**
 * <b>Класс пустой клетки</b>
 * <p>Данный класс описывает то, какой будет любая неактивная клетка
 * @author vlad_matveev
 * @see AbstractCell
 * @version 1.0
 */
public class VoidCell extends AbstractCell {

	/**
	 * <b>Конструктор класса {@link VoidCell} </b>
	 * @param color - <i>Цвет клетки</i>
	 * @param isCloneable - <i>Клонирование клетки</i>
	 */
	public VoidCell(Color color, boolean isCloneable) {
		super(color, isCloneable);
	}
	
	@Override
	public Object clone() {
		VoidCell cell = new VoidCell(color, true);
		
		cell.id = this.id;
		
		return cell;
	}
	
	@Override
	public String hashStringMeta() {
		return "V"+C+""+id+""+C+color.getID()+S;
	}
}

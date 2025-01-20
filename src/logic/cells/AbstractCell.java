package logic.cells;

import logic.interfaces.StringMeta;

/**
 * <b>Абстрактный класс клетк</b>
 * <p>Данный класс описывает то, какой будет любая клетка существующая в программе
 * @author vlad_matveev
 * @version 1.0
 */
public abstract class AbstractCell implements StringMeta {
	
	/**
	 * <b>Счётчик количества клеток</b>
	 */
	private static int counter = 0;
	/**
	 * <b>Порядковый номер клетки</b>
	 */
	protected int id;
	/**
	 * <b>Цвет клетки</b>
	 */
	protected Color color;
	
	public static int getCounter() {return counter;}
	public int getId() {return id;}
	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}
	public void setId(int id) {this.id = id;}
	
	/**
	 * <b>Конструктор класса {@link AbstractCell}</b>
	 * @param color - <i>Цвет клетки</i>
	 * @param isCloneable - <i>Клонирование клетка</i>
	 */
	public AbstractCell(Color color, boolean isCloneable) {
		this.color = color;
		
		if(!isCloneable) { 
			id = counter;
			counter++;
		}
	}
	
	@Override
	public Object clone() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AbstractCell && id == ((AbstractCell)obj).id ? true : false;
	}
	
	@Override
	public String toString() {
		return "["+getClass().getName()+"@"+hashCode()+", ID: "+id+"]";
	}
	
	@Override
	public String hashStringMeta() {
		return id+""+C+color.getID()+""+S;
	}
}

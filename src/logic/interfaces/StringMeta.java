package logic.interfaces;

/**
 * <b>Интерфейс строчной записи параметров объекта</b>
 * @author vlad_matveev
 * @see Capturable
 * @see Upgradable
 * @version 1.0
 */
public interface StringMeta {

	/**
	 * <b>Символ разделения объектов</b>
	 */
	public static final char C = '&';
	/**
	 * <b>Символ разделения строк</b>
	 */
	public static final char S = '$';
	
	/**
	 * <b>Метод получения параметров клетки в виде строки</b>
	 * @return
	 */
	public String hashStringMeta();
}

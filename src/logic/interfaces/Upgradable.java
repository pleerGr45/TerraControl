package logic.interfaces;

import logic.cells.Content;

/**
 * <b>Интерфейс, описывающий поведения улучшаемых клеток</b>
 * @deprecated Данный интерфейс в процессе разработки стал неиспользуемым
 * @author vlad_matveev
 * @see Capturable
 * @see StringMeta
 * @version 0.0
 */
@Deprecated
public interface Upgradable {

	/**
	 * <b>Метод улучшения/постороения содержимого клетки</b>
	 * @deprecated Данный метод в процеесе разработки стало неиспользуемым
	 * @param content <i>Содержимое</i>
	 * @return Успех - true, Неудача - false
	 */
	@Deprecated
	public boolean upgrade(Content content);
}

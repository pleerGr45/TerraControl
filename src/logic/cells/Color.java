package logic.cells;

import java.awt.Image;
import graphics.GraphicsPack;

/**
 * <b>Перечисление цветов клеток</b>
 * @author vlad_matveev
 * @see Content
 * @version 1.0
 */
public enum Color {

	/**
	 * <b><i>КРАСНЫЙ</i></b>
	 */
	RED((byte) 2),
	/**
	 * <b><i>ОРАНЖЕВЫЙ</i></b>
	 */
	ORANGE((byte) 3),
	/**
	 * <b><i>ЖЁЛТЫЙ</i></b>
	 */
	YELLOW((byte) 4),
	/**
	 * <b><i>СВЕТЛО-ЗЕЛЁНЫЙ</i></b>
	 */
	LIME((byte) 5),
	/**
	 * <b><i>ЗЕЛЁНЫЙ</i></b>
	 */
	GREEN((byte) 6),
	/**
	 * <b><i>СИНИЙ</i></b>
	 */
	BLUE((byte) 7),
	/**
	 * <b><i>СВЕТЛО-СИНИЙ</i></b>
	 */
	LIGHT_BLUE((byte) 8),
	/**
	 * <b><i>БИРЮЗОВЫЙ</i></b>
	 */
	CYAN((byte) 9),
	/**
	 * <b><i>РОЗОВЫЙ</i></b>
	 */
	PINK((byte) 10),
	/**
	 * <b><i>ПУРПУРНЫЙ</i></b>
	 */
	PURPUR((byte) 11),
	/**
	 * <b><i>ФИОЛЕТОВЫЙ</i></b>
	 */
	PURPLE((byte) 12),
	/**
	 * <b><i>БЕЛЫЙ</i></b>
	 */
	WHITE((byte) 13),
	/**
	 * <b><i>ПУСТОЙ</i></b>
	 */
	VOID((byte) 0),
	/**
	 * <b><i>НЕЙТРАЛЬНЫЙ</i></b>
	 */
	NITRAL((byte) 1),
	/**
	 * <b><i>ВЫБРАННЫЙ</i></b>
	 */
	SELECTED((byte) 14);
	
	/**
	 * <b>Изображение цвета</b>
	 */
	private static final Image[] IMAGES = {
		GraphicsPack.imageLoader("assets/images/cells/void.png"),
		GraphicsPack.imageLoader("assets/images/cells/nitral.png"),
		GraphicsPack.imageLoader("assets/images/cells/red.png"),
		GraphicsPack.imageLoader("assets/images/cells/orange.png"),
		GraphicsPack.imageLoader("assets/images/cells/yellow.png"),
		GraphicsPack.imageLoader("assets/images/cells/lime.png"),
		GraphicsPack.imageLoader("assets/images/cells/green.png"),
		GraphicsPack.imageLoader("assets/images/cells/blue.png"),
		GraphicsPack.imageLoader("assets/images/cells/light_blue.png"),
		GraphicsPack.imageLoader("assets/images/cells/cyan.png"),
		GraphicsPack.imageLoader("assets/images/cells/pink.png"),
		GraphicsPack.imageLoader("assets/images/cells/purpur.png"),
		GraphicsPack.imageLoader("assets/images/cells/purple.png"),
		GraphicsPack.imageLoader("assets/images/cells/white.png"),
		GraphicsPack.imageLoader("assets/images/cells/selected.png")
	};
	
	/**
	 * <b>Порядковый номер цвета</b>
	 */
	private byte ID;
	
	//                                \\
	//           GET-METODS           \\
	//                                \\
	public Image getImage() {
		return IMAGES[ID];
	}
	public byte getID() {
		return ID;
	}
	public void changeID(byte id) {
		this.ID = id;
	}
	public static Image getImage(byte id) {return IMAGES[id];}
	
	/**
	 * <b> Конструктор перечислений {@link Color Color}</b>
	 * @param id <i>ID изображения клетки</i>
	 */
	Color(byte id) {ID = id;}
	
	/**
	 * <b>Метод преобразования цвета {@link logic.cells.Color} в {@link java.awt.Color}</b>
	 * @param c - {@link logic.cells.Color ÷вет}
	 * @return {@link java.awt.Color} по {@link logic.cells.Color цвету} c
	 */
	public static java.awt.Color parseJavaColor(Color c) {
		return switch(c) {
		case RED        -> new java.awt.Color(255, 0, 0);       /*Красный*/
		case ORANGE     -> new java.awt.Color(255, 106, 0);     /*Оранжевый*/
		case YELLOW     -> new java.awt.Color(255, 212, 0);     /*Жёлтый*/
		case LIME       -> new java.awt.Color(76, 255, 0);      /*Сетло-зелёный*/
		case GREEN      -> new java.awt.Color(0, 127, 0);       /*«Зелёный*/
		case BLUE       -> new java.awt.Color(0, 4, 255);       /*Синий*/
		case LIGHT_BLUE -> new java.awt.Color(0, 255, 255);     /*Светло-синий*/
		case CYAN       -> new java.awt.Color(0, 145, 147);     /*Бирюзовый*/
		case PINK       -> new java.awt.Color(255, 127, 237);   /*Розовый*/
		case PURPUR     -> new java.awt.Color(255, 0, 220);     /*Пурпурный*/
		case PURPLE     -> new java.awt.Color(178, 0, 255);     /*Фиолетовый*/
		case WHITE      -> new java.awt.Color(239, 239, 239);   /*Белый*/
		default         -> new java.awt.Color(127, 127, 127);   /*Нейтральный*/
		};
	}
	
	/**
	 * <b>Метод получения цвета по названию</b>
	 * @param number - <i>число</i>
	 * @return {@link Color} по ID number
	 */
	public static Color getColorByString(String number) {
		return switch(number) {
		case "0"  -> VOID;			/*Пустой*/
		case "2"  -> RED;			/*Красный*/
		case "3"  -> ORANGE;		/*Оранжевый*/
		case "4"  -> YELLOW;		/*Жёлтый*/
		case "5"  -> LIME;			/*Сетло-зелёный*/
		case "6"  -> GREEN;			/*«Зелёный*/
		case "7"  -> BLUE;			/*Светло-синий*/
		case "8"  -> LIGHT_BLUE;	/*Светло-синий*/
		case "9"  -> CYAN;			/*Бирюзовый*/
		case "10" -> PINK;			/*Розовый*/
		case "11" -> PURPUR;		/*Пурпурный*/
		case "12" -> PURPLE;		/*Фиолетовый*/
		case "13" -> WHITE;			/*Белый*/
		default   -> NITRAL;		/*Нейтральный*/
		};
	}
}

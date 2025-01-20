package logic.cells;

import java.awt.Image;
import graphics.GraphicsPack;
import main.Main;

/**
 * <b>Перечисление содержимого клеток</b>
 * @author vlad_matveev
 * @see Color
 * @version 1.0
 */
public enum Content {

	/**
	 * <b><i>БАЗА</i></b>
	 */
	BASE((byte) 1, (byte)1, (byte)0, (byte)2, (byte)1),
	/**
	 * <b><i>ПУСТОТА</i></b>
	 */
	VOID((byte) 0, (byte)0, (byte)0, (byte)1, (byte)1),
	/**
	 * <b><i>ЗАЩИТА I</i></b>
	 */
	DEFENS_I((byte) 2, (byte) 1, (byte) 0, (byte) -1, (byte)-10),
	/**
	 * <b><i>ЗАЩИТА II</i></b>
	 */
	DEFENS_II((byte) 3, (byte) 2, (byte) 0, (byte) -5, (byte)-25),
	/**
	 * <b><i>АТАКА I</i></b>
	 */
	ATTACK_I((byte) 4,  (byte) 1, (byte) 1, (byte) -1, (byte)-10, (byte)1),
	/**
	 * <b><i>АТАКА II</i></b>
	 */
	ATTACK_II((byte) 5, (byte) 2, (byte) 2, (byte) -5, (byte)-20, (byte)1),
	/**
	 * <b><i>АТАКА III</i></b>
	 */
	ATTACK_III((byte) 6, (byte) 3, (byte) 3, (byte) -15, (byte)-30, (byte)1),
	/**
	 * <b><i>АТАКА IV</i></b>
	 */
	ATTACK_IV((byte) 7, (byte) 3, (byte) 5, (byte) -30, (byte)-40, (byte)1),
	/**
	 * <b><i>АРТИЛЛЕРИЯ (МОТРИТА)</i></b>
	 */
	MORTAR((byte) 8, (byte) 0, (byte) 0, (byte) -40, (byte)-50, (byte)2),
	/**
	 * <b><i>ШАХТА I</i></b>
	 */
	MINE_I((byte) 9, (byte) 0, (byte) 0, (byte) 2, (byte)-12),
	/**
	 * <b><i>ШАХТА II</i></b>
	 */
	MINE_II((byte) 10, (byte) 0, (byte) 0, (byte) 4, (byte)-20),
	/**
	 * <b><i>ШАХТА III</i></b>
	 */
	MINE_III((byte) 11, (byte) 0, (byte) 0, (byte) 8, (byte)-28);
	
	/**
	 * <b>Изображения содержимого</b>
	 */
	private static final Image[] IMAGES = {
			GraphicsPack.imageLoader("assets/images/cells/content/void.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/base.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/defens_1.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/defens_2.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/attack_1.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/attack_2.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/attack_3.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/attack_4.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/mortar.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/mine_1.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/mine_2.png"),
			GraphicsPack.imageLoader("assets/images/cells/content/mine_3.png"),
		};
	
	/**
	 * <b>ID содержимого</b>
	 */
	private byte ID;
	/**
	 * <b>Уровень защиты содержимого</b>
	 */
	private byte protectionLevel;
	/**
	 * <b>Уровень атаки содержимого</b>
	 */
	private byte attacktLevel;
	/**
	 * <b>Уровень добычи монет содержимого</b>
	 */
	private byte income;
	/**
	 * <b>Стоимость содержимого</b>
	 */
	private byte cost;
	/**
	 * <b>Радиус действия</b>
	 */
	private byte radius;
	
	//                                \\
	//           GET-МЕТОДЫ           \\
	//                                \\
	public byte getProtectionLevel() {return protectionLevel;}
	public byte getAttacktLevel() {return attacktLevel;}
	public byte getIncome() {return income;}
	public byte getID() {return ID;}
	public byte getCost() {return cost;}
	public byte getRadius() {return radius;}
	public Image getImage() {return IMAGES[ID];}
	public static Image getImage(byte id) {return IMAGES[id];}
	
	/**
	 * <b>Конструктор перечисления {@link Content Contents}</b>
	 * 
	 * @param id <i>ID изображения содержимого</i>
	 * @param protectionLevel <i>Уровень защиты содержимого</i>
	 * @param attacktLevel <i>Уровень атаки содержимого</i>
	 * @param income <i>Уровень добычи монет содержимого</i>
	 * @param cost <i>цена содержимого</i>
	 */
	Content(byte id, byte protectionLevel, byte attacktLevel, byte income, byte cost) {
		this.protectionLevel = protectionLevel;
		this.attacktLevel = attacktLevel;
		this.income = income;
		this.cost = cost;
		this.radius = 0;
		ID = id;
	}
	
	/**
	 * <b>Конструктор перечисления {@link Content Contents}</b>
	 * 
	 * @param id <i>ID изображения содержимого</i>
	 * @param protectionLevel <i>Уровень защиты содержимого</i>
	 * @param attacktLevel <i>Уровень атаки содержимого</i>
	 * @param income <i>Уровень добычи монет содержимого</i>
	 * @param cost <i>цена содержимого</i>
	 */
	Content(byte id, byte protectionLevel, byte attacktLevel, byte income, byte cost, byte radius) {
		this(id, protectionLevel, attacktLevel, income, cost);
		this.radius = radius;
	}
	
	/**
	 * <b>Метод получения содержимого по уровню атаки</b>
	 * @param power - <i>уровень атаки</i>
	 * @return Содержимое типа атака
	 */
	public static Content getAttackContentByPower(byte power) {
		
		//Проверки
		power = power < 1 ? 1 : power;
		power = power > 4 ? 4 : power;
		
		//Возвращение результата
		return switch(power) {
		case 1 -> ATTACK_I;
		case 2 -> ATTACK_II;
		case 3 -> ATTACK_III;
		case 4 -> ATTACK_IV;
		default -> {Main.logger.fatal("null content | такой силы не существует"); yield null;}
		};
	}
	
	/**
	 * <b>Метод получения типа содержимого</b>
	 * @param content - <i>Содержимое</i>
	 * @return <b>'a'</b> - если ATTACK_I, ATTACK_II, ATTACK_III, ATTACK_IV, MORTAR
	 * 	   <br><b>'d'</b> - если DEFENS_I, DEFENS_II
	 *	   <br><b>'m'</b> - если MINE_I, MINE_II, MINE_III
	 *	   <br><b>'n'</b> - если остальное
	 */
	public static char getContentType(Content content) {
		switch (content) {
		case ATTACK_I: case ATTACK_II: case ATTACK_III: case ATTACK_IV: case MORTAR: return 'a';
		case DEFENS_I: case DEFENS_II: return 'd';
		case MINE_I: case MINE_II: case MINE_III: return 'm';
		default: return 'n';
		}
	}
	
	/**
	 * <b>Метод получения содержимого по названию</b>
	 * @param number - <i>Число</i>
	 * @return {@link Content} по ID number
	 */
	public static Content getContentByString(String number) {
		return switch(number) {
		case "1"  -> BASE;
		case "0"  -> VOID;
		case "2"  -> DEFENS_I;
		case "3"  -> DEFENS_II;
		case "4"  -> ATTACK_I;
		case "5"  -> ATTACK_II;
		case "6"  -> ATTACK_III;
		case "7"  -> ATTACK_IV;
		case "8"  -> MORTAR;
		case "9"  -> MINE_I;
		case "10" -> MINE_II;
		case "11" -> MINE_III;
		default -> null;
		};
	}
}

package logic.process;

import java.util.Scanner;

import com.writer.FileStringWriter;

import logic.cells.AbstractCell;
import logic.cells.Cell;
import logic.cells.Content;
import logic.cells.VoidCell;
import logic.user.Bot;
import logic.user.Player;
import logic.user.User;
import logic.user.UserPoolExecutor;

/**
 * <b>Класс-пакет операций для записи карт в файлы</b>
 * @author vlad_matveev
 * @see Logic
 * @see LO
 * @version 1.0
 */
public final class IOStreamLogic {

	/**
	 * <b>Приватный конструктор класса {@link IOStreamLogic}</b>
	 * <br>Для работы с классом экземпляр не требуется
	 */
	private IOStreamLogic() {}
	
	/**
	 * <b>Поток записи карты</b>
	 * @param writter - <i>Поток записи</i>
	 * @param cells - <i>Массив клеток</i>
	 * @param users - <i>Обработчик пользователей</i>
	 */
	public static void inputStream(FileStringWriter writter, AbstractCell[][] cells, UserPoolExecutor users) {
		
		String str = "";
		
		for(byte i = 0; i < cells.length; i++) {
			for(byte j = 0; j < cells[i].length; j++) {
				str+=cells[i][j].hashStringMeta();
			}
			str = removeLastChar(str);
			str+="\n";
		}
		
		str+=users.getPool().size()+"\n";
		
		for(byte i = 0; i < users.getPool().size(); i++) {
			str+=users.get(i).hashStringMeta();
		}
		
		str = removeLastChar(str);
		
		writter.write(str);
		writter.close(0);
	}
	
	/**
	 * <b>Поток чтения каты</b>
	 * @param out - <i>{@link Scanner} для чтения файла</i>
	 * @return Массив объектов карты
	 */
	public static Object[] outputStream(Scanner out) {
		
		UserPoolExecutor users = new UserPoolExecutor(null, false);
		AbstractCell cells[][];
		
		if(!out.hasNextLine()) return null;
		
		try {
			cells = new AbstractCell[Byte.parseByte(out.nextLine())][Byte.parseByte(out.nextLine())];
		} catch (Exception e) {e.printStackTrace(); return null;}
		
		for(byte i = 0; i < cells.length; i++) {
			cells[i] = getCellsLineByString(out, (byte)cells[0].length);
		}
		
		byte usersCount = Byte.parseByte(out.nextLine());
		
		String[] pr = getStringBySymbol(out.nextLine(), usersCount, '$');
		
		for(byte i = 0; i < pr.length; i++) {
			String[] userParam = getStringBySymbol(pr[i], (byte)5,'&');
			
			User user;
			
			int id = Integer.parseInt(userParam[1]);
			String name = userParam[2];
			int coins = Integer.parseInt(userParam[3]);
			logic.cells.Color color = logic.cells.Color.getColorByString(userParam[4]);
			
			if(userParam[0].equals("P")) {
				user = new Player(color, coins, name, true);
			} else {
				user = new Bot(color, coins, name, true);
			}
			
			user.setUserUUID(id);
			users.add(user);
		}
		
		out.close();
		
		Object[] obj = new Object[2];
		
		obj[0] = cells;
		obj[1] = users;
		
		return obj;
	}
	
	/**
	 * <b>Метод получения строки клеток</b>
	 * @param out - <i>{@link Scanner} для чтения файла</i>
	 * @param length - <i>Длина строки</i>
	 * @return Строка клеток
	 */
	public static AbstractCell[] getCellsLineByString(Scanner out, byte length) {
		AbstractCell[] cellsLine = new AbstractCell[length];
		
		String[] pr = getStringBySymbol(out.nextLine(), (byte)(length), '$');
		
		for(byte i = 0; i < pr.length; i++) {
			byte cell = (byte)(pr[i].charAt(0) == 'C' ? 4 : 3);
			String[] cellsParam = getStringBySymbol(pr[i], cell,'&');
			
			AbstractCell newCell;
			
			int id = Integer.parseInt(cellsParam[1]);
			logic.cells.Color color = logic.cells.Color.getColorByString(cellsParam[2]);
			
			if(cell == 4) {
				Content content = Content.getContentByString(cellsParam[3]);
				newCell = new Cell(color, content, true);
			} else {
				newCell = new VoidCell(color, true);
			}
			
			newCell.setId(id);
			cellsLine[i] = newCell;
		}
		
		return cellsLine;
	}
	
	/**
	 * <b>Метод получения строк с разделением символом</b>
	 * @param str - <i>Исходная строка</i>
	 * @param length - <i>Длина массив строк</i>
	 * @param symbol - <i>Символ разделения</i>
	 * @return Одномерный массив строк, получившийся при разделении исходной строки
	 */
	public static String[] getStringBySymbol(String str, byte length, char symbol) {
		String[] s = new String[length];
		
		String data = "";
		byte c = 0;
		
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) != symbol) {
				data += str.charAt(i);
			} else {
				s[c] = data;
				c++;
				data = "";
			}
		}
		
		if(data != "" ) {
			s[c] = data;
		}
		
		return s;
	}
	
	/**
	 * <b>Метод удаления последнего символа строки</b>
	 * <p>Если string == null, то метод вернёт ""
	 * @param string - <i>Данная строка</i>
	 * @return Строка string без последнего символа
	 */
	public static String removeLastChar(String string) {
		String newString = "";
		
		if(string != null) {
			for(int i = 0; i < string.length()-1; i++) {
				newString += string.charAt(i);
			}
		}
		
		return newString;
	}
}

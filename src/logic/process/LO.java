package logic.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import exceptions.CellNotFoundException;
import logic.cells.AbstractCell;
import logic.cells.ActiveCell;
import logic.cells.Cell;
import logic.cells.Color;
import logic.cells.Content;
import logic.cells.VoidCell;
import logic.user.User;
import logic.user.UserPoolExecutor;
import main.Main;

/**
 * <b>Класс-пакет операций для работы с клетками</b>
 * <p>Класс служит для содержания статических методов
 * @author vlad_matveev
 * @see Logic
 * @see IOStreamLogic
 * @version 1.0
 */
public final class LO {
	
	/**
	 * <b>Приватный конструктор класса {@link LO LogicalOperations}</b>
	 * <br>Для работы с классом экземпляр не требуется
	 */
	private LO() {}
	
	/**
	 * <b>Метод генерации мира</b>
	 * <br> Генерирует двумерный массив типа {@link AbstractCell AbstaractCell}, в котором хранится информация о каждой клетке
	 * <p> <b><i>Размеры карты должны быть не меньше 7 и не больше 30</i></b>
	 * 
	 * @throws NumberFormatException Если размеры карты меньше 7 или больше 30
	 * @param sizeX - <i>длина карты</i>
	 * @param sizeY - <i>ширина карты</i>
	 * @return сгенерированный мир
	 */
	public static AbstractCell[][] generate(byte sizeX, byte sizeY, UserPoolExecutor users) {
		
		if(sizeX < 7 || sizeX > 30 || sizeY < 7 || sizeY > 30) throw new NumberFormatException("Размеры карты должны быть не меньше 7 и не больше 30");
		
		AbstractCell[][] cells = new AbstractCell[sizeX][sizeY];
		
		for(byte x = 0; x < cells.length; x++) {
			for(byte y = 0; y < cells[x].length; y++) {
				if(Math.random()*10 < 8) cells[x][y] = new Cell(Color.NITRAL, Content.VOID, false); 
				else cells[x][y] = new VoidCell(Color.VOID, false);
			}
		}
		
		byte x, y, i;
		
		while(users.getPool().size() != 0) {
			
			while(true) {
			x = (byte)(Math.random()*cells.length);
			y = (byte)(Math.random()*cells[0].length);
			i = (byte)(Math.random()*users.getPool().size());
			
				if(cells[x][y] instanceof ActiveCell && !(((ActiveCell)cells[x][y]).getContent() == Content.BASE)) {
					cells[x][y] = new Cell(users.get(i).getColor(), Content.BASE, false);
					users.delet(users.get(i));
					break;
				}
			}
		}
		
		return cells;
	}
	
	/**
	 * <b>Метод получения большего числа из двух</b>
	 * @param x - <i>Первое число</i>
	 * @param y - <i>Второе число число</i>
	 * @return Наибольшее число
	 */
	public static byte findMore(byte x, byte y) {
		return x > y ? x : y;
	}
	
	/**
	 * <b>Метод получения координат клетки</b>
	 * @param cell - <i>Клетка, координаты которой необходимо получить</i>
	 * @param cells - <i>Массив клеток</i>
	 * @return одномерный массив типа {@link Byte byte} = {x, y}
	 */
	public static byte[] getCellCoords(AbstractCell cell, AbstractCell[][] cells) {
		for(byte x = 0; x < cells.length; x++) {
			for (byte y = 0; y < cells[x].length; y++) {
				if(cells[x][y].equals(cell)) {
					byte [] c = {x, y};
					return c;
				}
			}
		}
		
		Main.logger.fatal("Потеря данных о координатах клетки "+cell.toString()+" в массиве "+cells.toString());
		
		throw new CellNotFoundException();
	}
	
	/**
	 * <b>Метод принадлежности клетки к владельцу</b>
	 * @param cell - <i>Келтка, у которой нужно узнать принадлежность к владельцу</i>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет владельца</i>
	 * @return <b>true</b> - если клетка рядом с другими клетками владельца
	 *     <br><b>false</b> - если это не так
	 */
	public static boolean cellAvailableToOwner(AbstractCell cell, AbstractCell[][] cells, Color color) {
		byte[] c = {0, 0};
		try {
			c = getCellCoords(cell, cells);
		} catch (Exception e) {
			c = new byte[2];
		}
		final byte[][] cd = 
			{
				{(byte)(c[0]-1), c[1]},
				{(byte)(c[0]+1), c[1]}, 
				{c[0], (byte)(c[1]+1)}, 
				{c[0], (byte)(c[1]-1)}, 
				{(byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)), (byte)(c[1]+1)}, 
				{(byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)), (byte)(c[1]-1)}
			};
		
		//Конструкция проверок	
//		if(c[0] <= 0 && c[1] <= 0) {
//			
//		} else if(c[0] <= 0 && c[1] >= cells[c[0]].length) {
//			
//		} else if(c[0] >= cells.length && c[1] <= 0) {
//			
//		} else if(c[0] >= cells.length && c[1] >= cells.length) {
//			
//		} else if(c[0] <= 0){
//			
//		} else if(c[1] <= 0){
//			
//		} else if(c[0] >= cells.length){
//			
//		} else if(c[1] >= cells[c[0]].length){
//			
//		} else {
//			
//		}
		
		//Конструкция методом исключений
		for(byte i = 0; i < cd.length; i++) {
			if(cellBelongsToCell(cd[i][0], cd[i][1], cells, color, false)) {return true;}
		}
		return false;
	}
	
	/**
	 * <b>Метод исключения и проверки соседних клеток данной</b>
	 * @param x - <i>Координата x</i>
	 * @param y - <i>Координата y</i>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет владельца</i>
	 * @param allcells - <i>Проверка на цвет</i>
	 * @return <b>true</b> - если клетка того же цвета, что и клетка владельца, или клетка не выходит за пределы массива
	 *     <br><b>false</b> - если это не так
	 */
	private static boolean cellBelongsToCell(byte x, byte y, AbstractCell[][] cells, Color color, boolean allCells) {
		try {if(cells[x][y].getColor() == color || allCells) return true;
		} catch (IndexOutOfBoundsException e) {}
		return false;
	}
	
	/**
	 * <b>Метод получения соседних клеток данной</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param cell - <i>Данная клетка</i>
	 * @param allCells - <i>Проверка на принадлежность к владельцу</i>
	 * @return Коллекция клеток
	 */
	public static List<ActiveCell> cellNeighbors(AbstractCell[][] cells, AbstractCell cell, boolean allCells) {
		List<ActiveCell> list = new ArrayList<ActiveCell>();
		byte[] c = {0, 0};
		try {
			c = getCellCoords(cell, cells);
		} catch (Exception e) {
			c = new byte[2];
		}
		final byte[][] cd = 
			{
				{(byte)(c[0]-1), c[1]},
				{(byte)(c[0]+1), c[1]}, 
				{c[0], (byte)(c[1]+1)}, 
				{c[0], (byte)(c[1]-1)}, 
				{(byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)), (byte)(c[1]+1)}, 
				{(byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)), (byte)(c[1]-1)}
			};
		
		for(byte i = 0; i < cd.length; i++) {
			if(cellBelongsToCell(cd[i][0], cd[i][1], cells, cell.getColor(), allCells) && cells[cd[i][0]][cd[i][1]] instanceof ActiveCell) {
				list.add((ActiveCell)cells[cd[i][0]][cd[i][1]]);
			}
		}
		
		return list;
	}
	
	/**
	 * <b>Метод проверки радиуса</b>
	 * @param cell - <i>Исходная клетка</i>
	 * @param verCell - <i>Клетка, которая должна находиться в радиусе cell</i>
	 * @param cells - <i>Массив клеток</i>
	 * @param radius - <i>Радиус</i>
	 * @return <b>true</b> - Если {@value verCell} находится в радиусе {@value radius} исходной клетки {@value cell}
	 *     <br><b>false</b> - если это не так
	 */
	public static boolean cellContainsInCellRadius(AbstractCell cell, AbstractCell verCell, AbstractCell[][] cells, byte radius) {
		List<ActiveCell> coll = new ArrayList<>();
		
		addCollectionElements(coll, cellNeighbors(cells, cell, true));
		
		short size = (short)coll.size();
		
		for(byte i = 1; i < radius; i++) {
			for(short j = 0; j < size; j++) {
				addCollectionElements(coll, cellNeighbors(cells, (AbstractCell)coll.get(j), true));
			}
			size = (short)coll.size();
		}
		
		for(short i = 0; i < coll.size(); i++) {
			if(coll.get(i).equals(verCell)) {return true;}
		}
		
		return false;
		
//		return Math.abs(getCellCoords(cell, cells)[0] - getCellCoords(verCell, cells)[0]) <= radis 
//			&& Math.abs(getCellCoords(cell, cells)[1] - getCellCoords(verCell, cells)[1]) <= radis
//			? true : false;
	}

	/**
	 * <b>Метод записи одной коллекции в другую без повторения элементов (с {@link Set} уклоном)</b>
	 * @param list - <i>Коллекция для записи</i>
	 * @param newList - <i>Записываемая коллекция</i>
	 */
	public static void addCollectionElements(List<ActiveCell> list, List<ActiveCell> newList) {
		boolean exsist = false;
		
		for(short i = 0; i < newList.size(); i++) {
			exsist = false;
			for(short j = 0; j < list.size(); j++) {
				if(newList.get(i).equals(list.get(j))) 
					exsist = true;
			}
			if(!exsist) list.add(newList.get(i));
		}
	}
	
	public static void addCollectionElement(List<User> list, User user) {
		
		for(byte i = 0; i < list.size(); i++) {
			if(list.get(i).equals(user)) return;
		}
		
		list.add(user);
	}
	
	/**
	 * <b>Метод получения собственных клеток</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет</i>
	 * @return Список клеток цвета color
	 */
	public static List<ActiveCell> getOwnCells(AbstractCell[][] cells, Color color) {
		List<ActiveCell> ownCells = new ArrayList<>();
		
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				if(cells[i][j].getColor() == color && cells[i][j] instanceof ActiveCell) {
					ownCells.add((ActiveCell)cells[i][j]);
				}
			}
		}
		
		return ownCells;
	}
	
	/**
	 * <b>Получение соседней клетки по направлению</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param cell - <i>Исходная клетка</i>
	 * @param direction - <i>Направление</i>
	 * @return Коллекция клеток по направлению от исходной
	 */
	public static List<ActiveCell> getDirectionCell(AbstractCell[][] cells, AbstractCell cell, byte direction, boolean allCells) {
		List<ActiveCell> list = new ArrayList<ActiveCell>();
		byte[] c = {0, 0};
		try {
			c = getCellCoords(cell, cells);
		} catch (Exception e) {
			c = new byte[2];
		}
		final byte[][] cd = new byte[6][2];
		
		cd[2][0] = (byte)(c[0]+1); 
		cd[2][1] = c[1];
		cd[5][0] = (byte)(c[0]-1); 
		cd[5][1] = c[1];
		
			if(c[1] % 2 == 0){
				cd[1][0] = c[0]; 
				cd[1][1] = (byte)(c[1]-1);
				cd[0][0] = (byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)); 
				cd[0][1] = (byte)(c[1]-1);
				cd[3][0] = c[0]; 
				cd[3][1] = (byte)(c[1]+1);
				cd[4][0] = (byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)); 
				cd[4][1] = (byte)(c[1]+1);
			} else {
				cd[0][0] = c[0]; 
				cd[0][1] = (byte)(c[1]-1);
				cd[1][0] = (byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)); 
				cd[1][1] = (byte)(c[1]-1);
				cd[4][0] = c[0]; 
				cd[4][1] = (byte)(c[1]+1);
				cd[3][0] = (byte)(c[0]+(c[1] % 2 == 0 ? -1 : 1)); 
				cd[3][1] = (byte)(c[1]+1);
			}
		
		byte i = 0, val = 0;
		
		switch (direction) {
		case 0: i = 0; val = 2; break;
		case 1: i = 1; val = 4; break;
		case 2: i = 3; val = 5; break;
		case 3: i = 4; val = 6; break;
		}
		
		for(; i < val; i++) {
			if(cellBelongsToCell(cd[i][0], cd[i][1], cells, cell.getColor(), allCells) && cells[cd[i][0]][cd[i][1]] instanceof ActiveCell)
				list.add((ActiveCell)cells[cd[i][0]][cd[i][1]]);
		}
		
		if(direction == 3 && cellBelongsToCell(cd[0][0], cd[0][1], cells, cell.getColor(), allCells) && cells[cd[0][0]][cd[0][1]] instanceof ActiveCell) list.add((ActiveCell)cells[cd[0][0]][cd[0][1]]);
		
		return list;
	}
	
	/**
	 * <b>Проверка на повторяющиеся объекты в коллекции</b>
	 * @param list - <i>Коллекция клеток</i>
	 * @param obj - <i>Объект на проверку</i>
	 * @return <b>true</b> - если объект obj повторяется в коллекции клеток
	 * 	   <br><b>false</b> - если иначе
	 */
	public static boolean isRecurringListContent(List<ActiveCell> list, ActiveCell obj) {
		
		byte fd = 0;
		
		for(byte i = 0; i < list.size(); i++) {
			if(list.get(i).equals(obj)) fd++;
		}
		
		return fd <= 1 ? false : true;
	}
	
	/**
	 * <b>Метод проверки на запрещённое содержимое соседних клеток</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param cell - <i>Данная клетка</i>
	 * @param forbiddenContent - <i>Запрещённое содержимое</i>
	 * @param allCells - <i>Проверка на принадлежность к владельцу</i>
	 * @return <b>true</b> - если у соседней клетки есть запрещённое содержимое forbiddenContent
	 *     <br><b>false</b> - если иначе
	 */
	public static boolean isForbiddenNeighborCells(AbstractCell[][] cells, AbstractCell cell, Content forbiddenContent, boolean allCells) {
		List<ActiveCell> neighCells = cellNeighbors(cells, cell, allCells);
		
		for(byte i = 0; i < neighCells.size(); i++) {
			if(neighCells.get(i).getContent() == forbiddenContent) return true;
		}
		
		return false;
	}
	
	/**
	 * <b>Метод получения клеток по уровню защиты</b>
	 * @param cells - <i>Коллекция клеток</i>
	 * @param level - <i>Уровень защищённости</i>
	 * @return Коллекция клеток с защитой = level
	 */
	public static List<ActiveCell> getWeaknessCells(List<ActiveCell> cells, byte level) {
		List<ActiveCell> newCells = new ArrayList<>();
		
		for(short i = 0; i < cells.size(); i++) {
			if(cells.get(i).getProtectionLevel() <= level) newCells.add(cells.get(i));
		}
		
		return newCells;
	}
	
	/**
	 * <b>Метод поиска базы пользователя</b>
	 * @param color - <i>Цвет пользователя</i>
	 * @param cells - <i>Массив клеток</i>
	 * @return Базу пользователя по цвету
	 */
	public static ActiveCell getUserBase(Color color, AbstractCell[][] cells) {
		for(short i = 0; i < cells.length; i++) {
			for(short j = 0; j < cells[i].length; j++) {
				if(cells[i][j] instanceof ActiveCell && cells[i][j].getColor() == color && ((ActiveCell)cells[i][j]).getContent() == Content.BASE) {
					return ((ActiveCell)cells[i][j]);
				}
			}
		}
		
		Main.logger.error("Ошибка при получении базы по цвету "+color+" в массиве клеток "+cells);
		
		return null;
	}
	
	/**
	 * <b>Метод получения направления, в зависимости от цели</b>
	 * @param cell - <i>Данная клетка</i>
	 * @param target - <i>Цель</i>
	 * @param cells - <i>Массив клеток</i>
	 * @return Направление
	 */
	public static byte getDirectionByTarget(ActiveCell cell, ActiveCell target, AbstractCell[][] cells) {
		try {
			return (byte)((getCellCoords(cell, cells)[1] > getCellCoords(target, cells)[1]) ? 3 : 1);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * <b>Метод получения количества шахт у поьзователя</b>
	 * @param cells - <i>Коллекция клеток</i>
	 * @return Число шахт
	 */
	public static short getMineCells(List<ActiveCell> cells) {
		short n = 0;
		
		for(short i = 0; i < cells.size(); i++) {
			if(Content.getContentType(cells.get(i).getContent()) == 'm') {
				n++;
			}
		}
		
		return n;
	}
	
	/**
	 * <b>Метод получения пользователя по цвету</b>
	 * @param color - <i>Цвет</i>
	 * @param users - <i>Массив пользователей</i>
	 * @return Пользователь, имеющий цвет color
	 */
	public static User getColorUser(Color color, User[] users) {
		for(byte i = 0; i < users.length; i++) {
			if(users[i].getColor() == color) {
				return users[i];
			}
		}
		
		return null;
	}
	
	/**
	 * <b>Метод записи одного массива в другой</b>
	 * @param cells - <i>Массив клеток, в который производится запись</i>
	 * @param newCells - <i>Записываемый массив</i>
	 */
	public static void writeArrayToArray(AbstractCell[][] cells, AbstractCell[][] newCells) {
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				cells[i][j] = newCells[i][j];
			}
		}
	}
	
	/**
	 * <b>Метод подсчёта доли цвета в массиве</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет владельца</i>
	 * @return Колличество клеток данного цвета в массиве
	 */
	public static int countWeight(AbstractCell[][] cells, Color color) {
		int weight = 0;
		
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				if(cells[i][j].getColor() == color) weight++;
			}
		}
		
		return weight;
	}
	
	/**
	 * <b>Метод подсчёта дохода</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет владельца</i>
	 * @return Доход
	 */
	public static int countIncome(AbstractCell[][] cells, Color color) {
		int income = 0;
		
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				if(cells[i][j] instanceof ActiveCell && cells[i][j].getColor() == color)  {
					income+=((ActiveCell)cells[i][j]).getContent().getIncome();
				}
			}
		}
		
		return income;
	}
	
	/**
	 * <b>Метод расчета глобального счёта пользователя (по цвету)</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет</i>
	 * @return счёт
	 */
	public static int getGlobalValue(AbstractCell[][] cells, Color color) {
		int value = 0;
		
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				if(cells[i][j] instanceof ActiveCell && cells[i][j].getColor() == color) {
					value += Math.abs(((ActiveCell)cells[i][j]).getContent().getCost());
				}
			}
		}
		
		return value;
	}
	
	/**
	 * <b>Метод рассчёта глобальной защиты пользователя (по цвету)</b>
	 * @param cells - <i>Массив клеток</i>
	 * @param color - <i>Цвет</i>
	 * @return счёт глобальной защиты
	 */
	public static int getGlobalProtection(AbstractCell[][] cells, Color color) {
		int protection = 0;
		
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
				if(cells[i][j] instanceof ActiveCell && cells[i][j].getColor() == color)
				protection += ((ActiveCell)cells[i][j]).getProtectionLevel();
			}
		}
		
		return protection;
	}
}

package logic.user;

import java.util.ArrayList;
import java.util.List;

import graphics.Window;
import logic.cells.AbstractCell;
import logic.cells.ActiveCell;
import logic.cells.Cell;
import logic.cells.Color;
import logic.cells.Content;
import logic.process.LO;
import main.Main;

/**
 * <b>Класс бота</b>
 * @author vlad_matveev
 * @see User
 * @see Player
 * @version 3.5
 */
public class Bot extends User {

	/**
	 * <b>Координаты базы</b>
	 */
	private ActiveCell base;
	/**
	 * <b>Текущее положение клеток</b>
	 */
	private AbstractCell[][] cells;
	
	private List<ActiveCell> ownCells;
	/**
	 * <b>Список Атакующих клеток</b>
	 */
	private List<ActiveCell> attackList;
	/**
	 * <b>Список исчезнувших клеток</b>
	 */
	private List<ActiveCell> disappearedCells;
	/**
	 * <b>ПользователиБ являющиеся потенциальными целями</b>
	 */
	private List<User> potentialTargets;
	/**
	 * <b>Текущее направление движения</b>
	 * <pre>
	 * <b>0</b> - <i>Вверх</i>
	 * <b>1</b> - <i>Вправо</i>
	 * <b>2</b> - <i>Вниз</i>
	 * <b>3</b> - <i>Влево</i>
	 */
	private byte direction;
	/**
	 * <b>Текущее состояние даного пользователя</b>
	 * <pre>
	 * <b>0</b> - <i>Развитие</i>
	 * <b>1</b> - <i>Защита</i>
	 * <b>2</b> - <i>Нападение</i>
	 */
	private byte status;
	/**
	 * <b>Коэффициент рандома</b>
	 * <pre>Значение, которое влеяет на действия игрока в этом ходу. В начале каждого хода переменная получает значение из {@link Math#random()}.
	 */
	private double rc;
	/**
	 * <b>Данная цель</b>
	 */
	private ActiveCell target;
	
	public void addDisappearedCell(ActiveCell cell) {disappearedCells.add(cell);}
	public void addPotentialTarget(User user) {LO.addCollectionElement(potentialTargets, user);}
	
	/**
	 * <b>Конструктор класса {@link Bot} </b>
	 * @param color - <i>Цвет бота</i>
	 * @param coins - <i>Количество монет бота</i>
	 * @param name - <i>Имя бота</i>
	 * @param isCloneable - <i>Клонирование бота</i>
	 */
	public Bot(Color color, int coins, String name, boolean isCloneable) {
		super(color, coins, name, isCloneable);
		
		attackList = new ArrayList<>();
		disappearedCells = new ArrayList<>();
		potentialTargets = new ArrayList<>();
		status = 0;
	}

	/**
	 * <b>Метод совершения хода</b>
	 */
	@Override
	public void doMove() {
		
		cells = Window.l.getCells();
		base = LO.getUserBase(color, cells);
		ownCells = LO.getOwnCells(cells, color);
		rc = Math.random()*10;
		ejectedNonAttackContent();
		addLostAttackContent();
		
		eventExecutor();
		
		if(disappearedCells.size() > 0) {status = 1;}
		
		for(short i = 0; i < attackList.size(); i++) {
			captureMove(attackList.get(i), Window.l.getState() == 0 ? false : true);
		}
		
		List<ActiveCell> neighBaseCells = null;
		
		try {
			neighBaseCells = LO.cellNeighbors(cells, base, true);
		} catch (Exception e) {
			return;
		}
		
		for(byte i = 0; i < neighBaseCells.size(); i++) {
			if(neighBaseCells.get(i).getColor() != color) {
				Content attack = Content.getAttackContentByPower((byte)(neighBaseCells.get(i).getContent().getProtectionLevel()+1));
				
				if(Window.l.addAttackContent(attack, this, neighBaseCells.get(i))) {
					attackList.add(neighBaseCells.get(i));
				}
				
			}
		}
		
		switch(Window.l.getState()) {
		case 0:
			List<ActiveCell> neighCells;
			switch(status) {
			case 0:
				neighCells = LO.cellNeighbors(cells, base, false);
				
				for(byte i = 0; i < neighCells.size(); i++) {
					if(neighCells.get(i).getContent() == Content.VOID) {
						Window.l.addBuildingContent(Content.MINE_I, this, neighCells.get(i));
					}
				}
				break;
			case 1:
				for(short i = 0; i < disappearedCells.size(); i++) {
					neighCells = LO.cellNeighbors(cells, disappearedCells.get(i), false);
					
					if(neighCells.size() > 0) {
						byte itr = 0;
						bk: while(true) {
							byte r = (byte)(Math.random()*neighCells.size());
							itr++;
							if(!LO.isForbiddenNeighborCells(cells, neighCells.get(r), Content.DEFENS_I, false) && neighCells.get(r).getContent() == Content.VOID) {
								Window.l.addBuildingContent(Content.DEFENS_I, this, neighCells.get(r));
								break bk;
							}
							if(itr > rc) break bk;
						}
					}
					
				}
				status = 0;
			}
			break;
		case 1:
			
			switch (status) {
			case 0:
				byte itr = 0;
				
				if(!(((double)(LO.getMineCells(ownCells))/ownCells.size())*100 >= 35))
				
				bk2: for(short i = 0; i < ownCells.size(); i++) {
					if(Content.getContentType(ownCells.get(i).getContent()) == 'm' || ownCells.get(i).getContent() == Content.ATTACK_IV) {
						neighCells = LO.cellNeighbors(Window.l.getCells(), ownCells.get(i), false);
						
						for(byte j = 0; j < neighCells.size(); j++) {
							
							if(neighCells.get(j).getContent() == Content.VOID) {
								if(Math.random()*100 > 50) Window.l.addBuildingContent(Content.MINE_II, this, neighCells.get(j));
								else Window.l.addBuildingContent(Content.MINE_III, this, neighCells.get(j));
								
								itr++;
								
								if(j+3 > rc || itr > 3) break bk2;
							}
						}
					}
				}
				
				if(disappearedCells.size() > 4) status = 1;
				
				if(rc*100 < 300 && potentialTargets.size() > 0) status = 2;
				
				break;
			case 1:
				List<ActiveCell> weakCells = LO.getWeaknessCells(ownCells, (byte)0);
				
				short r = (short)(Math.random()*weakCells.size());
				
				if(Math.random()*100 < 30 && weakCells.size() != 0) 
				if(weakCells.get(r).getContent() == Content.VOID && !LO.isForbiddenNeighborCells(cells, weakCells.get(r), Content.DEFENS_II, false) && Math.random()*100 < 30) {
					Window.l.addBuildingContent(Content.DEFENS_II, this, weakCells.get(r));
				}
				
				status = 0;
				
				break;
			case 2:
				
				if(target == null) {target = LO.getUserBase(potentialTargets.get((byte)(Math.random()*potentialTargets.size())).getColor(), cells);}
				itr = 0;
				
				if(target != null)
				bk3: for(short i = 0; i < ownCells.size(); i++) {
					neighCells = LO.cellNeighbors(cells, ownCells.get(i), true);
					for(byte j = 0; j < neighCells.size(); j++) {
						if(neighCells.get(j) instanceof ActiveCell && neighCells.get(j).getColor() == target.getColor()) {
							Content attack = Content.getAttackContentByPower((byte)(((ActiveCell)neighCells.get(j)).getContent().getProtectionLevel()+1));
							
							itr++;
							
							if(i < 3*rc) {
								if(Window.l.addAttackContent(attack, this, neighCells.get(j))) {
									attackList.add(neighCells.get(j));
								}
							} else {
								potentialTargets.remove(LO.getColorUser(target.getColor(), Window.l.getUsers()));
								status = 1;
								break bk3;
							}
						}
					}
				}
				
				break;
			}
			
			break;
		}
		
		disappearedCells.clear();
	}
	
	/**
	 * <b>Метод совершения атакующего хода</b>
	 * @param cell - <i>Клетка</i>
	 * @param directionMove - <i>Направление</i>
	 * @return Срабатывание - true, Несрабатывание - falsed
	 */
	protected boolean captureMove(ActiveCell cell, boolean directionMove) {
		List<ActiveCell> neighCells = LO.cellNeighbors(cells, cell, true);
		ActiveCell newCell = null;
		byte itr = 0;
		
		if(Window.l.getState() == 1 && target != null) {
			direction = LO.getDirectionByTarget(cell, target, cells);
		}
		
		neighCells = directionMove ? LO.getDirectionCell(cells, cell, direction, true) : LO.cellNeighbors(cells, cell, true);
		
		cellEjection(neighCells, cell);
		
		if(neighCells.size() == 0) {
			neighCells = LO.cellNeighbors(cells, cell, true);
		}
		
		for(byte i = 0; i < neighCells.size(); i++) {
			if(neighCells.get(i).getContent() == Content.BASE && neighCells.get(i).getColor() != color) {
				newCell = neighCells.get(i);
			}
		}
		
		while(true) {
			if(newCell == null) newCell = neighCells.get((byte)(Math.random()*neighCells.size()));
			itr++;
			
			if(move(cell, newCell)) return true;
			if(itr > rc) return false;
			
			newCell = null;
		}
	}
	
	
	/**
	 * <b>Метод совершения хода</b>
	 * @param moveCell - <i>Клетка хода</i>
	 * @param cell - <i>Клетка для перехода</i>
	 * @return Успех - true, неудача - false
	 */
	protected boolean move(ActiveCell moveCell, ActiveCell cell) {
		Content moveContent = moveCell.getContent();
		
		if(Content.getContentType(moveContent) != 'a') return false;
		
		if(!(cell.getColor() == moveCell.getColor() && cell.getContent() == moveContent && moveContent == Content.ATTACK_IV)) {
		if(cell instanceof ActiveCell
			&& (LO.cellAvailableToOwner(cell, cells, color) 
			|| moveContent == Content.MORTAR)
			&& LO.cellContainsInCellRadius(moveCell, cell, cells, moveContent.getRadius())
			&& moveCell.isActivity()
			&& !cell.equals(moveCell)) {
			//Если переходный контент = Content.MORTAR
			if(moveContent == Content.MORTAR) {
				if((moveCell.getProtectionLevel()+moveContent.getAttacktLevel()) > cell.getContent().getProtectionLevel() 
						&& cell.getColor() != moveCell.getColor() 
						&& cell.getContent() != Content.BASE) {
					cell.setContent(Content.VOID);
					moveCell.setActivity(false);
					return true;
				}
			//Иначе
			} else if(((Cell)cell).capture(this, moveContent)) {
				if(cell.getContent() == moveContent) 
					cell.setContent(Content.getAttackContentByPower((byte)(cell.getContent().getAttacktLevel()+1)));
				else ((Cell)cell).setContent(moveContent);
				moveCell.setContent(Content.VOID);
				((Cell)cell).setActivity(false);
				attackList.remove(moveCell);
				attackList.add(cell);
				return true;
			}
		}
			
			Window.l.recalculateProtectionLevel();
		}
		
		return false;
	}
	
	
	/**
	 * <b>Обработчик событий</b>
	 * @see Bot#changeDirectionEvent()
	 * @see Bot#buyAttackContentEvent()
	 */
	protected void eventExecutor() {
		changeDirectionEvent();
		buyAttackContentEvent();
	}
	
	
	/**
	 * <b>Событие изменения направления</b>
	 */
	protected void changeDirectionEvent() {
		switch(Window.l.getState()) {
		case 0: if((int)(Math.random()*100) <= 25) {
				direction = (byte)(Math.random()*4);
			}
		break;
		case 1: 
			direction = (byte)(target != null && Math.random()*100 >= 12 ? LO.getDirectionByTarget(base, target, cells) : (byte)(Math.random()*4));
			break;
		}
	}
	
	
	/**
	 * <b>Событие покупки юнитов</b>
	 */
	protected void buyAttackContentEvent() {
		
		if(attackList.size() > (storage/48)) return;
		
		Content[] attackType = {Content.ATTACK_I, Content.ATTACK_II, Content.ATTACK_III, Content.ATTACK_IV};
		
		byte cont = -1;
		
		for(byte i = 3; i >= 0; i--) {
			if(((double)(-attackType[i].getIncome()) / income * 100 < 7) || (-attackType[i].getCost() < coins && Window.l.getMoves() < 20)) cont = i;
		}
		
		List<ActiveCell> neighcells = LO.cellNeighbors(cells, ownCells.get((int)(Math.random()*ownCells.size())), true);

		if(neighcells.size() == 0) return;
		
		for(byte i = 0; i < neighcells.size(); i++) { 
			if(neighcells.get(i).getColor() == color) neighcells.remove(i);
		}
		
		if(neighcells.size() > 0 && cont >= 0 && cont < 4) {
			ActiveCell cell = neighcells.get((int)(Math.random()*neighcells.size()));
			if(Window.l.addAttackContent(attackType[cont], this, cell)) {
				attackList.add(cell);
			}
		}
		
	}

	/**
	 * <b>Изгнание ошибочно помещённого содержимого</b>
	 */
	protected void ejectedNonAttackContent() {
		for(short i = 0; i < attackList.size(); i++) {
			if(Content.getContentType(attackList.get(i).getContent()) != 'a' || 
			   LO.isRecurringListContent(attackList, attackList.get(i))   ||
			   attackList.get(i).getColor() != color) {
			Main.logger.warning("Мусор "+attackList.get(i).getContent()+" в списке атаки у "+color);
			attackList.remove(i);
			}
		}
		
	}
	
	/**
	 * <b>Возвращение потерянного юнита</b>
	 */
	
	protected void addLostAttackContent() {
		next: for(short i = 0; i < ownCells.size(); i++) {
			if(Content.getContentType(ownCells.get(i).getContent()) == 'a') {
				for(short j = 0; j < attackList.size(); j++) {
					if(ownCells.get(i).equals(attackList.get(j))) 
						continue next;
				}
				
				attackList.add(ownCells.get(i));
				Main.logger.warning("Потеря юнита у "+color);
			}
		}
	}
	
	
	/**
	 * <b>Метод исключения клетки</b>
	 * @param c - <i>Коллекция</i>
	 * @param cell - <i>Клетка</i>
	 */
	private void cellEjection(List<ActiveCell> c, ActiveCell cell) {
		for(byte i = 0; i < c.size(); i++) {
			if(c.get(i).getColor() == color || cell.getContent().getAttacktLevel() < c.get(i).getContent().getProtectionLevel()) {
				c.remove(i);
			}
		}
	}
	
	
	@Override
	public Bot clone() {
		Bot bot = new Bot(color, coins, name, true);
		
		bot.alive = alive;
		bot.income = income;
		bot.userUUID = userUUID;
		
		return bot;
	}
}

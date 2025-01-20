package graphics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import logic.cells.Color;
import logic.user.UserPoolExecutor;

/**
 * <b>Класс панели отрисовки пользователей</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class JPlayerStatPanel extends JPanel {

	/**
	 * <b>Изображение черепа</b>
	 */
	private Image skull;
	/**
	 * <b>Обработчик пользователей</b>
	 */
	private UserPoolExecutor users;
	
	public void setUsers(UserPoolExecutor users) {this.users = users;}
	
	/**
	 * <b>Метод отрисовки</b>
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(users == null) return;
		
		for(byte i = 0; i < users.getPool().size(); i++) {
			
			g.setFont(new Font("", Font.BOLD, 16));
			g.setColor(Color.parseJavaColor(users.get(i).getColor()));
			g.fillRect(1, i*50+1, 161, 50);
			g.setColor(java.awt.Color.BLACK);
			g.drawRect(1, i*50+1, 161, 50);
			g.drawString(users.get(i).getName(), 5, i*50+20);
			g.fillRect(4, i*50+26, 10*((users.get(i).getCoins()+"").length()+1), 22);
			g.setColor(new java.awt.Color(255, 215, 0));
			g.setFont(new Font("", Font.BOLD, 10));
			g.drawString(users.get(i).getCoins()+"⏣", 5, i*50+35);
			g.setColor(users.get(i).getIncome() > 0 ? java.awt.Color.GREEN : java.awt.Color.RED);
			g.drawString(users.get(i).getIncome() > 0 ? (users.get(i).getIncome()+"")+"▲" : (users.get(i).getIncome()+"").replaceAll("-", "")+"▼", 6, i*50+45);
			if(!users.get(i).isAlive()) {
				g.drawImage(skull, 135, i*50+3, 25, 25, null);
			}
		}
	}
	
	/**
	 * <b>Конструктор класса {@link JPlayerStatPanel}</b>
	 * @param skull - <i>Изображение черепа</i>
	 * @param users - <i>Обработчик пользователей</i>
	 */
	public JPlayerStatPanel(Image skull, UserPoolExecutor users) {
		this.skull = skull;
		this.users = users;
	}
	
	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = 1L;
}

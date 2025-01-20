package graphics;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * <b>Класс панели с задним фоном</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class JPanelBack extends JPanel{

	/**
	 * <b>Изображение фона</b>
	 */
	private Image image;
	
	/**
	 * <b>Метод отрисовки</b>
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(image, 0, 0, 850, 700, null);
	};
	
	/**
	 * <b>Конструктор класса {@link JPanelBack}</b>
	 * @param image - <i>Изображение фона</i>
	 */
	public JPanelBack(Image image) {
		super();
		
		this.image = image;
	}
	
	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = 1L;
}

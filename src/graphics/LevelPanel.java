package graphics;

import java.awt.Color;
import java.awt.Image;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import logic.cells.AbstractCell;
import logic.process.IOStreamLogic;
import logic.user.UserPoolExecutor;

/**
 * <b>Класс панели уровней</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class LevelPanel extends JPanelBack {

	/**
	 * <b>Панель загрузки файлов</b>
	 */
	private FileLoaderPanel filePanel;
	/**
	 * <b>Окно программы</b>
	 */
	private Window parent;
	/**
	 * <b>Кнопка начала игры</b>
	 */
	private JButton playButton;
	
	/**
	 * <b>Конструктор класса {@link LevelPanel}</b>
	 * @param image - <i>Изображение фона</i>
	 * @param parent - <i>Окно программы</i>
	 */
	public LevelPanel(Image image, Window parent) {
		super(image);
		
		this.parent = parent;
		
		filePanel = new FileLoaderPanel(image, "assets/resource/levels");
		filePanel.setBounds(0, 0, 834, 561);
		add(filePanel);
		
		playButton = new JButton(new ImageIcon(GraphicsPack.imageLoader("assets/images/attributes/icons/playButton_icon.png")));
		playButton.setBounds(10, 610, 40, 40);
		playButton.setBackground(Color.LIGHT_GRAY);
		playButton.addActionListener(a -> {
			try {
				Object[] obj = IOStreamLogic.outputStream(new Scanner(filePanel.getCurrentFile()));
				
				this.parent.setCells((AbstractCell[][])obj[0]);
				this.parent.setUsers((UserPoolExecutor)obj[1]);
				
				parent.start(true, true, (byte) 50);
				
			} catch (Exception e1) {e1.printStackTrace();}
		});
		add(playButton);
	}

	/**
	 * <b>Серийный номер</b>
	 */
	private static final long serialVersionUID = -2418293334187755777L;
}

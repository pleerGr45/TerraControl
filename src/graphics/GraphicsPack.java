package graphics;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import main.Main;

/**
 * <b>Класс-пакет инструментов к графике</b>
 * <br>Класс статичных методов для работы с графикой
 * @author vlad_matveev
 * @version 1.0
 */
public final class GraphicsPack {

	/**
	 * <b>Приватный конструктор класса {@link GraphicsPack GraphicsPack}</b>
	 * <br>Для использоваия данного класса объект не требуется
	 */
	private GraphicsPack() {}
	
	/**
	 * <b>Метод комбинирования двух изображений</b>
	 * <br>Накладывает i1 на i2
	 * @throws NullPointerException если одно или несколько изображений = null
	 * @param i1 - <i>Основное изображение</i>
	 * @param i2 - <i>изображение, которое нужно наложить</i>
	 * @return Скомбинированное изображение
	 */
	public static Image combineImage(Image i1, Image i2) {
		if(i1 == null || i2 == null) throw new NullPointerException("Одно или несколько изображений = null");
		
		Image image = i1;
		
		Graphics g = image.getGraphics();
		
		g.drawImage(i2, 0, 0, null);
		g.dispose();
		
		return image;
	}
	
	/**
	 * <b>Метод комбинирования массива изображений</b>
	 * <br>Попорядке накладывает все элементы массива на первый, если же в массиве один элемент, то медот возвращает его.
	 * @throws NullPointerException если размер массива изображений, не равных null равен 0
	 * @param images - <i>Массив изображений</i>
	 * @return Скомбинированное изображение
	 */
	public static Image combineImage(Image[] images) {
		
		List<Image> nonNullImages = new ArrayList<>();
		
		for(byte i = 0; i < images.length; i++) {
			if(images[i] == null) nonNullImages.add(images[i]);
		}
		
		if(nonNullImages.size() == 0) throw new ArrayStoreException("Длинна массива не может быть равна нолю");
		
		Image image = nonNullImages.get(0);

		Graphics g = image.getGraphics();
		
		for(byte i = 1; i < nonNullImages.size(); i++) {
			g.drawImage(nonNullImages.get(i), 0, 0, null);
		}
		
		return image;
	}
	
	/**
	 * <b>Метод загрузки изображения из файл в ОЗУ</b>
	 * 
	 * @param path <i>Путь к файлу изображения</i>
	 * @return {@link Image instance}
	 */
	public static Image imageLoader(String path) {
		try {return ImageIO.read(new File(path));}
		catch (Exception e) {Main.logger.error("Изображение из "+path+" не загрузилось"); return null;}
	}
}

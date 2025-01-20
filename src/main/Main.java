package main;

import com.logger.LocalLogger;
import graphics.Window;
/**
 * <b>Главный класс</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class Main {
	
	/**
	 * <b>Програмный лог процессов</b>
	 */
	public static final LocalLogger logger = new LocalLogger("logs","log", "error analysis log"); 
	
	/**
	 * <pre>
	 *         ___     ___
	 * |\  /| |___| | |   |
	 * | \/ | |   | | |   |
	 * |    | |   | | |   |
	 * </pre>
	 * @param args - <i>Аргументы</i>
	 * @author vlad_matveev
	 * @since 0.1
	 */
	public static void main(String[] args) {
		try {
			new Window();
			logger.fine("Успешный запуск");
		} catch (Exception e) {
			logger.fatal("Запуск не осуществлён по причине: "+e.getMessage());
		}
	}
}

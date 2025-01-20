package exceptions;

/**
 * <b>Ошибка потери координат клетки</b>
 * @author vlad_matveev
 * @version 1.0
 */
public class CellNotFoundException extends RuntimeException {
	
	/**
	 * <b>Конструктор класса {@link CellNotFoundException}</b>
	 */
	public CellNotFoundException() {
		super();
	}
	
	public CellNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CellNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CellNotFoundException(String message) {
		super(message);
	}

	public CellNotFoundException(Throwable cause) {
		super(cause);
	}

	@java.io.Serial
	private static final long serialVersionUID = -6065623274645074664L;
}
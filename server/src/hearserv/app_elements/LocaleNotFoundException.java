package hearserv.app_elements;

public class LocaleNotFoundException extends Exception {

	private static final long serialVersionUID = 4400045900403230841L;

	public LocaleNotFoundException() {
		super();
	}

	public LocaleNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LocaleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocaleNotFoundException(String message) {
		super(message);
	}

	public LocaleNotFoundException(Throwable cause) {
		super(cause);
	}

}

package io.github.jokoframework.common.errors;

/**
 * Esto es un error inesperado dentro de la plataforma Joko Security. La aparicion de
 * esta excepcion indica una condicion inesperada y no deseada
 * 
 * @author danicricco
 *
 */
public class JokoApplicationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5029508179517400869L;

	public JokoApplicationException(String msg) {
		super(msg);
	}

	public JokoApplicationException(Throwable e) {
		super(e);
	}
}

package io.github.jokoframework.common.dto;

/**
 * Devuelve cuando se ha realizado la correcta creacion de un objeto
 * 
 * @author danicricco
 *
 */
public class JokoCreationResponse extends JokoBaseResponse {

	private BaseDTO obj;

	public JokoCreationResponse(BaseDTO obj) {
		super();
		this.obj = obj;
	}

	public JokoCreationResponse() {

	}

	public BaseDTO getObj() {
		return obj;
	}

	public void setObj(BaseDTO obj) {
		this.obj = obj;
	}

}

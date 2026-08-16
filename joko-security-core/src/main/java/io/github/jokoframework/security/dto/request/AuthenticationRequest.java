package io.github.jokoframework.security.dto.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Recibe las propiedades de la consulta login.
 * Se pueden agregar varias propiedades extras dentro de custom
 * @author danicricco
 *
 */
public class AuthenticationRequest implements Serializable {
	private static final long serialVersionUID = -8574310592446951264L;

	private String username;
	private String password;
	private String seed;
	//Ignoramos el warning del sonar. HashMap sí implementa serializable
	private HashMap<String, Object> custom;

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public HashMap<String, Object> getCustom() {
		return custom;
	}

	public void setCustom(HashMap<String, Object> custom) {
		this.custom = custom;
	}

}

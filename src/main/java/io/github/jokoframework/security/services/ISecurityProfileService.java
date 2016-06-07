package io.github.jokoframework.security.services;

import io.github.jokoframework.security.entities.SecurityProfile;

/**
 * Un security profile es una configuracion particular de seguridad que resume
 * como deben de ser creados los tokens del usuario
 * 
 * @author danicricco
 *
 */
public interface ISecurityProfileService {

	/**
	 * Obtiene la aplicacion basada en el key
	 * 
	 * @param key
	 * @return
	 */
	SecurityProfile getProfileByKey(String key);

	SecurityProfile getApplicationByKeySafety(String key, boolean throwIFDoesntExists);

	/**
	 * Guarda la aplicacion
	 * 
	 * @param entity
	 * @return
	 */
	SecurityProfile save(SecurityProfile entity);

	/**
	 * Busca la aplicacion basada en el key. Si no existe la crea en base a los
	 * datos del entity
	 */
	SecurityProfile getOrSaveProfile(String key, SecurityProfile app);
}

package io.github.jokoframework.security.services;

import java.util.List;

import io.github.jokoframework.security.dto.ConsumerAPIDTO;
import io.github.jokoframework.security.errors.JokoConsumerException;

/**
 * <p>
 * Maneja el storage de usuarios que tienen acceso a nivel de API.
 * <p/>
 * <p>
 * Un consumer NO es una persona sino una aplicacion que posee accesos de mayor
 * nivel
 * </p>
 * 
 * @author danicricco
 *
 */
public interface IConsumerAPIService {

    /**
     * Obtiene un usuario en base a su nombre
     * 
     * @param username
     * @return
     */
    public ConsumerAPIDTO getConsumer(String username);

    /**
     * Genera API Keys para el usuario
     * 
     * @param user
     * @return
     */
    public ConsumerAPIDTO generateAndStoreConsumer(ConsumerAPIDTO user) throws JokoConsumerException;

    public List<ConsumerAPIDTO> list();

    /**
     * Prueba si las credenciales son validas.
     * 
     * @param consumerId
     * @param password
     * @return true si son validas, false en cualquier otro caso
     */
    public boolean isValid(String consumerId, String password);

    /**
     * Cambia el password el consumer. Genera un password nuevo y devuelve
     * 
     * @param consumerId
     * @return
     */
    public ConsumerAPIDTO changePassword(String consumerId);
}

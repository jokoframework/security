package io.github.jokoframework.security.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;

/**
 * Esta es la interfaz recomendable para integrar con joko-security. Se puede
 * utilizar cualquier AuthenticationManager de spring en cuyo caso se utilizaran
 * los mecanismos default para determinar el security profile.
 * 
 * @author danicricco
 *
 */
public interface JokoAuthenticationManager {

	/**
	 * 
	 * Intenta autenticar el objeto {@link JokoAuthentication}, retornando un
	 * objeto totalmente completo, incluyendo
	 * 
	 * 
	 * <p>
	 * Un <code>JokoAuthenticationManager</code> tiene que honrar el mismo
	 * contrato de errores que un {@link AuthenticationManager}:
	 * <ul>
	 * <li>A {@link DisabledException} must be thrown if an account is disabled
	 * and the <code>AuthenticationManager</code> can test for this state.</li>
	 * <li>A {@link LockedException} must be thrown if an account is locked and
	 * the <code>AuthenticationManager</code> can test for account locking.</li>
	 * <li>A {@link BadCredentialsException} must be thrown if incorrect
	 * credentials are presented. Whilst the above exceptions are optional, an
	 * <code>AuthenticationManager</code> must <B>always</B> test credentials.
	 * </li>
	 * </ul>
	 * Exceptions should be tested for and if applicable thrown in the order
	 * expressed above (i.e. if an account is disabled or locked, the
	 * authentication request is immediately rejected and the credentials
	 * testing process is not performed). This prevents credentials being tested
	 * against disabled or locked accounts.
	 *
	 * @param authentication
	 *            El objeto de request
	 *
	 * @return Un objeto autenticado incluyendo credenciales
	 *
	 * @throws AuthenticationException
	 *             if authentication fails
	 */
	JokoAuthentication authenticate(JokoAuthentication authentication) throws AuthenticationException;
}

package io.github.jokoframework.common.dto;

import java.util.ArrayList;
import java.util.List;

/***
 * Utilidades para trabajar con DTOs. Los DTOs deben implementar la interfaz
 * {@link BaseDTO} Los elementos que se pueden convertir a DTO deben implementar
 * {@link DTOConvertable}
 *
 * @author danicricco
 *
 */
public class DTOUtils {

    private DTOUtils() {
    };

    /**
     * Recorre una lista de elemenos de tipo DTOConvertable, los conviente a DTO
     * y devuelve una lista de DTOs
     *
     * @param entities
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities, Class<T> c) {
        List<T> list = new ArrayList<T>();
        List<DTOConvertable> l = (List<DTOConvertable>) entities;
        for (DTOConvertable o : l) {
            list.add((T) o.toDTO());
        }
        return list;
    }
}
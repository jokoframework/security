package io.github.jokoframework.common.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Utilidades para trabajar con DTOs. Los DTOs deben implementar la interfaz
 * {@link BaseDTO} Los elementos que se pueden convertir a DTO deben implementar
 * {@link DTOConvertable}
 *
 * @author danicricco
 */
public class DTOUtils {

    private DTOUtils() {
    }

    /**
     * Recorre una lista de elemenos de tipo DTOConvertable, los conviente a DTO
     * y devuelve una lista de DTOs
     *
     * @param entities
     * @return
     */
    public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities) {
        List<T> list = new ArrayList<>();
        List<DTOConvertable> dtoConvertables = (List<DTOConvertable>) entities;
        list.addAll(dtoConvertables.stream().map(o -> (T) o.toDTO()).collect(Collectors.toList()));
        return list;
    }
}
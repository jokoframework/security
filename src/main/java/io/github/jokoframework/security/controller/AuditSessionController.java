package io.github.jokoframework.security.controller;

import com.wordnik.swagger.annotations.*;
import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.dto.AuditSessionDTO;
import io.github.jokoframework.security.dto.BaseResponseDTO;
import io.github.jokoframework.security.dto.request.AuditSessionRequestDTO;
import io.github.jokoframework.security.dto.response.AuditSessionResponseDTO;
import io.github.jokoframework.security.services.IAuditSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by afeltes on 07/09/16.
 */
@RestController
public class AuditSessionController {

    @Autowired
    private IAuditSessionService auditSessionService;

    @ApiOperation(value = "Obtiene la lista de sesiones.", notes = "Obtiene la lista de sesiones ordenados por fecha de ingreso en orden descendente.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = AuditSessionResponseDTO.class)})
    @RequestMapping(value = ApiPaths.SESSIONS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({@ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = SecurityConstants.VERSION_HEADER_NAME, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = "1.0")})
    public List<AuditSessionResponseDTO> getSessions(HttpServletRequest request, HttpServletResponse response,
                                                     @ApiParam(name = "startPage", value = "El número de página en que se iniciará la consulta. Si se pasa 0 no se toma en cuenta la paginación.")
                                                     @RequestParam(value = "startPage", required = false, defaultValue = "1") Integer startPage,
                                                     @ApiParam(name = "rowsPerPage", value = "Cuantos resultados por página se desean consultar.")
                                                     @RequestParam(value = "rowsPerPage", required = false, defaultValue = "5") Integer rowsPerPage) {
        return auditSessionService.findAllOrderdByUserDate(startPage, rowsPerPage);
    }

    @ApiOperation(value = "Guarda datos relacionados a la sesión de usuario, para fines de auditoría. Para la fecha de la sesión, se toma la del servidor.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Auditoria de sesión guardada correctamente."),
            @ApiResponse(code = 409, message = "No se pudo guardar la información de auditoría.")})
    @ApiImplicitParams({@ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = SecurityConstants.VERSION_HEADER_NAME, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = "1.0")})
    @RequestMapping(value = ApiPaths.SESSIONS, method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDTO> saveAuditSession(HttpServletRequest pHttpServletRequest, HttpServletResponse pHttpServletResponse, @RequestBody AuditSessionRequestDTO pAuditSessionRequestDTO) {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        AuditSessionDTO auditDTO = auditSessionService.save(pAuditSessionRequestDTO);
        if (auditDTO != null && auditDTO.getId() != null) {
            responseDTO.setHttpStatus(HttpStatus.OK);
            responseDTO.setSuccess(true);
        } else {
            responseDTO.setHttpStatus(HttpStatus.CONFLICT);
            responseDTO.setMessage(String.format("No se pudo guardar la información de auditoria: %s ", pAuditSessionRequestDTO));
        }
        return new ResponseEntity<BaseResponseDTO>(responseDTO, responseDTO.getHttpStatus());
    }
}

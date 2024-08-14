package com.diceprojects.msvcauthentication.utils;

import com.diceprojects.msvcauthentication.clients.ConfigurationClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Servicio para manejar el estado de las entidades a través de la configuración centralizada.
 * Esta clase se comunica con el servicio de configuración para obtener el estado activo de las entidades,
 * permitiendo a otros componentes de la aplicación acceder a esta información de manera reactiva.
 */
@Service
public class EntityStatusService {

    private final ConfigurationClient configurationClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor de EntityStatusService.
     *
     * @param configurationClient El cliente para acceder a los parámetros de configuración.
     * @param objectMapper        El mapeador de objetos para el procesamiento de JSON.
     */
    public EntityStatusService(ConfigurationClient configurationClient, ObjectMapper objectMapper) {
        this.configurationClient = configurationClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene el estado activo desde el servicio de configuración.
     * El estado activo es obtenido del parámetro "EntityStatus" en la configuración,
     * y es procesado para devolver el valor asociado a la clave "status1".
     *
     * @return Un {@link Mono} que emite el estado activo como una cadena, o un error si no se puede procesar el parámetro.
     */
    public Mono<String> obtenerEstadoActivo() {
        return configurationClient.getParameterByName("EntityStatus")
                .flatMap(parameter -> {
                    try {
                        Map<String, String> statusMap = objectMapper.readValue(parameter.getValue(), new TypeReference<>() {
                        });
                        return Mono.justOrEmpty(statusMap.get("status1"));
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Error al procesar los valores del parámetro", e));
                    }
                });
    }
}


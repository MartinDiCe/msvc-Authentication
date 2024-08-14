package com.diceprojects.msvcauthentication.clients;

import com.diceprojects.msvcauthentication.persistences.models.dtos.ParameterDTO;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * Cliente para comunicarse con el microservicio de msvc-configurations.
 */
@Component
public class ConfigurationClient {

    private final WebClient webClient;

    /**
     * Constructor de ConfigurationClient.
     *
     * @param configurationsServiceUrl la URL base del servicio de configuraciones, inyectada desde el archivo de configuración.
     */
    public ConfigurationClient(@Value("${msvc.configurations.url}") String configurationsServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(configurationsServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .compress(true)
                                .wiretap("reactor.netty.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                ))
                .build();
    }

    /**
     * Obtiene un parámetro por su nombre desde el microservicio de msvc-configurations.
     *
     * @param parameterName el nombre del parámetro.
     * @return un Mono que emite el parámetro encontrado.
     */
    public Mono<ParameterDTO> getParameterByName(String parameterName) {
        return webClient.get()
                .uri("/parameters/getParameterName/{parameterName}", parameterName)
                .retrieve()
                .bodyToMono(ParameterDTO.class);
    }

    /**
     * Guarda o actualiza un parámetro en el microservicio de msvc-configurations.
     *
     * @param parameter el objeto ParameterDTO a crear o actualizar.
     * @return un Mono que emite el parámetro guardado.
     */
    public Mono<ParameterDTO> saveOrUpdateParameter(ParameterDTO parameter) {
        return webClient.post()
                .uri("/parameters")
                .bodyValue(parameter)
                .retrieve()
                .bodyToMono(ParameterDTO.class);
    }

    /**
     * Elimina un parámetro por su ID en el microservicio de msvc-configurations.
     *
     * @param parameterId el ID del parámetro a eliminar.
     * @return un Mono vacío que indica la finalización de la eliminación.
     */
    public Mono<Void> deleteParameterById(String parameterId) {
        return webClient.delete()
                .uri("/parameters/delete/{parameterId}", parameterId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * Obtiene todos los parámetros desde el microservicio de msvc-configurations.
     *
     * @return un Flux que emite todos los parámetros.
     */
    public Flux<ParameterDTO> getAllParameters() {
        return webClient.get()
                .uri("/parameters/ListAll")
                .retrieve()
                .bodyToFlux(ParameterDTO.class);
    }
}

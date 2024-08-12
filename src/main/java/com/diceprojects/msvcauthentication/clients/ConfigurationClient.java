package com.diceprojects.msvcauthentication.clients;


import com.diceprojects.msvcauthentication.persistences.dto.ParameterDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Cliente para comunicarse con el microservicio de msvc-configurations.
 */
@Component
public class ConfigurationClient {

    private final WebClient.Builder webClientBuilder;

    public ConfigurationClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Obtiene un parámetro por su nombre desde el microservicio de msvc-configurations.
     *
     * @param parameterName el nombre del parámetro.
     * @return un Mono que emite el parámetro encontrado.
     */
    public Mono<ParameterDTO> getParameterByName(String parameterName) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8005/api/parameters/getParameterName/{parameterName}", parameterName)
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
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8005/api/parameters")
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
        return webClientBuilder.build()
                .delete()
                .uri("http://localhost:8005/api/parameters/delete/{parameterId}", parameterId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * Obtiene todos los parámetros desde el microservicio de msvc-configurations.
     *
     * @return un Flux que emite todos los parámetros.
     */
    public Flux<ParameterDTO> getAllParameters() {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8005/api/parameters/ListAll")
                .retrieve()
                .bodyToFlux(ParameterDTO.class);
    }
}
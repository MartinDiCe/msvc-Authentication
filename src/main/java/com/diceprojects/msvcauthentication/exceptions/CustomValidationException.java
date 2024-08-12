package com.diceprojects.msvcauthentication.exceptions;

/**
 * CustomValidationException es una clase de excepción personalizada que extiende RuntimeException.
 * Se utiliza específicamente para representar fallos de validación dentro de la aplicación,
 * proporcionando una indicación clara de que ocurrió un error debido a datos o estado inválidos.
 */
public class CustomValidationException extends RuntimeException {

    /**
     * Construye una nueva CustomValidationException con el mensaje de detalle especificado.
     * El mensaje de detalle se utiliza para proporcionar una razón específica para el fallo de validación.
     *
     * @param message el mensaje de detalle que proporciona información específica sobre el fallo de validación.
     *                El mensaje de detalle se guarda para su posterior recuperación mediante el método Throwable.getMessage().
     */
    public CustomValidationException(String message) {
        super(message);
    }

    /**
     * Construye una nueva CustomValidationException con el mensaje de detalle y la causa especificados.
     * Este constructor es útil para excepciones de validación que son el resultado de otro problema subyacente.
     *
     * @param message el mensaje de detalle que proporciona información específica sobre el fallo de validación.
     *                El mensaje de detalle se guarda para su posterior recuperación mediante el método Throwable.getMessage().
     * @param cause   la causa de la excepción, que representa la razón subyacente del fallo de validación
     *                (guardada para su posterior recuperación mediante el método Throwable.getCause()).
     *                Se permite un valor nulo y indica que la causa es inexistente o desconocida.
     */
    public CustomValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

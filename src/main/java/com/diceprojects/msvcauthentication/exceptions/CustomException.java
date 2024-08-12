package com.diceprojects.msvcauthentication.exceptions;

/**
 * CustomException es una clase de excepción personalizada que extiende RuntimeException.
 * Se utiliza en toda la aplicación para representar excepciones que ocurren debido a
 * lógica de negocio específica o fallos operacionales, proporcionando más contexto que
 * una excepción de tiempo de ejecución estándar.
 */
public class CustomException extends RuntimeException {

    /**
     * Construye una nueva CustomException con el mensaje de detalle especificado.
     * El mensaje de detalle se guarda para su posterior recuperación mediante el método Throwable.getMessage().
     *
     * @param message el mensaje de detalle. El mensaje de detalle se guarda para su posterior
     *                recuperación mediante el método Throwable.getMessage().
     */
    public CustomException(String message) {
        super(message);
    }

    /**
     * Construye una nueva CustomException con el mensaje de detalle y la causa especificados.
     * Note que el mensaje de detalle asociado con la causa no se incorpora automáticamente
     * en el mensaje de detalle de esta excepción.
     *
     * @param message el mensaje de detalle. El mensaje de detalle se guarda para su posterior
     *                recuperación mediante el método Throwable.getMessage().
     * @param cause   la causa (que se guarda para su posterior recuperación mediante el método Throwable.getCause()).
     *                (Se permite un valor nulo, lo que indica que la causa es inexistente o desconocida).
     */
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}

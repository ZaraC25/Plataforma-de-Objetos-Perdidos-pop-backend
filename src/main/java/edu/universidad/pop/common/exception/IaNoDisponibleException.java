package edu.universidad.pop.common.exception;

/** El servicio de IA (Gemini) no pudo responder; el mensaje se muestra al usuario. */
public class IaNoDisponibleException extends RuntimeException {
    public IaNoDisponibleException(String message) { super(message); }
}

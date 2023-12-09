package monopoly_exceptions;

import monopoly_core.Jugador;

public class SinDineroException extends MonopolyException {
    private Jugador jugador;
    private Jugador destinatario;
    private float cantidad;

    public SinDineroException(Jugador jugador, Jugador destinatario ,float cantidad) {
        super("");
        this.jugador = jugador;
        this.cantidad = cantidad;
        this.destinatario = destinatario;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public float getCantidad() {
        return cantidad;
    }

    public Jugador getDestinatario() {
        return destinatario;
    }
}

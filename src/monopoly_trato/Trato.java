package monopoly_trato;

import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public abstract class Trato {
    
    private final String identificador;
    private final Jugador jugador1;
    private final Jugador jugador2;
    private String mensajeProponer;
    private String mensajeAceptar;
    public Trato(Jugador jugador1, Jugador jugador2,String identificador) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.identificador = identificador;
    }
    public String getIdentificador() {
        return identificador;
    }
    public Jugador getJugador1() {
        return jugador1;
    }
    public Jugador getJugador2() {
        return jugador2;
    }
    public String getMensajeProponer() {
        return mensajeProponer;
    }
    public String getMensajeAceptar() {
        return mensajeAceptar;
    }
    public void setMensajeProponer(String mensajeProponer) {
        this.mensajeProponer = mensajeProponer;
    }
    public void setMensajeAceptar(String mensajeAceptar) {
        this.mensajeAceptar = mensajeAceptar;
    }
    public boolean esProponente(Jugador jugador){
        return this.jugador1.equals(jugador);
    }
    public abstract void aceptar() throws MonopolyException;
}

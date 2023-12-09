package monopoly_exceptions;

import monopoly_core.Jugador;

public class BancarrotaException extends MonopolyException{
    private Jugador jugador;
    private Jugador destinatario;
    public BancarrotaException(Jugador jugador,Jugador destinatario){
        super("");
        this.jugador = jugador;
        this.destinatario = destinatario;
    }
    @Override
    public String getMessage(){
        return "El jugador " + this.jugador.getNombre() + " ha caido en bancarrota.";
    }
    public Jugador getJugador(){
        return this.jugador;
    }
    public Jugador getDestinatario(){
        return this.destinatario;
    }
}

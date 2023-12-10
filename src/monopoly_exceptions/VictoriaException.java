package monopoly_exceptions;

import monopoly_core.Jugador;

public class VictoriaException extends MonopolyException{
    private Jugador ganador;
    public VictoriaException(Jugador ganador){
        super("");
        this.ganador = ganador;
    }
    public Jugador getGanador(){
        return this.ganador;
    }
}

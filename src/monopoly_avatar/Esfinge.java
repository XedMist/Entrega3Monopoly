package monopoly_avatar;

import monopoly_casilla.Casilla;
import monopoly_casilla.Salida;
import monopoly_exceptions.MonopolyException;

import java.util.List;
public class Esfinge extends Avatar{
    public Esfinge(char id, Salida s){
        super(id,s);
    }
    @Override
    public void moverEnAvanzado(List<Casilla> casillas, boolean debug) throws MonopolyException {

    }
    @Override
    public void siguienteTurno(){
        this.getJugador().setAccion(true);
        this.setEnMovimiento(false);
    }
    @Override
    public String toString() {
        return """
        {
            id: %c,
            tipo: esfinge,
            casilla: %s,
            jugador: %s
        }""".formatted(this.getId(),this.getCasilla().getNombre(),this.getJugador().getNombre());
    }
}

package monopoly_casilla;

import java.util.ArrayList;
import java.util.List;

import monopoly_avatar.Avatar;
import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public class Accion extends Casilla{
    public Accion(String nombre,int posicion){
        super(nombre,posicion);
    }
    public void accion(Jugador j) throws MonopolyException {
        float bote = Juego.banca.ganarBote();
        j.cobrar(bote);
        j.incrementarPremiosInversionesOBote(bote);
        Juego.consola.imprimir("Gana el bote de la banca de %.2f€\n".formatted(bote));
    }
    @Override
    public void caer(Avatar av) throws MonopolyException {
        this.accion(av.getJugador());

    }
    @Override
    public String toString() {
        float bote = Juego.banca.getBote();
        List<String> jugadores = new ArrayList<>();
        for(Avatar a: this.getAvatares()){
            jugadores.add(a.getJugador().getNombre());
        }
        return """
        {
            bote: %.2f,
            jugadores: %s
        }""".formatted(bote,jugadores);
    }
} 

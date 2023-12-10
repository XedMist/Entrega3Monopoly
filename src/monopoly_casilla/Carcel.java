package monopoly_casilla;

import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

import java.util.HashMap;
import java.util.Map;

public class Carcel extends Especial{
    //Porque no se puede cambiar el mapa, pero si se puede modificar el contenido del mapa
    private final Map<String,Integer> encarcelados;
    public Carcel(String nombre,int posicion){
        super(nombre,posicion);
        this.encarcelados = new HashMap<>(); 
    }
    public void encarcelar(Jugador j){
        Juego.consola.imprimir("El jugador %s ha sido encarcelado\n".formatted(j.getNombre()));
        this.encarcelados.put(j.getNombre(),0);
    }
    public boolean estaEncarcelado(Jugador j){
        return this.encarcelados.containsKey(j.getNombre());
    }
    public void desencarcelar(Jugador j, boolean dobles) throws MonopolyException {
        if(dobles){
            this.encarcelados.remove(j.getNombre());
            Juego.consola.imprimir("El jugador %s ha sacado dados dobles y sale de la cárcel. Puede lanzar los dados de nuevo.\n".formatted(j.getNombre()));
            j.setAccion(true);
            return;
        }
        int turnos = this.encarcelados.get(j.getNombre());
        if(turnos == 2){
            j.pagar(Juego.banca,0.25f* Juego.salida.getPremio());
            this.encarcelados.remove(j.getNombre());
            Juego.consola.imprimir("El jugador %s ha salido de la cárcel pagando %.2f. Puede lanzar los dados de nuevo.\n".formatted(j.getNombre(),0.25f* Juego.salida.getPremio()));
            j.setAccion(true);
            return;
        }
        this.encarcelados.put(j.getNombre(),turnos+1);
        j.setAccion(false);
        Juego.consola.imprimir("El jugador %s sigue en la cárcel\n".formatted(j.getNombre()));

    }

    @Override
    public String toString() {
        return """
        {
            salir: %.2f,
            jugadores: %s
        }""".formatted(0.25f* Juego.salida.getPremio(),this.encarcelados);
    }
}

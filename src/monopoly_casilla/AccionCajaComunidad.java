package monopoly_casilla;

import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

import java.util.ArrayList;
import java.util.List;

import monopoly_carta.CartaCajaComunidad;
public class AccionCajaComunidad extends Accion{
    private List<CartaCajaComunidad> cartas;
    private List<Jugador> jugadores;
    public AccionCajaComunidad(String nombre,int posicion){
        super(nombre,posicion);
    }
    public void inicializarCartas(List<CartaCajaComunidad> cartas,List<Jugador> jugadores){
        this.cartas = cartas;
        this.jugadores = jugadores;
    }
    @Override
    public void accion(Jugador jugador) throws MonopolyException {
        String carta = Juego.consola.leer("Elige una carta: ");
        try{
            int num = Integer.parseInt(carta);
            
            if(num < 1 || num > 6){
                Juego.consola.imprimir("Entrada no valida\n");
                accion(jugador);
                return;
            }
            //Baraja las cartas
            List<CartaCajaComunidad> cartas = new ArrayList<>(this.cartas);
            for(int i = 0; i < cartas.size(); i++){
                int pos = (int)(Math.random() * cartas.size());
                CartaCajaComunidad aux = cartas.get(i);
                cartas.set(i,cartas.get(pos));
                cartas.set(pos,aux);
            }
            //Ejecuta la accion de la carta elegida
            cartas.get(num - 1).accion(jugador,this.jugadores);
        }catch(NumberFormatException e){
            Juego.consola.imprimir("Entrada no valida\n");
            accion(jugador);
        }
    }
    @Override
    public String toString() {
        return """
        {
            tipo: caja_comunidad
        }""";
    }

}

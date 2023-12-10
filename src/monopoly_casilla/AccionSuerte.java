package monopoly_casilla;

import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

import java.util.ArrayList;
import java.util.List;

import monopoly_carta.CartaSuerte;
public class AccionSuerte extends Accion{
    private List<CartaSuerte> cartas;
    private List<Jugador> jugadores;
    public AccionSuerte(String nombre,int posicion){
        super(nombre,posicion);
    }
    public void inicializarCartas(List<CartaSuerte> cartas,List<Jugador> jugadores){
        this.cartas = cartas;
        this.jugadores = jugadores;
    }

    //Pide un numero, baraja aleatoriamente las cartas y ejecuta la accion de la carta elegida
    @Override
    public void accion(Jugador jugador) throws MonopolyException {
        String carta = Juego.consola.leer("Elige una carta: ");
        try{
            int num = Integer.parseInt(carta);

            if(num < 1 || num > this.cartas.size()){
                Juego.consola.imprimir("Entrada no valida\n");
                accion(jugador);
                return;
            }
            //Baraja las cartas
            List<CartaSuerte> cartas = new ArrayList<>(this.cartas);
            for(int i = 0; i < cartas.size(); i++){
                int pos = (int)(Math.random() * cartas.size());
                CartaSuerte aux = cartas.get(i);
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
            tipo: suerte
        }""";
    }
}

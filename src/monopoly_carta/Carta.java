package monopoly_carta;

import java.util.List;

import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public  abstract class Carta{
    private String descripcion;
    public Carta(String descripcion){
        this.descripcion = descripcion;
    }
    //Cada carta tiene una accion distinta
    public  abstract void accion(Jugador j, List<Jugador> jugadores) throws MonopolyException;
    public String getDescripcion(){
        return descripcion;
    }
}

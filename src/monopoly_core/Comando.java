package monopoly_core;

import monopoly_exceptions.LogicaException;
import monopoly_exceptions.MonopolyException;
import monopoly_exceptions.VictoriaException;

public interface Comando{
    void verTablero();
    void empezar() throws LogicaException;
    void crearJugador(String nombreJugador, String tAvatar) throws LogicaException;
    void lanzarDados(boolean debug) throws MonopolyException;
    void acabarTurno() throws VictoriaException;
    void comprar(String nombreCasilla) throws MonopolyException;
    void jugador();
    void listarJugadores();
    void listarAvatares();
    void listarEnVenta();
    void salirCarcel() throws MonopolyException;
    void edificar(String tipoEdificio) throws MonopolyException;
    void listarEdificios();
    void listarEdificios(String grupo);
    void vender(String tipoEdificio, String nombreCasilla,int numero) throws LogicaException;

    void hipotecar(String nombreCasilla) throws LogicaException;
    void deshipotecar(String nombreCasilla) throws MonopolyException;
    void bancarrota() throws MonopolyException;

    void estadisticas();
    void estadisticas(String nombreJugador) throws LogicaException;
    void cambiarModo() throws LogicaException;

    void trato(String nombreJugador2,String propiedad1,String propiedad2) throws MonopolyException;
    void trato(String nombreJugador1,String nombreJugador2,String propiedad1,float cantidad) throws MonopolyException;
    void trato(String nombreJugador1,String nombreJugador2,String propiedad1,String propiedad2,float cantidad) throws MonopolyException;
    void trato(String nombreJugador2, String propiedad1,String propiedad2,String propiedad3,int turnos) throws MonopolyException;
    void aceptar(String identificador) throws MonopolyException;
    void tratos();    
    void eliminarTrato(String identificador) throws MonopolyException;
}

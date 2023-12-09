package monopoly_core;

import monopoly_exceptions.MonopolyException;

public interface Comando{
    void verTablero();
    void empezar();
    void crearJugador(String nombreJugador, String tAvatar);
    void lanzarDados(boolean debug) throws MonopolyException;
    void acabarTurno();
    void comprar(String nombreCasilla) throws MonopolyException;
    void jugador();
    void listarJugadores();
    void listarAvatares();
    void salirCarcel() throws MonopolyException;
    void edificar(String tipoEdificio) throws MonopolyException;
    void listarEdificios();
    void listarEdificios(String grupo);
    void vender(String tipoEdificio, String nombreCasilla,int numero);

    void hipotecar(String nombreCasilla);
    void deshipotecar(String nombreCasilla);
    void bancarrota();

    void estadisticas();
    void estadisticas(String nombreJugador);
    void cambiarModo();

    void trato(String nombreJugador2,String propiedad1,String propiedad2) throws MonopolyException;
    void trato(String nombreJugador1,String nombreJugador2,String propiedad1,float cantidad) throws MonopolyException;
    void trato(String nombreJugador1,String nombreJugador2,String propiedad1,String propiedad2,float cantidad) throws MonopolyException;
    void trato(String nombreJugador2, String propiedad1,String propiedad2,String propiedad3,int turnos) throws MonopolyException;
    void aceptar(String identificador) throws MonopolyException;
    void tratos();    
    void eliminarTrato(String identificador) throws MonopolyException;
}

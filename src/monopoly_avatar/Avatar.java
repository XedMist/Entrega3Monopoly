package monopoly_avatar;

import monopoly_casilla.Casilla;
import monopoly_casilla.Salida;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

import java.util.List;

public abstract class Avatar{
    private char id;
    private Casilla casilla;
    private Jugador jugador;
    private int posicion;
    private boolean enModoAavanzado;
    private boolean enMovimiento;
    public Avatar(char id, Salida s){
        this.id=id;
        this.casilla=s;
        this.posicion=s.getPosicion();
        this.casilla.addAvatar(this);
        this.enModoAavanzado=false;
    }
    public void setEnMovimiento(boolean enMovimiento){
        this.enMovimiento=enMovimiento;
    }
    public boolean getEnMovimiento(){
        return this.enMovimiento;
    }

    public void setEnModoAvanzado(boolean enModoAvanzado){
        this.enModoAavanzado=enModoAvanzado;
    }
    public boolean getEnModoAvanzado(){
        return this.enModoAavanzado;
    }
    public char getId(){
        return this.id;
    }
    public int getPosicion(){
        return this.posicion;
    }
    public void moverEnBasico(Casilla c) throws MonopolyException {
        this.casilla.eliminarAvatar(this);
        this.casilla=c;
        this.posicion=c.getPosicion();
        this.casilla.addAvatar(this);
        

        this.casilla.addVisita(this.jugador.getNombre());
        this.casilla.caer(this);
    }
    public Jugador getJugador(){
        return this.jugador;
    }
    public Casilla getCasilla(){
        return this.casilla;
    }
    public void eliminar(){
        this.casilla.eliminarAvatar(this);
    }
    public void moverACasilla(Casilla c){
        this.casilla.eliminarAvatar(this);
        this.casilla = c;
        this.posicion = c.getPosicion();
        this.casilla.addAvatar(this);
    }
    public  abstract void moverEnAvanzado(List<Casilla> casillas, boolean debug) throws MonopolyException;
    public abstract void siguienteTurno();
    public void asignarJugador(Jugador j){
        this.jugador=j;
    }
    public void cambiarModo(){
        this.enModoAavanzado=!this.enModoAavanzado;
    }

}

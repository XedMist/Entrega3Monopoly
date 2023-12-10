package monopoly_avatar;

import monopoly_casilla.Casilla;
import monopoly_casilla.Salida;
import monopoly_core.Juego;
import monopoly_exceptions.MonopolyException;

import java.util.List;
public class Coche extends Avatar{

    private int tiradasRestantes;
    private int turnosPenalizacion;
    private boolean puedeComprar;

    public Coche(char id, Salida salida){
        super(id,salida);
        this.tiradasRestantes = -1;
        this.puedeComprar = true;
        this.turnosPenalizacion = 0;
    }
    @Override
    public void moverEnAvanzado(List<Casilla> casillas, boolean debug) throws MonopolyException {
        if(!this.getJugador().tieneAccion()){
            Juego.consola.imprimir("El jugador " + this.getJugador().getNombre() + " ya ha lanzado los dados este turno.");
            return;
        }
        if(tiradasRestantes == -1){
           this.tiradasRestantes = 4; 
        }


        int tirada = debug ? Juego.dados.debugLanzar() : Juego.dados.lanzar();
        this.tiradasRestantes--;
        this.setEnMovimiento(true);
        Casilla casillaActual = this.getCasilla();
        Casilla casillaNueva = null;
        if(tirada <= 4){
            int posicionActual = casillas.indexOf(casillaActual);
            int posicionNueva = posicionActual - tirada;
            if(posicionNueva < 0){
                this.getJugador().decrementarVueltas();
                this.getJugador().pagar(Juego.banca,Juego.salida.getPremio());
                posicionNueva = casillas.size() + posicionNueva;
                casillaNueva = casillas.get(posicionNueva);
            }else {
                casillaNueva = casillas.get(posicionNueva);
            }
            this.tiradasRestantes = 0;
            this.turnosPenalizacion = 3;
            Juego.consola.imprimir("El jugador %s ha sacado %d y se ha movido a la casilla %s. Tiene una penalización de 2 turnos sin poder lanzar los dados.\n"
                    .formatted(this.getJugador().getNombre(),tirada,casillaNueva.getNombre()));
        }else { 
            int posicionActual = casillas.indexOf(casillaActual);
            int posicionNueva = posicionActual + tirada;
            if(posicionNueva >= casillas.size()){
                this.getJugador().incrementarVueltas();
                this.getJugador().incrementarPasarPorCasillaDeSalida(Juego.salida.getPremio());
                posicionNueva = posicionNueva - casillas.size();
                casillaNueva = casillas.get(posicionNueva);
                this.getJugador().cobrar(Juego.salida.getPremio());
            }else {
                casillaNueva = casillas.get(posicionNueva);
            }
            Juego.consola.imprimir("El jugador %s ha sacado %d y se ha movido a la casilla %s.\n"
                    .formatted(this.getJugador().getNombre(),tirada,casillaNueva.getNombre()));
        }
        this.moverEnBasico(casillaNueva);
        if(tiradasRestantes == 0){
            this.getJugador().setAccion(false);
            this.setEnMovimiento(false);
            Juego.consola.imprimir("El jugador %s ha terminado su movimiento especial.\n".formatted(this.getJugador().getNombre()));
            return;
        }
    }

    public boolean puedeComprar(){
        return this.puedeComprar;
    }
    public boolean setPuedeComprar(boolean puedeComprar){
        this.puedeComprar = puedeComprar;
        return this.puedeComprar;
    }

    public void siguienteTurno(){
        if(this.turnosPenalizacion > 0){
            this.turnosPenalizacion--;
            this.getJugador().setAccion(false);
        }
        this.puedeComprar = true;
        this.setEnModoAvanzado(false);
    }

    public boolean penalizado(){
        return this.turnosPenalizacion > 0;
    }


}

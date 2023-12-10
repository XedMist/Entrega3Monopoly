package monopoly_avatar;

import monopoly_casilla.Casilla;
import monopoly_casilla.Salida;
import monopoly_core.Juego;
import monopoly_exceptions.MonopolyException;

import java.util.ArrayList;
import java.util.List;

public class Pelota extends Avatar{
    
    private List<Integer> movimientosRestantes;

    public Pelota(char id, Salida s){
        super(id,s);

    }
    @Override
    public void moverEnAvanzado(List<Casilla> casillas, boolean debug) throws MonopolyException {
        if(!this.getJugador().tieneAccion()){
            Juego.consola.imprimir("El jugador " + this.getJugador().getNombre() + " ya ha lanzado los dados este turno.\n");
            return;
        }
        if(this.movimientosRestantes == null){

            this.movimientosRestantes = new ArrayList<>();
            int tirada = debug ? Juego.dados.debugLanzar() : Juego.dados.lanzar();
            int tiradaOriginal = tirada;
            if(tirada > 4){
                this.movimientosRestantes.add(5);
                tirada -= 5;
                while(tirada > 1){
                    this.movimientosRestantes.add(2);
                    tirada -= 2;
                }
                if(tirada == 1){
                    this.movimientosRestantes.add(1);
                }
            }else {
                this.movimientosRestantes.add(-tirada);
            }

            Juego.consola.imprimir("El jugador %s ha sacado %d y tiene %d tiradas restantes.\n"
                    .formatted(this.getJugador().getNombre(),tiradaOriginal,this.movimientosRestantes.size()));
        }
        this.setEnMovimiento(true);
        Casilla casillaActual = this.getCasilla();
        Casilla casillaNueva = null;

        int posicionActual = casillas.indexOf(casillaActual);
        int posicionNueva = posicionActual + this.movimientosRestantes.get(0);
        if(posicionNueva >= casillas.size()){
            this.getJugador().incrementarVueltas();
            this.getJugador().incrementarPasarPorCasillaDeSalida(Juego.salida.getPremio());
            posicionNueva = posicionNueva - casillas.size();
            casillaNueva = casillas.get(posicionNueva);
            this.getJugador().cobrar(Juego.salida.getPremio());
        }
        else if(posicionNueva < 0){
            this.getJugador().decrementarVueltas();
            posicionNueva = casillas.size() + posicionNueva;
            casillaNueva = casillas.get(posicionNueva);
            this.getJugador().pagar(Juego.banca, Juego.salida.getPremio());
        }
        else {
            casillaNueva = casillas.get(posicionNueva);
        }
        this.movimientosRestantes.remove(0);
        Juego.consola.imprimir("El jugador %s se ha movido a la casilla %s.\n"
                .formatted(this.getJugador().getNombre(),casillaNueva.getNombre()));
        this.moverEnBasico(casillaNueva);
        if(this.movimientosRestantes.size() == 0){
            this.getJugador().setAccion(false);
            this.setEnMovimiento(false);
            Juego.consola.imprimir("El jugador %s ha terminado su movimiento especial.\n".formatted(this.getJugador().getNombre()));
        }
    }


    public void siguienteTurno(){
        this.movimientosRestantes = null;
        this.setEnMovimiento(false);
        this.setEnModoAvanzado(false);
    }
}

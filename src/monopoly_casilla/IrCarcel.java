package monopoly_casilla;

import monopoly_avatar.Avatar;
import monopoly_core.Juego;

public class IrCarcel extends Especial{
    private Carcel carcel;
    public IrCarcel(String nombre, int posicion){
        super(nombre,posicion);
    }
    
    public void setCarcel(Carcel carcel){
        this.carcel = carcel;
    }
    @Override
    public void caer(Avatar av){
        Juego.consola.imprimir("Se coloca en la cárcel.");
        this.carcel.encarcelar(av.getJugador());
        av.moverACasilla(this.carcel);

        av.getJugador().incrementarVecesEnLaCarcel();
    }    
} 

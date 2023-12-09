package monopoly_casilla;

import monopoly_avatar.Avatar;
import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public class Impuesto extends Casilla{
    private float impuesto;
    public Impuesto(String nombre, int posicion){
        super(nombre,posicion);
    }
    public void inicializarImpuesto(float impuesto){
        this.impuesto = impuesto;
    }
    @Override
    public void caer(Avatar av) throws MonopolyException {
        Jugador j = av.getJugador();
        Juego.consola.imprimir("%s cae en %s y paga %.2f€\n".formatted(j.getNombre(),this.getNombre(),this.impuesto));
        j.pagar(Juego.banca,this.impuesto);
        j.incrementarPagoTasasEImpuestos(this.impuesto);
    }

}

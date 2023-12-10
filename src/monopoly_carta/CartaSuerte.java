package monopoly_carta;
import java.util.List;

import monopoly_casilla.Propiedad;
import monopoly_casilla.Solar;
import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_edificios.Casa;
import monopoly_edificios.Hotel;
import monopoly_edificios.Piscina;
import monopoly_edificios.PistaDeporte;
import monopoly_exceptions.MonopolyException;
public class CartaSuerte extends Carta{
    public enum TipoSuerte{
        GanarLoteria,
        VenderBillete,
        SerPresidente,
        AumentarImpuesto,
        MultarMovil,
        BeneficiarAcciones
    }

    private TipoSuerte accion;

    public CartaSuerte(String descripcion,TipoSuerte accion){
        super(descripcion);
        this.accion = accion;
    }
    public void accion(Jugador j, List<Jugador> jugadores) throws MonopolyException{
        switch(this.accion){
            case GanarLoteria:
                Juego.consola.imprimir("¡Has ganado el bote de la loteria! Recibe 1000000€\n");
                j.incrementarPremiosInversionesOBote(1_000_000);
                j.cobrar(1_000_000);
                break;
            case VenderBillete:
                Juego.consola.imprimir("Vendes tu billete de avión a Cádiz en una subasta por Internet. Recibe 500000€\n");
                j.incrementarPremiosInversionesOBote(500_000);
                j.cobrar(500_000);
                break;
            case MultarMovil:
                Juego.consola.imprimir("Te multan por usar el móvil mientras conduces. Paga 250000€\n");
                j.incrementarPagoTasasEImpuestos(250_000);
                j.pagar(Juego.banca,250_000);
                break;
            case BeneficiarAcciones:
                Juego.consola.imprimir("Beneficio por la venta de tus acciones. Recibe 1500000€\n");
                j.incrementarPremiosInversionesOBote(1_500_000);
                j.cobrar(1_500_000);
                break;
            case SerPresidente:
                int N = jugadores.size()-1;
                float total = N*250_000;
                if(!j.puedePagar(total)){
                    j.sinDinero(Juego.banca,total);
                }
                for(Jugador jugador : jugadores){
                    if(jugador != j){
                        j.pagar(jugador,250_000);
                    }
                }
                Juego.consola.imprimir("Eres elegido presidente de la comunidad. Paga 250000€ a cada jugador\n");
                break;
            case AumentarImpuesto:
                float total2 = 0;
                for(Propiedad p : j.getPropiedades()){
                    if(p instanceof Solar s){
                        total2 += s.nTipoEdificio(Casa.class) * 400_000;
                        total2 += s.nTipoEdificio(Hotel.class) * 1_150_000;
                        total2 += s.nTipoEdificio(Piscina.class) * 200_000;
                        total2 += s.nTipoEdificio(PistaDeporte.class) * 750_000;
                    }
                }
                j.incrementarPagoTasasEImpuestos(total2);
                j.pagar(Juego.banca,total2);
                break;
        }
    }
}

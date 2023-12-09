package monopoly_carta;
import java.util.List;

import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;
public class CartaCajaComunidad extends Carta{
    public enum TipoComunidad{
        AlquilarVilla,
        AlquilarJet,
        PagarViaje,
        DevolverHacienda,
        BeneficiarCompanhia,
        PagarBalneario
    }

    private TipoComunidad accion;

    public CartaCajaComunidad(String descripcion,TipoComunidad accion){
        super(descripcion);
        this.accion = accion;
    }
    public void accion(Jugador j, List<Jugador> jugadores) throws MonopolyException{
        switch(this.accion){
            case AlquilarJet:
                Juego.consola.imprimir("Recibe 1000000 de beneficios por alquilar los servicios de tu jet privado.\n");
                j.cobrar(1_000_000);
                break;
            case PagarBalneario:
                Juego.consola.imprimir("Paga 500000€ por un fin de semana en un balneario de 5 estrellas.\n");
                j.pagar(Juego.banca,500_000);
                break;
            case DevolverHacienda:
                Juego.consola.imprimir("Devolución de Hacienda. Recibe 500000€.\n");
                j.cobrar(500_000);
                break;
            case BeneficiarCompanhia:
                Juego.consola.imprimir("Tu compañía de Internet obtiene beneficios. Recibe 2000000€.\n");
                j.cobrar(2_000_000);
                break;
            case PagarViaje:
                Juego.consola.imprimir("Paga 1000000€ por invitar a todos tus amigos a un viaje a León.\n");
                j.pagar(Juego.banca,1_000_000);
                break;
            case AlquilarVilla:
                int N = jugadores.size()-1;
                float total = N*200_000;
                if(!j.puedePagar(total)){
                    j.sinDinero(Juego.banca,total);
                }
                for(Jugador jugador : jugadores){
                    if(jugador != j){
                        jugador.cobrar(200_000);
                        j.pagar(jugador,200_000);
                    }
                }
                Juego.consola.imprimir("Alquila una villa en la playa para pasar el verano. Paga 200000€ a cada jugador.\n");
                break;
        }
    }
}

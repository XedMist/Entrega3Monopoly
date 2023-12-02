import java.util.List;
public class Impuesto extends Casilla{
    private float impuesto;
    public Impuesto(String nombre, int posicion){
        super(nombre,posicion);
    }
    public void inicializarImpuesto(float impuesto){
        this.impuesto = impuesto;
    }
    @Override
    public void caer(Avatar av){
        Jugador j = av.getJugador();
        j.pagar(Juego.banca,impuesto);
    }

}

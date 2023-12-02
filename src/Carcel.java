import java.util.ArrayList;
import java.util.List;

public class Carcel extends Especial{
    private List<Jugador> encarcelados;
    public Carcel(String nombre,int posicion){
        super(nombre,posicion);
        this.encarcelados = new ArrayList<>();
    }

}

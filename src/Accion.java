import java.util.List;

public class Accion extends Casilla{
    public Accion(String nombre,int posicion){
        super(nombre,posicion);
    }
    public void accion(Jugador j){

    }
    @Override
    public void caer(Avatar av){
        this.accion(av.getJugador());
    }
    @Override
    public String toString(int longitud,int numMaximoAvatares){
            String nameSpan = " ".repeat(longitud - this.getNombre().length());
        String avatarSpan = numMaximoAvatares == 0 ? "" : " ".repeat(numMaximoAvatares + 1);
        List<Avatar> avatares = super.getAvatares();
        if (!avatares.isEmpty()){
            avatarSpan = "&";
            for(Avatar av: avatares){
                avatarSpan += av;
            }
        }
        return new ColorString(this.getNombre() + nameSpan + " " + avatarSpan,ColorString.Color.Blanco) + "|";
    }
} 

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public abstract class Casilla {
    private int posicion;
    private String nombre;
    private Map<String,Integer> visitas;
    private List<Avatar> avatares; 
    
    public Casilla(String nombre,int posicion){
        this.posicion = posicion;
        this.nombre = nombre;
        this.avatares = new ArrayList<>();
    }

    public String getNombre(){
        return this.nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public boolean estaAvatar(Avatar avatar){
        return avatares.contains(avatar);
    }
    public int frecuenciaVisita(String nombreJugador){
        return visitas.get(nombreJugador);
    }
    public int getNumeroAvatares(){
        return this.avatares.size();
    }
    public List<Avatar> getAvatares(){
        return this.avatares;
    }
    public void addAvatar(Avatar avatar){
        this.avatares.add(avatar);
    }
    public void eliminarAvatar(Avatar avatar){
        this.avatares.remove(avatar);
    }
    public int getPosicion(){
        return this.posicion;
    }
    public String toString(int longitud,int numMaximoAvatares){
            String nameSpan = " ".repeat(longitud - this.getNombre().length());
        String avatarSpan = numMaximoAvatares == 0 ? "" : " ".repeat(numMaximoAvatares + 1);
        List<Avatar> avatares = this.getAvatares();
        if (!avatares.isEmpty()){
            avatarSpan = "&";
            for(Avatar av: avatares){
                avatarSpan += av.getId();
            }
        }
        return new ColorString(this.getNombre() + nameSpan + " " + avatarSpan,ColorString.Color.Blanco) + "|";
    }

    public abstract void caer(Avatar av);


}

package monopoly_casilla;

import monopoly_avatar.Avatar;
import monopoly_core.ColorString;
import monopoly_exceptions.MonopolyException;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
public abstract class Casilla {
    private int posicion;
    private String nombre;
    private Map<String,Integer> visitas;
    private List<Avatar> avatares;
    
    public Casilla(String nombre,int posicion){
        this.posicion = posicion;
        this.nombre = nombre;
        this.avatares = new ArrayList<>();
        this.visitas = new HashMap<>(); 
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
        if(!visitas.containsKey(nombreJugador)){
            return 0;
        }
        return visitas.get(nombreJugador);
    }

    public int frecuenciaVisita(){
        int total = 0;
        for(int i: visitas.values()){
            total += i;
        }
        return total;
    }

    public void addVisita(String nombreJugador){
        if(!visitas.containsKey(nombreJugador)){
            visitas.put(nombreJugador,1);
        }else{
            visitas.put(nombreJugador,visitas.get(nombreJugador) + 1);
        }
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
        return new ColorString(this.getNombre() + nameSpan + " " + avatarSpan, ColorString.Color.Blanco) + "|";
    }

    public abstract void caer(Avatar av) throws MonopolyException;


}

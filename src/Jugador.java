import java.util.ArrayList;
import java.util.List;

public class Jugador{
    private String nombre;
    private float fortuna;
    private List<Propiedad> propiedades;
    private List<Propiedad> hipotecas;
    private Avatar avatar;
    
    public Jugador(){
        this.nombre = "Banca";
        this.propiedades = new ArrayList<>();
    }
    public Jugador(String nombre,Avatar avatar){
        this.nombre = nombre;
        this.avatar = avatar;
        this.propiedades = new ArrayList<>();
        this.hipotecas = new ArrayList<>();
    }

    public String getNombre(){
        return this.nombre;
    }
    public void cobrar(float cantidad){
        this.fortuna += cantidad;
    }
    public void pagar(Jugador destinatario,float cantidad){
        this.fortuna -= cantidad;
        destinatario.cobrar(cantidad);
    }
    public Avatar getAvatar(){
        return this.avatar;
    }
    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if( o == null || o.getClass() != this.getClass()){
            return false;
        }
        final Jugador other = (Jugador) o;
        if(!this.nombre.equals(other.nombre)){
            return false;
        }
        return true;
    }
}



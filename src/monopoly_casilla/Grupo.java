package monopoly_casilla;
import java.util.ArrayList;
import java.util.List;

import monopoly_core.Jugador;
import monopoly_core.ColorString.Color;

public class Grupo{
    //La lista(su contenido si) y el color no cambian
    private final List<Propiedad> propiedades;
    private final Color color;
    private int numCasillas;
    public Grupo(Color color){
        this.propiedades = new ArrayList<Propiedad>();
        this.color = color;
    }
    public void addPropiedad(Propiedad propiedad){
        this.propiedades.add(propiedad);
        this.numCasillas++;
    }
    public List<Propiedad> getPropiedades(){
        return this.propiedades;
    }
    public Color getColor(){
        return this.color;
    }
    public int getNumCasillas(){
        return this.numCasillas;
    }

    public boolean esPropietarioGrupo(Jugador jugador){
        for(Propiedad p : this.propiedades){
            if(p.getPropietario() != jugador){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.color.toString();
    }

}

package monopoly_edificios;

import monopoly_casilla.Solar;
import monopoly_core.ColorString;
import monopoly_core.Jugador;

public abstract class Edificio{
    
    private String nombre;
    private Solar casilla;
    
    public Edificio(String nombre, Solar casilla){
        this.nombre = nombre;
        this.casilla = casilla;
    }

    public String getNombre(){
        return this.nombre;
    }
    public Solar getCasilla(){
        return this.casilla;
    }
    
    public abstract float valorInicial();
    public abstract float alquiler();
    public abstract boolean condicion();

    @Override
    public String toString() {
        Jugador j = this.casilla.getPropietario();
        ColorString.Color g = this.casilla.getGrupo();
        return """
        {
            id: %s,
            propietario: %s,
            casilla: %s,
            grupo: %s,
            coste: %.2f
        }""".formatted(this.nombre, j.getNombre(), this.casilla.getNombre(), g, this.valorInicial());
    }
}

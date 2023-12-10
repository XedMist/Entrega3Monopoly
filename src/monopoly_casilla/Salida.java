package monopoly_casilla;

public class Salida extends Especial{
    private float premio;
    public Salida(String nombre, int posicion){
        super(nombre,posicion);
    }
    public void setPremio(float premio){
        this.premio = premio;
    }
    public float getPremio(){
        return this.premio;
    }

    @Override
    public String toString() {
        return """
        {
            tipo: salida,
            premio: %.2f
        }""".formatted(this.premio);
    }
}

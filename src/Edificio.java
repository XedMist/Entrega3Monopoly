public abstract class Edificio{
    private String nombre;
    private float valorInicial;
    private Solar casilla;
    public String getNombre(){
        return this.nombre;
    }
    public float getValorInicial(){
        return this.valorInicial;
    }

    public abstract float getAlquiler();

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
            cost: %s
        }""".formatted(this.nombre, j.getNombre(), this.casilla.getNombre(), g, this.valorInicial);
    }
}

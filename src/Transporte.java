public class Transporte extends Propiedad{
    private float operacionTransporte;
    public Transporte(String nombre,int posicion){
        super(nombre,posicion,0);
    }
    @Override
    public float alquiler(){
        return 0.1f*this.valor();
    }
    public void inicializar(float valor){
        this.setValorInicial(valor);
        this.operacionTransporte = valor;
    }
    @Override
    public String toString(){
        return """
        {
            tipo: Transporte,
            propietario: %s,
            valor: %.2f,
            alquiler: %.2f
        }""".formatted(this.getPropietario().getNombre(),this.valor(),this.alquiler());
    }

}

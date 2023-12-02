public class Servicios extends Propiedad{
    private float factorServicio;
    public Servicios(String nombre,int posicion){
        super(nombre,posicion,0);
    }
    @Override
    public float alquiler(){
        return 0.1f*this.valor();
    }
    public void inicializar(float valor){
        this.setValorInicial(0.75f*valor);
        this.factorServicio = valor/200;
    }
    @Override
    public String toString(){
        return """
        {
            tipo:Servicio,
            propietario:%s,
            valor: %.2f
        }""".formatted(this.getPropietario().getNombre(),this.valor());
    }
}

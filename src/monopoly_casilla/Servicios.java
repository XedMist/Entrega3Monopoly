package monopoly_casilla;

import monopoly_core.Juego;
import monopoly_core.Jugador;
public class Servicios extends Propiedad{
    private float factorServicio;
    public Servicios(String nombre,int posicion){
        super(nombre,posicion,0);
    }
    @Override
    public float alquiler(){
        int multiplicador = this.getPropietario().getNumServicios() == 1 ? 4 : 10;
        return this.factorServicio * multiplicador* Juego.dados.getValor();
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

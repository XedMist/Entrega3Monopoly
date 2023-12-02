import java.util.List;

public abstract class Propiedad extends Casilla{
    private Jugador propietario;
    private float valorInicial;
    private float valor;
    private float alquiler;


    public Propiedad(String nombre,int posicion,float valorInicial){
        super(nombre,posicion);
        this.valorInicial=valorInicial;
        this.valor=valorInicial;
        this.alquiler=0.1f*this.valor;
    }

    public float valor(){
        return this.valor;
    }
    public float valorInicial(){
        return this.valorInicial;
    }
    public abstract float alquiler();
    public void comprar(Jugador j){
        //TODO
    }
    public float getAlquiler(){
        return this.alquiler;
    }
    public Jugador getPropietario(){
        return this.propietario;
    }
    public void setValorInicial(float valorInicial){
        this.valorInicial=valorInicial;
        this.valor=valorInicial;
    }
    public void setPropietario(Jugador propietario){
        this.propietario=propietario;
    }

    @Override
    public void caer(Avatar av){
        Jugador propietario = this.getPropietario();
        if(propietario.equals(Juego.banca)){
            return;
        }
        if(av.getJugador().equals(propietario)){
            return;
        }
        float alquiler = this.alquiler();
        av.getJugador().pagar(propietario,alquiler);
    }

}

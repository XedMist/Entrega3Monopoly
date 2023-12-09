package monopoly_casilla;

import java.util.Map;

import monopoly_avatar.Avatar;
import monopoly_avatar.Coche;
import monopoly_core.Juego;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public abstract class Propiedad extends Casilla{
    private Jugador propietario;
    private float valorInicial;
    private float valor;
    private float alquiler;


    private boolean hipotecada;
    //Estadistica
    private float beneficios;

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

    public void setHipotecada(boolean hipotecada){
        this.hipotecada=hipotecada;
    }
    public boolean getHipotecada(){
        return this.hipotecada;
    }

    public abstract float alquiler();
    public void comprar(Avatar av) throws MonopolyException {
        Jugador jugador = av.getJugador();
        if(av.getPosicion() != this.getPosicion()){
            Juego.consola.imprimir("%s no puede comprar la casilla porque su avatar no está en ella\n".formatted(jugador.getNombre()));
            return;
        }
        if(!this.propietario.equals(Juego.banca)){
            Juego.consola.imprimir("%s no puede comprar la casilla porque ya tiene propietario\n".formatted(jugador.getNombre()));
            return;
        }
        if(!jugador.puedePagar(this.valor)){
            Juego.consola.imprimir("%s no puede comprar la casilla porque no tiene suficiente dinero\n".formatted(jugador.getNombre()));
            return;
        }
        if(av instanceof Coche c && c.getEnModoAvanzado() && !c.puedeComprar()){
            Juego.consola.imprimir("%s no puede comprar la casilla porque ya ha comprado una durante el movimiento avanzado.\n".formatted(jugador.getNombre()));
            return;
        }

        Juego.banca.addBote(this.valor);
        jugador.pagar(Juego.banca,this.valor);
        jugador.incrementarDineroInvertido((this.valor));
        this.propietario=jugador;
        jugador.addPropiedad(this);
        if (av instanceof Coche c && c.getEnModoAvanzado()){
            ((Coche) av).setPuedeComprar(false);
        }
        Juego.consola.imprimir("%s compra la casilla %s por %.2f€\n".formatted(jugador.getNombre(),this.getNombre(),this.valor));
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
    public float getBeneficios(){
        return this.beneficios;
    }
    @Override
    public void caer(Avatar av) throws MonopolyException {
        if (this.getPropietario().equals(Juego.banca) || this.hipotecada){
            return;
        }
        if (this.getPropietario().equals(av.getJugador())){
            return;
        }
        if(av.getJugador().esExentoDeAlquiler(this.getNombre())){
            return;
        }

        float alquiler = this.alquiler();
        av.getJugador().pagar(this.getPropietario(),alquiler);
        av.getJugador().incrementarPagoDeAlquileres(alquiler);
        this.getPropietario().incrementarCobroDeAlquileres(alquiler);
        Juego.consola.imprimir("El jugador %s ha pagado %.2f al jugador %s por caer en %s.\n".formatted(av.getJugador().getNombre(),alquiler,this.getPropietario().getNombre(),this.getNombre()));
        this.beneficios+=alquiler;
    }
}

package monopoly_trato;

import monopoly_casilla.Propiedad;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public class TratoPPC extends Trato{
    private Jugador proponente;
    private Propiedad propiedad1;

    private Propiedad propiedad2;
    private float cantidad;
    public TratoPPC(Jugador jugador1, Jugador jugador2, String identificador, Propiedad propiedad1, Propiedad propiedad2,float cantidad,Jugador proponente) {
        super(jugador1, jugador2, identificador);
        this.propiedad1 = propiedad1;
        this.cantidad = cantidad;
        this.proponente = proponente;
        this.propiedad2 = propiedad2;
    }
    public Propiedad getPropiedad1() {
        return propiedad1;
    }
    public float getCantidad() {
        return cantidad;
    }
    @Override
    public void aceptar() throws MonopolyException {
        this.getJugador2().pagar(this.getJugador1(),this.cantidad);

        this.getJugador1().getPropiedades().remove(this.propiedad1);
        this.getJugador2().getPropiedades().remove(this.propiedad2);
        this.getJugador1().getPropiedades().add(this.propiedad2);
        this.getJugador2().getPropiedades().add(this.propiedad1);

        this.propiedad1.setPropietario(this.getJugador2());
        this.propiedad2.setPropietario(this.getJugador1());
    }
    @Override
    public boolean esProponente(Jugador jugador){
        return this.proponente.equals(jugador);
    }

    @Override
    public String toString() {
        return """
        {
            jugadorPropone: %s,
            trato: cambiar (%s, %s, %.2f€)
        }\n""".formatted(this.proponente.getNombre(),this.propiedad1.getNombre(),this.propiedad2.getNombre(),this.cantidad);
    }
}

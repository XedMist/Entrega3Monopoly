package monopoly_trato;

import monopoly_casilla.Propiedad;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public class TratoPC extends Trato{
    private final Propiedad propiedad1;
    private final Jugador proponente;
    private final float cantidad;
    public TratoPC(Jugador jugador1, Jugador jugador2, String identificador, Propiedad propiedad1, float cantidad,Jugador proponente) {
        super(jugador1, jugador2, identificador);
        this.propiedad1 = propiedad1;
        this.cantidad = cantidad;
        this.proponente = proponente;
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
        this.getJugador2().getPropiedades().add(this.propiedad1);
        this.propiedad1.setPropietario(this.getJugador2());
    }
    @Override
    public boolean esProponente(Jugador jugador){
        return this.proponente.equals(jugador);
    }

    @Override
    public String toString() {
        return """
        {
            id: %s,
            jugadorPropone: %s,
            trato: cambiar (%s, %.2f€)
        }\n""".formatted(this.getIdentificador(),this.getJugador1().getNombre(),this.propiedad1.getNombre(),this.cantidad);
    }
}

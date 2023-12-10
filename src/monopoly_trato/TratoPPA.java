package monopoly_trato;

import monopoly_casilla.Propiedad;
import monopoly_core.Jugador;
import monopoly_exceptions.MonopolyException;

public class TratoPPA extends Trato{
    private final Propiedad propiedad1;
    private final Propiedad propiedad2;
    private final Propiedad propiedad3;
    private int turnos;
    public TratoPPA(Jugador jugador1, Jugador jugador2, String identificador, Propiedad propiedad1, Propiedad propiedad2, Propiedad propiedad3, int turnos) {
        super(jugador1, jugador2, identificador);
        this.propiedad1 = propiedad1;
        this.propiedad2 = propiedad2;
        this.propiedad3 = propiedad3;
        this.turnos = turnos;
    }
    @Override
    // Jugador 1 da propiedad1 a jugador 2, jugador 2 da propiedad2 a jugador 1 y jugador1 no paga alquiler a jugador2 durante turnos turnos
    public void aceptar() throws MonopolyException {
        this.getJugador1().getPropiedades().remove(this.propiedad1);
        this.getJugador2().getPropiedades().remove(this.propiedad2);
        this.getJugador1().getPropiedades().add(this.propiedad2);
        this.getJugador2().getPropiedades().add(this.propiedad1);

        this.propiedad1.setPropietario(this.getJugador2());
        this.propiedad2.setPropietario(this.getJugador1());

        this.getJugador1().nuevoExentoDeAlquiler(propiedad3.getNombre(),this.turnos);
    }
    @Override
    public boolean esProponente(Jugador jugador){
        return this.getJugador1().equals(jugador);
    }

    @Override
    public String toString() {
        return """
        {
            id: %s,
            jugadorPropone: %s,
            trato: cambiar (%s, %s) y noalquiler(%s, %d)
        }\n""".formatted(this.getIdentificador(),this.getJugador1().getNombre(),this.propiedad1.getNombre(),this.propiedad2.getNombre(),this.propiedad3.getNombre(),this.turnos);
    }
}

package monopoly_trato;

import monopoly_casilla.Propiedad;
import monopoly_core.Jugador;

public class TratoPP extends Trato{
    private final Propiedad propiedad1;
    private final Propiedad propiedad2;
    public TratoPP(Jugador jugador1, Jugador jugador2, String identificador, Propiedad propiedad1, Propiedad propiedad2) {
        super(jugador1, jugador2, identificador);
        this.propiedad1 = propiedad1;
        this.propiedad2 = propiedad2;
    }
    public Propiedad getPropiedad1() {
        return propiedad1;
    }
    public Propiedad getPropiedad2() {
        return propiedad2;
    }
    @Override
    public void aceptar() {
        this.getJugador1().getPropiedades().remove(this.propiedad1);
        this.getJugador2().getPropiedades().remove(this.propiedad2);
        this.getJugador1().getPropiedades().add(this.propiedad2);
        this.getJugador2().getPropiedades().add(this.propiedad1);

        this.propiedad1.setPropietario(this.getJugador2());
        this.propiedad2.setPropietario(this.getJugador1());
    }

    @Override
    public String toString() {
        return """
        {
            id: %s,
            jugadorPropone: %s,
            trato: cambiar (%s, %s)
        }\n""".formatted(this.getIdentificador(),this.getJugador1().getNombre(),this.propiedad1.getNombre(),this.propiedad2.getNombre());
    }
}

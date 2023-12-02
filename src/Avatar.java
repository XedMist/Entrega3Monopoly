public /*abstract*/ class Avatar{
    private char id;
    private Casilla casilla;
    private Jugador jugador;
    private int posicion;

    public Avatar(char id,Salida s){
        this.id=id;
        this.casilla=s;
        this.posicion=s.getPosicion();
        this.casilla.addAvatar(this);
    }
    public char getId(){
        return this.id;
    }
    public int getPosicion(){
        return this.posicion;
    }
    public void moverEnBasico(Casilla c){
        this.casilla.eliminarAvatar(this);
        this.casilla=c;
        this.posicion=c.getPosicion();
        this.casilla.addAvatar(this);
        this.casilla.caer(this);
    }
    public Jugador getJugador(){
        return this.jugador;
    }
    public Casilla getCasilla(){
        return this.casilla;
    }
    //public abstract void moverEnAvanzado(int cantidad);
    public void asignarJugador(Jugador j){
        this.jugador=j;
    }
}

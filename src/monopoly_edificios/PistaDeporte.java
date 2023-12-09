package monopoly_edificios;

import monopoly_casilla.Solar;

public class PistaDeporte extends Edificio{
    public PistaDeporte(String nombre, Solar casilla){
        super(nombre,casilla);
    }

    @Override
    public float valorInicial() {
        return this.getCasilla().valorInicial() * 1.25f;
    }

    @Override
    public boolean condicion(){
        return true;
    }
    @Override
    public float alquiler() {
        return this.getCasilla().alquiler() * 25;
    }
}

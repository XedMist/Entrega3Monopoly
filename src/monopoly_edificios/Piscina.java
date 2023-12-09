package monopoly_edificios;

import monopoly_casilla.Solar;

public class Piscina extends Edificio{
    public Piscina(String nombre, Solar casilla){
        super(nombre,casilla);
    }

    @Override
    public float valorInicial() {
        return this.getCasilla().valorInicial() * 0.4f;
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

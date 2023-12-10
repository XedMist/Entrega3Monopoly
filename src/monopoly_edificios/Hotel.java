package monopoly_edificios;


import monopoly_casilla.Solar;

public class Hotel extends Edificio{
    public Hotel(String nombre, Solar casilla){
        super(nombre,casilla);
    }

    @Override
    public float valorInicial() {
        return this.getCasilla().valorInicial() * 0.6f;
    }

    @Override
    public boolean condicion(){
        return this.getCasilla().nTipoEdificio(Casa.class) == 4;
    }    
    @Override
    public float alquiler() {
        return this.getCasilla().getAlquiler() * 70;
    }
}

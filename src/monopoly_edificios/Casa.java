package monopoly_edificios;


import monopoly_casilla.Solar;

public class Casa extends Edificio{

    public Casa(String nombre, Solar casilla){
        super(nombre,casilla);
    }

    @Override
    public float valorInicial() {
        return this.getCasilla().valorInicial() * 0.6f;
    }
    @Override
    public boolean condicion(){
        return this.getCasilla().nTipoEdificio(this.getClass()) < 4;
    }

    @Override
    public float alquiler() {
        int n = this.getCasilla().nTipoEdificio(this.getClass());
        switch(n){
            case 1:
                return this.getCasilla().getAlquiler() * 5;
            case 2:
                return this.getCasilla().getAlquiler() * 15;
            case 3:
                return this.getCasilla().getAlquiler() * 35;
            case 4:
                return this.getCasilla().getAlquiler() * 50;
        }
        return 0;
    }

}

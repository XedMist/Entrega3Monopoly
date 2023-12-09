package monopoly_casilla;

import monopoly_avatar.Avatar;
import monopoly_core.ColorString;
import monopoly_core.Juego;
import monopoly_edificios.Casa;
import monopoly_edificios.Edificio;
import monopoly_edificios.Hotel;
import monopoly_exceptions.MonopolyException;

import java.util.ArrayList;
import java.util.List;

public class Solar extends Propiedad{
    private ColorString.Color grupo;
    private List<Edificio> edificios;
    public Solar(String nombre, int posicion, float valorInicial, ColorString.Color grupo){
        super(nombre,posicion,valorInicial);
        this.grupo=grupo;
        this.edificios = new ArrayList<>();
    }


    public int nTipoEdificio(Class<? extends Edificio> tipo){
        int n = 0;
        for(Edificio e : this.edificios){
            if(tipo.isInstance(e)){
                n++;
            }
        }
        return n;
    }

    public List<Edificio> getEdificios(){
        return this.edificios;
    }
    public void vender(Class<? extends Edificio> tipo, int n){
        int i = 0;
        float valor =0;
        while(i < n){
            for(Edificio e: this.edificios){
                if(tipo.isInstance(e)){
                    valor += e.valorInicial()/2;
                    this.edificios.remove(e);
                    i++;
                    break;
                }
            }
        }
        this.getPropietario().cobrar(valor);
        Juego.consola.imprimir("Cobra %.2f€.\n".formatted(valor));
    }

    @Override
    public float alquiler(){
        float alquilerEdificios = 0;
        boolean check = true;
        for(Edificio e: this.edificios){
            if(e instanceof Casa && check){
                alquilerEdificios += e.alquiler();
                check = false;
            }
            alquilerEdificios += e.alquiler();
        }
        int multiplicador = !this.getPropietario().equals(Juego.banca) && this.getPropietario().tieneTodosLosSolares(this.grupo) ? 2 : 1;
        return 0.1f*this.valor() * multiplicador + alquilerEdificios;
    }
    public ColorString.Color getGrupo(){
        return this.grupo;
    }
    public void edificar(Edificio e) throws MonopolyException {
        if(!e.condicion()){
            Juego.consola.imprimir("%s no puede edificar porque no cumple la condición\n".formatted(this.getPropietario().getNombre()));
            return;
        }
        if(!this.getPropietario().puedePagar(e.valorInicial())){
            Juego.consola.imprimir("%s no puede edificar porque no tiene suficiente dinero\n".formatted(this.getPropietario().getNombre()));
            return;
        }
        this.getPropietario().pagar(Juego.banca,e.valorInicial());
        this.getPropietario().incrementarDineroInvertido(e.valorInicial());
        this.edificios.add(e);
        if(e instanceof Hotel){
            for(int i = 0; i < 4; i++){
                this.edificios.removeIf(ed -> ed instanceof Casa);
            }
        }
        Juego.consola.imprimir("%s edifica %s en %s por %.2f€\n".formatted(this.getPropietario().getNombre(),e.getNombre(),this.getNombre(),e.valorInicial()));
        
    }

    @Override
    public String toString(int longitud,int numMaximoAvatares){
            String nameSpan = " ".repeat(longitud - this.getNombre().length());
        String avatarSpan = numMaximoAvatares == 0 ? "" : " ".repeat(numMaximoAvatares + 1);
        List<Avatar> avatares = super.getAvatares();
        if (!avatares.isEmpty()){
            avatarSpan = "&";
            for(Avatar av: avatares){
                avatarSpan += av.getId();
            }
        }
        return new ColorString(this.getNombre() + nameSpan + " " + avatarSpan,this.grupo) + "|";
    }
    @Override
    public String toString(){
        return """
        {
            tipo: Solar,
            grupo: %s,
            propietario: %s,
            valor: %.2f,
            alquiler: %.2f,
            edificios: %s,
        }
        """.formatted(this.grupo,this.getPropietario().getNombre(),this.valor(),this.alquiler(),this.edificios);
    }
}

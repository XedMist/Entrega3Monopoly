import java.util.ArrayList;
import java.util.List;

public class Solar extends Propiedad{
    private ColorString.Color grupo;
    private List<Edificio> edificios;

    public Solar(String nombre,int posicion,float valorInicial,ColorString.Color grupo){
        super(nombre,posicion,valorInicial);
        this.grupo=grupo;
        this.edificios = new ArrayList<>();
    }

    @Override
    public float alquiler(){
        float alquilerEdificios = 0;
        for(Edificio e: this.edificios){
            alquilerEdificios += e.getAlquiler();
        }
        //TODO Multiplicador de tener todos los solares
        return this.getAlquiler() + alquilerEdificios;
    }
    public ColorString.Color getGrupo(){
        return this.grupo;
    }
    public void edificar(){
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

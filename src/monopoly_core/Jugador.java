package monopoly_core;

import monopoly_avatar.Avatar;
import monopoly_casilla.Propiedad;
import monopoly_casilla.Servicios;
import monopoly_casilla.Solar;
import monopoly_casilla.Transporte;
import monopoly_edificios.Edificio;
import monopoly_exceptions.BancarrotaException;
import monopoly_exceptions.MonopolyException;
import monopoly_trato.Trato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jugador{
    private final String nombre;
    private float fortuna;
    private final List<Propiedad> propiedades;
    private List<Propiedad> hipotecas;
    private Avatar avatar;
    private boolean accion;
    private boolean activo;    
    private float bote;
    private List<Trato> tratos;
    
    //Estadisticas
    private float dineroInvertido;
    private float pagoTasasEImpuestos;
    private float pagoDeAlquileres;
    private float cobroDeAlquileres;
    private float pasarPorCasillaDeSalida;
    private float premiosInversionesOBote;
    private int vecesEnLaCarcel;

    private Map<String,Integer> exentosDeAlquilerEn;
    private int vueltas;
    private int vecesDados;

    public Jugador(){
        this.nombre = "Banca";
        this.propiedades = new ArrayList<>();
    }
    public Jugador(String nombre,Avatar avatar){
        this.nombre = nombre;
        this.avatar = avatar;
        this.propiedades = new ArrayList<>();
        this.hipotecas = new ArrayList<>();
        this.accion = true;
        this.activo = true;
        this.tratos = new ArrayList<>();
        this.exentosDeAlquilerEn = new HashMap<>();
    }


    /////////////////////////////
    //Estadisticas
    /////////////////////////////

    public void incrementarDineroInvertido(float cantidad){
        this.dineroInvertido += cantidad;
    }
    public void incrementarPagoTasasEImpuestos(float cantidad){
        this.pagoTasasEImpuestos += cantidad;
    }
    public void incrementarPagoDeAlquileres(float cantidad){
        this.pagoDeAlquileres += cantidad;
    }
    public void incrementarCobroDeAlquileres(float cantidad){
        this.cobroDeAlquileres += cantidad;
    }
    public void incrementarPasarPorCasillaDeSalida(float cantidad){
        this.pasarPorCasillaDeSalida+= cantidad;
    }
    public void incrementarPremiosInversionesOBote(float cantidad){
        this.premiosInversionesOBote += cantidad;
    }
    public void incrementarVecesEnLaCarcel(){
        this.vecesEnLaCarcel++;
    }

    public String getNombre(){
        return this.nombre;
    }
    public boolean activo(){
        return this.activo;
    }
    public boolean puedePagar(float cantidad){
        return this.fortuna >= cantidad;
    }
    public void cobrar(float cantidad){
        if(this.equals(Juego.banca)){
            this.addBote(cantidad);
            return;
        }
        this.fortuna += cantidad;
    }
    public void pagar(Jugador destinatario,float cantidad) throws BancarrotaException {
        if(!this.puedePagar(cantidad)){
            Juego.consola.imprimir("No tienes suficiente dinero para pagar\n");
            this.sinDinero(destinatario,cantidad);
        }
        this.fortuna -= cantidad;
        destinatario.cobrar(cantidad);
    }
    
    public int getVueltas(){
        return this.vueltas;
    }
    public void incrementarVueltas(){
        this.vueltas++;
    }
    public void decrementarVueltas(){
        this.vueltas--;
    }
    public void setVueltas(int vueltas){
        this.vueltas = vueltas;
    }
    public void nuevoTrato(Trato trato){
        this.tratos.add(trato);
    }
    public void aceptarTrato(String identificador) throws MonopolyException{
        Trato trato = null;
        for(Trato t: this.tratos){
            if(t.getIdentificador().equals(identificador)){
                trato = t;
                break;
            }
        }
        if(trato == null){
            throw new MonopolyException("No existe ningún trato con ese identificador\n");
        }
        if(trato.esProponente(this)){
            throw new MonopolyException("No puedes aceptar un trato que has propuesto\n");
        }
        trato.aceptar();
        trato.getJugador1().tratos.remove(trato);
        trato.getJugador2().tratos.remove(trato);
        Juego.consola.imprimir(trato.getMensajeAceptar());
    }
    public String listarTratos(){
        StringBuilder sb = new StringBuilder();
        for(Trato t: this.tratos){
            sb.append(t.toString());
        }
        return sb.toString();
    }

    public int getVecesDados(){
        return this.vecesDados;
    }
    public void incrementarVecesDados(){
        this.vecesDados++;
    }
    public float fortunaTotal(){
        float total = this.fortuna;
        for(Propiedad p: this.propiedades){
            total += p.valor();
            if(p instanceof Solar){
                Solar s = (Solar) p;
                for(Edificio e: s.getEdificios()){
                    total += e.valorInicial();
                }
            }
        }
        return total;
    }
    public void sinDinero(Jugador destinatario,float cantidad) throws BancarrotaException{
        List<Propiedad> hipotecables = this.getHipotecables();
        if(hipotecables.size() == 0){
            throw new BancarrotaException(this,destinatario);
        }
        if(Juego.consola.leer("%s necesita %.2f€.¿Quiere hipotecar alguna propiedad?(s/n)\n".formatted(this.getNombre(),cantidad)).equals("s")){
            while(!this.puedePagar(cantidad)){
                Juego.consola.imprimir("¿Qué propiedad quiere hipotecar?\n");
                int i = 1;
                for(Propiedad p: hipotecables){
                    Juego.consola.imprimir("%d.%s\n".formatted(i,p.getNombre()));
                    i++;
                }
                String n = Juego.consola.leer("Escribe el número: ");
                int num = Integer.parseInt(n);
                if(num < 1 || num > hipotecables.size()){
                    Juego.consola.error("Ese número no es válido\n");
                    continue;
                }
                Propiedad p = hipotecables.get(num-1);
                this.hipotecar(p);
            }
            this.decrementarFortuna(cantidad);
            destinatario.cobrar(cantidad);
            return;
        }
        throw new BancarrotaException(this,destinatario);
    }


    public void decrementarFortuna(float cantidad){
        this.fortuna -= cantidad;
    }
    public Avatar getAvatar(){
        return this.avatar;
    }
    public boolean tieneAccion(){
        return accion;
    }
    public void setAccion(boolean v){
        this.accion = v;
    }
    public void setFortuna(float fortuna){
        this.fortuna = fortuna;
    }

    public void addPropiedad(Propiedad p){
        this.propiedades.add(p);
    }
    public void removePropiedad(Propiedad p){
        this.propiedades.remove(p);
    }

    public List<Edificio> getEdificios(){
        List<Edificio> edificios = new ArrayList<>();
        for(Propiedad p: this.propiedades){
            if(p instanceof Solar){
                Solar s = (Solar) p;
                
                edificios.addAll(s.getEdificios());
            }
        }
        return edificios;
    }
    
    public boolean tieneTodosLosSolares(ColorString.Color grupo){
        int cantidad = 0;
        for(Propiedad p: this.propiedades){
            if(p instanceof Solar){
                Solar s = (Solar) p;
                if(s.getGrupo() == grupo){
                    cantidad++;
                }
            }
        }
        return cantidad == Juego.getSizeGrupo(grupo);
    }
    public int getNumTransporte(){
        int cantidad = 0;
        for(Propiedad p: this.propiedades){
            if(p instanceof Transporte){
                cantidad++;
            }
        }
        return cantidad;
    }
    public int getNumServicios(){
        int cantidad = 0;
        for(Propiedad p: this.propiedades){
            if(p instanceof Servicios){
                cantidad++;
            }
        }
        return cantidad;
    }

    public void addBote(float cantidad){
        this.bote += cantidad;
    }
    public float getBote(){
        return this.bote;
    }
    public float ganarBote(){
        float bote = this.bote;
        this.bote = 0;
        return bote;
    }
    public List<Propiedad> getPropiedades(){
        return this.propiedades;
    }
    public List<Propiedad> getHipotecables(){
        List<Propiedad> hipotecables = new ArrayList<>();
        for(Propiedad p: this.propiedades){
            if(!p.getHipotecada()){
                hipotecables.add(p);
            }
        }
        return hipotecables;
    }

    public void hipotecar(Propiedad p){
        if(!this.propiedades.contains(p)){
            Juego.consola.imprimir("%s no puede hipotecar %s porque no es suya\n".formatted(this.nombre,p.getNombre()));
            return;
        }

        if(p.getHipotecada()){
            Juego.consola.imprimir("%s no puede hipotecar %s porque ya está hipotecada\n".formatted(this.nombre,p.getNombre()));
            return;
        }

        if(p instanceof Solar){
            Solar s = (Solar) p;
            if(s.getEdificios().size() > 0){
                Juego.consola.imprimir("%s no puede hipotecar %s porque tiene edificios\n".formatted(this.nombre,p.getNombre()));
                return;
            }
        }

        this.hipotecas.add(p);
        p.setHipotecada(true);

        this.fortuna += p.valorInicial()/2;
        Juego.consola.imprimir("%s hipoteca %s por %.2f€\n".formatted(this.nombre,p.getNombre(),p.valorInicial()/2));
    }

    public void deshipotecar(Propiedad p){
        if(!this.propiedades.contains(p)){
            Juego.consola.imprimir("%s no puede deshipotecar %s porque no es suya\n".formatted(this.nombre,p.getNombre()));
            return;
        }

        if(!p.getHipotecada()){
            Juego.consola.imprimir("%s no puede deshipotecar %s porque no está hipotecada\n".formatted(this.nombre,p.getNombre()));
            return;
        }
        
        float valorHipoteca = 1.1f * p.valorInicial()/2;

        if(!this.puedePagar(valorHipoteca)){
            Juego.consola.imprimir("%s no puede deshipotecar %s porque no tiene suficiente dinero\n".formatted(this.nombre,p.getNombre()));
            return;
        }

        this.hipotecas.remove(p);
        p.setHipotecada(false);
        this.fortuna -= valorHipoteca;
        Juego.consola.imprimir("%s deshipoteca %s por %.2f€\n".formatted(this.nombre,p.getNombre(),valorHipoteca));
    }

    public void bancarrota(Jugador destinatario){
        for(Propiedad p: this.propiedades){
            p.setPropietario(destinatario);
            destinatario.addPropiedad(p);
            p.setHipotecada(false);

        }
        this.propiedades.clear();
        this.hipotecas.clear();

        destinatario.cobrar(this.fortuna);
        this.fortuna = 0;
        this.activo = false;
        this.avatar.eliminar();
        Juego.consola.imprimir("%s ha caído en bancarrota y %s se queda con todas sus propiedades\n".formatted(this.nombre,destinatario.getNombre()));
    }

    public void nuevoExentoDeAlquiler(String nombreCasilla,int turnos){
        if(this.exentosDeAlquilerEn.containsKey(nombreCasilla)){
            this.exentosDeAlquilerEn.replace(nombreCasilla,turnos);
        }else{
            this.exentosDeAlquilerEn.put(nombreCasilla,turnos);
        }
    }
    public void decrementarExentosDeAlquiler(){
        List<String> eliminar = new ArrayList<>();
        for(String nombreCasilla: this.exentosDeAlquilerEn.keySet()){
            int turnos = this.exentosDeAlquilerEn.get(nombreCasilla);
            if(turnos == 1){
                eliminar.add(nombreCasilla);
            }else{
                this.exentosDeAlquilerEn.replace(nombreCasilla,turnos-1);
            }
        }
        for(String nombreCasilla: eliminar){
            this.exentosDeAlquilerEn.remove(nombreCasilla);
        }
    }
    public boolean esExentoDeAlquiler(String nombreCasilla){
        return this.exentosDeAlquilerEn.containsKey(nombreCasilla);
    }

    public void eliminarTrato(String id) throws MonopolyException{
        this.tratos.removeIf(t -> t.getIdentificador().equals(id));
    }
    public Trato getTrato(String id) throws MonopolyException{
        for(Trato t: this.tratos){
            if(t.getIdentificador().equals(id)){
                return t;
            }
        }
        throw new MonopolyException("Este jugador no tiene ese trato\n");
    }


    public String estado(){

        String propiedades = "[";
        for(Propiedad p: this.propiedades){
            propiedades += p.getNombre() + ",";
        }
        if(propiedades.length() > 1){
            propiedades = propiedades.substring(0,propiedades.length()-1);
        }
        propiedades += "]";

        return """
        {
            propiedades: %s,
            fortuna: %.2f,
            gastos: %.2f,
        }\n""".formatted(propiedades,this.fortuna,this.dineroInvertido+this.pagoTasasEImpuestos+this.pagoDeAlquileres);
    }

    public void estadisticas(){
        Juego.consola.imprimir("""
        {
            dineroInvertido: %.2f,
            pagoTasasEImpuestos: %.2f,
            pagoDeAlquileres: %.2f,
            cobroDeAlquileres: %.2f,
            pasarPorCasillaDeSalida: %.2f,
            premiosInversionesOBote: %.2f,
            vecesEnLaCarcel: %d
        }\n""".formatted(
            this.dineroInvertido,
            this.pagoTasasEImpuestos,
            this.pagoDeAlquileres,
            this.cobroDeAlquileres,
            this.pasarPorCasillaDeSalida,
            this.premiosInversionesOBote,
            this.vecesEnLaCarcel)
        );
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if( o == null || o.getClass() != this.getClass()){
            return false;
        }
        final Jugador other = (Jugador) o;
        if(!this.nombre.equals(other.nombre)){
            return false;
        }
        return true;
    }
    @Override
    public String toString(){

        String propiedades = "[";
        for(Propiedad p: this.propiedades){
            propiedades += p.getNombre() + ",";
        }
        if(propiedades.length() > 1){
            propiedades = propiedades.substring(0,propiedades.length()-1);
        }
        propiedades += "]";

        String hipotecas = "[";
        for(Propiedad p: this.hipotecas){
            hipotecas += p.getNombre() + ",";
        }
        if(hipotecas.length() > 1){
            hipotecas = hipotecas.substring(0,hipotecas.length()-1);
        }
        hipotecas += "]";


        String edificios = "[";
        for(Edificio e: this.getEdificios()){
            edificios += e.getNombre() + ",";
        }
        if(edificios.length() > 1){
            edificios = edificios.substring(0,edificios.length()-1);
        }
        edificios += "]";

        return """
        {
            nombre: %s,
            avatar: %c,
            fortuna: %.2f,
            propiedades: %s,
            hipotecas: %s,
            edificios: %s
        }\n""".formatted(this.nombre,this.avatar.getId(),this.fortuna,propiedades,hipotecas,edificios);
    }
}



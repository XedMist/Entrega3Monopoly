package monopoly_core;

import monopoly_avatar.*;
import monopoly_carta.CartaCajaComunidad;
import monopoly_carta.CartaSuerte;
import monopoly_casilla.*;
import monopoly_core.ColorString.Color;
import monopoly_edificios.*;
import monopoly_exceptions.LogicaException;
import monopoly_exceptions.MonopolyException;
import monopoly_exceptions.VictoriaException;
import monopoly_trato.Trato;
import monopoly_trato.TratoPC;
import monopoly_trato.TratoPP;
import monopoly_trato.TratoPPA;
import monopoly_trato.TratoPPC;

import java.io.File;
import java.util.Comparator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import java.util.HashMap;
import java.util.Random;
public class Juego implements Comando{
    public static final int NJUGADORES = 6;
    public static Consola consola;
    public static Jugador banca;
    public static Dado dados;
    public static Salida salida;

    private Tablero tablero;
    private List<Casilla> casillas;
    private Map<ColorString.Color,List<Solar>> solares;
    private Carcel carcel;
    private boolean haEmpezado;
    private Queue<Jugador> colaJugadores;
    private Map<String,Jugador> jugadores;
    
    private Map<Character,Jugador> avatares;
    
    private Jugador jugadorActual;
    private int tiradas;

    private int nTratos;

    private Map<String,Integer> nEdificios;

    private Map<Color,Grupo> grupos;
    public Juego(String nombreFichero){
        try{
            Scanner sc = new Scanner(new File(nombreFichero));
            Comparator<Casilla> comparador = Comparator.comparingInt(Casilla::getPosicion);
            dados = new Dado();
            List<Casilla> casillas = new ArrayList<>();
            this.solares = new HashMap<>();
            banca = new Jugador();
            this.colaJugadores = new ArrayDeque<>();
            this.jugadores = new HashMap<>();
            this.avatares = new HashMap<>();
            this.haEmpezado = false;
            consola = new ConsolaNormal();
            this.grupos = new HashMap<>();
            for(ColorString.Color c: ColorString.Color.values()){
                this.solares.put(c,new ArrayList<>());
                this.grupos.put(c,new Grupo(c));
            }

            int i = 0;
            while(sc.hasNextLine()){
                String linea = sc.nextLine();
                String[] campos = linea.split(" ");
                switch(campos.length){
                    case 2:
                        switch(campos[1]){
                            case "Suerte":
                                casillas.add(new AccionSuerte(campos[0],_posArrayATablero(i)));
                                break;
                            case "Comunidad":
                                casillas.add(new AccionCajaComunidad(campos[0],_posArrayATablero(i)));
                                break;
                            case "Impuestos":
                                casillas.add(new Impuesto(campos[0],_posArrayATablero(i)));
                                break;
                            case "Servicios":
                                casillas.add(new Servicios(campos[0],_posArrayATablero(i)));
                                break;
                            case "Transporte":
                                casillas.add(new Transporte(campos[0],_posArrayATablero(i)));
                                break;
                            default:
                                throw new IllegalArgumentException("Tipo de casilla no reconocido");
                        }
                        break;
                    case 3:
                        switch(campos[2]){
                            case "IrCarcel":
                                casillas.add(new IrCarcel(campos[0],_posArrayATablero(i)));
                                break;
                            case "Carcel":
                                this.carcel = new Carcel(campos[0],_posArrayATablero(i));
                                casillas.add(this.carcel);
                                break;
                            case "Salida":
                                salida = new Salida(campos[0],_posArrayATablero(i));
                                casillas.add(salida);
                                break;
                            case "Parking":
                                casillas.add(new Accion(campos[0],_posArrayATablero(i)));
                                break;
                        }
                        break;
                    case 4: 
                        ColorString.Color color = ColorString.Color.valueOf(campos[2]);
                        Grupo grupo = this.grupos.get(color);
                        Solar nuevoSolar = new Solar(campos[0],_posArrayATablero(i),Float.parseFloat(campos[3]),grupo);
                        casillas.add(nuevoSolar);
                        this.solares.get(nuevoSolar.getGrupo()).add(nuevoSolar);
                        grupo.addPropiedad(nuevoSolar);
                        break;
                }
                i+=1;
            }
            this.casillas = new ArrayList<>(casillas);
            this.tablero = new Tablero(casillas);
            this.casillas.sort(comparador);
            this.nEdificios = new HashMap<>();
            this.nEdificios.put("casa",0);
            this.nEdificios.put("hotel",0);
            this.nEdificios.put("piscina",0);
            this.nEdificios.put("pista",0);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public boolean haEmpezado(){
        return this.haEmpezado;
    }
    public Jugador getJugadorActual(){
        return this.jugadorActual;
    }
    private List<CartaSuerte> getCartasSuerte(){
        ArrayList<CartaSuerte> cartas = new ArrayList<>();
        cartas.add(new CartaSuerte("¡Has ganado el bote de la loteria! Recibe 1000000€",CartaSuerte.TipoSuerte.GanarLoteria));
        cartas.add(new CartaSuerte("Vendes tu billete de avión a Cádiz en una subasta por Internet. Recibe 500000€",CartaSuerte.TipoSuerte.VenderBillete));
        cartas.add(new CartaSuerte("Te multan por usar el móvil mientras conduces. Paga 250000€",CartaSuerte.TipoSuerte.MultarMovil));
        cartas.add(new CartaSuerte("Beneficio por la venta de tus acciones. Recibe 1500000€",CartaSuerte.TipoSuerte.BeneficiarAcciones));
        cartas.add(new CartaSuerte("Has sido elegido presidente de la junta directiva. Paga 1000000€ a cada jugador",CartaSuerte.TipoSuerte.SerPresidente));
        cartas.add(new CartaSuerte("El ayuntamiento ha decidido aumentar los impuestos. Paga 1000000€",CartaSuerte.TipoSuerte.AumentarImpuesto));
        return cartas;
    }

    private List<CartaCajaComunidad> getCartasComunidad(){
        ArrayList<CartaCajaComunidad> cartas = new ArrayList<>();
        cartas.add(new CartaCajaComunidad("Recibe 1000000 de beneficios por alquilar los servicios de tu jet privado.",CartaCajaComunidad.TipoComunidad.AlquilarJet));
        cartas.add(new CartaCajaComunidad("Paga 500000€ por un fin de semana en un balneario de 5 estrellas.",CartaCajaComunidad.TipoComunidad.PagarBalneario));
        cartas.add(new CartaCajaComunidad("Devolución de Hacienda. Recibe 500000€.",CartaCajaComunidad.TipoComunidad.DevolverHacienda));
        cartas.add(new CartaCajaComunidad("Tu compañía de Internet obtiene beneficios. Recibe 2000000€.",CartaCajaComunidad.TipoComunidad.BeneficiarCompanhia));
        cartas.add(new CartaCajaComunidad("Paga 1000000€ por invitar a todos tus amigos a un viaje a León.",CartaCajaComunidad.TipoComunidad.PagarViaje));
        cartas.add(new CartaCajaComunidad("Has sido elegido presidente de la junta directiva. Recibe 1000000€ de cada jugador.",CartaCajaComunidad.TipoComunidad.AlquilarVilla));
        return cartas;
    }

    public void inicializarJuego(){
        float totalSolares = 0;

        
        for(List<Solar> solares: this.solares.values()){
            for(Solar s: solares){
                totalSolares += s.valorInicial();
            }
        }
        float premioVuelta = totalSolares/this.solares.size();
        salida.setPremio(premioVuelta);
        for(Jugador j: this.jugadores.values()){
            j.setFortuna(totalSolares/3);
        }


        float impuesto = premioVuelta;
        for(Casilla c: this.casillas){
            if(c instanceof Impuesto){
                ((Impuesto)c).inicializarImpuesto(impuesto);
                impuesto /=2;
            }else if(c instanceof Transporte){
                ((Transporte)c).inicializar(premioVuelta);
            }else if(c instanceof Servicios){
                ((Servicios)c).inicializar(premioVuelta);
            }else if(c instanceof IrCarcel){
                ((IrCarcel)c).setCarcel(this.carcel);
            }else if(c instanceof AccionSuerte){
                List<CartaSuerte> cartas = this.getCartasSuerte();
                List<Jugador> jugadores = new ArrayList<>(this.jugadores.values());
                ((AccionSuerte)c).inicializarCartas(cartas,jugadores);
            }else if(c instanceof AccionCajaComunidad){
                List<CartaCajaComunidad> cartas = this.getCartasComunidad();
                List<Jugador> jugadores = new ArrayList<>(this.jugadores.values());
                ((AccionCajaComunidad)c).inicializarCartas(cartas,jugadores);
            }
            if( c instanceof Propiedad){
                ((Propiedad)c).setPropietario(banca);
            }
        }

        this.jugadorActual = this.colaJugadores.peek();
    
        this.verTablero();
    }
    public void ejecutar(){
        consola.empezar(this);
    }

    public static int getSizeGrupo(ColorString.Color c){
        switch(c){
            case Rojo:
                return 3;
            case Azul:
                return 3;
            case VerdeClaro:
                return 2;
            case Verde:
                return 3;
            case Cyan:
                return 3;
            case Morado:
                return 3;
            case Amarillo:
                return 2;
            case Blanco:
                return 0;
            default:
                return 0;
        }
    }

    private int _posArrayATablero(int i){
        if(i <= 10){
            return i+ 20;
        }
        if(i < 30){
            return i % 2 != 0 ? 19-(i-11)/2 : 31 + (i-12)/2;
        }
        return 39-i;
    }
    private char generarLetraAvatar(){
        Random r = new Random();
        char randomChar = (char)(r.nextInt(90-65)+65);
        if(this.avatares.containsKey(randomChar)){
            return generarLetraAvatar();
        }
        return randomChar;
    }
    private Casilla getCasillaPorNombre(String nombreCasilla){
        for(Casilla c: this.casillas){
            if(c.getNombre().equals(nombreCasilla)){
                return c;
            }
        }
        return null;
    }

    public Map<String, Integer> cupoEdificiosGrupo(ColorString.Color grupo){
        Map<String,Integer> cupo = new HashMap<>();
        cupo.put("casa",0);
        cupo.put("hotel",0);
        cupo.put("piscina",0);
        cupo.put("pista",0);
        int nCasas = 0;
        for(Solar s: this.solares.get(grupo)){
            nCasas += s.nTipoEdificio(Casa.class);
            cupo.put("hotel",cupo.get("hotel") + s.nTipoEdificio(Hotel.class));
            cupo.put("piscina",cupo.get("piscina") + s.nTipoEdificio(Piscina.class));
            cupo.put("pista",cupo.get("pista") + s.nTipoEdificio(PistaDeporte.class));
        }
        
        for (Map.Entry<String,Integer> entry : cupo.entrySet()){
            if(entry.getKey().equals("casa")){
                continue;
            }
            entry.setValue(getSizeGrupo(grupo) - entry.getValue());
        }

        if(cupo.get("hotel") == 1 && nCasas - 4 > getSizeGrupo(grupo))
            cupo.put("hotel",0);
        
        cupo.put("casa",getSizeGrupo(grupo)*4 - nCasas);
        return cupo;

    }
    private boolean tieneTodosSolar(Jugador j,ColorString.Color grupo){
        boolean tieneTodos = true;
        for(Solar s: this.solares.get(grupo)){
            if(!s.getPropietario().equals(j)){
                tieneTodos = false;
            }
        }
        return tieneTodos;
    }

    private boolean condicionPiscina(ColorString.Color grupo){
        int nCasas = 0;
        int nHoteles = 0;
        for(Solar s: this.solares.get(grupo)){
            nCasas += s.nTipoEdificio(Casa.class);
            nHoteles += s.nTipoEdificio(Hotel.class);
        }
        return nCasas >= 2 && nHoteles >= 1;
    }

    private boolean condicionPista(ColorString.Color grupo){
        //Comprueba si el grupo tiene dos hoteles
        int nHoteles = 0;
        for(Solar s: this.solares.get(grupo)){
            nHoteles += s.nTipoEdificio(Hotel.class);
        }
        return nHoteles >= 2;
    }

    private void comprobar4Vueltas(){
        boolean check = true;
        for(Jugador j : this.jugadores.values()){
            if(j.getVueltas() < 4){
                check = false;
            }
        }
        if(!check){
            return;
        }
        for(Jugador j : this.jugadores.values()){
            j.setVueltas(0);
        }
        float nuevoPremio = 0;
        for(List<Solar> ls: this.solares.values()){
            for(Solar s: ls){
                if(s.getPropietario().equals(Juego.banca)){
                    s.incrementarValor();
                }
                nuevoPremio += s.valor();
            }
        }
        salida.setPremio(nuevoPremio/this.solares.size());
        consola.imprimir("Todos los jugadores han dado 4 vueltas, por lo tanto se incrementa el valor de las propiedades sin dueño en un 5%.\n");
    }

    /*                                          */
    /*             Interfaz Comando             */
    /*                                          */
    /*                                          */
    public void verTablero(){
        consola.imprimir(tablero.toString());
    }
    public void empezar() throws LogicaException{
        if(this.jugadores.size() < 2){
            throw new LogicaException("No se puede empezar el juego con menos de dos jugadores\n");
        }
        if(this.haEmpezado){
            throw new LogicaException("El juego ya ha empezado\n");
        }
        this.haEmpezado = true;
        this.inicializarJuego();
        consola.imprimir("El juego ha empezado\n");
        consola.imprimir("El jugador actual es %s\n".formatted(this.jugadorActual.getNombre()));
    }
    public void crearJugador(String nombreJugador, String tAvatar) throws LogicaException{
        if(this.colaJugadores.size() == NJUGADORES){
            throw new LogicaException("No se pueden crear más jugadores\n");
        }
        if(this.haEmpezado){
            throw new LogicaException("No se pueden crear jugadores una vez ha empezado el juego\n");
        }
        if(this.jugadores.containsKey(nombreJugador)){
            throw new LogicaException("Ya existe un jugador con ese nombre\n");
        }
        String[] tAvataresDisponibles = {"coche","sombrero","esfinge","pelota"};
        boolean esTipoValido = false;
        for(String t:tAvataresDisponibles){
            if(t.equals(tAvatar)){
                esTipoValido = true;
            }
        }
        if(!esTipoValido){
            throw new LogicaException("No existe ese tipo de avatar\n");
        }
        char id = generarLetraAvatar();
        Avatar avatar = null;
        switch(tAvatar){
            case "coche":
                avatar = new Coche(id,salida);
                break;
            case "sombrero":
                avatar = new Sombrero(id,salida);
                break;
            case "esfinge":
                avatar = new Esfinge(id,salida);
                break;
            case "pelota":
                avatar = new Pelota(id,salida);
                break;
        }
        this.avatares.put(id,this.jugadorActual);
        Jugador nuevoJugador = new Jugador(nombreJugador,avatar);
        avatar.asignarJugador(nuevoJugador);
        this.colaJugadores.add(nuevoJugador);
        this.jugadores.put(nombreJugador,nuevoJugador);
        consola.imprimir(
                """
                {
                    nombre: %s,
                    avatar: %c
                }\n""".formatted(nombreJugador,id));


    }
    public void describirJugador(String nombreJugador) throws LogicaException{
        if(!this.jugadores.containsKey(nombreJugador)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        consola.imprimir(this.jugadores.get(nombreJugador).toString());

    }
    public void describirCasilla(String nombreCasilla) throws LogicaException{
        Casilla c = this.getCasillaPorNombre(nombreCasilla);
        if(c == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        consola.imprimir(c.toString());
    }
    public void describirAvatar(char idAvatar) throws LogicaException{
        if(!this.avatares.containsKey(idAvatar)){
            throw new LogicaException("No existe ningún avatar con ese id\n");
        }
        consola.imprimir(this.avatares.get(idAvatar).toString());
    }
    public void lanzarDados(boolean debug) throws MonopolyException {
        this.jugadorActual.incrementarVecesDados();
        Avatar avatar = this.jugadorActual.getAvatar();
        if(avatar.getEnModoAvanzado() && (avatar instanceof Coche || avatar instanceof Pelota)){
            avatar.moverEnAvanzado(this.casillas,debug);
            return;
        }
        if(!this.jugadorActual.tieneAccion()){
            throw new LogicaException("El jugador actual ya ha lanzado los dados\n");
        }

        if(avatar instanceof Coche c && c.penalizado()){
            throw new LogicaException("El jugador actual está penalizado\n");
        }
        int tirada = debug ? dados.debugLanzar() : dados.lanzar();
        
        if (avatar.getCasilla() instanceof Carcel && this.carcel.estaEncarcelado(this.jugadorActual)){
            this.carcel.desencarcelar(this.jugadorActual,dados.getFueronDobles());
            return;
        }

        consola.imprimir("El jugador %s ha sacado un %d.".formatted(this.jugadorActual.getNombre(),tirada));
        Casilla casillaActual = avatar.getCasilla();
        int nuevaPosicion = casillaActual.getPosicion() + tirada;
        if(nuevaPosicion >= 40){
            nuevaPosicion -= 40;
            this.jugadorActual.incrementarVueltas();
            this.jugadorActual.cobrar(salida.getPremio());
            this.jugadorActual.incrementarPasarPorCasillaDeSalida(salida.getPremio());
            consola.imprimir("El jugador %s ha pasado por la salida y ha cobrado %.2f\n".formatted(this.jugadorActual.getNombre(),salida.getPremio()));
        }
        Casilla casillaNueva = this.casillas.get(nuevaPosicion);
        consola.imprimir("El jugador %s ha caido en %s\n".formatted(this.jugadorActual.getNombre(),casillaNueva.getNombre()));
        this.jugadorActual.setAccion(false);
        this.tiradas += 1;
        if(dados.getFueronDobles()){
            if(this.tiradas == 3){
                consola.imprimir("El jugador ha sacado dados dobles tres veces. Por lo tanto va a la carcel\n");
                avatar.moverACasilla(this.carcel);
                return;
            }
            consola.imprimir("Han sido dados dobles. Puede volver a tirar los dados\n");
            this.jugadorActual.setAccion(true);
        }
        avatar.moverEnBasico(casillaNueva);
        this.verTablero();
        consola.imprimir(this.jugadorActual.estado());

    }
    public void comprar(String nombreCasilla) throws MonopolyException{
        Casilla c = this.getCasillaPorNombre(nombreCasilla);
        if(c == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(c instanceof Propiedad)){
            throw new LogicaException("La casilla no es una propiedad\n");
        }
        Propiedad p = (Propiedad) c;
        p.comprar(this.jugadorActual.getAvatar());
    }
    public void acabarTurno() throws VictoriaException{
        if(this.jugadorActual.activo() && this.jugadorActual.tieneAccion()){
            consola.imprimir("El jugador actual no ha lanzado los dados\n");
            return;
        }
        this.colaJugadores.poll();
        Jugador j = this.colaJugadores.peek();
        if(this.jugadorActual.activo()){
            this.colaJugadores.offer(this.jugadorActual);
        }
        if(this.colaJugadores.size() == 1){
            throw new VictoriaException(this.colaJugadores.peek());
        }
        this.jugadorActual = j;
        j.setAccion(true);
        this.tiradas = 0;
        
        Avatar avatar = this.jugadorActual.getAvatar();
        this.jugadorActual.decrementarExentosDeAlquiler();
        if(avatar instanceof Coche c){
            c.siguienteTurno();
        }
        else if(avatar instanceof Pelota p){
            p.siguienteTurno();
        }
        this.comprobar4Vueltas();
        consola.imprimir("El jugador actual es %s.\n".formatted(this.jugadorActual.getNombre()));
    }
    public void jugador(){
        consola.imprimir("""
                {
                    nombre: %s,
                    avatar: %c
                }""".formatted(this.jugadorActual.getNombre(),this.jugadorActual.getAvatar().getId()));
    }
    public void listarJugadores(){
        for(Jugador j: this.jugadores.values()){
            if(j.equals(banca)){
                continue;
            }
            consola.imprimir(j.toString() + "\n" );
        }
    }
    public void listarAvatares(){
        for(Jugador j: this.jugadores.values()){
            if(j.equals(banca)){
                continue;
            }
            consola.imprimir(j.getAvatar().toString() + "\n" );
        }
    }
    public void salirCarcel() throws MonopolyException{
        if(!this.carcel.estaEncarcelado(this.jugadorActual)){
            throw new LogicaException("El jugador actual no está en la carcel\n");
        }
        if(this.jugadorActual.tieneAccion()){
            throw new LogicaException("El jugador actual ya ha lanzado los dados\n");
        }
        if(!this.jugadorActual.puedePagar(salida.getPremio()*0.25f)){
            throw new LogicaException("El jugador actual no tiene suficiente dinero para pagar la fianza\n");
        }
        this.jugadorActual.pagar(banca,salida.getPremio()*0.25f);
        this.carcel.desencarcelar(this.jugadorActual,false);
        consola.imprimir("El jugador actual ha salido de la carcel\n");
    }

    

    public void edificar(String tipoEdificio) throws MonopolyException{
        Casilla casilla = this.jugadorActual.getAvatar().getCasilla();
        if(!(casilla instanceof Solar)){
            throw new LogicaException("El jugador actual no está en una casilla de tipo solar\n");
        }
        Solar solar = (Solar) casilla;
        if(!this.jugadorActual.equals(solar.getPropietario())){
            throw new LogicaException("El jugador actual no es el propietario de la casilla\n");
        }
        if(!this.tieneTodosSolar(this.jugadorActual,solar.getGrupo()) && solar.frecuenciaVisita(this.jugadorActual.getNombre()) < 3){
            throw new LogicaException("El jugador actual no tiene todos los solares del grupo o no ja caido más de dos veces en esta casilla\n");
        }

        Map<String,Integer> cupo = this.cupoEdificiosGrupo(solar.getGrupo());
        if(cupo.get(tipoEdificio) == 0){
            throw new LogicaException("No se pueden edificar más edificios de ese tipo en el grupo\n");
        }
        switch(tipoEdificio){
            case "casa":
                Casa c = new Casa("casa-" + this.nEdificios.get("casa"),solar);
                this.nEdificios.put("casa",this.nEdificios.get("casa") + 1);
                solar.edificar(c);
                break;
            case "hotel":
                Hotel h = new Hotel("hotel-" + this.nEdificios.get("hotel"),solar);
                this.nEdificios.put("hotel",this.nEdificios.get("hotel") + 1);
                solar.edificar(h);
                break;
            case "piscina":
                if(!this.condicionPiscina(solar.getGrupo())){
                    consola.imprimir("No se puede edificar una piscina en el grupo porque no se cumplen las condiciones\n");
                    return;
                }
                Piscina p = new Piscina("piscina-" + this.nEdificios.get("piscina"),solar);
                this.nEdificios.put("piscina",this.nEdificios.get("piscina") + 1);
                solar.edificar(p);
                break;
            case "pista":
                if(!this.condicionPista(solar.getGrupo())){
                    consola.imprimir("No se puede edificar una pista de deporte en el grupo porque no se cumplen las condiciones\n");
                    return;
                }

                PistaDeporte pd = new PistaDeporte("pista-" + this.nEdificios.get("pista"),solar);
                this.nEdificios.put("pista",this.nEdificios.get("pista") + 1);
                solar.edificar(pd);
                break;
            default:
                return;
        } 

    }

    public void listarEdificios(){
        for(List<Solar> lista: this.solares.values()){
            for(Solar s: lista){
                for(Edificio e: s.getEdificios()){
                    consola.imprimir(e.toString() + "\n");
                }
            }
        }
    }
    public void listarEdificios(String nombreGrupo){
        ColorString.Color grupo = ColorString.Color.Blanco;
        switch(nombreGrupo.toLowerCase()){
            case "rojo":
                grupo = ColorString.Color.Rojo;
                break;
            case "azul":
                grupo = ColorString.Color.Azul;
                break;
            case "verdeclaro":
                grupo = ColorString.Color.VerdeClaro;
                break;
            case "verde":
                grupo = ColorString.Color.Verde;
                break;
            case "cyan":
                grupo = ColorString.Color.Cyan;
                break;
            case "morado":
                grupo = ColorString.Color.Morado;
                break;
            case "amarillo":
                grupo = ColorString.Color.Amarillo;
                break;
        }

        for(Solar s: this.solares.get(grupo)){
            String hoteles = "[";
            String casas = "[";
            String piscinas = "[";
            String pistas = "[";
            for(Edificio e: s.getEdificios()){
                if(e instanceof Hotel){
                    hoteles += e.getNombre() + ",";
                }else if(e instanceof Casa){
                    casas += e.getNombre() + ",";
                }else if(e instanceof Piscina){
                    piscinas += e.getNombre() + ",";
                }else if(e instanceof PistaDeporte){
                    pistas += e.getNombre() + ",";
                }
            }
            hoteles += "]";
            casas += "]";
            piscinas += "]";
            pistas += "]";
            consola.imprimir("""
                    {
                        propiedad: %s,
                        hoteles: %s,
                        casas: %s,
                        piscinas: %s,
                        pistasDeDeporte: %s,
                        alquiler: %.2f
                    }\n""".formatted(s.getNombre(),hoteles,casas,piscinas,pistas,s.alquiler()));
        }

        Map<String,Integer> cupo = this.cupoEdificiosGrupo(grupo);
        String mensaje1 = "Aún se pueden edificar ";
        String mensaje2 = "Ya no se pueden construir ";
        if(cupo.get("casa") > 0){
            mensaje1 += "%d casas, ".formatted(cupo.get("casa"));
        }else{
            mensaje2 += "casas, ";
        }
        if(cupo.get("hotel") > 0){
            mensaje1 += "%d hoteles, ".formatted(cupo.get("hotel"));
        }else{
            mensaje2 += "hoteles, ";
        }
        if(cupo.get("piscina") > 0){
            mensaje1 += "%d piscinas, ".formatted(cupo.get("piscina"));
        }else{
            mensaje2 += "piscinas, ";
        }
        if(cupo.get("pista") > 0){
            mensaje1 += "%d pistas de deporte. ".formatted(cupo.get("pista"));
        }else{
            mensaje2 += "pistas de deporte. ";
        }

        if(mensaje1.equals("Aún se pueden edificar ")){
            consola.imprimir(mensaje2 + "\n");
            return;
        }else if(mensaje2.equals("Ya no se pueden construir ")){
            consola.imprimir(mensaje1 + "\n");
            return;
        }
        consola.imprimir(mensaje1 + mensaje2 + "\n");
    }
    public void vender(String tipoEdificio, String nombreCasilla,int numero) throws LogicaException{
        Casilla casilla = this.getCasillaPorNombre(nombreCasilla);
        if(casilla == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(casilla instanceof Solar)){
            throw new LogicaException("Esa casilla no es un solar\n");
        }
        Solar solar = (Solar) casilla;
        if(!this.jugadorActual.equals(solar.getPropietario())){
            throw new LogicaException("El jugador actual no es el propietario de la casilla\n");
        }
        if(numero <= 0){
            throw new LogicaException("El número de edificios a vender no puede ser menor o igual que 0\n");
        }
        switch(tipoEdificio){
            case "casa":
                if(numero > solar.nTipoEdificio(Casa.class)){
                    consola.imprimir("El número de edificios a vender no puede ser mayor que el número de edificios de ese tipo en la casilla\n");
                    return;
                }
                consola.imprimir("%s vende %d casas en %s.".formatted(this.jugadorActual.getNombre(),numero,solar.getNombre()));
                solar.vender(Casa.class,numero);
                break;
            case "hotel":
                if(numero > solar.nTipoEdificio(Hotel.class)){
                    consola.imprimir("El número de edificios a vender no puede ser mayor que el número de edificios de ese tipo en la casilla\n");
                    return;
                }
                consola.imprimir("%s vende %d hoteles en %s.".formatted(this.jugadorActual.getNombre(),numero,solar.getNombre()));
                solar.vender(Hotel.class,numero);
                break;
            case "piscina":
                if(numero > solar.nTipoEdificio(Piscina.class)){
                    consola.imprimir("El número de edificios a vender no puede ser mayor que el número de edificios de ese tipo en la casilla\n");
                    return;
                }
                consola.imprimir("%s vende %d piscinas en %s.".formatted(this.jugadorActual.getNombre(),numero,solar.getNombre()));
                solar.vender(Piscina.class,numero);
                break;
            case "pista":
                if(numero > solar.nTipoEdificio(PistaDeporte.class)){
                    consola.imprimir("El número de edificios a vender no puede ser mayor que el número de edificios de ese tipo en la casilla\n");
                    return;
                }
                consola.imprimir("%s vende %d pistas en %s.".formatted(this.jugadorActual.getNombre(),numero,solar.getNombre()));
                solar.vender(PistaDeporte.class,numero);
                break;
            default:
                return;
        }
    }

    public void hipotecar(String nombreCasilla) throws LogicaException{
        Casilla casilla = this.getCasillaPorNombre(nombreCasilla);
        if(casilla == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(casilla instanceof Propiedad)){
            throw new LogicaException("Esa casilla no es una propiedad\n");
        }
        Propiedad propiedad = (Propiedad) casilla;
        if(!this.jugadorActual.equals(propiedad.getPropietario())){
            throw new LogicaException("El jugador actual no es el propietario de la casilla\n");
        }
        this.jugadorActual.hipotecar(propiedad);
    }
    public void deshipotecar(String nombreCasilla) throws MonopolyException{
        Casilla casilla = this.getCasillaPorNombre(nombreCasilla);
        if(casilla == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(casilla instanceof Propiedad)){
            throw new LogicaException("Esa casilla no es una propiedad\n");
        }
        Propiedad propiedad = (Propiedad) casilla;
        if(!this.jugadorActual.equals(propiedad.getPropietario())){
            throw new LogicaException("El jugador actual no es el propietario de la casilla\n");
        }
        this.jugadorActual.deshipotecar(propiedad);
    }
    public void bancarrota() throws MonopolyException{
        this.jugadorActual.bancarrota(banca);
        this.acabarTurno();
    }
    public void estadisticas(){
        Propiedad cMasRentable = null;
        ColorString.Color grupoMasRentable = null;
        float beneficiosGrupoMasRentable = 0;
        Casilla cMasFrecuentada = null;
        int nVecesMasFrecuentada = 0;
        for(Casilla c : this.casillas){
            if(c instanceof Propiedad p){
                if(cMasRentable == null){
                    cMasRentable = p;
                }else if(p.getBeneficios() > cMasRentable.getBeneficios()){
                    cMasRentable = p;
                }
            }
            if(cMasFrecuentada == null){
                cMasFrecuentada = c;
                nVecesMasFrecuentada = c.frecuenciaVisita();
            }
            else if(c.frecuenciaVisita() > nVecesMasFrecuentada){
                cMasFrecuentada = c;
                nVecesMasFrecuentada = c.frecuenciaVisita();
            }

        }
        for(ColorString.Color c: ColorString.Color.values()){
            float totalGrupo = 0;
            if(c.equals(ColorString.Color.Blanco)){
                continue;
            }
            for(Solar s: this.solares.get(c)){
                totalGrupo += s.getBeneficios();
            }
            if(grupoMasRentable == null){
                grupoMasRentable = c;
            }else if(totalGrupo > beneficiosGrupoMasRentable){
                grupoMasRentable = c;
                beneficiosGrupoMasRentable = totalGrupo;
            }
        }

        Jugador jMasVueltas = null;
        Jugador jMasVecesDados = null;
        Jugador jEnCabeza = null;
        for(Jugador j: this.jugadores.values()){
            if(jMasVueltas == null){
                jMasVueltas = j;
            }else if(j.getVueltas() > jMasVueltas.getVueltas()){
                jMasVueltas = j;
            }
            if(jMasVecesDados == null){
                jMasVecesDados = j;
            }else if(j.getVecesDados() > jMasVecesDados.getVecesDados()){
                jMasVecesDados = j;
            }
            if(jEnCabeza == null){
                jEnCabeza = j;
            }else if(j.fortunaTotal() > jEnCabeza.fortunaTotal()){
                jEnCabeza = j;
            }
        }

        consola.imprimir("""
                {
                    casillaMasRentable: %s,
                    grupoMasRentable: %s,
                    casillaMasFrecuentada: %s,
                    jugadorMasVueltas: %s,
                    jugadorMasVecesDados: %s,
                    jugadorEnCabeza: %s
                }\n""".formatted(cMasRentable.getNombre(),grupoMasRentable,cMasFrecuentada.getNombre(),jMasVueltas.getNombre(),jMasVecesDados.getNombre(),jEnCabeza.getNombre()));
    }
    public void estadisticas(String nombreJugador) throws LogicaException{
        if(!this.jugadores.containsKey(nombreJugador) || this.jugadores.get(nombreJugador).equals(banca)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        this.jugadores.get(nombreJugador).estadisticas();

    }

    public void cambiarModo() throws LogicaException{
        if(!this.jugadorActual.tieneAccion()){
            throw new LogicaException("El jugador actual ya ha lanzado los dados\n");
        }
        if ((this.jugadorActual.getAvatar() instanceof Coche c ) && c.getEnMovimiento()){
            throw new LogicaException("El jugador está en medio de un movimiento.\n");
        }
        if ((this.jugadorActual.getAvatar() instanceof Pelota p ) && p.getEnMovimiento()){
            throw new LogicaException("El jugador está en medio de un movimiento.\n");
        }


        Avatar avatar = this.jugadorActual.getAvatar();
        if(avatar instanceof Coche c){
            c.cambiarModo();
            if(c.getEnModoAvanzado()){
                consola.imprimir("El jugador %s se moverá en modo avanzado\n".formatted(this.jugadorActual.getNombre()));
                return;
            }
            consola.imprimir("El jugador %s se moverá en modo basico\n".formatted(this.jugadorActual.getNombre()));
        }
        if(avatar instanceof Pelota c){
            c.cambiarModo();
            if(c.getEnModoAvanzado()){
                consola.imprimir("El jugador %s se moverá en modo avanzado\n".formatted(this.jugadorActual.getNombre()));
                return;
            }
            consola.imprimir("El jugador %s se moverá en modo basico\n".formatted(this.jugadorActual.getNombre()));
        }
    }
    
    public void trato(String nombreJugador2,String propiedad1,String propiedad2) throws MonopolyException{
        if(!this.jugadores.containsKey(nombreJugador2)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        Jugador j2 = this.jugadores.get(nombreJugador2);
        if(this.jugadorActual.equals(j2)){
            throw new LogicaException("No se puede hacer un trato con uno mismo\n");
        }
        Casilla c1 = this.getCasillaPorNombre(propiedad1);
        Casilla c2 = this.getCasillaPorNombre(propiedad2);
        if(c1 == null || c2 == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(c1 instanceof Propiedad) || !(c2 instanceof Propiedad)){
            throw new LogicaException("La casilla no es una propiedad\n");
        }
        Propiedad p1 = (Propiedad) c1;
        Propiedad p2 = (Propiedad) c2;
        if(!this.jugadorActual.equals(p1.getPropietario()) || !j2.equals(p2.getPropietario())){
            throw new LogicaException("Uno de los jugadores no es propietario de la propiedad\n");
        }

        TratoPP t = new TratoPP(this.jugadorActual,j2,"trato-" + nTratos,p1,p2);
        this.nTratos += 1;
        this.jugadorActual.nuevoTrato(t);
        j2.nuevoTrato(t);
        consola.imprimir("%s, ¿te doy %s y tú me das %s?\n".formatted(j2.getNombre(),p1.getNombre(),p2.getNombre()));
        t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: le doy %s y %s me da %s.\n".formatted(j2.getNombre(),p1.getNombre(),j2.getNombre(),p2.getNombre()));
    }

    void trato(String nombreJugador2,String propiedad1,float cantidad) throws MonopolyException{
        if(!this.jugadores.containsKey(nombreJugador2)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        Jugador j2 = this.jugadores.get(nombreJugador2);
        if(this.jugadorActual.equals(j2)){
            throw new LogicaException("No se puede hacer un trato con uno mismo\n");
        }
        Casilla c1 = this.getCasillaPorNombre(propiedad1);
        if(c1 == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(c1 instanceof Propiedad)){
            throw new LogicaException("La casilla no es una propiedad\n");
        }
        Propiedad p1 = (Propiedad) c1;
        if(!this.jugadorActual.equals(p1.getPropietario())){
            throw new LogicaException("El jugador no es el propietario de la propiedad\n");
        }
        if(cantidad <= 0){
            throw new LogicaException("La cantidad debe ser mayor que 0\n");
        }

        TratoPC t = new TratoPC(this.jugadorActual,j2,"trato-" + nTratos,p1,cantidad,this.jugadorActual);
        this.nTratos += 1;
        this.jugadorActual.nuevoTrato(t);
        j2.nuevoTrato(t);
        consola.imprimir("%s, ¿te doy %s y tú me das %.2f?\n".formatted(j2.getNombre(),p1.getNombre(),cantidad));
        t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: le doy %s y %s me da %.2f.\n".formatted(j2.getNombre(),p1.getNombre(),j2.getNombre(),cantidad));
    }
    public void trato(String nombreJugador1,String nombreJugador2,String propiedad1,float cantidad) throws MonopolyException{
        if(!this.jugadores.containsKey(nombreJugador1) || !this.jugadores.containsKey(nombreJugador2)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        Jugador j1 = this.jugadores.get(nombreJugador1);
        Jugador j2 = this.jugadores.get(nombreJugador2);
        if(j1.equals(j2)){
            throw new LogicaException("No se puede hacer un trato con uno mismo\n");
        }
        Casilla c1 = this.getCasillaPorNombre(propiedad1);
        if(c1 == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(c1 instanceof Propiedad)){
            throw new LogicaException("La casilla no es una propiedad\n");
        }
        Propiedad p1 = (Propiedad) c1;
        if(!j1.equals(p1.getPropietario())){
            throw new LogicaException("Uno de los jugadores no es el propietario de la propiedad\n");
        }
        if(cantidad <= 0){
            throw new LogicaException("La cantidad debe ser mayor que 0\n");
        }

        TratoPC t = new TratoPC(j1,j2,"trato-" + nTratos,p1,cantidad,this.jugadorActual);
        this.nTratos += 1;
        j1.nuevoTrato(t);
        j2.nuevoTrato(t);
        if(j1.equals(this.jugadorActual)){
            consola.imprimir("%s, ¿te doy %s y tú me das %.2f?\n".formatted(j2.getNombre(),p1.getNombre(),cantidad));
            t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: %s me da %s y yo le doy %.2f.\n".formatted(j2.getNombre(),j1.getNombre(),p1.getNombre(),cantidad));
            return;
        }else{
            consola.imprimir("%s, ¿te doy %s y %.2f y tú me das %s?\n".formatted(j1.getNombre(),p1.getNombre(),cantidad,j2.getNombre()));
            t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: %s me da %s y %.2f y yo le doy %s.\n".formatted(j1.getNombre(),j2.getNombre(),p1.getNombre(),cantidad,j2.getNombre()));
            return;
        }
    }
    public void trato(String nombreJugador1,String nombreJugador2,String propiedad1,String propiedad2,float cantidad) throws MonopolyException{
        if(!this.jugadores.containsKey(nombreJugador1) || !this.jugadores.containsKey(nombreJugador2)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        Jugador j1 = this.jugadores.get(nombreJugador1);
        Jugador j2 = this.jugadores.get(nombreJugador2);
        if(j1.equals(j2)){
            throw new LogicaException("No se puede hacer un trato con uno mismo\n");
        }
        Casilla c1 = this.getCasillaPorNombre(propiedad1);
        Casilla c2 = this.getCasillaPorNombre(propiedad2);
        if(c1 == null || c2 == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(c1 instanceof Propiedad) || !(c2 instanceof Propiedad)){
            throw new LogicaException("Una de las casillas no es una propiedad\n");
        }
        Propiedad p1 = (Propiedad) c1;
        Propiedad p2 = (Propiedad) c2;
        if(!j1.equals(p1.getPropietario()) || !j2.equals(p2.getPropietario())){
            throw new LogicaException("Uno de los jugadores no es el propietario de la propiedad\n");
        }
        if(cantidad <= 0){
            throw new LogicaException("La cantidad debe ser mayor que 0\n");
        }

        TratoPPC t = new TratoPPC(j1,j2,"trato-" + nTratos,p1,p2,cantidad,this.jugadorActual);
        this.nTratos += 1;
        j1.nuevoTrato(t);
        j2.nuevoTrato(t);
        if(j1.equals(this.jugadorActual)){
            consola.imprimir("%s, ¿te doy %s y tú me das %s y %.2f?\n".formatted(j2.getNombre(),p1.getNombre(),p2.getNombre(),cantidad));
            t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: %s me da %s y yo le doy %s y %.2f.\n".formatted(j2.getNombre(),j1.getNombre(),p1.getNombre(),p2.getNombre(),cantidad));
            return;
        }else{
            consola.imprimir("%s, ¿te doy %s y %.2f y tú me das %s?\n".formatted(j1.getNombre(),p1.getNombre(),cantidad,p2.getNombre()));
            t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: %s me da %s y %.2f y yo le doy %s.\n".formatted(j1.getNombre(),j2.getNombre(),p1.getNombre(),cantidad,p2.getNombre()));
            return;
        }
    }

    public void trato(String nombreJugador2, String propiedad1,String propiedad2,String propiedad3,int turnos) throws MonopolyException{
        if(!this.jugadores.containsKey(nombreJugador2)){
            throw new LogicaException("No existe ningún jugador con ese nombre\n");
        }
        Jugador j2 = this.jugadores.get(nombreJugador2);
        if(this.jugadorActual.equals(j2)){
            throw new LogicaException("No se puede hacer un trato con uno mismo\n");
        }
        Casilla c1 = this.getCasillaPorNombre(propiedad1);
        Casilla c2 = this.getCasillaPorNombre(propiedad2);
        Casilla c3 = this.getCasillaPorNombre(propiedad3);
        if(c1 == null || c2 == null || c3 == null){
            throw new LogicaException("No existe ninguna casilla con ese nombre\n");
        }
        if(!(c1 instanceof Propiedad) || !(c2 instanceof Propiedad) || !(c3 instanceof Propiedad)){
            throw new LogicaException("Una de las casillas no es una propiedad\n");
        }
        Propiedad p1 = (Propiedad) c1;
        Propiedad p2 = (Propiedad) c2;
        Propiedad p3 = (Propiedad) c3;
        if(!this.jugadorActual.equals(p1.getPropietario()) || !j2.equals(p2.getPropietario()) || !j2.equals(p3.getPropietario())){
            throw new LogicaException("Uno de los jugadores no es el propietario de la propiedad\n");
        }
        if(turnos <= 0){
            throw new LogicaException("El número de turnos debe ser mayor que 0\n");
        }

        TratoPPA t = new TratoPPA(this.jugadorActual,j2,"trato-" + nTratos,p1,p2,p3,turnos);
        this.nTratos += 1;
        this.jugadorActual.nuevoTrato(t);
        j2.nuevoTrato(t);
        consola.imprimir("%s, ¿te doy %s y tú me das %s y no pago alquiler en %s durante %d turnos?\n".formatted(j2.getNombre(),p1.getNombre(),p2.getNombre(),p3.getNombre(),turnos));
        t.setMensajeAceptar("Se ha aceptado el siguiente trato con %s: le doy %s y %s me da %s y no pago alquiler en %s durante %d turnos.\n".formatted(j2.getNombre(),p1.getNombre(),j2.getNombre(),p2.getNombre(),p3.getNombre(),turnos));
    }

    public void aceptar(String id) throws MonopolyException{
        this.jugadorActual.aceptarTrato(id);
    }

    public void tratos(){
        consola.imprimir(this.jugadorActual.listarTratos());
    }
    public void eliminarTrato(String id) throws MonopolyException{
        Trato t = this.jugadorActual.getTrato(id);
        if(!t.esProponente(this.jugadorActual)){
            throw new LogicaException("El jugador actual no es el proponente del trato\n");
        }
        t.getJugador1().eliminarTrato(id);
        t.getJugador2().eliminarTrato(id);
        consola.imprimir("Se ha eliminado el %s\n".formatted(id));
    }

    public void listarEnVenta(){
        for(Casilla c: this.casillas){
            if(c instanceof Propiedad p){
                if(p.getPropietario().equals(banca)){
                    consola.imprimir(p.toString() + "\n");
                }
            }
        }

    }
}

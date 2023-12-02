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
    public static Jugador banca; // <- No me gusta
    public static Dado dados;

    private Tablero tablero;
    private List<Casilla> casillas;
    private Map<ColorString.Color,Solar> solares;
    private Salida salida;
    private boolean haEmpezado;
    private Queue<Jugador> colaJugadores;
    private Map<String,Jugador> jugadores;
    private Map<Character,Jugador> avatares;
    
    private Jugador jugadorActual;
    
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
                                casillas.add(new Impuesto(campos[0],_posArrayATablero(i)));
                                break;
                            case "Transporte":
                                casillas.add(new Impuesto(campos[0],_posArrayATablero(i)));
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
                                casillas.add(new Carcel(campos[0],_posArrayATablero(i)));
                                break;
                            case "Salida":
                                this.salida = new Salida(campos[0],_posArrayATablero(i));
                                casillas.add(this.salida);
                                break;
                            case "Parking":
                                casillas.add(new Salida(campos[0],_posArrayATablero(i)));
                                break;
                        }
                        break;
                    case 4: 
                        Solar nuevoSolar = new Solar(campos[0],_posArrayATablero(i),Float.parseFloat(campos[3]),ColorString.Color.valueOf(campos[2]));
                        casillas.add(nuevoSolar);
                        this.solares.put(nuevoSolar.getGrupo(),nuevoSolar);
                        break;
                }
                i+=1;
            }
            this.casillas = new ArrayList<>(casillas);
            this.tablero = new Tablero(casillas);
            this.casillas.sort(comparador);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public boolean haEmpezado(){
        return this.haEmpezado;
    }
    public void inicializarJuego(){
        float totalSolares = 0;
        for(Solar s: this.solares.values()){
            totalSolares += s.valorInicial();
        }
        float premioVuelta = totalSolares/this.solares.size();
        this.salida.setPremio(premioVuelta);
        
        float impuesto = premioVuelta;
        for(Casilla c: this.casillas){
            if(c instanceof Impuesto){
                ((Impuesto)c).inicializarImpuesto(impuesto);
                impuesto /=2;
            }else if(c instanceof Transporte){
                ((Transporte)c).inicializar(premioVuelta);
            }else if(c instanceof Servicios){
                ((Servicios)c).inicializar(premioVuelta);
            }

            if( c instanceof Propiedad){
                ((Propiedad)c).setPropietario(this.banca);
            }
        }

        this.jugadorActual = this.colaJugadores.peek();


    }
    public void ejecutar(){
        consola.empezar(this);
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
    private boolean existeCasilla(String nombreCasilla){
        for(Casilla c: this.casillas){
            if(c.getNombre().equals(nombreCasilla)){
                return true;
            }
        }
        return false;

    }

    /*                                          */
    /*             Interfaz Comando             */
    /*                                          */
    /*                                          */
    public void verTablero(){
        consola.imprimir(tablero.toString());
    }
    public void empezar(){
        if(this.jugadores.size() < 2){
            consola.imprimir("No se puede empezar con menos de 2 jugadores\n");
            return;
        }
        if(this.haEmpezado){
            consola.imprimir("El juego ya ha empezado\n");
            return;
        }
        this.haEmpezado = true;
        this.inicializarJuego();
        consola.imprimir("El juego ha empezado\n");
        consola.imprimir("El jugador actual es %s\n".formatted(this.jugadorActual.getNombre()));
    }
    public void crearJugador(String nombreJugador, String tAvatar){
        if(this.colaJugadores.size() == NJUGADORES){
            consola.imprimir("No se pueden crear más jugadores\n");
            return;
        }
        if(this.haEmpezado){
            consola.imprimir("No se pueden crear jugadores una vez empezado el juego\n");
            return;
        }
        if(this.jugadores.containsKey(nombreJugador)){
            consola.imprimir("Ya existe un jugador con ese nombre\n");
            return;
        }
        String[] tAvataresDisponibles = {"coche","sombrero","esfinge","pelota"};
        boolean esTipoValido = false;
        for(String t:tAvataresDisponibles){
            if(t.equals(tAvatar)){
                esTipoValido = true;
            }
        }
        if(!esTipoValido){
            consola.imprimir("Ese no es un tipo válido de avatar\n");
            return;
        }
        //TODO crear avatar especifico
        char id = generarLetraAvatar();
        Avatar avatar = new Avatar(id,this.salida);
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
    public void describirJugador(String nombreJugador){
        if(!this.jugadores.containsKey(nombreJugador)){
            consola.imprimir("No existe ningún jugador con ese nombre\n");
            return;
        }
        consola.imprimir(this.jugadores.get(nombreJugador).toString());

    }
    public void describirCasilla(String nombreCasilla){
        if(!this.existeCasilla(nombreCasilla)){
            consola.imprimir("No existe ninguna casilla con ese nombre\n");
            return;
        }
        for(Casilla c: this.casillas){
            if(c.getNombre().equals(nombreCasilla)){
                consola.imprimir(c.toString());
                return;
            }
        }
    }
    public void describirAvatar(char idAvatar){
        if(!this.avatares.containsKey(idAvatar)){
            consola.imprimir("No existe ningún avatar con ese id\n");
            return;
        }
        consola.imprimir(this.avatares.get(idAvatar).toString());
    }
    public void lanzarDados(){
        int tirada = dados.lanzar();
        consola.imprimir("El jugador %s ha sacado un %d\n".formatted(this.jugadorActual.getNombre(),tirada));
        Avatar avatar = this.jugadorActual.getAvatar();
        Casilla casillaActual = avatar.getCasilla();
        int nuevaPosicion = casillaActual.getPosicion() + tirada;
        if(nuevaPosicion >= 40){
            nuevaPosicion -= 40;
            this.jugadorActual.cobrar(this.salida.getPremio());
            consola.imprimir("El jugador %s ha pasado por la salida y ha cobrado %.2f\n".formatted(this.jugadorActual.getNombre(),this.salida.getPremio()));
        }
        Casilla casillaNueva = this.casillas.get(nuevaPosicion);
        avatar.moverEnBasico(casillaNueva);
    }

}

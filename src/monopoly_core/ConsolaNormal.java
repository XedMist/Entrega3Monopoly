package monopoly_core;

import monopoly_exceptions.*;
import monopoly_exceptions.ArgumentosIncorrectosException.ArgumentosComandos;

import java.util.Scanner;

public class ConsolaNormal implements Consola{
    Scanner sc;
    public void imprimir(String s){
        System.out.print(s);
    }
    public String leer(String s){
        System.out.print(s);
        return sc.nextLine();
    }
    public void error(String s){
        this.imprimir(ColorString.error(s));
    }
    public ConsolaNormal(){
        sc = new Scanner(System.in);
    }
    public void empezar(Juego juego){
        imprimir("Bienvenido a Monopoly\n");
        while(true){
            String comando = leer("$>");
            if (comando.equals("salir")){
                break;
            }

            try{
                procesarComando(juego,comando);
            }catch(BancarrotaException e){
                Jugador jugador = e.getJugador();
                Jugador destinatario = e.getDestinatario();
                jugador.bancarrota(destinatario);
                imprimir(e.getMessage());
                try{
                    juego.acabarTurno();
                }catch(VictoriaException ex){
                    imprimir("El ganador es %s\n".formatted(ex.getGanador().getNombre()));
                    ex.getGanador().estadisticas();
                    System.exit(0);
                }
            }catch(VictoriaException e){
                imprimir("El ganador es %s\n".formatted(e.getGanador().getNombre()));
                e.getGanador().estadisticas();
                System.exit(0);
            }catch(ArgumentosIncorrectosException e){
                int numeroArgumentos = e.getNumeroArgumentos();
                String[] argumentos = e.getArgumentos();
                if(numeroArgumentos == 0){
                    error("Uso: %s.\n".formatted(argumentos[0]));
                }
                else if(numeroArgumentos == -1){ 
                    error("Uso: %s\n".formatted(argumentos[0]));
                    for(int i = 1; i < argumentos.length; i++){
                        error("\t- %s\n".formatted(argumentos[i]));
                    }
                }else {
                    error("Uso: %s %s.\n".formatted(argumentos[0],argumentos[numeroArgumentos]));
                }

            }catch(NoExisteComandoException e){
                error("No existe ese comando.\n");
                for(ArgumentosComandos c : ArgumentosComandos.values()){
                    error("- %s\n".formatted(c.getArgumento(0)));
                }
            }
            catch(LogicaException e){
                error(e.getMessage());
            }catch(MonopolyException e){
                error(e.getMessage());
            }

        }
        imprimir("Terminando...\n");
        sc.close();
    }
    private void procesarComando(Juego juego, String comando) throws MonopolyException{
        String[] args = comando.split(" ");
        if(!juego.haEmpezado()){
            if(args[0].equals("empezar")){
                juego.empezar();
                return;
            }else if(args.length == 4 && args[0].equals("crear") && args[1].equals("jugador")){
                juego.crearJugador(args[2],args[3]);
                return;
            }
            throw new NoExisteComandoException(); 
        }
        switch (args[0]){
            case "describir":
                comandoDescribir(juego,args);
                break;
            case "ver":
                comandoVer(juego,args);
                break;
            case "lanzar":
                comandoLanzar(juego,args);
                break;
            case "acabar":
                comandoAcabar(juego,args);
                break;
            case "comprar":
                comandoComprar(juego,args);
                break;
            case "jugador":
                comandoJugador(juego,args);
                break;
            case "listar":
                comandoListar(juego,args);
                break;
            case "edificar":
                comandoEdificar(juego,args);
                break;
            case "vender":
                comandoVender(juego,args);
                break;
            case "hipotecar":
                comandoHipotecar(juego,args);
                break;
            case "deshipotecar":
                comandoDeshipotecar(juego,args);
                break;
            case "bancarrota":
                comandoBancarrota(juego,args);
                break;
            case "estadisticas":
                comandoEstadisticas(juego,args);
                break;
            case "cambiar":
                comandoCambiar(juego,args);
                break;
            case "trato":
                comandoTrato(juego,args);
                break;
            case "aceptar":
                comandoAceptar(juego,args);
                break;
            case "tratos":
                comandoTratos(juego,args);
                break;
            case "eliminar":
                comandoEliminarTrato(juego,args);
                break;
            case "salir":
                comandoSalir(juego,args);
                break;
            case "debug":
                comandoDebug(juego,args);
                break;
            default:
                throw new NoExisteComandoException();
        }
    }
    private void comandoDescribir(Juego juego, String[] args) throws ArgumentosIncorrectosException,LogicaException{
        if(args.length == 1){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.describir,0);
        }
        switch(args[1]){
            case "jugador":
                if(args.length == 2){
                    throw new ArgumentosIncorrectosException(ArgumentosComandos.describir,2);
                }
                juego.describirJugador(args[2]);
                break;
            case "avatar":
                if(args.length == 2){
                    throw new ArgumentosIncorrectosException(ArgumentosComandos.describir,3);
                }
                if(args[2].length() != 1){
                    throw new ArgumentosIncorrectosException(ArgumentosComandos.describir,3);

                }
                juego.describirAvatar(args[2].charAt(0));
                break;
            default:
                juego.describirCasilla(args[1]);
        }
    }
    private void comandoVer(Juego juego, String[] args) throws ArgumentosIncorrectosException{
        if(args.length != 2 || !args[1].equals("tablero")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.verTablero,0);
        }
        juego.verTablero();
    }
    private void comandoLanzar(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2 || !args[1].equals("dados")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.lanzarDados,0);
        }
        juego.lanzarDados(false);
    }
    private void comandoAcabar(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.acabarTurno,0);
        }
        juego.acabarTurno();
    }

    private void comandoComprar(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.comprar,1);
        }
        juego.comprar(args[1]);
    }

    private void comandoDebug(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            imprimir("Ese comando necesita argumentos\n");
            return;
        }
        switch(args[1]){
            case "lanzar":
                juego.lanzarDados(true);
                break;
            default:
                throw new NoExisteComandoException();
        }
    }

    private void comandoJugador(Juego juego, String[] args) throws ArgumentosIncorrectosException{
        if(args.length != 1){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.jugador,0);
        }
        juego.jugador();
    }
    private void comandoListar(Juego juego, String[] args) throws ArgumentosIncorrectosException{
        if(args.length < 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.listar,-1);
        }
        switch(args[1]){
            case "jugadores":
                juego.listarJugadores();
                break;
            case "avatares":
                juego.listarAvatares();
                break;
            case "edificios":
                if(args.length == 3){
                    juego.listarEdificios(args[2]);
                    return;
                }
                juego.listarEdificios();
                break;
            case "enventa":
                juego.listarEnVenta();
                break;
            default:
                throw new ArgumentosIncorrectosException(ArgumentosComandos.listar,-1);
        }
    }
    private void comandoEdificar(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.edificar,1);
        }

        if(!args[1].equals("casa") && !args[1].equals("hotel") && !args[1].equals("piscina") && !args[1].equals("pista")){
            throw new ConsolaException("Ese tipo de edificio no es válido.\n");
        }
        juego.edificar(args[1]);
    }
    private void comandoVender(Juego juego, String[] args) throws ArgumentosIncorrectosException,LogicaException{
        if(args.length != 4){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.vender,1);
        }
        juego.vender(args[1],args[2],Integer.parseInt(args[3]));
    }

    private void comandoHipotecar(Juego juego, String[] args) throws ArgumentosIncorrectosException,LogicaException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.hipotecar,1);
        }
        juego.hipotecar(args[1]);
    }

    private void comandoDeshipotecar(Juego juego, String[] args) throws ArgumentosIncorrectosException,MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.deshipotecar,1);
        }
        juego.deshipotecar(args[1]);
    }

    private void comandoBancarrota(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 1){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.bancarrota,0);
        }
        juego.bancarrota();
    }

    private void comandoEstadisticas(Juego juego, String[] args) throws ArgumentosIncorrectosException, LogicaException {
        if (args.length == 1){
            juego.estadisticas();
            return;
        }
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.estadisticas,1);
        }
        juego.estadisticas(args[1]);
    }

    private void comandoCambiar(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2 || !args[1].equals("modo")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.cambiarModo,0);
        }
        juego.cambiarModo();
    }

    private void comandoTrato(Juego juego, String[] args) throws MonopolyException{
        switch(args.length){
            case 5:
                comandoTratoPP_PC(juego,args);
                break;
            case 7:
                comandoTratoPPC(juego,args);
                break;
            case 8:
                comandoTratoPPA(juego,args);
                break;
            default:
                throw new ArgumentosIncorrectosException(ArgumentosComandos.trato,-1);
        }

    }
    private void comandoTratoPP_PC(Juego juego, String[] args) throws MonopolyException{
        String nombreJugador2 = args[1].substring(0,args[1].length()-1);
        if(!args[2].equals("cambiar")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.trato,1);
        }
        String el1 = args[3].substring(1,args[3].length()-1);
        String el2 = args[4].substring(0,args[4].length()-1);

        String propiedad1 = null;
        String propiedad2 = null;
        float cantidad1 = 0; 
        float cantidad2 = 0;
        try{
            cantidad1 = Float.parseFloat(el1);
        }catch(NumberFormatException e){
            propiedad1 = el1;
        }

        try{
            cantidad2 = Float.parseFloat(el2);
        }catch(NumberFormatException e){
            propiedad2 = el2;
        }

        if(propiedad1 == null && propiedad2 == null){
            throw new NoExisteComandoException();
        }
        if(propiedad1 != null){
            if(propiedad2 != null){
                juego.trato(nombreJugador2,propiedad1,propiedad2);
                return;
            }
            juego.trato(juego.getJugadorActual().getNombre(),nombreJugador2,propiedad1,cantidad2);
            return;
        }
        juego.trato(nombreJugador2,juego.getJugadorActual().getNombre(),propiedad2,cantidad1);
    }

    private void comandoTratoPPC(Juego juego,String[] args) throws MonopolyException{
        String nombreJugador2 = args[1].substring(0,args[1].length()-1);
        if(!args[2].equals("cambiar")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.trato,2);
        }
        String el1 = args[3].substring(1,args[3].length()-1);
        String el2 = args[4];
        String el3 = args[6].substring(0,args[6].length()-1);

        if(!args[5].equals("y")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.trato,2);
        }

        String propiedad1 = null;
        String propiedad2 = null;
        float cantidad1 = 0; 
        float cantidad2 = 0;
        try{
            cantidad1 = Float.parseFloat(el2);
        }catch(NumberFormatException e){
            propiedad1 = el2;
        }

        try{
            cantidad2 = Float.parseFloat(el3);
        }catch(NumberFormatException e){
            propiedad2 = el3;
        }

        if((propiedad1 == null && propiedad2 == null) ||(propiedad1 != null && propiedad2 != null)){
            throw new ConsolaException("Ese tipo de trato no existe\n");
        }
        if(propiedad1 != null){
            juego.trato(juego.getJugadorActual().getNombre(),nombreJugador2,el1,propiedad1,cantidad2);
            return;
        }
        juego.trato(nombreJugador2,juego.getJugadorActual().getNombre(),propiedad2,el1,cantidad1);
        return;

    }

    public void comandoTratoPPA(Juego juego,String[] args) throws MonopolyException{
        String nombreJugador2 = args[1].substring(0,args[1].length()-1);
        if(!args[2].equals("cambiar")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.trato,3);
        }
        String el1 = args[3].substring(1,args[3].length()-1);
        String el2 = args[4].substring(0,args[4].length()-1);

        if(!args[5].equals("y")){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.trato,3);
        }

        for(int i = 0; i < args.length; i++){
            imprimir(i + ":" +args[i] + " ");
        }
        String el3 = args[6].substring(11,args[6].length()-1);
        String el4 = args[7].substring(0,args[7].length()-1);
        int turnos = 0;
        try{
            turnos = Integer.parseInt(el4);
        }catch(NumberFormatException e){
            throw new NoExisteComandoException();
        }
        juego.trato(nombreJugador2,el1,el2,el3,turnos);
    }

    private void comandoAceptar(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.aceptar,1);
        }
        juego.aceptar(args[1]);
    }
    private void comandoTratos(Juego juego, String[] args) throws ArgumentosIncorrectosException{
        if(args.length != 1){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.tratos,0);
        }
        juego.tratos();
    }
    private void comandoEliminarTrato(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.eliminarTrato,1);
        }
        juego.eliminarTrato(args[1]);
    }
    private void comandoSalir(Juego juego, String[] args) throws MonopolyException{
        if(args.length != 2){
            throw new ArgumentosIncorrectosException(ArgumentosComandos.salirCarcel,0);
        }
        juego.salirCarcel();
    }
}

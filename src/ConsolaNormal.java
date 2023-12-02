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
            }catch(ConsolaException e){
                imprimir(e.getMessage());
            }
        }
        imprimir("Terminando...\n");
        sc.close();
    }
    private void procesarComando(Juego juego, String comando) throws ConsolaException{
        String args[] = comando.split(" ");
        if(!juego.haEmpezado()){
            if(args[0].equals("empezar")){
                juego.empezar();
                return;
            }else if(args.length == 4 && args[0].equals("crear") && args[1].equals("jugador")){
                juego.crearJugador(args[2],args[3]);
                return;
            }
            imprimir("Ese comando o no existe o no se puede ejecutar ahora mismo\n");
            return;
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
        }
    }
    private void comandoDescribir(Juego juego, String[] args){
        if(args.length == 1){
            imprimir("Ese comando necesita un argumento\n");
            return;
        }
        switch(args[1]){
            case "jugador":
                if(args.length == 2){
                    imprimir("Ese comando necesita un argumento\n");
                    return;
                }
                juego.describirJugador(args[2]);
                break;
            case "avatar":
                if(args.length == 2){
                    imprimir("Ese comando necesita un argumento\n");
                    return;
                }
                if(args[2].length() != 1){
                    imprimir("Ese comando necesita un argumento de un caracter\n");
                    return;

                }
                juego.describirAvatar(args[2].charAt(0));
                break;
            default:
                juego.describirCasilla(args[1]);
        }
    }
    private void comandoVer(Juego juego, String[] args){
        if(args.length != 2 || !args[1].equals("tablero")){
            imprimir("Ese comando necesita un argumento\n");
            return;
        }
        juego.verTablero();
    }
    private void comandoLanzar(Juego juego, String[] args){
        if(args.length != 1){
            imprimir("Ese comando no necesita argumentos\n");
            return;
        }
        juego.lanzarDados();
    }
}

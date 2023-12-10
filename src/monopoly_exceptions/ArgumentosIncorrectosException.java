package monopoly_exceptions;

public class ArgumentosIncorrectosException extends ConsolaException{
    
    public enum ArgumentosComandos{
        describir(new String[]{"describir","<nombreCasilla>","jugador <nombreJugador>","avatar <idAvatar>"}),
        verTablero(new String[]{"ver tablero"}),
        lanzarDados(new String[]{"lanzar dados"}),
        acabarTurno(new String[]{"acabar turno"}),
        comprar(new String[]{"comprar","<nombreCasilla>"}),
        jugador(new String[]{"jugador"}),
        listar(new String[]{"listar","jugadores","avatares","en venta","edificios","edificios <grupo>"}),
        edificar(new String[]{"edificar","<tipoEdificio>"}),
        vender(new String[]{"vender","<tipoEdificio>","<nombreCasilla>","<numero>"}),
        hipotecar(new String[]{"hipotecar","<nombreCasilla>"}),
        bancarrota(new String[]{"bancarrota"}),
        deshipotecar(new String[]{"deshipotecar","<nombreCasilla>"}),
        estadisticas(new String[]{"estadisticas","<nombreJugador>"}),
        cambiarModo(new String[]{"cambiar modo"}),
        trato(new String[]{"trato","<nombreJugador>: cambiar (<1>, <2>)","<nombreJugador>: cambiar (<1>, <2> y <3>)","<nombreJugador>: cambiar (<1>, <2>) y noalquiler(<3>, <4>)"}),
        aceptar(new String[]{"aceptar","<identificador>"}),
        tratos(new String[]{"tratos"}),
        eliminarTrato(new String[]{"eliminar trato","<identificador>"}),
        salirCarcel(new String[]{"salir carcel"}); 
        private String[] argumentos;
        ArgumentosComandos(String[] argumentos){
            this.argumentos = argumentos;
        }

        public String[] getArgumentos(){
            return this.argumentos;
        }
        public String getArgumento(int i){
            return this.argumentos[i];
        }
        public String toString(){
            String s = "";
            for(String arg:this.argumentos){
                s += arg + " ";
            }
            return s;
        }
    }
    private ArgumentosComandos argumentos;
    private int numeroArgumentos;
    public ArgumentosIncorrectosException(ArgumentosComandos comando,int i){
        super("");
        this.numeroArgumentos = i;
        this.argumentos = comando;
    }

    public String[] getArgumentos(){
        return this.argumentos.getArgumentos();
    }
    public int getNumeroArgumentos(){
        return this.numeroArgumentos;
    }
    public String getMensaje(){
        if(this.numeroArgumentos == 0){
            return "Uso: %s".formatted(this.argumentos.getArgumento(0));
        }
        return """
            Uso: %s %s
            """.formatted(this.argumentos.getArgumento(0),this.argumentos.getArgumento(this.numeroArgumentos));
    }


}

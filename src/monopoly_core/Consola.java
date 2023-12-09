package monopoly_core;

public interface Consola {
    void imprimir(String s);
    String leer(String s);
    void empezar(Juego juego);
    void error(String s);
}

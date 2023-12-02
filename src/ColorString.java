public class ColorString {
    public enum Color{
        Rojo("\u001B[38;5;1m"),
        Verde("\u001B[38;5;2m"),
        Amarillo("\u001B[38;5;3m"),
        Azul("\u001B[38;5;4m"),
        Rosa("\u001B[38;5;5m"),
        //Naranja("\u001B[38;5;202m"),
        Morado("\u001B[38;5;93m"),
        Cyan("\u001B[38;5;123m"),
        Blanco("\u001B[38;5;15m"),
        //GrisClaro("\u001B[38;5;250m"),
        VerdeClaro("\u001B[38;5;118m");
        private String value;
        private Color(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_UNDERLINE = "\u001B[4m";

    private String content;
    private Color color;

    public ColorString(String content){
        this.content = content;
    };
    public ColorString(String content,Color c){
        this.content = content;
        this.color = c;
    };
    public ColorString setColor(Color c){
        this.color = c;
        return this;
    }

    @Override
    public String toString() {
        if (this.color == null){
            return ANSI_UNDERLINE + this.content + ANSI_RESET;
        }
        return this.color.getValue() + ANSI_UNDERLINE + this.content + ANSI_RESET;
    }

}



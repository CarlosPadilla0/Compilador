public class Token {
    private TokenType tipo;
    private String valor;
    private boolean pr;

    public Token(TokenType tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.pr = false;
    }

	public TokenType getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public boolean isPr() {
        return pr;
    }

    public void setPr(boolean pr) {
        this.pr = pr;
    }

    private String ajustarAncho(String valor, int ancho) {
        if (valor.length() < ancho) {
            return String.format("%-" + ancho + "s", valor);
        }
        return valor.substring(0, ancho);
    }


    @Override
    public String toString() {
        int anchoValor = 12;
        String valorStr = ajustarAncho(valor, anchoValor);
        if (isPr()) {
            valorStr += "PR";
        }
        return String.format("%s | %s", valorStr, tipo);
    }
}


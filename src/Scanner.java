import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private String contenido;
    private List<TokenObj>tokens;

    public void setContenido(String texto) {
        this.contenido = texto;
    }

    public String getContenido() {
        return contenido;
    }

    public void reset() {
        this.contenido = "";
        this.tokens.clear();
    }

    public String analizarSintactico() {
    	Parser parser = new Parser(tokens);
    	parser.parse();
    	return parser.getLineaError();
    }

    public List<TokenObj> analizarLexico() {
       tokens = new ArrayList<>();
        int i = 0;

        while (i < contenido.length()) {
            char currentChar = contenido.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                i++;
                continue;
            }

            // palabras reservadas o identificadores
            if (Character.isLetter(currentChar)) {
                StringBuilder sb = new StringBuilder();
                while (i < contenido.length() && Character.isLetterOrDigit(contenido.charAt(i))) {
                    sb.append(contenido.charAt(i));
                    i++;
                }
                String word = sb.toString();

                TokenObj nuevoToken;
                switch (word) {
                    case "START":
                        nuevoToken = new TokenObj(TokenType.START, word);
                        nuevoToken.setPr(true);
                        break;
                    case "END":
                        nuevoToken = new TokenObj(TokenType.END, word);
                        nuevoToken.setPr(true);
                        break;
                    case "INT":
                        nuevoToken = new TokenObj(TokenType.INT, word);
                        nuevoToken.setPr(true);
                        break;
                    case "BOOLEAN":
                        nuevoToken = new TokenObj(TokenType.BOOLEAN, word);
                        nuevoToken.setPr(true);
                        break;
                    case "DOUBLE":
                        nuevoToken = new TokenObj(TokenType.DOUBLE, word);
                        nuevoToken.setPr(true);
                        break;
                    case "WHILE":
                        nuevoToken = new TokenObj(TokenType.WHILE, word);
                        nuevoToken.setPr(true);
                        break;
                    case "IF":
                        nuevoToken = new TokenObj(TokenType.IF, word);
                        nuevoToken.setPr(true);
                        break;
                    case "ELSE":
                        nuevoToken = new TokenObj(TokenType.ELSE, word);
                        nuevoToken.setPr(true);
                        break;
                    default:
                        nuevoToken = new TokenObj(TokenType.IDENTIFICADOR, word);
                        break;
                }
                tokens.add(nuevoToken);
                continue;
            }

            // Números
            if (Character.isDigit(currentChar)) {
                StringBuilder sb = new StringBuilder();
                while (i < contenido.length() && Character.isDigit(contenido.charAt(i))) {
                    sb.append(contenido.charAt(i));
                    i++;
                }
                tokens.add(new TokenObj(TokenType.NUMERO, sb.toString()));
                continue;
            }

            // Operadores
            if (currentChar == '+' || currentChar == '-' || currentChar == '*') {
                tokens.add(new TokenObj(TokenType.OPERADOR, String.valueOf(currentChar)));
                i++;
                continue;
            }

            // Operadores lógicos y asignación
            if (currentChar == '!' || currentChar == '<' || currentChar == '>') {
                StringBuilder sb = new StringBuilder();
                sb.append(currentChar);
                i++;
                if (i < contenido.length() && contenido.charAt(i) == '=') {
                    sb.append(contenido.charAt(i));
                    i++;
                    tokens.add(new TokenObj(TokenType.OPERADOR_RELACIONAL, sb.toString()));
                } else {
                    tokens.add(new TokenObj(TokenType.OPERADOR_RELACIONAL, sb.toString()));
                }
                continue;
            }

            if (currentChar == '=') {
                if (i + 1 < contenido.length() && contenido.charAt(i + 1) == '=') {
                    tokens.add(new TokenObj(TokenType.OPERADOR_RELACIONAL, "=="));
                    i += 2;
                } else {
                    tokens.add(new TokenObj(TokenType.IGUAL, "="));
                    i++;
                }
                continue;
            }

            // Paréntesis y llaves
            switch (currentChar) {
                case '(':
                    tokens.add(new TokenObj(TokenType.PARENTESIS_IZQUIERDO, "("));
                    break;
                case ')':
                    tokens.add(new TokenObj(TokenType.PARENTESIS_DERECHO, ")"));
                    break;
                case '{':
                    tokens.add(new TokenObj(TokenType.LLAVE_IZQUIERDA, "{"));
                    break;
                case '}':
                    tokens.add(new TokenObj(TokenType.LLAVE_DERECHA, "}"));
                    break;
                case ';':
                    tokens.add(new TokenObj(TokenType.PUNTO_COMA, ";"));
                    break;
                default:
                    tokens.add(new TokenObj(TokenType.DESCONOCIDO, String.valueOf(currentChar)));
                    break;
            }

            i++;
        }

        return tokens;
    }
}

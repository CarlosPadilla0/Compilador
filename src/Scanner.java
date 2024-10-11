import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private String contenido;
    private List<Token>tokens;
    private Parser parser;
    private Semantic semantico;

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
    	parser = new Parser(tokens);
    	parser.parse();
    	return parser.getLineaError();
    }
    public String analizarSemantico() {
        semantico = new Semantic(tokens);
        return semantico.analyze();
    }
    
    public String generarCodigoIntermedio() {
    	return semantico.generarCodigoIntermedio();
    }


    public List<Token> analizarLexico() {
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

                Token nuevoToken;
                switch (word) {
                    case "START":
                        nuevoToken = new Token(TokenType.START, word);
                        nuevoToken.setPr(true);
                        break;
                    case "END":
                        nuevoToken = new Token(TokenType.END, word);
                        nuevoToken.setPr(true);
                        break;
                    case "INT":
                        nuevoToken = new Token(TokenType.INT, word);
                        nuevoToken.setPr(true);
                        break;
                    case "BOOLEAN":
                        nuevoToken = new Token(TokenType.BOOLEAN, word);
                        nuevoToken.setPr(true);
                        break;
                    case "WHILE":
                        nuevoToken = new Token(TokenType.WHILE, word);
                        nuevoToken.setPr(true);
                        break;
                    case "IF":
                        nuevoToken = new Token(TokenType.IF, word);
                        nuevoToken.setPr(true);
                        break;
                    case "ELSE":
                        nuevoToken = new Token(TokenType.ELSE, word);
                        nuevoToken.setPr(true);
                        break;
                    case "STRING":
                        nuevoToken = new Token(TokenType.STRING, word);
                        nuevoToken.setPr(true);
                        break;

                    default:
                        nuevoToken = new Token(TokenType.IDENTIFICADOR, word);
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
                tokens.add(new Token(TokenType.NUMERO, sb.toString()));
                continue;
            }

            // Operadores
            if (currentChar == '+' || currentChar == '-' || currentChar == '*') {
                tokens.add(new Token(TokenType.OPERADOR, String.valueOf(currentChar)));
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
                    tokens.add(new Token(TokenType.OPERADOR_RELACIONAL, sb.toString()));
                } else {
                    tokens.add(new Token(TokenType.OPERADOR_RELACIONAL, sb.toString()));
                }
                continue;
            }

            if (currentChar == '=') {
                if (i + 1 < contenido.length() && contenido.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.OPERADOR_RELACIONAL, "=="));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.IGUAL, "="));
                    i++;
                }
                continue;
            }
            if (currentChar == '"') {
                i++;  
                StringBuilder sb = new StringBuilder();
                
                while (i < contenido.length() && contenido.charAt(i) != '"') {
                    sb.append(contenido.charAt(i));
                    i++;
                }
                
                if (i < contenido.length() && contenido.charAt(i) == '"') {
                    i++;  
                    tokens.add(new Token(TokenType.TEXTO, sb.toString()));
                } 
                continue;
            }


            // Paréntesis y llaves
            switch (currentChar) {
                case '(':
                    tokens.add(new Token(TokenType.PARENTESIS_IZQUIERDO, "("));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.PARENTESIS_DERECHO, ")"));
                    break;
                case '{':
                    tokens.add(new Token(TokenType.LLAVE_IZQUIERDA, "{"));
                    break;
                case '}':
                    tokens.add(new Token(TokenType.LLAVE_DERECHA, "}"));
                    break;
                case ';':
                    tokens.add(new Token(TokenType.PUNTO_COMA, ";"));
                    break;
                default:
                    tokens.add(new Token(TokenType.DESCONOCIDO, String.valueOf(currentChar)));
                    break;
            }

            i++;
        }

        return tokens;
    }
}

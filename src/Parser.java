import java.util.List;

public class Parser {
    private List<TokenObj> tokens;
    private int posicion;
    private TokenObj tokenActual;
    private String errorMsg;

    public Parser(List<TokenObj> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
        this.tokenActual = tokens.get(0);
        this.errorMsg = null;
    }

    private void avanzar() {
        posicion++;
        if (posicion < tokens.size()) {
            tokenActual = tokens.get(posicion);
            System.out.println("Avanzando a token: " + tokenActual.getTipo() + " en posición " + posicion);

        } else {
            tokenActual = null;
            System.out.println("No hay más tokens.");
        }
    }
    
    public void parse() {
        programa();
        if (errorMsg != null) {
            System.out.println("Error: " + errorMsg);
        } else {
            System.out.println("Análisis sintáctico completado sin errores.");
        }
    }
    // PROGRAMA → START INSTRUCCIONES END
    private void programa() {
    	if (tokens.get(tokens.size()-1).getTipo() != TokenType.END) {
    		errorMsg="Se esperaba un END al final del programa";
    		System.out.println("No hubo we");
    		return;

    	}
    	if (tokens.get(0).getTipo() != TokenType.START) {
    		errorMsg="Se esperaba un START al INICIO del programa";
    		return;
    	}

    	avanzar();
    	instrucciones();

    }
    // INSTRUCCIONES → [[ INSTRUCCION;]]*
    private void instrucciones() {
        while (tokenActual != null ) {
            System.out.println("Procesando token: " + tokenActual.getTipo() + " en posición " + posicion);

            if (tokenActual.getTipo() == TokenType.END) {
                System.out.println("Fin del programa encontrado en posición " + posicion);
                break;
                
            }
            instruccion();
            avanzar();
        }
    }
    // INSTRUCCION → DATA_TYPE IDENTIFICADOR | IDENTIFICADOR = EXPRESION | WHILE EXPRESION {INSTRUCCIONES} | IF CONDICION {INSTRUCCIONES} [[ ELSE {INSTRUCCIONES}]]?
    private void instruccion() {
    	if (tipo(tokenActual)) {
    		declaracion();
    		return;
    	}

    	if(tokenActual.getTipo()==TokenType.IDENTIFICADOR) {
    		identificadorexp();
    		return;
    	}

    	if(tokenActual.getTipo()==TokenType.IF||tokenActual.getTipo()==TokenType.WHILE) {
        	System.out.println("Me cachondeo pq soy if"+tokenActual.getTipo());

        	CheckIfWhile();
        	return;
    	}

    	if(tokenActual.getTipo()==TokenType.END) {
    		errorMsg="No hay errores";
    		return;
    	}
    	System.out.println("A mi no me acomodaron"+ tokenActual.getTipo()+" "+tokenActual.getValor());
    	return;
    }
    // EXPRESION → IDENTIFICADOR | NUMERO | (EXPRESION OPERADOR EXPRESION)
    private void expresion() {
        if (tokenActual.getTipo() == TokenType.IDENTIFICADOR ||
            tokenActual.getTipo() == TokenType.NUMERO) {
            avanzar();
            return;
        }
        if (tokenActual.getTipo() == TokenType.PARENTESIS_IZQUIERDO) {
            avanzar();
            expresion();

            if (tokenActual.getTipo() != TokenType.OPERADOR) {
                errorMsg = "Se esperaba un operador válido";
                return;
            }

            avanzar();
            expresion();

            if (tokenActual.getTipo() != TokenType.PARENTESIS_DERECHO) {
                errorMsg = "Falta el paréntesis de cierre";
                return;
            }
            avanzar();
            return;
        }

        errorMsg = "Se esperaba un identificador, número o paréntesis";
    }
    // CONDICION → EXPRESION OPERADOR_LOGICO EXPRESION
    private void condicion() {
        if (tokenActual.getTipo() != TokenType.NUMERO && tokenActual.getTipo() != TokenType.IDENTIFICADOR) {
            errorMsg = "Error en la condición: se esperaba un número o identificador, pero se encontró " + tokenActual.getTipo();
            return;
        }
        avanzar();

        if (tokenActual.getTipo() != TokenType.OPERADOR_RELACIONAL) {
            errorMsg = "Se esperaba un operador relacional en la condición.";
            return;
        }
        avanzar();

        if (tokenActual.getTipo() != TokenType.NUMERO && tokenActual.getTipo() != TokenType.IDENTIFICADOR) {
            errorMsg = "Error en la condición: se esperaba un número o identificador después del operador relacional.";
            return;
        }
        avanzar();
    }

    private void declaracion() {
    	avanzar();
    	if (tokenActual.getTipo() != TokenType.IDENTIFICADOR) {
    		errorMsg = "Se esperaba un identificador en la declaración.";
    		return;
    	}
    	avanzar();
    	if(tokenActual.getTipo()!= TokenType.PUNTO_COMA) {
    		errorMsg="Se espera punto y coma en la instruccion";
    		return;
    	}
    	avanzar();
    	return;
    }

    private void identificadorexp() {
        avanzar();
        if (tokenActual.getTipo() != TokenType.IGUAL) {
            errorMsg = "Se esperaba un = en la instrucción de asignación.";
            return;
        }

        avanzar(); 
        expresion();

        if (tokenActual.getTipo() != TokenType.PUNTO_COMA) {
            errorMsg = "Se esperaba un ; al final de la instrucción.";
            return;
        }

        avanzar(); 
    }

    private void CheckIfWhile() {
        if (tokenActual.getTipo() == TokenType.IF) {
            avanzar();
            condicion();
            if (errorMsg != null) {
                System.out.println("Error en la condición del IF: " + errorMsg);
                return;
            }

            if (tokenActual.getTipo() != TokenType.LLAVE_IZQUIERDA) {
                errorMsg = "Se esperaba llave de apertura '{' después de la condición del IF.";
                return;
            }
            avanzar();

            if (tokenActual.getTipo() == TokenType.LLAVE_DERECHA) {
                System.out.println("Bloque IF vacío, llave de cierre encontrada.");
                avanzar(); 
            } else {
                instrucciones();
                if(tokenActual.getTipo()==TokenType.END)
                	return;
                if (errorMsg != null) {
                    System.out.println("Error en las instrucciones del IF: " + errorMsg);
                    return;
                }
                System.out.println(tokenActual.getTipo()+"aaaa");
                if (tokenActual.getTipo() != TokenType.LLAVE_DERECHA) {
                	errorMsg = "Se esperaba llave de cierre '}' al final del bloque IF."+tokenActual.getTipo();
                	return;
                }
                System.out.println("Llave derecha encontrada: " + tokenActual.getTipo() + " " + tokenActual.getValor());
                avanzar();
            }

            if (tokenActual.getTipo() == TokenType.ELSE) {
            	avanzar(); 

            	if (tokenActual.getTipo() != TokenType.LLAVE_IZQUIERDA) {
            		errorMsg = "Se esperaba llave de apertura '{' después del ELSE.";
            		return;
            	}

            	avanzar();  
            	instrucciones();  

            	if (tokenActual.getTipo() != TokenType.LLAVE_DERECHA) {
            		errorMsg = "Se esperaba llave de cierre '}' al final del bloque ELSE.";
            		return;
            	}
            	avanzar();  
            }
        }
        if (tokenActual.getTipo() == TokenType.WHILE) {
        	avanzar();
        	condicion();
        	System.out.println("Llave " + tokenActual.getTipo() + " " + tokenActual.getValor());

        	if (tokenActual.getTipo() == TokenType.LLAVE_DERECHA) {
        		System.out.println("Bloque WHILE vacío, llave de cierre encontrada.");
        		avanzar(); 
        	} else {
        		instrucciones();
        		if(tokenActual.getTipo()==TokenType.END)
        			return;
        		if (errorMsg != null) {
        			System.out.println("Error en las instrucciones del WHILE: " + errorMsg);
        			return;
        		}
        		System.out.println(tokenActual.getTipo()+"aaaa");
        		if (tokenActual.getTipo() != TokenType.LLAVE_DERECHA) {
        			errorMsg = "Se esperaba llave de cierre '}' al final del bloque WHILE."+tokenActual.getTipo();
        			return;
        		}
        		System.out.println("Llave derecha encontrada: " + tokenActual.getTipo() + " " + tokenActual.getValor());
        		avanzar();
        	}
        }
    }

    private boolean tipo(TokenObj tok) {
    	return tok.getTipo()==TokenType.BOOLEAN||tok.getTipo()==TokenType.DOUBLE||tok.getTipo()==TokenType.INT;
    }

    public String getLineaError() {
        if (errorMsg != null) {
            return errorMsg;
        } else {
            return "Análisis sintáctico completado sin errores.";
        }
    }
} 

    
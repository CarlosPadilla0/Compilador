import java.util.List;

public class Parser {
	private List<Token> tokens;
	private int posicion;
	private Token tokenActual;
	private String errorMsg;

	public Parser(List<Token> tokens) {
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

	private void instrucciones() {
	    while (tokenActual != null) {
	        System.out.println("Procesando token: " + tokenActual.getTipo() + " en posición " + posicion);

	        if (tokenActual.getTipo() == TokenType.LLAVE_DERECHA) {
	            System.out.println("Se encontró llave de cierre '}' en posición " + posicion);
	            return; 
	        }

	        if (tokenActual.getTipo() == TokenType.END) {
	            System.out.println("Fin del programa encontrado en posición " + posicion);
	            break;
	        }

	        instruccion();
	        
	        if (errorMsg != null) {
	            return;
	        }
	    }
	}

	private void instruccion() {
	    if (errorMsg != null) {
	        return;
	    }

	    if (tipo(tokenActual)) {
	        declaracion();
	        return;
	    }

	    if(tokenActual.getTipo() == TokenType.IDENTIFICADOR) {
	        identificadorexp();
	        return;
	    }

	    if(tokenActual.getTipo() == TokenType.IF ) {
	        System.out.println("Me cachondeo pq soy if" + tokenActual.getTipo());
	        CheckIf();
	        return;
	    }
	    if (tokenActual.getTipo()==TokenType.WHILE) {
	    	CheckWhile();
	    	return;
	    	
	    }
	    if(tokenActual.getTipo() == TokenType.END) {
	        errorMsg = "No hay errores";
	        return;
	    }

	    if(tokenActual.getTipo() == TokenType.LLAVE_DERECHA) {
	        avanzar();
	        return;
	    }

	    System.out.println("A mi no me acomodaron " + tokenActual.getTipo() + " " + tokenActual.getValor());
	    errorMsg="Syntax Error";
	    return;
	}

	private void expresion() {
	    if (tokenActual.getTipo() == TokenType.IDENTIFICADOR ||
	        tokenActual.getTipo() == TokenType.NUMERO ||
	        tokenActual.getTipo() == TokenType.TEXTO) {  
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

	    errorMsg = "Se esperaba un identificador, número, cadena o paréntesis";
	}

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
	    
	
	    if ( tokenActual.getTipo() != TokenType.TEXTO) {
	        errorMsg = "Error: Se esperaba una cadena de texto para la variable de tipo STRING.";
	        return;
	    }

	    expresion();

	    if (tokenActual.getTipo() != TokenType.PUNTO_COMA) {
	        errorMsg = "Se esperaba un ; al final de la instrucción.";
	        return;
	    }

	    avanzar();
	}

	private void CheckIf() {
	    if (tokenActual == null) {
	        errorMsg = "Error: token inesperadamente null al procesar IF o WHILE.";
	        return;
	    }

	    if (tokenActual.getTipo() == TokenType.IF) {
	        avanzar();  
	        condicion();  
	        if (errorMsg != null) {
	            System.out.println("Error en la condición del IF: " + errorMsg);
	            return;
	        }

	        if (tokenActual == null || tokenActual.getTipo() != TokenType.LLAVE_IZQUIERDA) {
	            errorMsg = "Se esperaba llave de apertura '{' después de la condición del IF.";
	            return;
	        }

	        avanzar();  

	        
	        if (tokenActual != null && tokenActual.getTipo() == TokenType.LLAVE_DERECHA) {
	            System.out.println("Bloque vacío encontrado para IF.");
	            avanzar();  
	            return;
	        }

	        instrucciones();

	        if (errorMsg != null) {
	            return;
	        }

	        if (tokenActual == null || tokenActual.getTipo() != TokenType.LLAVE_DERECHA) {
	            errorMsg = "Se esperaba llave de cierre '}' al final del bloque IF.";
	            return;
	        }

	        avanzar(); 
	       
	        if (tokenActual != null && tokenActual.getTipo() == TokenType.END) {
	            return;
	        }
	        
	        if (tokenActual != null && tokenActual.getTipo() == TokenType.ELSE) {
	            avanzar();  

	            if (tokenActual == null || tokenActual.getTipo() != TokenType.LLAVE_IZQUIERDA) {
	                errorMsg = "Se esperaba llave de apertura '{' después del ELSE.";
	                return;
	            }

	            avanzar(); 
	            instrucciones();  

	            if (errorMsg != null) {
	                return;
	            }

	            if (tokenActual == null || tokenActual.getTipo() != TokenType.LLAVE_DERECHA) {
	                errorMsg = "Se esperaba llave de cierre '}' al final del bloque ELSE.";
	                return;
	            }

	            avanzar();  
	        }
	        if (tokenActual != null && tokenActual.getTipo() == TokenType.END) {
	        	return;
	        }
	    }
	}

	private void CheckWhile() {
	    if (tokenActual == null) {
	        errorMsg = "Error: token inesperadamente null al procesar WHILE.";
	        return;
	    }

	    if (tokenActual.getTipo() == TokenType.WHILE) {
	        avanzar();  
	        condicion();  
	        if (errorMsg != null) {
	            System.out.println("Error en la condición del WHILE: " + errorMsg);
	            return;
	        }

	        if (tokenActual == null || tokenActual.getTipo() != TokenType.LLAVE_IZQUIERDA) {
	            errorMsg = "Se esperaba llave de apertura '{' después de la condición del WHILE.";
	            return;
	        }

	        avanzar(); 

	        if (tokenActual != null && tokenActual.getTipo() == TokenType.LLAVE_DERECHA) {
	            System.out.println("Bloque WHILE vacío, llave de cierre encontrada.");
	            avanzar();  
	            return;
	        }

	        instrucciones();  

	        if (errorMsg != null) {
	            System.out.println("Error en las instrucciones del WHILE: " + errorMsg);
	            return;
	        }

	        if (tokenActual == null || tokenActual.getTipo() != TokenType.LLAVE_DERECHA) {
	            errorMsg = "Se esperaba llave de cierre '}' al final del bloque WHILE.";
	            return;
	        }

	        System.out.println("Llave derecha encontrada: " + tokenActual.getTipo() + " " + tokenActual.getValor());
	        avanzar(); 
	    }
	}

	private boolean tipo(Token tok) {
		return tok.getTipo()==TokenType.BOOLEAN||tok.getTipo()==TokenType.STRING||tok.getTipo()==TokenType.INT;
	}

	public String getLineaError() {
		if (errorMsg != null) {
			return errorMsg;
		} else {
			return "Análisis sintáctico completado sin errores.";
		}
	}
	
	
}

import java.util.List;
import java.util.Map;

public class IntermediateCodeGenerator {
    private List<Token> tokens;
    private Map<String, String> symbolTable;
    private int labelCounter;

    public IntermediateCodeGenerator(Map<String, String> symbolTable, List<Token> tokens) {
        this.symbolTable = symbolTable;
        this.tokens = tokens;
        this.labelCounter = 0;
    }

    public String generate() {
        StringBuilder code = new StringBuilder();
        code.append(".DATA\n");

        for (Map.Entry<String, String> entry : symbolTable.entrySet()) {
            String varName = entry.getKey();
            String varType = entry.getValue();

            if ("INT".equals(varType)) {
                code.append(varName).append(" dw ?\n"); 
            } 
            if ("BOOLEAN".equals(varType)) {
                code.append(varName).append(" db ?\n"); 
            }
            if ("STRING".equals(varType)) {
                code.append(varName).append(" db 80 dup('$')\n");
            }
        }

        code.append(".CODE\n");
        code.append("START:\n");

        System.out.println("Tokens iniciales: " + tokens);

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            System.out.println("Procesando token: " + token.getValor()); 

            if (token.getTipo() == TokenType.PARENTESIS_IZQUIERDO || token.getTipo() == TokenType.PARENTESIS_DERECHO) {
                System.out.println("Se omite paréntesis"); 
                continue; 
            }

            if (token.getTipo() == TokenType.IDENTIFICADOR && i + 2 < tokens.size() && tokens.get(i + 1).getValor().equals("=")) {
                Token valorAsignacion = tokens.get(i + 2);

                if (valorAsignacion.getTipo() != TokenType.PARENTESIS_IZQUIERDO && valorAsignacion.getTipo() != TokenType.PARENTESIS_DERECHO) {
                    code.append("MOV ").append(token.getValor()).append(", ").append(valorAsignacion.getValor()).append("\n");
                    System.out.println("Generando asignación: MOV " + token.getValor() + ", " + valorAsignacion.getValor()); // Depuración
                    i += 2;  
                }
            }

            // Suma y resta
            else if (token.getTipo() == TokenType.IDENTIFICADOR) {
                Token nextToken = tokens.get(i + 1); 

                // Suma
                if (nextToken.getTipo() == TokenType.OPERADOR && nextToken.getValor().equals("+")) {
                    Token valorDerecha = tokens.get(i + 2);  

                    if (valorDerecha.getTipo() != TokenType.PARENTESIS_IZQUIERDO && valorDerecha.getTipo() != TokenType.PARENTESIS_DERECHO) {
                        code.append("MOV AX, ").append(token.getValor()).append("\n");
                        code.append("ADD AX, ").append(valorDerecha.getValor()).append("\n");
                        code.append("MOV ").append(token.getValor()).append(", AX\n");
                        System.out.println("Generando suma: MOV AX, " + token.getValor() + " + " + valorDerecha.getValor()); // Depuración
                        i += 2;
                    }
                }

                // Resta
                else if (nextToken.getTipo() == TokenType.OPERADOR && nextToken.getValor().equals("-")) {
                    Token valorDerecha = tokens.get(i + 2);

                    if (valorDerecha.getTipo() != TokenType.PARENTESIS_IZQUIERDO && valorDerecha.getTipo() != TokenType.PARENTESIS_DERECHO) {
                        code.append("MOV AX, ").append(token.getValor()).append("\n");
                        code.append("SUB AX, ").append(valorDerecha.getValor()).append("\n");
                        code.append("MOV ").append(token.getValor()).append(", AX\n");
                        System.out.println("Generando resta: MOV AX, " + token.getValor() + " - " + valorDerecha.getValor()); // Depuración
                        i += 2;
                    }
                }
            }

            // Comparación (if)
            if (token.getTipo() == TokenType.IF) {
                Token exprIzquierda = tokens.get(i + 1);
                Token operadorLogico = tokens.get(i + 2);
                Token exprDerecha = tokens.get(i + 3);
                String labelFinal = "FINAL_" + (labelCounter++);

                code.append("CMP ").append(exprIzquierda.getValor()).append(", ").append(exprDerecha.getValor()).append("\n");
                System.out.println("Generando comparación IF: CMP " + exprIzquierda.getValor() + ", " + exprDerecha.getValor()); // Depuración

                switch (operadorLogico.getValor()) {
                    case "==":
                        code.append("JE ").append(labelFinal).append("\n");
                        break;
                    case "!=":
                        code.append("JNE ").append(labelFinal).append("\n");
                        break;
                    case "<":
                        code.append("JL ").append(labelFinal).append("\n");
                        break;
                    case ">":
                        code.append("JG ").append(labelFinal).append("\n");
                        break;
                    case "<=":
                        code.append("JLE ").append(labelFinal).append("\n");
                        break;
                    case ">=":
                        code.append("JGE ").append(labelFinal).append("\n");
                        break;
                }

                code.append("MOV ").append(exprIzquierda.getValor()).append(", 1\n");
                code.append(labelFinal).append(":\n");

                i += 4;
            }

            System.out.println("Código generado hasta ahora: \n" + code.toString());
        }

        code.append("END START\n");

        System.out.println("Código final generado:\n" + code.toString());

        return code.toString();
    }
}

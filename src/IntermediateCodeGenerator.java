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
        code.append("	.DATA\n");

        for (Map.Entry<String, String> entry : symbolTable.entrySet()) {
            String varName = entry.getKey();
            String varType = entry.getValue();

            if ("INT".equals(varType)) {
                code.append(varName).append(" 	DW	 ?\n");
            }
            if ("BOOLEAN".equals(varType)) {
                code.append(varName).append("	DB	 ?\n");
            }
            if ("STRING".equals(varType)) {
                code.append(varName).append(" 	DB	 80 dup('$')\n");
            }
        }

        code.append("	.CODE\n");
        code.append("START:\n");

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (token.getTipo() == TokenType.PARENTESIS_IZQUIERDO || token.getTipo() == TokenType.PARENTESIS_DERECHO) {
                continue;
            }

            if (token.getTipo() == TokenType.IDENTIFICADOR && i + 2 < tokens.size() && tokens.get(i + 1).getValor().equals("=")) {
                Token valorAsignacion = tokens.get(i + 2);

                if ("STRING".equals(symbolTable.get(token.getValor()))) {
                    String stringValue = valorAsignacion.getValor();
                    code.append(generateStringAssignment(token.getValor(), stringValue)).append("\n");
                }
                if ("INT".equals(symbolTable.get(token.getValor()))) {
                    if (valorAsignacion.getTipo() != TokenType.PARENTESIS_IZQUIERDO && valorAsignacion.getTipo() != TokenType.PARENTESIS_DERECHO) {
                        code.append("	MOV ").append(token.getValor()).append(",").append(valorAsignacion.getValor())
                                .append("\n");
                    }
                }
                if ("BOOLEAN".equals(symbolTable.get(token.getValor()))) {
                    code.append("	MOV ").append(token.getValor()).append(",").append(valorAsignacion.getValor()).append("\n");
                }

                i += 2; 
            }

            if (token.getTipo() == TokenType.IF) {
                Token exprIzquierda = tokens.get(i + 1);
                Token operadorLogico = tokens.get(i + 2);
                Token exprDerecha = tokens.get(i + 3);
                String labelFinal = "FINAL_" + (labelCounter++);

                code.append("	CMP ").append(exprIzquierda.getValor()).append(",").append(exprDerecha.getValor()).append("\n");

                switch (operadorLogico.getValor()) {
                    case ">":
                        code.append("	JG ").append(labelFinal).append("\n");
                        break;
                    case "==":
                        code.append("	JE ").append(labelFinal).append("\n");
                        break;
                    case "!=":
                        code.append("	JNE ").append(labelFinal).append("\n");
                        break;
                    case "<":
                        code.append("	JL ").append(labelFinal).append("\n");
                        break;
                    case ">=":
                        code.append("	JGE ").append(labelFinal).append("\n");
                        break;
                    case "<=":
                        code.append("	JLE ").append(labelFinal).append("\n");
                        break;
                }
                code.append("	JMP ").append(labelFinal).append("\n");
                code.append(labelFinal).append(":\n");
                

                i += 4;  
            }
            if (token.getTipo() == TokenType.WHILE) {
                String startLabel = "INICIO_WHILE_" + labelCounter;
                String endLabel = "FIN_WHILE_" + (labelCounter++);
                
                Token exprIzquierda = tokens.get(i + 1);
                Token operadorLogico = tokens.get(i + 2);
                Token exprDerecha = tokens.get(i + 3);

                code.append(startLabel).append(":\n");
                code.append("	CMP").append(exprIzquierda.getValor()).append(",").append(exprDerecha.getValor()).append("\n");

                switch (operadorLogico.getValor()) {
                    case "==":
                        code.append("   JNE ").append(endLabel).append("\n");
                        break;
                    case "!=":
                        code.append("	JE ").append(endLabel).append("\n");
                        break;
                    case "<":
                        code.append("	JGE ").append(endLabel).append("\n");
                        break;
                    case ">":
                        code.append("	JLE ").append(endLabel).append("\n");
                        break;
                    case "<=":
                        code.append("	JG ").append(endLabel).append("\n");
                        break;
                    case ">=":
                        code.append("	JL ").append(endLabel).append("\n");
                        break;
                }

                code.append("	JMP ").append(startLabel).append("\n");
                code.append(endLabel).append(":\n");

                i += 4; 
            }

            if (token.getTipo() == TokenType.PRINT) {
                Token valorPrint = tokens.get(i + 1);

                if (valorPrint.getTipo() == TokenType.TEXTO) {
                    String stringValue = valorPrint.getValor();
                    code.append(generatePrintString(stringValue)).append("\n");
                } else if (valorPrint.getTipo() == TokenType.IDENTIFICADOR) {
                    code.append(generatePrintVariable(valorPrint.getValor())).append("\n");
                }

                i += 1;  
                continue;
            }

            if (token.getTipo() == TokenType.IDENTIFICADOR) {
                Token nextToken = tokens.get(i + 1);

                if (nextToken.getTipo() == TokenType.OPERADOR) {
                    Token valorDerecha = tokens.get(i + 2);

                    if (nextToken.getValor().equals("+")) {
                        code.append("	MOV AX, ").append(token.getValor()).append("\n");
                        code.append("	ADD AX, ").append(valorDerecha.getValor()).append("\n");
                        code.append("	MOV ").append(token.getValor()).append(", AX\n");
                    } else if (nextToken.getValor().equals("-")) {
                        code.append("	MOV AX, ").append(token.getValor()).append("\n");
                        code.append("	SUB AX, ").append(valorDerecha.getValor()).append("\n");
                        code.append("	MOV").append(token.getValor()).append(", AX\n");
                    }

                    i += 2;  
                }
            }
        }

        code.append("END START\n");
        return code.toString();
    }

    private String generateStringAssignment(String varName, String stringValue) {
        StringBuilder stringCode = new StringBuilder();
        char[] characters = stringValue.toCharArray();

        for (int j = 0; j < characters.length; j++) {
            stringCode.append("	MOV	 [").append(varName).append(" + ").append(j).append("], '").append(characters[j]).append("'\n");
        }

        return stringCode.toString();
    }

    private String generatePrintString(String stringValue) {
        StringBuilder stringCode = new StringBuilder();
        stringCode.append("	MOV	BX, 01H\n");
        stringCode.append("	LEA	DX, ").append(stringValue).append("\n");
        stringCode.append("	MOV	AH, 09H\n");
        stringCode.append("	INT	21H\n");
        return stringCode.toString();
    }

    private String generatePrintVariable(String varName) {
        StringBuilder stringCode = new StringBuilder();
        stringCode.append("	MOV	BX, 01H\n");
        stringCode.append("	LEA	DX, ").append(varName).append("\n");
        stringCode.append("	MOV	AH, 09H\n");
        stringCode.append("	INT	21H\n");
        return stringCode.toString();
    }
}

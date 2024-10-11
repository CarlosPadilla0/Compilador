import java.util.Map;

public class IntermediateCodeGenerator {
    private Map<String, String> symbolTable;

    public IntermediateCodeGenerator(Map<String, String> symbolTable) {
        this.symbolTable = symbolTable;
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
            if("STRING".equals(varType)){
            	code.append(varName).append(" db 80 dup ('$')");
            }
        }

        return code.toString();
    }
}

public class InstructionFormat {
   private final String mode;       // Nombre del modo de operación (ej. "Registro a registro")
    private final String binaryCode; //Formato asignado

    InstructionFormat(String mode, String binaryCode) {
        this.mode = mode;
        this.binaryCode = binaryCode;
    }

    public String getMode() {
        return mode;
    }

    public String getBinaryCode() {
        return binaryCode;
    }

}
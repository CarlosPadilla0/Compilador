import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjGenerator {

    private static final Map<String, String> registerMap = new HashMap<>();
    private static final Map<String, String> WMap = new HashMap<>();
    private static final Map<String, String> MODMap = new HashMap<>();
    private static final Map<String, String> RMMap = new HashMap<>();
    private static StringBuilder code = new StringBuilder();
    private static final Map<String, List<InstructionFormat>> instructionMap = new HashMap<>();
    private static List<Instruccion> instrucciones = new ArrayList<>();
    private int memoryAddressCounter = 0x0000; // Dirección de inicio de la memoria

    // Método para generar la dirección de memoria como una cadena hexadecimal de 4 dígitos
    private String generateMemoryAddress() {
        String address = String.format("%04X", memoryAddressCounter);
        return address;
    }

    // Método para incrementar la dirección de memoria en función del tipo de dato
    private void incrementMemoryAddress(String dataType) {
        switch (dataType) {
            case "DB":
                memoryAddressCounter += 2; // Incremento de 2 byte para datos tipo DB
                break;
            case "DW":
                memoryAddressCounter += 4; // Incremento de 4 bytes para datos tipo DW
                break;
            default:
                break;
        }
    }

    public ObjGenerator() {
        defRegs();
        defW();
        defMOD();
        defRM();
        defInstr();
    }

    private String getInstruccion(String instruccion, String x, String y) {
        List<InstructionFormat> instrFormats = instructionMap.get(instruccion);
        String op1 = x;
        String op2 = y;

        // Mapa para asociar las condiciones con los índices de los formatos de instrucción
        Map<String, Integer> formatMap = new HashMap<>();
        formatMap.put("Registro a registro", 0);
        formatMap.put("Memoria a registro", 1);
        formatMap.put("Registro a memoria", 2);
        formatMap.put("Memoria a inmediato", 3);
        formatMap.put("Registro a inmediato", 4);

        // Si el mapa es de saltos
        if (instruccion.startsWith("J")) {
            // Condicion para saber si es una etiqueta ejemplo if:
            if (op1.matches("[a-zA-Z]+:")) {
                return instrFormats.get(0).getBinaryCode();
            }
            formatMap.put("JG", 1);
            formatMap.put("JL", 2);
            formatMap.put("JE", 3);
            formatMap.put("JNE", 4);
            formatMap.put("JGE", 5);
            formatMap.put("JLE", 6);

            // Determinar el formato de instrucción basado en el operando
            Integer i = formatMap.get(op1);
            if (i != null && i < instrFormats.size()) {
                return instrFormats.get(i).getBinaryCode();
            }
        }

        // Determinar el formato de instrucción basado en los operandos
        String formatKey = "";
        if (isRegistro(op1) && isRegistro(op2)) {
            formatKey = "Registro a registro";
        }
        if (op1.startsWith("[")||op1.matches("[a-zA-Z]+") && isRegistro(op2)) {
            formatKey = "Memoria a registro";
        }
        if (isRegistro(op1) && op2.startsWith("[")||op2.matches("[a-zA-Z]+")) {
            formatKey = "Registro a memoria";
        }
        if (op1.startsWith("[")||op1.matches("[a-zA-Z]+") && op2.matches("[0-9]+")) {
            formatKey = "Memoria a inmediato";
        }
        if (isRegistro(op1) && op2.matches("[0-9]+")) {
            formatKey = "Registro a inmediato";
        }

        // Obtener el índice del formato de instrucción y devolver el código binario
        Integer index = formatMap.get(formatKey);
        if (index != null && index < instrFormats.size()) {
            return instrFormats.get(index).getBinaryCode();
        }

        return null;
    }

    private boolean isRegistro(String operando) {
        try {
            Registros.valueOf(operando);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void defInstr() {
        List<InstructionFormat> movFormats = new ArrayList<>();
        movFormats.add(new InstructionFormat("Registro a registro", "1000 10{d}w mod reg r/m desp"));
        movFormats.add(new InstructionFormat("Memoria a registro", "1000 10{d}w mod reg r/m desp"));
        movFormats.add(new InstructionFormat("Registro a memoria", "1000 10{d}w mod reg r/m desp"));
        movFormats.add(new InstructionFormat("Memoria a inmediato", "1100 011w mod 000 mmm {datos} desp"));
        movFormats.add(new InstructionFormat("Registro a inmediato", "1011 w reg {datos}"));
        instructionMap.put("MOV", movFormats);

        List<InstructionFormat> addFormats = new ArrayList<>();
        addFormats.add(new InstructionFormat("Registro a registro", "0000 00{d} w mod reg r/m desp"));
        addFormats.add(new InstructionFormat("Memoria a registro", "0000 00{d} w mod reg r/m desp"));
        addFormats.add(new InstructionFormat("Registro a memoria", "0000 00{d} w mod reg r/m desp"));
        addFormats.add(new InstructionFormat("Memoria a inmediato", "1000 00{s} w mod 000 r/m desp {datos}"));
        addFormats.add(new InstructionFormat("Registro a inmediato", "1000 00{s} w mod 000 r/m desp {datos}"));
        instructionMap.put("ADD", addFormats);

        List<InstructionFormat> subFormats = new ArrayList<>();
        subFormats.add(new InstructionFormat("Registro a registro", "0010 10{d}w mod reg mmm desp"));
        subFormats.add(new InstructionFormat("Memoria a registro", "0010 10{d}w mod reg mmm desp"));
        subFormats.add(new InstructionFormat("Registro a memoria", "0010 10{d}w mod reg mmm desp"));
        subFormats.add(new InstructionFormat("Memoria a inmediato", "1000 00{s}w mod 101 mmm desp {datos}"));
        subFormats.add(new InstructionFormat("Registro a inmediato", "1000 00{s}w mod 101 mmm desp {datos}"));
        instructionMap.put("SUB", subFormats);

        List<InstructionFormat> JumpFormats = new ArrayList<>();
        JumpFormats.add(new InstructionFormat("Etiqueta", "1110 1001 desp"));
        JumpFormats.add(new InstructionFormat("JG", "0111 1111 desp"));
        JumpFormats.add(new InstructionFormat("JL", "0111 1100 desp"));
        JumpFormats.add(new InstructionFormat("JE", "0111 0100 desp"));
        JumpFormats.add(new InstructionFormat("JNE", "0111 0101 desp"));
        JumpFormats.add(new InstructionFormat("JGE", "0111 1101 desp"));
        JumpFormats.add(new InstructionFormat("JLE", "0111 1110 desp"));

        instructionMap.put("JMP", JumpFormats);

        List<InstructionFormat> cmpFormats = new ArrayList<>();
        cmpFormats.add(new InstructionFormat("Registro a registro", "0011 10{d}w mod reg r/m desp"));
        cmpFormats.add(new InstructionFormat("Memoria a registro", "0011 10{d}w mod reg r/m desp"));
        cmpFormats.add(new InstructionFormat("Registro a memoria", "0011 10{d}w mod reg r/m desp"));
        cmpFormats.add(new InstructionFormat("Memoria a inmediato", "1000 00{s}w mod 111 r/m desp {datos}"));
        cmpFormats.add(new InstructionFormat("Registro a inmediato", "1000 00{s}w mod 111 r/m desp {datos}"));
        instructionMap.put("CMP", cmpFormats);
    }

    private void defRegs() {
        registerMap.put("AX", "000");
        registerMap.put("AL", "000");
        registerMap.put("EAX", "000");
        registerMap.put("BX", "001");
        registerMap.put("BL", "001");
        registerMap.put("EBX", "001");
        registerMap.put("CX", "010");
        registerMap.put("CL", "010");
        registerMap.put("ECX", "010");
        registerMap.put("DX", "011");
        registerMap.put("DL", "011");
        registerMap.put("EDX", "011");
        registerMap.put("AH", "100");
        registerMap.put("SP", "100");
        registerMap.put("ESP", "100");
        registerMap.put("CH", "101");
        registerMap.put("BP", "101");
        registerMap.put("EBP", "101");
        registerMap.put("DH", "110");
        registerMap.put("SI", "110");
        registerMap.put("ESI", "110");
        registerMap.put("BH", "111");
        registerMap.put("DI", "111");
        registerMap.put("EDI", "111");
    }

    private void defW() {
        WMap.put("AX", "1");
        WMap.put("CX", "1");
        WMap.put("DX", "1");
        WMap.put("BX", "1");
        WMap.put("SP", "1");
        WMap.put("BP", "1");
        WMap.put("SI", "1");
        WMap.put("DI", "1");
        WMap.put("AL", "0");
        WMap.put("CL", "0");
        WMap.put("DL", "0");
        WMap.put("BL", "0");
        WMap.put("AH", "0");
        WMap.put("CH", "0");
        WMap.put("DH", "0");
        WMap.put("BH", "0");
    }

    private void defMOD() {
        MODMap.put("AL", "11");
        MODMap.put("CL", "11");
        MODMap.put("DL", "11");
        MODMap.put("BL", "11");
        MODMap.put("AH", "11");
        MODMap.put("CH", "11");
        MODMap.put("DH", "11");
        MODMap.put("BH", "11");

        MODMap.put("AX", "00");
        MODMap.put("CX", "01");
        MODMap.put("DX", "10");
        MODMap.put("BX", "11");
        MODMap.put("SP", "00");
        MODMap.put("BP", "01");
        MODMap.put("SI", "10");
        MODMap.put("DI", "11");
    }

    private void defRM() {
        RMMap.put("AX", "000");
        RMMap.put("CX", "001");
        RMMap.put("DX", "010");
        RMMap.put("BX", "011");
        RMMap.put("SP", "100");
        RMMap.put("BP", "101");
        RMMap.put("SI", "110");
        RMMap.put("DI", "111");

        RMMap.put("AL", "000");
        RMMap.put("CL", "001");
        RMMap.put("DL", "010");
        RMMap.put("BL", "011");
        RMMap.put("AH", "100");
        RMMap.put("CH", "101");
        RMMap.put("DH", "110");
        RMMap.put("BH", "111");
    }

    private String getw(String reg) {
        String w = WMap.get(reg);
        return w;
    }

    private String getmod(String reg) {
        String mod = MODMap.get(reg);
        return mod;
    }

    private String getrm(String reg) {
        String rm = RMMap.get(reg);
        if (reg.startsWith("[")) {
            return rm = "000";
        }
        if (reg.matches("[0-9]+")) {
            return rm = "101";
        }

        return rm;
    }

    public String translate(String lineas) {
        getShits(lineas);
        for (Instruccion instr : instrucciones) {
            System.out.println("Procesando instrucción: " + instr.getnameOp() + " " + instr.getOp1() + " " + instr.getOp2());
            String codOp = getInstruccion(instr.getnameOp(), instr.getOp1(), instr.getOp2());
            if (codOp == null) {
                System.out.println("Instrucción no reconocida: " + instr.getnameOp() + " " + instr.getOp1() + " " + instr.getOp2());
                continue; // Si codOp es null, saltar a la siguiente instrucción
            }
            String d = "";
            String w = "";
            String mod = "";
            String reg = "";
            String rm = "";
            if (codOp.contains("w")) {
                w = getw(instr.getOp1());
                if (w != null) {
                    codOp = codOp.replace("w", w);
                } else {
                  //  System.out.println("Valor 'w' es null para el registro: " + instr.getOp1());
                    w="0";
                    codOp = codOp.replace("w", w);
                }
            }
            if (codOp.contains("{d}")) {
                d = "00";
                codOp = codOp.replace("{d}", d);
            }
            if (codOp.contains("mod")) {
                mod = getmod(instr.getOp1());
                if (mod != null) {
                    codOp = codOp.replace("mod", mod);
                } else {
                    System.out.println("Valor 'mod' es null para el registro: " + instr.getOp1());
                    mod = "00"; // Valor por defecto
                    codOp = codOp.replace("mod", mod);
                }
            }
            if (codOp.contains("reg")) {
                reg = registerMap.get(instr.getOp1());
                if (reg != null) {
                    codOp = codOp.replace("reg", reg);
                } else {
                    System.out.println("Valor 'reg' es null para el registro: " + instr.getOp1());
                    reg = registerMap.get(instr.getOp2());
                    if (reg != null) {
                        codOp = codOp.replace("reg", reg);
                    } else {
                        reg = "000"; // Valor por defecto
                        codOp = codOp.replace("reg", reg);
                    }
                }
            }
            
            if (codOp.contains("r/m")) {
                rm = getrm(instr.getOp2());
                if (rm != null) {
                    codOp = codOp.replace("r/m", rm);
                } else {
                    System.out.println("Valor 'r/m' es null para el registro: " + instr.getOp2());
                    rm = "000"; // Valor por defecto
                    codOp = codOp.replace("r/m", rm);
                }
            }
            if (codOp.contains("{s}")) {
                String s = "0";
                codOp = codOp.replace("{s}", s);
            }
            if (codOp.contains("{datos}")) {
                String datos = Integer.toBinaryString(Integer.parseInt(instr.getOp2()));
                // añadir ceros para que sean dos bytes
                while (datos.length() < 8) {
                    datos = "0" + datos;
                }
                codOp = codOp.replace("{datos}", datos);
            }
            if (codOp.contains("desp")) {
                String mmm = "0000 0000 ";
                codOp = codOp.replace("desp", mmm);
            }

            appendCodeWithAddress(codOp);


        }

        return code.toString();
    }

    private void appendCodeWithAddress(String codOp) {
        String[] parts = codOp.split(" ");
        StringBuilder formattedCode = new StringBuilder();
        String address = "00A0:" + generateMemoryAddress();
    
        for (int i = 0; i < parts.length; i++) {
            formattedCode.append(parts[i]);
            if ((i + 1) % 2 == 0) {
                formattedCode.append(" ");
            }
        }
    
        // Insertar un espacio cada 4 bits
        StringBuilder spacedCode = new StringBuilder();
        for (int i = 0; i < formattedCode.length(); i++) {
            spacedCode.append(formattedCode.charAt(i));
            if ((i + 1) % 4 == 0 && i != formattedCode.length() - 1) {
                spacedCode.append(" ");
            }
        }
    
        code.append(address).append(" ").append(spacedCode.toString().trim()).append("\n");
    
        // Incrementar la dirección de memoria en función del número de bytes
        int byteCount = (int) Math.ceil(parts.length / 2.0);
        memoryAddressCounter += byteCount;
    }

    public void getShits(String linea) {
        // Generar la cabecera si contiene .data
        if (linea.contains(".DATA")) {
            generateHeader(linea);
        }

        // Separar las instrucciones por saltos de línea
        String[] lineas = linea.split("\n");

        // Procesar cada línea de instrucción
        for (int i = 0; i < lineas.length; i++) {
            if (lineas[i].equalsIgnoreCase(",")) {
                continue;
            }
            if (lineas[i].contains(".CODE")) {
                continue; // Saltar la línea que contiene .code
            }
            if (lineas[i].contains(".DATA") || lineas[i].contains("DB") || lineas[i].contains("DW")) {
                System.out.println("Línea de cabecera saltada: " + lineas[i]);
                continue; // Saltar las líneas de cabecera
            }

            String[] instruccion = lineas[i].split("[ ,]+");
            if (instruccion.length == 3) {
                Instruccion instr = new Instruccion(instruccion[0], instruccion[1], instruccion[2]);
                instrucciones.add(instr);
            }
        }
    }

    // Método actualizado para generar el header del archivo
    public String generateHeader(String linea) {
        String[] lineas = linea.split("\n");
        for (String lin : lineas) {
            if (lin.contains(".CODE")) {
                break;
            }

            if (lin.contains("DB")) {
                String address = generateMemoryAddress();
                String[] parts = lin.split(" ");
                String value = parts[parts.length - 1];
                String binaryValue = "0000 0000 0000 0000"; // Valor por defecto

                if (!value.equals("?")) {
                    int intValue = Integer.parseInt(value);
                    String binaryPart = String.format("%8s", Integer.toBinaryString(intValue)).replace(' ', '0');
                    binaryValue = "0000 0000 " + binaryPart;
                }

                code.append("00A0:").append(address).append(": ").append(binaryValue).append("\n");
                incrementMemoryAddress("DB");
            }

            if (lin.contains("DW")) {
                String address = generateMemoryAddress();
                String[] parts = lin.split(" ");
                String value = parts[parts.length - 1];
                String binaryValue = "0000 0000 0000 0000 0000 0000 0000 0000"; // Valor por defecto

                if (!value.equals("?")) {
                    int intValue = Integer.parseInt(value);
                    String binaryPart = String.format("%16s", Integer.toBinaryString(intValue)).replace(' ', '0');
                    binaryValue = "0000 0000 0000 0000 " + binaryPart;
                }

                code.append("00A0:").append(address).append(": ").append(binaryValue).append("\n");
                incrementMemoryAddress("DW");
            }
        }

        return code.toString();
    }

    public static void main(String[] args) {
        ObjGenerator obj = new ObjGenerator();
        String instrucciones = ".DATA\n" +
"X DB 9\n" +
"Y DB 10\n" +
".CODE\n" +
"MOV X, AX\n" +
"MOV Y, AX\n";

        String codigo = obj.translate(instrucciones.toUpperCase());
        System.out.println(codigo);
    }
}

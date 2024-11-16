import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjGenerator {

    private final Map<String, String> registerMap;
    private final Map<String, String> WMap;
    private final Map<String, String> MODMap;
    private final Map<String, String> RMMap;
    private final Map<String, String> MMMap;
    private StringBuilder code;
    private final Map<String, List<InstructionFormat>> instructionMap;
    private List<Instruccion> instrucciones;
    private int memoryAddressCounter;
    private final Map<String, Integer> labelMap;
    private Map<String, Integer> formatMap;

    public ObjGenerator() {
        this.registerMap = new HashMap<>();
        this.WMap = new HashMap<>();
        this.MODMap = new HashMap<>();
        this.RMMap = new HashMap<>();
        this.MMMap = new HashMap<>();
        this.code = new StringBuilder();
        this.instructionMap = new HashMap<>();
        this.instrucciones = new ArrayList<>();
        this.memoryAddressCounter = 0x0000;
        this.labelMap = new HashMap<>();
        this.formatMap = new HashMap<>();
        defInstr();
        defRegs();
        defW();
        defMOD();
        defRM();
    }

    private String generateMemoryAddress() {
        String address = String.format("%04X", memoryAddressCounter);
        return address;
    }

    private void incrementMemoryAddress(String dataType) {
        switch (dataType) {
            case "DB":
                memoryAddressCounter += 2;
                break;
            case "DW":
                memoryAddressCounter += 4;
                break;
            default:
                break;
        }
    }

    private String getInstruccion(String instruccion, String x, String y) {
        System.out.println("Buscando instrucción: " + instruccion);
        List<InstructionFormat> instrFormats;
        //si la instruccion empieza con J, buscar en el mapa de saltos
        instruccion.trim();
        instruccion.stripLeading();
        instruccion.stripTrailing();
        instruccion.toUpperCase();
        instruccion.replaceAll("\t", " ");

        //si tiene espacios o tabulaciones, quitarlos
        System.out.println("instruccion:"+instruccion);
        if (instruccion.startsWith("J")) {
            System.out.println("empece con J "+instruccion);
            instrFormats = instructionMap.get("JUMP");
            if (instrFormats == null) {
                System.out.println("Instrucción no encontrada en el mapa de saltos: " + instruccion);
                return null;
            }
            //encontro el mapa de JUMP ahora buscar la instruccion
            for (InstructionFormat format : instrFormats) {
                if (format.getMode().equals(instruccion)) {
                    System.out.println("Formato encontrado para " + instruccion + ": " + format.getBinaryCode());
                    return format.getBinaryCode();
                }
            }
            
        }
        //si no empieza con J, buscar en el mapa de instrucciones
        instrFormats = instructionMap.get(instruccion.trim());
        if (instrFormats == null) {
            System.out.println("Instrucción no encontrada en el mapa de instrucciones: " + instruccion);
            return null;
        }

        String op1 = x.trim();
        String op2 = y.trim();

        // Determinar el formato de instrucción basado en los operandos
        String formatKey = "";
        if (isRegistro(op1) && isRegistro(op2)) {
            formatKey = "Registro a registro";
        } 
        if (op1.matches("[a-zA-Z_][a-zA-Z_0-9]*") && isRegistro(op2)) {
            formatKey = "Memoria a registro";
        } 
        if (isRegistro(op1) && op2.matches("[a-zA-Z_][a-zA-Z_0-9]*")) {
            formatKey = "Registro a memoria";
        } 
         if (op1.matches("[a-zA-Z_][a-zA-Z_0-9]*") && op2.matches("[0-9]+")) {
            formatKey = "Memoria a inmediato";
        }
        if (isRegistro(op1) && op2.matches("[0-9]+")) {
            formatKey = "Registro a inmediato";
        }

        System.out.println("Formato determinado: " + formatKey);

        Integer index = formatMap.get(formatKey);
        if (index != null && index < instrFormats.size()) {
            return instrFormats.get(index).getBinaryCode();
        }

        System.out.println(
                "Formato no encontrado para la instrucción: " + instruccion + " con operandos: " + op1 + ", " + op2);
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
        addFormats.add(new InstructionFormat("Registro a registro", "0000 00{d}w mod reg r/m desp"));
        addFormats.add(new InstructionFormat("Memoria a registro", "0000 00{d}w mod reg r/m desp"));
        addFormats.add(new InstructionFormat("Registro a memoria", "0000 00{d}w mod reg r/m desp"));
        addFormats.add(new InstructionFormat("Memoria a inmediato", "1000 00{s}w mod 000 r/m desp {datos}"));
        addFormats.add(new InstructionFormat("Registro a inmediato", "1000 00{s}w mod 000 r/m desp {datos}"));
        instructionMap.put("ADD", addFormats);

        List<InstructionFormat> subFormats = new ArrayList<>();
        subFormats.add(new InstructionFormat("Registro a registro", "0010 10{d}w mod reg mmm desp"));
        subFormats.add(new InstructionFormat("Memoria a registro", "0010 10{d}w mod reg mmm desp"));
        subFormats.add(new InstructionFormat("Registro a memoria", "0010 10{d}w mod reg mmm desp"));
        subFormats.add(new InstructionFormat("Memoria a inmediato", "1000 00{s}w mod 101 mmm desp {datos}"));
        subFormats.add(new InstructionFormat("Registro a inmediato", "1000 00{s}w mod 101 mmm desp {datos}"));
        instructionMap.put("SUB", subFormats);

        List<InstructionFormat> JumpFormats = new ArrayList<>();
        JumpFormats.add(new InstructionFormat("Etiqueta", "1110 1001 eti"));
        JumpFormats.add(new InstructionFormat("JG", "0111 1111 eti"));
        JumpFormats.add(new InstructionFormat("JL", "0111 1100 eti"));
        JumpFormats.add(new InstructionFormat("JE", "0111 0100 eti"));
        JumpFormats.add(new InstructionFormat("JNE", "0111 0101 eti"));
        JumpFormats.add(new InstructionFormat("JGE", "0111 1101 eti"));
        JumpFormats.add(new InstructionFormat("JLE", "0111 1110 eti"));
        JumpFormats.add(new InstructionFormat("JMP", "1110 1001 eti"));

        instructionMap.put("JUMP", JumpFormats);

        List<InstructionFormat> cmpFormats = new ArrayList<>();
        cmpFormats.add(new InstructionFormat("Registro a registro", "0011 10{d}w mod reg r/m desp"));
        cmpFormats.add(new InstructionFormat("Memoria a registro", "0011 10{d}w mod reg r/m desp"));
        cmpFormats.add(new InstructionFormat("Registro a memoria", "0011 10{d}w mod reg r/m desp"));
        cmpFormats.add(new InstructionFormat("Memoria a inmediato", "1000 00{s}w mod 111 r/m desp {datos}"));
        cmpFormats.add(new InstructionFormat("Registro a inmediato", "1000 00{s}w mod 111 r/m desp {datos}"));

        instructionMap.put("CMP", cmpFormats);

        System.out.println("Instrucciones definidas: " + instructionMap.keySet());

        formatMap.put("Registro a registro", 0);
        formatMap.put("Memoria a registro", 1);
        formatMap.put("Registro a memoria", 2);
        formatMap.put("Memoria a inmediato", 3);
        formatMap.put("Registro a inmediato", 4);
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

    private String getrm(String operando) {
        if (operando.startsWith("[")) {
            return "110";
        }
        if (labelMap.containsKey(operando)) {
            return "101";
        }
        String rm = RMMap.get(operando);
        return rm != null ? rm : "000"; // Por defecto
    }

    public String translate(String lineas) {
        System.out.println("Traduciendo código intermedio a código objeto...");
        lineas = lineas.replace("\t+", " ");
        //darle un trim a todo para que solo tenga 1 espacio entre cada cosa de la linea
        lineas = lineas.replaceAll(" +", " ");
        lineas = lineas.replace(",", " ");

        System.out.println("Codigo mega formateado: " + lineas);
            
        getShits(lineas);
        for (Instruccion instr : instrucciones) {
            System.out.println("Procesando instrucción: " + instr.getnameOp() + " " + instr.getOp1() + " " + instr.getOp2());
            System.out.println("soy la instruccion:" + instr.getnameOp().replace("\t+"," ")+" op1= "+instr.getOp1()+" op2= "+instr.getOp2());
            String codOp=" ";
            if (instr.getnameOp().startsWith("J")) {
                codOp = buscarInstruccionSalto(instr.getnameOp().toUpperCase());
            } else {
                codOp = getInstruccion(instr.getnameOp(), instr.getOp1(), instr.getOp2());
            }
    
            if (codOp == null) {
                System.out.println("Instrucción no reconocida: " + instr.getnameOp() + " " + instr.getOp1() + " "
                        + instr.getOp2());
                continue;
            }
    
            String d = "";
            String w = "";
            String mod = "";
            String reg = "";
            String rm = "";
            if (codOp.contains("w")) {
                w = getw(instr.getOp1());
                codOp = codOp.replace("w", w != null ? w : "0");
            }
            if (codOp.contains("{d}")) {
                d = "0";
                codOp = codOp.replace("{d}", d);
            }
            if (codOp.contains("mod")) {
                mod = getmod(instr.getOp1());
                codOp = codOp.replace("mod", mod != null ? mod : "00");
            }
            if (codOp.contains("reg")) {
                reg = registerMap.get(instr.getOp1());
                if (reg == null) {
                    reg = registerMap.get(instr.getOp2());
                }
                codOp = codOp.replace("reg", reg != null ? reg : "000");
            }
    
            if (codOp.contains("r/m")) {
                rm = getrm(instr.getOp2());
                codOp = codOp.replace("r/m", rm != null ? rm : "000");
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
                String desp = "00000000";
                codOp = codOp.replace("desp", desp);
            }
            if (codOp.contains("mmm")) {
                String mmm = MMMap.containsKey(instr.getOp1()) ? getrm(instr.getOp1()) : getrm(instr.getOp2());
                codOp = codOp.replace("mmm", mmm != null ? mmm : "000");
            }

            if (codOp.contains("eti")) {
                String etiqueta = instr.getOp1();
                Integer etiquetaDireccion = labelMap.get(etiqueta);
                if (etiquetaDireccion != null) {
                    int desplazamiento = etiquetaDireccion - memoryAddressCounter - 2; // -2 para el tamaño del salto
                    String despHex = String.format("%08X", desplazamiento);
                    String despHexLastTwo = despHex.substring(despHex.length() - 2);            
                    String despBin = new BigInteger(despHexLastTwo, 16).toString(2);
            
                    // Reemplazar "eti" en codOp con el valor binario
                    codOp = codOp.replace("eti", despBin);
                }
            }
    
            appendCodeWithAddress(codOp);
        }
        System.out.println("Código objeto final:\n" + code.toString());
        return code.toString();
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
            System.out.println("Procesando línea: " + lineas[i]);
            if (lineas[i].isEmpty()||lineas[i].contains("END")||lineas[i].contains("START") ) {
                continue; 
            }
            if (lineas[i].equalsIgnoreCase(",")) {
                System.out.println("A mi no me saltaron"+lineas[i]);
                continue;
            }
            if (lineas[i].contains(".CODE")) {
                System.out.println("Línea de código encontrada y saltada: " + lineas[i]);
                continue; // Saltar la línea que contiene .code
            }
            if (lineas[i].contains(".DATA") || lineas[i].contains("DB") || lineas[i].contains("DW")) {
                System.out.println("Línea de cabecera saltada: " + lineas[i]);
                continue; // Saltar las líneas de cabecera
            }
            String[] instruccion = lineas[i].split("[, ]+");
            if (instruccion.length == 3) {
                System.out.println("Instrucción encontradaaaaa: " + instruccion[0] + " " + instruccion[1] + " " + instruccion[2]);
                Instruccion instr = new Instruccion(instruccion[0], instruccion[1], instruccion[2]);
                instrucciones.add(instr);
                System.out.println("Instrucción agregada: " + instr.getnameOp() + " " + instr.getOp1() + " " + instr.getOp2());
            }

            // Almacenar etiquetas y sus direcciones de memoria
            if (instruccion.length == 1 && instruccion[0].endsWith(":")) {
                String etiqueta = instruccion[0].substring(0, instruccion[0].length() - 1);
                labelMap.put(etiqueta, memoryAddressCounter);
                System.out.println("Etiqueta agregadaaaaaa: " + etiqueta + " en dirección " + memoryAddressCounter);
            }
            if (instruccion.length == 2) {
                Instruccion instr = new Instruccion(instruccion[0], instruccion[1], "");
                instrucciones.add(instr);
                System.out.println("Instrucción agregadaaaaaa2: " + instr.getnameOp() + " " + instr.getOp1());
            }

        }
    }

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
                String binaryValue = "0000 0000 0000 0000";

                if (!value.equals("?")) {
                    int intValue = Integer.parseInt(value);
                    String binaryPart = String.format("%8s", Integer.toBinaryString(intValue)).replace(' ', '0');
                    binaryValue = "0000 0000 " + binaryPart;
                }

                code.append("00A0:").append(address).append(": ").append(binaryValue).append("\n");
                incrementMemoryAddress("DB");
                MMMap.put(parts[0], address);
            }

            if (lin.contains("DW")) {
                String address = generateMemoryAddress();
                String[] parts = lin.split(" ");
                String value = parts[parts.length - 1];
                String binaryValue = "0000 0000 0000 0000 0000 0000 0000 0000";

                if (!value.equals("?")) {
                    int intValue = Integer.parseInt(value);
                    String binaryPart = String.format("%16s", Integer.toBinaryString(intValue)).replace(' ', '0');
                    binaryValue = "0000 0000 0000 0000 " + binaryPart;
                }
                code.append("00A0:").append(address).append(": ").append(binaryValue).append("\n");
                incrementMemoryAddress("DW");
                MMMap.put(parts[0], address);
            }

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

    private String buscarInstruccionSalto(String instruccion) {
        System.out.println("Buscando instrucción de salto: ENTRE" + instruccion);
        List<InstructionFormat> jumpFormats = instructionMap.get("JUMP");

        if (jumpFormats == null) {
            System.out.println("Mapa de saltos no encontrado.");
            return null;
        }
        for (InstructionFormat format : jumpFormats) {
            if (format.getMode().equals(instruccion)) {
                System.out.println("Formato encontrado para " + instruccion + ": " + format.getBinaryCode());
                return format.getBinaryCode();
            }
        }

        System.out.println("Instrucción de salto no encontrada: " + instruccion);
        return null;
    }

}

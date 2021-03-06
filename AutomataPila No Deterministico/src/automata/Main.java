package automata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Scanner;
import java.util.Arrays;

import utils.Quintuple;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



class Main {
public static void main(String[] args) {
        if (args.length > 0) {

                String[] instrucciones = leerArchivo(args[0]);
                try {
                        NFAPila automata = interpretarInstrucciones(instrucciones);
                        if (automata == null) {
                                System.out.println("el automata estaba mal escrito");
                        }

                        boolean exit =false;

                        while (!exit) {
                                exit =menu(automata);

                        }



                } catch (Exception e) {
                        System.out.println("Ha ocurrido un error en la construccion del automata");
                        System.out.println(e.getMessage());

                }

        } else {
                System.out.println("Por favor ponga la ruta del archivo como parametro de Main para cargar el dot");
                NFAPila automata = leerGramatica();
                boolean exit =true;
                System.out.println("El automata leido por la gramatica es:");
                System.out.println(automata.to_dot());
                while (!exit) {
                        exit =menu(automata);

                }



        }

}

private static NFAPila leerGramatica() {
	System.out.println("Ingrese la ruta del archivo de texto donde esta especificada la gramatica");
	Scanner sc = new Scanner(System.in);
	String filename =sc.nextLine();
	System.out.println(filename);
    
    try {
            sc = new Scanner(new FileReader(filename));
    } catch (Exception e) {
            System.out.println("el archivo no existe o no se puede leer");
    }
    StringBuilder sb = new StringBuilder();
    String[] gramatica = new String[20];
    int i=0;
    while (sc.hasNext()) {
            gramatica[i]=sc.next();
;			System.out.println("se guardo en gramatica "+gramatica[i]);
            i++;
    }
    
    sc.close();
    
    NFAPila autom = NFAPila.gramaticaToAutomataPila(gramatica);
    
    return autom;
	
	
	

}

private static boolean menu(automata.NFAPila automata) {
        System.out.println("El automata actual es: ");
        System.out.println(automata.to_dot());
        int opcion=0;
        Scanner scan = new Scanner(System.in);
        System.out.println("1 - probar string");
        System.out.println("2 - Ejecutar algoritmo de EF a PV");
        System.out.println("3 - Ejecutar algoritmo de PV a EF");
                System.out.println("4 - Salir");
        if(scan.hasNextInt()){opcion = scan.nextInt();}
        
        if(opcion==4) {
        	
        	return true; }
        if(opcion==2) {
        	automata.toPilaVacia();
        	

        	return true;
        	}
        if(opcion==3) {
        	automata.toEstadoFinal();
        	

        	return true;
        	}
        if(opcion==1) {
                System.out.println("introduzca el string para analizar su aceptacion ");
                System.out.println("");
                scan= new Scanner(System.in);
                String s = scan.nextLine();
                if(automata.accepts(s)){System.out.println("acepto!");}else{System.out.println("no acepto!");}
                
        }


        return true;
}

private static NFAPila interpretarInstrucciones(String[] instructions) throws Exception {
        
        Set<Quintuple<State, Character, Character, String, State> > transiciones = new HashSet<Quintuple<State, Character, Character, String, State> >();
        Set<State> estados = new HashSet<State>();
        Set<Character> alfabeto = new HashSet<Character>();
        Set<Character> alfabetoPila = new HashSet<Character>();
        Set<State> estadosfinales = new HashSet<State>();
        
        int i = 1;
        boolean tienequesertransicion = true;
        State estadoinicial = null;
        Quintuple<State, Character, Character, String, State> nuevatransicion = null;
        Character caracterInicialPila = 'Z';
        if (instructions[0].matches("digraph")) {
                State nuevoEstadoFinal=null;
                while (i < instructions.length && instructions[i] != null) {

                        if (instructions[i].startsWith("inic[shape=point")||instructions[i].replaceAll("\\s", "").length()==0) {
                                tienequesertransicion = false;
                        }

                        if (instructions[i].startsWith("inic->")) {
                                estadoinicial = new State(""+instructions[i].substring(6));
                                if(!estadoinicial.existeEnConjunto(estados)) {estados.add(estadoinicial); }

                                tienequesertransicion = false;
                              
                        }

                        if (instructions[i].endsWith("[shape=doublecircle]")
                            && instructions[i].length() > 20) {
                                nuevoEstadoFinal = new State(instructions[i].substring(0,instructions[i].length() - 20));
                                if(!nuevoEstadoFinal.existeEnConjunto(estados)) {estados.add(nuevoEstadoFinal); }
                                if(!nuevoEstadoFinal.existeEnConjunto(estadosfinales)) {estadosfinales.add(nuevoEstadoFinal); }
                                tienequesertransicion = false;
                        }
                        if (tienequesertransicion) {
                                nuevatransicion = extraerTransicion(instructions[i]);
                                if (nuevatransicion != null) {
                                        transiciones.add(nuevatransicion);
                                }
                        }

                        i++;
                        tienequesertransicion = true;
                }
                Iterator<Quintuple<State, Character, Character, String, State> > iteradorDeltas = transiciones.iterator();
                while (iteradorDeltas.hasNext()) {
                        Quintuple<State, Character, Character, String, State> elemento = iteradorDeltas.next();
                        if(!elemento.first().existeEnConjunto(estados)) {estados.add(elemento.first()); }
                        if(!elemento.fifth().existeEnConjunto(estados)) {estados.add(elemento.fifth()); }
                        alfabeto.add(elemento.second());

                        alfabetoPila.add(elemento.third());
                        int indiceAuxiliar = 0;
                        while (indiceAuxiliar < elemento.fourth().length()) {
                                alfabetoPila.add(elemento.fourth().charAt(indiceAuxiliar));
                                indiceAuxiliar++;
                        }
                }
               


                return new NFAPila(estados, alfabeto, alfabetoPila, transiciones,
                                   caracterInicialPila, estadoinicial, estadosfinales);

        }
        return null;

}

private static Quintuple<State, Character, Character, String, State> extraerTransicion(String linea) {
        int i = 0;
        int j=0;

        while (j < linea.length() && linea.charAt(j) != '-') {
                j++;
        }


        State primerEstado =new State(linea.substring(i, j));

        j++;
        j++;
        i = j;
        while (j < linea.length() && linea.charAt(j) != '[') {
                j++;
        }

        State segundoEstado =new State(linea.substring(i, j));

        j+=8;
        i=j;

        char primerCaracter =linea.charAt(i);

        i+=2;

        char segundoCaracter =linea.charAt(i);

        i+=2;
        j=i;
        while (j < linea.length() && linea.charAt(j) != '"') {
                j++;
        }
        String primerString =linea.substring(i, j);


        return new Quintuple<State, Character, Character, String, State>(primerEstado,primerCaracter,segundoCaracter,primerString,segundoEstado);
}

public static String[] leerArchivo(String filename) {
        System.out.println(filename);
        Scanner in = null;
        try {
                in = new Scanner(new FileReader(filename));
        } catch (Exception e) {
                System.out.println("el archivo no existe o no se puede leer");
        }
        StringBuilder sb = new StringBuilder();
        while (in.hasNext()) {
                sb.append(in.next());
        }
        String automataInDot = sb.toString().replaceAll("\\s", "");
        String[] instructions = extraerLineas(automataInDot);
        in.close();
        return instructions;

}

public static String[] extraerLineas(String linea) {
        String[] lineas = new String[100];
        System.out.println(linea);
        int indexOfArray = 0;
        int i = 0;
        int j = 0;
        while (j < linea.length()-1) {
                while (j < linea.length() && linea.charAt(j) != ';'
                       && linea.charAt(j) != '{' && linea.charAt(j) != '}') {
                        j++;
                }

                lineas[indexOfArray] = linea.substring(i, j);
                indexOfArray++;
                j++;
                i = j;
        }
        return lineas;
}

}

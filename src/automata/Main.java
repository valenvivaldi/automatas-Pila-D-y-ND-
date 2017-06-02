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
                System.out.println(Arrays.toString(instrucciones));
                try {
                        DFAPila automata = interpretarInstrucciones(instrucciones);
                        if (automata == null) {
                                System.out.println("el automata estaba mal escrito");
                        }
                        System.out.println(automata.to_dot());


                        boolean exit =false;

                        while (!exit) {
                                exit =menu(automata);

                        }



                } catch (Exception e) {
                        System.out.println("Ha ocurrido un error en la construccion del automata");
                        System.out.println(e.getMessage());

                }

        } else {
                System.out.println("Por favor ponga la ruta del archivo como parametro de Main");
        }

}

private static boolean menu(DFAPila automata) {
        Scanner sc = new Scanner(System.in);
        System.out.println("3 - probar string");
        System.out.println("4 - Salir");
        int opcion = sc.nextInt();
        sc.nextLine();
        if(opcion==4) {return false; }
        if(opcion==3) {
                System.out.println("introduzca el string para analizar su aceptacion ");
                System.out.println("");
                String s = sc.nextLine();
                automata.accepts(s);
        }


        return true;
}

private static DFAPila interpretarInstrucciones(String[] instructions) throws Exception {
        System.out.println("empezo interpretar");
        Set<Quintuple<State, Character, Character, String, State> > transiciones = new HashSet<Quintuple<State, Character, Character, String, State> >();
        Set<State> estados = new HashSet<State>();
        Set<Character> alfabeto = new HashSet<Character>();
        Set<Character> alfabetoPila = new HashSet<Character>();
        Set<State> estadosfinales = new HashSet<State>();
        System.out.println("ya creo los sets");
        int i = 1;
        boolean tienequesertransicion = true;
        State estadoinicial = null;
        Quintuple<State, Character, Character, String, State> nuevatransicion = null;
        Character caracterInicialPila = 'Z';
        System.out.println("instructions[0]:" + instructions[0]);
        if (instructions[0].matches("digraph")) {
                System.out.println("empieza con dig");
                while (i < instructions.length && instructions[i] != null) {
                        System.out.println("instruccion actual: |" + instructions[i]+"|");

                        if (instructions[i].startsWith("inic[shape=point")||instructions[i].replaceAll("\\s", "").length()==0) {
                                System.out.println("HOLA,ESTOSOLOTIENEQUEAPARECERALFINAL");
                                tienequesertransicion = false;
                        }

                        if (instructions[i].startsWith("inic->")) {
                                estadoinicial = new State(""+instructions[i].substring(6));
                                estados.add(estadoinicial);
                                tienequesertransicion = false;
                                System.out.println("Estado inicial encontrado!:"+ instructions[i].substring(6));
                                System.out.println(estadoinicial.name());
                        }

                        if (instructions[i].endsWith("[shape=doublecircle]")
                            && instructions[i].length() > 20) {
                                estados.add(new State(instructions[i].substring(0,
                                                                                instructions[i].length() - 20)));
                                estadosfinales.add(new State(instructions[i].substring(0,
                                                                                       instructions[i].length() - 20)));
                                tienequesertransicion = false;
                        }
                        System.out.println("el tienequesertransicion es:"+tienequesertransicion);
                        if (tienequesertransicion) {
                                System.out.println("LA INSTRUCCION ES UNA TRANSICION ");
                                nuevatransicion = extraerTransicion(instructions[i]);
                                if (nuevatransicion != null) {
                                        System.out.println("LA TRANSICION EXTRAIDA ES DISTINTA DE NULL");
                                        transiciones.add(nuevatransicion);
                                }
                        }

                        System.out.println("paso los if, fin instruccion:" + i);
                        i++;
                        tienequesertransicion = true;
                }
                System.out.println("salio del reconocedor de strings");
                Iterator<Quintuple<State, Character, Character, String, State> > iteradorDeltas = transiciones.iterator();
                System.out.println("Se creo el iterador que recorrera el set de quintuples para armar los alfabetos");
                while (iteradorDeltas.hasNext()) {
                        Quintuple<State, Character, Character, String, State> elemento = iteradorDeltas.next();
                        System.out.println(elemento.toString());

                        alfabeto.add(elemento.second());
                        alfabetoPila.add(elemento.third());
                        int indiceAuxiliar = 0;
                        while (indiceAuxiliar < elemento.fourth().length()) {
                                alfabetoPila.add(elemento.fourth().charAt(indiceAuxiliar));
                                indiceAuxiliar++;
                        }
                }
                System.out.println("termino el seteo de los alfabetos");
                System.out.println("ESTADOS:");
                System.out.println(estados.toString());
                System.out.println("ALFABETO:");
                System.out.println(alfabeto.toString());
                System.out.println("ALFABETO PILA:");
                System.out.println(alfabetoPila.toString());


                System.out.println("DELTA:");
                System.out.println(transiciones.toString());




                return new DFAPila(estados, alfabeto, alfabetoPila, transiciones,
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

        //System.out.println(linea.substring(i, j));

        State primerEstado =new State(linea.substring(i, j));

        j++;
        j++;
        i = j;
        while (j < linea.length() && linea.charAt(j) != '[') {
                j++;
        }

        //System.out.println(linea.substring(i, j));
        State segundoEstado =new State(linea.substring(i, j));

        j+=8;
        i=j;

        //System.out.println(linea.charAt(i));
        char primerCaracter =linea.charAt(i);

        i+=2;

        System.out.println(linea.charAt(i));
        char segundoCaracter =linea.charAt(i);

        i+=2;
        j=i;
        while (j < linea.length() && linea.charAt(j) != '"') {
                j++;
        }
        System.out.println(linea.substring(i, j));
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
        // System.out.println(Arrays.toString(lineas));
        return lineas;
}

}

package automata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import utils.Quintuple;

public final class NFAPila extends AP {

private Object nroStates[];
private Stack<Character> stack;       //the stack of the automaton


/**
 * Constructor of the class - returns a DFAPila object
 * @param states - states of the DFAPila
 * @param alphabet - the alphabet of the automaton
 * @param stackAlphabet - the alphabet of the stack
 * @param transitions - transitions of the automaton
 * @param stackInitial - a Character which represents the initial element of the stack
 * @param initial - initial State of the automaton
 * @param final_states - acceptance states of the automaton
 * @throws IllegalArgumentException
 */
public NFAPila(
        Set<State> states,
        Set<Character> alphabet,
        Set<Character> stackAlphabet,
        Set<Quintuple<State, Character,Character,String, State> > transitions,
        Character stackInitial,
        State initial,
        Set<State> final_states)
throws IllegalArgumentException {

        this.states = states;
        this.alphabet = alphabet;
        this.stackAlphabet = stackAlphabet;


        //public static final Character Initial = 'Z';

        stackAlphabet.add(Lambda); // _  the character lambda is used in the stack to know when do a pop

        stackAlphabet.add(Joker); //  @   the mark of the stack
        this.transitions = transitions;
        this.stackInitial = stackInitial;
        this.initial = initial;
        this.finalStates = final_states;
        nroStates =  states.toArray();
        stack = new Stack<Character>();
        stack.add(stackInitial); //insert the mark in the stack ANTES IBA JOKER
        if (!rep_ok()) {
                throw new  IllegalArgumentException();
        }
        System.out.println("Is a DFA Pila");
}


@Override
public State delta(State from, Character c){
        //System.out.println("Se buscara un quintuple compatible para el caracter: "+c+" y el estado actual: "+from.name()+" PILA: "+stack.toString());
        Iterator<Quintuple<State, Character,Character,String, State> > iterator =  this.transitions.iterator();
        Quintuple<State, Character,Character,String, State> quintupleCompatible = null;

        State fromPostEpsilonsTrans=deltaEpsilons(from);
        //System.out.println("ya ejecuto las transiciones epsilon disponibles!");
        while ((quintupleCompatible ==null )&&iterator.hasNext()) {
                Quintuple<State, Character,Character,String, State> quintupleCandidato = iterator.next();
                //System.out.println("quintuple candidato: "+quintupleCandidato.toString());

                if (!(stack.empty())&&quintupleCandidato.first().equals(fromPostEpsilonsTrans)&&quintupleCandidato.second().equals(c)&&(quintupleCandidato.third().equals(this.stack.peek())||quintupleCandidato.third().equals(Joker))  ) {
                        quintupleCompatible = quintupleCandidato;
                }
        }
        //System.out.println("quintuple candidato: ya busco las transiciones normales disponibles");
        if(quintupleCompatible != null) {
                //System.out.println("Se encontro un quintuple compatible para el caracter: "+c+" y el estado actual: "+from.name());

                return ejecutarTransicion(quintupleCompatible);
        }

        return null;
}

private State ejecutarTransicion(Quintuple<State, Character, Character, String, State> transicion) {

        //System.out.println("El quintuple es: "+quintupleCompatible.toString());
        char elementpop=stack.pop();
        String stringParaApilar =transicion.fourth();
        int i =stringParaApilar.length()-1;
        while(i>=0&&stringParaApilar.charAt(i)!=Lambda) {
                if(stringParaApilar.charAt(i)==Joker) {stack.push(elementpop); }else{
                        stack.push(stringParaApilar.charAt(i));
                }
                i--;
        }
       // System.out.println("Stack: "+stack.toString());
        return transicion.fifth();
}


private State deltaEpsilons(State from) {
        State result =from;
        boolean continuar=true;
        while (continuar) {
                Iterator<Quintuple<State, Character,Character,String, State> > iterator =  this.transitions.iterator();
                Quintuple<State, Character,Character,String, State> quintupleCompatible = null;
                while ((quintupleCompatible ==null )&&iterator.hasNext()) {
                        Quintuple<State, Character,Character,String, State> quintupleCandidato = iterator.next();
                        //System.out.println("quintuple candidato: "+quintupleCandidato.toString());
                        if (quintupleCandidato.first().equals(result)&&quintupleCandidato.second().equals(Lambda)&&(quintupleCandidato.third().equals(this.stack.peek())||quintupleCandidato.third().equals(Joker))  ) {
                                quintupleCompatible = quintupleCandidato;
                        }

                }
                if(quintupleCompatible!=null) {
                        result=ejecutarTransicion(quintupleCompatible);
                }else{
                        continuar=false;
                }

        }
        return result;
}


@Override
public boolean accepts(String cadena) {

        State estadoActual = this.initial;
        int indiceString = 0;
        while((indiceString<cadena.length()) && estadoActual!=null) {
                estadoActual=delta(estadoActual, cadena.charAt(indiceString));
                indiceString++;
        }

        //System.out.println("estados finales: "+this.finalStates().toString());
        if(indiceString==cadena.length()&&estadoActual!=null) {
                if (stack.empty()) {
                        System.out.println("Por pila vacia fue aceptado");
                        return true;
                }

                System.out.println("verif estado fial");
                Iterator<State> iteradorestadosfinales =finalStates.iterator();
                while (iteradorestadosfinales.hasNext()) {
                        if (iteradorestadosfinales.next().equals(estadoActual)) {
                                System.out.println("Por estado final fue aceptado");
                                return true;
                        }
                }
        }



        System.out.println("no acepto por ninguno");

        return false;
}

public boolean rep_ok() {
        return true;
}

public State newState(){
        int i=0;
        State estado = new State("q"+i);
        //System.out.println("nuevos posible estado: "+estado.toString());
        while(this.existeEnEstados(estado)) {
          //      System.out.println("estado rechazado");
                i++;
                estado = new State("q"+i);
                //System.out.println("nuevos posible estado: "+estado.toString());
        }
        this.states.add(estado);
        return estado;
}

public boolean existeEnEstados(State elementoprueba){
        boolean result=false;
        Iterator<State> iterador = states.iterator();
        while (iterador.hasNext()&&!result) {
                State estadoiterador =iterador.next();
                result = estadoiterador.equals(elementoprueba);
        }

        return result;
}

public void toPilaVacia(){
    //System.out.println("va a crear los dos estados");
    State nuevoinicial = newState();
    State nuevofinal =  newState();
    //System.out.println("creo los estados");

    Character caracterinicialnuevo = encontrarCaracterNoUsado();

    Quintuple<State, Character,Character,String, State> nuevatransicion =null;
    State actualIter =null;
    this.stackAlphabet.add(caracterinicialnuevo);
    Iterator<State> iteradorEstados =this.finalStates.iterator();
    while(iteradorEstados.hasNext()) {
        //    System.out.println("while to pila vacia");
            actualIter = iteradorEstados.next();
            if((!actualIter.equals(nuevofinal))&&(!actualIter.equals(nuevoinicial))) {
          //  	System.out.println("ENTRO AL IF TOpILAVACIA()");
                    nuevatransicion = new Quintuple<State, Character,Character,String, State> (actualIter,Lambda,Joker,""+Lambda,nuevofinal);
                    this.transitions.add(nuevatransicion);
            }
    }


    String strNuevaTrans = ""+this.stackInitial+""+caracterinicialnuevo;
    nuevatransicion = new Quintuple<State, Character,Character,String, State> (nuevoinicial,Lambda,caracterinicialnuevo,strNuevaTrans,this.initial);
    this.transitions.add(nuevatransicion);

    nuevatransicion = new Quintuple<State, Character,Character,String, State> (nuevofinal,Lambda,Joker,""+Lambda,nuevofinal);
    this.transitions.add(nuevatransicion);

    this.finalStates.clear();

    this.initial=nuevoinicial;
    this.stackInitial=caracterinicialnuevo;

    System.out.println("EJECUCION DEL ALGORITMO DE  ESTADO FINAL A PILA VACIA ! \n EL AUTOMATA RESULTANTE ES:");
    System.out.println(this.to_dot());

}

public void toEstadoFinal(){
   //     System.out.println("va a crear los dos estados");

        State nuevoinicial = newState();
        State nuevofinal =  newState();
     //   System.out.println("creo los estados");

        Character caracterinicialnuevo = encontrarCaracterNoUsado();
        Iterator<State> iteradorEstados =this.states.iterator();
        Quintuple<State, Character,Character,String, State> nuevatransicion =null;
        State actualIter =null;
        this.stackAlphabet.add(caracterinicialnuevo);

        while(iteradorEstados.hasNext()) {
       //         System.out.println("while toEStadoFinal");
                actualIter = iteradorEstados.next();
                if(!actualIter.equals(nuevofinal)&&!actualIter.equals(nuevoinicial)) {
                        nuevatransicion = new Quintuple<State, Character,Character,String, State> (actualIter,Lambda,caracterinicialnuevo,""+Lambda,nuevofinal);
                        this.transitions.add(nuevatransicion);
                }
        }
        String strNuevaTrans = ""+this.stackInitial+""+caracterinicialnuevo;
        nuevatransicion = new Quintuple<State, Character,Character,String, State> (nuevoinicial,Lambda,caracterinicialnuevo,strNuevaTrans,this.initial);
        this.transitions.add(nuevatransicion);
        this.finalStates.add(nuevofinal);
        this.initial=nuevoinicial;
        this.stackInitial=caracterinicialnuevo;

        System.out.println("EJECUCION DEL ALGORITMO DE PILA VACIA A ESTADO FINAL! \n EL AUTOMATA RESULTANTE ES:");
        System.out.println(this.to_dot());
}


private Character encontrarCaracterNoUsado() {
        Character c =65;
        System.out.println("BUSCANDO CARACTERES:");
        System.out.println(c);
        while(this.stackAlphabet.contains(c)) {
                c++;
                System.out.println(c);
        }
        return c;
}

public static NFAPila gramaticaToAutomataPila(String[] gramatica){
	
	Set<Quintuple<State, Character, Character, String, State> > transiciones = new HashSet<Quintuple<State, Character, Character, String, State> >();
    Set<State> estados = new HashSet<State>();
    Set<Character> alfabeto = new HashSet<Character>();
    Set<Character> alfabetoPila = new HashSet<Character>();
    Set<State> estadosfinales = new HashSet<State>();
	State estadounico =new State("q0");
    
    estados.add(estadounico);
	String[][] renglones = new String[30][30];
	int numerorenglon = 0; 
	int i=0;
	
	Set<Character> terminales = new HashSet<Character>();
    while (i<gramatica.length&&gramatica[i]!=null){
		renglones[i] = interpretarRenglon(gramatica[i]);
		terminales.add(renglones[i][0].charAt(0));
		i++;
	}
    System.out.println(terminales.toString());
    System.out.println("creacion de transiciones para renglones de no terminales");
    
    
    
    
    int j =0;
    i=0;
    int k=0;
    while(i<renglones.length&&renglones[i]!=null){
    	j=1;
    	while(j<renglones[i].length && renglones[i][j]!=null){
    		transiciones.add(new Quintuple<State, Character, Character, String, State>(estadounico, Lambda, renglones[i][0].charAt(0), renglones[i][j], estadounico) );
    		k=0;
    		System.out.println("renglones["+i+"]["+j+"] = "+renglones[i][j]);
    		while(k<renglones[i][j].length()){
    		   System.out.println("va a aprobarel k"+k);
    		   System.out.println("es "+renglones[i][j].charAt(k));
    			if(renglones[i][j].charAt(k)!=Lambda&&!terminales.contains(renglones[i][j].charAt(k))){
    				transiciones.add(new Quintuple<State, Character, Character, String, State>(estadounico,renglones[i][j].charAt(k), renglones[i][j].charAt(k), ""+Lambda, estadounico) );
    				
    			}
    			System.out.println("ya probo! el k"+k);
    			k++;
    		}
    		
    
    		
    		j++;
    	}
    	i++;
    }
    
    System.out.println(transiciones.toString());
    
    
    Iterator<Quintuple<State, Character, Character, String, State> > iteradorDeltas = transiciones.iterator();
    //System.out.println("Se creo el iterador que recorrera el set de quintuples para armar los alfabetos");
    while (iteradorDeltas.hasNext()) {
            Quintuple<State, Character, Character, String, State> elemento = iteradorDeltas.next();
            //System.out.println(elemento.toString());
            alfabeto.add(elemento.second());
            alfabetoPila.add(elemento.third());
            int indiceAuxiliar = 0;
            while (indiceAuxiliar < elemento.fourth().length()) {
                    alfabetoPila.add(elemento.fourth().charAt(indiceAuxiliar));
                    indiceAuxiliar++;
            }
    }
    
    Character inicialPila = renglones[0][0].charAt(0);
    
    return new NFAPila(estados, alfabeto, alfabetoPila, transiciones,
            inicialPila, estadounico, estadosfinales);
    
    
    
    
//	while(imp<renglon.length&&renglon[imp]!=null){
//		
//		imp++;
//	
//		
//		
//	}
//	
//	
//	
//	imp=0;
    
    
	
	
	
}


private static String[] interpretarRenglon(String r) {
	int i =0;
	int j =0;
	int indexOfArray=0;
	String[] result= new String[30];
	while(j<r.length() && r.charAt(j)!='-'){
		j++;
	}
	result[indexOfArray]=r.substring(i, j);
	indexOfArray++;
	i=j+2;
	j=i;
	while (j < r.length()-1) {
        while (j < r.length() && r.charAt(j) != '|'
               && r.charAt(j) != ';') {
                j++;
        }

        
		
		result[indexOfArray] = r.substring(i, j);
        indexOfArray++;
        j++;
        i = j;
}
	if(result[0]==null ){return null;}
	return result;
}




}

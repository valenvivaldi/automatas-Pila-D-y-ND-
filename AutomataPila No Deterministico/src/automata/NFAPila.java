package automata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.security.auth.login.Configuration;

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
        System.out.println("Is a NFA Pila");
}









@Override
public boolean accepts(String cadena){
	Stack<Character> stackaccept = (Stack<Character>) this.stack.clone();
	System.out.println(stackaccept.toString());
	Configuracion configuracion = new Configuracion(this.initial, this.stack, cadena, 0);
	
	return acceptsRECURSIVE(configuracion,20);
	
	
}

private boolean acceptsRECURSIVE(Configuracion configuracion, int nivel) {
	
	
	
	
	if(configuracion.indicecadena==configuracion.cadena.length()&&configuracion.stackactual.isEmpty()){
		System.out.println("Acepto por pila vacia!");
		return true;
	}
	if(configuracion.indicecadena==configuracion.cadena.length()&&configuracion.estadoactual.existeEnConjunto(this.finalStates)){
		System.out.println("Acepto por estadofinal!");
		return true;
	}
	
	
	if(nivel<0){
		
		return false;
	}	
	LinkedList<Configuracion> listaProximos = proximasConfiguraciones(configuracion);
	
	if(listaProximos.isEmpty()){
		
		return false;
	}else{
		
		System.out.println(" tiene configuraciones futuras posibles! son:"+listaProximos.size());
		int i=0;
		boolean result=false;
		while(i<listaProximos.size()&&!result){
	
			result=acceptsRECURSIVE(listaProximos.get(i),nivel-1);		
			i++;
		}
		return result;
	}
	
}

private LinkedList<Configuracion> proximasConfiguraciones(Configuracion configuracion) {
	LinkedList<Configuracion> result = new LinkedList<Configuracion>();
	
	Iterator<Quintuple<State, Character,Character,String, State> >iteradorTransiciones = this.transitions.iterator();
	Quintuple<State, Character,Character,String, State> elementoActual;
	String cadena =configuracion.cadena;
	int indicecadena =configuracion.indicecadena;
	if(configuracion.stackactual.isEmpty()){return result;}
	
	Character topePila =configuracion.stackactual.peek();
	
	boolean coincide1;
	boolean coincide2;
	boolean coincide3;
	boolean coincide4;
	
	while(iteradorTransiciones.hasNext()){
		 
		elementoActual=iteradorTransiciones.next();
		 
		 coincide1 =elementoActual.first().equals(configuracion.estadoactual);
		 coincide2 =configuracion.indicecadena<configuracion.cadena.length()&&elementoActual.second().equals(cadena.charAt(indicecadena));
		 coincide4 = elementoActual.second().equals(Lambda);		 
		 coincide3 =elementoActual.third().equals(topePila)||elementoActual.third().equals(Joker);
		 
		 
		 if(coincide1&&coincide2&&coincide3&&!coincide4){
			 
			 result.add(aplicarTransicion(configuracion,elementoActual));
		 }
		 
		 if(coincide1&&coincide4&&coincide3&&!coincide2){
			 result.add(aplicarTransicion(configuracion,elementoActual));
		 }
		 
	}
	
	return result;

	
}



private Configuracion aplicarTransicion(Configuracion configuracion,
		Quintuple<State, Character, Character, String, State> transicion) {
		Stack<Character> nuevostack = (Stack<Character>) configuracion.stackactual.clone();
		State nuevoestado =transicion.fifth();
		Character caracterpop =nuevostack.pop();
		int nuevoindicecadena = configuracion.indicecadena;
		if(transicion.second()!=Lambda){
			nuevoindicecadena++;
			
		}
		String paraApilar = transicion.fourth();
		int i=paraApilar.length()-1;
		while(i>=0){
			if(paraApilar.charAt(i)==Joker){
				nuevostack.add(caracterpop);
			}
			if(paraApilar.charAt(i)!=Joker && paraApilar.charAt(i)!=Lambda){
				nuevostack.add(paraApilar.charAt(i));
			}
		i--;
		}
		Configuracion resultado = new Configuracion(nuevoestado, nuevostack, configuracion.cadena, nuevoindicecadena);
	
	return resultado;
}




public boolean rep_ok() {
        return true;
}

public State newState(){
        int i=0;
        State estado = new State("q"+i);
        while(this.existeEnEstados(estado)) {

                i++;
                estado = new State("q"+i);

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

    State nuevoinicial = newState();
    State nuevofinal =  newState();


    Character caracterinicialnuevo = encontrarCaracterNoUsado();

    Quintuple<State, Character,Character,String, State> nuevatransicion =null;
    State actualIter =null;
    this.stackAlphabet.add(caracterinicialnuevo);
    Iterator<State> iteradorEstados =this.finalStates.iterator();
    while(iteradorEstados.hasNext()) {

            actualIter = iteradorEstados.next();
            if((!actualIter.equals(nuevofinal))&&(!actualIter.equals(nuevoinicial))) {

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


        State nuevoinicial = newState();
        State nuevofinal =  newState();


        Character caracterinicialnuevo = encontrarCaracterNoUsado();
        Iterator<State> iteradorEstados =this.states.iterator();
        Quintuple<State, Character,Character,String, State> nuevatransicion =null;
        State actualIter =null;
        this.stackAlphabet.add(caracterinicialnuevo);

        while(iteradorEstados.hasNext()) {
                actualIter = iteradorEstados.next();
                if(!actualIter.equals(nuevofinal)&&!actualIter.equals(nuevoinicial)) {
                        nuevatransicion = new Quintuple<State, Character,Character,String, State> (actualIter,Lambda,this.stackInitial,""+Lambda,nuevofinal);
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

    while (iteradorDeltas.hasNext()) {
            Quintuple<State, Character, Character, String, State> elemento = iteradorDeltas.next();
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




@Override
public Object delta(State from, Character c) {
	// TODO Auto-generated method stub
	return null;
}

}




class Configuracion{
	
	public State estadoactual;
	public Stack<Character> stackactual;
	public String cadena;
	public int indicecadena;

	public Configuracion(State actual, Stack<Character> pila,String cadena,int indice){
		this.estadoactual=actual;
		this.stackactual =pila;
		this.cadena=cadena;
		this.indicecadena=indice;
		}
	
	
	public String toString(){
		return "Estado: "+estadoactual.toString()+" strack:"+stackactual.toString()+" cadena:"+cadena+" indice"+indicecadena;
		
	}
	
}
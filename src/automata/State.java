package automata;

import java.util.Set;
import java.util.Iterator;

public class State {


private String name;

public State(String name) {
        this.name = name;
}

public String name() {
        return name;
}

public boolean existeEnConjunto(Set<State> conjunto){
	boolean result =false;
	Iterator<State> iterador = conjunto.iterator();
	while(iterador.hasNext()&& !result){
		System.out.println("iteracion existeconjunto");
		result= iterador.next().equals(this);
	}
	
	return result;
}



public boolean equals(Object obj) {
        if (!(obj instanceof State)){
        	
		System.out.println("no es instancia de State");
		return false;
        }
                
        if (obj == this)
                return true;
        System.out.println("this.name = '"+this.name+"'"+"obj.name = '"+((State) obj).name+"'");
        String nombreobj = ((State) obj).name;
        System.out.println("verificacion por strings "+this.name.equals(nombreobj));
        return this.name.equals(nombreobj);
}

public String toString(){
        return name;
}

public void rename(String Newname){
        name= Newname;
}

}

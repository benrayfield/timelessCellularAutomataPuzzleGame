package humanaicore.common;
//import static log.Lg.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Synchronized Var */
public class SVar<T> implements Supplier<T>, Consumer<T>{
	
	protected T value;
	
	protected final Set<Consumer<SVar<T>>> listeners = new HashSet();
	
	public SVar(T firstValue){
		value = firstValue;
		//lgErr("Constructor SVar.value="+this.value);
	}
	
	public synchronized void startListening(Consumer<SVar<T>> listener){
		listeners.add(listener);
	}
	
	public synchronized void stopListening(Consumer<SVar<T>> listener){
		listeners.remove(listener);
	}
	
	public synchronized void stopAllListeners(){
		listeners.clear();
	}
	
	/** Unsynchronized for speed, allowed cuz a pointer wont be split by thread-error */
	public T get(){
		//lgErr("Get SVar.value="+this.value);
		return value;
	}
	
	public synchronized void accept(T value){
		this.value = value;
		//lgErr("Set SVar.value="+this.value);
		for(Consumer<SVar<T>> listener : listeners){
			listener.accept(this);
		}
	}

}

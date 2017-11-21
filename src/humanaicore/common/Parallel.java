/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL */
package humanaicore.common;
import java.util.concurrent.ForkJoinPool;

public class Parallel{
	private Parallel(){}
	
	public static final ForkJoinPool cpus;
	static{
		int howManyCpus = Runtime.getRuntime().availableProcessors();
		/*ForkJoinWorkerThreadFactory factory = new ForkJoinWorkerThreadFactory(){
			public ForkJoinWorkerThread newThread(ForkJoinPool pool){
				ForkJoinWorkerThread t = new ForkJoinWorkerThread(pool){
					
				};
			}
		};
		UncaughtExceptionHandler handler = new UncaughtExceptionHandler(){
			public void uncaughtException(Thread t, Throwable e){
				throw new Err(e);
			}
		};
		boolean asyncMode = false;
		cpus = new ForkJoinPool(howManyCpus, factory, handler, asyncMode);
		*/
		cpus = new ForkJoinPool(howManyCpus);
	}

}

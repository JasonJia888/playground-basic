import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

public class IntermediateTask {
	
	// Create a FHIR client
    FhirContext fhirContext = FhirContext.forR4();
    IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
    RequestStopWatch requestStopWatch = new RequestStopWatch();


    public IntermediateTask() {
        client.registerInterceptor(new LoggingInterceptor(false));
        client.registerInterceptor(new TimerInterceptor(requestStopWatch));
    }
    
    private List<String> getFirstNames (File dataFile){
    	List<String> firstNames = new ArrayList<>();
    	
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			String line = reader.readLine();
			while (line != null) {
				if(line.trim().length()>0)
					firstNames.add(line.trim());
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return firstNames;
    }
    
    public void runBatchSearch(File dataFile, boolean disableCache) {
    	requestStopWatch.reset();
    	List<String> firstNames = getFirstNames(dataFile);
    	CacheControlDirective cacheCtrol = new CacheControlDirective();
        cacheCtrol.setNoCache(disableCache);
        
        for(String fistName : firstNames) {
	        client
			    .search()
			    .forResource("Patient")
			    .where(Patient.FAMILY.matches().value(fistName))
			    .cacheControl(cacheCtrol)
			    .returnBundle(Bundle.class)
			    .execute();
        }
        
        System.out.println("The average request time is : " + requestStopWatch.getAverageTime());
    }
	
    public static void main(String[] theArgs) throws URISyntaxException {

    	IntermediateTask task = new IntermediateTask();
    	File resource = new File(task.getClass().getClassLoader().getResource("first_names.txt").toURI());
        task.runBatchSearch(resource, false);
        task.runBatchSearch(resource, false);
        task.runBatchSearch(resource, true);
 
    }
    
    

    static class TimerInterceptor  implements IClientInterceptor {
    	
    	RequestStopWatch stopWatch;
        public TimerInterceptor(RequestStopWatch stopWatch) {
        	super();
    		this.stopWatch = stopWatch;
    	}

    	@Override
    	public void interceptRequest(IHttpRequest theRequest) {
    		stopWatch.start();
    	}

    	@Override
    	public void interceptResponse(IHttpResponse theResponse) throws IOException {
    		stopWatch.stop();
    	}
    	
    }
    
    static class RequestStopWatch {
    	long startMili = -1;
    	long accumulatedMili = 0;
    	int times = 0;
    	
    	public void reset()
    	{
    		startMili = -1;
        	accumulatedMili = 0;
        	times = 0;
    	}
    	
    	public void start() {
    		startMili = System.currentTimeMillis();
    	}
    	
    	public void stop() {
    		if(startMili != -1) {
    			accumulatedMili += System.currentTimeMillis() - startMili;
    			times += 1;
    		}
    		startMili = -1;
    	}
    	
    	public long getTotalTime () {
    		return accumulatedMili;
    	}
    	
    	public long getAverageTime() {
    		if(times > 0)
    			return accumulatedMili/times;
    		else 
    			return -1;
    	}
    	
    }
    

    
}



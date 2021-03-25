import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

public class BasicTask {
    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Search for Patient resources
        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value("SMITH"))
                .returnBundle(Bundle.class)
                .execute();
        
        List<BundleEntryComponent> entries = response.getEntry();

        entries.stream().sorted((x, y) -> ((Patient)x.getResource()).getName().get(0).getGivenAsSingleString().compareTo(((Patient)y.getResource()).getName().get(0).getGivenAsSingleString()))
        	   .forEach(x -> System.out.println (((Patient)x.getResource()).getName().get(0).getGivenAsSingleString() + " " + ((Patient)x.getResource()).getName().get(0).getFamily()));

    }

}

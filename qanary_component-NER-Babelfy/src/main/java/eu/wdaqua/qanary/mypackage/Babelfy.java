package eu.wdaqua.qanary.mypackage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.wdaqua.qanary.commons.QanaryMessage;
import eu.wdaqua.qanary.commons.QanaryQuestion;
import eu.wdaqua.qanary.commons.QanaryUtils;
import eu.wdaqua.qanary.component.QanaryComponent;


@Component
/**
 * This component connected automatically to the Qanary pipeline.
 * The Qanary pipeline endpoint defined in application.properties (spring.boot.admin.url)
 * @see <a href="https://github.com/WDAqua/Qanary/wiki/How-do-I-integrate-a-new-component-in-Qanary%3F" target="_top">Github wiki howto</a>
 */
public class Babelfy extends QanaryComponent {
	private static final Logger logger = LoggerFactory.getLogger(Babelfy.class);
	private static long questionCount = 0;

	/**
	 * implement this method encapsulating the functionality of your Qanary
	 * component
	 * @throws Exception 
	 */
	@Override
	public QanaryMessage process(QanaryMessage myQanaryMessage) throws Exception {
		logger.info("process: {}", myQanaryMessage);
		// TODO: implement processing of question
		// STEP1: Retrieve the named graph and the endpoint
		
	      QanaryUtils myQanaryUtils = this.getUtils(myQanaryMessage);
	      QanaryQuestion<String> myQanaryQuestion = new QanaryQuestion(myQanaryMessage);
	      String myQuestion = myQanaryQuestion.getTextualRepresentation();
	      ArrayList<Selection> selections = new ArrayList<Selection>();
	      
	      logger.info("Question {}", myQuestion);
	      
	      String thePath = "";
	      thePath = URLEncoder.encode(myQuestion, "UTF-8"); 
	      
	      logger.info("Path {}", thePath);
	      
	      HttpClient httpclient = HttpClients.createDefault();
	      HttpGet httpget = null;
		    if(questionCount < 990) {
		    		httpget = new HttpGet("https://babelfy.io/v1/disambiguate?text="+thePath+"&lang=AGNOSTIC&key=ffebfbbc-77b4-4606-95e2-2ff750df0fb9");
		    }
		    else if( questionCount >=990 && questionCount < 1980) {
		    		httpget = new HttpGet("https://babelfy.io/v1/disambiguate?text="+thePath+"&lang=AGNOSTIC&key=fa48a042-a7a8-4021-9609-b6b40dc6ff2a");
			}
		    else if(questionCount >=1980 && questionCount < 2970) {
		     	httpget = new HttpGet("https://babelfy.io/v1/disambiguate?text="+thePath+"&lang=AGNOSTIC&key=57d661bf-61fc-4798-ad15-1c8af09edff4");
		    }
		    else {
	    			httpget = new HttpGet("https://babelfy.io/v1/disambiguate?text="+thePath+"&lang=AGNOSTIC&key=3b7f49fb-099f-4145-8e0c-c858e4d923e7");
		    }
	      HttpResponse response = httpclient.execute(httpget);
	      try {
	    	 
	          HttpEntity entity = response.getEntity();
	          if (entity != null) {
 	             InputStream instream = entity.getContent();
    // String result = getStringFromInputStream(instream);
 	             String text = IOUtils.toString(instream, StandardCharsets.UTF_8.name());
 	             JSONArray jsonArray = new JSONArray(text); 
 	            for (int i = 0; i < jsonArray.length(); i++) {
 	                JSONObject explrObject = jsonArray.getJSONObject(i);
 	                logger.info("JSON {}", explrObject);
 	                double score = (double) explrObject.get("score");
 	                if(score>=0.5)
 	                {
 	                JSONObject char_array = explrObject.getJSONObject("charFragment");
 	               int begin = (int) char_array.get("start");
 	                int end = (int) char_array.get("end");
 	               logger.info("Question: {}", begin);
 	                logger.info("Question: {}", end);
 	               Selection s = new Selection();
 	                s.begin = begin;
 	                s.end = end+1;
 	                selections.add(s);
 	                }
 	            }
	          }
	      }
	      catch (ClientProtocolException e) {
	 		 logger.info("Exception: {}", e);
	         // TODO Auto-generated catch block
	     } catch (IOException e1) {
	     	logger.info("Except: {}", e1);
	         // TODO Auto-generated catch block
	     }catch (Exception e1) {
		     	logger.info("Except: {}", e1);
		         // TODO Auto-generated catch block
		     }
		logger.info("store data in graph {}", myQanaryMessage.getValues().get(myQanaryMessage.getEndpoint()));
		// TODO: insert data in QanaryMessage.outgraph

		logger.info("apply vocabulary alignment on outgraph");
		// TODO: implement this (custom for every component)
		for (Selection s : selections) {
            String sparql = "prefix qa: <http://www.wdaqua.eu/qa#> "
                    + "prefix oa: <http://www.w3.org/ns/openannotation/core/> "
                    + "prefix xsd: <http://www.w3.org/2001/XMLSchema#> " + "INSERT { " + "GRAPH <" + myQanaryMessage.getOutGraph() + "> { "
                    + "  ?a a qa:AnnotationOfSpotInstance . " + "  ?a oa:hasTarget [ "
                    + "           a    oa:SpecificResource; " + "           oa:hasSource    <" + myQanaryQuestion.getUri() + ">; "
                    + "           oa:hasSelector  [ " + "                    a oa:TextPositionSelector ; "
                    + "                    oa:start \"" + s.begin + "\"^^xsd:nonNegativeInteger ; "
                    + "                    oa:end  \"" + s.end + "\"^^xsd:nonNegativeInteger  " + "           ] "
                    + "  ] ; " + "     oa:annotatedBy <http://babelfyNER.com> ; "
                    + "	    oa:AnnotatedAt ?time  " + "}} " + "WHERE { " + "BIND (IRI(str(RAND())) AS ?a) ."
                    + "BIND (now() as ?time) " + "}";
            myQanaryUtils.updateTripleStore(sparql);
        }
		return myQanaryMessage;
	}
	class Selection {
        public int begin;
        public int end;
    }
}

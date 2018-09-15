package eu.wdaqua.qanary.earlrel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
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
public class EarlRelationLinking extends QanaryComponent {
	private static final Logger logger = LoggerFactory.getLogger(EarlRelationLinking.class);

	/**
	 * implement this method encapsulating the functionality of your Qanary
	 * component
	 */
	@Override
	public QanaryMessage process(QanaryMessage myQanaryMessage) throws Exception{
		logger.info("process: {}", myQanaryMessage);
		// TODO: implement processing of question
		QanaryUtils myQanaryUtils = this.getUtils(myQanaryMessage);
		QanaryQuestion<String> myQanaryQuestion = new QanaryQuestion(myQanaryMessage);
		String myQuestion = myQanaryQuestion.getTextualRepresentation();
		//String myQuestion = "Who is the president of Russia?";
		ArrayList<Selection> selections = new ArrayList<Selection>();
		logger.info("Question {}", myQuestion);
		String thePath = URLEncoder.encode(myQuestion, "UTF-8");
		logger.info("thePath {}", thePath);
		JSONObject msg = new JSONObject();
		msg.put("nlquery", myQuestion);
		String jsonThePath = msg.toString();
		try {

			String[] entityLinkCmd = { "curl", "-XPOST", "http://sda.tech/earl/api/processQuery", "-H",
					"Content-Type: application/json", "-d", jsonThePath };
			// logger.info("EntityLinkCmd: {}",Arrays.toString(entityLinkCmd));
			ProcessBuilder processEL = new ProcessBuilder(entityLinkCmd);
			// logger.info("ProcessEL: {}", processEL.command());
			Process pEL = processEL.start();
			// logger.error("Process PEL: {}", IOUtils.toString(pEL.getErrorStream()));
			InputStream instreamEL = pEL.getInputStream();
			String textEL = IOUtils.toString(instreamEL, StandardCharsets.UTF_8.name());
			JSONObject response = new JSONObject(textEL);
			JSONObject lists = response.getJSONObject("rerankedlists");
			//System.out.println(lists);
			JSONArray chunks = response.getJSONArray("chunktext");
			//System.out.println(chunks +"\n" + lists);
			for (int i = 0; i < chunks.length(); i++) {
				JSONObject ja = chunks.getJSONObject(i);
				String str = ja.getString("class").toLowerCase();
				if(str.equals("relation")) {
					Selection s = new Selection();
					int start = ja.getInt("surfacestart");
					int end = start + ja.getInt("surfacelength") - 1;
					String link = lists.getJSONArray(i+"").getJSONArray(0).getString(1);
					s.begin = start;
					s.end = end;
					s.link = link;
					System.out.println(start+" "+end+" "+link);
					selections.add(s);
				}
			}
		}	
		catch (JSONException e) {
			logger.error("Except: {}", e);

		}
		catch (IOException e) {
			 logger.error("Except: {}", e);
			// TODO Auto-generated catch block
		} 
		catch (Exception e) {
			 logger.error("Except: {}", e);
			// TODO Auto-generated catch block
		}


		for (Selection s : selections) {
            String sparql = "PREFIX qa: <http://www.wdaqua.eu/qa#> " //
                    + "PREFIX oa: <http://www.w3.org/ns/openannotation/core/> " //
                    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " //
                    + "INSERT { " + "GRAPH <" + myQanaryQuestion.getOutGraph() + "> { " //
                    + "  ?a a qa:AnnotationOfRelation . " //
                    + "  ?a oa:hasTarget [ " //
                    + "           a    oa:SpecificResource; " //
                    + "           oa:hasSource    <" + myQanaryQuestion.getUri() + ">; " //
                    + "           oa:hasSelector  [ " //
                    + "                    a oa:TextPositionSelector ; " //
                    + "                    oa:start \"" + s.begin + "\"^^xsd:nonNegativeInteger ; " //
                    + "                    oa:end  \"" + s.end + "\"^^xsd:nonNegativeInteger  " //
                    + "           ] " //
                    + "  ] . " //
                    + "  ?a oa:hasBody <" + s.link + "> ;" //
                    + "     oa:annotatedBy <http://earlrelationlinker.com> ; " //
                    + "	    oa:AnnotatedAt ?time  " + "}} " //
                    + "WHERE { " //
                    + "  BIND (IRI(str(RAND())) AS ?a) ."//
                    + "  BIND (now() as ?time) " //
                    + "}";
            logger.debug("Sparql query: {}", sparql);
            myQanaryUtils.updateTripleStore(sparql);
		}

		logger.info("store data in graph {}", myQanaryMessage.getValues().get(myQanaryMessage.getEndpoint()));
		// TODO: insert data in QanaryMessage.outgraph

		logger.info("apply vocabulary alignment on outgraph");
		// TODO: implement this (custom for every component)

		return myQanaryMessage;
	}
	class Selection {
		public int begin;
		public int end;
		public String link;
	}
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.gestore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Giovanni
 */
public class AIMLReader {

    private final String PATH;

    private Document doc;

    public AIMLReader(String PATH) {
        this.PATH = PATH;
        init();
        getPatternText();
    }

    private void init() {
        try {
            File fXmlFile = new File(PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            

        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(AIMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public HashMap<String, Element> getPatternText() {

        HashMap<String, Element> patternTamplate = new HashMap<>();
        NodeList pattern = doc.getElementsByTagName("category");

        for (int i = 0; i < pattern.getLength(); i++) {
            Node nNode = pattern.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element category = (Element) nNode;
                patternTamplate.put(category.getElementsByTagName("pattern").item(0).getTextContent(), category);
				
				
                
                //Element template = (Element)eElement.getElementsByTagName("template").item(0);
              
                /*for (int j = 0; j < template.getElementsByTagName("star").getLength(); j++) {
                    System.out.println(template.getElementsByTagName("star").item(i).getNodeName());
                }*/

            }
        }

        return patternTamplate;
        
    }

}

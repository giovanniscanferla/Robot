/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.gestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Giovanni
 */
public class Protocol {

    private AIMLReader aiml;
    private String clientSentence;
    private HashMap<String, Element> pattern;
    private HashMap<String, String> variabili;
    private HashMap<String, String> costanti;

    public Protocol() {
        aiml = new AIMLReader("aiml\\aiml.xml");
        pattern = aiml.getPatternText();
        variabili = new HashMap<>();
        costanti = new HashMap<>();
    }

    public String elaborateAnswer(String clientSentence) {

        this.clientSentence = clientSentence;

        String serverSentence = "Something went wrong during the question elaboration";
        
        if(clientSentence.equals("")){
            return serverSentence;
        }

        clientSentence = formatString(clientSentence); //FORMATTA LA STRINGA IN MODO CHE SIA POSSIBILE INTEPRETARLA

        String patternString = matchPattern(pattern.keySet().toArray(), clientSentence); //TROVA IL PATTERN CORRETTO PER LA DOMANDA

        Element categoyNode = pattern.get(patternString);
        Element template = (Element) categoyNode.getElementsByTagName("template").item(0);
        NodeList list = template.getChildNodes();

        String answer = getAnswer(list, patternString, clientSentence); //TROVA LA RISPOSTA GIUSTA ALLA DOMANDA

        serverSentence = (answer.equals("")) ? serverSentence : answer;

        return serverSentence;
    }

    private String formatString(String sentence) {
        try (Scanner sc = new Scanner(new File("aiml\\normal.substitution"))) {

            sentence = sentence.toLowerCase(Locale.ENGLISH);

            if (sentence.charAt(sentence.length() - 1) == '.' || sentence.charAt(sentence.length() - 1) == '?' || sentence.charAt(sentence.length() - 1) == '!') {
                sentence = sentence.substring(0, sentence.length() - 1) + "";
            }

            while (sc.hasNextLine()) {
                String[] replace = sc.nextLine().split("~");
                while (contains(sentence, replace[1])) {
                    sentence = sentence.replace(replace[1], replace[3]);
                }
            }
            sentence = sentence.toUpperCase(Locale.ENGLISH);
        } catch (FileNotFoundException ex) {

        }
        return sentence;
    }

    private String matchPattern(Object[] patterns, String clientSentence) {
        String patternChose = null;
        String[] clientArray = clientSentence.split(" ");
        Arrays.sort(patterns, new ComparatorXML());

        for (Object patString : patterns) {
            String pat = (String) patString;

            if (pat.equals(clientSentence)) {
                return pat;
            }
        }

        for (Object patString : patterns) {
            String pat = (String) patString;

            if (pat.contains("*")) {

                String[] patternArray = pat.split(" ");
                boolean corretta = true;

                for (String patternElement : patternArray) {
                    if (!patternElement.equals("*") && !clientSentence.contains(patternElement)) {
                        corretta = false;
                    }
                }

                int j = 0;

                for (int i = 0; i < patternArray.length && j < clientArray.length; i++) {
                    if (!patternArray[i].equals("*") && patternArray[i].equals(clientArray[j])) {
                        j++;
                    } else if (patternArray[i].equals("*")) {
                        j++;
                        if (i != patternArray.length - 1 && j != clientArray.length && patternArray[i + 1].equals(clientArray[j])) {
                            i++;
                        }
                        i--;
                    } else {
                        corretta = false;
                        break;
                    }
                }

                if (corretta && j == clientArray.length) {
                    return pat;
                }

            }

        }
        return patternChose;
    }

    private String getAnswer(NodeList list, String key, String clientSentence) {

        String response = "";

        for (int i = 0; i < list.getLength(); i++) {
            Node nNode = list.item(i);

            if (nNode.getNodeType() == Node.TEXT_NODE) {
                response = response + nNode.getTextContent();
            } else {

                switch (nNode.getNodeName()) {
                    //-----------------------------------------------------------------------------
                    case "star":
                        String index = ((Element) nNode).getAttribute("index");
                        index = (index.equals("")) ? "1" : index;

                        response = response + getStar(key, clientSentence, Integer.parseInt(index));

                        break;
                    //-----------------------------------------------------------------------------
                    case "srai":
                        String newPattern = "";

                        NodeList listSrai = ((Element) nNode).getChildNodes();

                        newPattern = getAnswer(listSrai, key, clientSentence);

                        newPattern = formatString(newPattern);
                        String patternString = matchPattern(pattern.keySet().toArray(), newPattern); //TROVA IL PATTERN CORRETTO PER LA DOMANDA

                        Element categoyNode = pattern.get(patternString);
                        Element template = (Element) categoyNode.getElementsByTagName("template").item(0);

                        NodeList listTemplate = template.getChildNodes();

                        response = response + getAnswer(listTemplate, patternString, newPattern);

                        break;
                    //-----------------------------------------------------------------------------
                    case "random":

                        NodeList listaRandom = ((Element) nNode).getChildNodes();

                        Random r = new Random();

                        Node randomNode = listaRandom.item(r.nextInt(listaRandom.getLength()));

                        response = response + getAnswer(((Element) randomNode).getChildNodes(), key, clientSentence);

                        break;

                    //-----------------------------------------------------------------------------
                    case "think":

                        NodeList listaVariabili = ((Element) nNode).getChildNodes();

                        for (int j = 0; j < listaVariabili.getLength(); j++) {
                            Node variabile = listaVariabili.item(j);
                            String nome = ((Element) variabile).getAttribute("name");
                            String type = ((Element) variabile).getAttribute("type");
                            type = (!type.equals("final")) ? "normal" : type;
                            String info = "";

                            NodeList infoList = ((Element) variabile).getChildNodes();

                            info = getAnswer(infoList, key, clientSentence);

                            if (type.equals("normal")) {
                                this.variabili.put(nome, info);
                            } else if (!this.costanti.containsKey(nome)) {
                                this.costanti.put(nome, info);
                            }

                        }

                        break;
                    //-----------------------------------------------------------------------------
                    case "get":

                        if (this.costanti.containsKey(((Element) nNode).getAttribute("name"))) {
                            response = response + this.costanti.get(((Element) nNode).getAttribute("name"));
                        } else {
                            response = response + this.variabili.get(((Element) nNode).getAttribute("name"));
                        }

                        break;
                    //-----------------------------------------------------------------------------
                    case "condition":

                        Element conditionElement = (Element) nNode;

                        String variableName = conditionElement.getAttribute("name");
                        String variableEqual = conditionElement.getAttribute("value");
                        String variableType = conditionElement.getAttribute("type");
                        variableType = (variableType.equals("")) ? "equals" : variableType;
                        String variableValue;

                        if (variableType.equals("equals")) {
                            if (this.costanti.containsKey(variableName)) {
                                variableValue = this.costanti.get(variableName);

                                if (variableEqual.equals(variableValue)) {
                                    response = response + getAnswer(conditionElement.getChildNodes(), key, clientSentence);
                                }

                            } else if (this.variabili.containsKey(variableName)) {
                                variableValue = this.variabili.get(variableName);
                                if (variableEqual.equals(variableValue)) {
                                    response = response + getAnswer(conditionElement.getChildNodes(), key, clientSentence);
                                }
                            }

                        } else if (variableType.equals("exist")) {
                            if (this.costanti.containsKey(variableName) || this.variabili.containsKey(variableName)) {
                                response = response + getAnswer(conditionElement.getChildNodes(), key, clientSentence);
                            }
                        } else if (variableType.equals("notexist")) {
                            if (!this.costanti.containsKey(variableName) && !this.variabili.containsKey(variableName)) {
                                response = response + getAnswer(conditionElement.getChildNodes(), key, clientSentence);
                            }
                        }

                }

            }
        }

        return response;
    }

    private String getStar(String pattern, String client, int index) {
        String star;

        int precNum = -1;
        int pointer = -1;
        String succ;
        String prec;

        while (index > 0) {
            precNum = pointer;
            pointer = pattern.indexOf("*", precNum + 1);
            index--;
        }

        if (pattern.substring(pointer + 1).contains("*")) {
            succ = pattern.substring(pointer + 1, pattern.indexOf("*", pointer + 1));
        } else {
            succ = pattern.substring(pointer + 1);
        }

        prec = pattern.substring(precNum + 1, pointer);

        if (prec.equals("")) {
            precNum = 0;
        } else {
            precNum = client.indexOf(prec) + prec.length();
        }

        if (succ.equals("")) {
            pointer = client.length();
        } else {
            pointer = client.indexOf(succ, pointer);
        }

        star = this.clientSentence.substring(precNum, pointer);

        return star;
    }

    private boolean contains(String toCheck, String contained) {
        if (toCheck.contains(contained)) {
            String[] checkArray = toCheck.split(" ");
            String[] contArray = contained.split(" ");
            for (int i = 0; i < checkArray.length - contArray.length; i++) {
                boolean cont = true;
                for (int j = 0; j < contArray.length; j++) {
                    if (!contArray[j].equals(checkArray[j])) {
                        cont = false;
                        break;
                    }
                }

                if (cont) {
                    return true;
                }
            }

        }
        return false;
    }
}

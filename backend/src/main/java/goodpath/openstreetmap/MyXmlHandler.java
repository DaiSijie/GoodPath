package goodpath.openstreetmap;

import org.w3c.dom.DOMException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

public class MyXmlHandler extends DefaultHandler {

    public static Graph parse() {
        MyXmlHandler xmlHandler = new MyXmlHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse("maps", xmlHandler);


        } catch (DOMException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlHandler.getGraph();
    }

    private Graph graph;
    private long previous = -1;

    @Override
    public void startDocument() throws SAXException {
        System.out.println("Début du parsing");
        graph = new Graph();
    }

    @Override
    public void endDocument() throws SAXException {
        System.out.println("Fin du parsing: nodes = "+graph.getNodeKeys().size());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if("node".equals(qName)) {
            long id = Long.parseLong(attributes.getValue("id"));
            double lat = Double.parseDouble(attributes.getValue("lat"));
            double lng = Double.parseDouble(attributes.getValue("lon"));
            graph.addNode(id, lng, lat);
        }

        if("nd".equals(qName)) {
            long current = Long.parseLong(attributes.getValue("ref"));
            if(previous != -1) {
                graph.addEdge(previous, current);
            }
            previous = current;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("way".equals(qName)) {
            previous = -1;
        }
    }

    public Graph getGraph() {
        return graph;
    }
}

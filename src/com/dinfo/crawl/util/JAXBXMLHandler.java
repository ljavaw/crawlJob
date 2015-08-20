package com.dinfo.crawl.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.oxm.CharacterEscapeHandler;
 
import com.dinfo.crawl.bean.Crawl;
 
public class JAXBXMLHandler {
 
	/**
	 * object to xml
	 * @param Crawl
	 * @param selectedFile
	 * @throws IOException
	 * @throws JAXBException
	 */
    public static void marshal(Crawl crawl, File selectedFile)
            throws IOException, JAXBException {
        JAXBContext context;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(selectedFile));
            context = JAXBContext.newInstance(Crawl.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty("com.sun.xml.internal.bind.characterEscapeHandler",
                new CharacterEscapeHandler() {
                    @Override
                    public void escape(char[] ch, int start, int length,
                            boolean isAttVal, Writer writer)
                            throws IOException {
                        writer.write(ch, start, length);
                    }
                });
            m.marshal(crawl, writer);
        } finally {
            try {
                writer.close();
            } catch (IOException io) { 
            }
        }
    }
 
   /* public static Crawl unmarshal(File importFile) throws JAXBException {
        Crawl Crawl = new Crawl();
        JAXBContext context;
 
        context = JAXBContext.newInstance(Crawl.class);
        Unmarshaller um = context.createUnmarshaller();
        Crawl = (Crawl) um.unmarshal(importFile);
 
        return Crawl;
    }*/
}
	
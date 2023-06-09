import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LoadOSM {
    public void Load() throws ParserConfigurationException, IOException, SAXException {
        //read file, keep id lat and long
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        RstarTree tree = new RstarTree(new RstarNode(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE));

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(new File("map.xml"));
        NodeList nodeList = document.getElementsByTagName("node");
        nodeList.getLength();
        int size = nodeList.getLength();
        long[] id = new long[size];
        double[] lat = new double[size], lon = new double[size];
        for (int x = 0; x < size; x++) {
            id[x] = Long.parseLong((nodeList.item(x).getAttributes().getNamedItem("id").getNodeValue()));
            lat[x] = Double.parseDouble((nodeList.item(x).getAttributes().getNamedItem("lat").getNodeValue()));
            lon[x] = Double.parseDouble((nodeList.item(x).getAttributes().getNamedItem("lon").getNodeValue()));

        }

        // Create datafile
        String strFilePath = "dataBlocks.txt";
        try {
            FileOutputStream fos = new FileOutputStream(strFilePath);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeChar('b');                 // The first 4 digits are the block id
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeInt(size/1365+1);
            dos.writeInt(size);
            dos.writeInt(1365);         // Each entry consist of 24bytes so in order to create 32KB blocks each block must have 1365 entries plus 8 bytes at the beggining which shows the block id
            int blocks_Number = 1;
            dos.writeChar('b');
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeChar((char) blocks_Number);
            int count = 0;
            for (int x = 0; x < size; x++) {
                if (count == 1365) {
                    // The number of entries in each block is 1365 so the block size is 32 KB. When we change blocks  we print the id of the new block in the beginning
                    count = 0;
                    blocks_Number++;
                    dos.writeChar('b');
                    if (blocks_Number < 10) {
                        dos.writeChar('0');
                        dos.writeChar('0');
                        dos.writeChar((char) blocks_Number);
                    } else {
                        String bns = String.valueOf(blocks_Number);
                        char[] tmp = bns.toCharArray();
                        if (blocks_Number < 99) {
                            dos.writeChar('0');
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                        } else {
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                            dos.writeChar((char) tmp[2]);
                        }
                    }

                }
                tree.Insert(new Point(lat[x],lon[x],blocks_Number,count));
                // We print the data in the datafile
                dos.writeLong(id[x]);
                dos.writeDouble(lat[x]);
                dos.writeDouble(lon[x]);
                count++;
            }
            tree.WriteFile();//serializable
            dos.close();
        } catch (
                IOException e) {
            System.out.println("IOException : " + e);
        }

    }


    public void addLocation(Location loc, ArrayList<Location> locations) {
        String strFilePath = "Database Technology Project/dataBlocks.txt";
        locations.add(loc);
        try {
            FileOutputStream fos = new FileOutputStream(strFilePath);
            DataOutputStream dos = new DataOutputStream(fos);
            /*dos.writeChar('b');                 // The first 4 digits are the block id
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeInt(size/1365+1);
            dos.writeInt(size);
            dos.writeInt(1365);*/          // Each entry consist of 24bytes so in order to create 32KB blocks each block must have 1365 entries plus 8 bytes at the beggining which shows the block id
            int blocks_Number = 1;
            dos.writeChar('b');
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeChar((char) blocks_Number);
            int count = 0;
            for (int x = 0; x < locations.size(); x++) {
                if (count == 1365) {
                    // The number of entries in each block is 1365 so the block size is 32 KB. When we change blocks  we print the id of the new block in the beginning
                    count = 0;
                    blocks_Number++;
                    dos.writeChar('b');
                    if (blocks_Number < 10) {
                        dos.writeChar('0');
                        dos.writeChar('0');
                        dos.writeChar((char) blocks_Number);
                    } else {
                        String bns = String.valueOf(blocks_Number);
                        char[] tmp = bns.toCharArray();
                        if (blocks_Number < 99) {
                            dos.writeChar('0');
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                        } else {
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                            dos.writeChar((char) tmp[2]);
                        }
                    }

                }
                // We print the data in the datafile
                dos.writeLong(locations.get(x).getId());
                dos.writeDouble(locations.get(x).getLat());
                dos.writeDouble(locations.get(x).getLon());
                count++;
            }
            dos.close();
        } catch (
                IOException e) {
            System.out.println("IOException : " + e);


        }

    }
}


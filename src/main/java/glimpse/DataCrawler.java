package glimpse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import glimpse.kafka.KafkaProducerService;
import glimpse.models.Address;
import glimpse.models.Destination;
import glimpse.models.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DataCrawler {

    static Map<Type, CrawlData> destinationInfo = null;

    private static String baseDirectory
            ="/Users/kunal.singh1/projects/glimpse/";

    private static String baseDocumentDirectory
            = baseDirectory+"src/main/resources/documents/karnataka/trekking";

    private static String website = "http://www.bangaloreorbit.com/";
    private static String baseUrl = "http://www.bangaloreorbit.com/trekking-in-karnataka/";
    private static String url = baseUrl + "index.html";

    static {
        destinationInfo = new HashMap<>();

        destinationInfo.put(Type.TREKKING, new CrawlData("trekking-in-karnataka", "trekking"));
        destinationInfo.put(Type.HILL_STATION, new CrawlData("hill-stations-in-karnataka", "hillstation"));
        destinationInfo.put(Type.ARCHAEOLOGY, new CrawlData("archaeology-in-karnataka", "archaeology"));
        destinationInfo.put(Type.BEACH,  new CrawlData("beaches-in-karnataka", "beaches"));
        destinationInfo.put(Type.RIVER,  new CrawlData("rivers-in-karnataka", "rivers"));
        destinationInfo.put(Type.WATERFALL, new CrawlData( "waterfalls-in-karnataka", "waterfalls"));
        destinationInfo.put(Type.ISLAND,  new CrawlData("islands-in-karnataka", "islands"));
        destinationInfo.put(Type.DAM,  new CrawlData("dams-in-karnataka", "dams"));
    }

    public static Document getRemoteDocument(String url) throws IOException {

        File file = new File(getQualifiedFileName(url));
        if(file.exists()){
            //System.out.println("File exists. Fetching document from local");
            if(file.canRead()){
                return Jsoup.parse(Files.toString(new File(getQualifiedFileName(url)), Charsets.UTF_8));
            }else
            {
                //System.out.println("Unable to read the file");
                throw new IOException();
            }
        }
        else
        {
            System.out.println("File does not exists. Fetching document from remote");
            Document document = Jsoup.connect(url).get();
            File directory = new File(baseDocumentDirectory);
            if(directory.exists()) {
                File newFile = new File(directory, url.substring(url.lastIndexOf("/")));
                if (newFile.createNewFile()) {
                    System.out.println("new file created");
                }
                if (newFile.exists()) {
                    FileWriter fileWriter = new FileWriter(newFile);
                    if (newFile.canWrite()) {
                        System.out.println("able to write to new file");
                        fileWriter.write(document.toString());
                        fileWriter.close();
                    } else {
                        System.out.println("Unable to write to file");
                    }
                } else {
                    System.out.println("Unable to create new file");
                }
            }
            else{
                System.out.println("Base directory does not exists. Please create the directory");
            }
            return document;
        }
    }

    @Test
    public void createFile() throws IOException{
        System.out.println("trying to create a new file");
        File directory = new File(baseDocumentDirectory);
        if(directory.exists()){
            System.out.println("directory exists");
            File newFile = new File(directory, "web-Content");
            if(newFile.createNewFile()){
                System.out.println("new file created");
            }
            if(newFile.canWrite()){
                System.out.println("able to write to new file");
            }
            System.out.println(newFile);
        }
        else{
            System.out.println("directory does not exist");
        }
    }

    public static void main(String[] args) throws IOException {
        ESOperations.getClient();
        for (Map.Entry<Type, CrawlData> destinationInfoMapEntry : destinationInfo.entrySet()) {

            Document document = getRemoteDocument(website+destinationInfoMapEntry.getValue().getUrl()+"/index.html");
            Map<String, Destination> uniqueDesinationHrefs = DataExtractor.destinationHrefs(document);

            boolean allowReindexing = true;
            int lookUpLimit = 1000000000;
            int counter = 0;
            for (Map.Entry<String, Destination> mapEntry : uniqueDesinationHrefs.entrySet()) {
                if (counter < lookUpLimit) {
                    Document remoteDoc = getRemoteDocument(baseUrl + mapEntry.getKey());
                    Destination.DESTINATION_ID = RedisOperations.loadDestinationId();
                    Destination destination = new Destination();
                    destination.setId(++Destination.DESTINATION_ID);
                    destination.setName(mapEntry.getKey().substring(0, mapEntry.getKey().indexOf("/")).replaceAll("-", " "));
                    //destination.setDistance(glimpse.DataExtractor.textBasedOnPattern(remoteDoc));
                    DataExtractor.textBasedOnTag(remoteDoc, "p", destination);
                    DataExtractor.textBasedOnString(remoteDoc, "Distances:", destination);
                    destination.setAddress(new Address("Bangalore", "Karnataka"));
                    destination.setType(Type.TREKKING);
                    destination.setImages(DataExtractor.extractImageUrl(remoteDoc));

                    mapEntry.setValue(destination);
                    //System.out.println(mapEntry.getKey());
                    //System.out.println(destination);

                    ObjectMapper objectMapper = new ObjectMapper();
                    String destinationString = objectMapper.writeValueAsString(destination);

                    System.out.println(destinationString);

                    KafkaProducerService.sendRecords(String.valueOf(destination.getId()), destinationString);
                    counter++;
                }
            }
            //System.out.println(elements);
        }
    }
    public static String getFileName(String url){
        return url.substring(url.lastIndexOf("/"));
    }

    public static String getQualifiedFileName(String url){
        return baseDocumentDirectory+getFileName(url);
    }
}

@Data
@AllArgsConstructor
class CrawlData{
    String url;
    String directory;
}

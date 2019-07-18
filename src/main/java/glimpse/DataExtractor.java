package glimpse;

import glimpse.models.Destination;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataExtractor {

    public static Map<String, Destination> destinationHrefs(Document document){

        Elements elements = document.getElementsByTag("tbody");
        Map<String, Destination> hrefs = new HashMap<>();
        elements.forEach(x->x.getElementsByTag("tr")
                .forEach(y->y.getElementsByTag("td")
                        .forEach(z->z.getElementsByTag("a")
                                .forEach(a->a.getElementsByAttribute("href")
                                        .forEach(b-> hrefs.put(b.attr("href"),null))))));
        return hrefs;
    }

    public static int textBasedOnPattern(Document document){

        String kmPattern = "(\\d+)\\s*((kilometers)|(km)|(kms)|(kilometres))";
        Pattern kmP = Pattern.compile(kmPattern);

        Elements elem = document.getElementsByTag("tbody");
        elem.forEach(x->x.getElementsByTag("tr")
                .forEach(y->y.getElementsByTag("td")
                        .forEach(z->z.getElementsMatchingText (kmP)
                                .forEach(t-> System.out.println(t.text())))));
        return 0;
    }

    public static void textBasedOnString(Document document, String searchString, Destination d){
        String kmPattern = "(\\d+)\\s*((kilometers)|(km)|(kms)|(kilometres))";
        Pattern kmP = Pattern.compile(kmPattern);
        Elements elem = document.getElementsByTag("tbody");
        elem.forEach(x->x.getElementsByTag("tr")
                .forEach(y->y.getElementsByTag("td")
                        .forEach(z->z.getElementsContainingText(searchString)
                                .forEach(p->p.getElementsMatchingText(kmP)
                                        .stream()
                                        .forEach(t-> Arrays.stream(t.text().split(" "))
                                                .filter(s->s.matches(kmPattern))
                                                .map(r->r.replaceAll("[^0-9]", ""))
                                                .forEach(e->d.setDistance(Integer.parseInt(e))))
                                                  ))));
    }

    public static List<Object> textBased(Document document, String searchString){
        String kmPattern = "(\\d+)\\s*((kilometers)|(km)|(kms)|(kilometres))";
        Pattern kmP = Pattern.compile(kmPattern);
        Elements elem = document.getElementsByTag("tbody");
        //Stream<String> matchedTextList =

        Stream s1 =
                elem.stream()
                .map(x->x.getElementsByTag("tr")
                        .stream()
                        .map(y->y.getElementsByTag("td")
                        .stream()
                                .map(z->z.getElementsContainingText(searchString)
                                .stream().map(t-> Arrays.stream(t.text().split(" "))
                                                .filter(s->s.matches(kmPattern))
                                                .map(r->r.replaceAll("[^0-9]", ""))
                                                .collect(Collectors.toList())
                                )
                        )
                ));

       return Collections.singletonList(s1);
    }

    public static void textBasedOnTag(Document document, String tag, Destination d) {
        Elements elem = document.getElementsByTag("tbody");
        elem.forEach(x -> x.getElementsByTag("tr")
                .forEach(y -> y.getElementsByTag("td")
                        .forEach(z -> z.getElementsByTag("p")
                                .forEach(t-> d.setDescription(d.getDescription()+t.text())))));

        if(d.getDescription() != null && d.getDescription().contains("null")) {
            d.setDescription(d.getDescription().substring(4));
        }
    }

    public static String getTitle(Document document){
        return document.getElementsByTag("title").text();
    }

    public static String getHeadings(Document document){
        Elements elements = document.getElementsByTag("h1");
        elements.addAll(document.getElementsByTag("h2"));
        elements.addAll(document.getElementsByTag("h3"));
        elements.addAll(document.getElementsByTag("h4"));
        elements.addAll(document.getElementsByTag("h5"));
        elements.addAll(document.getElementsByTag("h6"));
        elements.forEach(System.out::println);
        return "";
    }

    public static String summarise(Document document){
        Elements elements = document.getElementsByTag("h3");

        elements.stream()
                .map(e->{
                    System.out.println(e);
                        return Arrays.asList(e,e.parent());
                })
                .map(p->{
                    System.out.println(p.get(0));
                    return p.get(1).getElementsByTag("p");

                })
                .forEach(System.out::println);
        return "";
    }

    @Test
    public void test() throws IOException {
        String baseUrl = "http://www.bangaloreorbit.com/trekking-in-karnataka/";
        String url = baseUrl + "agumbe/agumbe.html";
        Document document = DataCrawler.getRemoteDocument(url);

        summarise(document);
    }

    @Test
    public void testRegularExpression(){
        Pattern pattern = Pattern.compile("[a-zA-Z]]");
        System.out.println(pattern);
    }

    @Test
    public void testImageCrawl() throws IOException{

        String url  = "https://rukminim1.flixcart.com/image/832/832/jwdupow0/ghee/b/q/b/100-cow-plastic-bottle-thirumala-original-imafgxh9h3ccszx3.jpeg";
        downloadImage(url);

    }

    public static List<String> extractImageUrl(Document document) {

        Elements elements = document.getElementsByTag("img");
        String baseUrl =  "http://www.bangaloreorbit.com";
        List<String> imgUrls = new ArrayList<>();

        List<String> indexedImageNames = new ArrayList<>();

        elements.stream()
                .filter(e-> e.attr("src").contains(".jpg") || e.attr("src").contains(".jpeg"))
                .forEach(e-> {
                    String src = e.attr("src");
                    imgUrls.add(src.substring(src.indexOf("/assets/")));
                });

        imgUrls.forEach(
                e->{
                    System.out.println(baseUrl+e);
                    String imageName = downloadImage(baseUrl+e);
                    if(imageName != null) {
                        indexedImageNames.add(imageName);
                    }
                }
        );

        return indexedImageNames;
    }

    public static String downloadImage(String url){

        Image image = null;
        String imageName = null;

        String imageDirectory = "/Users/kunalsingh.k/glimpse/src/main/resources/assets/images/";

        try {
            URL urlObject = new URL(url);

            imageName = url.substring(url.lastIndexOf("/")+1);
            System.out.println(imageName);

            File file = new File(imageDirectory+imageName);

            if(file.exists()){
                System.out.println(imageName + " is already present");
                return imageName;
            }
            image = ImageIO.read(urlObject);
            InputStream in = new BufferedInputStream(urlObject.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream(imageDirectory+imageName);
            fos.write(response);
            fos.close();
            return imageName;

        } catch (IOException e) {
            System.out.println("Exception while downloading image: "+e);
        }
        return null;
    }

    @Test
    public void downloadImageTest() throws IOException {
        String baseUrl = "http://www.bangaloreorbit.com/trekking-in-karnataka/";
        String url = baseUrl + "agumbe/agumbe.html";
        Document document = DataCrawler.getRemoteDocument(url);
        extractImageUrl(document);
    }
}

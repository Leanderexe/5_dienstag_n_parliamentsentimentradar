package protocolscraping;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The class to receive a picture of the desired person from the bundestag webite
 * @author Manuel Aha
 */
public class PictureScrap {

		String parentUrl = "https://bilddatenbank.bundestag.de";
		URIBuilder ub;
		//String speakerName;

	/**
	 * Straight forward only this method is necessary for this process
	 * @param speakerName is the name of the speaker we are looking for
	 * @return the URL but we can also change the return format easily. Also entire picture information can be extracted which was not necessary for frontend purposes
	 * @author Manuel Aha
	 */
		public URL run(String speakerName) {

			try {
				ub = new URIBuilder(parentUrl + "/search/picture-result");
				ub.addParameter("query", speakerName);
				String url = ub.toString();

				Scarper scarper = new Scarper(url);
				String img = scarper.init();

				URL speakerIMG = scarper.getImageByName(img);
				return speakerIMG;

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			return null;
		}
}

/**
 * Since both clasess are for the same pupose both is in the same file
 * The actual scraping process is in here but initiated by @link{PictureScrap.run()}
 * @author Manuel Aha
 */
class Scarper {

	private String url;

	/**
	 * constrcuto class with pictureURL
	 * @param url
	 * @author Manuel Aha
	 */
	public Scarper(String url) {
		this.url = url;
	}

	/**
	 * starts the picture scraping process
	 * @return
	 * @auhtor Manuel Aha
	 */
	public String init() {
		String imageURL = null;
		Document pageSource = getPageSource(url);

		//based on html analysis we need to fetch these infos to receive the URL of the image
		Elements documents = pageSource.getElementsByClass("item");

		if (!documents.isEmpty()) {
			Element element = documents.get(1).getElementsByTag("img").get(0);

			imageURL = element.attr("src");
			System.out.println(imageURL);
		}

		return imageURL;

	}

	/**
	 * Determinging basic jsoup configurations
	 * @param url
	 * @return
	 * @author Manuel Aha
	 */
	private Document getPageSource(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0").get();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Receiving the image by given name
	 * @param name is the name of the speaker
	 * @return the URL, but other formats are easily possible from this point
	 * @throws URISyntaxException
	 * @author Manuel Aha
	 */
	public URL getImageByName(String name) throws URISyntaxException {

		//base url part
		String parentUrl = "https://bilddatenbank.bundestag.de";

		byte[] fileContent = null;
		try {
			//fileContent = IOUtils.toByteArray(new URL(parentUrl + name));
			URL uriella = new URL(parentUrl + name);

			//Creation of different data formats are possible here

			//fileContent = IOUtils.toByteArray(uriella);
			//ByteArrayInputStream inStreambj = new ByteArrayInputStream(fileContent);
			//BufferedImage metaData = ImageIO.read(inStreambj);
			//ImageIO.write(newImage, "jpg", new File(name ));

			System.out.println("Image generated from the byte array.");

			return uriella;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}

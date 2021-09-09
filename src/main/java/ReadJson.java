import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class ReadJson {
    public static void main(String[] args) throws IOException {
        String productName = "Redken Clean Maniac Hair Cleansing Cream Clarifying Shampoo";
        var product = GetAmazonProduct(productName);
        System.out.println("product : " + product);
        System.out.println("Url : " + product.getJSONObject("product").getString("link"));
        System.out.println("Image : " + product.getJSONObject("product").getJSONObject("main_image").getString("link"));
        if (product.getJSONObject("product").has("description")) {
            System.out.println("Description : " + product.getJSONObject("product").getString("description"));
        }
        System.out.println("price : " + product.getJSONObject("product").getJSONObject("buybox_winner").getJSONObject("price").getString("raw"));
    }

    // API key gotten from environment variable with name "RAINFOREST_API_KEY"
    private static final String RAINFOREST_API_KEY =  System.getenv("RAINFOREST_API_KEY");
    private static final String RAINFOREST_SEARCH_URL = String.format("https://api.rainforestapi.com/request?api_key=%s&type=search&amazon_domain=amazon.com&search_term=", RAINFOREST_API_KEY);
    private static final String RAINFOREST_PRODUCT_URL = String.format("https://api.rainforestapi.com/request?api_key=%s&type=product&url=", RAINFOREST_API_KEY);
    public static JSONObject GetAmazonProduct(String productName) throws IOException {
        productName = productName.replace(" ", "+");
        String uri = RAINFOREST_SEARCH_URL.concat(productName);
        JSONObject json = readJsonFromUrl(uri.trim());
        JSONArray response = json.getJSONArray("search_results");
        // first is sponsored product
        JSONObject item = response.getJSONObject(0);
        // get first non sponsored product
        for (int i = 0; i < response.length(); i++) {
            JSONObject current_item = response.getJSONObject(i);
            if (!current_item.has("sponsored")) {
                item = current_item;
                break;
            }
        }
        // get product url
        String link = item.getString("link");
        // build uri
        String link_uri = RAINFOREST_PRODUCT_URL.concat(link);
        //get product json
        JSONObject product = readJsonFromUrl(link_uri.trim());
        return product;
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }

}

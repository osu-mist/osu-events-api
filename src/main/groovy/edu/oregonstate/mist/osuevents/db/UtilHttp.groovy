package edu.oregonstate.mist.osuevents.db

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.HttpClient
import org.apache.http.HttpHeaders
import org.apache.http.client.utils.URIBuilder

class UtilHttp {
    private final Map<String, String> apiConfiguration

    UtilHttp(Map<String, String> apiConfiguration) {
        this.apiConfiguration = apiConfiguration
    }

    public CloseableHttpResponse sendGet(String resourceURI,
                                         HttpClient httpClient) {
        URI uri = getBackendURI(resourceURI)

        HttpGet httpGet = new HttpGet(uri)
        println(httpGet.URI)
        httpClient.execute(httpGet)
    }

    private URI getBackendURI(String resourceURI) {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(backendScheme)
                .setHost(backendHost)
                .setPath(backendPath + resourceURI)

        uriBuilder.build()
    }

    private String getBackendHost() {
        apiConfiguration.get("backendHost")
    }

    private String getBackendScheme() {
        apiConfiguration.get("backendScheme")
    }

    private String getBackendPath() {
        apiConfiguration.get("backendPath")
    }
}

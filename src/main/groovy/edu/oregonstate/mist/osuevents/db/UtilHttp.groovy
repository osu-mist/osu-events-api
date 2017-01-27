package edu.oregonstate.mist.osuevents.db

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.HttpClient
import org.apache.http.client.utils.URIBuilder

class UtilHttp {
    private final Map<String, String> apiConfiguration

    UtilHttp(Map<String, String> apiConfiguration) {
        this.apiConfiguration = apiConfiguration
    }

    public CloseableHttpResponse sendGet(String resourceURI,
                                         HttpClient httpClient,
                                         LinkedHashMap<String, Integer> query) {
        URI uri = getBackendURI(resourceURI, query)

        HttpGet httpGet = new HttpGet(uri)
        httpClient.execute(httpGet)
    }

    private URI getBackendURI(String resourceURI, LinkedHashMap<String, Integer> query) {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(backendScheme)
                .setHost(backendHost)
                .setPath(backendPath + resourceURI)

        query.each { k, v ->
            uriBuilder.setParameter(k, v.toString())
        }

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

package edu.oregonstate.mist.osuevents.db

import com.fasterxml.jackson.core.type.TypeReference
import groovy.json.JsonSlurper
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils

class CacheDAO {
    private static final String placesResource = "/places"

    private UtilHttp utilHttp
    private HttpClient httpClient
    private ObjectMapper mapper = new ObjectMapper()

    CacheDAO(UtilHttp utilHttp, HttpClient httpClient) {
        this.httpClient = httpClient
        this.utilHttp = utilHttp
    }

    public def getPlaces() {
        def jsonSlurper = new JsonSlurper()
        CloseableHttpResponse response
        def data = []

        try {
            response = utilHttp.sendGet(placesResource, httpClient)
            HttpEntity entity = response.getEntity()
            String entityString = EntityUtils.toString(entity)
            data.add(jsonSlurper.parseText(entityString))
            EntityUtils.consume(entity)
        } finally {
            response?.close()
        }
        data
    }
}

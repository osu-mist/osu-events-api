package edu.oregonstate.mist.osuevents.db

import groovy.json.JsonSlurper
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils

class CacheDAO {
    private static final String placesResource = "/places"

    private UtilHttp utilHttp
    private HttpClient httpClient
    private JsonSlurper jsonSlurper = new JsonSlurper()

    CacheDAO(UtilHttp utilHttp, HttpClient httpClient) {
        this.httpClient = httpClient
        this.utilHttp = utilHttp
    }
//    public def getFilters() {}
//    public def getGroups() {}
//    public def getDepartments() {}
//    public def getGroups() {}

    public def getCustomFields() {
        def data = []

    }

    public def getPlaces() {
        def data = []
        Integer page = 1
        def query = [pp:100]
        query['page'] = page

        data.add(page - 1,
                jsonSlurper.parseText(
                        sendRequest(placesResource,
                                    query)
                )
        )

        while (data[page - 1].page.current != data[page - 1].page.total) {
            page++
            query['page'] = page
            data.add(page - 1,
                    jsonSlurper.parseText(
                            sendRequest(placesResource,
                                    query)
                    )
            )
        }
        sanitizePlaces(data)
    }
    private def sanitizePlaces(def data) {
        def places = [:]

        data.each {
            it.places.each {
                places[new String("${it.place.id}")] = new String("${it.place.name}")
            }
        }
        places
    }

    private String sendRequest(String resourceURI,
                               def query = []) {
        CloseableHttpResponse response
        String responseBody
        try {
            response = utilHttp.sendGet(resourceURI, httpClient, query)
            HttpEntity entity = response.getEntity()
            responseBody = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
        } finally {
            response?.close()
        }
        responseBody
    }
}

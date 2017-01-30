package edu.oregonstate.mist.osuevents.db

import groovy.json.JsonSlurper
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils

class CacheDAO {
    private static final String placesResource = "/places"
    private static final String labelsResource = "/events/labels"
    private static final String filterItemsResource = "/events/filters"
    private static final String groupsResource = "/groups"
    private static final String departmentsResource = "/departments"

    private UtilHttp utilHttp
    private HttpClient httpClient
    private JsonSlurper jsonSlurper = new JsonSlurper()

    CacheDAO(UtilHttp utilHttp, HttpClient httpClient) {
        this.httpClient = httpClient
        this.utilHttp = utilHttp
    }

    public def getCustomFields() {
        def data = jsonSlurper.parseText(sendRequest(labelsResource))
        def customFields = [:]

        data.custom_fields.each {
            customFields[new String("${it.key}")] = new String("${it.value}")
        }

        customFields
    }

    public def getDepartments() {
        def data = pageIteration(departmentsResource)
        def departments = [:]

        data.each {
            it.departments.each {
                departments[new String("${it.department.id}")] = new String("${it.department.name}")
            }
        }

        departments
    }

    public def getFilters() {
        def data = jsonSlurper.parseText(sendRequest(labelsResource))
        def filters = [:]

        data.filters.each {
            filters[new String("${it.key}")] = new String("${it.value}")
        }

        filters
    }

    public def getFilterItems() {
        jsonSlurper.parseText(sendRequest(filterItemsResource))
    }

    public def getGroups() {
        def data = pageIteration(groupsResource)
        def groups = [:]

        data.each {
            it.groups.each {
                groups[new String("${it.group.id}")] = new String("${it.group.name}")
            }
        }

        groups
    }

    public def getPlaces() {
        def data = pageIteration(placesResource)
        def places = [:]

        data.each {
            it.places.each {
                places[new String("${it.place.id}")] = new String("${it.place.name}")
            }
        }

        places
    }

    private def pageIteration(String resource) {
        def data = []
        Integer page = 1
        def query = [pp:100]
        query['page'] = page

        data.add(page - 1,
                jsonSlurper.parseText(
                        sendRequest(resource,
                                query)
                )
        )

        while (data[page - 1].page.current != data[page - 1].page.total) {
            page++
            query['page'] = page
            data.add(page - 1,
                    jsonSlurper.parseText(
                            sendRequest(resource,
                                    query)
                    )
            )
        }
        data
    }

    private String sendRequest(String resourceURI,
                               def query = [:]) {
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

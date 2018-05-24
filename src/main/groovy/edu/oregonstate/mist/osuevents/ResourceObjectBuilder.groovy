package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.api.jsonapi.ResourceObject

import javax.ws.rs.core.UriBuilder

class ResourceObjectBuilder {
    URI endpointUri

    ResourceObjectBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    ResourceObject buildResourceObject(String id, String resource, Object object) {
        new ResourceObject(
                id: id,
                type: resource,
                attributes: object,
                links: ['self': selfLink(id, resource)]
        )
    }

    private URI selfLink(String id, String resource) {
        UriBuilder.fromUri(this.endpointUri)
                .path("/${resource}/{id}")
                .build(id)
    }
}
